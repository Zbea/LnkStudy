package com.bll.lnkstudy

import org.greenrobot.eventbus.EventBus

object LocalDataManager {

    /**
     * 一键下载
     */
    fun downloadData(){
        EventBus.getDefault().post(Constants.DATA_UPLOAD_EVENT)
    }

    /**
     * 一键清除
     */
    fun clearLocalData(){

    }

}