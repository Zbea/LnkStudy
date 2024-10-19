package com.bll.lnkstudy.ui.activity.drawing

import android.content.Intent
import android.os.Bundle
import android.view.EinkPWInterface
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.calalog.CatalogChild
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParent
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
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
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Collections


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
    private var pageStart=1
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
                    if (homeworkType?.createStatus==1){
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
        deleteCommitDraw()
        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            content=homeworkCommitInfoItem?.title
            homeworkTypeStr=homeworkType?.name
            course=homeworkType?.course
            time=System.currentTimeMillis()
        })
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
            1->{
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
            2->{
                val list = homeworkType?.parents
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 1) {
                            messages.add(ItemList().apply {
                                id=item.id
                                name=item.content
                            })
                        }
                    }
                }
            }
        }

        book = HomeworkBookDaoManager.getInstance().queryBookByID(bookId)
        if (book == null) return
        page = book?.pageIndex!!
        val catalogFilePath = FileAddress().getPathTextbookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(catalogFilePath))
        {
            val catalogMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(catalogFilePath)))
            try {
                catalogMsg = Gson().fromJson(catalogMsgStr, CatalogMsg::class.java)
            } catch (e: Exception) {
            }
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
            }
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        pageCount = if (catalogMsg==null)0 else catalogMsg?.totalCount!!
        pageStart = if (catalogMsg==null)0 else catalogMsg?.startCount!!

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
        CatalogDialog(this,screenPos, getCurrentScreenPos(),catalogs, 1, pageStart).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=position-1){
                    page = position - 1
                    onContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
            }
        })
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

        tv_page_total.text="${pageCount-pageStart}"
        tv_page_total_a.text="${pageCount-pageStart}"

        loadPicture(page, elik_b!!, v_content_b!!)
        tv_page.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
        if (isExpand){
            loadPicture(page-1, elik_a!!, v_content_a!!)
            if (screenPos== Constants.SCREEN_LEFT){
                tv_page.text = if (page-(pageStart-1)>0) "${page-(pageStart-1)}" else ""
                tv_page_a.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
            }
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text = if (page-(pageStart-1)>0) "${page-(pageStart-1)}" else ""
                tv_page.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
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
        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
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

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = getIndexFile(index)
        if (showFile != null) {
            val correctBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
            if (correctBean==null){
                GlideUtils.setImageUrl(this, showFile.path, view)
            }
            else{
                GlideUtils.setImageUrl(this, showFile.path, view,correctBean.state)
            }

            if (isCommitState(index)){
                elik.setPWEnabled(false)
            }
            else{
                val drawPath = book?.bookDrawPath+"/${index+1}.png"
                elik.setLoadFilePath(drawPath, true)
                elik.setPWEnabled(true)
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

    override fun onElikStart_a() {
        if (!HomeworkBookCorrectDaoManager.getInstance().isExist(bookId, page-1)){
            //保存本次题卷本批改详情
            val bookCorrectBean = HomeworkBookCorrectBean()
            bookCorrectBean.bookId = bookId
            bookCorrectBean.page = page-1
            bookCorrectBean.startTime=System.currentTimeMillis()
            val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
            //更新增量数据
            DataUpdateManager.createDataUpdate(7, id.toInt(),2,bookCorrectBean.bookId ,Gson().toJson(bookCorrectBean),"")
        }
    }

    override fun onElikStart_b() {
        if (!HomeworkBookCorrectDaoManager.getInstance().isExist(bookId, page)){
            //保存本次题卷本批改详情
            val bookCorrectBean = HomeworkBookCorrectBean()
            bookCorrectBean.bookId = bookId
            bookCorrectBean.page = page
            bookCorrectBean.startTime=System.currentTimeMillis()
            val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
            //更新增量数据
            DataUpdateManager.createDataUpdate(7, id.toInt(),2,bookCorrectBean.bookId ,Gson().toJson(bookCorrectBean),"")
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
        elik.saveBitmap(true) {}
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
        DrawingCommitDialog(this,getCurrentScreenPos(),pageStart-1,pageCount,messages).builder().setOnDialogClickListener {
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
                //查找页码需要加上开始页面的初始下标
                val imageFile=getIndexFile(index)
                val path=imageFile?.path.toString()
                val drawPath = book?.bookDrawPath+"/${index+1}.png"
                homeworkCommitInfoItem?.paths?.add(path)
                Thread{
                    BitmapUtils.mergeBitmap(path,drawPath)
                    commitItems.add(ItemList().apply {
                        id = index
                        url = path
                    })
                    FileUtils.deleteFile(File(drawPath))
                    if (commitItems.size==homeworkCommitInfoItem?.contents!!.size){
                        takeTime = if (getStartTime(homeworkCommitInfoItem?.contents!!)==0L){
                            0
                        } else{
                            System.currentTimeMillis()-getStartTime(homeworkCommitInfoItem?.contents!!)
                        }
                        if (homeworkCommitInfoItem?.isSelfCorrect == true){
                            hideLoading()
                            for (page in homeworkCommitInfoItem?.contents!!){
                                if (HomeworkBookCorrectDaoManager.getInstance().isExist(bookId,page)){
                                    val bookCorrectBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
                                    bookCorrectBean.homeworkTitle=homeworkCommitInfoItem?.title
                                    bookCorrectBean.state = 1
                                    bookCorrectBean.isSelfCorrect=true
                                    bookCorrectBean.correctMode = homeworkCommitInfoItem?.correctMode!!
                                    bookCorrectBean.correctJson = homeworkCommitInfoItem?.correctJson
                                    bookCorrectBean.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                                    bookCorrectBean.answerUrl=homeworkCommitInfoItem?.answerUrl
                                    bookCorrectBean.commitJson=Gson().toJson(homeworkCommitInfoItem)
                                    HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(bookCorrectBean)
                                    DataUpdateManager.editDataUpdate(7,bookCorrectBean.id.toInt(),2,bookId,Gson().toJson(bookCorrectBean))
                                }
                                else{
                                    //保存本次题卷本批改详情
                                    val bookCorrectBean = HomeworkBookCorrectBean()
                                    bookCorrectBean.homeworkTitle = homeworkCommitInfoItem?.title
                                    bookCorrectBean.bookId = bookId
                                    bookCorrectBean.startTime=System.currentTimeMillis()
                                    bookCorrectBean.page=page
                                    bookCorrectBean.state = 1
                                    bookCorrectBean.isSelfCorrect=true
                                    bookCorrectBean.correctMode = homeworkCommitInfoItem?.correctMode!!
                                    bookCorrectBean.correctJson = homeworkCommitInfoItem?.correctJson
                                    bookCorrectBean.scoreMode=homeworkCommitInfoItem?.scoreMode!!
                                    bookCorrectBean.answerUrl=homeworkCommitInfoItem?.answerUrl
                                    bookCorrectBean.commitJson=Gson().toJson(homeworkCommitInfoItem)
                                    val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                                    //更新增量数据
                                    DataUpdateManager.createDataUpdate(7, id.toInt(),2,bookId ,Gson().toJson(bookCorrectBean),"")
                                }
                            }
                            deleteCommitDraw()
                            gotoSelfCorrect()
                        }
                        else{
                            for (page in homeworkCommitInfoItem?.contents!!){
                                if (HomeworkBookCorrectDaoManager.getInstance().isExist(bookId,page)){
                                    val bookCorrectBean=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
                                    bookCorrectBean.homeworkTitle=homeworkCommitInfoItem?.title
                                    bookCorrectBean.state = 1
                                    HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(bookCorrectBean)
                                    DataUpdateManager.editDataUpdate(7,bookCorrectBean.id.toInt(),2,bookId,Gson().toJson(bookCorrectBean))
                                }
                                else{
                                    //保存本次题卷本批改详情
                                    val bookCorrectBean = HomeworkBookCorrectBean()
                                    bookCorrectBean.homeworkTitle = homeworkCommitInfoItem?.title
                                    bookCorrectBean.bookId = bookId
                                    bookCorrectBean.page=page
                                    bookCorrectBean.startTime=System.currentTimeMillis()
                                    bookCorrectBean.state = 1
                                    val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                                    //更新增量数据
                                    DataUpdateManager.createDataUpdate(7, id.toInt(),2,bookId ,Gson().toJson(bookCorrectBean),"")
                                }
                            }
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
    private fun getStartTime(pages: List<Int>): Long {
        val times = mutableListOf<Long>()
        for (page in pages) {
            if (HomeworkBookCorrectDaoManager.getInstance().isExist(bookId, page)) {
                val correctBean = HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId, page)
                if (correctBean.startTime > 0) {
                    times.add(correctBean.startTime)
                }
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
        val intent = Intent(this, CorrectActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("homeworkCommit", homeworkCommitInfoItem)
        intent.putExtra("bundle", bundle)
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
        intent.putExtra("android.intent.extra.KEEP_FOCUS", true)
        activityResultLauncher.launch(intent)
    }

    /**
     * 删除手写文件
     */
    private fun deleteCommitDraw(){
        //删除手写
        for (index in homeworkCommitInfoItem?.contents!!){
            val drawPath = book?.bookDrawPath+"/${index+1}.png"
            FileUtils.deleteFile(File(drawPath))
        }
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
            onContent()
        }
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File? {
        val path = FileAddress().getPathTextbookPicture(book?.bookPath!!)
        val listFiles = FileUtils.getAscFiles(path)
        return if (listFiles.size>index) listFiles[index] else null
    }

    override fun onDestroy() {
        book?.pageIndex = page
        HomeworkBookDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
        super.onDestroy()
    }

}