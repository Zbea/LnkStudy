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
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.ui.adapter.DatePlanListAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import kotlinx.android.synthetic.main.ac_date_plan_list.*
import kotlinx.android.synthetic.main.common_fragment_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DatePlanListActivity:BaseAppCompatActivity() {

    private var popWindowList: PopWindowList?=null
    private var popWindowBeans = mutableListOf<PopWindowBean>()

    private var mAdapter:DatePlanListAdapter?=null
    private var plans= mutableListOf<DateEvent>()

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_list
    }

    override fun initData() {
        popWindowBeans.add(PopWindowBean(0,"添加",true))
        popWindowBeans.add(PopWindowBean(1,"复制",false))
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        EventBus.getDefault().register(this)

        setPageTitle("学习计划")
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            setPopWindow()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanListAdapter(R.layout.item_date_plan_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent=Intent(this,DatePlanDetailsActivity::class.java)
            intent.addFlags(1)
            var bundle = Bundle()
            bundle.putSerializable("dateEvent", plans[position])
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                CommonDialog(this).setContent("确定要删除？").builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {

                        //删除已加入日历
                        for (item in plans[position]?.plans!!){
                            if (item.isRemindStart){
                                CalendarReminderUtils.deleteCalendarEvent(this@DatePlanListActivity,plans[position]?.title+"开始："+item.course+item.content)
                            }
                            if (item.isRemindEnd){
                                CalendarReminderUtils.deleteCalendarEvent(this@DatePlanListActivity,plans[position]?.title+"结束："+item.course+item.content)
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
        if (popWindowList==null)
        {
            popWindowList= PopWindowList(this,popWindowBeans,iv_manager,5).builder()
            popWindowList?.setOnSelectListener { item ->
                if (item.id == 0) {
                    customStartActivity(Intent(this,DatePlanDetailsActivity::class.java).addFlags(0))
                }
                if (item.id==1){
                    setCopy()
                }
            }
        }
        else{
            popWindowList?.show()
        }
    }

    private fun findDatas(){
        plans= DateEventGreenDaoManager.getInstance().queryAllDateEvent(0)
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

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.DATE_EVENT){
            findDatas()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}