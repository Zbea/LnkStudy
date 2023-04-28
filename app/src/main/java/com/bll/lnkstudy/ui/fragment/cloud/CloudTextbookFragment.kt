package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.ui.adapter.BookAdapter
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

class CloudTextbookFragment:BaseCloudFragment() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var textBook=""//用来区分课本类型
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=9
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab(){
        val texts= DataBeanManager.textbookType.toMutableList()
        texts.removeLast()
        textBook=texts[0]
        for (i in texts.indices) {
            rg_group.addView(getRadioButton(i ,texts[i],texts.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            textBook=texts[id]
            pageIndex=1
            fetchData()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity,33f),38))
            setOnItemClickListener { adapter, view, position ->
                val book=books[position]
                val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookId)
                if (localBook == null) {
                    showLoading()
                    //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
                    if (book.downloadUrl=="null"){
                        downloadBook(book)
                    }
                    else{
                        downloadBookDrawing(book)
                    }
                } else {
                    showToast(screenPos,R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudTextbookFragment.position=position
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
        FileDownManager.with(activity).create(book.zipUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val fileTargetPath =book.bookDrawPath
                    FileUtils.deleteFile(File(fileTargetPath))
                    ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                //删除教材的zip文件
                                FileUtils.deleteFile(File(zipPath))
                                showLog(fileTargetPath)
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
                                showLog(fileTargetPath)
                                BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                                //删除教材的zip文件
                                FileUtils.deleteFile(File(zipPath))
                                Handler().postDelayed({
                                    hideLoading()
                                    EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
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
        map["type"] = 1
        map["grade"] = grade
        map["subTypeStr"] = textBook
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(item: CloudList) {
        setPageNumber(item.total)
        books.clear()
        for (item in item.list){
            if (item.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(item.listJson, BookBean::class.java)
                bookBean.id=null
                bookBean.cloudId=item.id
                bookBean.isCloud=true
                bookBean.zipUrl=item.downloadUrl
                books.add(bookBean)
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }
}