package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.DateRepeat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


/**
 * 重要日子重复
 */
class RepeatDayDialog(val context: Context,val repeatStr: String,val type:Int){

    private val repeats= mutableListOf<DateRepeat>()

    fun builder(): RepeatDayDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_repeat_day, null)
        val dialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = 800
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams

        val strS=if (type==0) DataBeanManager.getIncetance().dateDayTitle else DataBeanManager.getIncetance().dateScheduleTitle
        for (str in strS ){
            var repeat=DateRepeat()
            repeat.title=str
            repeat.isShow=str==repeatStr
            repeats.add(repeat)
        }

        var rv_repeat=view.findViewById<RecyclerView>(R.id.rv_repeat)
        rv_repeat.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = RepeatAdapter(R.layout.item_date_repeat_title, repeats)
        rv_repeat.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_repeat)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            repeatListener?.getRepeat(repeats[position].title)
            dialog.dismiss()
        }
        var tv_cancel=view.findViewById<ImageView>(R.id.iv_close)
        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        return this
    }




    private var repeatListener: OnRepeatListener? = null

    fun interface OnRepeatListener {
        fun getRepeat(repeatStr: String?)
    }

    fun setDialogClickListener(repeatListener: OnRepeatListener?) {
        this.repeatListener = repeatListener
    }


    class RepeatAdapter(layoutResId: Int, data: List<DateRepeat>?) : BaseQuickAdapter<DateRepeat, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DateRepeat) {
            helper.setText(R.id.tv_title, item.title)
            helper.setVisible(R.id.iv_select,item.isShow)
//            helper.setVisible(R.id.v_line,helper.adapterPosition!=data.size-1)
        }

    }


}