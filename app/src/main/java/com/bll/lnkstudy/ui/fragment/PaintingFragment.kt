package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.manager.PaintingTypeDaoManager
import com.bll.lnkstudy.mvp.model.UploadItem
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICloudUploadView
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
class PaintingFragment : BaseFragment(),ICloudUploadView {

    private val mCloudUploadPresenter=CloudUploadPresenter(this)
    private var typeId = 1//类型
    private val halfYear=180*24*60*60*1000
    private val cloudList= mutableListOf<CloudListBean>()
    private var isLocalDrawing=false

    override fun onSuccess() {
        if (isLocalDrawing){
            //删除所有本地画本、书法分类
            PaintingTypeDaoManager.getInstance().clear()
            //删除所有本地画本、书法内容
            PaintingDrawingDaoManager.getInstance().clear()
            FileUtils.deleteFile(File(Constants.PAINTING_PATH))
        }
        else{
            //删除本地线上书画
            for (item in cloudList){
                val path=FileAddress().getPathImage("painting" ,item.id)
                FileUtils.deleteFile(File(path))
                val paintingBean=PaintingBeanDaoManager.getInstance().queryBean(item.id)
                PaintingBeanDaoManager.getInstance().deleteBean(paintingBean)
            }
        }
    }

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
        iv_hb.setOnClickListener {
            gotoPaintingDrawing(0)
        }
        iv_sf.setOnClickListener {
            gotoPaintingDrawing(1)
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
            typeId = id+1
        }
    }

    private fun onClick(time: Int) {
        val intent = Intent(activity, PaintingListActivity::class.java)
        intent.putExtra("title", "${getString(DataBeanManager.dynastys[time])}   ${DataBeanManager.PAINTING[typeId]}")
        intent.putExtra("time", time)
        intent.putExtra("paintingType", typeId)
        intent.flags = 0
        customStartActivity(intent)
    }

    /**
     * 线上书画（半年上传）
     */
    fun uploadPainting(token: String){
        if (grade==0) return
        cloudList.clear()
        isLocalDrawing=false
        //获取线上书画
        val paintings=PaintingBeanDaoManager.getInstance().queryPaintings()
        for (item in paintings){
            if (System.currentTimeMillis()<item.date+halfYear){
                cloudList.add(CloudListBean().apply {
                    type=5
                    id=item.contentId
                    downloadUrl=item.bodyUrl
                    subType=item.paintingType
                    subTypeStr=item.paintingTypeStr
                    dynasty=item.time
                    dynastyStr=item.timeStr
                    date=System.currentTimeMillis()
                    listJson=Gson().toJson(item)
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
        cloudList.clear()
        isLocalDrawing=true
        //查找我的画本、我的书法
        val list= mutableListOf<UploadItem>()
        val hbPath=FileAddress().getPathPainting(0, grade)
        if (File(hbPath).exists()){
            list.add(UploadItem().apply {
                type=0
                typeStr="我的画本"
                path=hbPath
                fileName="我的画本${grade}年级"
            })
        }
        val sfPath=FileAddress().getPathPainting(1, grade)
        if (File(sfPath).exists()){
            list.add(UploadItem().apply {
                type=1
                typeStr="我的书法"
                path=sfPath
                fileName="我的书法${grade}年级"
            })
        }

        for (item in list){
            Handler().postDelayed({
                FileUploadManager(token).apply {
                    startUpload(item.path,item.fileName)
                    setCallBack{
                        val drawPaintings=PaintingDrawingDaoManager.getInstance().queryAllByType(0,grade)
                        cloudList.add(CloudListBean().apply {
                            type=5
                            subType=if (item.type==0) 7 else 8
                            subTypeStr=item.typeStr
                            date=System.currentTimeMillis()
                            grade=this@PaintingFragment.grade
                            contentJson=Gson().toJson(drawPaintings)
                            this.downloadUrl=it
                        })
                        if (cloudList.size==list.size){
                            mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
            },500)
        }

    }


}