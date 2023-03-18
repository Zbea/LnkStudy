package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkCommitDetailsDialog(val context: Context,val screenPos:Int, val list: List<HomeworkMessage>) {

    private var dialog:Dialog?=null
    private var mAdapter:CommitAdapter?=null

    fun builder(): HomeworkCommitDetailsDialog? {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_homework_commit_details)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams?.width=DP2PX.dip2px(context,750f)
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,750f))/2
        }
        dialog?.show()

        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)

        recyclerview.layoutManager = LinearLayoutManager(context)
        mAdapter= CommitAdapter(R.layout.item_homework_commit, list)
        recyclerview.adapter = mAdapter
        recyclerview.addItemDecoration(SpaceItemDeco(0,0,0,20,))

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


    class CommitAdapter(layoutResId: Int, data: List<HomeworkMessage>) : BaseQuickAdapter<HomeworkMessage, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkMessage) {
            helper.setText(R.id.tv_title,item.title)
            helper.setText(R.id.tv_date, DateUtils.longToStringNoYear(item.date))
            helper.setText(R.id.tv_state,if (item.state==0) "未提交" else "已提交")
            helper.setTextColor(R.id.tv_state,if (item.state==0)
                mContext.resources.getColor(R.color.black) else mContext.resources.getColor(R.color.gray))
        }

    }

}