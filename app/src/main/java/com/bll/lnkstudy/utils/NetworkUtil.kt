package com.bll.lnkstudy.utils


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager


@SuppressLint("StaticFieldLeak")
object NetworkUtil {

    private var connManager: ConnectivityManager? = null
    private var isForceOpenNet=false
    private var context:Context?=null

    fun init(context: Context) {
        this.context=context
        connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /**
     * @return 网络是否连接可用
     */
    fun isNetworkConnected(): Boolean {
        val networkInfo = connManager?.activeNetworkInfo
        return networkInfo!=null&&networkInfo.isConnected
    }

    /**
     * 设置强制打开网络 true 强制打开后不能关闭 false 关闭后，可以打开以及关闭
     */
    fun setForceOpenNet(isEnable: Boolean){
        toggleMobileNet(isEnable)
        toggleWiFi(isEnable)
        isForceOpenNet=isEnable
    }

    /**
     * 开关网络
     */
    fun toggleNetwork(boolean: Boolean){
        toggleMobileNet(boolean)
        toggleWiFi(boolean)
    }

    /**
     * @return wifi是否连接可用
     */
    fun isWifiConnected(): Boolean {
        val mWifi = connManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi?.isConnected ?: false
    }

    /**
     * 当wifi不能访问网络时，mobile才会起作用
     * @return GPRS是否连接可用
     */
    fun isMobileConnected(): Boolean {
        val mMobile = connManager?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return mMobile?.isConnected ?: false
    }

    /**
     * GPRS网络开关 反射ConnectivityManager中hide的方法setMobileDataEnabled 可以开启和关闭GPRS网络
     *
     * @param isEnable
     * @throws Exception
     */
    private fun toggleMobileNet(isEnable: Boolean) {
        if (!isForceOpenNet){
            try {
                val telephonyService = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val setMobileDataEnabledMethod = telephonyService.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
                setMobileDataEnabledMethod.invoke(telephonyService, isEnable)
            } catch (e: Exception) {
                // 处理异常情况
                e.printStackTrace()
            }
        }
    }

    /**
     * WIFI网络开关
     *
     * @param enabled
     * @return 设置是否success
     */
    private fun toggleWiFi(enabled: Boolean): Boolean {
        if (!isForceOpenNet){
            val wm= context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return wm.setWifiEnabled(enabled)
        }
        return false
    }

    /**
     *
     * @return 是否处于飞行模式
     */
    fun isAirplaneModeOn(): Boolean {
        // 返回值是1时表示处于飞行模式
        val modeIdx: Int = Settings.System.getInt(context!!.contentResolver, Settings.System.AIRPLANE_MODE_ON, 0)
        return modeIdx == 1
    }

    /**
     * 飞行模式开关
     * @param setAirPlane
     */
    fun toggleAirplaneMode(setAirPlane: Boolean) {
        Settings.System.putInt(context?.contentResolver, Settings.System.AIRPLANE_MODE_ON, if (setAirPlane) 1 else 0)
        // 广播飞行模式信号的改变，让相应的程序可以处理。
        // 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。
        // 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。
        val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        // intent.putExtra("Sponsor", "Sodino");
        // 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况
        intent.putExtra("state", setAirPlane)
        context?.sendBroadcast(intent)
    }

}