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
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.RecordBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.ui.adapter.CloudHomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_homework.*
import kotlinx.android.synthetic.main.fragment_painting.*
import java.io.File

class CloudHomeworkFragment:BaseCloudFragment(){

    private var mAdapter:CloudHomeworkAdapter?=null
    private var course=""
    private var position=0
    private val types= mutableListOf<HomeworkTypeBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=9
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab(){
        val courses= DataBeanManager.courses
        if (courses.size>0){
            course=courses[0].desc
            for (i in courses.indices) {
                rg_group.addView(getRadioButton(i ,courses[i].desc,courses.size-1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                course=courses[id].desc
                pageIndex=1
                fetchData()
            }
        }
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity, 3)
        mAdapter = CloudHomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 50))
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudHomeworkFragment.position=position
                val ids= mutableListOf<Int>()
                ids.add(types[position].cloudId)
                mCloudPresenter.deleteCloud(ids)
                true
            }
            setOnItemClickListener { adapter, view, position ->
                val homeworkTypeBean=types[position]
                val item= HomeworkTypeDaoManager.getInstance().queryByTypeId(homeworkTypeBean.typeId)
                if (item==null){
                    download(homeworkTypeBean)
                }
                else{
                    showToast(screenPos,R.string.toast_downloaded)
                }
            }
        }
    }

    /**
     * 下载
     */
    private fun download(item: HomeworkTypeBean){
        item.id=null//设置数据库id为null用于重新加入
        //没有内容直接添加
        if (item.downloadUrl=="null")
        {
            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
            //创建增量数据
            DataUpdateManager.createDataUpdate(2,item.typeId,1,item.typeId,Gson().toJson(item))
            return
        }
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        val zipFile = File(zipPath)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fileTargetPath=when(item.state){
            3-> FileAddress().getPathRecord(item.course,item.typeId)
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
                    ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,item.typeId,1,item.typeId,Gson().toJson(item))
                                when(item.state){
                                    1->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val paperBean=Gson().fromJson(json, PaperBean::class.java)
                                            paperBean.id=null//设置数据库id为null用于重新加入
                                            PaperDaoManager.getInstance().insertOrReplace(paperBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdate(2,paperBean.contentId,2,paperBean.typeId,Gson().toJson(paperBean))
                                        }
                                        val jsonSubtypeArray= JsonParser().parse(item.contentSubtypeJson).asJsonArray
                                        for (json in jsonSubtypeArray){
                                            val contentBean=Gson().fromJson(json, PaperContentBean::class.java)
                                            contentBean.id=null//设置数据库id为null用于重新加入
                                            val id=PaperContentDaoManager.getInstance().insertOrReplaceGetId(contentBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdate(2,id.toInt(),3,contentBean.typeId
                                                ,Gson().toJson(contentBean),contentBean.path)
                                        }
                                    }
                                    2->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val homeworkContentBean=Gson().fromJson(json, HomeworkContentBean::class.java)
                                            homeworkContentBean.id=null//设置数据库id为null用于重新加入
                                            val id=HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContentBean)
                                            val path=if (homeworkContentBean.state==0) File(homeworkContentBean.path).parent else homeworkContentBean.path
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdate(2,id.toInt(),2
                                                ,homeworkContentBean.homeworkTypeId,Gson().toJson(homeworkContentBean),path)
                                        }
                                    }
                                    3->{
                                        val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                        for (json in jsonArray){
                                            val recordBean=Gson().fromJson(json, RecordBean::class.java)
                                            recordBean.id=null//设置数据库id为null用于重新加入
                                            val id=RecordDaoManager.getInstance().insertOrReplaceGetId(recordBean)
                                            //创建增量数据
                                            DataUpdateManager.createDataUpdate(2,id.toInt(),2,recordBean?.typeId!!
                                                ,Gson().toJson(recordBean),recordBean.path)
                                        }
                                    }
                                }
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                                Handler().postDelayed({
                                    showToast(screenPos,R.string.book_download_success)
                                    hideLoading()
                                },500)
                            }
                        }

                        override fun onProgress(percentDone: Int) {
                        }

                        override fun onError(msg: String?) {
                            showToast(screenPos,msg!!)
                            hideLoading()
                        }

                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(screenPos, R.string.book_download_fail)
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

    override fun onCloudList(item: CloudList) {
        setPageNumber(item.total)
        types.clear()
        for (type in item.list){
            if (type.listJson.isNotEmpty()){
                val homeworkTypeBean= Gson().fromJson(type.listJson, HomeworkTypeBean::class.java)
                homeworkTypeBean.cloudId=type.id
                homeworkTypeBean.isCloud=true
                homeworkTypeBean.downloadUrl=type.downloadUrl
                homeworkTypeBean.contentJson=type.contentJson
                homeworkTypeBean.contentSubtypeJson=type.contentSubtypeJson
                types.add(homeworkTypeBean)
            }
        }
        mAdapter?.setNewData(types)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

}