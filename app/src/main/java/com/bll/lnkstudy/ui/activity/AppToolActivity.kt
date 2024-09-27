package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_list.rv_list
import kotlinx.android.synthetic.main.ac_app_list.rv_list_tool
import kotlinx.android.synthetic.main.ac_app_list.tv_add
import kotlinx.android.synthetic.main.ac_app_list.tv_out

/**
 * 应用工具
 */
class AppToolActivity:BaseAppCompatActivity() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null
    private var mAdapterTool:AppListAdapter?=null
    private var toolApps= mutableListOf<AppBean>()
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_app_list
    }

    override fun initData() {
        if (!AppDaoManager.getInstance().isExist(Constants.PACKAGE_GEOMETRY)){
            AppDaoManager.getInstance().insertOrReplace(AppBean().apply {
                appName="几何绘图"
                imageByte = BitmapUtils.drawableToByte(getDrawable(R.mipmap.icon_app_geometry))
                packageName=Constants.PACKAGE_GEOMETRY
                isTool=false
            })
        }

    }

    override fun initView() {
        setPageTitle(DataBeanManager.resources[0])

        initRecyclerView()
        initRecyclerTool()

        tv_add.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    item.isTool=true
                    if (!AppDaoManager.getInstance().isTool(item.packageName)){
                        AppDaoManager.getInstance().insertOrReplace(item)
                    }
                }
            }
            setDataApp()
            setDataAppTool()
        }

        tv_out.setOnClickListener {
            for (item in toolApps){
                if (item.isCheck){
                    item.isTool=false
                    AppDaoManager.getInstance().insertOrReplace(item)
                }
            }
            setDataApp()
            setDataAppTool()
        }

        setDataApp()
        setDataAppTool()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(6,50))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=apps[position]
            when(view.id){
                R.id.ll_name->{
                    item.isCheck=!item.isCheck
                    mAdapter?.notifyItemChanged(position)
                }
                R.id.iv_image->{
                    val packageName= item.packageName
                    if (packageName!=Constants.PACKAGE_GEOMETRY)
                        AppUtils.startAPP(this,packageName)
                }
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            this.position=position
            val item=apps[position]
            val packageName= item.packageName
            if (packageName!=Constants.PACKAGE_GEOMETRY){
                CommonDialog(this).setContent("卸载应用？").builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        AppUtils.uninstallAPK(this@AppToolActivity,packageName)
                    }
                })
            }
            true
        }

    }

    private fun initRecyclerTool(){
        rv_list_tool.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapterTool = AppListAdapter(R.layout.item_app_list, null)
        rv_list_tool.adapter = mAdapterTool
        mAdapterTool?.bindToRecyclerView(rv_list_tool)
        rv_list_tool.addItemDecoration(SpaceGridItemDeco(6,30))
        mAdapterTool?.setOnItemChildClickListener { adapter, view, position ->
            val item=toolApps[position]
            when(view.id){
                R.id.ll_name->{
                    item.isCheck=!item.isCheck
                    mAdapterTool?.notifyItemChanged(position)
                }
                R.id.iv_image->{
                    val packageName= item.packageName
                    if (packageName!=Constants.PACKAGE_GEOMETRY)
                        AppUtils.startAPP(this,packageName)
                }
            }
        }

    }

    private fun setDataApp(){
        apps=MethodManager.getAppTools(this,0)
        for (item in apps){
            item.isCheck=false
        }
        mAdapter?.setNewData(apps)
    }

    private fun setDataAppTool(){
        toolApps=MethodManager.getAppTools(this,1)
        for (item in toolApps){
            item.isCheck=false
        }
        mAdapterTool?.setNewData(toolApps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_UNINSTALL_EVENT->{
                AppDaoManager.getInstance().deleteBean(apps[position])
                setDataApp()
                setDataAppTool()
            }
            Constants.APP_INSERT_EVENT->{
                setDataApp()
            }
        }
    }

}