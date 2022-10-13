package com.bll.lnkstudy.ui.fragment

import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco4
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_bookcase.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    private var mAdapter: BookAdapter?=null
    private var position=0
    private var book:Book?=null
    private var books= mutableListOf<Book>()//所有数据

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {

        EventBus.getDefault().register(this)

        setTitle("书架")

        initRecyclerView()
        findData()

        tv_type.setOnClickListener {
            (activity as BaseAppCompatActivity).navigationToFragment(BookCaseTypeFragment())
        }

        ll_book_top.setOnClickListener {
            if (books.size>0){
                gotoBookDetails(books[0])
            }
        }

    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco4(63,28))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoBookDetails(books[position])
        }

        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            this.position=position
            book=books[position]
            onLongClick()
        }

    }



    /**
     * 查找本地书籍
     */
    private fun findData(){
        books=BookGreenDaoManager.getInstance(activity).queryAllBook("0")
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (books.size>0){
            var book=books[0]
            tv_top_page.text="${book.pageIndex+1}页"

            if (book.pageUpUrl==null){
                setImageUrl(book?.assetUrl,iv_content_up)
            }
            else{
                setImageUrl(book?.pageUpUrl,iv_content_up)
            }

            if (book.pageUrl==null){
                setImageUrl(book?.assetUrl,iv_content_down)
            }
            else{
                setImageUrl(book?.pageUrl,iv_content_down)
            }

        }
    }


    private fun setImageUrl(url: String,image:ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    //长按显示课本管理
    private fun onLongClick(): Boolean {
        val dialogManager=BookManageDialog(requireActivity(),screenPos,0,book!!)
        dialogManager.builder().setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
            override fun onCollect() {
                book?.isCollect=true
                books[position].isCollect=true
                mAdapter?.notifyDataSetChanged()
                BookGreenDaoManager.getInstance(activity).insertOrReplaceBook(book)
                showToast(screenPos,"收藏成功")
            }
            override fun onDelete() {
                delete()
            }
            override fun onMove() {
            }
        })

        return true
    }

    //删除书架书籍
    private fun delete(){
        CommonDialog(activity,screenPos).setContent("确认删除该书籍？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                BookGreenDaoManager.getInstance(activity).deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book?.bookPath))//删除下载的书籍资源
                books.remove(book)
                mAdapter?.notifyDataSetChanged()
                EventBus.getDefault().post(BOOK_EVENT)

            }
        })
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==BOOK_EVENT){
            findData()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}