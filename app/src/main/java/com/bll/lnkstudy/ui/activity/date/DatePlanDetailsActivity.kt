package com.bll.lnkstudy.ui.activity.date

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CalendarMultiDialog
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.ui.adapter.DatePlanEventAddAdapter
import com.bll.lnkstudy.ui.adapter.DatePlanWeekAdapter
import com.bll.lnkstudy.ui.adapter.DateTimeAdapter
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_date_plan_details.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class DatePlanDetailsActivity:BaseAppCompatActivity() {

    private var flags=0
    private var planList = mutableListOf<DatePlan>()
    private var mAdapter: DatePlanEventAddAdapter? = null
    private var mWeekAdapter: DatePlanWeekAdapter? = null
    private var mTimeAdapter: DateTimeAdapter? = null
    private var dateEventBean: DateEventBean?=null
    private var weeks= mutableListOf<DateWeek>()
    private var times= mutableListOf<Long>()
    private var startStr=""
    private var endStr=""
    private var calendarDialog:CalendarMultiDialog?=null
    private var isWeek=false //是否是编辑星期
    private var isDate=false //是否是编辑日期
    private var selectWeekInt= mutableListOf<Int>() //已选星期
    private var selectDateLong= mutableListOf<Long>() //已选日期

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_details
    }

    override fun initData() {
        flags=intent.flags
        weeks= DataBeanManager.weeks

        startStr=getString(R.string.start)
        endStr=getString(R.string.end)

        if (flags==0){
            dateEventBean= DateEventBean()
            dateEventBean?.type=0

            for (i in 0..7) {
                val date = DatePlan()
                planList.add(date)
            }
        }
        else{
            dateEventBean = intent.getBundleExtra("bundle")?.getSerializable("dateEvent") as DateEventBean
            et_title.setText(dateEventBean?.title)

            planList=dateEventBean?.plans!!
            if (dateEventBean?.plans?.size!! <8) {
                for (i in 0 until  8-dateEventBean?.plans?.size!!){
                    val date = DatePlan()
                    planList.add(date)
                }
            }

            if (dateEventBean?.date==0){
                isWeek=true
                for (item in weeks){
                    for (ite in dateEventBean?.weeks!!){
                        if (ite.week==item.week){
                            item.isCheck=true
                        }
                    }
                }
            }
            else{
                isDate=true
                times=dateEventBean?.dates!!
            }
        }
    }

    override fun initView() {
        setPageTitle(R.string.date_plan)
        setPageSetting(R.string.save)

        selectWeekInt=SPUtil.getListInt("week")
        selectDateLong=SPUtil.getListLong("date")
        //修改编辑星期时：要把当前星期从已星期选中移出
        if (isWeek){
            disMissView(tv_date,rv_date)
            for (item in dateEventBean?.weeks!!){
                selectWeekInt.remove(item.week)
            }
        }
        //修改日期时，需要把当前日期从已选日期中移出
        if (isDate){
            disMissView(rv_week)
            selectDateLong.removeAll(times)
        }

        initWeeks()

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanEventAddAdapter(R.layout.item_date_plan_add, planList)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)

        rv_week.layoutManager = GridLayoutManager(this,7) //创建布局管理
        mWeekAdapter = DatePlanWeekAdapter(R.layout.item_date_plan_add_week, weeks)
        rv_week.adapter = mWeekAdapter
        mWeekAdapter?.bindToRecyclerView(rv_week)
        mWeekAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=weeks[position]
            if (view.id==R.id.cb_week){
                item.isCheck=!item.isCheck
                mWeekAdapter?.notifyItemChanged(position)
                if (getSelectWeeks().isNotEmpty()){
                    times.clear()
                    mTimeAdapter?.notifyDataSetChanged()
                }
            }
        }

        rv_date.layoutManager = GridLayoutManager(this,4) //创建布局管理
        mTimeAdapter = DateTimeAdapter(R.layout.item_date_time, times)
        rv_date.adapter = mTimeAdapter
        mTimeAdapter?.bindToRecyclerView(rv_date)
        rv_date.addItemDecoration(SpaceGridItemDeco(4,30))

        iv_add.setOnClickListener {
            planList.add(DatePlan())
            mAdapter?.notifyItemChanged(planList.size-1)
            rv_list.scrollToPosition(planList.size-1)
        }

        tv_setting.setOnClickListener {
            save()
        }

        tv_date.setOnClickListener {
            CalendarMultiDialog(this,times,selectDateLong).builder().setOnDateListener{
                times=it
                mTimeAdapter?.setNewData(times)
                if (times.isNotEmpty()){
                    weeks=DataBeanManager.weeks
                    initWeeks()
                    mWeekAdapter?.setNewData(weeks)
                }
            }
        }
    }

    /**
     * 设置已选星期不可以点击
     */
    private fun initWeeks():MutableList<DateWeek>{
        for (i in selectWeekInt){
            for (item in weeks){
                if (item.week==i){
                    item.isSelected=true
                }
            }
        }
        return weeks
    }

    /**
     * 获取选中的星期
     */
    private fun getSelectWeeks():MutableList<DateWeek>{
        val selectWeeks= mutableListOf<DateWeek>()
        for (item in weeks){
            if (item.isCheck)
                selectWeeks.add(item)
        }
        return selectWeeks
    }

    private fun save(){
        val titleStr = et_title.text.toString()
        if (titleStr.isEmpty()) {
            showToast(R.string.toast_input_title)
            return
        }
        dateEventBean?.title=titleStr
        dateEventBean?.dayLong=System.currentTimeMillis()

        if (getSelectWeeks().size==0&&times.size==0){
            showToast(R.string.toast_select_week)
            return
        }

        if (getSelectWeeks().isNotEmpty()){
            dateEventBean?.weeks=getSelectWeeks()
            dateEventBean?.date=0
            for (item in getSelectWeeks()){
                selectWeekInt.add(item.week)
            }
            //存储已选星期
            SPUtil.putListInt("week",selectWeekInt)
        }
        else{
            dateEventBean?.dates=times
            dateEventBean?.date=1
            dateEventBean?.maxLong=times.last()
            //防止重复添加日期
            for (time in times){
                if (!selectDateLong.contains(time))
                    selectDateLong.add(time)
            }
            //存储已选日期
            SPUtil.putListLong("date",selectDateLong)
        }

        val plans = mutableListOf<DatePlan>()
        val items = mAdapter?.data!!
        for (item in items) {
            if (!item.content.isNullOrEmpty() && !item.course.isNullOrEmpty() && !item.endTimeStr.isNullOrEmpty()) {
                plans.add(item)
            }
        }

        dateEventBean?.plans=plans

        DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEventBean)

        EventBus.getDefault().post(Constants.DATE_EVENT)
        finish()
    }



}

