package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.utilssdk.utils.FileUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_bookcase_type.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 书架分类
 */
class BookCaseTypeFragment: BaseFragment() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var type=0//当前分类
    private var booksAll= mutableListOf<Book>()//所有数据
    private var bookMap=HashMap<Int,MutableList<Book>>()//将所有数据按12个分页
    private var pageIndex=1
    private var position=0
    private var book:Book?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase_type
    }

    override fun initView() {
        EventBus.getDefault().register(this)

        setPageTitle("分类展示")

        initTab()

        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(60,60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity, BookDetailsActivity::class.java).putExtra("book_id",books[position].id))
        }
        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            this.position=position
            book=books[position]
            onLongClick()
        }

        findData()

        ivBack?.setOnClickListener {
            (activity as BaseActivity).popToStack(BookCaseTypeFragment())
        }
    }

    override fun lazyLoad() {
    }

    //设置tab
    private fun initTab(){
        val strings=DataBeanManager.getIncetance().kmArray
        for (str in strings){
            var tab=xt_type?.newTab()
            tab?.text = str
            xt_type?.addTab(tab!!)
        }
        xt_type.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                type= tab?.position!!
                findData()
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }
        })

        btn_arrow_left.setOnClickListener {
            var pos=xt_type?.selectedTabPosition!!
            if (pos>0){
                type=pos-1
                xt_type?.getTabAt(type)?.select()
            }
        }
        btn_arrow_right.setOnClickListener {
            var pos=xt_type?.selectedTabPosition!!
            if (pos<strings.size-1){
                type=pos+1
                xt_type?.getTabAt(type)?.select()
            }
        }

    }

    /**
     * 查找本地书籍
     */
    private fun findData(){
        booksAll=BookGreenDaoManager.getInstance(activity).queryAllBook("1",type.toString())
        pageNumberView()
    }

    //翻页处理
    private fun pageNumberView(){
        bookMap.clear()
        pageIndex=1
        var pageTotal=booksAll.size
        var pageCount=Math.ceil((pageTotal.toDouble()/12)).toInt()
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            books= mutableListOf()
            mAdapter?.setNewData(books)
            return
        }

        var toIndex=9
        for(i in 0 until pageCount){
            var index=i*9
            if(index+9>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
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
        ll_page_number.visibility= View.VISIBLE
    }

    //长按显示课本管理
    private fun onLongClick(): Boolean {
        val dialogManager= BookManageDialog(requireActivity(),0,book!!)
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