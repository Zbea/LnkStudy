package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.ui.adapter.CloudDiaryAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_content.rv_list
import java.io.File

class CloudPaintingDrawingFragment : BaseCloudFragment() {

    private var typeStr=""
    private var position=0
    private var cloudLists= mutableListOf<CloudListBean>()
    private var mLocalAdapter: CloudDiaryAdapter?=null

    /**
     * 实例 传送数据
     */
    fun newInstance(typeStr: String): CloudPaintingDrawingFragment {
        val fragment = CloudPaintingDrawingFragment()
        val bundle = Bundle()
        bundle.putString("typeStr", typeStr)
        fragment.arguments = bundle
        return fragment
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        typeStr= arguments?.getString("typeStr") as String
        pageSize=13
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()){
            fetchData()
        }
    }

    /**
     * 本地画本、书法
     */
    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mLocalAdapter = CloudDiaryAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudPaintingDrawingFragment.position=position
                CommonDialog(requireActivity()).setContent("确定下载？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            downloadItem()
                        }
                    })
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudPaintingDrawingFragment.position=position
                if (view.id==R.id.iv_delete){
                    CommonDialog(requireActivity()).setContent("确定删除？").builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteItem()
                            }
                        })
                }
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(30))
    }

    private fun downloadItem(){
        val item=cloudLists[position]
        if (!ItemTypeDaoManager.getInstance().isExist(if (typeStr=="我的画本")3 else 4,item.id)){
            downloadLocal(item)
        }
        else{
            showToast(R.string.toast_downloaded)
        }
    }

    /**
     * 删除云数据
     */
    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(cloudLists[position].id)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载本地画本、书法
     */
    private fun downloadLocal(item:CloudListBean){
        showLoading()
        val typeBean = Gson().fromJson(item.listJson, ItemTypeBean::class.java)
        typeBean.typeId=item.id
        val path=FileAddress().getPathPaintingDraw(if (typeStr=="我的画本")0 else 1,typeBean.typeId)
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, path, object : IZipCallback {
                        override fun onFinish() {
                            typeBean.date=System.currentTimeMillis()
                            typeBean.path=path
                            ItemTypeDaoManager.getInstance().insertOrReplace(typeBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5,typeBean.typeId,1,typeBean.typeId, Gson().toJson(typeBean))

                            //存储画本内容
                            val contents=Gson().fromJson(item.contentJson, object : TypeToken<List<PaintingDrawingBean>>() {}.type) as MutableList<PaintingDrawingBean>
                            for (drawingBean in contents){
                                drawingBean.id=null
                                drawingBean.cloudId=typeBean.typeId
                                drawingBean.path=path+"/"+File(drawingBean.path).name
                                val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(drawingBean)
                                //创建本地画本增量更新
                                DataUpdateManager.createDataUpdate(5,id.toInt(),2,typeBean.typeId, Gson().toJson(drawingBean),drawingBean.path)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            showToast(R.string.book_download_success)
                            hideLoading()
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
                    showToast( R.string.book_download_fail)
                }
            })
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 5
        map["subTypeStr"] = typeStr
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(item: CloudList) {
        cloudLists=item.list
        mLocalAdapter?.setNewData(cloudLists)
        setPageNumber(item.total)
    }

    override fun onCloudDelete() {
        mLocalAdapter?.remove(position)
        onRefreshList(cloudLists)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}