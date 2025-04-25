package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.os.Bundle
import android.view.EinkPWInterface
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList.MessageBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IFileUploadView
import com.bll.lnkstudy.ui.activity.HomeworkCorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.ac_drawing.ll_score
import kotlinx.android.synthetic.main.common_correct_score.rl_topic_content
import kotlinx.android.synthetic.main.common_correct_score.tv_answer
import kotlinx.android.synthetic.main.common_correct_score.tv_correct_title
import kotlinx.android.synthetic.main.common_correct_score.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File


/**
 * 作业卷提交
 */
class HomeworkPaperDrawingActivity: BaseDrawingActivity(),IFileUploadView {

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var homeworkType:HomeworkTypeBean?=null
    private var isHomework=false
    private var course=""
    private var homeworkTypeId=0//分组id
    private var daoManager: HomeworkPaperDaoManager?=null
    private var papers= mutableListOf<HomeworkPaperBean>()
    private var paper: HomeworkPaperBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var page = 0//页码
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem?=null

    override fun onToken(token: String) {
        FileImageUploadManager(token, paper?.paths!!).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=homeworkCommitInfoItem?.messageId!!
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = homeworkCommitInfoItem?.typeId!!
                    map["takeTime"]=homeworkCommitInfoItem?.takeTime!!
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
        showToastLong(if (paper?.endTime!!>0)"作业提交成功" else "作业已完成")

        paper?.isHomework = false
        paper?.date=System.currentTimeMillis()
        daoManager?.insertOrReplace(paper)
        refreshDataUpdate()

        if (paper?.endTime!!>0){
            FileUtils.deleteFile(File(getPathDraw()))
            FileUtils.deleteFile(File(getPathMerge()))
        }

        EventBus.getDefault().post(Constants.HOMEWORK_MESSAGE_COMMIT_EVENT)
        finish()
    }


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        initChangeScreenData()
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)
        val index = intent.getIntExtra("messageIndex", DEFAULT_PAGE)
        isHomework=index>=0
        course=homeworkType?.course!!
        homeworkTypeId=homeworkType?.typeId!!
        currentPosition=intent.getIntExtra("page",DEFAULT_PAGE)

        daoManager= HomeworkPaperDaoManager.getInstance()
        if (isHomework){
            val item=homeworkType!!.messages[index] as MessageBean
            homeworkCommitInfoItem=HomeworkCommitInfoItem().apply {
                state=homeworkType?.state!!
                messageId =item.contendId
                title = item.title
                typeId=item.typeId
                isSelfCorrect=item.selfBatchStatus==1
                correctJson=item.question
                correctMode=item.questionType
                scoreMode=item.questionMode
                answerUrl=item.answerUrl
                submitState=item.submitState
                standardTime=item.minute
            }
            papers.add(daoManager?.queryByContentID(item.contendId)!!)
        }
        else{
            papers= daoManager?.queryAllByLocal(course,homeworkTypeId) as MutableList<HomeworkPaperBean>
        }

        if(papers.size>0){
            if (currentPosition == DEFAULT_PAGE)
                currentPosition=papers.size-1
            onContent()
        }
        else{
            setDisableTouchInput(true)
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        if (homeworkType?.isCloud!!||!isHomework){
            disMissView(iv_btn)
        }

        if (isHomework&&getSelfCorrect()){
            showToast("请及时自批改后提交")
        }

        iv_btn.setOnClickListener {
            if (!NetworkUtil.isNetworkConnected()){
                showToast("网络连接失败，无法提交")
                return@setOnClickListener
            }
            if (getSelfCorrect()){
                homeworkCommitInfoItem=Gson().fromJson(paper?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            if (paper?.endTime!!>0){
                if (!FileUtils.isExistContent(getPathMerge())){
                    showToast("未填写答案,无法提交")
                    return@setOnClickListener
                }
                CommonDialog(this,getCurrentScreenPos()).setContent("确定提交作业卷？").builder().setDialogClickListener(
                    object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            showLoading()
                            setDisableTouchInput(true)
                            commit()
                        }
                    })
            }
            else{
                //不提交作业直接完成
                showLoading()
                val map = HashMap<String, Any>()
                map["studentTaskId"] = paper?.contentId!!
                mUploadPresenter.commitHomework(map)
            }
        }
    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=papers.indexOf(item)
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
            onContent()
        }
        else{
            if (currentPosition<papers.size-1){
                currentPosition+=1
                page=0
                onContent()
            }
        }
    }

    override fun onPageUp() {
        if (page>0){
            page-=if (isExpand)2 else 1
            onContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                onContent()
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
        }
        //用来判断重复加载
        oldPosition=currentPosition
    }

    /**
     * 获取当前作业是否处于已提交自批状态
     */
    private fun getSelfCorrect():Boolean{
        return paper?.isSelfCorrect == true &&paper?.state==1 && !paper?.commitJson.isNullOrEmpty()
    }

    /**
     * 需要提交且状态为0
     */
    private fun isDrawing():Boolean{
        return isHomework&&paper?.state==0&&paper?.endTime!!>0
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
                disMissView(rl_topic_content)
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
        if (isDrawing()){
            BitmapUtils.saveScreenShot(v_content_a, getPathMergeStr(page))
        }
        refreshDataUpdate()
    }

    override fun onElikSava_b() {
        if (isDrawing()){
            if (isExpand){
                BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(page+1))
            }
            else{
                BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(page))
            }
        }
        refreshDataUpdate()
    }

    override fun onElikStart_a() {
        if (paper?.startDate==0L){
            paper?.startDate=System.currentTimeMillis()
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
        }
    }

    override fun onElikStart_b() {
        if (paper?.startDate==0L){
            paper?.startDate=System.currentTimeMillis()
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
        }
    }

    /**
     * 提交
     */
    private fun commit(){
        homeworkCommitInfoItem?.takeTime=System.currentTimeMillis()- paper?.startDate!!
        homeworkCommitInfoItem?.paths=paper?.paths
        if (paper?.state==0){
            for (i in paper?.paths!!.indices){
                val mergePath=getPathMergeStr(i)
                if (FileUtils.isExist(mergePath)){
                    FileUtils.replaceFileContents(mergePath, paper?.paths!![i])
                }
            }
            //修改当前paper状态
            paper?.state = 1
            paper?.commitJson=Gson().toJson(homeworkCommitInfoItem)
            daoManager?.insertOrReplace(paper)
            refreshDataUpdate()
        }

        if (homeworkCommitInfoItem?.isSelfCorrect == true){
            gotoSelfCorrect()
        }
        else{
            mUploadPresenter.getToken()
        }
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
        return paper?.filePath+"/merge/${index+1}.png"
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
        val intent = Intent(this, HomeworkCorrectActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("homeworkCommit", homeworkCommitInfoItem)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true)
        customStartActivity(intent)
    }
}