package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.ResultStandardDetailsDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList.MessageBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessageList.ParentMessageBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.HomeworkCorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Collections
import java.util.stream.Collectors


/**
 * 普通作业本
 */
class HomeworkDrawingActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter :FileUploadPresenter
    private var course = ""//科目
    private var homeworkTypeId = 0//作业分组id
    private var contendId=0
    private var homeworkType: HomeworkTypeBean? = null
    private var isHomework=false//false本地作业 true老师布置作业
    private var homeworkContent: HomeworkContentBean? = null//当前作业内容
    private var homeworkContent_a: HomeworkContentBean? = null//a屏作业
    private var homeworks = mutableListOf<HomeworkContentBean>() //所有作业内容
    private var page = 0//页码
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem? = null

    override fun onToken(token: String) {
        FileImageUploadManager(token, homeworkCommitInfoItem?.paths!!).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map = HashMap<String, Any>()
                    if (homeworkType?.createStatus == 2) {
                        map["studentTaskId"] = homeworkCommitInfoItem?.messageId!!
                        map["studentUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkCommitInfoItem?.typeId!!
                        map["takeTime"]=homeworkCommitInfoItem?.takeTime!!
                        mUploadPresenter.commit(map)
                    } else {
                        map["id"] = homeworkCommitInfoItem?.messageId!!
                        map["submitUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkCommitInfoItem?.typeId!!
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
        showToastLong(if (homeworkCommitInfoItem?.submitState==0)"作业提交成功" else "作业已完成")
        for (homework in homeworks) {
            val mergePath=FileAddress().getPathHomeworkDrawingMerge(homework.path)
            if (FileUtils.isExist(mergePath)){
                if (homeworkCommitInfoItem?.submitState==1)
                    homework.title=homeworkCommitInfoItem?.title//不提交成功后改标题
                homework.isHomework=false//提交成功后改变作业为本地
                homework.date=System.currentTimeMillis()
                HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                refreshDataUpdate(homework)
            }
            else{
                HomeworkContentDaoManager.getInstance().deleteBean(homework)
                DataUpdateManager.deleteDateUpdate(2,homework.id.toInt(),2,homeworkTypeId)
            }
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
        isDrawingSave=isHomework

        page = intent.getIntExtra("page", DEFAULT_PAGE)
        homeworkTypeId =homeworkType?.typeId!!
        course = homeworkType?.course!!

        if (isHomework){
            when (homeworkType?.createStatus) {
                2 -> {
                    val item=homeworkType!!.messages[index] as MessageBean
                    homeworkCommitInfoItem=HomeworkCommitInfoItem().apply {
                        homeworkTypeId=homeworkType?.typeId!!
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
                }
                1 -> {
                    val item =homeworkType!!.messages[index]  as ParentMessageBean
                    homeworkCommitInfoItem=HomeworkCommitInfoItem().apply {
                        homeworkTypeId=homeworkType?.typeId!!
                        state=homeworkType?.state!!
                        messageId = item.contendId
                        title = item.title
                        typeId=item.typeId
                        standardTime=item.minute
                    }
                }
            }
            contendId=homeworkCommitInfoItem?.messageId!!
            homeworks =  HomeworkContentDaoManager.getInstance().queryAllByContentId(homeworkTypeId,homeworkCommitInfoItem?.messageId!!)
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

        if (isHomework&&getSelfCorrect()){
            showToast("请及时自批改后提交")
        }

        iv_btn.setOnClickListener {
            if (!NetworkUtil.isNetworkConnected()){
                showToast("网络连接失败")
                return@setOnClickListener
            }
            if (getSelfCorrect()){
                homeworkCommitInfoItem=Gson().fromJson(homeworkContent?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            if (homeworkCommitInfoItem?.submitState==0){
                CommonDialog(this,getCurrentScreenPos()).setContent("确定提交作业？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        showLoading()
                        Handler().postDelayed({
                            commit()
                        },500)
                    }
                })
            }
            else{
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    commit()
                }
                else{
                    //不提交作业直接完成
                    showLoading()
                    val map = HashMap<String, Any>()
                    map["studentTaskId"] = homeworkCommitInfoItem?.messageId!!
                    mUploadPresenter.commitHomework(map)
                }
            }
        }

        iv_score.setOnClickListener {
            val items=DataBeanManager.getResultStandardItems(homeworkType!!.state,homeworkType!!.name,homeworkContent!!.correctMode).stream().collect(Collectors.toList())
            ResultStandardDetailsDialog(this,homeworkContent?.title!!,homeworkContent?.score!!,if (homeworkType!!.state==10)10 else homeworkContent!!.correctMode,homeworkContent?.correctJson!!,items).builder()
        }

        onContent()
    }

    override fun onCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in homeworks) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = homeworks.indexOf(item)
            itemList.isEdit=true
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }
        }
        list.reverse()
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
        return if (contentBean.state==0){
            File(contentBean.path).exists()
        } else{
            true
        }
    }

    /**
     * 获取当前作业是否处于已提交自批状态
     */
    private fun getSelfCorrect():Boolean{
        return homeworkCommitInfoItem?.isSelfCorrect == true &&homeworkContent?.state==1 && !homeworkContent?.commitJson.isNullOrEmpty()
    }

    override fun onContent() {
        homeworkContent = homeworks[page]
        if (isExpand)
            homeworkContent_a = homeworks[page - 1]

        tv_page_total.text="${homeworks.size}"
        tv_page_total_a.text="${homeworks.size}"

        //已提交后不能手写，显示合图后的图片
        elik_b?.disableTouchInput(homeworkContent?.state == 1||homeworkType?.isCloud!!)
        val drawPath_b=homeworkContent?.path!!
        setElikLoadPath(elik_b!!, drawPath_b)
        tv_page.text = "${page + 1}"

        when(homeworkContent?.state){
            0->{
                MethodManager.setImageResource(this,ToolUtils.getImageResId(this, homeworkType?.contentResId),v_content_b)
            }
            else->{
                MethodManager.setImageFile(FileAddress().getPathHomeworkDrawingMerge(drawPath_b),v_content_b)
//                GlideUtils.setImageUrl(this, FileAddress().getPathHomeworkDrawingMerge(drawPath_b), v_content_b,homeworkContent?.state!!)
            }
        }
        if (isExpand) {
            elik_a?.disableTouchInput(homeworkContent_a?.state == 1||homeworkType?.isCloud!!)
            val drawPath_a=homeworkContent_a?.path!!
            setElikLoadPath(elik_a!!, drawPath_a)
            when(homeworkContent_a?.state){
                0->{
                    MethodManager.setImageResource(this,ToolUtils.getImageResId(this, homeworkType?.contentResId),v_content_a)
                }
                else->{
                    MethodManager.setImageFile(FileAddress().getPathHomeworkDrawingMerge(drawPath_a),v_content_a)
//                    GlideUtils.setImageUrl(this, FileAddress().getPathHomeworkDrawingMerge(drawPath_a), v_content_a,homeworkContent_a?.state!!)
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

        if (homeworkContent?.state==2&&homeworkContent?.correctJson!!.isNotEmpty()){
            showView(iv_score)
        }
        else{
            disMissView(iv_score)
        }
    }

    //设置手写
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
        if (isHomework){
            BitmapUtils.saveScreenShot(v_content_a, FileAddress().getPathHomeworkDrawingMerge(homeworkContent_a?.path!!))
        }
        refreshDataUpdate(homeworkContent_a!!)
    }

    override fun onElikSava_b() {
        if (isHomework){
            BitmapUtils.saveScreenShot(v_content_b, FileAddress().getPathHomeworkDrawingMerge(homeworkContent?.path!!))
        }
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
        if (isHomework)
            homeworkContent?.contentId=contendId

        page = homeworks.size
        val id = HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContent)
        homeworkContent?.id = id
        homeworks.add(homeworkContent!!)

        DataUpdateManager.createDataUpdateState(2, id.toInt(), 2,homeworkTypeId ,homeworkType?.state!!, Gson().toJson(homeworkContent), path)
    }


    //作业提交
    private fun commit() {
        setDisableTouchInput(true)
        homeworkCommitInfoItem?.paths?.clear()
        homeworkCommitInfoItem?.takeTime=getTakeTime()
        for (homework in homeworks) {
            val mergePath=FileAddress().getPathHomeworkDrawingMerge(homework.path)
            if (FileUtils.isExist(mergePath)){
                FileUtils.delete(homework.path)
                homeworkCommitInfoItem?.paths?.add(mergePath)
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    homework.commitJson=Gson().toJson(homeworkCommitInfoItem)
                }
                //提交后改变作业状态以及标题
                homework.title=homeworkCommitInfoItem?.title
                homework.state=1
                HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                refreshDataUpdate(homework)
            }
        }
        if (homeworkCommitInfoItem?.isSelfCorrect == true){
            gotoSelfCorrect()
        }
        else{
            mUploadPresenter.getToken()
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
        val intent = Intent(this, HomeworkCorrectActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("homeworkCommit", homeworkCommitInfoItem)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true)
        customStartActivity(intent)
    }

    /**
     * 刷新增量更新
     */
    private fun refreshDataUpdate(homework: HomeworkContentBean){
        DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2,homeworkTypeId,Gson().toJson(homework))
    }
}