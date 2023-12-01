package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.textbook.TextbookBean
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_radiogroup_fragment.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.CountDownLatch

class CloudTextbookFragment:BaseCloudFragment() {
    private var countDownTasks: CountDownLatch?=null //异步完成后操作
    private var mAdapter:TextBookAdapter?=null
    private var books= mutableListOf<TextbookBean>()
    private var textBook=""//用来区分课本类型
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
        val texts= arrayListOf("往期课本","参考课本")
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
        mAdapter = TextBookAdapter(R.layout.item_textbook, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity,33f),38))
            setOnItemClickListener { adapter, view, position ->
                val book=books[position]
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook == null) {
                    showLoading()
                    //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
                    if (!book.drawUrl.isNullOrEmpty()){
                        countDownTasks= CountDownLatch(2)
                        downloadBook(book)
                        downloadBookDrawing(book)
                    }
                    else{
                        countDownTasks=CountDownLatch(1)
                        downloadBook(book)
                    }
                    downloadSuccess(book)
                } else {
                    showToast(getScreenPosition(),R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudTextbookFragment.position=position
                CommonDialog(requireActivity(),getScreenPosition()).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            val ids= mutableListOf<Int>()
                            ids.add(books[position].cloudId)
                            mCloudPresenter.deleteCloud(ids)
                        }
                    })
                true
            }
        }
    }

    /**
     * 下载完成
     */
    private fun downloadSuccess(book: TextbookBean){
        //等待两个请求完成后刷新列表
        Thread{
            countDownTasks?.await()
            requireActivity().runOnUiThread {
                hideLoading()
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByID(book.bookId)
                if (localBook!=null){
                    showToast(getScreenPosition(),book.bookName+getString(R.string.book_download_success))
                    EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
                }
                else{
                    if (FileUtils.isExistContent(book.bookDrawPath)){
                        FileUtils.deleteFile(File(book.bookDrawPath))
                    }
                    if (FileUtils.isExistContent(book.bookPath)){
                        FileUtils.deleteFile(File(book.bookPath))
                    }
                    showToast(getScreenPosition(),book.bookName+getString(R.string.book_download_fail))
                }
            }
            countDownTasks=null
        }.start()
    }

    /**
     * 下载书籍手写内容
     */
    private fun downloadBookDrawing(book: TextbookBean){
        val fileName = book.bookId.toString()//文件名
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
                            //创建增量更新
                            DataUpdateManager.createDataUpdate(1,book.bookId,2,book.bookId,"",book.bookDrawPath)
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
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

    /**
     * 下载书籍
     */
    private fun downloadBook(book: TextbookBean) {
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(activity).create(book.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, book.bookPath, object : IZipCallback {
                        override fun onFinish() {
                            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                            //创建增量更新
                            DataUpdateManager.createDataUpdateSource(1,book.bookId,1,book.bookId
                                ,Gson().toJson(book),book.downloadUrl)
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        map["type"] = 1
        map["grade"] = grade
        map["subTypeStr"] = textBook
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        books.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(item.listJson, TextbookBean::class.java)
                bookBean.id=null
                bookBean.cloudId=item.id
                bookBean.drawUrl=item.downloadUrl
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