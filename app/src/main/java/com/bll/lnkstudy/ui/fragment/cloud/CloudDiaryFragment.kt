package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.ui.adapter.CloudDiaryAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_list.*
import java.io.File

class CloudDiaryFragment: BaseCloudFragment() {
    private var mAdapter: CloudDiaryAdapter?=null
    private var items= mutableListOf<DiaryBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_list
    }

    override fun initView() {
        pageSize=20
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,50f), DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        mAdapter = CloudDiaryAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                val localItem=DiaryDaoManager.getInstance().queryBean(item.date)
                if (localItem==null){
                    showLoading()
                    download(item)
                }
                else{
                    showToast(getScreenPosition(),"已存在")
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudDiaryFragment.position=position
                CommonDialog(requireActivity()).setContent("确定删除").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            val ids= mutableListOf<Int>()
                            ids.add(items[position].cloudId)
                            mCloudPresenter.deleteCloud(ids)
                        }
                    })
                true
            }
        }
    }

    private fun download(item:DiaryBean){
        item.id=null//设置数据库id为null用于重新加入
        showLoading()
        val fileName=DateUtils.longToString(item.date)
        val zipPath = FileAddress().getPathZip(fileName)
        val fileTargetPath= FileAddress().getPathDiary(fileName)
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
                        override fun onFinish() {
                            DiaryDaoManager.getInstance().insertOrReplace(item)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast(getScreenPosition(),"下载成功")
                                hideLoading()
                            },500)
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                            showToast(getScreenPosition(),msg!!)
                            hideLoading()
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(getScreenPosition(),"下载失败")
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 7
        map["subTypeStr"] = "日记"
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        items.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val diaryBean= Gson().fromJson(item.listJson, DiaryBean::class.java)
                diaryBean.cloudId=item.id
                diaryBean.downloadUrl=item.downloadUrl
                items.add(diaryBean)
            }
        }
        mAdapter?.setNewData(items)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }
}