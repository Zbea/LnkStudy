package com.bll.lnkstudy.ui.activity.date

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.ui.adapter.DatePlanListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class DatePlanListActivity:BaseAppCompatActivity() {

    private var mAdapter:DatePlanListAdapter?=null
    private var plans= mutableListOf<DateEventBean>()
    private var startStr=""
    private var endStr=""

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        startStr=getString(R.string.start)
        endStr=getString(R.string.end)
    }

    override fun initView() {
        setPageTitle(R.string.date_plan)
        setImageManager(R.mipmap.icon_add)

        iv_manager.setOnClickListener {
            customStartActivity(Intent(this,DatePlanDetailsActivity::class.java).addFlags(0)
                .putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
            )
//            SPUtil.putListInt("weekDateEvent", mutableListOf())
//            SPUtil.putListLong("dateDateEvent", mutableListOf())
        }

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,40f),
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanListAdapter(R.layout.item_date_plan_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30,false))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent=Intent(this,DatePlanDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val bundle = Bundle()
            bundle.putSerializable("dateEvent", plans[position])
            intent.putExtra("bundle", bundle)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
            customStartActivity(intent)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                CommonDialog(this).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        val item=plans[position]
                        //删除计划的同时，移出星期的选择
                        if (item.date==0){
                            val selectWeek=SPUtil.getListInt("weekDateEvent")
                            for (week in item.weeks){
                                selectWeek.remove(week.week)
                            }
                            SPUtil.putListInt("weekDateEvent",selectWeek)
                        }
                        else{
                            val selectDate=SPUtil.getListLong("dateDateEvent")
                            selectDate.removeAll(item.dates)
                            SPUtil.putListLong("dateDateEvent",selectDate)
                        }
                        DateEventGreenDaoManager.getInstance().deleteDateEvent(item)
                        plans.removeAt(position)
                        mAdapter?.notifyItemRemoved(position)
                        EventBus.getDefault().post(Constants.DATE_EVENT)
                    }
                })
            }
        }

        findDatas()

    }


    private fun findDatas(){
        plans= DateEventGreenDaoManager.getInstance().queryAllDateEvent()
        mAdapter?.setNewData(plans)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag== Constants.DATE_EVENT){
            findDatas()
        }
    }

}