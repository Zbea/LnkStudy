package com.bll.lnkstudy.ui.activity.book

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.BOOK_TYPE_EVENT
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookcaseDetailsDialog
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.BookcaseTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.rv_list
import kotlinx.android.synthetic.main.ac_book_type_list.rv_type
import kotlinx.android.synthetic.main.common_title.tv_btn
import org.greenrobot.eventbus.EventBus

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
    private var mTabAdapter:BookcaseTypeAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_book_type_list
    }

    override fun initData() {
        pageSize = 12
        longItems.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })

        initTab()
    }

    override fun initView() {
        setPageTitle(R.string.book_type_title)
        showView(tv_btn)

        tv_btn.text="书架明细"
        tv_btn.setOnClickListener {
            BookcaseDetailsDialog(this).builder()
        }

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)

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
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(this, 35f)))

        fetchData()
    }


    private fun initTab() {
        itemTabTypes =ItemTypeDaoManager.getInstance().queryAll(5)
        if (itemTabTypes.size>0){
            for (item in itemTabTypes){
                item.isCheck=false
            }
            itemTabTypes[0].isCheck=true
            typeStr = itemTabTypes[0].title
        }

        rv_type.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        mTabAdapter=BookcaseTypeAdapter(R.layout.item_bookcase_type, itemTabTypes).apply {
            rv_type.adapter = this
            bindToRecyclerView(rv_type)
            setOnItemClickListener { adapter, view, position ->
                getItem(typePos)?.isCheck = false
                typePos = position
                getItem(typePos)?.isCheck = true
                typeStr = itemTabTypes[typePos].title
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
                MethodManager.deleteBook(book)
            }
    }

    override fun fetchData() {
        books = BookGreenDaoManager.getInstance().queryAllBook(typeStr, pageIndex, pageSize)
        val total= BookGreenDaoManager.getInstance().queryAllBook(typeStr).size
        if (total==0)
            ItemTypeDaoManager.getInstance().saveBookBean(5,typeStr,false)
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            BOOK_EVENT->{
                fetchData()
            }
            BOOK_TYPE_EVENT->{
                itemTabTypes =ItemTypeDaoManager.getInstance().queryAll(5)
                itemTabTypes[typePos].isCheck=true
                mTabAdapter?.setNewData(itemTabTypes)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(BOOK_EVENT)
    }
}