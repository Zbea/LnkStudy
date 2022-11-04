package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.APK_PATH
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.mvp.model.AppListBean
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.mvp.presenter.AppPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AppDownloadListAdapter
import com.bll.lnkstudy.ui.adapter.AppWallpaperListAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_app_download.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.File

class AppDownloadActivity:BaseAppCompatActivity(),
    IContractView.IAPPView {

    private val presenter=AppPresenter(this)
    private var appBean:AppListBean?=null
    private var apps= mutableListOf<AppListBean.ListBean>()
    private var wallpapers= mutableListOf<AppListBean.ListBean>()
    private var paintings= mutableListOf<AppListBean.ListBean>()
    private var pageCount = 0
    private var pageIndex = 1 //当前页码
    private var mAdapterDownload:AppDownloadListAdapter?=null
    private var mWallpaperListAdapter:AppWallpaperListAdapter?=null
    private var mPaintingAdapter:AppWallpaperListAdapter?=null
    private var position=0 //当前选择应用位置
    private var currentDownLoadTask: BaseDownloadTask? = null
    private var type=0 //0应用1壁纸

    private var popTimes= mutableListOf<PopWindowBean>()
    private var popPaintings= mutableListOf<PopWindowBean>()
    private var popWindowTime:PopWindowList?=null
    private var popWindowPainting:PopWindowList?=null

    override fun onAppList(appBean: AppListBean?) {
        this.appBean=appBean
        pageCount=appBean?.pageCount!!
        val totalCount=appBean?.totalCount
        if (totalCount==0)
            ll_page_number.visibility= View.GONE
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()

        if (!appBean?.list.isNullOrEmpty()){
            apps=appBean?.list
            mAdapterDownload?.setNewData(apps)
        }
    }

    override fun onDown(appDown: AppListBean?) {
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

        for (i in 1..12){
            val item =AppListBean.ListBean()
            item.name="壁纸$i"
            item.images=null
            item.price=i
            wallpapers.add(item)
        }

        for (i in 1..12){
            val item =AppListBean.ListBean()
            item.name="书法$i"
            item.price=i
            paintings.add(item)
        }

        val yeas= DataBeanManager.getIncetance().YEARS
        for (i in yeas.indices){
            popTimes.add(PopWindowBean(i,yeas[i],i==0))
        }

        val paintings= DataBeanManager.getIncetance().PAINTING
        for (i in paintings.indices){
            popPaintings.add(PopWindowBean(i,paintings[i],i==0))
        }

    }

    override fun initView() {
        setPageTitle("应用下载")

        initApp()
        initWallpaper()
        initPainting()

        tv_time.text=popTimes[0].name
        tv_time.setOnClickListener {
            selectorTime()
        }
        tv_painting_type.text=popPaintings[0].name
        tv_painting_type.setOnClickListener {
            selectorPainting()
        }

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

        rg_app.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.rb_tool->{
                    showView(rv_tool)
                    disMissView(rv_wallpaper,rv_painting,ll_painting)
                    type=0
                }
                R.id.rb_wallpaper->{
                    showView(rv_wallpaper)
                    disMissView(rv_tool,rv_painting,ll_painting)
                    type=1
                }
                else->{
                    showView(rv_painting,ll_painting)
                    disMissView(rv_tool,rv_wallpaper)
                    type=2
                }
            }
            pageIndex=1
        }


    }

    private fun getData(){
        val map = HashMap<String, Any>()
        map["pageIndex"] = pageIndex
        map["pageSize"] = Constants.PAGE_SIZE
        presenter.getAppList(map)
    }

    private fun initApp(){
        rv_tool.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapterDownload = AppDownloadListAdapter(R.layout.item_app_download, null)
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
        rv_wallpaper.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mWallpaperListAdapter = AppWallpaperListAdapter(R.layout.item_app_wallpaper, wallpapers,0)
        rv_wallpaper.adapter = mWallpaperListAdapter
        rv_wallpaper.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),30))
        mWallpaperListAdapter?.bindToRecyclerView(rv_wallpaper)
        mWallpaperListAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position

        }
    }

    private fun initPainting(){
        rv_painting.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mPaintingAdapter = AppWallpaperListAdapter(R.layout.item_app_wallpaper, paintings,1)
        rv_painting.adapter = mPaintingAdapter
        rv_painting.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(this,22f),30))
        mPaintingAdapter?.bindToRecyclerView(rv_painting)
        mPaintingAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position

        }
    }


    /**
     * 朝代选择器
     */
    private fun selectorTime(){
        if (popWindowTime==null)
        {
            popWindowTime= PopWindowList(this,popTimes,tv_time,5).builder()
            popWindowTime?.setOnSelectListener { item ->
                tv_time.text=item.name
            }
        }
        else{
            popWindowTime?.show()
        }
    }

    /**
     * 书画选择器
     */
    private fun selectorPainting(){
        if (popWindowPainting==null)
        {
            popWindowPainting= PopWindowList(this,popPaintings,tv_painting_type,5).builder()
            popWindowPainting?.setOnSelectListener { item ->
                tv_painting_type.text=item.name
            }
        }
        else{
            popWindowPainting?.show()
        }
    }

    //下载应用
    private fun downLoadStart(bean: AppListBean): BaseDownloadTask? {

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
        showLoading()
        val download = FileDownManager.with(this).create(bean.contentUrl).setPath(targetFileStr).startDownLoad(object : FileDownManager.DownLoadCallBack {

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