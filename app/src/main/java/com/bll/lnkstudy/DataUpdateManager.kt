package com.bll.lnkstudy

import com.bll.lnkstudy.manager.DataUpdateDaoManager
import com.bll.lnkstudy.mvp.model.DataUpdateBean

/**
 * 每天数据增量更新处理（本地数据）
 */
object DataUpdateManager {

    private val mDataUpdateDaoManager=DataUpdateDaoManager.getInstance()

    /**
     * 创建增量更新
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,typeId:Int,json:String){
        DataUpdateDaoManager.getInstance().deleteBean(type,contentType,id, typeId)
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            date=System.currentTimeMillis()
            listJson= json
            downloadUrl=""
            sourceUrl=""
        })
    }

    /**
     * 创建增量更新
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,typeId:Int,state:Int,json:String){
        DataUpdateDaoManager.getInstance().deleteBean(type,contentType,id, typeId)
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            this.state=state
            date=System.currentTimeMillis()
            listJson= json
            downloadUrl=""
            sourceUrl=""
        })
    }

    /**
     * 创建增量更新(带有源文件地址)
     */
    fun createDataUpdateSource(type:Int,id:Int,contentType:Int,typeId:Int,json:String,sourceUrl:String){
        DataUpdateDaoManager.getInstance().deleteBean(type,contentType,id, typeId)
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            date=System.currentTimeMillis()
            listJson= json
            this.sourceUrl=sourceUrl
            downloadUrl=""
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,typeId:Int,json:String,path:String){
        DataUpdateDaoManager.getInstance().deleteBean(type,contentType,id, typeId)
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            date=System.currentTimeMillis()
            listJson= json
            this.path=path
            downloadUrl=""
            sourceUrl=""
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,typeId:Int,state: Int,json:String,path:String){
        DataUpdateDaoManager.getInstance().deleteBean(type,contentType,id, typeId)
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            this.state=state
            date=System.currentTimeMillis()
            listJson= json
            this.path=path
            downloadUrl=""
            sourceUrl=""
        })
    }

    /**
     * 删除增量更新
     */
    fun deleteDateUpdate(type:Int,id:Int,contentType:Int,typeId: Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,contentType,id,typeId).apply {
            date=System.currentTimeMillis()
            isDelete=true
        })
    }


    /**
     * 修改增量更新（图片内容变化，地址不变）
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,typeId: Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,contentType,id,typeId).apply {
            date=System.currentTimeMillis()
        })
    }

    /**
     * 修改增量更新
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,typeId: Int,json: String){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,contentType,id,typeId).apply {
            date=System.currentTimeMillis()
            listJson=json
        })
    }

    /**
     * 修改增量更新(有手写内容,保存路径)
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,typeId: Int,json: String,path: String){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,contentType,id,typeId).apply {
            date=System.currentTimeMillis()
            listJson=json
            this.path=path
        })
    }

    /**
     * 删除本地增量数据
     */
    fun clearDataUpdate(type: Int){
        mDataUpdateDaoManager.deleteBeans(type)
    }

    /**
     * 删除本地增量数据
     */
    fun clearDataUpdate(type: Int,typeId: Int){
        mDataUpdateDaoManager.deleteBeans(type,typeId)
    }

}