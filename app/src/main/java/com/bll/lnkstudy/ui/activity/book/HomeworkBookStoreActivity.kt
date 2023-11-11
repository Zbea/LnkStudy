package com.bll.lnkstudy.ui.activity.book

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookDetailsDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.book.BookStore
import com.bll.lnkstudy.mvp.model.book.BookStoreType
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
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
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

/**
 * 题卷本书城
 */
class HomeworkBookStoreActivity : BaseAppCompatActivity(), IContractView.IBookStoreView {

    private var tabId = 1
    private var tabStr=""
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<BookBean>()
    private var mAdapter: BookStoreAdapter? = null
    private var gradeId =0
    private var semester=1
    private var provinceStr=""
    private var courseId=0//科目
    private var course=""
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var position=0

    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var tabList = mutableListOf<String>()
    private var bookVersion = mutableListOf<ItemList>()

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        for (book in books){
            book.version=bookVersion[book.bookVersion-1].desc
        }
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        bookVersion=bookStoreType.bookVersion
        if (subjectList.size>0){
            courseId=subjectList[0].id
            course=subjectList[0].name
            initSelectorView()
            fetchData()
        }
    }

    override fun buyBookSuccess() {
        books[position].buyStatus = 1
        bookDetailsDialog?.setChangeStatus()
        mAdapter?.notifyItemChanged(position)
    }


    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        pageSize=12
        tabList = DataBeanManager.homeworkBookType.toMutableList()
        tabStr = tabList[0]

        semesterList=DataBeanManager.semesters

        provinceStr= mUser?.schoolProvince.toString()
        for (i in DataBeanManager.provinces.indices){
            provinceList.add(PopupBean(i,DataBeanManager.provinces[i].value,DataBeanManager.provinces[i].value==provinceStr))
        }
        gradeId = mUser?.grade!!
        gradeList=DataBeanManager.popupGrades(gradeId)

        for (classGroup in DataBeanManager.classGroups()){
            for (item in DataBeanManager.popupCourses){
                if (classGroup.subject==item.name){
                    subjectList.add(item)
                }
            }
        }

        presenter.getBookType()
    }

    override fun initView() {
        setPageTitle(R.string.homework_book)
        showView(tv_course,tv_grade,tv_semester,tv_province)
        disMissView(ll_search,tv_download)

        initRecyclerView()
        initTab()

    }

    //设置tab分类
    private fun initTab() {
        for (i in tabList.indices) {
            rg_group.addView(getRadioButton(i, tabList[i], tabList.size - 1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    tabId = 1
                    showView(tv_province)
                    gradeId = mUser?.grade!!
                }
                else -> {
                    tabId=2
                    showView(tv_province)
                }
            }
            tabStr=tabList[i]
            pageIndex = 1
            fetchData()
        }

    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, null)
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
                gradeId = item.id
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
                    course=item.name
                    tv_course.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }
    }


    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: BookBean) {
        bookDetailsDialog = BookDetailsDialog(this, book)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                if (!HomeworkBookDaoManager.getInstance().isExist(book.bookId)) {
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
                map["type"] = 2
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String, book: BookBean): BaseDownloadTask? {
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
                            val s = getFormatNum(
                                soFarBytes.toDouble() / (1024 * 1024),
                                "0.0"
                            ) + "M/" + getFormatNum(
                                totalBytes.toDouble() / (1024 * 1024),
                                "0.0"
                            ) + "M"
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
                    val fileTargetPath = FileAddress().getPathHomeworkBook(fileName)
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
    private fun unzip(book: BookBean, zipPath: String, fileTargetPath: String) {
        ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
                //题卷本不存在，创建题卷本
                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkTypeBook(book.bookId)){
                    val homeworkTypeBean=HomeworkTypeBean().apply {
                        name=book.bookName
                        grade=book.grade
                        typeId=ToolUtils.getDateId()
                        state=4
                        date=System.currentTimeMillis()
                        course=this@HomeworkBookStoreActivity.course
                        bookId=book.bookId
                        bgResId=book.imageUrl
                        createStatus=0
                    }
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                }

                val homeworkBookBean=HomeworkBookBean().apply {
                    bookId=book.bookId
                    imageUrl=book.imageUrl
                    bookName=book.bookName
                    bookDesc=book.bookDesc
                    price=book.price
                    subtypeStr=tabStr
                    semester=book.semester
                    area=book.area
                    grade=book.grade
                    subject=book.subjectName
                    supply=book.supply
                    bodyUrl=book.downloadUrl
                    bookPath=fileTargetPath
                    bookDrawPath=FileAddress().getPathHomeworkBookDraw(File(fileTargetPath).name)
                    downDate=System.currentTimeMillis()
                }
                HomeworkBookDaoManager.getInstance().insertOrReplaceBook(homeworkBookBean)
                //创建增量更新
                DataUpdateManager.createDataUpdateSource(8,book.bookId,1,book.bookId
                    ,Gson().toJson(homeworkBookBean),book.downloadUrl)

                book.loadSate=2
                //更新列表
                mAdapter?.notifyDataSetChanged()
                bookDetailsDialog?.dismiss()

                Handler().postDelayed({
                    EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
                    showToast(book.bookName+getString(R.string.book_download_success))
                },500)

                hideLoading()
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
                hideLoading()
                showToast(book.bookName+msg!!)
            }

            override fun onStart() {
            }

        })
    }

    fun getFormatNum(pi: Double, format: String?): String? {
        val df = DecimalFormat(format)
        return df.format(pi)
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
        NetworkUtil(this).toggleNetwork(false)
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        if (NetworkUtil(this).isNetworkConnected()){
            books.clear()
            mAdapter?.notifyDataSetChanged()
            val map = HashMap<String, Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            if (tabId==1)
                map["area"] = provinceStr
            map["grade"] = gradeId
            map["type"] = tabId
            map["semester"]=semester
            map["subjectName"]=courseId
            presenter.getHomeworkBooks(map)
        }
        else{
            showNetworkDialog()
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}