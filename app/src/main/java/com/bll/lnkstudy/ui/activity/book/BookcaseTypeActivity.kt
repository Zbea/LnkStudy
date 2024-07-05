package com.bll.lnkstudy.ui.activity.book

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 书架分类
 */
class BookcaseTypeActivity : BaseAppCompatActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<BookBean>()
    private var typePos = 0
    private var typeStr = ""//当前分类
    private var pos = 0 //当前书籍位置
    private var longItems= mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_book_type_list
    }

    override fun initData() {
        pageSize = 12
        longItems.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
    }

    override fun initView() {
        setPageTitle(R.string.book_type_title)

        initTab()

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4,
                    DP2PX.dip2px(this@BookcaseTypeActivity, 22f),
                    DP2PX.dip2px(this@BookcaseTypeActivity, 35f)
                )
            )
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(this@BookcaseTypeActivity,bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                    pos = position
                    delete()
                    true
                }
        }

        fetchData()
    }

    //设置tab
    @SuppressLint("NotifyDataSetChanged")
    private fun initTab() {
        val types =ItemTypeDaoManager.getInstance().queryAll(5)
        types[0].isCheck=true
        typeStr = types[0].title

        rv_type.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        BookCaseTypeAdapter(R.layout.item_bookcase_type, types).apply {
            rv_type.adapter = this
            bindToRecyclerView(rv_type)
            setOnItemClickListener { adapter, view, position ->
                getItem(typePos)?.isCheck = false
                typePos = position
                getItem(typePos)?.isCheck = true
                typeStr = types[typePos].title
                //修改当前分类状态
                ItemTypeDaoManager.getInstance().saveBookBean(5,typeStr,false)
                notifyDataSetChanged()
                pageIndex = 1
                fetchData()
            }
        }
    }

    //删除书架书籍
    private fun delete() {
        val book = books[pos]
        LongClickManageDialog(this,getCurrentScreenPos(),book.bookName,longItems).builder()
            .setOnDialogClickListener {
                BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                if (File(book.bookDrawPath).exists())
                    FileUtils.deleteFile(File(book.bookDrawPath))
                mAdapter?.remove(pos)
                EventBus.getDefault().post(BOOK_EVENT)
                //删除增量更新
                DataUpdateManager.deleteDateUpdate(6,book.bookId,1)
                //删除增量更新
                DataUpdateManager.deleteDateUpdate(6,book.bookId,2)
            }
    }

    override fun fetchData() {
        //判断是否是搜索
        books = BookGreenDaoManager.getInstance().queryAllBook(typeStr, pageIndex, pageSize)
        val total= BookGreenDaoManager.getInstance().queryAllBook(typeStr).size
        if (total==0)
            ItemTypeDaoManager.getInstance().saveBookBean(5,typeStr,false)
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }

}