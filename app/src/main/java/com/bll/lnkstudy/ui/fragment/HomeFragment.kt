package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ICommonView
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.ui.adapter.MainDatePlanAdapter
import com.bll.lnkstudy.ui.adapter.MainNoteAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus


/**
 * 首页
 */
class HomeFragment : BaseFragment(), IContractView.IClassGroupView,ICommonView{

    private val mClassGroupPresenter = ClassGroupPresenter(this)
    private val mCommonPresenter=CommonPresenter(this)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var noteAdapter: MainNoteAdapter? = null
    private var bookAdapter: BookAdapter? = null
    private var nowDate = 0L

    override fun onList(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
            DataBeanManager.grades=commonData.grade
        if (!commonData.subject.isNullOrEmpty())
            DataBeanManager.courses=commonData.subject
        if (!commonData.typeGrade.isNullOrEmpty())
            DataBeanManager.typeGrades=commonData.typeGrade
    }

    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        if (DataBeanManager.classGroups != classGroups){
            DataBeanManager.classGroups=classGroups
            EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
        }
    }
    override fun onQuit() {
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        setTitle(R.string.main_home_title)

        initPlanView()
        initNote()
        initTextbookView()

        ll_main_plan.setOnClickListener {
            customStartActivity(Intent(activity, DatePlanListActivity::class.java))
        }

        tv_free_note.setOnClickListener {
            customStartActivity(Intent(activity, FreeNoteActivity::class.java))
        }

    }

    override fun lazyLoad() {
        nowDate=DateUtils.getStartOfDayInMillis()
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            mCommonPresenter.getCommonGrade()
            mClassGroupPresenter.getClassGroupList(false)
        }
        findDataPlan()
        findNotes()
        findTextbooks()
    }

    //今日计划
    private fun initPlanView() {
        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null).apply {
            rv_list_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list_plan.adapter = this
            bindToRecyclerView(rv_list_plan)
        }
    }

    //笔记
    private fun initNote() {
        noteAdapter = MainNoteAdapter(R.layout.item_main_note, null).apply {
            rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
            setOnItemClickListener { adapter, view, position ->
                gotoIntent(noteAdapter?.data?.get(position)!!)
            }
        }
    }

    //课本阅读
    private fun initTextbookView(){
        bookAdapter = BookAdapter(R.layout.item_main_book, null).apply {
            rv_main_textbook.layoutManager = GridLayoutManager(activity,3)//创建布局管理
            rv_main_textbook.adapter = bookAdapter
            bindToRecyclerView(rv_main_textbook)
            rv_main_textbook.addItemDecoration(SpaceGridItemDeco1(3, 0,35))
            setOnItemClickListener { adapter, view, position ->
                val bookBean= bookAdapter?.data?.get(position)
                gotoTextBookDetails(bookBean?.bookId!!)
            }
        }
    }

    /**
     * 查找学习计划
     */
    private fun findDataPlan() {
        val planList = DateEventGreenDaoManager.getInstance().queryAllDateEvent(nowDate)
        val plans = mutableListOf<DatePlan>()
        for (item in planList) {
            //当天时间是否是学习计划选中的星期
            for (week in item.weeks){
                if (DateUtils.getWeek(nowDate)==week.week){
                    plans.addAll(item.plans)
                    break
                }
            }
        }
        mPlanAdapter?.setNewData(plans)
    }

    /**
     * 查找笔记
     */
    private fun findNotes() {
        val notes= NoteDaoManager.getInstance().queryNotesExceptDiary(12)
        noteAdapter?.setNewData(notes)
    }

    private fun findTextbooks(){
        val textbooks= BookGreenDaoManager.getInstance().queryAllTextBook(DataBeanManager.textbookType[0],1,9)
        bookAdapter?.setNewData(textbooks)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            DATE_EVENT -> {
                findDataPlan()
            }
            NOTE_BOOK_MANAGER_EVENT,NOTE_EVENT -> {
                findNotes() //用于删除笔记本后 刷新列表
            }
            TEXT_BOOK_EVENT->{
                findTextbooks()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}