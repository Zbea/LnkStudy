package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.AccountQdBean
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class WalletBuyXdDialog(val context: Context, val list: List<AccountQdBean>) {

    private var dialog:Dialog?=null
    private var id=0

    fun builder(): WalletBuyXdDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_account_xd)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        val btn_ok = dialog!!.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog!!.findViewById<TextView>(R.id.tv_cancel)
        val rb_wx = dialog!!.findViewById<RadioButton>(R.id.rb_wx)

        recyclerview.layoutManager = GridLayoutManager(context,4)//创建布局管理
        val mAdapter = AccountXdAdapter(R.layout.item_account_smoney, list)
        recyclerview.adapter = mAdapter
        recyclerview.addItemDecoration(SpaceGridItemDeco(4,40))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            mAdapter.setItemView(position)
            id= list[position].id
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_ok.setOnClickListener {
            dismiss()
            val payType=if (rb_wx.isChecked)  2  else  1
            if (listener!=null)
                listener?.onClick(payType,id.toString())
        }

        if (list.isNotEmpty()) {
            id = list[0].id
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

    fun interface OnDialogClickListener {
        fun onClick(payType:Int,id:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class AccountXdAdapter(layoutResId: Int, data: List<AccountQdBean>?) : BaseQuickAdapter<AccountQdBean, BaseViewHolder>(layoutResId, data) {

        var mPosition = 0

        override fun convert(helper: BaseViewHolder, item: AccountQdBean) {
            helper.setText(R.id.tv_name,item.amount.toString())
            if (helper.adapterPosition==mPosition){
                helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_black_solid_5dp_corner)
                helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.white) )
            }
            else{
                helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_black_stroke_5dp_corner)
                helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
            }
        }

        fun setItemView(position: Int) {
            mPosition=position
            notifyDataSetChanged()
        }

    }

}