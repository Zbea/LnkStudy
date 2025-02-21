package com.bll.lnkstudy.ui.activity.book

import android.os.Handler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.book.TextbookStore
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.presenter.TextbookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.dialog_book_detail.btn_ok
import kotlinx.android.synthetic.main.dialog_book_detail.iv_book
import kotlinx.android.synthetic.main.dialog_book_detail.tv_book_name
import kotlinx.android.synthetic.main.dialog_book_detail.tv_course
import kotlinx.android.synthetic.main.dialog_book_detail.tv_info
import kotlinx.android.synthetic.main.dialog_book_detail.tv_price
import kotlinx.android.synthetic.main.dialog_book_detail.tv_version
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat

/**
 * 题卷本书城
 */
class HomeworkBookStoreActivity : BaseAppCompatActivity(), IContractView.ITextbookStoreView {

    private lateinit var presenter :TextbookStorePresenter
    private var book: TextbookBean?=null
    private var bookId =0

    override fun onTextbook(bookStore: TextbookStore) {
        if (bookStore.list.size>0){
            book = bookStore.list[0]
            showBooks()
        }
    }

    override fun buySuccess() {
        book?.buyStatus = 1
        btn_ok?.setText(R.string.book_download_str)
    }


    override fun layoutId(): Int {
        return R.layout.dialog_book_detail
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        bookId=intent.getIntExtra("bookId",0)

        if (NetworkUtil(this).isNetworkConnected()){
            fetchData()
        }
    }

    override fun initChangeScreenData() {
        presenter = TextbookStorePresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        disMissView(btn_ok)

        btn_ok.setOnClickListener {
            if (book?.buyStatus == 1) {
                if (!HomeworkBookDaoManager.getInstance().isExist(book!!.bookId)) {
                    downLoadStart(book!!.downloadUrl, book!!)
                } else {
                    book?.loadSate = 2
                    disMissView(btn_ok)
                    showToast(R.string.toast_downloaded)
                }
            } else {
                val map = HashMap<String, Any>()
                map["type"] = 1
                map["bookId"] = book?.bookId!!
                presenter.buyBook(map)
            }
        }
    }

    private fun showBooks() {
        showView(btn_ok)
        GlideUtils.setImageUrl(this@HomeworkBookStoreActivity,book?.imageUrl,iv_book)
        tv_book_name?.text = book?.bookName+"-"+ DataBeanManager.popupSemesters()[book!!.semester-1].name
        tv_price?.text = getString(R.string.price)+"： " + if (book?.price==0) getString(R.string.free) else book?.price
        tv_version?.text =getString(R.string.press)+"： " + DataBeanManager.getBookVersionStr(book!!.version)
        tv_info?.text = getString(R.string.introduction)+"： " + book?.bookDesc
        tv_course?.text = getString(R.string.subject)+"： " + DataBeanManager.getCourseStr(book!!.subject)
        if (book?.buyStatus == 1) {
            btn_ok?.setText(R.string.book_download_str)
        } else {
            btn_ok?.setText(R.string.book_buy_str)
        }
    }

    //下载book
    private fun downLoadStart(url: String, book: TextbookBean): BaseDownloadTask? {
        showLoading()
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val targetFile = File(zipPath)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val download = FileBigDownManager.with(this).create(url).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0") + "M/" +
                                    getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0") + "M"
                            btn_ok?.text=s
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?,soFarBytes: Long, totalBytes: Long) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    val fileTargetPath = FileAddress().getPathHomeworkBook(fileName)
                    unzip(book, zipPath, fileTargetPath)
                    //删除zip文件
                    FileUtils.deleteFile(File(zipPath))
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast(book.bookName+getString(R.string.book_download_fail))
                }
            })
        return download
    }

    /**
     * 解压
     */
    private fun unzip(book: TextbookBean, zipPath: String, fileTargetPath: String) {
        ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
                val homeworkBookBean=HomeworkBookBean().apply {
                    bookId=book.bookId
                    imageUrl=book.imageUrl
                    bookName=book.bookName
                    bookDesc=book.bookDesc
                    price=book.price
                    type=book.type
                    semester=book.semester
                    area=book.area
                    grade=book.grade
                    subject=book.subject
                    downloadUrl=book.downloadUrl
                    bookPath=fileTargetPath
                    bookDrawPath=FileAddress().getPathHomeworkBookDraw(File(fileTargetPath).name)
                    time=System.currentTimeMillis()
                }
                HomeworkBookDaoManager.getInstance().insertOrReplaceBook(homeworkBookBean)
                book.loadSate=2
                disMissView(btn_ok)

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

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["bookId"]=bookId
        presenter.getHomeworkBooks(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}