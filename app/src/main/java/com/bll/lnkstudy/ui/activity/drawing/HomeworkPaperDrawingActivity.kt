package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.EinkPWInterface
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IFileUploadView
import com.bll.lnkstudy.ui.activity.CorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.ac_drawing.ll_score
import kotlinx.android.synthetic.main.common_correct_score.rv_list_multi
import kotlinx.android.synthetic.main.common_correct_score.rv_list_score
import kotlinx.android.synthetic.main.common_correct_score.tv_answer
import kotlinx.android.synthetic.main.common_correct_score.tv_correct_title
import kotlinx.android.synthetic.main.common_correct_score.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File


/**
 * 作业卷提交
 */
class HomeworkPaperDrawingActivity: BaseDrawingActivity(),IFileUploadView {

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var homeworkType:HomeworkTypeBean?=null
    private var course=""
    private var homeworkTypeId=0//分组id
    private var daoManager: HomeworkPaperDaoManager?=null
    private var papers= mutableListOf<HomeworkPaperBean>()
    private var paper: HomeworkPaperBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var page = 0//页码
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem?=null
    private var takeTime=0L

    override fun onToken(token: String) {
        FileImageUploadManager(token, getCommitPaths()).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=paper?.contentId!!
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = paper?.typeId!!
                    map["takeTime"]=takeTime
                    mUploadPresenter.commit(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        //设置不能手写
        setDisableTouchInput(true)
        //修改状态
        paper?.state=1
        papers[currentPosition]=paper!!
        daoManager?.insertOrReplace(paper)
        refreshDataUpdate()

        //替换本地图片，删除合图以及手写
        for (i in paper?.paths!!.indices){
            val mergePath=getPathMergeStr(i+1)
            if (File(mergePath).exists()){
                FileUtils.replaceFileContents(mergePath,paper?.paths!![i])
            }
        }
        Handler().postDelayed({
            FileUtils.deleteFile(File(getPathDraw()))
            FileUtils.deleteFile(File(getPathMerge()))
            //刷新当前paper
            oldPosition=-1
            onContent()
        },500)
    }


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        initChangeScreenData()
        homeworkType = intent.getBundleExtra("homeworkBundle")?.getSerializable("homework") as HomeworkTypeBean
        currentPosition=intent.getIntExtra("page",Constants.DEFAULT_PAGE)
        course=homeworkType?.course!!
        homeworkTypeId=homeworkType?.typeId!!

        daoManager= HomeworkPaperDaoManager.getInstance()
        papers= daoManager?.queryAll(course,homeworkTypeId) as MutableList<HomeworkPaperBean>
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        //云书库没有提交按钮
        if (homeworkType?.isCloud!!){
            disMissView(iv_btn)
        }

        if(papers.size>0){
            if (currentPosition==Constants.DEFAULT_PAGE)
                currentPosition=papers.size-1
            onContent()
        }
        else{
            setDisableTouchInput(true)
        }

        iv_btn.setOnClickListener {
            if (!NetworkUtil(this).isNetworkConnected()){
                showToast("网络连接失败，无法提交")
                return@setOnClickListener
            }
            if (!FileUtils.isExistContent(getPathMerge())){
                showToast("未填写答案,无法提交")
                return@setOnClickListener
            }
            if (paper?.state==1&&paper?.isSelfCorrect==true&&!paper?.commitJson.isNullOrEmpty()){
                homeworkCommitInfoItem=Gson().fromJson(paper?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            CommonDialog(this).setContent(R.string.toast_commit_ok).builder().setDialogClickListener(
                object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        commit()
                    }
                })
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
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,false).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (currentPosition!=pageNumber){
                    currentPosition = pageNumber
                    oldPosition=-1
                    page = 0
                    onContent()
                }
            }
        })
    }

    override fun onPageDown() {
        val count=if (isExpand) pageCount-2 else pageCount-1
        if (page<count){
            page+=if (isExpand)2 else 1
            Handler().postDelayed({
                onContent()
            },if (paper?.state==0)300 else 0)
        }
        else{
            if (currentPosition<papers.size-1){
                currentPosition+=1
                page=0
                Handler().postDelayed({
                    onContent()
                },if (paper?.state==0)300 else 0)
            }
        }
    }

    override fun onPageUp() {
        if (page>0){
            page-=if (isExpand)2 else 1
            Handler().postDelayed({
                onContent()
            },if (paper?.state==0)300 else 0)
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                Handler().postDelayed({
                    onContent()
                },if (paper?.state==0)300 else 0)
            }
        }
    }

    override fun onChangeExpandContent() {
        //单屏时只有一页无法展开
        if (!isExpand&&pageCount==1)
            return
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent() {
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]
        pageCount=paper!!.paths.size

        if (isExpand&&pageCount==1){
            onChangeExpandContent()
            return
        }

        if (isExpand&&page>pageCount-2)
            page=pageCount-2
        if (page<0)
            page=0

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        if (isExpand){
            setElikLoadPath(page,elik_a!!,v_content_a!!)
            setElikLoadPath(page+1,elik_b!!,v_content_b!!)
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text="${page+1}"
                tv_page.text="${page+1+1}"
            }
            else{
                tv_page.text="${page+1}"
                tv_page_a.text="${page+1+1}"
            }
        }
        else{
            setElikLoadPath(page,elik_b!!,v_content_b!!)
            tv_page.text="${page+1}"
        }

        //云书库下载无法手写
        setDisableTouchInput(homeworkType?.isCloud!!||paper?.state==1)

        if (currentPosition!=oldPosition){
            setScoreDetails(paper!!)
            when(paper?.state){
                0->{
                    if (paper?.endTime!!>0L){
                        showView(iv_btn)
                        val endTime=paper?.endTime!!*1000
                        if (System.currentTimeMillis()<=endTime){
                            showToast(DateUtils.longToStringWeek(endTime)+getString(R.string.toast_before_commit))
                        }
                    }
                    else{
                        disMissView(iv_btn)
                    }
                }
                1->{
                    if (paper?.commitJson.isNullOrEmpty()){
                        disMissView(iv_btn)
                    }
                    else{
                        showToast("请及时自批改后提交")
                        showView(iv_btn)
                    }
                }
                2->{
                    disMissView(iv_btn)
                }
            }
        }
        //用来判断重复加载
        oldPosition=currentPosition
    }

    /**
     * 设置批改详情
     */
    private fun setScoreDetails(item: HomeworkPaperBean){
        if (item.state==2){
            if (!ll_score.isVisible)
                showView(iv_score)
            if (item.answerUrl.isNullOrEmpty()){
                disMissView(tv_answer)
            }
            else{
                answerImages= item.answerUrl?.split(",") as MutableList<String>
                showView(tv_answer)
            }
            correctMode=item.correctMode
            scoreMode=item.scoreMode
            tv_correct_title.text=item.title
            tv_total_score.text=item.score.toString()
            if (item.correctJson?.isNotEmpty() == true&&correctMode>0){
                setScoreListDetails(item.correctJson)
            }
            else{
                disMissView(rv_list_multi,rv_list_score)
            }
        }
        else{
            disMissView(iv_score,ll_score)
        }
    }
    
    //加载图片
    private fun setElikLoadPath(index: Int, elik:EinkPWInterface, view:ImageView) {
        val path=paper!!.paths[index]
        if (paper?.state==1){
            GlideUtils.setImageNoCacheUrl(this,path,view)
        }
        else{
            GlideUtils.setImageCacheUrl(this,path,view, paper?.state!!)
        }
        elik.setLoadFilePath(paper!!.drawPaths[index],true)
    }

    override fun onElikSava_a() {
        if (paper?.endTime!!>0&&paper?.state!=2&&isExpand){
            Thread {
                BitmapUtils.saveScreenShot(this, v_content_a, getPathMergeStr(page+1))
            }.start()
        }
        refreshDataUpdate()
    }

    override fun onElikSava_b() {
        if (paper?.endTime!!>0&&paper?.state!=2){
            if (isExpand){
                Thread {
                    BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(page+1+1))
                }.start()
            }
            else{
                Thread {
                    BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(page+1))
                }.start()
            }
        }
        refreshDataUpdate()
    }

    override fun onElikStart_a() {
        if (paper?.startTime==0L){
            paper?.startTime=System.currentTimeMillis()
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
        }
    }

    override fun onElikStart_b() {
        if (paper?.startTime==0L){
            paper?.startTime=System.currentTimeMillis()
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
        }
    }

    /**
     * 提交
     */
    private fun commit(){
        if (paper?.isSelfCorrect == true){
            homeworkCommitInfoItem= HomeworkCommitInfoItem()
            homeworkCommitInfoItem?.messageId=paper?.contentId
            homeworkCommitInfoItem?.correctJson=paper?.correctJson
            homeworkCommitInfoItem?.correctMode=paper?.correctMode
            homeworkCommitInfoItem?.scoreMode=paper?.scoreMode
            homeworkCommitInfoItem?.answerUrl=paper?.answerUrl
            homeworkCommitInfoItem?.typeId=paper?.typeId
            homeworkCommitInfoItem?.typeName=homeworkType?.name
            homeworkCommitInfoItem?.course=homeworkType?.course
            homeworkCommitInfoItem?.state=homeworkType?.state
            homeworkCommitInfoItem?.paths=getCommitPaths()
        }

        takeTime=System.currentTimeMillis()- paper?.startTime!!
        if (paper?.isSelfCorrect == true){
            homeworkCommitInfoItem?.takeTime=takeTime
            //修改当前paper状态
            paper?.state = 1
            paper?.commitJson=Gson().toJson(homeworkCommitInfoItem)
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
            //云书库下载无法手写
            setDisableTouchInput(true)
            gotoSelfCorrect()
        }
        else{
            showLoading()
            mUploadPresenter.getToken()
        }
    }

    /**
     * 获取提交图片地址
     */
    private fun getCommitPaths():List<String>{
        //获取合图的图片，没有手写的页面那原图
        val paths= mutableListOf<String>()
        for (i in paper!!.paths.indices){
            val mergePath=getPathMergeStr(i+1)
            if (File(mergePath).exists()){
                paths.add(mergePath)
            }
            else{
                paths.add(paper!!.paths[i])
            }
        }
        return paths
    }

    /**
     * 得到当前手写地址
     */
    private fun getPathDraw():String{
        return paper?.filePath+"/draw/"
    }

    /**
     * 得到当前合图地址
     */
    private fun getPathMerge():String{
        return paper?.filePath+"/merge/"
    }

    /**
     * 得到当前合图地址
     */
    private fun getPathMergeStr(index: Int):String{
        return paper?.filePath+"/merge/${index}.png"
    }

    /**
     * 刷新增量更新
     */
    private fun refreshDataUpdate(){
        //更新目录增量数据
        DataUpdateManager.editDataUpdate(2, paper?.contentId!!, 2, paper?.typeId!!, Gson().toJson(paper))
    }

    /**
     * 开启自批
     */
    private fun gotoSelfCorrect(){
        hideLoading()
        val intent = Intent(this, CorrectActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("homeworkCommit", homeworkCommitInfoItem)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true)
        activityResultLauncher.launch(intent)
    }

    /**
     * 开始通知回调
     */
    private val activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode==10001){
            papers= daoManager?.queryAll(course,homeworkTypeId) as MutableList<HomeworkPaperBean>
            oldPosition=-1
            onContent()
        }
    }

}