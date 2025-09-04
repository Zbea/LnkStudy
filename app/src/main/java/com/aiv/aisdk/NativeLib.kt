package com.aiv.aisdk

class NativeLib {
    interface MqttCallback {
        fun onCallback(message: String?)
    }

    external fun netConn(name: String, number: Int,  onCallback: MqttCallback): String

    external fun sendMessage(msg: String, len: Int): String

//    external fun connmqtt(number: Int): String

    companion object {
        init {
            // 加载 java/jinLibs/arm64-v8a 或相应目录年 libAivAiSdk.so 库
            System.loadLibrary("AivAiSdk")
        }
    }

}