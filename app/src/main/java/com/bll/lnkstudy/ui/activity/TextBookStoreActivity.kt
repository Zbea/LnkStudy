package com.bll.lnkstudy.ui.activity

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookDetailsDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

/**
 * 教材书城
 */
class TextBookStoreActivity : BaseAppCompatActivity(),
    IContractView.IBookStoreView {

    private var typeId = 0 //教材分类
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<BookBean>()
    private var mAdapter: BookStoreAdapter? = null
    private var provinceStr = ""
    private var gradeStr = ""
    private var typeStr = ""
    private var semesterStr=""
    private var courseId=0//科目
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var mBook: BookBean? = null

    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        //年级分类
        for (i in bookStoreType.grade.indices) {
            gradeList.add(PopupBean(i, bookStoreType.grade[i], i == 0))
        }
        gradeStr = gradeList[mUser?.grade!!-1].name

        for (i in bookStoreType.subjectList.indices) {
            val item=bookStoreType.subjectList[i]
            subjectList.add(PopupBean(item.type, item.desc, i == 0))
        }
        courseId = subjectList[0].id

        initSelectorView()
        getDataBook()
    }

    override fun buyBookSuccess() {
        mBook?.buyStatus = 1
        bookDetailsDialog?.setChangeStatus()
        mAdapter?.notifyDataSetChanged()
    }


    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        pageSize=12
        //获取地区分类
        val citysStr = FileUtils.readFileContent(resources.assets.open("city.json"))
        val area = Gson().fromJson(citysStr, Area::class.java)
        for (i in area.provinces.indices) {
            provinceList.add(PopupBean(i, area.provinces[i].provinceName, i == 0))
        }
        provinceStr = provinceList[0].name
        typeList = DataBeanManager.textbookType.toMutableList()
        typeList.removeAt(3)
        typeStr = typeList[0]
        semesterList=DataBeanManager.semesters
        semesterStr= semesterList[0].name

        getData()
    }

    override fun initView() {
        setPageTitle(R.string.main_teaching)
        disMissView(ll_search,tv_course)
        showView(tv_province,tv_grade,tv_semester)

        initRecyclerView()
        initTab()

        tv_download?.setOnClickListener {

            val tasks = mutableListOf<BaseDownloadTask>()
            if (typeId == 0) {
                for (item in books) {
                    val localBook =
                        BookGreenDaoManager.getInstance().queryBookByBookID(item.bookId)
                    if (localBook == null) {
                        val downloadTask = downLoadStart(item.downloadUrl, item)
                        tasks.add(downloadTask!!)
                        mDownMapPool[item.bookId] = downloadTask
                    }
                }
            }
        }

    }

    //获取数据
    private fun getData() {
        presenter.getBookType()
    }

    //获取教材
    private fun getDataBook() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = 12
        map["area"] = provinceStr
        map["grade"] = gradeStr
        map["type"] = typeStr
        map["semester"]=semesterStr
        presenter.getTextBooks(map)
    }

    //获取参考
    private fun getDataBookCk() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["grade"] = gradeStr
        map["semester"]=semesterStr
        map["subjectName"]=courseId
        presenter.getTextBookCks(map)
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {

        tv_grade.text = gradeStr
        tv_grade.setOnClickListener {
            //我的课本时候不能筛选其他年级的书籍
            if (typeId==0)return@setOnClickListener
            PopupList(this, gradeList, tv_grade, tv_grade.width, 5).builder()
            .setOnSelectListener { item ->
                gradeStr = item.name
                tv_grade.text = gradeStr
                pageIndex = 1
                fetchData()
            }
        }

        tv_province.text = provinceStr
        tv_province.setOnClickListener {
            PopupList(this, provinceList, tv_province, tv_province.width, 5).builder()
            .setOnSelectListener { item ->
                provinceStr = item.name
                tv_province.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        tv_semester.text = semesterStr
        tv_semester.setOnClickListener {
            PopupList(this, semesterList, tv_semester, tv_semester.width, 5).builder()
                .setOnSelectListener { item ->
                    semesterStr = item.name
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


    //设置tab分类
    private fun initTab() {
        for (i in typeList.indices) {
            rg_group.addView(getRadioButton(i, typeList[i], typeList.size - 1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    //本地我的课本是否有数据，没有显示下载按钮
                    val textbook=typeList[0]
                    val localBooks=BookGreenDaoManager.getInstance().queryAllTextBook(textbook)
                    if (localBooks.size>0){
                        disMissView(tv_download)
                    }
                    else{
                        showView(tv_download)
                    }
                    disMissView(tv_course)
                }
                1 -> {
                    disMissView(tv_download,tv_course)
                }
                else -> {
                    showView(tv_course)
                    disMissView(tv_download)
                }
            }
            typeId = i
            typeStr = typeList[typeId]
            pageIndex = 1
            fetchData()
        }

    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this, 22f), 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            mBook = books[position]
            showBookDetails(mBook!!)
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
                val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookId)
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
                map["type"] = if (typeId == 2) 1 else 2
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
                            if (bookDetailsDialog != null)
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
        ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
            override fun onFinish(success: Boolean) {
                if (success) {
                    book.apply {
                        textBookType = typeStr
                        loadSate = 2
                        category = 0
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = fileTargetPath
                        bookDrawPath="${fileTargetPath}draw"
                    }
                    //下载解压完成后更新存储的book
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    //更新列表
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.dismiss()
                    //删除教材的zip文件
                    FileUtils.deleteFile(File(zipPath))
                    Handler().postDelayed({
                        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
                        showToast(book.bookName+getString(R.string.book_download_success))
                    },500)
                    if (mDownMapPool.entries.size == 0) {
                        hideLoading()
                    }

                } else {
                    showToast(book.bookName+getString(R.string.book_decompression_fail))
                }
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
                hideLoading()
                showToast(msg!!)
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
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        when (typeId) {
            0, 1 -> {
                showView(tv_province)
                getDataBook()
            }
            else -> {
                disMissView(tv_province)
                getDataBookCk()
            }
        }
    }

}