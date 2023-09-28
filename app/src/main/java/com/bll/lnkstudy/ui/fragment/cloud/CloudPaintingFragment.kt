package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.manager.PaintingTypeDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.mvp.model.painting.PaintingTypeBean
import com.bll.lnkstudy.ui.activity.CloudStorageActivity
import com.bll.lnkstudy.ui.adapter.CloudPaintingLocalAdapter
import com.bll.lnkstudy.ui.adapter.MyPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_my_painting_list.rv_list
import kotlinx.android.synthetic.main.common_radiogroup_fragment.*
import kotlinx.android.synthetic.main.fragment_cloud_content.*
import java.io.File

class CloudPaintingFragment : BaseCloudFragment() {

    var typeId = 1
    private var dynasty = 1
    private var position=0
    private var cloudLists= mutableListOf<CloudListBean>()
    private var mAdapter:MyPaintingAdapter?=null
    private var mLocalAdapter: CloudPaintingLocalAdapter?=null
    private var paintings= mutableListOf<PaintingBean>()//线上数据

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=6
        initTab()
        initRecyclerPaintingView()
        initRecyclerLocalView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab() {
        val types = mutableListOf<String>()
        types.addAll(DataBeanManager.PAINTING.toList())
        types.add(getString(R.string.my_drawing_str))
        types.add(getString(R.string.my_calligraphy_str))
        for (i in types.indices) {
            rg_group.addView(getRadioButton(i, types[i], types.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId = id+1
            when (typeId) {
                1, 2, 3, 4, 5,6 -> {
                    pageSize=6
                    showView(rv_list)
                    disMissView(rv_local)
                    (activity as CloudStorageActivity).showDynastyView()
                }
                else -> {
                    pageSize=9
                    showView(rv_local)
                    disMissView(rv_list)
                    (activity as CloudStorageActivity).closeDynastyView()
                }
            }
            pageIndex = 1
            fetchData()
        }
    }

    /**
     * 线上书画
     */
    private fun initRecyclerPaintingView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,50f),
            DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
        mAdapter = MyPaintingAdapter(R.layout.item_download_painting,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(2, DP2PX.dip2px(activity,20f),100))
            setOnItemClickListener { adapter, view, position ->
                val item=paintings[position]
                val paintingBean=PaintingBeanDaoManager.getInstance().queryBean(item.contentId)
                if (paintingBean==null){
                    downloadPainting(item)
                }
                else{
                    showToast(getScreenPosition(),R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
                deleteCloud(position)
                true
            }
        }
    }

    /**
     * 本地画本、书法
     */
    private fun initRecyclerLocalView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity, 20f),
            DP2PX.dip2px(activity, 40f),
            DP2PX.dip2px(activity, 20f), 0
        )
        layoutParams.weight = 1f
        rv_local.layoutParams = layoutParams
        rv_local.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mLocalAdapter = CloudPaintingLocalAdapter(R.layout.item_painting_type, null).apply {
            rv_local.adapter = this
            bindToRecyclerView(rv_local)
            rv_local.addItemDecoration(SpaceGridItemDeco(3, 60))
            setOnItemClickListener { adapter, view, position ->
                val item=cloudLists[position]
                val paintType=PaintingTypeDaoManager.getInstance().queryAllByGrade(getType(),item.grade)
                if (paintType==null){
                    downloadLocal(item)
                }
                else{
                    showToast(getScreenPosition(),R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
                deleteCloud(position)
                true
            }
        }
    }

    /**
     * 下载线上书画
     */
    private fun downloadPainting(item: PaintingBean){
        showLoading()
        val pathStr= FileAddress().getPathImage("painting" ,item.contentId)
        val images = mutableListOf(item.bodyUrl)
        val savePaths= arrayListOf("$pathStr/1.png")
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    val id=PaintingBeanDaoManager.getInstance().insertOrReplaceGetId(item)
                    //新建增量更新
                    DataUpdateManager.createDataUpdateSource(7,id.toInt(),1,item.contentId, Gson().toJson(item),item.bodyUrl)
                    showToast(1,R.string.book_download_success)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(1,R.string.book_download_fail)
                }
            })
    }

    /**
     * 下载本地画本、书法
     */
    private fun downloadLocal(item:CloudListBean){
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        val fileTargetPath=FileAddress().getPathPainting(getType(),item.grade)
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
                            //存储画本分类
                            val date=System.currentTimeMillis()
                            val beanType =
                                PaintingTypeBean()
                            beanType.type = getType()
                            beanType.grade = item.grade
                            beanType.date = date
                            beanType.isCloud=true
                            beanType.cloudId=item.id
                            val id=PaintingTypeDaoManager.getInstance().insertOrReplaceGetId(beanType)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5,id.toInt(),1, 1, Gson().toJson(beanType))

                            //存储画本内容
                            val jsonArray=JsonParser().parse(item.contentJson).asJsonArray
                            for (json in jsonArray){
                                val drawingBean=Gson().fromJson(json,
                                    PaintingDrawingBean::class.java)
                                drawingBean.id=null
                                val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(drawingBean)
                                //创建本地画本增量更新
                                DataUpdateManager.createDataUpdate(5,id.toInt(),2,1
                                    , Gson().toJson(drawingBean),drawingBean.path)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast(getScreenPosition(),R.string.book_download_success)
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
                    showToast(getScreenPosition(), R.string.book_download_fail)
                }
            })
    }

    /**
     * 删除云数据
     */
    private fun deleteCloud(position:Int){
        val ids= mutableListOf<Int>()
        ids.add(paintings[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 根据typeId 得到画本、书法type类型
     */
    private fun getType():Int{
        return if (typeId==7) 0 else 1
    }

    /**
     * 主activity切换朝代
     */
    fun changeDynasty(dynasty: Int) {
        this.dynasty = dynasty
        fetchData()
    }

    override fun refreshData() {
        fetchData()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 5
        map["subType"] = typeId
        if (typeId!=7&&typeId!=8)
            map["dynasty"] = dynasty
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        cloudLists=cloudList.list

        when(typeId){
            7,8->{
                mLocalAdapter?.setNewData(cloudLists)
            }
            else->{
                paintings.clear()
                for (item in cloudLists){
                    if (item.listJson.isNotEmpty()){
                        val paintingBean=Gson().fromJson(item.listJson,
                            PaintingBean::class.java)
                        paintingBean.id=null //设置数据库id为null用于重新加入
                        paintingBean.cloudId=item.id
                        paintingBean.isCloud=true
                        paintings.add(paintingBean)
                    }
                }
                mAdapter?.setNewData(paintings)
            }
        }

    }

    override fun onCloudDelete() {
        when(typeId){
            7,8->{
                mLocalAdapter?.remove(position)
            }
            else->{
                mAdapter?.remove(position)
            }
        }
    }

}