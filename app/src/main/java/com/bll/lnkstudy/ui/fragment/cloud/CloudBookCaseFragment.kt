package com.bll.lnkstudy.ui.fragment.cloud

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_content.*
import java.io.File
import java.util.concurrent.CountDownLatch

class CloudBookCaseFragment:BaseCloudFragment() {

    private var countDownTasks: CountDownLatch?=null //异步完成后操作
    private var mAdapter:BookStoreAdapter?=null
    private var bookTypeStr=""
    private val books= mutableListOf<BookBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=9
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            fetchData()
        }
        else{
            showNetworkDialog()
        }
    }

    private fun initTab(){
        val books= DataBeanManager.bookType
        bookTypeStr=books[0]
        for (i in books.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=books[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        bookTypeStr=itemTabTypes[position].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,28f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity,22f),50))
            setOnItemClickListener { adapter, view, position ->
                val book=books[position]
                val localBook = BookGreenDaoManager.getInstance().queryBookByID(book.bookId)
                if (localBook == null) {
                    showLoading()
                    //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
                    if (!book.drawUrl.isNullOrEmpty()){
                        countDownTasks= CountDownLatch(2)
                        downloadBook(book)
                        downloadBookDrawing(book)
                    }else{
                        countDownTasks= CountDownLatch(1)
                        downloadBook(book)
                    }
                    downloadSuccess(book)
                } else {
                    showToast(R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudBookCaseFragment.position=position
                CommonDialog(requireActivity(),getScreenPosition()).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            deleteItem()
                        }
                    })
                true
            }
        }
    }

    /**
     * 下载完成
     */
    private fun downloadSuccess(book: BookBean){
        //等待两个请求完成后刷新列表
        Thread{
            countDownTasks?.await()
            requireActivity().runOnUiThread {
                hideLoading()
                val localBook = BookGreenDaoManager.getInstance().queryBookByID(book.bookId)
                if (localBook!=null){
                    deleteItem()
                    showToast(book.bookName+getString(R.string.book_download_success))
                }
                else{
                    if (FileUtils.isExistContent(book.bookDrawPath)){
                        FileUtils.deleteFile(File(book.bookDrawPath))
                    }
                    if (FileUtils.isExistContent(book.bookPath)){
                        FileUtils.deleteFile(File(book.bookPath))
                    }
                    showToast(book.bookName+getString(R.string.book_download_fail))
                }
            }
            countDownTasks=null
        }.start()
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(books[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载书籍
     */
    private fun downloadBook(book: BookBean) {
        FileDownManager.with(activity).create(book.downloadUrl).setPath(book.bookPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    //修改书库分类状态
                    ItemTypeDaoManager.getInstance().saveBookBean(5,book.subtypeStr,true)
                    book.time=System.currentTimeMillis()
                    book.isLook=false
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(6,book.bookId,1,book.bookId, Gson().toJson(book),book.downloadUrl)
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    /**
     * 下载书籍手写内容
     */
    private fun downloadBookDrawing(book: BookBean){
        val fileName = MD5Utils.digest(book.bookId.toString())//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(activity).create(book.drawUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, book.bookDrawPath, object : IZipCallback {
                        override fun onFinish() {
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
                            DataUpdateManager.createDataUpdate(6,book.bookId,2,book.bookId,"",FileAddress().getPathBookDraw(fileName))
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 6
        map["subTypeStr"] = bookTypeStr
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        books.clear()
        for (bookCloud in cloudList.list){
            if (bookCloud.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(bookCloud.listJson, BookBean::class.java)
                bookBean.id=null
                bookBean.cloudId=bookCloud.id
                bookBean.drawUrl=bookCloud.downloadUrl
                books.add(bookBean)
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }
}