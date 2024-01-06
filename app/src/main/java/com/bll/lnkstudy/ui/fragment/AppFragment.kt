package com.bll.lnkstudy.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_list.*

class AppFragment:BaseFragment() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter:AppListAdapter?=null
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_app
    }

    override fun initView() {
        setTitle(R.string.main_app)

        initRecyclerView()
    }

    override fun lazyLoad() {
        findApps()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(requireActivity(),5)//创建布局管理
        mAdapter = AppListAdapter(1,R.layout.item_app_list, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val packageName= apps[position].packageName
            AppUtils.startAPP(requireActivity(),packageName)
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            this.position=position
            CommonDialog(requireActivity(),1).setContent(R.string.toast_uninstall).builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    AppUtils.uninstallAPK(requireActivity(),apps[position].packageName)
                }
            })
            true
        }
    }

    private fun findApps(){
        apps=AppUtils.scanLocalInstallAppList(requireActivity())
        mAdapter?.setNewData(apps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_INSTALL_EVENT->{
                lazyLoad()
            }
            Constants.APP_UNINSTALL_EVENT->{
                val item=apps[position]
                AppDaoManager.getInstance().deleteBean(item.packageName)
                mAdapter?.remove(position)
            }
        }
    }
}