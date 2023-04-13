package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class MessageSendDialog(private val context: Context,private val groups:MutableList<ClassGroup>) {

    private var dialog: Dialog?=null
    private var classGroup:ClassGroup?=null

    fun builder(): MessageSendDialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_message_send)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val tvOK = dialog?.findViewById<TextView>(R.id.tv_ok)
        val tvCancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val et_content = dialog?.findViewById<EditText>(R.id.et_content)
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)

        val mAdapter=MyAdapter(R.layout.item_message_classgroup,groups)
        rvList?.layoutManager=LinearLayoutManager(context)
        rvList?.adapter=mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            for (item in groups){
                item.isCheck=false
            }
            groups[position].isCheck=true
            classGroup=groups[position]
            mAdapter.notifyDataSetChanged()
        }

        tvCancel?.setOnClickListener { dismiss() }
        tvOK?.setOnClickListener {
            val contentStr=et_content?.text.toString()
            if (contentStr.isNotEmpty()&&classGroup!=null)
            {
                dismiss()
                listener?.onSend(contentStr,classGroup!!)
            }
        }

        return this
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onSend(contentStr:String,classGroup: ClassGroup)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

     class MyAdapter(layoutResId:Int,classs:MutableList<ClassGroup>):BaseQuickAdapter<ClassGroup,BaseViewHolder>(layoutResId,classs){
         override fun convert(helper: BaseViewHolder, item: ClassGroup?) {
             helper.setText(R.id.tv_class_name,item?.subject+" "+item?.teacher )
             helper.setChecked(R.id.cb_check,item?.isCheck!!)
         }
     }

}