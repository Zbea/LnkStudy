package com.bll.lnkstudy.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 课本
 */
class TextbookFragment : BaseFragment(){

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var typeId=0
    private var textBook=""//用来区分课本类型
    private var position=0
    private val bookGreenDaoManager=BookGreenDaoManager.getInstance()

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        pageSize=9
        EventBus.getDefault().register(this)
        setTitle(R.string.main_textbook_title)

        initTab()
        initRecyclerView()

        fetchData()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){
        val tabStrs= DataBeanManager.textbookType
        textBook=tabStrs[typeId]
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i ,tabStrs[i],tabStrs.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId=id
            textBook=tabStrs[id]
            pageIndex=1
            fetchData()
        }
    }

    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_textbook, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3,DP2PX.dip2px(activity,33f),38))
            setOnItemClickListener { adapter, view, position ->
                gotoBookDetails(books[position].bookId)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@TextbookFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick(){
        val book=books[position]
        val type=if (typeId==3) 2 else 1
        BookManageDialog(requireActivity(),screenPos,type,book).builder()
            .setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
                override fun onCollect() {
                }
                override fun onDelete() {
                    delete()
                }
                override fun onLock() {
                    book.isLock=!book.isLock
                    bookGreenDaoManager.insertOrReplaceBook(book)
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
                val book=books[position]
                bookGreenDaoManager.deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                books.remove(book)
                mAdapter?.remove(position)
            }
        })
    }

    /**
     * 移除教材到往期课本
     */
    private fun moveTextbook(){
        //获取所有往期教材
        val oldBooks=bookGreenDaoManager.queryAllTextBookOld()
        //加锁的往期教材不删除
        val it=oldBooks.iterator()
        if (it.hasNext()){
            val item=it.next()
            if (item.isLock){
                it.remove()
            }
        }
        //删除现在所有所有往期教材
        bookGreenDaoManager.deleteBooks(oldBooks)
        //删除所有往期教材的图片文件
        for (item in oldBooks){
            FileUtils.deleteFile(File(item.bookPath))
        }

        //所有教材更新为往期教材
        val items=bookGreenDaoManager.queryAllTextBookOther()
        for (item in items){
            item.dateState=1
        }
        bookGreenDaoManager.insertOrReplaceBooks(items)
        if (typeId!=3){
            books.clear()
            mAdapter?.notifyDataSetChanged()
        }
        else{
            pageIndex=1
            fetchData()
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            fetchData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        var total =0
        if (typeId == 3){
            total = bookGreenDaoManager.queryAllTextBookOld().size
            books = bookGreenDaoManager.queryAllTextBookOld(pageIndex, pageSize)
        }
        else{
            books = bookGreenDaoManager.queryAllTextBook(textBook, pageIndex, pageSize)
            total = bookGreenDaoManager.queryAllTextBook(textBook).size
        }
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }
}