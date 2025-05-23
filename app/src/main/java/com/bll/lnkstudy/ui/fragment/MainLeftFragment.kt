package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.AUTO_REFRESH_EVENT
import com.bll.lnkstudy.Constants.Companion.CALENDER_SET_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_DRAWING_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.MAIN_HOMEWORK_NOTICE_CLEAR_EVENT
import com.bll.lnkstudy.Constants.Companion.USER_CHANGE_GRADE_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolItemBean
import com.bll.lnkstudy.mvp.presenter.HomeworkNoticePresenter
import com.bll.lnkstudy.mvp.presenter.MainLeftPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkNoticeView
import com.bll.lnkstudy.mvp.view.IContractView.IMainLeftView
import com.bll.lnkstudy.ui.activity.HomeworkNoticeListActivity
import com.bll.lnkstudy.ui.activity.ScreenshotListActivity
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.activity.date.DateDayListActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkstudy.ui.adapter.MainDatePlanAdapter
import com.bll.lnkstudy.ui.adapter.MainHomeworkNoticeAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.date.CalenderUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_left.iv_calender
import kotlinx.android.synthetic.main.fragment_main_left.iv_close
import kotlinx.android.synthetic.main.fragment_main_left.iv_date_more
import kotlinx.android.synthetic.main.fragment_main_left.ll_calender
import kotlinx.android.synthetic.main.fragment_main_left.ll_notice
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_note
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_notice
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_plan
import kotlinx.android.synthetic.main.fragment_main_left.tv_correct_notice
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_today
import kotlinx.android.synthetic.main.fragment_main_left.tv_homework_notice
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_content
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_end_time
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_name
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_time
import kotlinx.android.synthetic.main.fragment_main_left.tv_plan
import kotlinx.android.synthetic.main.fragment_main_left.tv_planover
import kotlinx.android.synthetic.main.fragment_main_left.tv_screenshot
import kotlinx.android.synthetic.main.fragment_main_left.v_down
import kotlinx.android.synthetic.main.fragment_main_left.v_up
import java.util.Random


/**
 * 首页
 */
class MainLeftFragment : BaseMainFragment(), IMainLeftView,IHomeworkNoticeView {
    private val mMainLeftPresenter = MainLeftPresenter(this, 1)
    private val mHomeworkNoticePresenter=HomeworkNoticePresenter(this,1)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var correctAdapter: MainHomeworkNoticeAdapter? = null
    private var mNoticeAdapter: MainHomeworkNoticeAdapter? = null
    private var nowDate = 0L
    private var nowDayPos = 1
    private var calenderPath = ""
    private var dateEvents = mutableListOf<DateEventBean>()

    override fun onParentPermission(permissionParentBean: PermissionParentBean) {
        SPUtil.putObj(Constants.SP_PARENT_PERMISSION, permissionParentBean)
    }
    override fun onSchoolPermission(permissionSchoolBean: PermissionSchoolBean) {
        if (permissionSchoolBean!=null){
            if (permissionSchoolBean.config.isNotEmpty()) {
                val item = Gson().fromJson(permissionSchoolBean.config, PermissionSchoolItemBean::class.java)
                SPUtil.putObj(Constants.SP_SCHOOL_PERMISSION, item)
            } else {
                SPUtil.removeObj(Constants.SP_SCHOOL_PERMISSION)
            }
        }
    }
    override fun onCorrect(list: HomeworkNoticeList) {
        correctAdapter?.setNewData(list.list)
    }
    override fun onHomeworkNotice(list: HomeworkNoticeList) {
        mNoticeAdapter?.setNewData(list.list)
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {

        setTitle(DataBeanManager.listTitle[0])
        initDialog(1)

        initPlanView()
        initNoticeView()
        initCorrectView()

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        iv_date_more.setOnClickListener {
            customStartActivity(Intent(activity, DateDayListActivity::class.java))
        }

        tv_plan.setOnClickListener {
            customStartActivity(Intent(activity, DatePlanListActivity::class.java))
        }

        v_up.setOnClickListener {
            nowDate -= Constants.dayLong
            setDateView()
            if (nowDayPos > 1) {
                nowDayPos -= 1
                setCalenderImage()
            }
        }

        v_down.setOnClickListener {
            nowDate += Constants.dayLong
            setDateView()
            if (nowDayPos <= 366) {
                nowDayPos += 1
                setCalenderImage()
            }
        }

        tv_screenshot.setOnClickListener {
            customStartActivity(Intent(activity, ScreenshotListActivity::class.java))
        }

        tv_planover.setOnClickListener {
            customStartActivity(Intent(activity, PlanOverviewActivity::class.java))
        }

        tv_homework_notice.setOnClickListener {
            customStartActivity(Intent(activity, HomeworkNoticeListActivity::class.java).setFlags(0))
        }

        tv_correct_notice.setOnClickListener {
            customStartActivity(Intent(activity, HomeworkNoticeListActivity::class.java).setFlags(1))
        }
    }

    override fun lazyLoad() {
        nowDate = DateUtils.getStartOfDayInMillis()
        setDateView()
        showCalenderView()
        findDataPlan()
        fetchData()
    }

    override fun fetchData() {
        onCheckUpdate()
        if (NetworkUtil.isNetworkConnected()) {
            mMainLeftPresenter.active()
            if (grade>0){
                mMainLeftPresenter.getParentPermission()
                mMainLeftPresenter.getSchoolPermission(grade)

                val map=HashMap<String,Any>()
                map["size"]=7
                mHomeworkNoticePresenter.getHomeworkNotice(map)
                mHomeworkNoticePresenter.getCorrectNotice(map)
            }
        }
    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView() {
//        val solar= Solar()
//        solar.solarYear= DateUtils.getYear()
//        solar.solarMonth=DateUtils.getMonth()
//        solar.solarDay=DateUtils.getDay()
//        val lunar= LunarSolarConverter.SolarToLunar(solar)
//
//        val str = if (!solar.solar24Term.isNullOrEmpty()) {
//            "24节气   "+solar.solar24Term
//        } else {
//            if (!solar.solarFestivalName.isNullOrEmpty()) {
//                "节日  "+solar.solarFestivalName
//            } else {
//                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
//                    "节日   "+lunar.lunarFestivalName
//                }
//                else{
//                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
//                }
//            }
//        }
        tv_date_today.text = DateUtils.longToStringWeek(nowDate)

//        val path = FileAddress().getPathDate(DateUtils.longToStringCalender(nowDate)) + "/draw.png"
//        if (File(path).exists()) {
////            GlideUtils.setImageNoCacheRoundUrl(activity,path,iv_date,20)
//            val myBitmap = BitmapFactory.decodeFile(path)
//            iv_date.setImageBitmap(myBitmap)
//        } else {
//            iv_date.setImageResource(0)
//        }
    }

    /**
     * 是否显示台历
     */
    private fun showCalenderView() {
        val item = CalenderDaoManager.getInstance().queryCalenderBean()
        if (item != null) {
            calenderPath = item.path
            showView(ll_calender)
            val calenderUtils = CalenderUtils(DateUtils.longToStringDataNoHour(nowDate))
            nowDayPos = calenderUtils.elapsedTime()
            setCalenderImage()
        }
        else{
            disMissView(ll_calender)
        }
    }

    /**
     * 设置台历图片
     */
    private fun setCalenderImage() {
        val listFiles = FileUtils.getFiles(calenderPath)
        if (listFiles.size > 0) {
            val file = if (listFiles.size > nowDayPos - 1) {
                listFiles[nowDayPos - 1]
            } else {
                listFiles[Random().nextInt(listFiles.size)]
//                listFiles[listFiles.size - 1]
            }
            GlideUtils.setImageRoundUrl(requireActivity(), file.path, iv_calender, 15)
        }
    }

    //今日计划
    private fun initPlanView() {
        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null).apply {
            rv_main_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_plan.adapter = this
            bindToRecyclerView(rv_main_plan)
        }
    }

    /**
     * 作业通知
     */
    private fun initNoticeView() {
        mNoticeAdapter = MainHomeworkNoticeAdapter(R.layout.item_main_homework_notice, null).apply {
            rv_main_notice.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_notice.adapter = this
            bindToRecyclerView(rv_main_notice)
        }
        mNoticeAdapter?.setOnItemClickListener { adapter, view, position ->
            showView(ll_notice)
            setNoticeShow(0,mNoticeAdapter?.getItem(position)!!)
        }
    }

    //批改详情
    private fun initCorrectView() {
        correctAdapter = MainHomeworkNoticeAdapter(R.layout.item_main_homework_notice, null).apply {
            rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
        }
        correctAdapter?.setOnItemClickListener { adapter, view, position ->
            showView(ll_notice)
            setNoticeShow(1,correctAdapter?.getItem(position)!!)
        }
    }

    /**
     * 展示作业详情
     */
    private fun setNoticeShow(type:Int,item: HomeworkNoticeList.HomeworkNoticeBean) {
        tv_notice_name?.text = "${DataBeanManager.getCourseStr(item.subject)}   ${item.typeName} "
        tv_notice_time?.text = "${if (type==0)"布置时间：" else "批改时间："}" + DateUtils.longToStringWeek1(item.time)
        if (item.endTime > 0) {
            showView(tv_notice_end_time)
            tv_notice_end_time?.text = "提交时间：" + DateUtils.longToStringWeek(item.endTime)
        }
        else{
            disMissView(tv_notice_end_time)
        }
        tv_notice_content?.text = "通知内容：${item.title}"

        iv_close.setOnClickListener {
            disMissView(ll_notice)
        }
    }

    /**
     * 查找学习计划
     */
    private fun findDataPlan() {
//        val dates=DateEventGreenDaoManager.getInstance().queryAllDateEvent(1)
//        for (item in dates){
//            if (item.maxLong<nowDate){
//                val selectDate=SPUtil.getListLong("dateDateEvent")
//                selectDate.removeAll(item.dates)
//                SPUtil.putListLong("dateDateEvent",selectDate)
//                DateEventGreenDaoManager.getInstance().deleteDateEvent(item)
//            }
//        }

        val years = DateUtils.longToStringDataNoHour(nowDate)
        val dateBean = DateBean()
        dateBean.year = years[0].toInt()
        dateBean.month = years[1].toInt()
        dateBean.day = years[2].toInt()
        dateBean.time = nowDate
        dateBean.week = DateUtils.getWeek(nowDate)

        dateEvents = DateEventGreenDaoManager.getInstance().queryAllDateEvent(dateBean)
        if (dateEvents.size > 0) {
            mPlanAdapter?.setNewData(dateEvents[0].plans)
        } else {
            mPlanAdapter?.setNewData(null)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            AUTO_REFRESH_EVENT -> {
                lazyLoad()
            }
            DATE_EVENT -> {
                findDataPlan()
            }
            MAIN_HOMEWORK_NOTICE_CLEAR_EVENT -> {
                mHomeworkNoticePresenter.deleteHomeworkNotice()
                mHomeworkNoticePresenter.deleteCorrectNotice()
            }
            CALENDER_SET_EVENT -> {
                showCalenderView()
            }
            DATE_DRAWING_EVENT -> {
                setDateView()
            }
            USER_CHANGE_GRADE_EVENT->{
                fetchData()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}