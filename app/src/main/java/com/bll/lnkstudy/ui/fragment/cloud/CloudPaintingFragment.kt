package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.ui.adapter.PaintingMyAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class CloudPaintingFragment : BaseCloudFragment() {

    private var typeStr=""
    private var position=0
    private var paintings= mutableListOf<PaintingBean>()
    private var mAdapter: PaintingMyAdapter?=null


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=9
        typeStr="我的书画"
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
        layoutParams.setMargins(DP2PX.dip2px(activity,10f), DP2PX.dip2px(activity,60f), DP2PX.dip2px(activity,10f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 3)//创建布局管理
        mAdapter = PaintingMyAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
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
                this@CloudPaintingFragment.position=position
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
        rv_list.addItemDecoration(SpaceGridItemDeco(3,70))
    }

    private fun downloadItem(){
        val item=paintings[position]
        if (PaintingBeanDaoManager.getInstance().queryBean(item.contentId)==null){
            download(item)
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
        ids.add(paintings[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载本地画本、书法
     */
    private fun download(item:PaintingBean){
        showLoading()
        val images = item.bodyUrl.split(",")
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(item.paths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    PaintingBeanDaoManager.getInstance().insertOrReplace(item)
                    showToast(R.string.book_download_success)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(R.string.book_download_fail)
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

    override fun onCloudList(cloudList: CloudList) {
        paintings.clear()
        for (item in cloudList.list){
            val paintingBean= Gson().fromJson(item.listJson, PaintingBean::class.java)
            paintingBean.id=null
            paintingBean.cloudId=item.id
            paintings.add(paintingBean)
        }
        mAdapter?.setNewData(paintings)
        setPageNumber(cloudList.total)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(paintings)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}