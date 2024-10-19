package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.EinkPWInterface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
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
    private var homeworkContent: HomeworkContentBean? = null//当前作业内容
    private var homeworkContent_a: HomeworkContentBean? = null//a屏作业

    private var homeworks = mutableListOf<HomeworkContentBean>() //所有作业内容

    private var page = 0//页码
    private var messages = mutableListOf<ItemList>()
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
                    if (homeworkType?.createStatus == 1) {
                        map["studentTaskId"] = homeworkCommitInfoItem?.messageId!!
                        map["studentUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkTypeId
                        map["takeTime"]=takeTime
                        mUploadPresenter.commit(map)
                    } else {
                        map["id"] = homeworkCommitInfoItem?.messageId!!
                        map["submitUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkTypeId
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
        showToast(R.string.toast_commit_success)
        messages.removeAt(homeworkCommitInfoItem?.index!!)
        for (index in homeworkCommitInfoItem?.contents!!) {
            val homework = homeworks[index]
            homework.state = 1
            homework.title = homeworkCommitInfoItem?.title
            homework.contentId = homeworkCommitInfoItem?.messageId!!
            homework.commitDate = System.currentTimeMillis()
            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
            refreshDataUpdate(homework)
        }

        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            content=homeworkCommitInfoItem?.title
            homeworkTypeStr=homeworkType?.name
            course=homeworkType?.course
            time=System.currentTimeMillis()
        })

        onContent()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        initChangeScreenData()
        val bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkTypeBean
        page = intent.getIntExtra("page", DEFAULT_PAGE)
        homeworkTypeId = homeworkType?.typeId!!
        course = homeworkType?.course!!

        when (homeworkType?.createStatus) {
            1 -> {
                val list = homeworkType?.messages
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 3) {
                            messages.add(ItemList().apply {
                                id = item.studentTaskId
                                name = item.title
                                isSelfCorrect=item.selfBatchStatus==1
                            })
                        }
                    }
                }
            }
            2 -> {
                val list = homeworkType?.parents
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 1) {
                            messages.add(ItemList().apply {
                                id = item.id
                                name = item.content
                            })
                        }
                    }
                }
            }
        }

        homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(course, homeworkTypeId)

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
        iv_btn.setOnClickListener {
            if (messages.size == 0)
                return@setOnClickListener
            //开启自批
            if (homeworkContent?.isSelfCorrect==true && homeworkContent?.state==1&&!homeworkContent?.commitJson.isNullOrEmpty()){
                homeworkCommitInfoItem=Gson().fromJson(homeworkContent?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            if (NetworkUtil(this).isNetworkConnected()) {
                commit()
            } else {
                showToast("网络连接失败")
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
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=list[position].page){
                    page = list[position].page
                    onContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
                val item=homeworks[position]
                item.title=title
                HomeworkContentDaoManager.getInstance().insertOrReplace(item)
                refreshDataUpdate(item)
            }
        })
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                onContent()
            } else if (page == 2) {//当页面不够翻两页时
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
        if (isExpand) {
            when (page) {
                total -> {
                    newHomeWorkContent()
                    newHomeWorkContent()
                    page = homeworks.size - 1
                }
                total - 1 -> {
                    newHomeWorkContent()
                    page = homeworks.size - 1
                }
                else -> {
                    page += 2
                }
            }
        } else {
            if (page >= total) {
                newHomeWorkContent()
                page = homeworks.size - 1
            } else {
                page += 1
            }
        }
        onContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        if (homeworks.size == 1&&isExpand) {
            newHomeWorkContent()
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent() {
        homeworkContent = homeworks[page]
        if (isExpand) {
            if (page > 0) {
                homeworkContent_a = homeworks[page - 1]
            } else {
                page = 1
                homeworkContent = homeworks[page]
                homeworkContent_a = homeworks[0]
            }
        }

        tv_page_total.text="${homeworks.size}"
        tv_page_total_a.text="${homeworks.size}"

        if (homeworkContent?.isSelfCorrect == true &&homeworkContent?.state==1 && !homeworkContent?.commitJson.isNullOrEmpty()){
            showToast("请及时自批改后提交")
        }

        //已提交后不能手写，显示合图后的图片
        elik_b?.setPWEnabled(homeworkContent?.state != 1)
        when(homeworkContent?.state){
            0->{
                setElikLoadPath(elik_b!!, homeworkContent!!.path)
                GlideUtils.setImageUrl(this,ToolUtils.getImageResId(this, homeworkType?.contentResId), v_content_b)
            }
            1->{
                GlideUtils.setImageUrl(this,homeworkContent?.path, v_content_b,homeworkContent?.state!!)
            }
            2->{
                val file=File(homeworkContent?.path)
                GlideUtils.setImageUrl(this, file.path, v_content_b,homeworkContent?.state!!)
                val drawPath=file.parent+"/draw.png"
                setElikLoadPath(elik_b!!, drawPath)
            }
        }
        tv_page.text = "${page + 1}"

        if (isExpand) {
            elik_a?.setPWEnabled(homeworkContent_a?.state != 1)
            when(homeworkContent_a?.state){
                0->{
                    setElikLoadPath(elik_a!!, homeworkContent_a!!.path)
                    GlideUtils.setImageUrl(this,ToolUtils.getImageResId(this, homeworkType?.contentResId), v_content_a)
                }
                1->{
                    GlideUtils.setImageUrl(this, homeworkContent_a?.path, v_content_a,homeworkContent_a?.state!!)
                }
                2->{
                    val file=File(homeworkContent_a?.path)
                    GlideUtils.setImageUrl(this, file.path, v_content_a,homeworkContent_a?.state!!)
                    val drawPath=file.parent+"/draw.png"
                    setElikLoadPath(elik_b!!, drawPath)
                }
            }
            if (screenPos==Constants.SCREEN_LEFT){
                tv_page.text = "$page"
                tv_page_a.text = "${page + 1}"
            }
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text = "$page"
                tv_page.text = "${page + 1}"
            }
        }

        if (homeworkType?.createStatus==1)
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
        saveElik(elik_a!!, homeworkContent_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!, homeworkContent!!)
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

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface, homeworkContent: HomeworkContentBean) {
        elik.saveBitmap(true) {}
        refreshDataUpdate(homeworkContent)
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path = FileAddress().getPathHomework(course, homeworkTypeId, homeworks.size+1)
        val currentTime=System.currentTimeMillis()

        homeworkContent = HomeworkContentBean()
        homeworkContent?.course = course
        homeworkContent?.date = currentTime
        homeworkContent?.homeworkTypeId = homeworkTypeId
        homeworkContent?.bgResId = homeworkType?.bgResId
        homeworkContent?.typeStr = homeworkType?.name
        homeworkContent?.title = getString(R.string.unnamed) + (homeworks.size + 1)
        homeworkContent?.path = "$path/${DateUtils.longToString(currentTime)}.png"
        homeworkContent?.page = homeworks.size

        page = homeworks.size

        val id = HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContent)
        homeworkContent?.id = id
        homeworks.add(homeworkContent!!)

        DataUpdateManager.createDataUpdateState(2, id.toInt(), 2,homeworkTypeId ,2, Gson().toJson(homeworkContent), homeworkContent?.path!!)
    }


    //作业提交
    private fun commit() {
        DrawingCommitDialog(this, getCurrentScreenPos(),0,homeworks.size, messages).builder()
            .setOnDialogClickListener {
                homeworkCommitInfoItem = it
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    for (item in homeworkType?.messages!!){
                        if(homeworkCommitInfoItem?.messageId==item.studentTaskId){
                            homeworkCommitInfoItem?.correctJson=item.question
                            homeworkCommitInfoItem?.correctMode=item.questionType
                            homeworkCommitInfoItem?.scoreMode=item.questionMode
                            homeworkCommitInfoItem?.answerUrl=item.answerUrl
                            homeworkCommitInfoItem?.typeId=homeworkTypeId
                            homeworkCommitInfoItem?.typeName=homeworkType?.name
                            homeworkCommitInfoItem?.course=homeworkType?.course
                            homeworkCommitInfoItem?.state=homeworkType?.state
                        }
                    }
                }
                showLoading()
                commitItems.clear()
                for (index in homeworkCommitInfoItem?.contents!!) {
                    val homework = homeworks[index]
                    homeworkCommitInfoItem?.paths?.add(homework.path)
                    //异步合图后排序
                    Thread {
                        val path = saveImage(homework)
                        commitItems.add(ItemList().apply {
                            id = index
                            url = path
                        })
                        if (commitItems.size == homeworkCommitInfoItem?.contents!!.size) {
                            takeTime=System.currentTimeMillis()-getStartTime(homeworkCommitInfoItem?.contents!!)
                            homeworkCommitInfoItem?.takeTime=takeTime
                            if (homeworkCommitInfoItem?.isSelfCorrect == true){
                                hideLoading()
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
    }

    /**
     * 获取开始时间
     */
    private fun getStartTime(pages:List<Int>):Long{
        val times = mutableListOf<Long>()
        for (page in pages){
            if (homeworks[page].startDate>0){
                times.add(homeworks[page].startDate)
            }
        }
        if (times.size==0){
            return 0
        }
        return Collections.min(times)
    }

    /**
     * 开启自批
     */
    private fun gotoSelfCorrect(){
        hideLoading()
        for (index  in homeworkCommitInfoItem?.contents!!){
            val homework=homeworks[index]
            if (!homework.isSelfCorrect){
                homework.commitJson=Gson().toJson(homeworkCommitInfoItem)
                homework.state=1
                homework.isSelfCorrect=true
                homework.contentId=homeworkCommitInfoItem?.messageId!!
                homework.answerUrl=homeworkCommitInfoItem?.answerUrl
                homework.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                homework.correctMode=homeworkCommitInfoItem?.correctMode!!
                HomeworkContentDaoManager.getInstance().insertOrReplace(homework)

                refreshDataUpdate(homework)
            }
        }

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
        DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2,homeworkTypeId,Gson().toJson(homework))
    }

    /**
     * 开始通知回调
     */
    private val activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode==10001){
            val iterator=messages.iterator()
            while (iterator.hasNext()){
                val item=iterator.next()
                if (item.id==homeworkCommitInfoItem?.messageId){
                    iterator.remove()
                }
            }
            homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(course, homeworkTypeId)
            onContent()
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
        return drawPath
    }

}