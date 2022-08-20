package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.ui.adapter.AccountVipAdapter

class AccountBuyVipDialog(val context: Context, val list: List<AccountList.ListBean>) {

    private var dialog:Dialog?=null
    private var vipID=0

    fun builder(): AccountBuyVipDialog? {

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialog_account_vip)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        val btn_ok = dialog!!.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog!!.findViewById<Button>(R.id.btn_cancel)

        recyclerview.layoutManager = LinearLayoutManager(context)
        var vipAdapter = AccountVipAdapter(R.layout.item_account_vip, list)
        recyclerview.adapter = vipAdapter
        vipAdapter.setOnItemClickListener { adapter, view, position ->
            vipAdapter?.setItemView(position)
            vipID= list[position].id
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_ok.setOnClickListener {
            dismiss()
            if (listener!=null)
                listener?.onClick(vipID.toString())
        }

        if (list.isNotEmpty()) {
            vipID = list[0].id
        }
        return this
    }

    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick(id:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}