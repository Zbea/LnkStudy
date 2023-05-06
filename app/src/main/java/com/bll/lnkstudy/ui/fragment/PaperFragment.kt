package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.presenter.TestPaperPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_testpaper.*
import java.io.File

/**
 * 考卷
 */
class PaperFragment : BaseFragment(),IContractView.IPaperView{

    private val mPresenter = TestPaperPresenter(this)
    private var mAdapter:PaperTypeAdapter?=null
    private var paperTypes= mutableListOf<PaperTypeBean>()
    private var course=""//课程
    private var paperContents= mutableListOf<PaperList.PaperListBean>()//下载收到的考卷

    override fun onTypeList(list: MutableList<PaperTypeBean>) {
        for (item in list){
            if (!isSavePaperType(item)){
                item.course=course
                PaperTypeDaoManager.getInstance().insertOrReplace(item)
            }
        }
        paperTypes=PaperTypeDaoManager.getInstance().queryAllByCourse(course)
        mAdapter?.setNewData(paperTypes)
        if(paperContents.size>0){
            refreshView()
        }
    }

    override fun onList(paperList: PaperList?) {
        paperContents= paperList?.list as MutableList<PaperList.PaperListBean>
        loadPapers(paperContents)
        refreshView()
    }
    override fun onCommitSuccess() {
    }
    override fun onDeleteSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setTitle(R.string.main_testpaper_title)

        initRecyclerView()
        initTab()
    }

    override fun lazyLoad() {
        fetchData()
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView(){
        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type,paperTypes).apply {
            rv_list.layoutManager = GridLayoutManager(activity,2)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
            setOnItemClickListener { adapter, view, position ->
                gotoPaperDrawing(course,paperTypes[position].typeId)
            }
        }
    }

    //设置头部索引
    private fun initTab(){
        course=""
        rg_group.removeAllViews()
        val classGroups= DataBeanManager.classGroups
        if (classGroups.size>0){
            course=classGroups[0].subject
            for (i in classGroups.indices) {
                rg_group.addView(getRadioButton(i ,classGroups[i].subject,classGroups.size-1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                course=classGroups[id].subject
                fetchData()
            }
            fetchData()
        }
        else{
            paperTypes.clear()
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 判断 考卷分类是否已经保存本地
     */
    private fun isSavePaperType(item: PaperTypeBean): Boolean {
        var isSave = false
        for (list in PaperTypeDaoManager.getInstance().queryAllByCourse(course)) {
            if (item.name == list.name && item.typeId == list.typeId && item.course == list.course) {
                isSave = true
            }
        }
        return isSave
    }

    /**
     * 下载收到的考卷
     */
    private fun loadPapers(papers:MutableList<PaperList.PaperListBean>) {
        for (item in papers) {
            //设置路径
            val file = File(FileAddress().getPathTestPaper(item.examId, item.id))
            item.path = file.path
            val images=item.submitUrl.split(",").toTypedArray()
            val imageDownLoad = ImageDownLoadUtils(activity,images, file.path)
            imageDownLoad.startDownload()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    mPresenter.deletePaper(item.id)
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    hideLoading()
//                        imageDownLoad.reloadImage()
                }
            })
        }
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshView(){
        for (item in paperContents){
            for (ite in paperTypes){
                if (item.subject==ite.course&&item.examId==ite.typeId){
                    ite.score=item.score
                    ite.isPg=true
                }
                else{
                    ite.isPg=false
                }
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    fun uploadPaper(token:String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        for(classGroup in DataBeanManager.classGroups){
            val types=PaperTypeDaoManager.getInstance().queryAllByCourse(classGroup.subject)
            for (item in types){
                val papers=PaperDaoManager.getInstance().queryAll(1,item.course,item.typeId)
                val paperContents=PaperContentDaoManager.getInstance().queryAll(1,item.course,item.typeId)
                val path=FileAddress().getPathTestPaper(item.typeId)
                if (papers.size>0){
                    FileUploadManager(token).apply {
                        startUpload(path,item.name)
                        setCallBack{
                            cloudList.add(CloudListBean().apply {
                                type=3
                                subType=-1
                                subTypeStr=item.course
                                date=System.currentTimeMillis()
                                grade=item.grade
                                listJson=Gson().toJson(item)
                                contentJson= Gson().toJson(papers)
                                contentSubtypeJson=Gson().toJson(paperContents)
                                downloadUrl=it
                            })
                            if (cloudList.size==PaperTypeDaoManager.getInstance().queryAll().size){
                                mCloudUploadPresenter.upload(cloudList)
                            }
                        }
                    }
                }
                else{
                    cloudList.add(CloudListBean().apply {
                        type=3
                        subType=-1
                        subTypeStr=item.course
                        date=System.currentTimeMillis()
                        grade=item.grade
                        listJson= Gson().toJson(item)
                        downloadUrl="null"
                    })
                    if (cloudList.size==PaperTypeDaoManager.getInstance().queryAll().size){
                        mCloudUploadPresenter.upload(cloudList)
                    }
                }
            }
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.COURSE_EVENT){
            initTab()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchData()
    }

    override fun fetchData() {
        val classGroups = DataBeanManager.classGroups
        if (classGroups.size>0){
            var teacherId = 0
            for (classGroup in classGroups) {
                if (classGroup.subject == course) {
                    teacherId = classGroup.teacherId
                }
            }
            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 1
            map["userId"] = teacherId
            mPresenter.getTypeList(map)

            val map1= HashMap<String,Any>()
            map1["sendStatus"]=2
            mPresenter.getList(map1)
        }

    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        val ids= mutableListOf<Int>()
        //获取所有考试卷分类，查找从云书库下载的，然后删除（避免重复上传）
        val paperTypes=PaperTypeDaoManager.getInstance().queryAll()
        for (paperType in paperTypes){
            if (paperType.isCloud)
                ids.add(paperType.cloudId)
        }
        //删除云书库之前上传的
        if (ids.size>0)
            mCloudUploadPresenter.deleteCloud(ids)
        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        paperTypes.clear()
        mAdapter?.notifyDataSetChanged()
        //删除所有考卷内容
        PaperDaoManager.getInstance().deleteAllByType(1)
        PaperContentDaoManager.getInstance().deleteAllByType(1)
        FileUtils.deleteFile(File(Constants.TESTPAPER_PATH))
    }

}