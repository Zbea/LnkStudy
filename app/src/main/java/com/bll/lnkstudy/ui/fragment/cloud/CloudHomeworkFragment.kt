package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.ui.adapter.CloudHomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_content.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

class CloudHomeworkFragment:BaseCloudFragment(){

    private var mAdapter:CloudHomeworkAdapter?=null
    private var course=""
    private var position=0
    private val homeworkTypes= mutableListOf<HomeworkTypeBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_content
    }

    override fun initView() {
        pageSize=9
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
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
        //如果是题卷本
        if (homeworkTypeBean.state==4){
            if (!HomeworkBookDaoManager.getInstance().isExist(homeworkTypeBean.bookId)){
                startDownload(homeworkTypeBean)
            }
            else{
                showToast(R.string.toast_downloaded)
            }
        }
        else{
            if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(homeworkTypeBean.typeId)){
                download(homeworkTypeBean)
            }
            else{
                showToast(R.string.toast_downloaded)
            }
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(homeworkTypes[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载普通作业本、作业卷
     */
    private fun download(item: HomeworkTypeBean){
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
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
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                            if (item.state!=5){
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,item.typeId,1,Gson().toJson(item))
                                when(item.state){
                                    1->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val paperBean=Gson().fromJson(json, PaperBean::class.java)
                                            paperBean.id=null//设置数据库id为null用于重新加入
                                            PaperDaoManager.getInstance().insertOrReplace(paperBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,paperBean.contentId,2,paperBean.typeId,1,Gson().toJson(paperBean),paperBean.filePath)
                                        }
                                    }
                                    2->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val homeworkContentBean=Gson().fromJson(json, HomeworkContentBean::class.java)
                                            homeworkContentBean.id=null//设置数据库id为null用于重新加入
                                            val id=HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContentBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,id.toInt(),2,item.typeId,item.state,Gson().toJson(homeworkContentBean),homeworkContentBean.path)
                                        }
                                    }
                                    3->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val recordBean=Gson().fromJson(json, RecordBean::class.java)
                                            recordBean.id=null//设置数据库id为null用于重新加入
                                            val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdateState(2,id.toInt(),2,item.typeId,item.state,Gson().toJson(recordBean),recordBean.path)
                                        }
                                    }
                                }
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast(R.string.book_download_success)
                                deleteItem()
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
     * 下载
     */
    private fun startDownload(homeworkTypeBean: HomeworkTypeBean){
        val bookBean= Gson().fromJson(homeworkTypeBean.contentJson, HomeworkBookBean::class.java)
        bookBean.drawUrl=homeworkTypeBean.downloadUrl
        downloadBook(bookBean,!homeworkTypeBean.downloadUrl.isNullOrEmpty())
    }

    /**
     * 下载题卷本
     */
    private fun downloadBook(book: HomeworkBookBean,isDraw:Boolean) {
        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val url=if (isDraw) book.drawUrl else book.downloadUrl
        FileDownManager.with(activity).create(url).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, book.bookPath, object : IZipCallback {
                        override fun onFinish() {
                            val homeworkTypeBean=HomeworkTypeBean().apply {
                                name=book.bookName
                                grade=book.grade
                                typeId=ToolUtils.getDateId()
                                state=4
                                date=System.currentTimeMillis()
                                course=DataBeanManager.getCourseStr(book.subject)
                                bookId=book.bookId
                                bgResId=book.imageUrl
                                createStatus=0
                            }
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                            book.id=null
                            HomeworkBookDaoManager.getInstance().insertOrReplaceBook(book)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2, homeworkTypeBean.typeId, 1, Gson().toJson(homeworkTypeBean))
                            if (isDraw){
                                DataUpdateManager.createDataUpdateDrawing(7,book.bookId,1,book.bookDrawPath)
                            }
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                hideLoading()
                                deleteItem()
                                EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
                                showToast(book.bookName+getString(R.string.book_download_success))
                            },500)
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                            hideLoading()
                            //下载失败删掉已下载手写内容
                            FileUtils.deleteFile(File(book.bookDrawPath))
                            showToast(msg!!)
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    //下载失败删掉已下载手写内容
                    FileUtils.deleteFile(File(book.bookDrawPath))
                    showToast(book.bookName+getString(R.string.book_download_fail))
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
        setPageNumber(cloudList.total)
        homeworkTypes.clear()
        for (type in cloudList.list){
            if (type.listJson.isNotEmpty()){
                val homeworkTypeBean= Gson().fromJson(type.listJson, HomeworkTypeBean::class.java)
                homeworkTypeBean.cloudId=type.id
                homeworkTypeBean.isCloud=true
                homeworkTypeBean.downloadUrl=type.downloadUrl
                homeworkTypeBean.contentJson=type.contentJson
                homeworkTypeBean.contentSubtypeJson=type.contentSubtypeJson
                homeworkTypes.add(homeworkTypeBean)
            }
        }
        mAdapter?.setNewData(homeworkTypes)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}