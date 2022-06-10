package com.bll.lnkstudy.dialog

import android.content.Context
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bll.lnkstudy.R
import com.bll.lnkstudy.utils.DP2PX



class NoteAddDialog(private val context: Context) {

    fun builder(): NoteAddDialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_note_add, null)
        var dialog= AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog?.setView(view)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context, 700f)
        layoutParams.gravity = Gravity.CENTER
        window.attributes = layoutParams

        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)


        iv_cancel?.setOnClickListener { dialog?.dismiss() }


        return this
    }





    private var listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick()
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}