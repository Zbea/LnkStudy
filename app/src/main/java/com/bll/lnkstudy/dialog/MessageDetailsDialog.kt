package com.bll.lnkstudy.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class MessageDetailsDialog(private val context: Context, private val messageList: MessageList) {

    private var dialog: AlertDialog?=null

    fun builder(): AlertDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_message_details, null)
        dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(DP2PX.dip2px(context, 480f),DP2PX.dip2px(context, 320f))

        val ivCancel = dialog?.findViewById<ImageView>(R.id.iv_close)
        ivCancel?.setOnClickListener { dialog?.dismiss() }
        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        val tvTime = dialog?.findViewById<TextView>(R.id.tv_time)
        val tvContent = dialog?.findViewById<TextView>(R.id.tv_content)
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)

        tvName?.text=messageList.name
        tvTime?.text=messageList.createTime
        tvContent?.text=messageList.content

        rvList?.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = MessageAdapter(R.layout.item_message_bean, messageList.messages)
        rvList?.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            CommonDialog(context).setContent("确定要删除这条信息？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    messageList.messages.removeAt(position)
                    mAdapter.setNewData(messageList.messages)
                }
            })
        }

        return dialog
    }

    class MessageAdapter(layoutResId: Int, data: List<MessageList.MessageBean>?) : BaseQuickAdapter<MessageList.MessageBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: MessageList.MessageBean) {
            helper.setText(R.id.tv_message_name,item.message)
        }

    }


}