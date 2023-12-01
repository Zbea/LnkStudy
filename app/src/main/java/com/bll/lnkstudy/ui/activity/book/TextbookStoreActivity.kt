package com.bll.lnkstudy.ui.activity.book

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.dialog.TextbookDetailsDialog
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.textbook.TextbookBean
import com.bll.lnkstudy.mvp.model.textbook.TextbookStore
import com.bll.lnkstudy.mvp.presenter.TextbookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TextbookStoreAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.locks.ReentrantLock

/**
 * 教材书城
 */
class TextbookStoreActivity : BaseAppCompatActivity(), IContractView.ITextbookStoreView {

    private var tabId = 0 //课本分类
    private var tabStr=""
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = TextbookStorePresenter(this,1)
    private var books = mutableListOf<TextbookBean>()
    private var mAdapter: TextbookStoreAdapter? = null
    private var gradeId =0
    private var selectGradeId=0
    private var semester=1
    private var provinceStr=""
    private var courseId=0//科目
    private var bookDetailsDialog: TextbookDetailsDialog? = null
    private var position=0

    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()

    override fun onTextbook(bookStore: TextbookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        if (tabId==0){
            for (book in books){
                book.typeStr=getString(R.string.textbook_tab_my)
            }
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
        pageSize=12
        typeList = DataBeanManager.teachingType.toMutableList()
        tabStr = typeList[0]

        semesterList=DataBeanManager.popupSemesters()
        getSemester()

        provinceStr= mUser?.schoolProvince.toString()
        for (i in DataBeanManager.provinces.indices){
            provinceList.add(PopupBean(i,DataBeanManager.provinces[i].value,DataBeanManager.provinces[i].value==provinceStr))
        }
        gradeId = mUser?.grade!!
        selectGradeId=gradeId
        gradeList=DataBeanManager.popupGrades(gradeId)
        subjectList=DataBeanManager.popupCourses

        if (subjectList.size>0){
            courseId=subjectList[0].id
        }
        initSelectorView()

        if (NetworkUtil(this).isNetworkConnected()){
            fetchData()
        }
        else{
            showNetworkDialog()
        }
    }

    override fun initView() {
        setPageTitle(R.string.main_teaching)
        disMissView(ll_search,tv_course,tv_grade,tv_semester)

        initRecyclerView()
        initTab()

        tv_download?.setOnClickListener {
            if (tabId == 0) {
                //获取本地课本是否有数据
                val localBooks = TextbookGreenDaoManager.getInstance().queryAllTextBook(typeList[0])
                if (localBooks.isNullOrEmpty()){
                    for (item in books) {
                        val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(item.bookId)
                        if (localBook!=null){
                            localBook.typeStr=tabStr
                            localBook.time = System.currentTimeMillis()
                            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(localBook)
                        }
                        else{
                            val downloadTask = downLoadStart(item.downloadUrl, item)
                            mDownMapPool[item.bookId] = downloadTask!!
                        }
                    }
                }
            }
        }

    }

    //设置tab分类
    private fun initTab() {
        for (i in typeList.indices) {
            rg_group.addView(getRadioButton(i, typeList[i], typeList.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    showView(tv_download)
                    disMissView(tv_course,tv_grade,tv_semester)
                    gradeId = mUser?.grade!!
                    getSemester()
                }
                else -> {
                    showView(tv_grade,tv_course,tv_semester)
                    disMissView(tv_download)
                    gradeId = selectGradeId
                }
            }
            tabId=i
            tabStr=typeList[i]
            pageIndex = 1
            fetchData()
        }

    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TextbookStoreAdapter(R.layout.item_bookstore, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this, 22f), 60))
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

        tv_grade.text =gradeList[gradeId-1].name
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

        tv_semester.text = semesterList[semester-1].name
        tv_semester.setOnClickListener {
            PopupList(this, semesterList, tv_semester, tv_semester.width, 5).builder()
                .setOnSelectListener { item ->
                    semester = item.id
                    tv_semester.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }

        tv_course.text = subjectList[0].name
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
        bookDetailsDialog = TextbookDetailsDialog(this, book)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook == null) {
                    val downloadTask = downLoadStart(book.downloadUrl, book)
                    mDownMapPool[book.bookId] = downloadTask!!
                } else {
                    book.loadSate = 2
                    showToast(R.string.toast_downloaded)
                    bookDetailsDialog?.setDissBtn()
                    mAdapter?.notifyDataSetChanged()
                }
            } else {
                val map = HashMap<String, Any>()
                map["type"] = if (tabId==3)2 else 1
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String, book: TextbookBean): BaseDownloadTask? {
        showLoading()
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val targetFile = File(zipPath)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val download = FileDownManager.with(this).create(url).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning && task == mDownMapPool[book.bookId]) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024),"0.0M") + "/" +
                             ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            bookDetailsDialog?.setUnClickBtn(s)
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    //删除缓存 poolmap
                    deleteDoneTask(task)
                    lock.lock()
                    val fileTargetPath = FileAddress().getPathTextBook(fileName)
                    unzip(book, zipPath, fileTargetPath)
                    //删除zip文件
                    FileUtils.deleteFile(File(zipPath))
                    lock.unlock()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast(book.bookName+getString(R.string.book_download_fail))
                    deleteDoneTask(task)
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
                if (tabId==3||tabId==4){
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
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(8,book.bookId,1,book.bookId
                        ,Gson().toJson(homeworkBookBean),book.downloadUrl)
                    book.loadSate=2
                }
                else{
                    book.apply {
                        typeStr = tabStr
                        loadSate = 2
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = fileTargetPath
                        bookDrawPath=FileAddress().getPathTextBookDraw(File(fileTargetPath).name)
                    }
                    //下载解压完成后更新存储的book
                    TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(1,book.bookId,1,book.bookId
                        ,Gson().toJson(book),book.downloadUrl)
                }
                //更新列表
                mAdapter?.notifyItemChanged(books.indexOf(book))
                bookDetailsDialog?.dismiss()
                Handler().postDelayed({
                    EventBus.getDefault().post(if (tabId==3||tabId==4)Constants.HOMEWORK_BOOK_EVENT else Constants.TEXT_BOOK_EVENT)
                    showToast(book.bookName+getString(R.string.book_download_success))
                },500)

                if (mDownMapPool.entries.size == 0) {
                    hideLoading()
                }
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
                if (mDownMapPool.entries.size == 0) {
                    hideLoading()
                }
                showToast(book.bookName+msg!!)
            }

            override fun onStart() {
            }

        })
    }

    /**
     * 下载完成 需要删除列表
     */
    private fun deleteDoneTask(task: BaseDownloadTask?) {
        if (mDownMapPool.isNotEmpty()) {
            //拿出map中的键值对
            val entries = mDownMapPool.entries

            val iterator = entries.iterator();
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<*, *>
                val entity = entry.value
                if (task == entity) {
                    iterator.remove()
                }
            }

        }
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
                map["area"] = provinceStr
                map["type"] = 1
                presenter.getHomeworkBooks(map)
            }
            4->{
                map["type"] = 2
                presenter.getHomeworkBooks(map)
            }
            else->{
                map["area"] = provinceStr
                map["type"] = if (tabId==1) 2 else 1
                presenter.getTextBooks(map)
            }
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }


}