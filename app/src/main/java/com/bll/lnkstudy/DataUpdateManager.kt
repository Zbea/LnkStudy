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
    fun createDataUpdate(type:Int,id:Int,contentType:Int,json:String){
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            date=System.currentTimeMillis()
            listJson= json
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdateBook(type:Int,id:Int,contentType:Int,path:String){
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            date=System.currentTimeMillis()
            this.path=path
        })
    }

    /**
     * 创建增量更新
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,state:Int,json:String){
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.state=state
            date=System.currentTimeMillis()
            listJson= json
        })
    }

    /**
     * 创建增量更新(带有源文件地址)
     */
    fun createDataUpdateSource(type:Int,id:Int,contentType:Int,json:String,sourceUrl:String){
        //创建增量数据
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            date=System.currentTimeMillis()
            listJson= json
            downloadUrl=sourceUrl
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,json:String,path:String){
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            date=System.currentTimeMillis()
            listJson= json
            this.path=path
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdateTypeId(type:Int,id:Int,contentType:Int,typeId:Int,json:String,path:String){
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.typeId=typeId
            date=System.currentTimeMillis()
            listJson= json
            this.path=path
        })
    }

    /**
     * 创建增量更新（有内容）
     */
    fun createDataUpdate(type:Int,id:Int,contentType:Int,state: Int,json:String,path:String){
        mDataUpdateDaoManager.insertOrReplace(DataUpdateBean().apply {
            this.type=type
            uid=id
            this.contentType=contentType
            this.state=state
            date=System.currentTimeMillis()
            listJson= json
            this.path=path
            downloadUrl=""
        })
    }

    /**
     * 删除增量更新
     */
    fun deleteDateUpdate(type:Int,id:Int,contentType:Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType)?.apply {
            isDelete=true
            isUpload=false
        })
    }

    /**
     * 删除增量更新
     */
    fun deleteDateUpdate(type:Int,id:Int,contentType:Int,typeId: Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType,typeId)?.apply {
            isDelete=true
            isUpload=false
        })
    }


    /**
     * 修改增量更新（图片内容变化，地址不变）
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType)?.apply {
            isUpload=false
        })
    }

    /**
     * 修改增量更新（图片内容变化，地址不变）
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,typeId: Int){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType,typeId)?.apply {
            isUpload=false
        })
    }

    /**
     * 修改增量更新
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,json: String){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType)?.apply {
            listJson=json
            isUpload=false
        })
    }

    /**
     * 修改增量更新
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,typeId: Int,json: String){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType,typeId)?.apply {
            listJson=json
            isUpload=false
        })
    }

    /**
     * 修改增量更新(有手写内容,保存路径)
     */
    fun editDataUpdate(type:Int,id:Int,contentType:Int,json: String,path: String){
        mDataUpdateDaoManager.insertOrReplace(mDataUpdateDaoManager.queryBean(type,id,contentType)?.apply {
            listJson=json
            isUpload=false
            this.path=path
        })
    }

    /**
     * 删除本地增量数据
     */
    fun clearDataUpdate(type: Int){
        mDataUpdateDaoManager.deleteBeans(type)
    }

}