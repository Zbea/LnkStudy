package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
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
    private var books= mutableListOf<BookBean>()
    private var pageIndex=1
    private var pageTotal=1

    override fun layoutId(): Int {
        return R.layout.ac_bookcase_mycollect
    }

    override fun initData() {

    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setPageTitle("我的收藏")

        initRecyclerView()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1
                findData()
            }
        }

        findData()
    }


    private fun initRecyclerView(){
        mAdapter=BookAdapter(R.layout.item_book_type, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@BookCollectActivity,4)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_book_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this@BookCollectActivity,23f),DP2PX.dip2px(this@BookCollectActivity,35f)))
            setOnItemClickListener { adapter, view, position ->
                gotoBookDetails(books[position].bookId)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                cancel(books[position])
            }
        }

    }

    //取消收藏
    private fun cancel(book:BookBean): Boolean {
        CommonDialog(this).setContent("确认取消收藏？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                book?.isCollect=false
                BookGreenDaoManager.getInstance().insertOrReplaceBook(book) //删除本地数据库
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
        books = BookGreenDaoManager.getInstance().queryAllBook(true, pageIndex, 12)
        val total = BookGreenDaoManager.getInstance().queryAllBook(true)
        pageTotal = ceil(total.size.toDouble() / 12).toInt()
        mAdapter?.setNewData(books)
        tv_page_current.text = pageIndex.toString()
        tv_page_total.text = pageTotal.toString()
        ll_page_number.visibility=if (pageTotal==0) View.GONE else View.VISIBLE
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