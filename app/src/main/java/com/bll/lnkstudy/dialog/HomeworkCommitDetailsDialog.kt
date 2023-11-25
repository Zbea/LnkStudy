package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkCommitDetailsDialog(val context: Context,val type:Int) {

    private var dialog:Dialog?=null
    private var mAdapter:CommitAdapter?=null

    fun builder(): HomeworkCommitDetailsDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_homework_commit_details)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams?.width=DP2PX.dip2px(context,750f)
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,750f))/2
        dialog?.show()

        val list=HomeworkDetailsDaoManager.getInstance().queryAllByType(type)

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        recyclerview.layoutManager = LinearLayoutManager(context)
        mAdapter= CommitAdapter(R.layout.item_homework_commit,type, list)
        recyclerview.adapter = mAdapter
        recyclerview.addItemDecoration(SpaceItemDeco(20,false))

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


    class CommitAdapter(layoutResId: Int,val type: Int, data: List<HomeworkDetailsBean>) : BaseQuickAdapter<HomeworkDetailsBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item:HomeworkDetailsBean) {
            helper.setText(R.id.tv_title,item.content)
            helper.setText(R.id.tv_type,item.homeworkTypeStr)
            helper.setText(R.id.tv_date, DateUtils.longToStringWeek(item.time))
        }

    }

}