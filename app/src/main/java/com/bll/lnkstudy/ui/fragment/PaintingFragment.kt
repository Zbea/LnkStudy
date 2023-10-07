package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.manager.PaintingTypeDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.painting.PaintingTypeBean
import com.bll.lnkstudy.ui.activity.PaintingListActivity
import com.bll.lnkstudy.ui.activity.PaintingTypeListActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import com.qiniu.android.storage.*
import kotlinx.android.synthetic.main.common_radiogroup_fragment.*
import kotlinx.android.synthetic.main.fragment_painting.*
import java.io.File


/**
 * 书画
 */
class PaintingFragment : BaseFragment(){

    private var typeId = 0//类型
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
        iv_jd_1.setOnClickListener {
            onClick(7)
        }
        iv_dd_1.setOnClickListener {
            onClick(8)
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
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i, tabStrs[i], tabStrs.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId = id
            if (typeId==4||typeId==5){
                showView(ll_content2)
                disMissView(ll_content1)
            }
            else{
                showView(ll_content1)
                disMissView(ll_content2)
            }
        }
    }

    /**
     * 调整本地我的书画
     */
    private fun onClick(time: Int) {
        val intent = Intent(activity, PaintingListActivity::class.java)
        intent.putExtra("title", "${getString(DataBeanManager.dynastys[time-1])}   ${DataBeanManager.PAINTING[typeId]}")
        intent.putExtra("time", time)
        intent.putExtra("paintingType", typeId+1)
        customStartActivity(intent)
    }


    /**
     * 跳转手写画本
     */
    private fun gotoPaintingDrawing(type: Int){
        val items=PaintingTypeDaoManager.getInstance().queryAllByType(type)
        //当前年级 画本、书法分类为null则创建
        var item=PaintingTypeDaoManager.getInstance().queryAllByGrade(type,grade)
        if (item==null) {
            val date=System.currentTimeMillis()
            item= PaintingTypeBean()
            item.type = type
            item.grade = grade
            item.date = date
            val id=PaintingTypeDaoManager.getInstance().insertOrReplaceGetId(item)
            //创建本地画本增量更新
            DataUpdateManager.createDataUpdate(5,id.toInt(),1, 1, Gson().toJson(item))
        }

        //当本地画本或者书法分类不止一个时候，进去列表
        if (items.size>1){
            val intent=Intent(activity, PaintingTypeListActivity::class.java)
            intent.flags=type
            customStartActivity(intent)
        } else{
            MethodManager.gotoPaintingDrawing(requireActivity(),item,type)
        }

    }

    /**
     * 线上书画（半年上传）
     */
    fun uploadPainting(){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        isLocalDrawing=false
        //获取线上书画
        val paintings=PaintingBeanDaoManager.getInstance().queryPaintings()
        for (item in paintings){
            if (item.isCloud) continue //已上传过的不用再上传
            if (System.currentTimeMillis()>=item.date+Constants.halfYear){
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
        val paintingTypes=PaintingTypeDaoManager.getInstance().queryAllExcludeCloud()
        for (item in paintingTypes){
            val paintingContents=PaintingDrawingDaoManager.getInstance().queryAllByType(item.type,item.grade)
            val path=FileAddress().getPathPainting(item.type,item.grade)
            val fileName="${if (item.type==0) "画本" else "书法"}${item.grade}年级"
            if (paintingContents.size>0){
                uploadList.add(item)
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
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        if (isLocalDrawing){
            //删除所有本地画本、书法分类
            PaintingTypeDaoManager.getInstance().clear()
            //删除所有本地画本、书法内容
            PaintingDrawingDaoManager.getInstance().clear()
            FileUtils.deleteFile(File(Constants.PAINTING_PATH))
            //清除增量数据
            DataUpdateManager.clearDataUpdate(5,1)
            val map=HashMap<String,Any>()
            map["type"]=5
            map["typeId"]=1
            mDataUploadPresenter.onDeleteData(map)
        }
        else{
            val paintings=PaintingBeanDaoManager.getInstance().queryPaintings()
            for (item in paintings){
                if (System.currentTimeMillis()>=item.date+Constants.halfYear){
                    val path=FileAddress().getPathImage("painting" ,item.contentId)
                    FileUtils.deleteFile(File(path))
                    val paintingBean=PaintingBeanDaoManager.getInstance().queryBean(item.contentId)
                    PaintingBeanDaoManager.getInstance().deleteBean(paintingBean)
                    //删除增量更新
                    DataUpdateManager.deleteDateUpdate(7,item.id.toInt(),1,item.contentId)
                }
            }
        }
    }

}