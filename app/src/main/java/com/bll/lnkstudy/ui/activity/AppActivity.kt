package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.APK_PATH
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.FileDownManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.presenter.AppPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AppListAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.utilssdk.utils.AppUtils
import com.bll.utilssdk.utils.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_app.rv_list
import kotlinx.android.synthetic.main.common_page_number.*
import java.io.File

class AppActivity:BaseActivity(),
    IContractView.IAPPViewI {

    private val presenter=AppPresenter(this)
    private var appBean:AppBean?=null
    private var apps= mutableListOf<AppBean.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var mAdapter:AppListAdapter?=null
    private var position=0
    private var currentDownLoadTask: BaseDownloadTask? = null

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
            mAdapter?.setNewData(apps)
        }
    }

    override fun onDownBook(appDown: AppBean?) {
        apps[position].applicationId=appDown?.applicationId!!
        apps[position].contentUrl=appDown?.contentUrl
        mAdapter?.notifyDataSetChanged()

        if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
            currentDownLoadTask = downLoadStart(appDown)
        } else {
            showToast("当前有任务正在下载安装")
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_app
    }

    override fun initData() {
        getData()
    }

    override fun initView() {
        setPageTitle("应用下载")

        initRecyclerView()

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
    }

    private fun getData(){
        val map = HashMap<String, Any>()
        map["pageIndex"] = pageIndex
        map["pageSize"] = 12
        presenter.getAppList(map)
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_bookstore, apps)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(0,55))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            //如果没下载 那么就开始下载
            if (!isInstalled(position)) {
                presenter.download(apps[position].id.toString())
            }
        }
    }

    //下载应用
    private fun downLoadStart(entity: AppBean): BaseDownloadTask? {

        //看file 是否创建目录
        val filedir = File(APK_PATH)
        if (!filedir.exists()) {
            filedir.mkdir()
        }
        //看看 是否已经下载
        val listFiles = FileUtils.getFiles(filedir.path)
        for (file in listFiles) {
            if (file.name.contains("" + entity.applicationId)) {
                //已经下载 直接去解析apk 去安装
                installApk(filedir.path + File.separator + entity.applicationId + ".apk")
                return null
            }
        }

        val targetFileStr = filedir.path + File.separator + entity.applicationId + ".apk"
        val targetFile = File(targetFileStr)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        mDialog?.show()
        val download = FileDownManager.with(this).create(entity.contentUrl).setPath(targetFileStr)
                .startDownLoad(object : FileDownManager.DownLoadCallBack {

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

        if (filedir.exists()) {//证明已经下载过

            val listFile = FileUtils.getFiles(filedir.path)
            if (listFile.size > 0) {
                for (file in listFile) {
                    if (file.name.contains("" + apps[position].id)) {//证明已经下载
                        val packageName = AppUtils.getApkInfo(this, filedir.path + File.separator + "" + apps[position].id + ".apk")
                        try {
                            AppUtils.startAPP(this, packageName)
                            return true
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }

        return false
    }


}