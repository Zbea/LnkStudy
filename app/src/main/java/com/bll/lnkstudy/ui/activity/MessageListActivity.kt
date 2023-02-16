package com.bll.lnkstudy.ui.activity

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_page_number.*

class MessageListActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<MessageList>()
    private var mAdapter:MessageAdapter?=null
    private var pageIndex=1 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        lists= DataBeanManager.message
    }

    override fun initView() {
        setPageTitle("消息中心")

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageAdapter(R.layout.item_message, null)
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,50f),
            DP2PX.dip2px(this,20f),
            DP2PX.dip2px(this,50f),20)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            lists[position].isLook=true
            mAdapter?.notifyDataSetChanged()
            MessageDetailsDialog(this,getCurrentScreenPos(), lists[position]).builder()
        }
        pageNumberView()

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=lists.size //全部数量
        var pageNum=12
        var pageCount=Math.ceil((pageTotal.toDouble()/pageNum)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
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
        mAdapter?.setNewData(lists)
        tv_page_current.text=pageIndex.toString()
    }

}