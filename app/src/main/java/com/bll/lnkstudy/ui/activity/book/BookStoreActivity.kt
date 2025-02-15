package com.bll.lnkstudy.ui.activity.book

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DownloadBookDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.book.BookStore
import com.bll.lnkstudy.mvp.model.book.BookStoreType
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.rv_list
import kotlinx.android.synthetic.main.ac_bookstore.tv_download
import kotlinx.android.synthetic.main.common_title.tv_subgrade
import org.greenrobot.eventbus.EventBus

/**
 * 书城
 */
class BookStoreActivity : BaseAppCompatActivity(), IContractView.IBookStoreView {

    private var type=0
    private var typeStr = ""//类别
    private lateinit var presenter : BookStorePresenter
    private var books = mutableListOf<BookBean>()
    private var mAdapter: BookStoreAdapter? = null
    private var subTypeStr=""
    private var subtype=0
    private var downloadBookDialog: DownloadBookDialog? = null
    private var position=0

    private var gradeList = mutableListOf<PopupBean>()
    private var subTypeList = mutableListOf<ItemList>()

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        //子分类
        val types = bookStoreType.subType[typeStr]
        if (!types.isNullOrEmpty()){
            subTypeList=types
            subTypeStr=types[0].desc
            subtype=types[0].type
            initTab()
            typeFindData()
        }
    }

    override fun buySuccess() {
        books[position].buyStatus=1
        downloadBookDialog?.setChangeStatus()
        mAdapter?.notifyItemChanged(position)
    }


    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        type = intent.flags
        typeStr=DataBeanManager.bookStoreTypes()[type-1].desc

        onCommonData()

        if (NetworkUtil(this).isNetworkConnected()){
            presenter.getBookType()
        }
    }

    override fun onCommonData() {
        if (DataBeanManager.popupTypeGrades().size>0){
            gradeList=DataBeanManager.popupTypeGrades()
            grade=gradeList[DataBeanManager.popupTypePos()].id
            initSelectorView()
        }
    }

    override fun initChangeScreenData() {
        presenter = BookStorePresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(typeStr)
        showView(tv_subgrade)
        disMissView(tv_download)

        initRecyclerView()

    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {
        tv_subgrade.text = gradeList[DataBeanManager.popupTypePos()].name
        tv_subgrade.setOnClickListener {
            PopupList(this, gradeList, tv_subgrade,tv_subgrade.width, 5).builder()
            .setOnSelectListener { item ->
                grade = item.id
                tv_subgrade.text = item.name
                typeFindData()
            }
        }
    }

    /**
     * 分类查找上
     */
    private fun typeFindData(){
        pageIndex = 1
        fetchData()
    }

    private fun initTab(){
        itemTabTypes.clear()
        for (i in subTypeList.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=subTypeList[i].desc
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        subTypeStr = subTypeList[position].desc
        subtype=subTypeList[position].type
        typeFindData()
    }


    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            showBookDetails(books[position])
        }
    }


    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: BookBean) {
        downloadBookDialog = DownloadBookDialog(this, book)
        downloadBookDialog?.builder()
        downloadBookDialog?.setOnClickListener {
            if (book.buyStatus==1){
                val localBook = BookGreenDaoManager.getInstance().queryBookByID(book.bookId)
                if (localBook == null) {
                    downLoadStart(book.downloadUrl,book)
                } else {
                    book.loadSate =2
                    showToast(R.string.toast_downloaded)
                    downloadBookDialog?.setDissBtn()
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
        val fileName = book.bookId.toString()//文件名
        val targetFileStr = FileAddress().getPathBook(fileName+ MethodManager.getUrlFormat(book.downloadUrl))
        val download = FileBigDownManager.with(this).create(url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024),"0.0M") + "/" +
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            downloadBookDialog?.setUnClickBtn(s)
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?,soFarBytes: Long, totalBytes: Long) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    book.apply {
                        subtypeStr = when (typeStr) {
                            "思维科学", "自然科学" -> {
                                "科学技术"
                            }
                            "运动健康","艺术才能" -> {
                                "运动才艺"
                            }
                            else -> {
                                subTypeStr
                            }
                        }
                        loadSate = 2
                        typeId=type
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = targetFileStr
                        bookDrawPath=FileAddress().getPathBookDraw(fileName)
                    }
                    //修改书库分类状态
                    ItemTypeDaoManager.getInstance().saveBookBean(5,book.subtypeStr,true)
                    //下载解压完成后更新存储的book
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(6,book.bookId,1, Gson().toJson(book),book.downloadUrl)
                    downloadBookDialog?.dismiss()
                    EventBus.getDefault().post(Constants.BOOK_TYPE_EVENT)
                    EventBus.getDefault().post(Constants.BOOK_EVENT)
                    showToast(book.bookName+getString(R.string.book_download_success))
                    hideLoading()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    showLog(e?.message.toString())
                    hideLoading()
                    showToast(book.bookName+getString(R.string.book_download_fail))
                }
            })
        return download
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
        map["grade"] = grade
        map["subType"] = subtype
        map["type"] = type
        presenter.getBooks(map)
    }

    override fun onNetworkConnectionSuccess() {
        presenter.getBookType()
    }

}