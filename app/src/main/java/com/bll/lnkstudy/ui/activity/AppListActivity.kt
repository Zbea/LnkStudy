package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import kotlinx.android.synthetic.main.ac_app_list.*

class AppListActivity:BaseActivity() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_app_list
    }

    override fun initData() {
        var appBean=AppBean()
        appBean.appName="应用中心"
        appBean.image=getDrawable(R.mipmap.icon_app_center)

        var appBean1=AppBean()
        appBean1.appName="操机技巧"
        appBean1.image=getDrawable(R.mipmap.icon_app_cz)

        var appBean2=AppBean()
        appBean2.appName="官方壁纸"
        appBean2.image=getDrawable(R.mipmap.icon_app_wallpaper)

        apps=AppUtils.scanLocalInstallAppList(this)
        apps.add(0,appBean)
        apps.add(1,appBean1)
        apps.add(2,appBean2)
    }

    override fun initView() {
        setTitle("应用中心")

        initRecycler()

    }


    private fun initRecycler(){
        rv_list.layoutManager = GridLayoutManager(this,5)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if (position==0)
            {
                startActivity(Intent(this,AppDownloadActivity::class.java))
            }
            else{
                val packageName= apps[position].packageName
                AppUtils.startAPP(this,packageName)
            }

        }
    }




}