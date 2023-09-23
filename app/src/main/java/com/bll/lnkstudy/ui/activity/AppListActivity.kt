package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.activity.download.DownloadAppActivity
import com.bll.lnkstudy.ui.activity.download.DownloadPaintingActivity
import com.bll.lnkstudy.ui.activity.download.DownloadWallpaperActivity
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_list.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 官方应用
 */
class AppListActivity:BaseAppCompatActivity() {

    private val apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null
    private var mAdapterTool:AppListAdapter?=null
    private var toolApps= mutableListOf<AppBean>()

    override fun layoutId(): Int {
        return R.layout.ac_app_list
    }

    override fun initData() {
        apps.addAll(DataBeanManager.appBaseList)
        apps.addAll(AppUtils.scanLocalInstallAppList(this))

        toolApps=MethodManager.getAppTools(this)
    }

    override fun initView() {
        setPageTitle(R.string.download_app)

        initRecyclerView()
        initRecyclerTool()

        iv_back?.setOnClickListener {
            finish()
        }

        tv_add.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    item.isCheck=false
                    if (!AppDaoManager.getInstance().isExist(item.packageName)){
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
    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,5)//创建布局管理
        mAdapter = AppListAdapter(0,R.layout.item_app_list, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                apps[position].isCheck=! apps[position].isCheck
                mAdapter?.notifyItemChanged(position)
            }
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            when(position){
                0->{
                    customStartActivity(Intent(this, DownloadAppActivity::class.java))
                }
                1->{
                    customStartActivity(Intent(this, DownloadWallpaperActivity::class.java))
                }
                2->{
                    customStartActivity(Intent(this, DownloadPaintingActivity::class.java))
                }
                else->{
                    val packageName= apps[position].packageName
                    AppUtils.startAPP(this,packageName)
                }
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            CommonDialog(this).setContent(R.string.toast_uninstall).builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    AppUtils.uninstallAPK(this@AppListActivity,apps[position].packageName)
                }
            })
            true
        }

    }

    private fun initRecyclerTool(){

        rv_tool.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapterTool = AppListAdapter(0,R.layout.item_app_list, toolApps)
        rv_tool.adapter = mAdapterTool
        mAdapterTool?.bindToRecyclerView(rv_tool)
        rv_tool.addItemDecoration(SpaceGridItemDeco(4,60))
        mAdapterTool?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                toolApps[position].isCheck=! toolApps[position].isCheck
                mAdapterTool?.notifyItemChanged(position)
            }
        }

    }

    /**
     * 判断app是否已经存在
     */
    private fun isAppContains(item:AppBean,list: List<AppBean>):Boolean{
        var isContain=false
        for (ite in list){
            if (ite.packageName.equals(item.packageName))
            {
                isContain=true
            }
        }
        return isContain
    }

    override fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.APP_EVENT){
            apps.clear()
            apps.addAll(DataBeanManager.appBaseList)
            apps.addAll(AppUtils.scanLocalInstallAppList(this))
            mAdapter?.setNewData(apps)

            toolApps=MethodManager.getAppTools(this)
            mAdapterTool?.setNewData(toolApps)
        }
    }

}