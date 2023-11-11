package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.dialog.MessageSendDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.MessageAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class MessageListActivity:BaseAppCompatActivity(),IContractView.IMessageView {

    private var mMessagePresenter= MessagePresenter(this)
    private var messages= mutableListOf<MessageList.MessageBean>()
    private var mAdapter:MessageAdapter?=null
    private var groups= mutableListOf<ClassGroup>()

    override fun onList(message: MessageList) {
        setPageNumber(message.total)
        messages=message.list
        mAdapter?.setNewData(messages)
    }

    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        pageIndex=1
        fetchData()
        EventBus.getDefault().post(Constants.MESSAGE_COMMIT_EVENT)
    }


    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        for (item in DataBeanManager.classGroups()){
            if (item.status==1){
                groups.add(item)
            }
        }
        pageSize=10
        fetchData()
    }

    override fun initView() {
        setPageTitle(R.string.message_title_str)
        if (groups.size>0){
            setPageSetting(R.string.send)
        }

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

        tv_setting.setOnClickListener {
            MessageSendDialog(this,groups).builder()?.setOnClickListener { contentStr, classGroup ->
                val map=HashMap<String,Any>()
                map["title"]=contentStr
                map["userId"]=classGroup.teacherId
                map["id"]=classGroup.classId
                mMessagePresenter.commitMessage(map)
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