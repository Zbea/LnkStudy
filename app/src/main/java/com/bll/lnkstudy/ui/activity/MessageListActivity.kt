package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.mvp.model.Message
import com.bll.lnkstudy.mvp.model.MessageBean
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*

class MessageListActivity:BaseAppCompatActivity(),IContractView.IMessageView {

    private var mMessagePresenter= MessagePresenter(this)
    private var messages= mutableListOf<MessageBean>()
    private var mAdapter:MessageAdapter?=null

    override fun onList(message: Message) {
        setPageNumber(message.total)
        messages=message.list
        mAdapter?.setNewData(messages)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=10
        fetchData()
    }

    override fun initView() {
        setPageTitle(R.string.message_title_str)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageAdapter(1,R.layout.item_message, null).apply {
            val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(
                DP2PX.dip2px(this@MessageListActivity,50f),
                DP2PX.dip2px(this@MessageListActivity,20f),
                DP2PX.dip2px(this@MessageListActivity,50f),20)
            layoutParams.weight=1f
            rv_list.layoutParams= layoutParams
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                MessageDetailsDialog(this@MessageListActivity,getCurrentScreenPos(), messages[position]).builder()
            }
        }

    }


    override fun fetchData() {
        val map= HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["type"]=2
        mMessagePresenter.getList(map,true)
    }

}