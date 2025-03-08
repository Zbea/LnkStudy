package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.EinkPWInterface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.Constants.Companion.RESULT_10001
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.CorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
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
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Collections


/**
 * 普通作业本
 */
class HomeworkDrawingActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter :FileUploadPresenter
    private var course = ""//科目
    private var homeworkTypeId = 0//作业分组id
    private var homeworkType: HomeworkTypeBean? = null
    private var isHomework=false//false本地作业 true老师布置作业
    private var itemMessageBean:ItemList?=null
    private var homeworkContent: HomeworkContentBean? = null//当前作业内容
    private var homeworkContent_a: HomeworkContentBean? = null//a屏作业
    private var homeworks = mutableListOf<HomeworkContentBean>() //所有作业内容
    private var page = 0//页码
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem? = null
    private val commitItems = mutableListOf<ItemList>()
    private var takeTime=0L

    override fun onToken(token: String) {
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map = HashMap<String, Any>()
                    if (homeworkType?.createStatus == 2) {
                        map["studentTaskId"] = itemMessageBean?.id!!
                        map["studentUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = itemMessageBean?.typeId!!
                        map["takeTime"]=takeTime
                        mUploadPresenter.commit(map)
                    } else {
                        map["id"] = itemMessageBean?.id!!
                        map["submitUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = itemMessageBean?.typeId!!
                        mUploadPresenter.commitParent(map)
                    }
                }

                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onCommitSuccess() {
        showToast(if (itemMessageBean?.submitState==0)"作业提交成功" else "作业已完成")
        val localHomeworks=HomeworkContentDaoManager.getInstance().queryAllByLocalContent(course, homeworkTypeId)
        for (homework in homeworks) {
            if (itemMessageBean?.submitState==1)
                homework.title=itemMessageBean?.name//不提交成功后改标题
            homework.isHomework=false//提交成功后改变作业为本地
            homework.page=localHomeworks.size+homeworks.indexOf(homework)//改变本地页码
            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
            refreshDataUpdate(homework)
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
        page = intent.getIntExtra("page", DEFAULT_PAGE)
        homeworkTypeId =homeworkType?.typeId!!
        course = homeworkType?.course!!
        if (isHomework){
            when (homeworkType?.createStatus) {
                2 -> {
                    val item=homeworkType!!.messages[index]
                    itemMessageBean=ItemList().apply {
                        id =item.contendId
                        name = item.title
                        typeId=item.typeId
                        isSelfCorrect=item.selfBatchStatus==1
                        question=item.question
                        questionType=item.questionType
                        questionMode=item.questionMode
                        answerUrl=item.answerUrl
                        submitState=item.submitState
                    }
                }
                1 -> {
                    val item =homeworkType!!.parents[index]
                    itemMessageBean=ItemList().apply {
                        id = item.contendId
                        name = item.title
                        typeId=item.typeId
                    }
                }
            }

            homeworks =  HomeworkContentDaoManager.getInstance().queryAllByContentId(homeworkTypeId,itemMessageBean?.id!!)
        } else{
            homeworks =  HomeworkContentDaoManager.getInstance().queryAllByLocalContent(course, homeworkTypeId)
        }

        if (homeworks.size > 0) {
            if (page == DEFAULT_PAGE)
                page = homeworks.size - 1
            homeworkContent = homeworks[page]
        } else {
            newHomeWorkContent()
        }

    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        //云书库没有提交按钮
        if (homeworkType?.isCloud!!||!isHomework){
            disMissView(iv_btn)
        }

        iv_btn.setOnClickListener {
            if (getSelfCorrect()){
                homeworkCommitInfoItem=Gson().fromJson(homeworkContent?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            if (NetworkUtil(this).isNetworkConnected()) {
                showLoading()
                if (itemMessageBean?.submitState==0){
                    Handler().postDelayed({
                        commit()
                    },500)
                }
                else{
                    //不提交作业直接完成
                    val map = HashMap<String, Any>()
                        map["studentTaskId"] = itemMessageBean?.id!!
                        mUploadPresenter.commitHomework(map)
                }
            } else {
                showToast("网络连接失败，无法提交")
            }
        }

        onContent()
    }

    override fun onCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in homeworks) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = item.page
            itemList.isEdit=true
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,true).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (page!=pageNumber){
                    page = pageNumber
                    onContent()
                }
            }
            override fun onEdit(title: String, pages: List<Int>) {
                for (page in pages){
                    val item=homeworks[page]
                    item.title=title
                    HomeworkContentDaoManager.getInstance().insertOrReplace(item)
                    refreshDataUpdate(item)
                }
            }
        })
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                onContent()
            } else if (page == 2) {
                page = 1
                onContent()
            }
        } else {
            if (page > 0) {
                page -= 1
                onContent()
            }
        }
    }

    override fun onPageDown() {
        val total = homeworks.size - 1
        if(isExpand){
            if (page<total-1){
                page+=2
                onContent()
            }
            else if (page==total-1){
                if (isDrawLastContent()){
                    newHomeWorkContent()
                    onContent()
                }
                else{
                    page=total
                    onContent()
                }
            }
        }
        else{
            if (page==total) {
                if (isDrawLastContent()){
                    newHomeWorkContent()
                    onContent()
                }
            } else {
                page += 1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        if (homeworks.size==1){
            //如果最后一张已写,则可以在全屏时创建新的
            if (isDrawLastContent()){
                newHomeWorkContent()
            }
            else{
                return
            }
        }
        if (page==0){
            page=1
        }
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    /**
     * 最新content是否已写
     */
    private fun isDrawLastContent():Boolean{
        val contentBean = homeworks.last()
        return File(contentBean.path).exists()
    }

    /**
     * 获取当前作业是否处于已提交自批状态
     */
    private fun getSelfCorrect():Boolean{
        return itemMessageBean?.isSelfCorrect == true &&homeworkContent?.state==1 && !homeworkContent?.commitJson.isNullOrEmpty()
    }

    override fun onContent() {
        homeworkContent = homeworks[page]
        if (isExpand)
            homeworkContent_a = homeworks[page - 1]

        tv_page_total.text="${homeworks.size}"
        tv_page_total_a.text="${homeworks.size}"

        if (getSelfCorrect()){
            showToast("请及时自批改后提交")
        }

        //已提交后不能手写，显示合图后的图片
        elik_b?.disableTouchInput(homeworkContent?.state == 1||homeworkType?.isCloud!!)
        when(homeworkContent?.state){
            0->{
                setElikLoadPath(elik_b!!, homeworkContent!!.path)
                MethodManager.setImageResource(this,ToolUtils.getImageResId(this, homeworkType?.contentResId),v_content_b)
            }
            else->{
                GlideUtils.setImageCacheUrl(this, homeworkContent?.path, v_content_b,homeworkContent?.state!!)
                val file=File(homeworkContent?.path)
                val drawPath=file.parent+"/draw.png"
                setElikLoadPath(elik_b!!, drawPath)
            }
        }
        tv_page.text = "${page + 1}"

        if (isExpand) {
            elik_a?.disableTouchInput(homeworkContent_a?.state == 1||homeworkType?.isCloud!!)
            when(homeworkContent_a?.state){
                0->{
                    setElikLoadPath(elik_a!!, homeworkContent_a!!.path)
                    MethodManager.setImageResource(this,ToolUtils.getImageResId(this, homeworkType?.contentResId),v_content_a)
                }
                else->{
                    GlideUtils.setImageCacheUrl(this, homeworkContent_a?.path, v_content_a,homeworkContent_a?.state!!)
                    val file=File(homeworkContent_a?.path)
                    val drawPath=file.parent+"/draw.png"
                    setElikLoadPath(elik_b!!, drawPath)
                }
            }
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text = "$page"
            }
            else{
                tv_page.text = "$page"
                tv_page_a.text = "${page + 1}"
            }
        }

        if (homeworkType?.createStatus==2)
            setScoreDetails(homeworkContent!!)
    }

    /**
     * 设置批改详情
     */
    private fun setScoreDetails(item:HomeworkContentBean){
        if (item.state==2){
            if (!ll_score.isVisible)
                showView(iv_score)
            correctMode=item.correctMode
            scoreMode=item.scoreMode
            if (item.answerUrl.isNullOrEmpty()){
                disMissView(tv_answer)
            }
            else{
                answerImages= item.answerUrl?.split(",") as MutableList<String>
                showView(tv_answer)
            }
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

    //设置手写
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
//        if (isHomework){
//            Thread {
//                BitmapUtils.saveScreenShot(this, v_content_a, getPathMergeStr(page+1))
//            }.start()
//        }
        refreshDataUpdate(homeworkContent_a!!)
    }

    override fun onElikSava_b() {
//        if (isHomework){
//            if (isExpand){
//                Thread {
//                    BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(page+1+1))
//                }.start()
//            }
//            else{
//                Thread {
//                    BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(page+1))
//                }.start()
//            }
//        }
        refreshDataUpdate(homeworkContent!!)
    }

    override fun onElikStart_a() {
        if (homeworkContent_a?.startDate==0L){
            homeworkContent_a?.startDate=System.currentTimeMillis()
            HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent_a)
            refreshDataUpdate(homeworkContent_a!!)
        }
    }

    override fun onElikStart_b() {
        if (homeworkContent?.startDate==0L){
            homeworkContent?.startDate=System.currentTimeMillis()
            HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent)
            refreshDataUpdate(homeworkContent!!)
        }
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {
        val currentTime=System.currentTimeMillis()
        val contendId=if (isHomework) itemMessageBean?.id else ToolUtils.getDateId()
        val path = if (isHomework)FileAddress().getPathHomework(course, homeworkTypeId, contendId,homeworks.size+1) else FileAddress().getPathHomework(course, homeworkTypeId, contendId)

        homeworkContent = HomeworkContentBean()
        homeworkContent?.course = course
        homeworkContent?.date = currentTime
        homeworkContent?.homeworkTypeId = homeworkTypeId
        homeworkContent?.typeName = homeworkType?.name
        homeworkContent?.title = getString(R.string.unnamed) + (homeworks.size + 1)
        homeworkContent?.path = "$path/${DateUtils.longToString(currentTime)}.png"
        homeworkContent?.fromStatus=homeworkType?.fromStatus
        homeworkContent?.isHomework=isHomework
        if (isHomework){
            homeworkContent?.contentId=contendId
        }
        else{
            homeworkContent?.page=homeworks.size
        }
        page = homeworks.size
        val id = HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContent)
        homeworkContent?.id = id
        homeworks.add(homeworkContent!!)

        DataUpdateManager.createDataUpdateState(2, id.toInt(), 2,homeworkTypeId ,homeworkType?.state!!, Gson().toJson(homeworkContent), path)
    }


    //作业提交
    private fun commit() {
        setDisableTouchInput(true)
        commitItems.clear()
        if (itemMessageBean?.isSelfCorrect==true){
            homeworkCommitInfoItem=HomeworkCommitInfoItem()
            homeworkCommitInfoItem?.homeworkTypeId=homeworkTypeId
            homeworkCommitInfoItem?.typeName=homeworkType?.name
            homeworkCommitInfoItem?.course=homeworkType?.course
            homeworkCommitInfoItem?.state=homeworkType?.state
            homeworkCommitInfoItem?.createStatus=homeworkType?.createStatus
            homeworkCommitInfoItem?.messageId=itemMessageBean?.id
            homeworkCommitInfoItem?.typeId=itemMessageBean?.typeId
            homeworkCommitInfoItem?.correctJson=itemMessageBean?.question
            homeworkCommitInfoItem?.correctMode=itemMessageBean?.questionType
            homeworkCommitInfoItem?.scoreMode=itemMessageBean?.questionMode
            homeworkCommitInfoItem?.answerUrl=itemMessageBean?.answerUrl
        }
        for (homework in homeworks) {
            if (itemMessageBean?.isSelfCorrect==true){
                homeworkCommitInfoItem?.paths?.add(homework.path)
                homework.commitJson=Gson().toJson(homeworkCommitInfoItem)
            }
            //提交后改变作业状态以及标题
            homework.title=itemMessageBean?.name
            homework.state=1
            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
            refreshDataUpdate(homework)

            //异步合图后排序
            Thread {
                val path = saveImage(homework)
                commitItems.add(ItemList().apply {
                    id = homeworks.indexOf(homework)
                    url = path
                })
                if (commitItems.size == homeworks.size) {
                    takeTime=getTakeTime()
                    homeworkCommitInfoItem?.takeTime=takeTime
                    if (itemMessageBean?.isSelfCorrect == true){
                        gotoSelfCorrect()
                    }
                    else{
                        commitItems.sort()
                        mUploadPresenter.getToken()
                    }
                }
            }.start()
        }
    }

    /**
     * 获取用时
     */
    private fun getTakeTime():Long{
        val times = mutableListOf<Long>()
        for (homework in homeworks){
            if (homework.startDate>0){
                times.add(homework.startDate)
            }
        }
        if (times.size==0){
            return 0
        }
        return System.currentTimeMillis()-Collections.min(times)
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
     * 刷新增量更新
     */
    private fun refreshDataUpdate(homework: HomeworkContentBean){
        DataUpdateManager.editDataUpdateState(2, homework.id.toInt(), 2,homeworkTypeId,homeworkType?.state!!,Gson().toJson(homework))
    }

    /**
     * 开始通知回调
     */
    private val activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== RESULT_10001){
            finish()
        }
    }

    /**
     * 合图
     */
    private fun saveImage(homework: HomeworkContentBean): String {
        val resId = ToolUtils.getImageResId(this, homeworkType?.contentResId)
        val options = BitmapFactory.Options()
        options.inScaled = false
        val oldBitmap = BitmapFactory.decodeResource(resources, resId,options)

        val drawPath = homework.path
        val drawBitmap = BitmapFactory.decodeFile(drawPath)
        if (drawBitmap != null) {
            val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
            BitmapUtils.saveBmpGallery(this, mergeBitmap, drawPath)
        }
        else{
            BitmapUtils.saveBmpGallery(this,oldBitmap,drawPath)
        }
        return drawPath
    }

}