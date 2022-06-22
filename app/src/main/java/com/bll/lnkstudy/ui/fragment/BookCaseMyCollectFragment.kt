package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.AFTER_SCHOOL_EVENT
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_bookcase_mycollect.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 书架收藏
 */
class BookCaseMyCollectFragment: BaseFragment() {

    private val ARG_TYPE="TYPE"

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var book:Book?=null
    private var type=1//类型1书籍
    private var booksAll= mutableListOf<Book>()//所有数据
    private var bookMap=HashMap<Int,MutableList<Book>>()//将所有数据按12个分页
    private var pageIndex=1

    fun newInstance(index: Int): BookCaseMyCollectFragment? {
        val fragment= BookCaseMyCollectFragment()
        val args = Bundle()
        args.putInt(ARG_TYPE, index)
        fragment.arguments = args
        return fragment
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase_mycollect
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setPageTitle("我的收藏")
        type= arguments?.getInt(ARG_TYPE,0)!!

        initRecyclerView()

        findData()

        ivBack?.setOnClickListener {
            (activity as BaseActivity).popToStack(BookCaseMyCollectFragment())
        }
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(0,55))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity, BookDetailsActivity::class.java).putExtra("book_id",books[position].id))
        }
        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            book=books[position]
            cancel()
        }
    }

    //取消收藏
    private fun cancel(): Boolean {
        CommonDialog(activity).setContent("确认取消收藏？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                book?.isCollect=false
                BookGreenDaoManager.getInstance(activity).insertOrReplaceBook(book) //删除本地数据库
                books.remove(book)
                mAdapter?.notifyDataSetChanged()

                when(type){
                    1->{
                        EventBus.getDefault().post(BOOK_EVENT)
                    }
                    2->{
                        EventBus.getDefault().post(TEXT_BOOK_EVENT)
                    }
                    3->{
                        EventBus.getDefault().post(AFTER_SCHOOL_EVENT)
                    }
                    else ->{

                    }
                }

            }
        })
        return true
    }


    /**
     * 查找本地书籍
     */
    private fun findData(){
        var booksAlls=if (type==1){
            BookGreenDaoManager.getInstance(activity).queryAllBook("1",true)
        }
        else if (type==2){
            BookGreenDaoManager.getInstance(activity).queryAllBook("1",true)
        }
        else if (type==3){
            BookGreenDaoManager.getInstance(activity).queryAllBook("1",true)
        }
        else{
            BookGreenDaoManager.getInstance(activity).queryAllBook("1",true)
        }
        booksAll.clear()
        for (i in 0..15)
        {
            booksAll.addAll(booksAlls)
        }
        pageNumberView()
    }

    //翻页处理
    private fun pageNumberView(){
        bookMap.clear()
        pageIndex=1
        var pageTotal=booksAll.size
        showLog("size:$pageTotal")
        var pageCount=Math.ceil((pageTotal.toDouble()/12)).toInt()
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
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