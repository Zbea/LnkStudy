package com.bll.lnkstudy.ui.activity.date

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.ui.adapter.DateDayListAdapter
import com.bll.lnkstudy.utils.CalendarReminderUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_date_day_list.*
import kotlinx.android.synthetic.main.ac_date_day_list.rv_list
import kotlinx.android.synthetic.main.ac_date_plan_list.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.SimpleDateFormat
import java.util.*

class DateDayListActivity:BaseAppCompatActivity() {

    private val nowDate = DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var mAdapter:DateDayListAdapter?=null
    private var days= mutableListOf<DateEventBean>()

    override fun layoutId(): Int {
        return R.layout.ac_date_day_list
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle(R.string.date_day)
        setPageSetting(R.string.add)

        tv_setting.setOnClickListener {
            customStartActivity(Intent(this,DateDayDetailsActivity::class.java).addFlags(0))
        }

        initRecycler()
        findDatas()
    }

    @SuppressLint("WrongConstant")
    private fun initRecycler(){

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DateDayListAdapter(R.layout.item_date_day_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30,false))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent=Intent(this,DateDayDetailsActivity::class.java)
            intent.addFlags(1)
            val bundle = Bundle()
            bundle.putSerializable("dateEvent", days[position])
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
                        //删除加入的日历
                        CalendarReminderUtils.deleteCalendarEvent(this@DateDayListActivity,days[position].title)
                        DateEventGreenDaoManager.getInstance().deleteDateEvent(days[position])
                        days.removeAt(position)
                        mAdapter?.notifyItemRemoved(position)
                    }
                })
            }
        }
    }

    private fun findDatas(){
        days=DateEventGreenDaoManager.getInstance().queryAllDayEvent(nowDate)
        days.addAll(DateEventGreenDaoManager.getInstance().queryAllDayEventOld(nowDate))
        mAdapter?.setNewData(days)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag== Constants.DATE_DAY_EVENT){
            findDatas()
        }
    }

}