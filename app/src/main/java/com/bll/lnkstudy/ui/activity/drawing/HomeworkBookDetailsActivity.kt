package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.os.Bundle
import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogBookDialog
import com.bll.lnkstudy.dialog.HomeworkCommitDialog
import com.bll.lnkstudy.dialog.ResultStandardDetailsDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList.MessageBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessageList.ParentMessageBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.HomeworkCorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
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


class HomeworkBookDetailsActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var homeworkType: HomeworkTypeBean? = null
    private var isHomework=false//false本地作业 true老师布置作业
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem?=null
    private var book: HomeworkBookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var startCount=0
    private var page = 0 //当前页码
    private var bookId=0

    override fun onToken(token: String) {
        FileImageUploadManager(token, homeworkCommitInfoItem?.paths!!).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    if (homeworkType?.createStatus==2){
                        map["studentTaskId"]=homeworkCommitInfoItem?.messageId!!
                        map["page"]=ToolUtils.getImagesStr(homeworkCommitInfoItem?.contents)
                        map["studentUrl"]= ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkCommitInfoItem?.typeId!!
                        map["takeTime"] = homeworkCommitInfoItem?.takeTime!!
                        mUploadPresenter.commit(map)
                    }
                    else{
                        map["pageStr"]=ToolUtils.getImagesStr(homeworkCommitInfoItem?.contents)
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
        EventBus.getDefault().post(Constants.HOMEWORK_MESSAGE_COMMIT_EVENT)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        initChangeScreenData()
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)
        bookId=homeworkType?.bookId!!
        val index = intent.getIntExtra("messageIndex", DEFAULT_PAGE)
        isHomework=index>=0
        isDrawingSave=isHomework

        if (isHomework){
            when (homeworkType?.createStatus) {
                2 -> {
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
                }
                1 -> {
                    val item =homeworkType!!.messages[index]  as ParentMessageBean
                    homeworkCommitInfoItem=HomeworkCommitInfoItem().apply {
                        state=homeworkType?.state!!
                        messageId = item.contendId
                        title = item.title
                        typeId=item.typeId
                        standardTime=item.minute
                    }
                }
            }
        }
        book = HomeworkBookDaoManager.getInstance().queryBookByID(bookId)
        if (book == null) return
        page = book?.pageIndex!!
        val catalogFilePath = FileAddress().getPathBookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(catalogFilePath))
        {
            val catalogMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(catalogFilePath)))
            catalogMsg = Gson().fromJson(catalogMsgStr, CatalogMsg::class.java)
            if (catalogMsg!=null){
                for (item in catalogMsg?.contents!!) {
                    val catalogParentBean = CatalogParentBean()
                    catalogParentBean.title = item.title
                    catalogParentBean.pageNumber = item.pageNumber
                    catalogParentBean.picName = item.picName
                    for (ite in item.subItems) {
                        val catalogChildBean = CatalogChildBean()
                        catalogChildBean.title = ite.title
                        catalogChildBean.pageNumber = ite.pageNumber
                        catalogChildBean.picName = ite.picName
                        catalogParentBean.addSubItem(catalogChildBean)
                    }
                    catalogs.add(catalogParentBean)
                }
                pageCount =  catalogMsg?.totalCount!!
                startCount =  if (catalogMsg?.startCount!!-1<0)0 else catalogMsg?.startCount!!-1
            }
        }
        else{
            pageCount=FileUtils.getFiles(FileAddress().getPathBookPicture(book?.bookPath!!)).size
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        if (homeworkType?.isCloud!!||!isHomework){
            disMissView(iv_btn)
        }

        iv_btn.setOnClickListener {
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
            if (correctBean!=null&&correctBean.state==1&&!correctBean.commitJson.isNullOrEmpty()){
                val item=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
                homeworkCommitInfoItem=Gson().fromJson(item?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }

            if (!NetworkUtil.isNetworkConnected()){
                showToast("网络连接失败")
                return@setOnClickListener
            }
            if (homeworkCommitInfoItem?.submitState==0){
                HomeworkCommitDialog(this,getCurrentScreenPos(),startCount,pageCount,homeworkCommitInfoItem!!.title).builder().setOnDialogClickListener {
                    showLoading()
                    commit(it)
                }
            }
            else{
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    HomeworkCommitDialog(this,getCurrentScreenPos(),startCount,pageCount,homeworkCommitInfoItem!!.title).builder().setOnDialogClickListener {
                        commit(it)
                    }
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
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
            if (correctBean!=null&&correctBean.state==2){
                val items=DataBeanManager.getResultStandardItems(4,"",correctBean.correctMode).stream().collect(Collectors.toList())
                ResultStandardDetailsDialog(this,correctBean.homeworkTitle,correctBean.score,correctBean.correctMode,correctBean.correctJson,items).builder()
            }
        }

        onContent()
    }

    override fun onPageUp() {
        if (page > 0) {
            page -= if(isExpand)2 else 1
            onContent()
        }
    }

    override fun onPageDown() {
        if (page<pageCount-1){
            page+=if(isExpand)2 else 1
            onContent()
        }
    }

    override fun onCatalog() {
        CatalogBookDialog(this,screenPos, getCurrentScreenPos(),catalogs, startCount).builder().setOnDialogClickListener { pageNumber ->
            if (page != pageNumber - 1) {
                page = pageNumber - 1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    /**
     * 更新内容
     */
    override fun onContent() {
        if (pageCount==0)
            return
        if (page<0)
            page=0
        if (page>=pageCount){
            page=pageCount-1
        }
        if (page>pageCount-2&&isExpand)
            page=pageCount-2

        if (isExpand){
            val page_up=page+1//上一页页码
            loadPicture(page, elik_a!!, v_content_a!!)
            loadPicture(page_up, elik_b!!, v_content_b!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page,tv_page_a,tv_page_total_a)
                setPageCurrent(page_up,tv_page,tv_page_total)
            }
            else{
                setPageCurrent(page,tv_page,tv_page_total)
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
        }
        else{
            loadPicture(page, elik_b!!, v_content_b!!)
            setPageCurrent(page,tv_page,tv_page_total)
        }

        val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
        if (correctBean!=null&&correctBean.state==2&&correctBean.correctJson.isNotEmpty()){
            showView(iv_score)
        }
        else{
            disMissView(iv_score)
        }
    }

    /**
     * 设置当前页面页码
     */
    private fun setPageCurrent(currentPage:Int, tvPage: TextView, tvPageTotal: TextView){
        tvPage.text = if (currentPage>=startCount) "${currentPage-startCount+1}" else ""
        tvPageTotal.text=if (currentPage>=startCount) "${pageCount-startCount}" else ""
    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = FileUtils.getIndexFile(book?.bookPath,index)
        if (showFile != null) {
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
            val path=if (correctBean==null||correctBean.state==0){
                showFile.path
            }
            else{
                FileAddress().getPathHomeworkBookCorrectFile(book?.bookDrawPath!!,index)
            }
            MethodManager.setImageFile(path,view)

            val drawPath =FileAddress().getPathHomeworkBookDrawFile(book?.bookDrawPath!!,index)
            elik.setLoadFilePath(drawPath, true)

            if (homeworkType?.isCloud!!||isCommitState(index)){
                elik.disableTouchInput(true)
            }
            else{
                elik.disableTouchInput(false)
            }
        }
    }

    /**
     * 获取是否是提交状态
     */
    private fun isCommitState(page:Int):Boolean{
        val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
        return if (correctBean!=null){
            correctBean.state==1
        } else{
            false
        }
    }

    override fun onElikSava_a() {
        if (isDrawingSave){
            val mergePath=FileAddress().getPathHomeworkBookCorrectFile(book?.bookDrawPath!!,page-1)
            BitmapUtils.saveScreenShot(v_content_a, mergePath)
        }
        editCorrectBean(page-1)
    }

    override fun onElikSava_b() {
        if (isDrawingSave){
            val mergePath=FileAddress().getPathHomeworkBookCorrectFile(book?.bookDrawPath!!,page)
            BitmapUtils.saveScreenShot(v_content_b, mergePath)
        }
        editCorrectBean(page)
    }

    override fun onElikStart_a() {
        createCorrectBean(page-1)
    }

    override fun onElikStart_b() {
        createCorrectBean(page)
    }

    /**
     * 创建手写
     */
    private fun createCorrectBean(page:Int){
        if (!HomeworkBookCorrectDaoManager.getInstance().isExist(bookId, page)){
            val bookCorrectBean = HomeworkBookCorrectBean()
            bookCorrectBean.bookId = bookId
            bookCorrectBean.page = page
            bookCorrectBean.startTime=System.currentTimeMillis()
            val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
            val path=FileAddress().getPathHomeworkBookDrawPath(book?.bookDrawPath!!,page)
            //更新增量数据
            DataUpdateManager.createDataUpdate(2, id.toInt(),3,bookCorrectBean.bookId ,Gson().toJson(bookCorrectBean),path)
        }
    }

    /**
     * 手写后更新增量更新
     */
    private fun editCorrectBean(page: Int){
        val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
        if (correctBean!=null)
            DataUpdateManager.editDataUpdate(2,correctBean.id.toInt(),3,bookId)
    }

    /**
     * 题卷提交
     */
    private fun commit(pages: List<Int>) {
        setDisableTouchInput(true)
        homeworkCommitInfoItem?.bookId=bookId
        homeworkCommitInfoItem?.takeTime=getTakeTime(pages)
        homeworkCommitInfoItem?.paths?.clear()
        homeworkCommitInfoItem?.contents=pages
        for (page in pages){
            val imageFile=FileUtils.getIndexFile(book?.bookPath,page)
            val oldPath=imageFile?.path.toString()
            val drawPath =FileAddress().getPathHomeworkBookDrawFile(book?.bookDrawPath!!,page)
            val mergePath=FileAddress().getPathHomeworkBookCorrectFile(book?.bookDrawPath!!,page)
            //不存在手写时拿原图
            if (FileUtils.isExist(mergePath)){
                homeworkCommitInfoItem?.paths?.add(mergePath)
                FileUtils.deleteFile(File(drawPath))
            }
            else{
                homeworkCommitInfoItem?.paths?.add(oldPath)
            }
        }
        setBookCorrectBean(pages)
        if (homeworkCommitInfoItem?.isSelfCorrect == true){
            gotoSelfCorrect()
        }
        else{
            mUploadPresenter.getToken()
        }
    }

    /**
     * 获取开始时间
     */
    private fun getTakeTime(pages: List<Int>): Long {
        val times = mutableListOf<Long>()
        for (page in pages) {
            val correctBean = HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
            if (correctBean!=null) {
                if (correctBean.startTime > 0) {
                    times.add(correctBean.startTime)
                }
            }
        }
        if (times.size==0){
            return 0
        }
        return System.currentTimeMillis()-Collections.min(times)
    }

    /**
     * 设置当页作业提交详情
     */
    private fun setBookCorrectBean(pages: List<Int>){
        for (page in pages){
            var bookCorrectBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
            if (bookCorrectBean!=null){
                bookCorrectBean.homeworkTitle=homeworkCommitInfoItem?.title
                bookCorrectBean.state = 1
                bookCorrectBean.contendId=homeworkCommitInfoItem?.messageId!!
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    bookCorrectBean.isSelfCorrect=true
                    bookCorrectBean.correctMode = homeworkCommitInfoItem?.correctMode!!
                    bookCorrectBean.correctJson = homeworkCommitInfoItem?.correctJson
                    bookCorrectBean.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                    bookCorrectBean.answerUrl=homeworkCommitInfoItem?.answerUrl
                    bookCorrectBean.commitJson=Gson().toJson(homeworkCommitInfoItem)
                }
                HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(bookCorrectBean)
                DataUpdateManager.editDataUpdate(2,bookCorrectBean.id.toInt(),3,bookId,Gson().toJson(bookCorrectBean))
            }
            else{
                //保存本次题卷本批改详情
                bookCorrectBean = HomeworkBookCorrectBean()
                bookCorrectBean.homeworkTitle = homeworkCommitInfoItem?.title
                bookCorrectBean.bookId = bookId
                bookCorrectBean.startTime=System.currentTimeMillis()
                bookCorrectBean.page=page
                bookCorrectBean.state = 1
                bookCorrectBean.contendId=homeworkCommitInfoItem?.messageId!!
                if (homeworkCommitInfoItem?.isSelfCorrect==true){
                    bookCorrectBean.isSelfCorrect=true
                    bookCorrectBean.correctMode = homeworkCommitInfoItem?.correctMode!!
                    bookCorrectBean.correctJson = homeworkCommitInfoItem?.correctJson
                    bookCorrectBean.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                    bookCorrectBean.answerUrl=homeworkCommitInfoItem?.answerUrl
                    bookCorrectBean.commitJson=Gson().toJson(homeworkCommitInfoItem)
                }
                val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                val path=FileAddress().getPathHomeworkBookDrawPath(book?.bookDrawPath!!,page)
                //更新增量数据
                DataUpdateManager.createDataUpdate(2, id.toInt(),3,bookId ,Gson().toJson(bookCorrectBean),path)
            }
        }
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


    override fun onDestroy() {
        super.onDestroy()
        book?.pageIndex = page
        book?.pageUrl =  FileUtils.getIndexFile(book?.bookPath,page)?.path
        HomeworkBookDaoManager.getInstance().insertOrReplaceBook(book)
    }

}