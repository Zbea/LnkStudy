package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkShareDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.ui.adapter.CloudHomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_content.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.CountDownLatch

class CloudHomeworkFragment:BaseCloudFragment(){

    private var mAdapter:CloudHomeworkAdapter?=null
    private var course=""
    private var position=0
    private val homeworkTypes= mutableListOf<HomeworkTypeBean>()
    private var countDownTasks: CountDownLatch?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=9
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()){
            mCloudPresenter.getType(2)
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

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,50f),DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity, 3)

        mAdapter = CloudHomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 60))
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudHomeworkFragment.position=position
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
            setOnItemClickListener { adapter, view, position ->
                this@CloudHomeworkFragment.position=position
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
        val homeworkTypeBean=homeworkTypes[position]
        when(homeworkTypeBean.state){
            4->{
                if (!HomeworkBookDaoManager.getInstance().isExist(homeworkTypeBean.bookId)){
                    downloadBookItem(homeworkTypeBean)
                }
                else{
                    showToast(R.string.toast_downloaded)
                }
            }
            else->{
                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(homeworkTypeBean.typeId)){
                    download(homeworkTypeBean)
                }
                else{
                    showToast(R.string.toast_downloaded)
                }
            }
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(homeworkTypes[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun downloadBookItem(homeworkTypeBean: HomeworkTypeBean){
        showLoading()
        countDownTasks= CountDownLatch(2)
        val homeworkBookBean= Gson().fromJson(homeworkTypeBean.contentJson, HomeworkBookBean::class.java)
        downloadBook(homeworkTypeBean.downloadUrl,homeworkBookBean.bookDrawPath)
        downloadBook(homeworkTypeBean.zipUrl,homeworkBookBean.bookPath)
        downloadBookSuccess(homeworkTypeBean)
    }

    /**
     * 下载完成
     */
    private fun downloadBookSuccess(homeworkTypeBean: HomeworkTypeBean){
        //等待两个请求完成后刷新列表
        Thread{
            countDownTasks?.await()
            requireActivity().runOnUiThread {
                hideLoading()
                val homeworkBookBean= Gson().fromJson(homeworkTypeBean.contentJson, HomeworkBookBean::class.java)
                if (FileUtils.isExistContent(homeworkBookBean.bookPath)){
                    homeworkTypeBean.createStatus=0
                    homeworkTypeBean.date=System.currentTimeMillis()
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)

                    homeworkBookBean.id=null
                    HomeworkBookDaoManager.getInstance().insertOrReplaceBook(homeworkBookBean)

                    val correctBeans=Gson().fromJson(homeworkTypeBean.contentSubtypeJson, object : TypeToken<List<HomeworkBookCorrectBean>>() {}.type) as MutableList<HomeworkBookCorrectBean>
                    for (item in correctBeans){
                        item.id=null
                        val id= HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(item)
                        val path=FileAddress().getPathHomeworkBookDrawPath(homeworkBookBean?.bookDrawPath!!,item.page)
                        //更新增量数据
                        DataUpdateManager.createDataUpdate(2, id.toInt(),3,homeworkBookBean.bookId ,Gson().toJson(item),path)
                    }

                    showToast(homeworkBookBean.bookName+getString(R.string.book_download_success))
                    EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
                }
                else{
                    showToast(homeworkBookBean.bookName+getString(R.string.book_download_fail))
                }
            }
            countDownTasks=null
        }.start()
    }


    /**
     * 下载普通作业本、作业卷
     */
    private fun download(item: HomeworkTypeBean){
        showLoading()
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
        val fileTargetPath=when(item.state){
            5->FileAddress().getPathScreenHomework(item.name,item.grade)
            else->FileAddress().getPathHomework(item.course,item.typeId)
        }
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
                            item.createStatus=0
                            item.autoState=0
                            item.date=System.currentTimeMillis()
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                            if (item.state!=5){
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,item.typeId,1,item.typeId,Gson().toJson(item))
                                when(item.state){
                                    1,7->{
                                        val papers=Gson().fromJson(item.contentJson, object : TypeToken<List<HomeworkPaperBean>>() {}.type) as MutableList<HomeworkPaperBean>
                                        for (paperBean in papers){
                                            paperBean.id=null//设置数据库id为null用于重新加入
                                            HomeworkPaperDaoManager.getInstance().insertOrReplace(paperBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,paperBean.contentId,2,paperBean.typeId,1,Gson().toJson(paperBean),paperBean.filePath)
                                        }
                                    }
                                    3->{
                                        val recordBeans=Gson().fromJson(item.contentJson, object : TypeToken<List<RecordBean>>() {}.type) as MutableList<RecordBean>
                                        for (recordBean in recordBeans){
                                            recordBean.id=null//设置数据库id为null用于重新加入
                                            val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,id.toInt(),2,item.typeId,item.state,Gson().toJson(recordBean),recordBean.path)
                                        }
                                    }
                                    9->{
                                        val shareBeans=Gson().fromJson(item.contentJson, object : TypeToken<List<HomeworkShareBean>>() {}.type) as MutableList<HomeworkShareBean>
                                        for (shareBean in shareBeans){
                                            val id=HomeworkShareDaoManager.getInstance().insertOrReplaceGetId(shareBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,id.toInt(),2,item.typeId,item.state,Gson().toJson(shareBean),shareBean.filePath)
                                        }
                                    }
                                    else->{
                                        val homeworks=Gson().fromJson(item.contentJson, object : TypeToken<List<HomeworkContentBean>>() {}.type) as MutableList<HomeworkContentBean>
                                        for (homeworkContentBean in homeworks){
                                            homeworkContentBean.id=null//设置数据库id为null用于重新加入
                                            val id= HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContentBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,id.toInt(),2,item.typeId,item.state,Gson().toJson(homeworkContentBean),homeworkContentBean.path)
                                        }
                                    }
                                }
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast(R.string.book_download_success)
                                EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
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

    /**
     * 下载原书
     */
    private fun downloadBook(downloadUrl:String,path:String){
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(downloadUrl))
        FileDownManager.with(activity).create(downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, path, object : IZipCallback {
                        override fun onFinish() {
                            FileUtils.deleteFile(File(zipPath))
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 2
        map["grade"] = grade
        map["subTypeStr"] = course
        mCloudPresenter.getList(map)
    }

    override fun onCloudType(types: MutableList<String>) {
        this.types=types
        if (types.size>0)
            initTab()
    }

    override fun onCloudList(cloudList: CloudList) {
        homeworkTypes.clear()
        for (cloudListBean in cloudList.list){
            if (cloudListBean.listJson.isNotEmpty()){
                val homeworkTypeBean= Gson().fromJson(cloudListBean.listJson, HomeworkTypeBean::class.java)
                homeworkTypeBean.cloudId=cloudListBean.id
                homeworkTypeBean.isCloud=true
                homeworkTypeBean.downloadUrl=cloudListBean.downloadUrl
                homeworkTypeBean.zipUrl=cloudListBean.zipUrl
                homeworkTypeBean.contentJson=cloudListBean.contentJson
                homeworkTypeBean.contentSubtypeJson=cloudListBean.contentSubtypeJson
                homeworkTypes.add(homeworkTypeBean)
            }
        }
        mAdapter?.setNewData(homeworkTypes)
        setPageNumber(cloudList.total)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(homeworkTypes)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}