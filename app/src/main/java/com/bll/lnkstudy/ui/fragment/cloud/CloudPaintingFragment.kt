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
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.ui.adapter.CloudPaintingLocalAdapter
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

class CloudPaintingFragment : BaseCloudFragment() {

    private var typeStr=""
    private var tabId=3
    private var position=0
    private var cloudLists= mutableListOf<CloudListBean>()
    private var mLocalAdapter: CloudPaintingLocalAdapter?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=9
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            fetchData()
        }
    }

    private fun initTab(){
        types.add(getString(R.string.my_drawing_str))
        types.add(getString(R.string.my_calligraphy_str))
        typeStr=types[0]
        for (i in types.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=types[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex = 1
        tabId=if (position==0) 3 else 4
        typeStr=itemTabTypes[position].title
        fetchData()
    }

    /**
     * 本地画本、书法
     */
    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity, 20f),
            DP2PX.dip2px(activity, 40f),
            DP2PX.dip2px(activity, 20f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mLocalAdapter = CloudPaintingLocalAdapter(R.layout.item_painting_type, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3, 60))
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
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
                CommonDialog(requireActivity(),getScreenPosition()).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            deleteItem()
                        }
                    })
                true
            }
        }
    }

    private fun downloadItem(){
        val item=cloudLists[position]
        val paintType=ItemTypeDaoManager.getInstance().queryBean(tabId,item.grade)
        if (paintType==null){
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
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, typeBean.path, object : IZipCallback {
                        override fun onFinish() {
                            typeBean.id=null
                            ItemTypeDaoManager.getInstance().insertOrReplace(typeBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5,typeBean.typeId,1, Gson().toJson(typeBean))

                            //存储画本内容
                            val jsonArray=JsonParser().parse(item.contentJson).asJsonArray
                            for (json in jsonArray){
                                val drawingBean=Gson().fromJson(json, PaintingDrawingBean::class.java)
                                drawingBean.id=null
                                val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(drawingBean)
                                //创建本地画本增量更新
                                DataUpdateManager.createDataUpdate(5,id.toInt(),2,typeBean.typeId, Gson().toJson(drawingBean),drawingBean.path)
                            }

                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                deleteItem()
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

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        cloudLists=cloudList.list
        mLocalAdapter?.setNewData(cloudLists)
    }

    override fun onCloudDelete() {
        mLocalAdapter?.remove(position)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}