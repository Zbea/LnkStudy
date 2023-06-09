package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class CloudBookCaseFragment:BaseCloudFragment() {

    private var mAdapter:BookStoreAdapter?=null
    private var bookTypeStr=""
    private val books= mutableListOf<BookBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=12
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initTab(){
        val books= DataBeanManager.bookType
        bookTypeStr=books[0]
        for (i in books.indices) {
            rg_group.addView(getRadioButton(i ,books[i],books.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            bookTypeStr=books[id]
            pageIndex=1
            fetchData()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,28f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(activity,22f),50))
            setOnItemClickListener { adapter, view, position ->
                val book=books[position]
                val localBook = BookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook == null) {
                    showLoading()
                    //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
                    if (book.drawUrl!="null"){
                        downloadBookDrawing(book)
                    }else{
                        downloadBook(book)
                    }
                } else {
                    showToast(screenPos,R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudBookCaseFragment.position=position
                val ids= mutableListOf<Int>()
                ids.add(books[position].cloudId)
                mCloudPresenter.deleteCloud(ids)
                true
            }
        }
    }

    /**
     * 下载书籍手写内容
     */
    private fun downloadBookDrawing(book: BookBean){
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(activity).create(book.drawUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val fileTargetPath =book.bookDrawPath
                    ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                //删除教材的zip文件
                                FileUtils.deleteFile(File(zipPath))
                                downloadBook(book)
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载书籍
     */
    private fun downloadBook(book: BookBean) {
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(activity).create(book.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val fileTargetPath = book.bookPath
                    ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                                //删除教材的zip文件
                                FileUtils.deleteFile(File(zipPath))
                                //创建增量更新
                                DataUpdateManager.createDataUpdateSource(6,book.bookId,1,book.bookId
                                    , Gson().toJson(book),book.downloadUrl)

                                Handler().postDelayed({
                                    hideLoading()
                                    EventBus.getDefault().post(Constants.BOOK_EVENT)
                                    showToast(screenPos,book.bookName+getString(R.string.book_download_success))
                                },500)
                            } else {
                                showToast(screenPos,book.bookName+getString(R.string.book_decompression_fail))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                            hideLoading()
                            //下载失败删掉已下载手写内容
                            FileUtils.deleteFile(File(book.bookDrawPath))
                            showToast(screenPos,msg!!)
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    //下载失败删掉已下载手写内容
                    FileUtils.deleteFile(File(book.bookDrawPath))
                    showToast(screenPos,book.bookName+getString(R.string.book_download_fail))
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 0
        map["subTypeStr"] = bookTypeStr
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(item: CloudList) {
        setPageNumber(item.total)
        books.clear()
        for (book in item.list){
            if (book.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(book.listJson, BookBean::class.java)
                bookBean.id=null
                bookBean.cloudId=book.id
                bookBean.isCloud=true
                bookBean.drawUrl=book.downloadUrl
                books.add(bookBean)
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

}