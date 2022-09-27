package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.APK_PATH
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.FileDownManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.presenter.AppPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AppDownloadListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_app_download.*
import kotlinx.android.synthetic.main.common_page_number.*
import java.io.File

class AppDownloadActivity:BaseAppCompatActivity(),
    IContractView.IAPPView {

    private val presenter=AppPresenter(this)
    private var appBean:AppBean?=null
    private var apps= mutableListOf<AppBean.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var mAdapterDownload:AppDownloadListAdapter?=null
    private var mWallpaperListAdapter:AppDownloadListAdapter?=null
    private var position=0 //当前选择应用位置
    private var currentDownLoadTask: BaseDownloadTask? = null
    private var type=0 //0应用1壁纸

    override fun onAppList(appBean: AppBean?) {
        this.appBean=appBean
        pageCount=appBean?.pageCount!!
        val totalCount=appBean?.totalCount
        if (totalCount==0)
            ll_page_number.visibility= View.GONE
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()

        if (!appBean?.list.isNullOrEmpty()){
            apps=appBean?.list
            if (type==0)
                mAdapterDownload?.setNewData(apps)
            else
                mWallpaperListAdapter?.setNewData(apps)
        }
    }

    override fun onDownBook(appDown: AppBean?) {
        apps[position].applicationId=appDown?.applicationId!!
        apps[position].contentUrl=appDown?.contentUrl
        mAdapterDownload?.notifyDataSetChanged()

        if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
            currentDownLoadTask = downLoadStart(appDown)
        } else {
            showToast("当前有任务正在下载安装")
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_app_download
    }

    override fun initData() {
        getData()
    }

    override fun initView() {
        setPageTitle("应用下载")

        initApp()
        initWallpaper()

        btn_page_up.setOnClickListener {
            if (pageIndex>1){
                if(pageIndex<pageCount){
                    pageIndex-=1
                    getData()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                getData()
            }
        }

        rg_app.setOnCheckedChangeListener { radioGroup, i ->
            if (i==R.id.rb_tool){
                rv_tool.visibility=View.VISIBLE
                rv_wallpaper.visibility=View.GONE
                type=0
            }
            else{
                rv_tool.visibility=View.GONE
                rv_wallpaper.visibility=View.VISIBLE
                type=1
            }
            pageIndex=1
        }


    }

    private fun getData(){
        val map = HashMap<String, Any>()
        map["pageIndex"] = pageIndex
        map["pageSize"] = 12
        presenter.getAppList(map)
    }

    private fun initApp(){
        rv_tool.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapterDownload = AppDownloadListAdapter(R.layout.item_app_download, apps)
        rv_tool.adapter = mAdapterDownload
        mAdapterDownload?.bindToRecyclerView(rv_tool)
        mAdapterDownload?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            //如果没下载 那么就开始下载
            if (!isInstalled(position)) {
                presenter.download(apps[position].id.toString())
            }
        }
    }

    private fun initWallpaper(){
        rv_wallpaper.layoutManager = GridLayoutManager(this,3)//创建布局管理
        mWallpaperListAdapter = AppDownloadListAdapter(R.layout.item_app_wallpaper, apps)
        rv_wallpaper.adapter = mWallpaperListAdapter
        mWallpaperListAdapter?.bindToRecyclerView(rv_wallpaper)
        mWallpaperListAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position

        }
    }

    //下载应用
    private fun downLoadStart(bean: AppBean): BaseDownloadTask? {

        val targetFileStr=FileAddress().getPathApk(bean.applicationId.toString())

        //看看 是否已经下载
        val listFiles = FileUtils.getFiles(APK_PATH)
        for (file in listFiles) {
            if (file.name.contains( bean.applicationId.toString())) {
                //已经下载 直接去解析apk 去安装
                installApk(targetFileStr)
                return null
            }
        }
        mDialog?.show()
        val download = FileDownManager.with(this).create(bean.contentUrl).setPath(targetFileStr).startDownLoad(object : FileDownManager.DownLoadCallBack {

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
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
    }

    //是否已经下载安装
    private fun isInstalled(position: Int): Boolean {
        val filedir = File(APK_PATH)
        val listFile = FileUtils.getFiles(filedir.path)
        if (listFile.size > 0) {
            for (file in listFile) {
                if (file.name.contains("" + apps[position].id)) {//证明已经下载
                    val packageName = AppUtils.getApkInfo(this, FileAddress().getPathApk(apps[position].id.toString()))
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