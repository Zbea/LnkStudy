package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.PAGE_SIZE
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 书架收藏
 */
class BookCollectActivity: BaseAppCompatActivity() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<BookBean>()

    override fun layoutId(): Int {
        return R.layout.ac_bookcase_mycollect
    }

    override fun initData() {
        pageSize= PAGE_SIZE
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setPageTitle(R.string.book_collect_str)

        initRecyclerView()

        fetchData()
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
        CommonDialog(this).setContent(R.string.book_is_collect_tips).builder().setDialogClickListener(object :
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


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==BOOK_EVENT){
            fetchData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        books = BookGreenDaoManager.getInstance().queryAllBook(true, pageIndex, 12)
        val total = BookGreenDaoManager.getInstance().queryAllBook(true)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}