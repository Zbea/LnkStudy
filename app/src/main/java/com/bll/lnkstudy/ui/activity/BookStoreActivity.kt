package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.os.Handler
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
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
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

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
    private var gradeStr = ""
    private var typeStr = ""//子类
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var mBook: BookBean? = null

    private var popWindowGrade: PopupList? = null

    private var gradeList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()
    private var bookNameStr=""

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        //年级分类
        if (bookStoreType.typeGrade.isNullOrEmpty()) return
        for (i in bookStoreType.typeGrade.indices) {
            gradeList.add(PopupBean(i, bookStoreType.typeGrade[i], i == 0))
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

        fetchData()
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
        pageSize=12
        categoryStr = intent.getStringExtra("category").toString()
        getData()
    }


    override fun initView() {
        setPageTitle(categoryStr)
        showView(tv_grade,ll_search)
        disMissView(tv_download)

        initRecyclerView()

        et_search.addTextChangedListener {
            bookNameStr =it.toString()
            if (bookNameStr.isNotEmpty()){
                pageIndex=1
                fetchData()
            }
        }
    }

    //获取数据
    private fun getData() {
        presenter.getBookType()
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
                popWindowGrade = PopupList(this, gradeList, tv_grade, 5).builder()
                popWindowGrade?.setOnSelectListener { item ->
                    gradeStr = item.name
                    tv_grade.text = gradeStr
                    typeFindData()
                }
            } else {
                popWindowGrade?.show()
            }
        }

    }

    /**
     * 分类查找上
     */
    private fun typeFindData(){
        pageIndex = 1
        bookNameStr=""//清除搜索标记
        fetchData()
    }


    //设置tab分类
    @SuppressLint("InflateParams")
    private fun initTab() {

        for (i in typeList.indices) {
            rg_group.addView(getRadioButton(i,typeList[i],typeList.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            typeStr = typeList[i]
            typeFindData()
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
            if (book.buyStatus==1){
                val localBook = BookGreenDaoManager.getInstance().queryBookByID(book.bookPlusId)
                if (localBook == null) {
                    val downloadTask = downLoadStart(book.downloadUrl,book)
                    mDownMapPool[book.bookPlusId] = downloadTask!!
                } else {
                    book.loadSate =2
                    showToast(R.string.toast_downloaded)
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.setDissBtn()
                }
            }
            else{
                val map = HashMap<String, Any>()
                map["type"] = 3
                map["bookId"] = book.bookPlusId
                presenter.buyBook(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String,book: BookBean): BaseDownloadTask? {
        showLoading()
        val formatStr=book.downloadUrl.substring(book.downloadUrl.lastIndexOf("."))
        val fileName = MD5Utils.convertMD5(book.bookId.toString())+formatStr//文件名
        val targetFileStr = FileAddress().getPathBook(fileName)
        val targetFile = File(targetFileStr)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val download = FileDownManager.with(this).create(url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning && task == mDownMapPool[book.bookPlusId]) {
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
                    book.apply {
                        bookType = when (categoryStr) {
                            "思维科学", "自然科学" -> {
                                "科学技术"
                            }
                            "运动才艺" -> {
                                "运动才艺"
                            }
                            else -> {
                                typeStr
                            }
                        }
                        loadSate = 2
                        category = 1
                        downDate=System.currentTimeMillis()
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = targetFileStr
                    }
                    //下载解压完成后更新存储的book
                    val id=BookGreenDaoManager.getInstance().insertOrReplaceGetId(book)
                    EventBus.getDefault().post(BOOK_EVENT)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(0,id.toInt(),0,book.bookId
                        , Gson().toJson(book),book.downloadUrl)
                    //更新列表
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.dismiss()
                    mDialog?.dismiss()
                    Handler().postDelayed({
                        showToast(book.bookName+getString(R.string.book_download_success))
                    },500)
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    mDialog?.dismiss()
                    showToast(book.bookName+getString(R.string.book_download_fail))
                    deleteDoneTask(task)
                }
            })
        return download
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
            val iterator = entries.iterator()
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
        hideKeyboard()
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        if (bookNameStr.isEmpty()){
            map["grade"] = gradeStr
            map["type"] = categoryStr
            map["subType"] = typeStr
        }
        else{
            map["grade"] = gradeStr
            map["subType"] = typeStr
            map["type"] = categoryStr
            map["bookName"] = bookNameStr
        }

        presenter.getBooks(map)
    }

}