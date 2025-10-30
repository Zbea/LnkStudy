package com.bll.lnkteacher

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.google.gson.Gson
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus

class MQTTClient {
    // TAG
    companion object {
        const val TAG = "debug AndroidMqttClient"

        // 静态单例（通过 lazy 实现线程安全的延迟初始化）
        val INSTANCE: MQTTClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MQTTClient()
        }
    }

    private var mMqttAndroidClient: MqttAndroidClient? = null
    private var options : MqttConnectOptions?=null
    private val backgroundHandler = Handler(Looper.getMainLooper())
    private var SUBSCRIBE_MAX_RETRY = 0
    private var CONNECT_MAX_RETRY = 0
    private var currentClientId: String? = null

    fun init(context: Context){
        SUBSCRIBE_MAX_RETRY=0
        CONNECT_MAX_RETRY=0

        mMqttAndroidClient?.close() // 先关闭旧实例
        mMqttAndroidClient = null
        options = null

        val user = MethodManager.getUser()
        val accountId = user?.accountId ?: run {
            Log.e(TAG, "init: user accountId is null, cannot init MQTT")
            return
        }

        val serverURI = "tcp://api2.qinglanmb.com:1883"
        val username = "mqtt"
        val password = "EMQ12312@12asdf"
        currentClientId = "Client_${accountId}_${System.currentTimeMillis()}" // 加时间戳避免重复

        options= MqttConnectOptions()
        options?.userName = username
        options?.password = password.toCharArray()
        options?.isAutomaticReconnect = true // 开启自动重连
        options?.isCleanSession = false // 保持会话状态
        options?.keepAliveInterval = 60
        options?.connectionTimeout=10
        options?.maxReconnectDelay=60*1000 // 最大重连间隔30秒
//         options?.socketFactory = SSLSocketFactory.getDefault() // 使用安全连接
//         options?.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1

        mMqttAndroidClient = MqttAndroidClient(context, serverURI, currentClientId)
        mMqttAndroidClient?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
                val item = Gson().fromJson(message.toString(), ItemList::class.java)
                if (item.userType == 2) {
                    when(item.type){
                        3->{
                            EventBus.getDefault().post(Constants.MQTT_CLASSGROUP_PERMISSION_EVENT)
                        }
                        4->{
                            EventBus.getDefault().post(Constants.MQTT_TESTPAPER_CORRECT_NOTICE_EVENT)
                        }
                        5->{
                            EventBus.getDefault().post(Constants.MQTT_TESTPAPER_ASSIGN_NOTICE_EVENT)
                        }
                        6->{
                            EventBus.getDefault().post(Constants.CLASSGROUP_REFRESH_EVENT)
                        }
                        else->{
                            EventBus.getDefault().post(Constants.MQTT_HOMEWORK_NOTICE_EVENT)
                        }
                    }
                }
            }
            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })
    }

    fun connect() {
        try {
            mMqttAndroidClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "connect:Connection success")
                    subscribe()
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "connect:Connection failure")
                    reConnect()
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(qos: Int = 1) {
        val topic = "topic/user/" + MethodManager.getUser()?.accountId
        try {
            mMqttAndroidClient?.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                    SUBSCRIBE_MAX_RETRY=0
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                    if (SUBSCRIBE_MAX_RETRY<5){
                        SUBSCRIBE_MAX_RETRY+=1
                        // 失败后延迟重试
                        backgroundHandler.postDelayed({
                            subscribe()
                        },  5000)
                    }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    fun disconnect() {
        try {
            val accountId =MethodManager.getAccountId()
            if (MethodManager.getAccountId()!=0L) {
                val topic = "topic/user/${accountId}"
                mMqttAndroidClient?.unsubscribe(topic)
            }
            if (isClientValidity())
                mMqttAndroidClient?.disconnect()
        }
        catch (e: MqttException) {
            e.printStackTrace()
        }
        finally {
            safeCloseClient()
            // 重置所有状态，避免复用
            mMqttAndroidClient = null
            options = null
            currentClientId = null
            SUBSCRIBE_MAX_RETRY = 0
            CONNECT_MAX_RETRY = 0
        }
    }

    // 安全关闭 MqttAndroidClient 实例
    private fun safeCloseClient() {
        if (mMqttAndroidClient != null) {
            try {
                // 先判断实例是否已连接（间接说明 ClientHandle 有效）
                if (mMqttAndroidClient?.isConnected == true || currentClientId?.isNotBlank() == true) {
                    mMqttAndroidClient?.close() // 仅对有效实例执行 close()
                }
            } catch (e: IllegalArgumentException) {
                // 捕获 Invalid ClientHandle 异常，避免崩溃
                Log.e(TAG, "safeCloseClient: invalid ClientHandle when close", e)
            } catch (e: MqttException) {
                Log.e(TAG, "safeCloseClient: MqttException when close", e)
            } finally {
                mMqttAndroidClient = null // 无论是否成功，最终置空实例
            }
        }
    }

    fun isClientValidity(): Boolean {
        return mMqttAndroidClient != null
                && currentClientId != null
                && currentClientId?.isNotBlank() == true
    }


    fun isConnect():Boolean?{
        return mMqttAndroidClient?.isConnected
    }

    fun reConnect(){
        if (isClientValidity().not()) {
            Log.e(TAG, "reConnect: client is invalid, cannot reconnect")
            return
        }

        if (CONNECT_MAX_RETRY >= 5) {
            Log.e(TAG, "reConnect: max retry count reached, stop")
            return
        }

        try {
            mMqttAndroidClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "reConnect:Connection success")
                    CONNECT_MAX_RETRY=0
                    subscribe()
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "reConnect:Connection failure")
                    CONNECT_MAX_RETRY+=1
                    // 失败后延迟重试
                    backgroundHandler.postDelayed({
                        reConnect()
                    },  5000)
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            CONNECT_MAX_RETRY+=1
            // 失败后延迟重试
            backgroundHandler.postDelayed({
                reConnect()
            },  5000)
        }
    }
}