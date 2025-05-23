package com.bll.lnkstudy.ui.fragment.resource

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.DownloadCalenderDialog
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.mvp.model.CalenderItemBean
import com.bll.lnkstudy.mvp.model.CalenderList
import com.bll.lnkstudy.mvp.presenter.CalenderPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.CalenderListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_list.rv_list
import java.io.File
import java.text.DecimalFormat

class CalenderDownloadFragment:BaseMainFragment(), IContractView.ICalenderView {

    private var presenter=CalenderPresenter(this,getScreenPosition())
    private var items= mutableListOf<CalenderItemBean>()
    private var mAdapter:CalenderListAdapter?=null
    private var detailsDialog:DownloadCalenderDialog?=null
    private var position=0
    private var supply=1
    private var type=1

    override fun onList(list: CalenderList) {
        setPageNumber(list.total)
        for (item in list.list){
            item.pid=item.id.toInt()
            item.id=null
        }
        items=list.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initChangeScreenData()
        pageSize=12
        initRecycleView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            fetchData()
        }
    }

    private fun initRecycleView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 4)//创建布局管理
        mAdapter = CalenderListAdapter(R.layout.item_calendar, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                val urls=item.previewUrl.split(",")
                ImageDialog(requireActivity(),urls).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CalenderDownloadFragment.position=position
                val item=items[position]
                if (view.id==R.id.tv_buy){
                    showDetails(item)
                }
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(requireActivity(), 50f)))
    }


    private fun showDetails(item: CalenderItemBean) {
        detailsDialog = DownloadCalenderDialog(requireActivity(), item)
        detailsDialog?.builder()
        detailsDialog?.setOnClickListener {
            if (item.buyStatus==1){
                if (!CalenderDaoManager.getInstance().isExist(item.pid)) {
                    downLoadStart(item.downloadUrl,item)
                } else {
                    item.loadSate =2
                    showToast("已下载")
                    mAdapter?.notifyItemChanged(position)
                    detailsDialog?.setDissBtn()
                }
            }
            else{
                val map = HashMap<String, Any>()
                map["type"] = 7
                map["bookId"] = item.pid
                presenter.buy(map)
            }
        }
    }


    private fun downLoadStart(url: String, item: CalenderItemBean): BaseDownloadTask? {
        showLoading()
        val fileName = MD5Utils.digest(item.pid.toString())//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val download = FileDownManager.with(requireActivity()).create(url).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning) {
                        requireActivity().runOnUiThread {
                            val s = getFormatNum(soFarBytes.toDouble() / (1024 * 1024),) + "/" +
                                    getFormatNum(totalBytes.toDouble() / (1024 * 1024),)
                            detailsDialog?.setUnClickBtn(s)
                        }
                    }
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val fileTargetPath = FileAddress().getPathCalender(fileName)
                    unzip(item, zipPath, fileTargetPath)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast("${item.title}下载失败")
                    detailsDialog?.setChangeStatus()
                }
            })
        return download
    }

    private fun unzip(item: CalenderItemBean, zipPath: String, fileTargetPath: String) {
        ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
                item.apply {
                    loadSate = 2
                    date = System.currentTimeMillis()//下载时间用于排序
                    path = fileTargetPath
                }
                CalenderDaoManager.getInstance().insertOrReplace(item)
                //更新列表
                mAdapter?.notifyDataSetChanged()
                detailsDialog?.dismiss()
                FileUtils.deleteFile(File(zipPath))
                Handler().postDelayed({
                    hideLoading()
                    showToast(item.title+"下载成功")
                },500)
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onError(msg: String?) {
                hideLoading()
                showToast(item.title+msg!!)
                detailsDialog?.setChangeStatus()
            }
            override fun onStart() {
            }
        })
    }


    fun getFormatNum(pi: Double): String? {
        val df = DecimalFormat("0.0M")
        return df.format(pi)
    }

    /**
     * 改变供应商
     */
    fun changeSupply(supply:Int){
        this.supply=supply
        pageIndex=1
        fetchData()
    }

    fun changeType(type:Int){
        this.type=type
        pageIndex=1
        fetchData()
    }

    override fun initChangeScreenData() {
        super.initChangeScreenData()
        presenter=CalenderPresenter(this,getScreenPosition())
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["type"] = supply
        map["ageType"]=type
        map["mainType"]=1
        presenter.getList(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}