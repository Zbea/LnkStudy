package com.bll.lnkstudy.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.ui.adapter.CourseAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco2

/**
 * 课程表 课程选择
 */
class CourseSelectDialog(val context: Context){

    fun builder(): CourseSelectDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_course, null)
        val dialog = AlertDialog.Builder(ContextThemeWrapper(context, R.style.styleDialogCustom)).create()
        dialog.setView(view)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = DP2PX.dip2px(context,400F)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val courses=DataBeanManager.getIncetance().courses

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        var mAdapter = CourseAdapter(R.layout.item_course, courses)
        rvList.layoutManager = GridLayoutManager(context,2)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco2(0,30))
        mAdapter.setOnItemClickListener { _, view, position ->
            onClickListener?.onSelect(courses[position])
            dialog.dismiss()
        }

        return this
    }


    private var onClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onSelect(course: CourseBean)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}