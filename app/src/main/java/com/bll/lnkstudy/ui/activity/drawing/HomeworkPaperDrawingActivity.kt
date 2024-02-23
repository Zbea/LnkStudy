package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IFileUploadView
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File


/**
 * 作业卷提交
 */
class HomeworkPaperDrawingActivity: BaseDrawingActivity(),IFileUploadView {

    private val mUploadPresenter= FileUploadPresenter(this)
    private var homeworkType:HomeworkTypeBean?=null
    private var course=""
    private var typeId=0//分组id
    private var daoManager: HomeworkPaperDaoManager?=null
    private var daoContentManager: HomeworkPaperContentDaoManager?=null
    private var papers= mutableListOf<HomeworkPaperBean>()
    private var paperContents= mutableListOf<HomeworkPaperContentBean>()
    private var paper: HomeworkPaperBean?=null
    private var paperContentBean:HomeworkPaperContentBean?=null
    private var paperContentBean_a:HomeworkPaperContentBean?=null

    private var currentPosition=0
    private var page = 0//页码
    private var pageCount=0
    private val commitItems = mutableListOf<ItemList>()

    override fun onToken(token: String) {
        showLoading()
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=paper?.contentId!!
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = homeworkType?.typeId!!
                    mUploadPresenter.commit(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onSuccess(urls: MutableList<String>?) {
    }

    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        //设置不能手写
        setPWEnabled(false)
        //修改状态
        paper?.state=1
        papers[currentPosition]=paper!!
        daoManager?.insertOrReplace(paper)
        //更新增量数据
        DataUpdateManager.editDataUpdate(2,paper?.contentId!!,2,typeId,Gson().toJson(paper))

        //提交成功后循环遍历删除手写
        for (contentBean in paperContents){
            //更新增量作业卷内容(作业提交后合图后不存在手写内容)
            DataUpdateManager.editDataUpdate(2,contentBean.id.toInt(),3,typeId)
        }

        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            type=1
            studentTaskId=paper?.contentId!!
            content=paper?.title
            homeworkTypeStr=homeworkType?.name
            course=homeworkType?.course
            time=System.currentTimeMillis()
        })
    }


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        homeworkType = intent.getBundleExtra("homeworkBundle")?.getSerializable("homework") as HomeworkTypeBean
        currentPosition=intent.getIntExtra("page",Constants.DEFAULT_PAGE)
        course=homeworkType?.course!!
        typeId=homeworkType?.typeId!!

        daoManager= HomeworkPaperDaoManager.getInstance()
        daoContentManager= HomeworkPaperContentDaoManager.getInstance()

        papers= daoManager?.queryAll(course,typeId) as MutableList<HomeworkPaperBean>

    }

    override fun initView() {
        setDrawingTitleClick(false)
        if(papers.size>0){
            if (currentPosition==Constants.DEFAULT_PAGE)
                currentPosition=papers.size-1
            changeContent()
        }
        else{
            setPWEnabled(false)
        }

        changeExpandView()

        iv_btn.setOnClickListener {
            if (paper?.state==3&&paper?.isCommit == true){
                CommonDialog(this).setContent(R.string.toast_commit_ok).builder().setDialogClickListener(
                    object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            showLoading()
                            commit()
                        }
                    })
            }
        }
    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        DrawingCatalogDialog(this,list).builder()?.setOnDialogClickListener { position ->
            if (currentPosition!=position){
                currentPosition = papers[position].index
                page = 0
                changeContent()
            }
        }
    }

    override fun onPageDown() {
        if (isExpand&&page+2<pageCount){
            page+=2
            changeContent()
        }
        else if (!isExpand&&page+1<pageCount){
            page+=1
            changeContent()
        }
        else{
            if (currentPosition+1<papers.size){
                currentPosition+=1
                page=0
                changeContent()
            }
        }
    }

    override fun onPageUp() {
        if (isExpand&&page>1){
            page-=2
            changeContent()
        }
        else if (!isExpand&&page>0){
            page-=1
            changeContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                changeContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        //展屏时，如果当前page内容为最后一张且这次目录内容不止1张，则页码前移一位
        if (isExpand){
            if (page==pageCount-1&&pageCount>1){
                page-=1
            }
        }
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    /**
     * 设置是否可以手写
     */
    private fun setPWEnabled(boolean: Boolean){
        elik_a?.setPWEnabled(boolean)
        elik_b?.setPWEnabled(boolean)
    }


    //内容切换
    private fun changeContent(){
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]

        paperContents= daoContentManager?.queryByID(paper?.contentId!!) as MutableList<HomeworkPaperContentBean>
        pageCount=paperContents.size

        tv_title_a.text=paper?.title
        tv_title_b.text=paper?.title
        setPWEnabled(!paper?.isPg!!)

        //作业未提交 提示时间 以及关闭手写
        if (paper?.state==3){
            setPWEnabled(true)
            if (paper?.isCommit==true){
                showToast(DateUtils.longToStringWeek(paper?.endTime!!*1000)+getString(R.string.toast_before_commit))
            }
        }
        else{
            setPWEnabled(false)
        }

        if (homeworkType?.isCloud==true)
        {
            setPWEnabled(false)
        }

        if (isExpand){
            paperContentBean_a=paperContents[page]
            setElikLoadPath(paperContentBean_a!!,elik_a!!,v_content_a)
            tv_page_a.text="${paperContents[page].page+1}"

            if (page+1<pageCount){
                paperContentBean=paperContents[page+1]
                setElikLoadPath(paperContentBean!!,elik_b!!,v_content_b)
                tv_page_b.text="${paperContents[page+1].page+1}"
            }
            else{
                //不显示 ，不能手写
                v_content_b.setImageResource(0)
                elik_b?.setPWEnabled(false)
                tv_page_b.text=""
            }
        }
        else{
            paperContentBean=paperContents[page]
            setElikLoadPath(paperContentBean!!,elik_b!!,v_content_b)
            tv_page_b.text="${paperContents[page].page+1}"
        }
    }


    //加载图片
    private fun setElikLoadPath(paperContentBean: HomeworkPaperContentBean, elik:EinkPWInterface, view:ImageView) {
        GlideUtils.setImageFileNoCache(this,File(paperContentBean.path),view)
        elik.setLoadFilePath(paperContentBean.drawPath,true)
    }

    override fun onElikSava_a() {
        saveElik(elik_a!!,paperContentBean_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!,paperContentBean!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface,contentBean:HomeworkPaperContentBean){
        elik.saveBitmap(true) {}
        //更新增量作业卷内容(未提交原图和手写图)
        DataUpdateManager.editDataUpdate(2,contentBean.id.toInt(),3,typeId)
    }

    /**
     * 提交
     */
    private fun commit(){
        commitItems.clear()
        for (i in paperContents.indices) {
            val item=paperContents[i]
            Thread {
                val drawPath = item.drawPath.replace("tch", "png")//当前绘图路径
                BitmapUtils.mergeBitmap(item.path, drawPath)
                commitItems.add(ItemList().apply {
                    id = i
                    url = item.path
                })
                FileUtils.deleteFile(File(item.drawPath).parentFile)
                if (commitItems.size == paperContents.size) {
                    commitItems.sort()
                    mUploadPresenter.getToken()
                }
            }.start()
        }
    }
}