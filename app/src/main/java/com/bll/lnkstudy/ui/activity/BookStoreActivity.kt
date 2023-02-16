package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookDetailsDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.BookStore
import com.bll.lnkstudy.mvp.model.BookStoreType
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.ceil

/**
 * 书城
 */
class BookStoreActivity : BaseAppCompatActivity(),
    IContractView.IBookStoreView {

    private var categoryStr = ""//类别
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<BookBean>()
    private var mAdapter: BookStoreAdapter? = null
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var pageSize = 12
    private var gradeStr = ""
    private var typeStr = ""//子类
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var mBook: BookBean? = null

    private var popWindowGrade: PopupList? = null

    private var gradeList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()

    override fun onBook(bookStore: BookStore?) {
        pageCount = ceil(bookStore?.total?.toDouble()!! / pageSize).toInt()
        val totalCount = bookStore.total
        if (totalCount == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType?) {
        //年级分类
        if (bookStoreType?.typeGrade.isNullOrEmpty()) return
        for (i in bookStoreType?.typeGrade?.indices!!) {
            gradeList.add(
                PopupBean(
                    i,
                    bookStoreType.typeGrade[i],
                    i == 0
                )
            )
        }
        gradeStr = gradeList[0].name
        initSelectorView()

        //子分类
        val types = bookStoreType.subType[categoryStr]
        if (types?.size!! >0){
            typeList=types
            typeStr=types[0]
            initTab()
        }

        getDataBook()
    }

    override fun buyBookSuccess() {
        mBook?.buyStatus=1
        bookDetailsDialog?.setChangeStatus()
        mAdapter?.notifyDataSetChanged()
    }


    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        categoryStr = intent.getStringExtra("category")
        getData()
    }

    override fun initView() {
        setPageTitle(categoryStr)
        disMissView(tv_province,tv_download)

        initRecyclerView()

        btn_page_up.setOnClickListener {
            if (pageIndex > 1) {
                if (pageIndex < pageCount) {
                    pageIndex -= 1
                    getData()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if (pageIndex < pageCount) {
                pageIndex += 1
                getData()
            }
        }

    }

    //获取数据
    private fun getData() {
        presenter.getBookType()
    }

    //获取书籍
    private fun getDataBook() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = 12
        map["grade"] = gradeStr
        map["type"] = categoryStr
        map["subType"] = typeStr
        presenter.getBooks(map)
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {

        if (gradeList.size > 0) {
            tv_grade.text = gradeList[0].name
        } else {
            disMissView(tv_grade)
        }

        tv_grade.setOnClickListener {
            if (popWindowGrade == null) {
                popWindowGrade = PopupList(this, gradeList, tv_grade,tv_grade.width, 5).builder()
                popWindowGrade?.setOnSelectListener { item ->
                    gradeStr = item.name
                    tv_grade.text = gradeStr
                    pageIndex = 1
                    getDataBook()
                }
            } else {
                popWindowGrade?.show()
            }
        }

    }


    //设置tab分类
    @SuppressLint("InflateParams")
    private fun initTab() {

        for (i in typeList.indices) {
            rg_group.addView(getRadioButton(i,typeList[i],typeList.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            typeStr = typeList[i]
            pageIndex = 1
            getDataBook()
        }

    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this, 22f), 60))
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
            if (book.buyStatus==1){
                val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookId)
                if (localBook == null) {
                    val downloadTask = downLoadStart(book.downloadUrl,book)
                    mDownMapPool[book.bookId] = downloadTask!!
                } else {
                    book?.loadSate=2
                    showToast("已下载")
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.setDissBtn()
                }
            }
            else{
                val map = HashMap<String, Any>()
                map["type"] = 3
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String,book: BookBean): BaseDownloadTask? {
        showLoading()

        val fileName = book?.bookId.toString()//文件名
        val targetFileStr = FileAddress().getPathZip(fileName)
        val targetFile = File(targetFileStr)
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val download = FileDownManager.with(this).create(url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning && task == mDownMapPool[book?.bookId]) {
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
                    unzip(book,targetFileStr,fileName)
                    lock.unlock()
                    mDialog?.dismiss()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    mDialog?.dismiss()
                    showToast("${book.bookName}下载失败")
                    deleteDoneTask(task)
                }
            })
        return download
    }

    /**
     * 解压
     */
    private fun unzip(book: BookBean, targetFileStr:String, fileName:String){
        ZipUtils.unzip(targetFileStr, fileName, object : ZipUtils.ZipCallback {
            override fun onFinish(success: Boolean) {
                if (success) {
                    book.run {
                        showToast("${bookName}下载完成")
                        when (categoryStr) {
                            "思维科学", "自然科学" -> {
                                bookType="科学技术"
                            }
                            "运动才艺" -> {
                                bookType="运动才艺"
                            }
                            else -> {
                                bookType=typeStr
                            }
                        }
                        loadSate = 2
                        category = 1
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = FileAddress().getPathBook(fileName)
                    }
                    //下载解压完成后更新存储的book
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    EventBus.getDefault().post(BOOK_EVENT)
                    //更新列表
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.dismiss()
                } else {
                    showToast("${book.bookName}解压失败")
                }
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
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

        if (mDownMapPool != null && mDownMapPool.isNotEmpty()) {
            //拿出map中的键值对
            val entries = mDownMapPool.entries
            val iterator = entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<Long, BaseDownloadTask>
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

}