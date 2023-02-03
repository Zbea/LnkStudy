package com.bll.lnkstudy.ui.activity.center

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.AppList
import com.bll.lnkstudy.mvp.presenter.CenterAppPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AppDownloadListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_official_app.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.math.ceil

class OfficialAppActivity:BaseAppCompatActivity(),IContractView.IAPPView{

    private var presenter=CenterAppPresenter(this)
    private var pageCount = 0
    private var pageIndex = 1
    private var pageSize=8
    private var mAdapter:AppDownloadListAdapter?=null
    private var apps= mutableListOf<AppList.ListBean>()
    private var app: AppList.ListBean?=null
    private var currentDownLoadTask:BaseDownloadTask?=null

    override fun onAppList(appBean: AppList?) {
        pageCount = ceil(appBean?.total?.toDouble()!! / pageSize).toInt()
        val totalCount = appBean.total
        if (totalCount == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        apps=appBean?.list
        mAdapter?.setNewData(apps)
    }

    override fun buySuccess() {
        app?.buyStatus=1
        mAdapter?.notifyDataSetChanged()

        if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
            currentDownLoadTask = downLoadStart(app!!)
        } else {
            showToast("正在下载安装")
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_official_app
    }

    override fun initData() {
        fetchData()
    }

    override fun initView() {
        setPageTitle("官方应用")

        initRecyclerView()

        btn_page_up.setOnClickListener {
            if (pageIndex>1){
                if(pageIndex<pageCount){
                    pageIndex-=1
                    fetchData()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }

    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = AppDownloadListAdapter(R.layout.item_app_download, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            app=apps[position]
            if (app?.buyStatus==0){
                val map = HashMap<String, Any>()
                map["type"] = 4
                map["bookId"] = app?.applicationId!!
                presenter.buyApk(map)
            }
            else{
                if (!isInstalled()) {
                    if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
                        currentDownLoadTask = downLoadStart(app!!)
                    } else {
                        showToast("正在下载安装")
                    }
                }
            }
        }
    }

    /**
     * 请求数据
     */
    private fun fetchData(){
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["type"] = 1
        presenter.getAppList(map)
    }

    //下载应用
    private fun downLoadStart(bean: AppList.ListBean): BaseDownloadTask? {

        val targetFileStr= FileAddress().getPathApk(bean.applicationId.toString())

        //看看 是否已经下载
        val listFiles = FileUtils.getFiles(Constants.APK_PATH)
        for (file in listFiles) {
            if (file.name.contains( bean.applicationId.toString())) {
                //已经下载 直接去解析apk 去安装
                installApk(targetFileStr)
                return null
            }
        }
        showLoading()
        val download = FileDownManager.with(this).create(bean.contentUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
            FileDownManager.SingleTaskCallBack {

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun completed(task: BaseDownloadTask?) {
                hideLoading()
                installApk(targetFileStr)
                currentDownLoadTask = null//完成了废弃线程
            }
            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                showToast(e!!.message!!)
            }
        })
        return download
    }

    //安装apk
    private fun installApk(apkPath: String) {
        AppUtils.installApp(this, apkPath)
        EventBus.getDefault().post(Constants.APP_EVENT)
    }

    //是否已经下载安装
    private fun isInstalled(): Boolean {
        val listFile = FileUtils.getFiles(File(Constants.APK_PATH).path)
        if (listFile.size > 0) {
            for (file in listFile) {
                if (file.name.contains("" + app?.applicationId)) {//证明已经下载
                    val packageName = AppUtils.getApkInfo(this, FileAddress().getPathApk(app?.applicationId.toString()))
                    try {
                        AppUtils.startAPP(this, packageName)
                        return true
                    } catch (e: Exception) {
                    }
                }
            }
        }

        return false
    }


}