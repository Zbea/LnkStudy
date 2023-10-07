package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_list.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 应用工具
 */
class AppToolActivity:BaseAppCompatActivity() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null
    private var mAdapterTool:AppListAdapter?=null
    private var toolApps= mutableListOf<AppBean>()

    override fun layoutId(): Int {
        return R.layout.ac_app_list
    }

    override fun initData() {
        apps=MethodManager.getAppTools(this,0)
        toolApps=MethodManager.getAppTools(this,1)
    }

    override fun initView() {
        setPageTitle(R.string.tool)

        initRecyclerView()
        initRecyclerTool()

        iv_back?.setOnClickListener {
            finish()
        }

        tv_add.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    item.isCheck=false
                    item.isTool=true
                    if (!AppDaoManager.getInstance().isTool(item.packageName)){
                        AppDaoManager.getInstance().insertOrReplace(item)
                        toolApps.add(item)
                    }
                }
            }
            mAdapter?.notifyDataSetChanged()
            mAdapterTool?.notifyDataSetChanged()
        }

        tv_out.setOnClickListener {
            val iterator=toolApps.iterator()
            while (iterator.hasNext()){
                val item=iterator.next()
                if (item.isCheck){
                    item.isTool=false
                    AppDaoManager.getInstance().insertOrReplace(item)
                    iterator.remove()
                }
            }
            mAdapterTool?.notifyDataSetChanged()
        }

    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = AppListAdapter(0,R.layout.item_app_list, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(6,50))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                apps[position].isCheck=! apps[position].isCheck
                mAdapter?.notifyItemChanged(position)
            }
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val packageName= apps[position].packageName
            AppUtils.startAPP(this,packageName)
        }

    }

    private fun initRecyclerTool(){
        rv_list_tool.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapterTool = AppListAdapter(0,R.layout.item_app_list, toolApps)
        rv_list_tool.adapter = mAdapterTool
        mAdapterTool?.bindToRecyclerView(rv_list_tool)
        rv_list_tool.addItemDecoration(SpaceGridItemDeco(6,30))
        mAdapterTool?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                toolApps[position].isCheck=! toolApps[position].isCheck
                mAdapterTool?.notifyItemChanged(position)
            }
        }

    }

    private fun setDataApp(){
        apps=MethodManager.getAppTools(this,0)
        mAdapter?.setNewData(apps)
    }

    private fun setDataAppTool(){
        toolApps=MethodManager.getAppTools(this,1)
        mAdapterTool?.setNewData(toolApps)
    }

    override fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.APP_UNINSTALL_EVENT->{
                setDataApp()
                setDataAppTool()
            }
            Constants.APP_INSERT_EVENT->{
                setDataApp()
            }
        }
    }

}