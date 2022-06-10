package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.utilssdk.utils.FileUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_bookcase.*
import kotlinx.android.synthetic.main.fragment_bookcase.rv_list
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var position=0
    private var book:Book?=null
    private var pageIndex=1 //当前页码
    private var booksAll= mutableListOf<Book>()//所有数据
    private var bookMap=HashMap<Int,MutableList<Book>>()//将所有数据按12个分页

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {

        EventBus.getDefault().register(this)

        setPageTitle("书架")
        setDisBackShow()
        setMyCollect()

        initRecyclerView()
        findData()

        tv_type.setOnClickListener {
            (activity as BaseActivity).navigationToFragment(BookCaseTypeFragment())
        }

        tvMyCollect?.setOnClickListener {
            var fragment=BookCaseMyCollectFragment().newInstance(1)
            (activity as BaseActivity).navigationToFragment(fragment)
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
        rv_list?.addItemDecoration(SpaceGridItemDeco(0,30))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity,BookDetailsActivity::class.java).putExtra("book_id",books[position].id))
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
        var booksAlls=BookGreenDaoManager.getInstance(activity).queryAllBook("1")
        booksAll.clear()
        for (i in 0..15)
        {
            booksAll.addAll(booksAlls)
        }
        pageNumberView()
        onChangeTopView()
    }

    //翻页处理
    private fun pageNumberView(){
        bookMap.clear()
        pageIndex=1
        var pageTotal=booksAll.size //全部数量
        var pageCount=Math.ceil((pageTotal.toDouble()/12)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        var toIndex=12
        for(i in 0 until pageCount){
            var index=i*12
            if(index+12>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = booksAll.subList(index,index+toIndex)
            bookMap[i+1]=newList
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                upDateUI()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                upDateUI()
            }
        }

    }

    //刷新数据
    private fun upDateUI()
    {
        books= bookMap[pageIndex]!!
        mAdapter?.setNewData(books)
        tv_page_current.text=pageIndex.toString()
    }

    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (books.size==0){
            ll_topcontent.visibility= View.GONE
        }
        else{
            ll_topcontent.visibility=View.VISIBLE
            var book=books[0]
            tv_top_page.text=""+book.pageIndex+"页"
            Glide.with(this)
                .load(book.assetUrl)
                .thumbnail(0.1f).centerCrop().into(iv_content1)
            if (book.pageIndex==1)
            {
                Glide.with(this).load(book.assetUrl)
                    .thumbnail(0.1f).centerCrop().into(iv_content2)
            }
            else{
                Glide.with(this)
                    .load(book.pageUrl)
                    .thumbnail(0.1f).centerCrop().into(iv_content2)
            }

        }


    }

    //长按显示课本管理
    private fun onLongClick(): Boolean {
        val dialogManager=BookManageDialog(requireActivity(),0,book!!)
        dialogManager.builder().setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
            override fun onCollect() {
                book?.isCollect=true
                books[position].isCollect=true
                mAdapter?.notifyDataSetChanged()
                BookGreenDaoManager.getInstance(activity).insertOrReplaceBook(book)
                showToast("收藏成功")
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
        CommonDialog(activity).setContent("确认删除该书籍？").builder().setDialogClickListener(object : CommonDialog.DialogClickListener {
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