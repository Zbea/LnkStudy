package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookDetailsDialog
import com.bll.lnkstudy.dialog.PopWindowBookStoreType
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.FileDownManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.BookEvent
import com.bll.lnkstudy.mvp.model.BookStore
import com.bll.lnkstudy.mvp.presenter.BookStorePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.BookStoreAdapter
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco4
import com.google.android.material.tabs.TabLayout
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_xtab.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

class BookStoreActivity:BaseAppCompatActivity() ,
    IContractView.IBookStoreView {

    private var title=""
    private val mDownMapPool = HashMap<Long,BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter=BookStorePresenter(this)
    private var books= mutableListOf<Book>()
    private var mAdapter:BookStoreAdapter?=null
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var bookDetailsDialog:BookDetailsDialog?=null
    private var book:Book?=null

    private var popWindowGrade:PopWindowBookStoreType?=null
    private var popWindowProvince:PopWindowBookStoreType?=null

    var provinceList= mutableListOf<BaseTypeBean>()
    var gradeList= mutableListOf<BaseTypeBean>()
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
            mDownMapPool.put(book?.id!!,downloadTask)
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        title=intent.getStringExtra("title").toString()
        getData()
        getDataType()
    }

    override fun initView() {
        setPageTitle(title)
        initRecyclerView()
        showSearchView(true)

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


    private fun getDataType(){
        gradeList=DataBeanManager.getIncetance().bookTypeGrade
        if(title=="教材")
        {
            gradeList.clear()
            typeList=DataBeanManager.getIncetance().bookTypeJc
            xtab.setxTabDisplayNum(typeList.size)
            for (i in 0..12){
                var item= BaseTypeBean()
                item.name= "广东省$i"
                provinceList.add(item)
            }

            for (i in 1..6){
                var item= BaseTypeBean()
                item.name= "$i 年级"
                gradeList.add(item)
            }
        }
        if(title=="古籍")
        {
            typeList=DataBeanManager.getIncetance().bookTypeGj
            xtab.setxTabDisplayNum(typeList.size)
        }
        if(title=="社会科学")
        {
            typeList=DataBeanManager.getIncetance().bookTypeSHKX
            xtab.setxTabDisplayNum(typeList.size)
        }
        if(title=="运动才艺")
        {
            typeList=DataBeanManager.getIncetance().bookTypeYDCY
            xtab.setxTabDisplayNum(10)
            xtab.tabMode=TabLayout.MODE_SCROLLABLE
        }
        if(title=="自然科学")
        {
            typeList=DataBeanManager.getIncetance().bookTypeZRKX
            xtab.setxTabDisplayNum(typeList.size)
        }
        if(title=="思维科学")
        {
            typeList=DataBeanManager.getIncetance().bookTypeSWKX
            xtab.setxTabDisplayNum(typeList.size)
        }


        if(typeList.size>0){
            setTab()
        }
        setTypeView()
    }


    //设置分类
    private fun setTypeView(){

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
                popWindowGrade=PopWindowBookStoreType(this,gradeList,tv_grade).builder()
                popWindowGrade?.setOnSelectListener(object : PopWindowBookStoreType.OnSelectListener {
                    override fun onSelect(baseTypeBean: BaseTypeBean) {
                        tv_grade.text=baseTypeBean.name
                    }
                })
            }
            else{
                popWindowGrade?.show()
            }

        }

        tv_province.setOnClickListener {
            if (popWindowProvince==null)
            {
                popWindowProvince=PopWindowBookStoreType(this,provinceList,tv_province).builder()
                popWindowProvince?.setOnSelectListener(object : PopWindowBookStoreType.OnSelectListener {
                    override fun onSelect(baseTypeBean: BaseTypeBean) {
                        tv_province.text=baseTypeBean.name
                    }
                })
            }
            else{
                popWindowProvince?.show()
            }
        }
    }

    //设置tab分类
    private fun setTab(){
        for (item in typeList){
            xtab?.newTab()?.setText(item.name)?.let { xtab?.addTab(it) }
        }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()
        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {

                if (title=="教材"){
                    val baseTypeBean=typeList[tab?.position!!]
                    if (baseTypeBean.typeId==0){
                        showView(tv_province)
                    }
                    else{
                        disMissView(tv_province)
                    }
                }

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })
    }

    private fun initRecyclerView(){
        if(mAdapter==null){
            rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
            mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
            rv_list.adapter = mAdapter
            mAdapter?.bindToRecyclerView(rv_list)
            mAdapter?.setEmptyView(R.layout.common_book_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco4(75,20))
            mAdapter?.setOnItemClickListener { adapter, view, position ->
                book=books[position]
                showBookDetails(book)
            }
        }
        else{
            mAdapter?.setNewData(books)
        }
    }


    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: Book?){

        bookDetailsDialog=BookDetailsDialog(this,book!!)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener(object : BookDetailsDialog.OnClickListener {
            override fun onClick() {

                if (book.status==1){
                    presenter.buyBook(book.id.toString())
                }
                else if (book.status==2){
                    val bookDao=BookGreenDaoManager.getInstance(this@BookStoreActivity).queryBookByBookID(book.id)
                    if (bookDao==null){
                        presenter.downBook(book.id.toString())
                    }
                    else{
                        if (bookDetailsDialog!=null)
                            bookDetailsDialog?.setUnClickBtn("已下载")
                    }
                }
            }

        })
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
                                book?.time=System.currentTimeMillis()//下载时间用于排序
                                //解压完成就开始存数据库
                                book?.status=3
                                book?.loadState = 1//已经下载
                                book?.bookPath = FileAddress().getPathBook(fileName)
                                //下载解压完成后更新存储的book
                                BookGreenDaoManager.getInstance(this@BookStoreActivity).insertOrReplaceBook(book)
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