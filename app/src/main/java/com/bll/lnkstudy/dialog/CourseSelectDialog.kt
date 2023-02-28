package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.ui.adapter.CourseAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco

/**
 * 课程表 课程选择
 */
class CourseSelectDialog(val context: Context){

    fun builder(): CourseSelectDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_course)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val courses= DataBeanManager.courses

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter = CourseAdapter(R.layout.item_course, courses)
        rvList.layoutManager = GridLayoutManager(context,2)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(2,30))
        mAdapter.setOnItemClickListener { _, view, position ->
            onClickListener?.onSelect(courses[position])
            dialog.dismiss()
        }

        return this
    }


    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSelect(course: String)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}