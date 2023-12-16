package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 1左 2 右
 */
class AppToolDialog(val context: Context, val screenPos:Int, val location:Int) {

    private var dialog:Dialog?=null

    fun builder(): AppToolDialog? {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_app_tool)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.LEFT
        layoutParams?.x=DP2PX.dip2px(context,12f)
        layoutParams?.y=50
        if (location==2){
            layoutParams?.gravity = Gravity.BOTTOM or Gravity.RIGHT
        }
        dialog?.show()

        val toolApps=MethodManager.getAppTools(context,1)
        if (context is PlanOverviewActivity){
            val appBean= AppDaoManager.getInstance().queryAllByPackageName(Constants.PACKAGE_GEOMETRY)
            if (appBean!=null){
                toolApps.remove(appBean)
            }
        }

        val rv_list=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_app_name_list, toolApps)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val packageName= toolApps[position].packageName
            if (packageName.equals(Constants.PACKAGE_GEOMETRY)){
                dismiss()
                listener?.onClick()
            }
            else{
                when(screenPos){
                    1->{
                        AppUtils.startAPP(context,packageName,2)
                    }
                    2->{
                        AppUtils.startAPP(context,packageName,1)
                    }
                    3->{
                        if (location==1){
                            AppUtils.startAPP(context,packageName,2)
                        }
                        else{
                            AppUtils.startAPP(context,packageName,1)
                        }
                    }
                }

            }
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

    class MyAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: AppBean) {
            helper.setText(R.id.tv_name,item.appName)
            helper.setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(item.imageByte))
        }
    }

    var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        listener = onDialogClickListener
    }

}