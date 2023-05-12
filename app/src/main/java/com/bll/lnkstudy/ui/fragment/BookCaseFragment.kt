package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Handler
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.BookCaseTypeListActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    private val halfYear=180*24*60*60*1000
    private var mAdapter: BookAdapter?=null
    private var position=0
    private var book: BookBean?=null
    private var books= mutableListOf<BookBean>()//所有数据
    private val cloudList= mutableListOf<CloudListBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(R.string.main_bookcase_title)

        initRecyclerView()
        findData()

        tv_type.setOnClickListener {
            customStartActivity(Intent(activity,BookCaseTypeListActivity::class.java))
        }

    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_book_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(activity,23f),28))
            setOnItemClickListener { adapter, view, position ->

            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@BookCaseFragment.position=position
                book=books[position]
                onLongClick()
                true
            }
        }
    }


    /**
     * 查找本地书籍
     */
    private fun findData(){
        books=BookGreenDaoManager.getInstance().queryAllBook()
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (books.size>0){
            val book=books[0]
            tv_top_page.text="${book.pageIndex+1}页"

            if (book.pageUpUrl==null){
                setImageUrl(book.imageUrl,iv_content_up)
            }
            else{
                setImageUrl(book.pageUpUrl,iv_content_up)
            }

            if (book.pageUrl==null){
                setImageUrl(book.imageUrl,iv_content_down)
            }
            else{
                setImageUrl(book.pageUrl,iv_content_down)
            }

        }
    }


    private fun setImageUrl(url: String,image:ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    //长按显示课本管理
    private fun onLongClick(){
        BookManageDialog(requireActivity(),screenPos,1,book!!).builder()
            .setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
            override fun onCollect() {
            }
            override fun onDelete() {
                delete()
            }
            override fun onLock() {
            }
          })
    }

    //删除书架书籍
    private fun delete(){
        CommonDialog(requireActivity(),screenPos).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                books.remove(book)
                mAdapter?.notifyDataSetChanged()
                EventBus.getDefault().post(BOOK_EVENT)
            }
        })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==BOOK_EVENT){
            findData()
        }
    }

    fun upload(token:String){
        if (grade==0) return
        cloudList.clear()
        val books= BookGreenDaoManager.getInstance().queryAllBook()
        for (item in books){
            if (System.currentTimeMillis()>=item.downDate+halfYear){
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
            }
        }
        Handler().postDelayed({
            mCloudUploadPresenter.upload(cloudList)
        },500)

    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList){
            val bookBean=BookGreenDaoManager.getInstance().queryBookByID(item.bookId)
            FileUtils.deleteFile(File(bookBean.bookPath))
            BookGreenDaoManager.getInstance().deleteBook(bookBean)
        }
        findData()
    }


}