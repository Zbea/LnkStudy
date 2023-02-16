package com.bll.lnkstudy.ui.fragment

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil

/**
 * 课本
 */
class TextbookFragment : BaseFragment(){

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var textBook="我的课本"//用来区分课本类型
    private var pageIndex=1
    private var pageTotal=1

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("课本")

        initTab()
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

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){
        var tabStrs= DataBeanManager.textbookType
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i ,tabStrs[i],tabStrs.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            textBook=tabStrs[id]
            pageIndex=1
            findData()
        }
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(activity,33f),38))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoBookDetails(books[position].bookId)
        }

    }

    /**
     * 查找本地课本
     */
    private fun findData(){
        books = BookGreenDaoManager.getInstance().queryAllTextBook( textBook, pageIndex, 9)
        val total = BookGreenDaoManager.getInstance().queryAllTextBook(textBook)
        pageTotal = ceil((total.size.toDouble() / 9)).toInt()
        mAdapter?.setNewData(books)
        tv_page_current.text = pageIndex.toString()
        tv_page_total.text = pageTotal.toString()
        ll_page_number.visibility=if (pageTotal==0) View.GONE else View.VISIBLE
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            findData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}