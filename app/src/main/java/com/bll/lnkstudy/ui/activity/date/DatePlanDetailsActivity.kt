package com.bll.lnkstudy.ui.activity.date

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.SP_DATE_LIST
import com.bll.lnkstudy.Constants.Companion.SP_WEEK_DATE_LIST
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CalendarMultiDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.ui.adapter.DatePlanEventAddAdapter
import com.bll.lnkstudy.ui.adapter.DatePlanWeekAdapter
import com.bll.lnkstudy.ui.adapter.DateTimeAdapter
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_date_plan_details.et_title
import kotlinx.android.synthetic.main.ac_date_plan_details.iv_add
import kotlinx.android.synthetic.main.ac_date_plan_details.iv_clock
import kotlinx.android.synthetic.main.ac_date_plan_details.rg_group
import kotlinx.android.synthetic.main.ac_date_plan_details.rg_group_date
import kotlinx.android.synthetic.main.ac_date_plan_details.rv_date
import kotlinx.android.synthetic.main.ac_date_plan_details.rv_list
import kotlinx.android.synthetic.main.ac_date_plan_details.rv_week
import kotlinx.android.synthetic.main.ac_date_plan_details.tv_date
import kotlinx.android.synthetic.main.common_title.tv_setting
import org.greenrobot.eventbus.EventBus

class DatePlanDetailsActivity:BaseAppCompatActivity() {

    private var flags=0
    private var planList = mutableListOf<DatePlan>()
    private var mAdapter: DatePlanEventAddAdapter? = null
    private var mWeekAdapter: DatePlanWeekAdapter? = null
    private var mTimeAdapter: DateTimeAdapter? = null
    private var dateEventBean: DateEventBean?=null
    private var currentWeeks= mutableListOf<DateWeek>()
    private var currentTimes= mutableListOf<Long>()
    private var selectWeekInt= mutableListOf<Int>() //已选星期
    private var selectDateLong= mutableListOf<Long>() //已选日期
    private var popCourses= mutableListOf<PopupBean>()

    private var initHour=0
    private var hour=-1
    private var minute=-1
    private var position=-1
    private var oldPosition=-1
    private var isStart=true
    private var timeStr=""

    override fun layoutId(): Int {
        return R.layout.ac_date_plan_details
    }

    override fun initData() {
        flags=intent.flags
        currentWeeks= DataBeanManager.weeks

        selectWeekInt=SPUtil.getListInt(SP_WEEK_DATE_LIST)
        selectDateLong=SPUtil.getListLong(SP_DATE_LIST)

        val courses=MethodManager.getCourses()
        for (item in courses){
            popCourses.add(PopupBean(0,item))
        }
        popCourses.add(PopupBean(0,"运动"))
        popCourses.add(PopupBean(0,"娱乐"))
        popCourses.add(PopupBean(0,"才艺"))
        popCourses.add(PopupBean(0,"间休"))
        popCourses.add(PopupBean(0,"起卧"))

        if (flags==0){
            dateEventBean= DateEventBean()
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

                for (item in currentWeeks){
                    for (ite in dateEventBean?.weeks!!){
                        if (ite.week==item.week){
                            item.isCheck=true
                        }
                    }
                }

                //修改编辑星期时：要把当前星期从已星期选中移出
                for (item in dateEventBean?.weeks!!){
                    selectWeekInt.remove(item.week)
                }

                rg_group.check(R.id.rb_week)
                disMissView(tv_date,rv_date)
                showView(rv_week)
            }
            else{
                currentTimes=dateEventBean?.dates!!
                //修改日期时，需要把当前日期从已选日期中移出
                selectDateLong.removeAll(dateEventBean?.dates!!)

                rg_group.check(R.id.rb_date)
            }
        }
    }

    override fun initView() {
        setPageTitle(R.string.date_plan)
        setPageSetting(R.string.save)

        initWeeks()

        iv_add.setOnClickListener {
            planList.add(DatePlan())
            mAdapter?.notifyItemChanged(planList.size-1)
            rv_list.scrollToPosition(planList.size-1)
        }

        tv_setting.setOnClickListener {
            save()
        }

        tv_date.setOnClickListener {
            CalendarMultiDialog(this,currentTimes,selectDateLong).builder().setOnDateListener{
                currentTimes=it
                mTimeAdapter?.setNewData(currentTimes)
                if (currentTimes.isNotEmpty()){
                    currentWeeks=DataBeanManager.weeks
                    initWeeks()
                    mWeekAdapter?.setNewData(currentWeeks)
                }
            }
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            if (i==R.id.rb_date){
                showView(rv_date,tv_date)
                disMissView(rv_week)
            }
            else{
                disMissView(rv_date,tv_date)
                showView(rv_week)
            }
        }

        initClockView()
        initRecyclerViewPlan()
        initRecyclerViewDate()
        initRecyclerViewWeek()
    }
    
    private fun initClockView(){
        rg_group_date.setOnCheckedChangeListener { radioGroup, i ->
            initHour = if (i==R.id.rb_am){
                0
            }else{
                12
            }
        }

        iv_clock.setOnClockClickListener { type, time ->
            if (position>=0){
                if (type==0){
                    hour=initHour+time
                }
                else{
                    minute=time
                }
                if (hour>=0&&minute>=0){
                    timeStr= ToolUtils.getFormatNum(hour,"00")+":"+ ToolUtils.getFormatNum(minute,"00")
                    val plan=planList[position]
                    if (isStart)
                        plan.startTimeStr=timeStr
                    else
                        plan.endTimeStr=timeStr
                    mAdapter?.notifyItemChanged(position)
                    timeStr=""
                    hour=-1
                    minute=-1
                }
            }
        }
    }

    private fun initRecyclerViewPlan(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DatePlanEventAddAdapter(R.layout.item_date_plan_add, planList)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            val item=planList[position]
            when(view.id){
                R.id.tv_course->{
                    PopupClick(this,popCourses,view,100,2).builder().setOnSelectListener{
                        item.course=it.name
                        mAdapter?.notifyItemChanged(position)
                    }
                }
                R.id.tv_start_time->{
                    if (oldPosition>=0){
                        val oldItem=planList[oldPosition]
                        oldItem.isStartSelect=false
                        oldItem.isEndSelect=false
                        mAdapter?.notifyItemChanged(oldPosition)
                    }
                    item.isStartSelect=true
                    item.isEndSelect=false
                    mAdapter?.notifyItemChanged(position)
                    isStart=true
                    oldPosition=position
                }
                R.id.tv_end_time->{
                    if (oldPosition>=0){
                        val oldItem=planList[oldPosition]
                        oldItem.isStartSelect=false
                        oldItem.isEndSelect=false
                        mAdapter?.notifyItemChanged(oldPosition)
                    }
                    item.isStartSelect=false
                    item.isEndSelect=true
                    mAdapter?.notifyItemChanged(position)
                    isStart=false
                    oldPosition=position
                }
            }
        }
    }

    private fun initRecyclerViewWeek(){
        rv_week.layoutManager = GridLayoutManager(this,7) //创建布局管理
        mWeekAdapter = DatePlanWeekAdapter(R.layout.item_date_plan_add_week, currentWeeks)
        rv_week.adapter = mWeekAdapter
        mWeekAdapter?.bindToRecyclerView(rv_week)
        mWeekAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=currentWeeks[position]
            if (view.id==R.id.cb_week){
                item.isCheck=!item.isCheck
                mWeekAdapter?.notifyItemChanged(position)
                if (getSelectWeeks().isNotEmpty()){
                    currentTimes.clear()
                    mTimeAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initRecyclerViewDate(){
        rv_date.layoutManager = GridLayoutManager(this,4) //创建布局管理
        mTimeAdapter = DateTimeAdapter(R.layout.item_date_time, currentTimes)
        rv_date.adapter = mTimeAdapter
        mTimeAdapter?.bindToRecyclerView(rv_date)
        rv_date.addItemDecoration(SpaceGridItemDeco(4,30))
    }

    /**
     * 设置已选星期不可以点击
     */
    private fun initWeeks():MutableList<DateWeek>{
        for (i in selectWeekInt){
            for (item in currentWeeks){
                if (item.week==i){
                    item.isSelected=true
                }
            }
        }
        return currentWeeks
    }

    /**
     * 获取选中的星期
     */
    private fun getSelectWeeks():MutableList<DateWeek>{
        val selectWeeks= mutableListOf<DateWeek>()
        for (item in currentWeeks){
            if (item.isCheck)
                selectWeeks.add(item)
        }
        return selectWeeks
    }

    private fun save(){
        val titleStr = et_title.text.toString()
        if (titleStr.isEmpty()) {
            showToast(1,R.string.toast_input_title)
            return
        }
        dateEventBean?.title=titleStr
        dateEventBean?.dayLong=System.currentTimeMillis()

        if (getSelectWeeks().size==0&&currentTimes.size==0){
            showToast(1,R.string.toast_select_week)
            return
        }

        if (getSelectWeeks().isNotEmpty()){
            dateEventBean?.weeks=getSelectWeeks()
            dateEventBean?.date=0
            dateEventBean?.maxLong=0
            dateEventBean?.dates=null

            //加入已选的星期
            for (item in getSelectWeeks()){
                if (!selectWeekInt.contains(item.week))
                    selectWeekInt.add(item.week)
            }
        }
        else{
            dateEventBean?.dates=currentTimes
            dateEventBean?.date=1
            dateEventBean?.maxLong=currentTimes.last()
            dateEventBean?.weeks=null

            //加入已选日期
            for (lon in currentTimes){
                if (!selectDateLong.contains(lon))
                    selectDateLong.add(lon)
            }
        }

        //存储已选日期
        SPUtil.putListLong(SP_DATE_LIST,selectDateLong)
        SPUtil.putListInt(SP_WEEK_DATE_LIST,selectWeekInt)

        val plans = mutableListOf<DatePlan>()
        for (item in mAdapter?.data!!) {
            if (!item.content.isNullOrEmpty() && !item.course.isNullOrEmpty()&& !item.startTimeStr.isNullOrEmpty()) {
                item.isEndSelect=false
                item.isEndSelect=false
                plans.add(item)
            }
        }

        if (plans.size==0){
            showToast(1,"未添加计划")
            return
        }

        dateEventBean?.plans=plans

        DateEventGreenDaoManager.getInstance().insertOrReplaceDateEvent(dateEventBean)

        EventBus.getDefault().post(Constants.DATE_EVENT)
        finish()
    }



}

