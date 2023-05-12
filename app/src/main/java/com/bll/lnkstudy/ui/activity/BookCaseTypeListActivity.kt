package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_bookcase_type_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 书架分类
 */
class BookCaseTypeListActivity : BaseAppCompatActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<BookBean>()
    private var typePos = 0
    private var typeStr = ""//当前分类
    private var pos = 0 //当前书籍位置
    private var book: BookBean? = null
    private var bookNameStr = ""

    override fun layoutId(): Int {
        return R.layout.ac_bookcase_type_list
    }

    override fun initData() {
    }

    override fun initView() {
        pageSize = 12
        EventBus.getDefault().register(this)

        setPageTitle(R.string.book_type_title)
        showSearchView(true)

        et_search.addTextChangedListener {
            bookNameStr = it.toString()
            if (bookNameStr.isNotEmpty()) {
                pageIndex = 1
                fetchData()
            }
        }

        initTab()

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_book_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(
                    4,
                    DP2PX.dip2px(this@BookCaseTypeListActivity, 22f),
                    DP2PX.dip2px(this@BookCaseTypeListActivity, 35f)
                )
            )
            setOnItemClickListener { adapter, view, position ->

            }
            onItemLongClickListener =
                BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                    pos = position
                    book = books[position]
                    onLongClick()
                }
        }

        fetchData()
    }

    //设置tab
    @SuppressLint("NotifyDataSetChanged")
    private fun initTab() {
        val types = mutableListOf<ItemList>()
        val strings = DataBeanManager.bookType
        for (i in strings.indices) {
            val item = ItemList()
            item.name = strings[i]
            item.isCheck = i == 0
            types.add(item)
        }
        typeStr = types[0].name

        rv_type.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        BookCaseTypeAdapter(R.layout.item_bookcase_type, types).apply {
            rv_type.adapter = this
            bindToRecyclerView(rv_type)
            rv_type.addItemDecoration(SpaceGridItemDeco1(7, DP2PX.dip2px(this@BookCaseTypeListActivity, 14f)
                , DP2PX.dip2px(this@BookCaseTypeListActivity, 16f)))
            setOnItemClickListener { adapter, view, position ->
                getItem(typePos)?.isCheck = false
                typePos = position
                getItem(typePos)?.isCheck = true
                typeStr = types[typePos].name
                notifyDataSetChanged()
                bookNameStr = ""//清除搜索标记
                pageIndex = 1
                fetchData()
            }
        }
    }


    //长按显示课本管理
    private fun onLongClick(): Boolean {
        BookManageDialog(this, screenPos, 1, book!!).builder()
            .setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
                override fun onCollect() {
                }
                override fun onDelete() {
                    delete()
                }
                override fun onLock() {
                }
            })

        return true
    }

    //删除书架书籍
    private fun delete() {
        CommonDialog(this, screenPos).setContent(R.string.item_is_delete_tips).builder()
            .setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
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
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        hideKeyboard()
        var total = mutableListOf<BookBean>()
        //判断是否是搜索
        if (bookNameStr.isEmpty()) {
            books = BookGreenDaoManager.getInstance()
                .queryAllBook(typeStr, pageIndex, Constants.PAGE_SIZE)
            total = BookGreenDaoManager.getInstance().queryAllBook(typeStr)
        } else {
            books = BookGreenDaoManager.getInstance()
                .queryAllName(bookNameStr, typeStr, pageIndex, Constants.PAGE_SIZE)
            total = BookGreenDaoManager.getInstance().queryAllName(bookNameStr, typeStr)
        }

        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }


}