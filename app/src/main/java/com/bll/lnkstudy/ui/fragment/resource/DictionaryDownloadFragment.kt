package com.bll.lnkstudy.ui.fragment.resource

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.DownloadTextbookDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.book.TextbookStore
import com.bll.lnkstudy.mvp.presenter.DownloadDictionaryPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import java.io.File

class DictionaryDownloadFragment:BaseMainFragment(),IContractView.IDictionaryResourceView {

    private var presenter= DownloadDictionaryPresenter(this,getScreenPosition())
    private var books = mutableListOf<TextbookBean>()
    private var mAdapter: TextBookAdapter? = null
    private var bookDetailsDialog: DownloadTextbookDialog? = null
    private var position=0

    override fun onList(bookStore: TextbookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }
    override fun buySuccess() {
        books[position].buyStatus = 1
        mAdapter?.notifyItemChanged(position)
        bookDetailsDialog?.setChangeStatus()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initChangeScreenData()
        pageSize=12

        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            fetchData()
        }
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f

        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(requireActivity(), 4)//创建布局管理
        mAdapter = TextBookAdapter(R.layout.item_bookstore, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position = position
            showBookDetails(books[position])
        }
    }

    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: TextbookBean) {
        bookDetailsDialog = DownloadTextbookDialog(requireActivity(), book)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                if (AppDaoManager.getInstance().queryBeanByBookId(book.bookId)==null){
                    downLoadStart(book)
                }
                else{
                    book.loadSate = 2
                    showToast(R.string.toast_downloaded)
                    bookDetailsDialog?.setDissBtn()
                }
            } else {
                val map = HashMap<String, Any>()
                map["type"] = 8
                map["bookId"] = book.bookId
                presenter.buyDictionary(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(book: TextbookBean): BaseDownloadTask? {
        val fileName = MD5Utils.digest(book.bookId.toString())//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val download = FileBigDownManager.with().create(book.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        requireActivity().runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M") + "/" +
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            bookDetailsDialog?.setUnClickBtn(s)
                        }
                    }
                }
                override fun completed(task: BaseDownloadTask?) {
                    book.loadSate = 2
                    book.bookPath = FileAddress().getPathDictionary(fileName)
                    ZipUtils.unzip(zipPath, book.bookPath, object : IZipCallback {
                        override fun onFinish() {
                            val item=AppBean()
                            item.type=2
                            item.bookId=book.bookId
                            item.time=System.currentTimeMillis()
                            item.appName=book.bookName
                            item.path=book.bookPath
                            item.imageUrl=book.imageUrl
                            AppDaoManager.getInstance().insertOrReplace(item)

                            FileUtils.deleteFile(File(zipPath))
                            bookDetailsDialog?.dismiss()
                            showToast(book.bookName + getString(R.string.book_download_success))
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                            showToast(book.bookName + msg!!)
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    showToast(book.bookName + getString(R.string.book_download_fail))
                }
            })
        return download
    }

    override fun initChangeScreenData() {
        super.initChangeScreenData()
        presenter= DownloadDictionaryPresenter(this,getScreenPosition())
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        presenter.getList(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}