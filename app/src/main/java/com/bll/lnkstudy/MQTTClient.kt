package com.bll.lnkstudy

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
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
    }

    private var client: MQTTClient? = null
    private var mMqttAndroidClient: MqttAndroidClient? = null
    private var options : MqttConnectOptions?=null
    private val backgroundHandler = Handler(Looper.getMainLooper())
    private var SUBSCRIBE_MAX_RETRY = 0
    private var CONNECT_MAX_RETRY = 0

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    fun getInstance(): MQTTClient? {
        if (client == null) {
            synchronized(MQTTClient::class.java) {
                if (client == null) {
                    client = MQTTClient()
                }
            }
        }
        return client
    }

     fun init(context: Context){
         SUBSCRIBE_MAX_RETRY=0
         CONNECT_MAX_RETRY=0

        val serverURI = "tcp://api2.qinglanmb.com:1883"
        val username = "mqtt"
        val password = "EMQ12312@12asdf"
        val clientName = "Client_" + MethodManager.getUser()?.accountId

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

        mMqttAndroidClient = MqttAndroidClient(context, serverURI, clientName)
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
                    val topic = "topic/user/" + MethodManager.getUser()?.accountId
                    subscribe(topic)
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

    private fun subscribe(topic: String, qos: Int = 1) {
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
                            subscribe(topic)
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
            val topic = "topic/user/" + MethodManager.getUser()?.accountId
            mMqttAndroidClient?.unsubscribe(topic)
            mMqttAndroidClient?.disconnect(null, null)
            mMqttAndroidClient?.close()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        mMqttAndroidClient = null // 置空，避免后续复用
        options = null // 重置连接配置（避免旧配置干扰）
    }

    fun isClientValidity(): Boolean {
        return mMqttAndroidClient != null
    }

    fun isConnect():Boolean{
        return mMqttAndroidClient!!.isConnected
    }

    fun reConnect(){
        SUBSCRIBE_MAX_RETRY=0
        CONNECT_MAX_RETRY=0
        try {
            mMqttAndroidClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "reConnect:Connection success")
                    CONNECT_MAX_RETRY=0
                    // 重连成功后重新订阅主题
                    val topic = "topic/user/" + MethodManager.getUser()?.accountId
                    subscribe(topic)
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "reConnect:Connection failure")
                    if (CONNECT_MAX_RETRY<5){
                        CONNECT_MAX_RETRY+=1
                        // 失败后延迟重试
                        backgroundHandler.postDelayed({
                            reConnect()
                        },  5000)
                    }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}