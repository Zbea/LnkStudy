package com.bll.lnkstudy.ui.activity.date

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DatePlanCopyDialog
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.ui.adapter.DatePlanListAdapter
import kotlinx.android.synthetic.main.ac_date_plan_list.*
import kotlinx.android.synthetic.main.common_fragment_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DatePlanListActivity:BaseAppCompatActivity() {

    private var popWindowList: PopWindowList?=null
    private var popWindowBeans = mutableListOf<PopWindowBean>()
    private var mCopyDialog:DatePlanCopyDialog?=null

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
        plans= DateEventGreenDaoManager.getInstance(this).queryAllDateEvent(0)
        mAdapter?.setNewData(plans)
    }

    /**
     * 复制
     */
    private fun setCopy(){
        DatePlanCopyDialog(this,plans).builder().setOnSelectorListener {
            val dateEvent=plans[it]
            dateEvent.id=null
            dateEvent.title=dateEvent?.title+"(1)"

            plans.add(dateEvent)
            mAdapter?.notifyDataSetChanged()
            DateEventGreenDaoManager.getInstance(this).insertOrReplaceDateEvent(dateEvent)
//            EventBus.getDefault().post(Constants.DATE_EVENT)
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