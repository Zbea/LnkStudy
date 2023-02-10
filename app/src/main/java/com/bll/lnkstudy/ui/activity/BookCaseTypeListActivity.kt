package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_bookcase_type_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import kotlin.math.ceil

/**
 * 书架分类
 */
class BookCaseTypeListActivity: BaseAppCompatActivity() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var typePos=0
    private var typeStr=""//当前分类
    private var pageIndex=1
    private var pageTotal=1
    private var pos=0 //当前书籍位置
    private var book: BookBean?=null

    override fun layoutId(): Int {
        return R.layout.ac_bookcase_type_list
    }

    override fun initData() {
    }

    override fun initView() {
        EventBus.getDefault().register(this)

        setPageTitle("分类展示")
        showSearchView(true)

        initTab()

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),DP2PX.dip2px(this,35f)))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoBookDetails(books[position].bookId)
        }
        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            pos=position
            book=books[position]
            onLongClick()
        }

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

    //设置tab
    private fun initTab(){
        val types= mutableListOf<BaseTypeBean>()
        val strings= DataBeanManager.getIncetance().bookType
        for (i in strings.indices){
            var baseTypeBean= BaseTypeBean()
            baseTypeBean.name=strings[i]
            baseTypeBean.typeId=i
            baseTypeBean.isCheck=i==0
            types.add(baseTypeBean)
        }
        typeStr=types[0].name

        rv_type.layoutManager = GridLayoutManager(this,7)//创建布局管理
        var mAdapterType = BookCaseTypeAdapter(R.layout.item_bookcase_type, types)
        rv_type.adapter = mAdapterType
        mAdapterType?.bindToRecyclerView(rv_type)
        rv_type.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,14f),DP2PX.dip2px(this,16f)))
        mAdapterType?.setOnItemClickListener { adapter, view, position ->
            mAdapterType?.getItem(typePos)?.isCheck=false
            typePos=position
            mAdapterType?.getItem(typePos)?.isCheck=true
            typeStr=types[typePos].name
            mAdapterType?.notifyDataSetChanged()
            pageIndex=1
            findData()
        }
    }

    /**
     * 查找本地书籍
     */
    private fun findData(){
        books=BookGreenDaoManager.getInstance().queryAllBook(typeStr,pageIndex,Constants.PAGE_SIZE)
        val total=BookGreenDaoManager.getInstance().queryAllBook(typeStr)
        pageTotal= ceil((total.size.toDouble()/Constants.PAGE_SIZE)).toInt()
        mAdapter?.setNewData(books)
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageTotal.toString()
        ll_page_number.visibility=if (total.size==0) View.GONE else View.VISIBLE
    }

    //长按显示课本管理
    private fun onLongClick(): Boolean {
        val dialogManager= BookManageDialog(this,screenPos,0,book!!)
        dialogManager.builder().setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
            override fun onCollect() {
                book?.isCollect=true
                books[pos].isCollect=true
                mAdapter?.notifyDataSetChanged()
                BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
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
        CommonDialog(this,screenPos).setContent("确认删除该书籍？").builder().setDialogClickListener(object :
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
        if (msgFlag==BOOK_EVENT){
            findData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}