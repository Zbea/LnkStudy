package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.Constants.Companion.MAIN_HOMEWORK_NOTICE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.presenter.MainPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICommonView
import com.bll.lnkstudy.mvp.view.IContractView.IMainView
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.activity.date.DateDayListActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanDetailsActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkstudy.ui.adapter.MainDatePlanAdapter
import com.bll.lnkstudy.ui.adapter.MainHomeworkNoticeAdapter
import com.bll.lnkstudy.ui.adapter.MainNoteAdapter
import com.bll.lnkstudy.ui.adapter.TextbookStoreAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.date.LunarSolarConverter
import com.bll.lnkstudy.utils.date.Solar
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.fragment_main_left.*


/**
 * 首页
 */
class MainLeftFragment : BaseFragment(),ICommonView,IMainView{

    private val mCommonPresenter= CommonPresenter(this)
    private val mMainPresenter=MainPresenter(this,1)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var noteAdapter: MainNoteAdapter? = null
    private var bookAdapter: TextbookStoreAdapter? = null
    private var mNoticeAdapter:MainHomeworkNoticeAdapter?=null
    private var nowDate = 0L
    private var popupDates= mutableListOf<PopupBean>()
    private var dateEvents= mutableListOf<DateEventBean>()

    override fun onList(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
            SPUtil.putList("grades", commonData.grade)
        if (!commonData.subject.isNullOrEmpty())
            SPUtil.putList("courses", commonData.subject)
        if (!commonData.typeGrade.isNullOrEmpty())
            SPUtil.putList("typeGrades", commonData.typeGrade)
        if (!commonData.version.isNullOrEmpty())
            SPUtil.putList("bookVersions", commonData.version)
    }

    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        MethodManager.saveClassGroups(classGroups)
    }
    override fun onExam(exam: PaperList?) {
    }
    override fun onHomeworkNotice(list: HomeworkNoticeList) {
        if (list.list.isNotEmpty()){
            mNoticeAdapter?.setNewData(list.list)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {
        setTitle(R.string.main_main_title)

        popupDates.add(PopupBean(0,getString(R.string.main_plan)))
        popupDates.add(PopupBean(1,getString(R.string.date_plan)))
        popupDates.add(PopupBean(2,getString(R.string.date_day)))

        initPlanView()
        initNoticeView()
        initNote()
        initBookView()

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        iv_date_more.setOnClickListener {
            PopupClick(requireActivity(),popupDates,iv_date_more,-20).builder().setOnSelectListener{
                when (it.id) {
                    0 -> {
                        customStartActivity(Intent(activity, PlanOverviewActivity::class.java))
                    }
                    1->{
                        customStartActivity(Intent(activity, DatePlanListActivity::class.java))
                    }
                    else -> {
                        customStartActivity(Intent(activity, DateDayListActivity::class.java))
                    }
                }
            }
        }

        tv_plan.setOnClickListener {
            if (dateEvents.isEmpty())
                return@setOnClickListener
            val item =dateEvents[0]
            val intent=Intent(requireActivity(), DatePlanDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val bundle = Bundle()
            bundle.putSerializable("dateEvent", item)
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
    }

    override fun lazyLoad() {
        setDateView()
        findDataPlan()
        findBook()
        findNotes()
        fetchData()
    }

    override fun fetchData(){
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            mCommonPresenter.getCommonData()
            mMainPresenter.getHomeworkNotice()
            mMainPresenter.getClassGroupList()
        }
    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView(){
        nowDate=DateUtils.getStartOfDayInMillis()

        val solar= Solar()
        solar.solarYear= DateUtils.getYear()
        solar.solarMonth=DateUtils.getMonth()
        solar.solarDay=DateUtils.getDay()
        val lunar= LunarSolarConverter.SolarToLunar(solar)

        val str = if (!solar.solar24Term.isNullOrEmpty()) {
            "24节气   "+solar.solar24Term
        } else {
            if (!solar.solarFestivalName.isNullOrEmpty()) {
                "节日  "+solar.solarFestivalName
            } else {
                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                    "节日   "+lunar.lunarFestivalName
                }
                else{
                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
                }
            }
        }
        tv_date_today.text=DateUtils.longToStringWeek(nowDate)+"  "+str
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
    private fun initNoticeView(){
        mNoticeAdapter = MainHomeworkNoticeAdapter(R.layout.item_main_homework_notice, null).apply {
            rv_main_notice.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_notice.adapter = this
            bindToRecyclerView(rv_main_notice)
        }
    }


    private fun initBookView(){
        bookAdapter = TextbookStoreAdapter(R.layout.item_main_book, null).apply {
            rv_main_book.layoutManager = GridLayoutManager(activity,3)//创建布局管理
            rv_main_book.adapter = bookAdapter
            bindToRecyclerView(rv_main_book)
            rv_main_book.addItemDecoration(SpaceGridItemDeco1(3, 0,5))
            setOnItemClickListener { adapter, view, position ->
                val bookBean= bookAdapter?.data?.get(position)
                MethodManager.gotoTextBookDetails(requireActivity(),bookBean?.bookId!!)
            }
        }
    }

    //笔记
    private fun initNote() {
        noteAdapter = MainNoteAdapter(R.layout.item_main_note, null).apply {
            rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
            setOnItemClickListener { adapter, view, position ->
                val item=noteAdapter?.data?.get(position)!!
                MethodManager.gotoNoteDrawing(requireActivity(),item,2, DEFAULT_PAGE)
            }
        }
    }

    /**
     * 查找学习计划
     */
    private fun findDataPlan() {

        val dates=DateEventGreenDaoManager.getInstance().queryAllDateEvent(1)
        for (item in dates){
            if (item.maxLong<nowDate){
                val selectDate=SPUtil.getListLong("date")
                selectDate.removeAll(item.dates)
                SPUtil.putListLong("date",selectDate)
                DateEventGreenDaoManager.getInstance().deleteDateEvent(item)
            }
        }

        val years=DateUtils.longToStringDataNoHour(nowDate)
        val dateBean= DateBean()
        dateBean.year=years[0].toInt()
        dateBean.month=years[1].toInt()
        dateBean.day=years[2].toInt()
        dateBean.time=nowDate
        dateBean.week=DateUtils.getWeek(nowDate)

        dateEvents=DateEventGreenDaoManager.getInstance().queryAllDateEvent(dateBean)
        mPlanAdapter?.setNewData(if (dateEvents.size>0)dateEvents[0].plans else null)
    }


    /**
     * 查找书籍
     */
    private fun findBook(){
        val books= TextbookGreenDaoManager.getInstance().queryAllTextBook(DataBeanManager.textbookType[0],1,9)
        bookAdapter?.setNewData(books)
    }

    /**
     * 查找笔记
     */
    private fun findNotes() {
        val notes= NoteDaoManager.getInstance().queryNotesExceptDiary(12)
        noteAdapter?.setNewData(notes)
    }


    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            DATE_EVENT -> {
                findDataPlan()
            }
            TEXT_BOOK_EVENT->{
                findBook()
            }
            NOTE_BOOK_MANAGER_EVENT, NOTE_EVENT -> {
                findNotes()
            }
            MAIN_HOMEWORK_NOTICE_EVENT->{
                mMainPresenter.deleteHomeworkNotice()
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