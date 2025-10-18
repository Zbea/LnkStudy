package com.bll.lnkstudy.ui.fragment.resource

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.model.AppList
import com.bll.lnkstudy.mvp.presenter.DownloadAppPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.DownloadAppAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import org.greenrobot.eventbus.EventBus

class AppDownloadFragment :BaseMainFragment(), IContractView.IAPPView{

    private var index=0
    private var presenter= DownloadAppPresenter(this,getScreenPosition())
    private var mAdapter: DownloadAppAdapter?=null
    private var apps= mutableListOf<AppList.ListBean>()
    private var position=0
    private var supply=1

    override fun onAppList(appBean: AppList) {
        setPageNumber(appBean.total)
        apps=appBean.list
        mAdapter?.setNewData(apps)
    }

    override fun buySuccess() {
        apps[position].buyStatus=1
        mAdapter?.notifyItemChanged(position)
        downLoadStart(apps[position])
    }


    /**
     * 实例 传送数据
     */
    fun newInstance(index:Int): AppDownloadFragment {
        val fragment= AppDownloadFragment()
        val bundle= Bundle()
        bundle.putInt("index",index)
        fragment.arguments=bundle
        return fragment
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initChangeScreenData()
        index= arguments?.getInt("index")!!
        pageSize=8
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            fetchData()
        }
    }

    private fun initRecyclerView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),50f),
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),50f),0)
        layoutParams.weight=1f

        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())//创建布局管理
        mAdapter = DownloadAppAdapter(R.layout.item_download_app, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                this@AppDownloadFragment.position=position
                val app=apps[position]
                if (app.buyStatus==0){
                    val map = HashMap<String, Any>()
                    map["type"] = 4
                    map["bookId"] = app.applicationId
                    presenter.buyApk(map)
                }
                else{
                    if (AppDaoManager.getInstance().queryBeanByPackageName(app.packageName)==null){
                        downLoadStart(app)
                    }
                    else{
                        if (AppUtils.isAvailable(requireActivity(),app.packageName)){
                            showToast("已安装")
                        }
                        else{
                            downLoadStart(app)
                        }
                    }
                }
            }
        }
    }

    //下载应用
    private fun downLoadStart(bean: AppList.ListBean): BaseDownloadTask? {
        val targetFileStr= FileAddress().getPathApk(bean.applicationId.toString())
        showLoading()
        val download = FileDownManager.with(requireActivity()).create(bean.contentUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
            FileDownManager.SingleTaskCallBack {

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun completed(task: BaseDownloadTask?) {
                hideLoading()
                installApk(targetFileStr)
            }
            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                hideLoading()
                showToast(R.string.book_download_fail)
            }
        })
        return download
    }

    //安装apk
    private fun installApk(apkPath: String) {
        AppUtils.installApp(requireActivity(), apkPath)
    }

    /**
     * 改变供应商
     */
    fun changeSupply(supply:Int){
        this.supply=supply
        pageIndex=1
        fetchData()
    }

    override fun initChangeScreenData() {
        super.initChangeScreenData()
        presenter= DownloadAppPresenter(this,getScreenPosition())
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["type"] = supply
        map["subType"]=index
        map["bookType"]=1
        presenter.getAppList(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.APP_INSTALL_EVENT){
            if (index==2){
                val bean=apps[position]
                if (AppDaoManager.getInstance().queryBeanByPackageName(bean.packageName)==null){
                    val item= AppBean()
                    item.appName=bean.nickname
                    item.packageName=bean.packageName
                    item.imageByte= AppUtils.scanLocalInstallAppDrawable(requireActivity(),bean.packageName)
                    item.time=System.currentTimeMillis()
                    item.type=1
                    AppDaoManager.getInstance().insertOrReplace(item)
                    EventBus.getDefault().post(Constants.APP_INSTALL_INSERT_EVENT)
                }
            }
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}