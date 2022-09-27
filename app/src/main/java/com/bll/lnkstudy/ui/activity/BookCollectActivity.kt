package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco4
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_bookcase_mycollect.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil

/**
 * 书架收藏
 */
class BookCollectActivity: BaseAppCompatActivity() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var book:Book?=null
    private var booksAll= mutableListOf<Book>()//所有数据
    private var bookMap=HashMap<Int,MutableList<Book>>()//将所有数据按12个分页
    private var pageIndex=1


    override fun layoutId(): Int {
        return R.layout.ac_bookcase_mycollect
    }

    override fun initData() {

    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setPageTitle("我的收藏")

        initRecyclerView()

        findData()

    }


    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco4(50,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            var intent=Intent(this,BookDetailsActivity::class.java)
//            intent.putExtra(Intent.EXTRA_LAUNCH_SCREEN, Intent.EXTRA_LAUNCH_SCREEN_PANEL_BOTH)
            intent.putExtra("book_id",books[position].id)
            startActivity(intent)
        }
        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            book=books[position]
            cancel()
        }
    }

    //取消收藏
    private fun cancel(): Boolean {
        CommonDialog(this).setContent("确认取消收藏？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                book?.isCollect=false
                BookGreenDaoManager.getInstance(this@BookCollectActivity).insertOrReplaceBook(book) //删除本地数据库
                books.remove(book)
                mAdapter?.notifyDataSetChanged()

                EventBus.getDefault().post(BOOK_EVENT)

            }
        })
        return true
    }


    /**
     * 查找本地书籍
     */
    private fun findData(){
        booksAll=BookGreenDaoManager.getInstance(this).queryAllBook(true)
        pageNumberView()
    }

    //翻页处理
    private fun pageNumberView(){
        bookMap.clear()
        pageIndex=1
        var pageTotal=booksAll.size
        var toIndex=12
        var pageCount= ceil(pageTotal.toDouble()/toIndex).toInt()
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            return
        }

        for(i in 0 until pageCount){
            var index=i*toIndex
            if(index+toIndex>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
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