package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.MessageSendDialog
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_setting
import org.greenrobot.eventbus.EventBus

class MessageListActivity:BaseAppCompatActivity(),IContractView.IMessageView {

    private lateinit var mMessagePresenter:MessagePresenter
    private var messages= mutableListOf<MessageList.MessageBean>()
    private var mAdapter:MessageAdapter?=null

    override fun onList(message: MessageList) {
        setPageNumber(message.total)
        messages=message.list
        mAdapter?.setNewData(messages)
    }

    override fun onCommitSuccess() {
        showToast("发送成功")
        pageIndex=1
        fetchData()
        EventBus.getDefault().post(Constants.MESSAGE_COMMIT_EVENT)
    }


    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        fetchData()
    }

    override fun initChangeScreenData() {
        mMessagePresenter= MessagePresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(R.string.message_title_str)
        setPageSetting(R.string.send)

        tv_setting.setOnClickListener {
            MessageSendDialog(this).builder()?.setOnClickListener{
                val map=HashMap<String,Any>()
                map["title"]=it
                mMessagePresenter.commitMessage(map)
            }
        }

        initRecyclerView()

    }
    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@MessageListActivity,50f),
            DP2PX.dip2px(this@MessageListActivity,40f),
            DP2PX.dip2px(this@MessageListActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageAdapter(R.layout.item_message, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
        }
    }

    override fun fetchData() {
        val map= HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["type"]=2
        mMessagePresenter.getList(map,true)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}