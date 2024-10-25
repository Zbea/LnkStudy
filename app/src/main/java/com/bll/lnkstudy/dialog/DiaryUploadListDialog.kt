package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class DiaryUploadListDialog(val context: Context) {

    fun builder(): DiaryUploadListDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_diary_upload_list)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =dialog.window?.attributes!!
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val diaryTypes=ItemTypeDaoManager.getInstance().queryAllOrderDesc(6)

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_diary_upload, diaryTypes)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setEmptyView(R.layout.common_empty)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(diaryTypes[position].typeId)
            dialog.dismiss()
        }
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                CommonDialog(context).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            val item=diaryTypes[position]
                            val diaryBeans=DiaryDaoManager.getInstance().queryList(item.typeId)
                            for (diaryBean in diaryBeans){
                                val path= FileAddress().getPathDiary(DateUtils.longToStringCalender(diaryBean.date))
                                FileUtils.deleteFile(File(path))
                                DiaryDaoManager.getInstance().delete(diaryBean)
                            }
                            ItemTypeDaoManager.getInstance().deleteBean(item)
                            mAdapter.remove(position)
                        }
                    })
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(context,10f)))

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(typeId:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
            helper.setText(R.id.tv_name,item.title)
            helper.addOnClickListener(R.id.iv_delete)
        }
    }

}