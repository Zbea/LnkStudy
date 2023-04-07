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
    private var textBook=""//用来区分课本类型
    private var position=0

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
        textBook=tabStrs[0]
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i ,tabStrs[i],tabStrs.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
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
            setEmptyView(R.layout.common_book_empty)
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
        BookManageDialog(requireActivity(),screenPos,1,books[position]).builder()
            .setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
                override fun onCollect() {
                }
                override fun onDelete() {
                    delete()
                }
                override fun onMove() {
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
                BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                books.remove(book)
                mAdapter?.notifyDataSetChanged()
            }
        })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
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
        books = BookGreenDaoManager.getInstance().queryAllTextBook( textBook, pageIndex, 9)
        val total = BookGreenDaoManager.getInstance().queryAllTextBook(textBook)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }
}