package com.bll.lnkstudy.ui.activity.book

import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DownloadTextbookDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.textbook.TextbookBean
import com.bll.lnkstudy.mvp.model.textbook.TextbookStore
import com.bll.lnkstudy.mvp.presenter.TextbookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TextbookStoreAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.rv_list
import kotlinx.android.synthetic.main.ac_bookstore.tv_download
import kotlinx.android.synthetic.main.common_title.tv_course
import kotlinx.android.synthetic.main.common_title.tv_grade
import kotlinx.android.synthetic.main.common_title.tv_province
import kotlinx.android.synthetic.main.common_title.tv_semester
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.CountDownLatch

/**
 * 教材书城
 */
class TextbookStoreActivity : BaseAppCompatActivity(), IContractView.ITextbookStoreView {

    private var tabId = 0 //课本分类
    private var tabStr=""
    private lateinit var presenter :TextbookStorePresenter
    private var books = mutableListOf<TextbookBean>()
    private var mAdapter: TextbookStoreAdapter? = null
    private var gradeId =0
    private var selectGradeId=0
    private var semester=1
    private var provinceStr=""
    private var courseId=0//科目
    private var bookDetailsDialog: DownloadTextbookDialog? = null
    private var position=0
    private var countDownTasks: CountDownLatch? = null //异步完成后操作
    private var subjectList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()

    override fun onTextbook(bookStore: TextbookStore) {
        setPageNumber(bookStore.total)
        books.clear()
        if (tabId==0){
            for (book in bookStore.list){
                book.typeStr=getString(R.string.textbook_tab_my)
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook==null||localBook.typeStr!=getString(R.string.textbook_tab_my)){
                    books.add(book)
                }
            }
        }
        else{
            books=bookStore.list
        }
        mAdapter?.setNewData(books)
    }

    override fun buySuccess() {
        books[position].buyStatus = 1
        bookDetailsDialog?.setChangeStatus()
        mAdapter?.notifyItemChanged(position)
    }

    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        typeList = DataBeanManager.teachingType.toMutableList()
        tabStr = typeList[0]

        getSemester()

        provinceStr= mUser?.schoolProvince.toString()
        for (i in DataBeanManager.provinces.indices){
            provinceList.add(PopupBean(i,DataBeanManager.provinces[i].value,DataBeanManager.provinces[i].value==provinceStr))
        }
        gradeId = mUser?.grade!!
        onCommonData()
        if (NetworkUtil(this).isNetworkConnected()){
            fetchData()
        }
    }

    override fun initChangeScreenData() {
        presenter = TextbookStorePresenter(this,getCurrentScreenPos())
    }

    override fun onCommonData() {
        selectGradeId=gradeId
        gradeList=DataBeanManager.popupGradeThans(gradeId)
        subjectList=DataBeanManager.popupCourses
        if (subjectList.size>0){
            courseId=subjectList[0].id
            initSelectorView()
        }
    }

    override fun initView() {
        setPageTitle(R.string.main_teaching)
        disMissView(tv_course,tv_grade,tv_semester)

        initRecyclerView()
        initTab()

        tv_download?.setOnClickListener {
            if (tabId == 0) {
                //需要下载课本
                val downloadBooks= mutableListOf<TextbookBean>()
                for (book in books) {
                    val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                    if (localBook!=null){
                        //预习课本转移到我的课本中
                        if (localBook.typeStr!=tabStr){
                            localBook.typeStr=tabStr
                            localBook.time = System.currentTimeMillis()
                            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(localBook)
                        }
                    }
                    else{
                        downloadBooks.add(book)
                    }
                }

                if (downloadBooks.size==0){
                    showToast("已下载")
                    return@setOnClickListener
                }

                startBooks(downloadBooks)
            }
        }

    }

    private fun initTab(){
        for (i in typeList.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=typeList[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when (position) {
            0 -> {
                showView(tv_download)
                disMissView(tv_course,tv_grade,tv_semester)
                gradeId = mUser?.grade!!
                getSemester()
                tv_semester.text = DataBeanManager.popupSemesters()[semester-1].name
            }
            else -> {
                showView(tv_grade,tv_course,tv_semester)
                disMissView(tv_download)
                gradeId = selectGradeId
            }
        }
        tabId=position
        tabStr=typeList[position]
        pageIndex = 1
        fetchData()
    }


    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TextbookStoreAdapter(R.layout.item_bookstore, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            showBookDetails(books[position])
        }
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {

        tv_province.text = provinceStr
        tv_province.setOnClickListener {
            PopupList(this, provinceList, tv_province, tv_province.width, 5).builder()
                .setOnSelectListener { item ->
                    provinceStr = item.name
                    tv_province.text = provinceStr
                    pageIndex = 1
                    fetchData()
                }
        }

        tv_grade.text =DataBeanManager.getGradeStr(gradeId)
        tv_grade.setOnClickListener {
            PopupList(this, gradeList, tv_grade, tv_grade.width, 5).builder()
            .setOnSelectListener { item ->
                selectGradeId = item.id
                gradeId=selectGradeId
                tv_grade.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        tv_semester.text = DataBeanManager.popupSemesters()[semester-1].name
        tv_semester.setOnClickListener {
            PopupList(this, DataBeanManager.popupSemesters(semester), tv_semester, tv_semester.width, 5).builder()
                .setOnSelectListener { item ->
                    semester = item.id
                    tv_semester.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }

        tv_course.text = DataBeanManager.getCourseStr(courseId)
        tv_course.setOnClickListener {
            PopupList(this, subjectList, tv_course, tv_course.width, 5).builder()
                .setOnSelectListener { item ->
                    courseId = item.id
                    tv_course.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }
    }

    /**
     * 设置课本学期（月份为9月份之前为下学期）
     */
    private fun getSemester(){
        semester=if (DateUtils.getMonth()<9) 2 else 1
    }

    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: TextbookBean) {
        bookDetailsDialog = DownloadTextbookDialog(this, book)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook == null) {
                    startBooks(mutableListOf(book))
                } else {
                    book.loadSate = 2
                    showToast(R.string.toast_downloaded)
                    bookDetailsDialog?.setDissBtn()
                }
            } else {
                val map = HashMap<String, Any>()
                map["type"] = if (tabId==3)1 else 2
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    /**
     * 开始下载书籍
     */
    private fun startBooks(books:MutableList<TextbookBean>){
        countDownTasks = CountDownLatch(books.size)
        showLoading()
        for (book in books){
            Handler().postDelayed({
                downLoadStart(book.downloadUrl, book)
            },500)
        }
        //等待两个请求完成后刷新列表
        Thread {
            countDownTasks?.await()
            runOnUiThread {
                hideLoading()
                if (tabId>2){
                    EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
                }
                else{
                    EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
                }
                //刷新列表
                if (tabId==0)
                    fetchData()
            }
            countDownTasks = null
        }.start()
    }

    //下载book
    private fun downLoadStart(url: String, book: TextbookBean): BaseDownloadTask? {
        val fileName = book.bookId.toString()//文件名
        val path = if (tabId>2){
            FileAddress().getPathZip(fileName)
        } else{
            FileAddress().getPathTextBook(fileName+MethodManager.getUrlFormat(book.downloadUrl))
        }
        val download = FileBigDownManager.with(this).create(url).setPath(path)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024),"0.0M") + "/" +
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            bookDetailsDialog?.setUnClickBtn(s)
                        }
                    }
                }
                override fun completed(task: BaseDownloadTask?) {
                    if (tabId>2){
                        val fileTargetPath = FileAddress().getPathHomeworkBook(fileName)
                        unzip(book, path, fileTargetPath)
                    }
                    else{
                        book.apply {
                            typeStr = tabStr
                            loadSate = 2
                            time = System.currentTimeMillis()//下载时间用于排序
                            bookPath = path
                            bookDrawPath=FileAddress().getPathTextBookDraw(fileName)
                        }
                        //下载解压完成后更新存储的book
                        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                        //创建增量更新
                        DataUpdateManager.createDataUpdateSource(1,book.bookId,1,Gson().toJson(book),book.downloadUrl)
                    }
                    refreshView(book)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    showToast(book.bookName+getString(R.string.book_download_fail))
                    countDownTasks?.countDown()
                }
            })
        return download
    }

    /**
     * 解压
     */
    private fun unzip(book: TextbookBean, zipPath: String, fileTargetPath: String) {
        ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
                //题卷本不存在，创建题卷本
                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkTypeBook(book.bookId)){
                    val homeworkTypeBean= HomeworkTypeBean().apply {
                        name=book.bookName
                        grade=book.grade
                        typeId=ToolUtils.getDateId()
                        state=4
                        date=System.currentTimeMillis()
                        course=DataBeanManager.getCourseStr(book.subject)
                        bookId=book.bookId
                        bgResId=book.imageUrl
                        createStatus=0
                    }
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                    //创建增量数据
                    DataUpdateManager.createDataUpdate(2, homeworkTypeBean.typeId, 1,  Gson().toJson(homeworkTypeBean))
                }
                val homeworkBookBean= HomeworkBookBean().apply {
                    bookId=book.bookId
                    imageUrl=book.imageUrl
                    bookName=book.bookName
                    bookDesc=book.bookDesc
                    price=book.price
                    type=book.type
                    semester=book.semester
                    area=book.area
                    grade=book.grade
                    subject=book.subject
                    supply=book.supply
                    downloadUrl=book.downloadUrl
                    bookPath=fileTargetPath
                    bookDrawPath=FileAddress().getPathHomeworkBookDraw(File(fileTargetPath).name)
                    time=System.currentTimeMillis()
                }
                HomeworkBookDaoManager.getInstance().insertOrReplaceBook(homeworkBookBean)
                book.loadSate=2
                //删除zip文件
                FileUtils.deleteFile(File(zipPath))
                refreshView(book)
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
                showToast(book.bookName+msg!!)
                countDownTasks?.countDown()
            }

            override fun onStart() {
            }

        })
    }

    //更新列表
    private fun refreshView(book: TextbookBean){
        bookDetailsDialog?.dismiss()
        showToast(book.bookName+getString(R.string.book_download_success))
        countDownTasks?.countDown()
    }


    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        books.clear()
        mAdapter?.notifyDataSetChanged()
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["grade"] = gradeId
        map["semester"]=semester
        if (tabId!=0)
            map["subjectName"]=courseId
        when(tabId){
            3->{
                map["type"] = 2
                presenter.getHomeworkBooks(map)
            }
            1->{
                map["type"] = 2
                presenter.getTextBooks(map)
            }
            else->{
                map["area"] = provinceStr
                map["type"] = 1
                presenter.getTextBooks(map)
            }
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}