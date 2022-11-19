package com.bll.lnkstudy.ui.activity.date

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.DateEvent
import com.bll.lnkstudy.ui.adapter.DateDayListAdapter
import kotlinx.android.synthetic.main.ac_date_day_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DateDayListActivity:BaseAppCompatActivity() {

    private var mAdapter:DateDayListAdapter?=null
    private var days= mutableListOf<DateEvent>()

    override fun layoutId(): Int {
        return R.layout.ac_date_day_list
    }

    override fun initData() {
    }

    override fun initView() {
        EventBus.getDefault().register(this)

        setPageTitle("重要日子")
        setPageSetting("添加")

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
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent=Intent(this,DateDayDetailsActivity::class.java)
            intent.addFlags(1)
            var bundle = Bundle()
            bundle.putSerializable("dateEvent", days[position])
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
    }

    private fun findDatas(){
        days=DateEventGreenDaoManager.getInstance(this).queryAllDateEvent(1)
        mAdapter?.setNewData(days)
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