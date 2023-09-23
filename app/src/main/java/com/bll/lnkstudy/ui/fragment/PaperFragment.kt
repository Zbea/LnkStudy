package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
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
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_testpaper.*
import java.io.File

/**
 * 考卷(获取考卷分类，获取老师批改下发，考卷来源首页的考试)
 */
class PaperFragment : BaseFragment(),IContractView.IPaperView{

    private val mPresenter = TestPaperPresenter(this)
    private var mAdapter:PaperTypeAdapter?=null
    private var paperTypes= mutableListOf<PaperTypeBean>()
    private var onlineTypes= mutableListOf<PaperTypeBean>()
    private var course=""//课程
    private var paperContents= mutableListOf<PaperList.PaperListBean>()//下载收到的考卷

    override fun onTypeList(list: MutableList<PaperTypeBean>) {
        if (onlineTypes==list)
            return
        onlineTypes=list
        for (item in list){
            if (!isSavePaperType(item)){
                item.course=course
                PaperTypeDaoManager.getInstance().insertOrReplace(item)
                //创建增量数据
                DataUpdateManager.createDataUpdate(3,item.typeId,1,item.typeId,Gson().toJson(item))
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
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView(){
        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type,paperTypes).apply {
            rv_list.layoutManager = GridLayoutManager(activity,2)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
            setOnItemClickListener { adapter, view, position ->
                val item=paperTypes[position]
                MethodManager.gotoPaperDrawing(requireActivity(),item.course,item.typeId,Constants.DEFAULT_PAGE)
            }
        }
    }

    //设置头部索引
    private fun initTab(){
        course=""
        rg_group.removeAllViews()
        rg_group.setOnCheckedChangeListener(null)
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
            if (item.name == list.name && item.typeId == list.typeId) {
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
            val pathStr = FileAddress().getPathTestPaper(item.examId, item.id)
            item.path = pathStr
            val images=item.submitUrl.split(",").toMutableList()
            val savePaths= mutableListOf<String>()
            for (i in images.indices){
                savePaths.add("$pathStr/${i+1}.png")
            }
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(savePaths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        mPresenter.deletePaper(item.id)
                        val contentPapers=PaperContentDaoManager.getInstance().queryByID(item.id)
                        //更新考卷内容增量数据
                        for (contentPaper in contentPapers){
                            DataUpdateManager.editDataUpdate(3,contentPaper.id.toInt(),3,contentPaper.typeId)
                        }
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
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

    /**
     * 控制上传
     */
    fun uploadPaper(token: String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        for(classGroup in DataBeanManager.classGroups){
            val types=PaperTypeDaoManager.getInstance().queryAllByCourse(classGroup.subject)
            for (item in types){
                //云考卷不重新上传
                if (!item.isCloud){
                    val papers=PaperDaoManager.getInstance().queryAll(item.course,item.typeId)
                    val paperContents=PaperContentDaoManager.getInstance().queryAll(item.course,item.typeId)
                    val path=FileAddress().getPathTestPaper(item.typeId)
                    if (File(path).exists()){
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
                                if (cloudList.size==PaperTypeDaoManager.getInstance().queryAllExcludeCloud().size){
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
                        })
                        if (cloudList.size==PaperTypeDaoManager.getInstance().queryAllExcludeCloud().size){
                            mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
            }
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.CLASSGROUP_EVENT){
            onlineTypes.clear()
            initTab()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchData()
    }

    override fun fetchData() {
        if (!NetworkUtil.isNetworkAvailable(requireActivity())){
            return
        }
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
        paperTypes.clear()
        mAdapter?.notifyDataSetChanged()
        setClearExamPaper()
        setSystemControlClear()
    }

}