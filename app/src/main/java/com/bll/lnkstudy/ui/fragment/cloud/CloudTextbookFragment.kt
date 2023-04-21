package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_textbook.*

class CloudTextbookFragment:BaseFragment() {

    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var textBook=""//用来区分课本类型

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=9
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initTab(){
        val texts= DataBeanManager.textbookType.toMutableList()
        texts.removeLast()
        textBook=texts[0]
        for (i in texts.indices) {
            rg_group.addView(getRadioButton(i ,texts[i],texts.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            textBook=texts[id]
            pageIndex=1
            fetchData()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_book_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity,33f),38))
            setOnItemClickListener { adapter, view, position ->
                gotoBookDetails(books[position].bookId)
            }
        }
    }
}