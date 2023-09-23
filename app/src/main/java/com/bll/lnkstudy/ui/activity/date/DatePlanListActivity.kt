package com.bll.lnkstudy.ui.activity.date

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DatePlanCopyDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.ui.adapter.DatePlanListAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_date_plan_list.*
import kotlinx.android.synthetic.main.common_fragment_title.*
import org.greenrobot.eventbus.EventBus

class DatePlanListActivity:BaseAppCompatActivity() {

    private var popWindowBeans = mutableListOf<PopupBean>()

    private var mAdapter:DatePlanListAdapter?=null
    private var plans= mutableListOf<DateEventBean>()
    private var startStr=""
    private var endStr=""

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_list
    }

    override fun initData() {
        popWindowBeans.add(PopupBean(0, getString(R.string.add_plan)))
        popWindowBeans.add(PopupBean(1, getString(R.string.copy)))

        startStr=getString(R.string.start)
        endStr=getString(R.string.end)
    }

    override fun initView() {
        setPageTitle(R.string.date_plan)
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            setPopWindow()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanListAdapter(R.layout.item_date_plan_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30,false))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent=Intent(this,DatePlanDetailsActivity::class.java)
            intent.addFlags(1)
            val bundle = Bundle()
            bundle.putSerializable("dateEvent", plans[position])
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                CommonDialog(this).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {

                        //删除已加入日历
                        for (item in plans[position]?.plans!!){
                            if (item.isRemindStart){
                                CalendarReminderUtils.deleteCalendarEvent(this@DatePlanListActivity,
                                    plans[position].title +startStr+"："+item.course+item.content)
                            }
                            if (item.isRemindEnd){
                                CalendarReminderUtils.deleteCalendarEvent(this@DatePlanListActivity,
                                    plans[position].title +endStr+"："+item.course+item.content)
                            }
                        }

                        DateEventGreenDaoManager.getInstance().deleteDateEvent(plans[position])
                        plans.removeAt(position)
                        mAdapter?.notifyItemRemoved(position)
                    }
                })
            }
        }

        findDatas()

    }

    private fun setPopWindow(){
         PopupClick(this,popWindowBeans,iv_manager,5).builder().setOnSelectListener { item ->
            if (item.id == 0) {
                customStartActivity(Intent(this,DatePlanDetailsActivity::class.java).addFlags(0))
            }
            if (item.id==1){
                setCopy()
            }
        }
    }

    private fun findDatas(){
        plans= DateEventGreenDaoManager.getInstance().queryAllDateEvent()
        mAdapter?.setNewData(plans)
    }

    /**
     * 复制
     */
    private fun setCopy(){
        if (plans.size>0){
            DatePlanCopyDialog(this,plans).builder().setOnSelectorListener {

                DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(it)
                EventBus.getDefault().post(Constants.DATE_EVENT)
            }
        }
    }

    override fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.DATE_EVENT){
            findDatas()
        }
    }

}