package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.COURSE_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CourseModuleDialog
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.activity.MainActivity
import com.bll.lnkstudy.ui.activity.MainCourseActivity
import com.bll.lnkstudy.ui.activity.MessageListActivity
import com.bll.lnkstudy.ui.activity.date.MainDateActivity
import com.bll.lnkstudy.ui.adapter.*
import com.bll.lnkstudy.utils.PopWindowUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.StringUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


/**
 * 首页
 */
class MainFragment : BaseFragment() {

    private var dayNowLong= StringUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))
    private var planList= mutableListOf<DatePlanBean>()
    private var scheduleList= mutableListOf<DateScheduleEvent>()
    private var dayList= mutableListOf<DateDayEvent>()
    private var mainDateEventScheduleAdapter: MainDateEventScheduleAdapter?=null
    private var mainDateEventDayAdapter: MainDateEventDayAdapter?=null
    private var mainDateAdapter:MainDateAdapter?=null

    private var books= mutableListOf<Book>()
    private var mainTextBookAdapter:MainTextBookAdapter?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setPageTitle("首页")
        setDisBackShow()

        onClickView()

        initDateView()
        initMessageView()
        initTextBookView()
        initHomeWorkView()

        initCourse()

    }

    override fun lazyLoad() {
    }

    private fun onClickView(){
        ll_date.setOnClickListener {
            startActivity(Intent(activity, MainDateActivity::class.java))
        }

        ll_message.setOnClickListener {
            startActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_textbook.setOnClickListener {
            (activity as MainActivity).goToTextBook()
        }

    }

    //课程表相关处理
    private fun initCourse(){
        Glide.with(this)
            .load(Constants.SCREEN_PATH+"/course.png")
            .skipMemoryCache(true)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(iv_course)

        iv_course.setOnClickListener {
            var type=SPUtil.getInt("courseType")
            startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(1).putExtra("courseType",type))
        }
        tv_course_add.setOnClickListener {
            CourseModuleDialog(requireActivity()).builder()?.setOnClickListener(object :
                CourseModuleDialog.OnClickListener {
                override fun onClick(type: Int) {
                    startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(0).putExtra("courseType",type))
                }
            })
        }

    }

    //日历相关内容设置
    private fun initDateView(){

        tv_date_today.text=SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())

        findDateList()

        rv_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mainDateAdapter = MainDateAdapter(R.layout.item_main_date_plan_event_child, planList)
        rv_plan.adapter = mainDateAdapter
        mainDateAdapter?.bindToRecyclerView(rv_plan)
        mainDateAdapter?.emptyView=getEmptyView("去添加学习计划~")

        rv_schedule.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mainDateEventScheduleAdapter = MainDateEventScheduleAdapter(R.layout.item_main_date_schedule_event, scheduleList)
        rv_schedule.adapter = mainDateEventScheduleAdapter
        mainDateEventScheduleAdapter?.bindToRecyclerView(rv_schedule)
        rv_schedule.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mainDateEventScheduleAdapter?.emptyView=getEmptyView("去添加日程~")

        rv_day.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mainDateEventDayAdapter = MainDateEventDayAdapter(R.layout.item_main_date_day_event, dayList)
        rv_day.adapter = mainDateEventDayAdapter
        mainDateEventDayAdapter?.bindToRecyclerView(rv_day)
        rv_day.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mainDateEventDayAdapter?.emptyView=getEmptyView("去添加重要日子~")

        tv_date_switch.setOnClickListener {

            val view = LayoutInflater.from(activity).inflate(R.layout.popwindow_date_switch_view, null, false)
            val tvPlan=view.findViewById<TextView>(R.id.tv_plan)
            tvPlan.setOnClickListener {
                rv_plan.visibility=View.VISIBLE
                rv_schedule.visibility=View.GONE
                rv_day.visibility=View.GONE
                PopWindowUtil.getInstance().dismiss()
            }
            val tvSchedule=view.findViewById<TextView>(R.id.tv_schedule)
            tvSchedule.setOnClickListener {
                rv_plan.visibility=View.GONE
                rv_schedule.visibility=View.VISIBLE
                rv_day.visibility= View.GONE
                PopWindowUtil.getInstance().dismiss()
            }
            val tvDay=view.findViewById<TextView>(R.id.tv_day)
            tvDay.setOnClickListener {
                rv_plan.visibility=View.GONE
                rv_schedule.visibility=View.GONE
                rv_day.visibility=View.VISIBLE
                PopWindowUtil.getInstance().dismiss()
            }
            PopWindowUtil.getInstance().makePopupWindow(activity,tv_date_switch,view, -220,5, Gravity.RIGHT).show()

        }

    }

    //获得当前空内容
    private fun getEmptyView(title:String):View{
        var emptyView=layoutInflater.inflate(R.layout.common_empty,null)
        emptyView.setOnClickListener {
            startActivity(Intent(activity, MainDateActivity::class.java))
        }
        var tv_content=emptyView.findViewById<TextView>(R.id.tv_empty_title)
        tv_content.text=title
        tv_content.textSize= 18f

        return emptyView
    }

    //消息相关处理
    private fun initMessageView(){
        val messageDatas= DataBeanManager.getIncetance().message
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        var messageAdapter = MainMessageAdapter(R.layout.item_main_message, messageDatas)
        rv_main_message.adapter = messageAdapter
        messageAdapter?.bindToRecyclerView(rv_main_message)
        rv_main_message.addItemDecoration(SpaceItemDeco(0,0,0,20,0))
        messageAdapter?.setOnItemClickListener { adapter, view, position ->
            messageDatas[position].isLook=true
            messageAdapter?.notifyDataSetChanged()
            MessageDetailsDialog(requireContext(), messageDatas[position]).builder()?.setOnDismissListener {
                messageAdapter?.remove(position)
            }
        }
        messageAdapter?.setType(1)
    }

    //课业相关处理
    private fun initTextBookView(){

        rv_main_textbook.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mainTextBookAdapter = MainTextBookAdapter(R.layout.item_main_textbook, books)
        rv_main_textbook.adapter = mainTextBookAdapter
        mainTextBookAdapter?.bindToRecyclerView(rv_main_textbook)
        mainTextBookAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_main_textbook?.addItemDecoration(SpaceGridItemDeco(0,40))
        mainTextBookAdapter?.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity, BookDetailsActivity::class.java).putExtra("book_id",books[position].id))
        }

        findBooks()
    }

    //作业相关
    private fun initHomeWorkView(){
        val courses= DataBeanManager.getIncetance().courses

        var mainHomeWorkAdapter = CourseAdapter(R.layout.item_main_course, courses)
        rv_main_homework.layoutManager = GridLayoutManager(activity,2)
        rv_main_homework.adapter = mainHomeWorkAdapter
        mainHomeWorkAdapter?.bindToRecyclerView(rv_main_homework)
        mainHomeWorkAdapter?.setOnItemClickListener { adapter, view, position ->
            if (courses[position].isSelect){
                courses[position].isSelect=false
                mainHomeWorkAdapter?.notifyDataSetChanged()
            }
        }

        var mainTestPaperAdapter = CourseAdapter(R.layout.item_main_course, courses)
        rv_main_testpaper.layoutManager = GridLayoutManager(activity,2)
        rv_main_testpaper.adapter = mainTestPaperAdapter
        mainTestPaperAdapter?.bindToRecyclerView(rv_main_testpaper)
    }


    /**
     * 通过当天时间查找本地dateEvent事件集合
     */
    private fun findDateList(){
        scheduleList= DateScheduleEventGreenDaoManager.getInstance(activity).queryAllDateEvent(dayNowLong)
        dayList= DateDayEventGreenDaoManager.getInstance(activity).queryAllDateDayEvent(dayNowLong)
        val datas= DatePlanEventGreenDaoManager.getInstance(activity).queryAllDatePlanEvent(dayNowLong)
        planList.clear()
        for (data in datas){
            for (item in data.list){
                planList.add(item)
            }
        }
    }

    //查找作业数据
    private fun findBooks(){
        books= BookGreenDaoManager.getInstance(activity).queryAllBook("1")
        if (books.size>6){
            books=books.subList(0,6)
        }
        mainTextBookAdapter?.setNewData(books)
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==DATE_EVENT){
            findDateList()
            mainDateEventScheduleAdapter?.setNewData(scheduleList)

            mainDateEventDayAdapter?.setDateLong(dayNowLong)
            mainDateEventDayAdapter?.setNewData(dayList)

            mainDateAdapter?.setNewData(planList)
        }
        if (msgFlag==TEXT_BOOK_EVENT){
            findBooks()
        }
        if (msgFlag== COURSE_EVENT){
            initCourse()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}