package com.bll.lnkstudy.ui.activity.book

import android.content.Intent
import android.os.Bundle
import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogBookDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.calalog.CatalogChild
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParent
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.CorrectActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
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


class HomeworkBookDetailsActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var homeworkType: HomeworkTypeBean? = null
    private var messages= mutableListOf<ItemList>()
    private var homeworkCommitInfoItem: HomeworkCommitInfoItem?=null
    private val commitItems = mutableListOf<ItemList>()
    private var book: HomeworkBookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParent>()
    private var childItems = mutableListOf<CatalogChild>()
    private var startCount=0
    private var page = 0 //当前页码
    private var bookId=0
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
                    val map= HashMap<String, Any>()
                    if (homeworkType?.createStatus==2){
                        map["studentTaskId"]=homeworkCommitInfoItem?.messageId!!
                        map["page"]=ToolUtils.getImagesStr(homeworkCommitInfoItem?.contents!!)
                        map["studentUrl"]= ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkType?.typeId!!
                        map["takeTime"] = takeTime
                        mUploadPresenter.commit(map)
                    }
                    else{
                        map["pageStr"]=ToolUtils.getImagesStr(homeworkCommitInfoItem?.contents!!)
                        map["id"] = homeworkCommitInfoItem?.messageId!!
                        map["submitUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkType?.typeId!!
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
        onContent()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        initChangeScreenData()
        val bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkTypeBean
        bookId=homeworkType?.bookId!!
        when(homeworkType?.createStatus){
            2->{
                val list = homeworkType?.messages
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 3) {
                            messages.add(ItemList().apply {
                                id=item.studentTaskId
                                name=item.title
                                isSelfCorrect=item.selfBatchStatus==1
                            })
                        }
                    }
                }
            }
            1->{
                val list = homeworkType?.parents
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 1) {
                            messages.add(ItemList().apply {
                                id=item.id
                                name=item.title
                            })
                        }
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
                    val catalogParent = CatalogParent()
                    catalogParent.title = item.title
                    catalogParent.pageNumber = item.pageNumber
                    catalogParent.picName = item.picName
                    for (ite in item.subItems) {
                        val catalogChild = CatalogChild()
                        catalogChild.title = ite.title
                        catalogChild.pageNumber = ite.pageNumber
                        catalogChild.picName = ite.picName
                        catalogParent.addSubItem(catalogChild)
                        childItems.add(catalogChild)
                    }
                    parentItems.add(catalogParent)
                    catalogs.add(catalogParent)
                }
                pageCount =  catalogMsg?.totalCount!!
                startCount =  if (catalogMsg?.startCount!!-1<0)0 else catalogMsg?.startCount!!-1
            }
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        //云书库没有提交按钮
        if (homeworkType?.isCloud!!){
            disMissView(iv_btn)
        }

        iv_btn.setOnClickListener {
            if (messages.size==0)
                return@setOnClickListener
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
            if (correctBean!=null&&correctBean.state==1&&!correctBean.commitJson.isNullOrEmpty()){
                val item=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
                homeworkCommitInfoItem=Gson().fromJson(item?.commitJson, HomeworkCommitInfoItem::class.java)
                gotoSelfCorrect()
                return@setOnClickListener
            }
            if (NetworkUtil(this).isNetworkConnected()){
                commit()
            }
            else {
                showToast("网络连接失败")
            }
        }

        onContent()
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 1) {
                page -= 2
                onContent()
            } else {
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
        if (isExpand){
            if (page<pageCount-2){
                page+=2
                onContent()
            }
            else if (page==pageCount-2){
                page=pageCount-1
                onContent()
            }
        }
        else{
            if (page<pageCount-1){
                page+=1
                onContent()
            }
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
        if (page>=pageCount){
            page=pageCount-1
            return
        }
        if (page==0&&isExpand){
            page=1
        }

        loadPicture(page, elik_b!!, v_content_b!!)
        setPageCurrent(page,tv_page,tv_page_total)
        if (isExpand){
            val page_up=page-1//上一页页码
            loadPicture(page_up, elik_a!!, v_content_a!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
            else{
                setPageCurrent(page_up,tv_page,tv_page_total)
                setPageCurrent(page,tv_page_a,tv_page_total_a)
            }
        }

        if (HomeworkBookCorrectDaoManager.getInstance().isExist(bookId, page)){
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page)
            when(correctBean.state){
                0->{
                    disMissView(iv_score,ll_score)
                }
                1->{
                    disMissView(iv_score,ll_score)
                    if (!correctBean.commitJson.isNullOrEmpty()){
                        showToast("请及时自批改后提交")
                    }
                }
                2->{
                    setScoreDetails(correctBean)
                }
            }
        }
        else{
            disMissView(iv_score,ll_score)
        }
    }

    /**
     * 设置批改详情
     */
    private fun setScoreDetails(item:HomeworkBookCorrectBean){
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
        tv_correct_title.text=item.homeworkTitle
        tv_total_score.text=item.score.toString()
        if (item.correctJson?.isNotEmpty() == true&&correctMode>0){
            setScoreListDetails(item.correctJson)
        }
        else{
            disMissView(rv_list_multi,rv_list_score)
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
            GlideUtils.setImageCacheUrl(this, showFile.path, view, correctBean?.state ?: 0)
            val drawPath = book?.bookDrawPath+"/${index+1}.png"
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
        saveElik(elik_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface){
        if (File(elik.pwBitmapFilePath).exists()){
            DataUpdateManager.editDataUpdate(7,bookId,1)
        }
        else{
            //创建增量更新
            DataUpdateManager.createDataUpdateDrawing(7,bookId,1,book?.bookDrawPath!!)
        }
    }

    /**
     * 题卷提交
     */
    private fun commit() {
        DrawingCommitDialog(this,getCurrentScreenPos(),startCount,pageCount,messages).builder().setOnDialogClickListener {
            homeworkCommitInfoItem=it
            if (homeworkCommitInfoItem?.isSelfCorrect==true){
                for (item in homeworkType?.messages!!){
                    if(homeworkCommitInfoItem?.messageId==item.studentTaskId){
                        homeworkCommitInfoItem?.correctJson=item.question
                        homeworkCommitInfoItem?.correctMode=item.questionType
                        homeworkCommitInfoItem?.scoreMode=item.questionMode
                        homeworkCommitInfoItem?.answerUrl=item.answerUrl
                        homeworkCommitInfoItem?.typeId=homeworkType?.typeId
                        homeworkCommitInfoItem?.typeName=homeworkType?.name
                        homeworkCommitInfoItem?.course=homeworkType?.course
                        homeworkCommitInfoItem?.state=homeworkType?.state
                        homeworkCommitInfoItem?.bookId=homeworkType?.bookId
                    }
                }
            }
            showLoading()
            commitItems.clear()
            for (index in homeworkCommitInfoItem?.contents!!){
                val imageFile=FileUtils.getIndexFile(book?.bookPath,index)
                val path=imageFile?.path.toString()
                val drawPath = book?.bookDrawPath+"/${index+1}.png"
                homeworkCommitInfoItem?.paths?.add(path)
                Thread{
                    BitmapUtils.mergeBitmap(path,drawPath)
                    commitItems.add(ItemList().apply {
                        id = index
                        url = path
                    })
                    if (commitItems.size==homeworkCommitInfoItem?.contents!!.size){
                        for (page in homeworkCommitInfoItem?.contents!!){
                            //删除手写
                            FileUtils.deleteFile(File(book?.bookDrawPath+"/${page+1}.png"))
                            //保存本次题卷本批改详情
                            val bookCorrectBean = HomeworkBookCorrectBean()
                            bookCorrectBean.homeworkTitle = homeworkCommitInfoItem?.title
                            bookCorrectBean.bookId = bookId
                            bookCorrectBean.page=page
                            bookCorrectBean.startTime=System.currentTimeMillis()
                            bookCorrectBean.state = 1
                            if (homeworkCommitInfoItem?.isSelfCorrect == true){
                                bookCorrectBean.isSelfCorrect=true
                                bookCorrectBean.correctMode = homeworkCommitInfoItem?.correctMode!!
                                bookCorrectBean.correctJson = homeworkCommitInfoItem?.correctJson
                                bookCorrectBean.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                                bookCorrectBean.answerUrl=homeworkCommitInfoItem?.answerUrl
                                bookCorrectBean.commitJson=Gson().toJson(homeworkCommitInfoItem)
                            }
                            val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                            //更新增量数据
                            DataUpdateManager.createDataUpdate(7, id.toInt(),2,bookId ,Gson().toJson(bookCorrectBean),"")
                        }
                        if (homeworkCommitInfoItem?.isSelfCorrect == true){
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
            messages.removeIf{item->item.id==homeworkCommitInfoItem?.messageId}
            onContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.pageIndex = page
        book?.pageUrl =  FileUtils.getIndexFile(book?.bookPath,page)?.path
        HomeworkBookDaoManager.getInstance().insertOrReplaceBook(book)
    }

}