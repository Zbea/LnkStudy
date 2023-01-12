package com.bll.lnkstudy.ui.activity

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.BOOK_HOMEWORK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookDetailsDialog
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

class BookStoreActivity:BaseAppCompatActivity() ,
    IContractView.IBookStoreView {

    private var flags=0 //书籍类型
    private var typeId=0 //书籍分类
    private val mDownMapPool = HashMap<Long,BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter=BookStorePresenter(this)
    private var books= mutableListOf<Book>()
    private var mAdapter:BookStoreAdapter?=null
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var bookDetailsDialog:BookDetailsDialog?=null
    private var book:Book?=null

    private var popWindowGrade:PopWindowList?=null
    private var popWindowProvince:PopWindowList?=null

    var provinceList= mutableListOf<PopWindowBean>()
    var gradeList= mutableListOf<PopWindowBean>()
    var typeList= mutableListOf<BaseTypeBean>()

    override fun onBookStore(bookStore: BookStore?) {
        pageCount=bookStore?.pageCount!!
        val totalCount=bookStore?.totalCount
        if (totalCount==0)
            ll_page_number.visibility= View.GONE
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()

        if (!bookStore?.list.isNullOrEmpty()){
            books=bookStore?.list
            mAdapter?.setNewData(books)
        }
    }

    override fun onBuyBook(bookEvent: BookEvent?) {
        showToast("书籍购买成功")
        presenter.downBook(book?.id.toString())
        book?.status=2
        bookDetailsDialog?.setChangeStatus(2)

        mAdapter?.notifyDataSetChanged()
    }

    override fun onDownBook(bookEvent: BookEvent?) {
        book?.downloadUrl=bookEvent?.contentUrl
        book?.loadState = 2//下载ing
        mAdapter?.notifyDataSetChanged()
        //加载任务
        showLoading()
        val downloadTask = downLoadStart(bookEvent?.contentUrl!!)
        if (downloadTask !=null){
            mDownMapPool[book?.id!!] = downloadTask
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        flags=intent.flags
        getData()
        getDataType()
    }

    override fun initView() {
        setPageTitle(DataBeanManager.getIncetance().bookStoreType[flags])

        initRecyclerView()

        if(typeList.size>0){
            initTab()
        }

        initSelectorView()

        btn_page_up.setOnClickListener {
            if (pageIndex>1){
                if(pageIndex<pageCount){
                    pageIndex-=1
                    getData()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                getData()
            }
        }

    }

    //获取数据
    private fun getData(){
        val map = HashMap<String, Any>()
        map["pageIndex"] = pageIndex
        map["pageSize"] = 12
        presenter.getBooks(map)
    }

    //得到分类数据
    private fun getDataType(){
        gradeList= DataBeanManager.getIncetance().bookTypeGrade
        when(flags){
            0->{
                disMissView(tv_search)
                gradeList.clear()
                typeList= DataBeanManager.getIncetance().bookTypeJc
                for (i in 0..12){
                    provinceList.add(PopWindowBean(i,"广东省$i",i==0))
                }
                for (i in 1..6){
                    gradeList.add(PopWindowBean(i,"$i 年级",i==0))
                }
            }
            1->{
                disMissView(tv_province)
                typeList= DataBeanManager.getIncetance().bookTypeGj
            }
            2->{
                disMissView(tv_province)
                typeList= DataBeanManager.getIncetance().bookTypeZRKX
            }
            3->{
                disMissView(tv_province)
                typeList= DataBeanManager.getIncetance().bookTypeSHKX
            }
            4->{
                disMissView(tv_province)
                typeList= DataBeanManager.getIncetance().bookTypeSWKX
            }
            else->{
                disMissView(tv_province)
                typeList= DataBeanManager.getIncetance().bookTypeYDCY
            }
        }

    }


    //设置条件选择
    private fun initSelectorView(){

        if (provinceList.size>0){
            tv_province.text=provinceList[0].name
            provinceList[0].isCheck=true
        }
        else{
            tv_province.visibility=View.GONE
        }

        if (gradeList.size>0){
            tv_grade.text=gradeList[0].name
            gradeList[0].isCheck=true
        }
        else{
            tv_grade.visibility=View.GONE
        }

        tv_grade.setOnClickListener {
            if (popWindowGrade==null)
            {
                popWindowGrade= PopWindowList(this,gradeList,tv_grade,5).builder()
                popWindowGrade?.setOnSelectListener { item ->
                    tv_grade.text = item.name
                }
            }
            else{
                popWindowGrade?.show()
            }
        }

        tv_province.setOnClickListener {
            if (popWindowProvince==null)
            {
                popWindowProvince= PopWindowList(this,provinceList,tv_province,5).builder()
                popWindowProvince?.setOnSelectListener { item ->
                    tv_province.text = item.name
                }
            }
            else{
                popWindowProvince?.show()
            }
        }
    }

    //设置tab分类
    private fun initTab(){
        for (i in typeList.indices){
            var radioButton = layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
            radioButton.id = i
            radioButton.text = typeList[i].name
            radioButton.isChecked = i == 0
            var layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                DP2PX.dip2px(this, 45f))
            layoutParams.marginEnd = if (i == typeList.size-1) 0 else DP2PX.dip2px(this, 44f)
            radioButton.layoutParams = layoutParams
            rg_group.addView(radioButton)
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            typeId=typeList[i].typeId
        }

        tv_download.visibility=if (flags==0) View.VISIBLE else View.GONE

    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            book=books[position]
            showBookDetails(book)
        }
    }


    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: Book?){

        bookDetailsDialog=BookDetailsDialog(this,book!!)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.status == 1) {
                presenter.buyBook(book.id.toString())
            } else if (book.status == 2) {
                val bookDao = BookGreenDaoManager.getInstance()
                    .queryBookByBookID(book.id)
                if (bookDao == null) {
                    presenter.downBook(book.id.toString())
                } else {
                    if (bookDetailsDialog != null)
                        bookDetailsDialog?.setUnClickBtn("已下载")
                }
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String): BaseDownloadTask? {
        if (url.isNullOrEmpty()) {
            showToast("URL地址错误")
            return null
        }

        val fileName=book?.id.toString()//文件名

        val targetFileStr = FileAddress().getPathZip(fileName)
        val targetFile = File(targetFileStr)
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val download = FileDownManager.with(this).create(url).setPath(targetFileStr)
            .startDownLoad(object : FileDownManager.DownLoadCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task !=null && task.isRunning && task == mDownMapPool[book?.id]){
                        runOnUiThread {
                            val s="下载中(" + getFormatNum(soFarBytes.toDouble() / (1024*1024),"0.0") + "M/" + getFormatNum(totalBytes.toDouble() / (1024*1024),"0.0")+"M)"
                            if (bookDetailsDialog!=null)
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
                    ZipUtils.unzip(targetFileStr, fileName, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success){
                                showToast("下载完成")
                                //书籍中的参考课辅，保存到作业本
                                if (flags==0&&typeId==3){
                                    val item=HomeworkType()
                                    item.typeId= book?.id?.toInt()!!
                                    item.name=book?.name
                                    item.state=3
                                    item.bgResId=ToolUtils.getImageResStr(this@BookStoreActivity,R.mipmap.icon_homework_cover_1)
                                    item.date=System.currentTimeMillis()
                                    item.courseId= book?.classX?.toInt()!!
                                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                                    EventBus.getDefault().post(BOOK_HOMEWORK_EVENT)
                                }
                                book?.time=System.currentTimeMillis()//下载时间用于排序
                                //解压完成就开始存数据库
                                book?.status=3
                                book?.loadState = 1//已经下载
                                book?.bookPath = FileAddress().getPathBook(fileName)
                                //下载解压完成后更新存储的book
                                BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                                EventBus.getDefault().post(BOOK_EVENT)
                                //更新列表
                                mAdapter?.notifyDataSetChanged()
                                bookDetailsDialog?.dismiss()
                            }
                            else{
                                showToast("解压失败")
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
                    if (mDialog != null) {
                        mDialog!!.dismiss()
                    }
                    lock.unlock()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    mDialog?.dismiss()
                    showToast("下载书籍失败")
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

        if (mDownMapPool!=null&& mDownMapPool.isNotEmpty()){
            //拿出map中的键值对
            val entries = mDownMapPool.entries

            val iterator = entries.iterator();
            while (iterator.hasNext()){
                val entry = iterator.next() as Map.Entry<Long, BaseDownloadTask>
                val entity = entry.value
                if(task == entity){
                    iterator.remove()
                }
            }

        }
    }

}