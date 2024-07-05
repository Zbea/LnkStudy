package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.calalog.CatalogChild
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParent
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommit
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_correct_score.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class HomeworkBookDetailsActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var homeworkType: HomeworkTypeBean? = null
    private var messages= mutableListOf<ItemList>()
    private var homeworkCommit: HomeworkCommit?=null
    private val commitItems = mutableListOf<ItemList>()
    private var book: HomeworkBookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParent>()
    private var childItems = mutableListOf<CatalogChild>()
    private var pageStart=1
    private var page = 0 //当前页码
    private var bookId=0

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
                    if (homeworkType?.createStatus==1){
                        map["studentTaskId"]=homeworkCommit?.messageId!!
                        map["page"]=ToolUtils.getImagesStr(homeworkCommit?.contents!!)
                        map["studentUrl"]= ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkType?.typeId!!
                        mUploadPresenter.commit(map)
                    }
                    else{
                        map["pageStr"]=ToolUtils.getImagesStr(homeworkCommit?.contents!!)
                        map["id"] = homeworkCommit?.messageId!!
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

    override fun onSuccess(urls: MutableList<String>?) {
    }
    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        messages.removeAt(homeworkCommit?.index!!)
        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            type=1
            studentTaskId=homeworkCommit?.messageId!!
            content=homeworkCommit?.title
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

        onContent()

        iv_btn.setOnClickListener {
            if (messages.size==0)
                return@setOnClickListener
            if (NetworkUtil(this).isNetworkConnected()){
                commit()
            }
            else{
                showNetworkDialog()
            }
        }

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
        DrawingCatalogDialog(this,screenPos, getCurrentScreenPos(),catalogs, 1, pageStart).builder().setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
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
        setScoreDetails(page)
        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
    }

    /**
     * 设置批改详情
     */
    private fun setScoreDetails(page:Int){
        if (HomeworkBookCorrectDaoManager.getInstance().isExistCorrect(bookId,page.toString())){
            if (!ll_score.isVisible)
                showView(iv_score)
            val item=HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(bookId,page.toString())
            correctMode=item.correctMode
            tv_correct_title.text=item.homeworkTitle
            tv_total_score.text=item.score
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
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = getIndexFile(index)
        if (showFile != null) {
            GlideUtils.setImageFileNoCache(this, showFile, view)
            val drawPath = book?.bookDrawPath+"/${index+1}/draw.tch"
            elik.setLoadFilePath(drawPath, true)
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
        DrawingCommitDialog(this,getCurrentScreenPos(),messages).builder().setOnDialogClickListener {
            homeworkCommit=it
            showLoading()
            commitItems.clear()
            for (index in homeworkCommit?.contents!!){
                if (index>pageCount)
                {
                    showToast(R.string.toast_page_inexistence)
                    return@setOnDialogClickListener
                }
                //查找页码需要加上开始页面的初始下标
                val imageFile=getIndexFile(index)
                val path=imageFile?.path.toString()
                val drawPath = book?.bookDrawPath+"/${index+1}/draw.png"
                Thread{
                    BitmapUtils.mergeBitmap(path,drawPath)
                    FileUtils.deleteFile(File(drawPath).parentFile)
                    commitItems.add(ItemList().apply {
                        id = index
                        url = path
                    })
                    if (commitItems.size==homeworkCommit?.contents!!.size){
                        commitItems.sort()
                        mUploadPresenter.getToken()
                    }
                }.start()
            }
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
        closeNetwork()
        super.onDestroy()
    }

    override fun onNetworkConnectionSuccess() {
        commit()
    }

}