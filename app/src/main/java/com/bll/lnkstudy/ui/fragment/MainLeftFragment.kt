package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.CLASSGROUP_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.ClassGroupActivity
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.adapter.MainClassGroupAdapter
import com.bll.lnkstudy.ui.adapter.MainDatePlanAdapter
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.date.LunarSolarConverter
import com.bll.lnkstudy.utils.date.Solar
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.rv_main_group
import kotlinx.android.synthetic.main.fragment_main.tv_date_today
import kotlinx.android.synthetic.main.fragment_main_left.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*


/**
 * 首页
 */
class MainLeftFragment : BaseFragment(), IContractView.IClassGroupView{

    private val mClassGroupPresenter = ClassGroupPresenter(this)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var classGroupAdapter: MainClassGroupAdapter? = null

    //班级回调
    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        if (DataBeanManager.classGroups != classGroups){
            DataBeanManager.classGroups=classGroups
            EventBus.getDefault().post(CLASSGROUP_EVENT)
        }
    }
    override fun onQuit() {
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {
        setTitle(R.string.main_main_title)

        setDateView()

        initPlanView()
        initClassGroupView()


        ll_group.setOnClickListener {
            customStartActivity(Intent(activity, ClassGroupActivity::class.java))
        }

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }



    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            mClassGroupPresenter.getClassGroupList(false)
        }
    }

    //日历相关内容设置
    private fun initPlanView() {

        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null).apply {
            rv_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_plan.adapter = this
            bindToRecyclerView(rv_plan)
        }

        findDateList()

    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView(){
        tv_date_today.text = SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())

        val solar= Solar()
        solar.solarYear= DateUtils.getYear()
        solar.solarMonth=DateUtils.getMonth()
        solar.solarDay=DateUtils.getDay()
        val lunar= LunarSolarConverter.SolarToLunar(solar)
        tv_lunar.text=lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
    }


    //班群管理
    private fun initClassGroupView() {
        classGroupAdapter = MainClassGroupAdapter(R.layout.item_main_classgroup, null).apply {
            rv_main_group.layoutManager = LinearLayoutManager(context)//创建布局管理
            rv_main_group.adapter = this
            bindToRecyclerView(rv_main_group)
        }
    }

    /**
     * 通过当天时间查找本地dateEvent事件集合
     */
    private fun findDateList() {
        val planList = DateEventGreenDaoManager.getInstance().queryAllDateEvent(0)
        val plans = mutableListOf<DatePlan>()
        for (item in planList) {
            plans.addAll(item.plans)
        }
        mPlanAdapter?.setNewData(plans)
    }


    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            DATE_EVENT -> {
                findDateList()
            }
            CLASSGROUP_EVENT->{
                classGroupAdapter?.setNewData(DataBeanManager.classGroups)
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        setDateView()
        lazyLoad()
    }

}