package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.book.BookcaseTypeActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.*
import java.io.File


/**
 * 书架
 */
class BookcaseFragment: BaseFragment() {

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<BookBean>()//所有数据
    private var bookTopBean: BookBean?=null
    private val cloudList= mutableListOf<CloudListBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(R.string.main_bookcase_title)

        initRecyclerView()
        findBook()

        tv_type.setOnClickListener {
            customStartActivity(Intent(activity, BookcaseTypeActivity::class.java))
        }
        
        ll_book_top.setOnClickListener {
            if (bookTopBean!=null)
                MethodManager.gotoBookDetails(requireActivity(),bookTopBean,screenPos)
        }

    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(activity,22f),28))
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(requireActivity(),bookBean,screenPos)
            }
        }
    }


    /**
     * 查找本地书籍
     */
    private fun findBook(){
        books=BookGreenDaoManager.getInstance().queryAllBook(true,13)
        if (books.size==0){
            bookTopBean=null
        }
        else{
            bookTopBean=books[0]
            books.removeFirst()
        }
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (bookTopBean!=null){
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_up)
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_down)
            tv_name.text=bookTopBean?.bookName
        }
        else{
            iv_content_up.setImageBitmap(null)
            iv_content_down.setImageBitmap(null)
            tv_name.text=""
        }
    }


    private fun setImageUrl(url: String,image:ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==BOOK_EVENT){
            findBook()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        findBook()
    }

    fun upload(token:String){
        if (grade==0) return
        cloudList.clear()
        val maxBooks= mutableListOf<BookBean>()
        val books= BookGreenDaoManager.getInstance().queryAllBook()
        for (item in books){
            if (System.currentTimeMillis()>=item.time+Constants.halfYear){
                maxBooks.add(item)
                //判读是否存在手写内容
                if (File(item.bookDrawPath).exists()){
                    FileUploadManager(token).apply {
                        startUpload(item.bookDrawPath,item.bookId.toString())
                        setCallBack{
                            cloudList.add(CloudListBean().apply {
                                type=0
                                zipUrl=item.downloadUrl
                                downloadUrl=it
                                subType=-1
                                subTypeStr=item.subtypeStr
                                date=System.currentTimeMillis()
                                listJson= Gson().toJson(item)
                                bookId=item.bookId
                            })
                            if (cloudList.size==maxBooks.size)
                                mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
                else{
                    cloudList.add(CloudListBean().apply {
                        type=0
                        zipUrl=item.downloadUrl
                        subType=-1
                        subTypeStr=item.subtypeStr
                        date=System.currentTimeMillis()
                        listJson= Gson().toJson(item)
                        bookId=item.bookId
                    })
                    if (cloudList.size==maxBooks.size)
                        mCloudUploadPresenter.upload(cloudList)
                }
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList){
            val bookBean=BookGreenDaoManager.getInstance().queryBookByID(item.bookId)
            //删除书籍
            FileUtils.deleteFile(File(bookBean.bookPath))
            BookGreenDaoManager.getInstance().deleteBook(bookBean)
            //删除增量数据
            DataUpdateManager.deleteDateUpdate(6,bookBean.bookId,1,bookBean.bookId)
        }
        findBook()
    }


}