package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.util.Log
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
import com.bll.lnkstudy.mvp.model.BookStoreType
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
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
    private var pos=0 //当前书籍位置
    private var book:Book?=null
    private var isDown=false //是否向下打开

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
            pos=position
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

    //获取tab数据
    private fun getTabDatas(isDown: Boolean):List<BookStoreType>{
        val types= mutableListOf<BookStoreType>()
        val strings=DataBeanManager.getIncetance().bookType
        if (isDown){
            for (i in strings.indices){
                var bookStoreType=BookStoreType()
                bookStoreType.title=strings[i]
                bookStoreType.type=i
                bookStoreType.isCheck=i==type
                types.add(bookStoreType)
            }
        }
        else{
            for (i in 0..6){
                var bookStoreType=BookStoreType()
                bookStoreType.title=strings[i]
                bookStoreType.type=i
                if(type>6)
                    type=0
                bookStoreType.isCheck=i==type
                types.add(bookStoreType)
            }
        }
        return types
    }

    //设置tab
    private fun initTab(){
        var bookStoreTypes=getTabDatas(false)

        rv_type.layoutManager = GridLayoutManager(activity,7)//创建布局管理
        var mAdapterType = BookCaseTypeAdapter(R.layout.item_bookcase_type, bookStoreTypes)
        rv_type.adapter = mAdapterType
        mAdapterType?.bindToRecyclerView(rv_type)
        mAdapterType?.setOnItemClickListener { adapter, view, position ->
            mAdapterType?.getItem(type)?.isCheck=false
            type=position
            mAdapterType?.getItem(type)?.isCheck=true
            mAdapterType?.notifyDataSetChanged()
            findData()
        }

        iv_down.setOnClickListener {
            if (isDown){
                isDown=false
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_down)
            }
            else{
                isDown=true
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_up)
            }
            bookStoreTypes=getTabDatas(isDown)
            mAdapterType?.setNewData(bookStoreTypes)
            findData()
        }

    }

    /**
     * 查找本地书籍
     */
    private fun findData(){
        booksAll=BookGreenDaoManager.getInstance(activity).queryAllBook("0",type.toString())
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
                books[pos].isCollect=true
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
        CommonDialog(activity).setContent("确认删除该书籍？").builder().setDialogClickListener(object :
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