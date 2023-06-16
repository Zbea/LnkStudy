package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.BookCaseTypeListActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.*
import java.io.File

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    private val halfYear=180*24*60*60*1000
    private var mAdapter: BookAdapter?=null
    private var position=0
    private var books= mutableListOf<BookBean>()//所有数据
    private var bookTopBean:BookBean?=null
    private val cloudList= mutableListOf<CloudListBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(R.string.main_bookcase_title)

        initRecyclerView()
        findBook()

        tv_type.setOnClickListener {
            customStartActivity(Intent(activity,BookCaseTypeListActivity::class.java))
        }
        
        ll_book_top.setOnClickListener {
            bookTopBean?.let { gotoBookDetails(it) }
        }

    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(activity,23f),28))
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                gotoBookDetails(bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@BookCaseFragment.position=position
                delete()
                true
            }
        }
    }


    /**
     * 查找本地书籍
     */
    private fun findBook(){
        books=BookGreenDaoManager.getInstance().queryAllBook(true)
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
        }
        else{
            iv_content_up.setImageBitmap(null)
            iv_content_down.setImageBitmap(null)
        }
    }


    private fun setImageUrl(url: String,image:ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    //删除书架书籍
    private fun delete(){
        CommonDialog(requireActivity(),screenPos).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val book=books[position]
                BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                books.remove(book)
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                if (File(book.bookDrawPath).exists())
                    FileUtils.deleteFile(File(book.bookDrawPath))
                mAdapter?.notifyDataSetChanged()
                //删除增量更新
                DataUpdateManager.deleteDateUpdate(6,book.bookId,1,book.bookId)
                if (books.size==11)
                {
                    findBook()
                }
            }
        })
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
        val books= BookGreenDaoManager.getInstance().queryAllBook()
        for (item in books){
            if (System.currentTimeMillis()>=item.downDate+halfYear){
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
                                subTypeStr=item.bookType
                                date=System.currentTimeMillis()
                                listJson= Gson().toJson(item)
                                bookId=item.bookId
                            })
                            if (cloudList.size==books.size)
                                mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
                else{
                    cloudList.add(CloudListBean().apply {
                        type=0
                        zipUrl=item.downloadUrl
                        downloadUrl="null"
                        subType=-1
                        subTypeStr=item.bookType
                        date=System.currentTimeMillis()
                        listJson= Gson().toJson(item)
                        bookId=item.bookId
                    })
                    if (cloudList.size==books.size)
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