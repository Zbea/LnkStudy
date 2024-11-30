package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_content.rv_list
import java.io.File

class CloudExamFragment:BaseCloudFragment() {

    private var mAdapter:PaperTypeAdapter?=null
    private var course=""
    private var position=0
    private var paperTypes= mutableListOf<PaperTypeBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=6
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            mCloudPresenter.getType(3)
        }
    }

    private fun initTab(){
        course=types[0]
        for (i in types.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=types[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        course=types[position]
        pageIndex=1
        fetchData()
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,30f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 2)
        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudExamFragment.position=position
                CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            deleteItem()
                        }
                    })
                true
            }
            setOnItemClickListener { adapter, view, position ->
                this@CloudExamFragment.position=position
                CommonDialog(requireActivity()).setContent("确定下载？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            downloadItem()
                        }
                    })
            }
        }
    }

    private fun downloadItem(){
        val paperTypeBean=paperTypes[position]
        val item= PaperTypeDaoManager.getInstance().queryById(paperTypeBean.typeId)
        if (item==null){
            download(paperTypeBean)
        }
        else{
            showToast(R.string.toast_downloaded)
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(paperTypes[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载考试卷
     */
    private fun download(item:PaperTypeBean){
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        val fileTargetPath= FileAddress().getPathTestPaper(item.typeId)
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
                            item.id=null//设置数据库id为null用于重新加入
                            item.date=System.currentTimeMillis()
                            PaperTypeDaoManager.getInstance().insertOrReplace(item)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(3,item.typeId,1,Gson().toJson(item))

                            val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                            for (json in jsonArray){
                                val paperBean=Gson().fromJson(json, PaperBean::class.java)
                                paperBean.id=null//设置数据库id为null用于重新加入
                                PaperDaoManager.getInstance().insertOrReplace(paperBean)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(3,paperBean.contentId,2,paperBean.typeId,Gson().toJson(item),paperBean.filePath)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast(R.string.book_download_success)
                                hideLoading()
                            },500)
                        }

                        override fun onProgress(percentDone: Int) {
                        }

                        override fun onError(msg: String?) {
                            showToast(msg!!)
                            hideLoading()
                        }

                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(R.string.book_download_fail)
                }
            })

    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 3
        map["grade"] = grade
        map["subTypeStr"] = course
        mCloudPresenter.getList(map)
    }

    override fun onCloudType(types: MutableList<String>) {
        this.types=types
        if (types.size>0)
            initTab()
    }

    override fun onCloudList(item: CloudList) {
        setPageNumber(item.total)
        paperTypes.clear()
        for (type in item.list){
            if (type.listJson.isNotEmpty()){
                val paperTypeBean= Gson().fromJson(type.listJson, PaperTypeBean::class.java)
                paperTypeBean.cloudId=type.id
                paperTypeBean.isCloud=true
                paperTypeBean.downloadUrl=type.downloadUrl
                paperTypeBean.contentJson=type.contentJson
                paperTypeBean.contentSubtypeJson=type.contentSubtypeJson
                paperTypes.add(paperTypeBean)
            }
        }
        mAdapter?.setNewData(paperTypes)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}