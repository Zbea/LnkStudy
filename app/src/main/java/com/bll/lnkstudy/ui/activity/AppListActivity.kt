package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AppListActivity:BaseActivity() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null
    private var mAdapterTool:AppListAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_app_list
    }

    override fun initData() {
        apps= DataBeanManager.getIncetance().appBaseList
        apps.addAll(AppUtils.scanLocalInstallAppList(this))
    }

    override fun initView() {
        setPageTitle("应用")

        initRecycler()
        initRecyclerTool()

        tv_add.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    item.isCheck=false
                    if (!isAppContains(item,toolApps)){
                        AppDaoManager.getInstance().insertOrReplace(item)
                        item.id=AppDaoManager.getInstance().insertId
                        toolApps.add(item)
                    }
                }
            }
            mAdapter?.notifyDataSetChanged()
            mAdapterTool?.notifyDataSetChanged()
        }

        tv_remove.setOnClickListener {

            val it=toolApps.iterator()
            while (it.hasNext()){
                val item=it.next()
                if (item.isCheck){
                    AppDaoManager.getInstance().deleteBean(item)
                    it.remove()
                }
            }
            mAdapterTool?.notifyDataSetChanged()
        }

    }

    @SuppressLint("WrongConstant")
    private fun initRecycler(){

        rv_list.layoutManager = GridLayoutManager(this,5)//创建布局管理
        mAdapter = AppListAdapter(0,R.layout.item_app_list, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,70))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_image){
                when(position){
                    0->{
                        customStartActivity(Intent(this,AppDownloadActivity::class.java))
                    }
                    1->{

                    }
                    2->{
                        customStartActivity(Intent(this,AppOfficialActivity::class.java).setFlags(0))
                    }
                    3->{
                        customStartActivity(Intent(this,AppOfficialActivity::class.java).setFlags(1))
                    }
                    4->{
                        customStartActivity(Intent(this,AppOfficialBookActivity::class.java))
                    }
                    else->{
                        val packageName= apps[position].packageName
                        AppUtils.startAPP(this,packageName)
                    }
                }
            }
            if (view.id==R.id.cb_check){
                apps[position].isCheck=! apps[position].isCheck
                mAdapter?.notifyItemChanged(position)
            }
        }

    }

    private fun initRecyclerTool(){

        rv_tool.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapterTool = AppListAdapter(0,R.layout.item_app_list, toolApps)
        rv_tool.adapter = mAdapterTool
        mAdapterTool?.bindToRecyclerView(rv_tool)
        rv_tool.addItemDecoration(SpaceGridItemDeco(0,60))
        mAdapterTool?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                toolApps[position].isCheck=! toolApps[position].isCheck
                mAdapterTool?.notifyItemChanged(position)
            }
        }

    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.APP_EVENT){
            getAppTool()
            mAdapterTool?.setNewData(toolApps)
        }
    }

}