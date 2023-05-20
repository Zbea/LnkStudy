package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.manager.PaintingTypeDaoManager
import com.bll.lnkstudy.mvp.model.PaintingTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.PaintingListActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import com.qiniu.android.storage.*
import kotlinx.android.synthetic.main.fragment_painting.*
import java.io.File


/**
 * 书画
 */
class PaintingFragment : BaseFragment(){

    private var typeId = 0//类型
    private val halfYear=180*24*60*60*1000
    private var isLocalDrawing=false

    override fun getLayoutId(): Int {
        return R.layout.fragment_painting
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setTitle(R.string.main_painting_title)
        initTab()

        iv_han.setOnClickListener {
            onClick(1)
        }
        iv_tang.setOnClickListener {
            onClick(2)
        }
        iv_song.setOnClickListener {
            onClick(3)
        }
        iv_yuan.setOnClickListener {
            onClick(4)
        }
        iv_ming.setOnClickListener {
            onClick(5)
        }
        iv_qing.setOnClickListener {
            onClick(6)
        }
        iv_jd.setOnClickListener {
            onClick(7)
        }
        iv_dd.setOnClickListener {
            onClick(8)
        }

        tv_sm.setOnClickListener {
            val intent = Intent(activity, PaintingListActivity::class.java)
            intent.putExtra("title", getString(R.string.painting_smh))
            intent.putExtra("paintingType", 4)
            intent.flags = 1
            customStartActivity(intent)
        }

        tv_yb.setOnClickListener {
            val intent = Intent(activity, PaintingListActivity::class.java)
            intent.putExtra("title", getString(R.string.painting_ybsf))
            intent.putExtra("paintingType", 5)
            intent.flags = 1
            customStartActivity(intent)
        }

        iv_hb.setOnClickListener {
            gotoPaintingDrawing(0)
        }
        iv_sf.setOnClickListener {
            gotoPaintingDrawing(1)
        }

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {
        val tabStrs = DataBeanManager.PAINTING
        for (i in 0..3) {
            rg_group.addView(getRadioButton(i, tabStrs[i], 3))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId = id
        }
    }

    private fun onClick(time: Int) {
        val intent = Intent(activity, PaintingListActivity::class.java)
        intent.putExtra("title", "${getString(DataBeanManager.dynastys[time-1])}   ${DataBeanManager.PAINTING[typeId]}")
        intent.putExtra("time", time)
        intent.putExtra("paintingType", typeId+1)
        intent.flags = 0
        customStartActivity(intent)
    }

    /**
     * 线上书画（半年上传）
     */
    fun uploadPainting(token: String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        isLocalDrawing=false
        //获取线上书画
        val paintings=PaintingBeanDaoManager.getInstance().queryPaintings()
        for (item in paintings){
            if (item.isCloud) continue //已上传过的不用再上传
            if (System.currentTimeMillis()>=item.date+halfYear){
                cloudList.add(CloudListBean().apply {
                    type=5
                    zipUrl=item.bodyUrl
                    downloadUrl=item.bodyUrl
                    subType=item.paintingType
                    subTypeStr=item.paintingTypeStr
                    dynasty=item.time
                    dynastyStr=item.timeStr
                    date=System.currentTimeMillis()
                    listJson=Gson().toJson(item)
                    bookId=item.contentId
                })
            }
        }
        Handler().postDelayed({
            mCloudUploadPresenter.upload(cloudList)
        },500)
    }

    /**
     * 上传本地手绘书画
     */
    fun uploadLocalDrawing(token: String) {
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        isLocalDrawing=true

        val uploadList= mutableListOf<PaintingTypeBean>()
        //查找所有分类
        val paintingTypes=PaintingTypeDaoManager.getInstance().queryAll()
        for (item in paintingTypes){
            val paintingContents=PaintingDrawingDaoManager.getInstance().queryAllByType(item.type,item.grade)
            val path=FileAddress().getPathPainting(item.type,item.grade)
            val fileName="${if (item.type==0) "画本" else "书法"}${item.grade}年级"
            //存在内容则上传
            if (paintingContents.size>0){
                uploadList.add(item)
                Handler().postDelayed({
                    FileUploadManager(token).apply {
                        startUpload(path,fileName)
                        setCallBack{
                            cloudList.add(CloudListBean().apply {
                                type=5
                                subType=if (item.type==0) 7 else 8
                                subTypeStr=if (item.type==0) "我的画本" else "我的书法"
                                date=System.currentTimeMillis()
                                grade=this@PaintingFragment.grade
                                listJson=Gson().toJson(item)
                                contentJson=Gson().toJson(paintingContents)
                                downloadUrl=it
                            })
                            if (cloudList.size==uploadList.size){
                                mCloudUploadPresenter.upload(cloudList)
                            }
                        }
                    }
                },500)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        if (isLocalDrawing){
            //将已经上传过的本地手绘书画从云书库删除
            val ids= mutableListOf<Int>()
            //查找所有分类
            val paintingTypes=PaintingTypeDaoManager.getInstance().queryAll()
            for (item in paintingTypes){
                if (item.isCloud){
                    ids.add(item.cloudId)
                }
            }
            if (ids.size>0)
                mCloudUploadPresenter.deleteCloud(ids)
            //删除所有本地画本、书法分类
            PaintingTypeDaoManager.getInstance().clear()
            //删除所有本地画本、书法内容
            PaintingDrawingDaoManager.getInstance().clear()
            FileUtils.deleteFile(File(Constants.PAINTING_PATH))
            //清除增量数据
            DataUpdateManager.clearDataUpdate(5,0)
            val map=HashMap<String,Any>()
            map["type"]=5
            map["typeId"]=0
            mDataUploadPresenter.onDeleteData(map)
        }
        else{
            val paintings=PaintingBeanDaoManager.getInstance().queryPaintings()
            for (item in paintings){
                if (System.currentTimeMillis()>=item.date+halfYear){
                    val path=FileAddress().getPathImage("painting" ,item.contentId)
                    FileUtils.deleteFile(File(path))
                    val paintingBean=PaintingBeanDaoManager.getInstance().queryBean(item.contentId)
                    PaintingBeanDaoManager.getInstance().deleteBean(paintingBean)
                    //删除增量更新
                    DataUpdateManager.deleteDateUpdate(5,item.id.toInt(),0,item.contentId)
                }
            }
        }
    }

}