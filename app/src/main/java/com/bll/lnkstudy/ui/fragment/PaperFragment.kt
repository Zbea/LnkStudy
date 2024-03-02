package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.presenter.TestPaperPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_testpaper.*
import java.io.File
import java.util.concurrent.locks.ReentrantLock

/**
 * 考卷(获取考卷分类，获取老师批改下发，考卷来源首页的考试)
 */
class PaperFragment : BaseFragment(), IContractView.IPaperView {
    private val lock = ReentrantLock()
    private val mPresenter = TestPaperPresenter(this)
    private var mAdapter: PaperTypeAdapter? = null
    private var paperTypes = mutableListOf<PaperTypeBean>()
    private var onlineTypes = mutableListOf<PaperTypeBean>()
    private var course = ""//课程
    private var paperContents = mutableListOf<PaperList.PaperListBean>()//收到的考卷列表
    private var receiverPaperPos=0 //收到考卷列表位置
    private var tabId=0

    override fun onTypeList(list: MutableList<PaperTypeBean>) {
        if (onlineTypes == list) {
            fetchCorrectPaper()
        } else {
            onlineTypes = list
            for (item in list) {
                if (!isSavePaperType(item)) {
                    item.course = course
                    PaperTypeDaoManager.getInstance().insertOrReplace(item)
                    //创建增量数据
                    DataUpdateManager.createDataUpdate(3, item.typeId, 1, item.typeId, Gson().toJson(item))
                }
            }
            setData()
        }
    }

    override fun onList(paperList: PaperList?) {
        paperContents = paperList?.list as MutableList<PaperList.PaperListBean>
        loadPapers(paperContents)
        refreshView()
    }

    override fun onDeleteSuccess() {
        savePaperData(paperContents[receiverPaperPos])
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setTitle(R.string.main_testpaper_title)
        initRecyclerView()
    }

    override fun lazyLoad() {
        pageIndex=1
        onlineTypes.clear()
        initTab()
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView() {
        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type, paperTypes).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 2)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2, 80))
            setOnItemClickListener { adapter, view, position ->
                val item = paperTypes[position]
                MethodManager.gotoPaperDrawing(requireActivity(), item.course, item.typeId, Constants.DEFAULT_PAGE)
            }
            setOnItemLongClickListener { adapter, view, position ->
                CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(
                    object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }

                        override fun ok() {
                            val item = paperTypes[position]
                            //删除本地当前作业本
                            PaperTypeDaoManager.getInstance().deleteBean(item)
                            PaperDaoManager.getInstance().delete(item.course, item.typeId)
                            PaperContentDaoManager.getInstance().delete(item.course, item.typeId)
                            val path = FileAddress().getPathTestPaper(item.typeId)
                            FileUtils.deleteFile(File(path))
                            remove(position)
                        }
                    })
                true
            }
        }
    }

    //设置头部索引
    private fun initTab() {
        course = ""
        rg_group.removeAllViews()
        val courseItems = MethodManager.getCourses()
        val courses= mutableListOf<String>()
        if (courseItems.size > 0) {
            for (item in courseItems){
                val typeId=item.userId.toInt()+10000+mUser?.grade!!
                if (PaperTypeDaoManager.getInstance().queryById(typeId)==null){
                    val typeItem=PaperTypeBean()
                    typeItem.name="学校考试卷"
                    typeItem.course=item.subject
                    typeItem.date=System.currentTimeMillis()
                    typeItem.grade=mUser?.grade!!
                    typeItem.typeId=typeId
                    typeItem.userId=item.userId
                    PaperTypeDaoManager.getInstance().insertOrReplace(typeItem)
                }
            }

            for (item in courseItems){
                if (!courses.contains(item.subject)){
                    courses.add(item.subject)
                }
            }
            course = courses[0]
            for (i in courses.indices) {
                rg_group.addView(getRadioButton(i, courses[i], courses.size - 1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                tabId=id
                course = courses[id]
                fetchData()
            }
            fetchData()
        } else {
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
    private fun loadPapers(papers: MutableList<PaperList.PaperListBean>) {
        for (item in papers) {
            lock.lock()
            val images = item.submitUrl.split(",").toMutableList()
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(getPaperPaths(item)).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        receiverPaperPos=papers.indexOf(item)
                        mPresenter.deletePaper(item.id)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
            lock.unlock()
        }
    }

    /**
     * 获取考试图片下载地址
     */
    private fun getPaperPaths(item:PaperList.PaperListBean):MutableList<String>{
        val pathStr = FileAddress().getPathTestPaper(item.examId, item.id)
        val images = item.submitUrl.split(",").toMutableList()
        val savePaths = mutableListOf<String>()
        for (i in images.indices) {
            savePaths.add("$pathStr/${i + 1}.png")
        }
        return savePaths
    }

    /**
     *  下载完成后，将考卷保存在本地试卷里面
     */
    private fun savePaperData(item:PaperList.PaperListBean){
        val paths=getPaperPaths(item)
        //获取分类已保存多少次考试，用于页码排序
        val papers= PaperDaoManager.getInstance().queryAll(course,item.commonTypeId)
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=item.id
            course=item.subject
            typeId=item.commonTypeId
            type=item.examName
            title=item.title
            path=FileAddress().getPathTestPaper(item.examId, item.id)
            page=paperContents.size
            index=papers.size
        }
        PaperDaoManager.getInstance()?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,item.id,2,item.commonTypeId,Gson().toJson(paper))
        for (i in paths.indices){
            //保存本次考试的试卷内容
            val paperContent= PaperContentBean()
                .apply {
                    course=item.subject
                    typeId=item.commonTypeId
                    contentId=item.id
                    path=paths[i]
                    page=paperContents.size+i
                }
            val id=PaperContentDaoManager.getInstance().insertOrReplaceGetId(paperContent)
            DataUpdateManager.createDataUpdate(3,id.toInt(),3,item.commonTypeId,Gson().toJson(paperContent),paperContent.path)
        }
    }


    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshView() {
        for (item in paperContents) {
            for (ite in paperTypes) {
                if (item.subject == ite.course && item.examId == ite.typeId) {
                    ite.score = item.score
                    ite.paperTitle=item.title
                    ite.isPg = true
                } else {
                    ite.isPg = false
                }
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun setData() {
        if (paperTypes != PaperTypeDaoManager.getInstance().queryAllByCourse(course)) {
            paperTypes = PaperTypeDaoManager.getInstance().queryAllByCourse(course)
            mAdapter?.setNewData(paperTypes)
        }
        if (NetworkUtil(requireActivity()).isNetworkConnected())
            fetchCorrectPaper()
    }

    override fun fetchData() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {
            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 1
            map["userId"] = MethodManager.getCourses()[tabId].userId
            mPresenter.getTypeList(map)
        } else {
            setData()
        }
    }

    /**
     * 获取老师批改下发考卷
     */
    private fun fetchCorrectPaper() {
        val map = HashMap<String, Any>()
        map["sendStatus"] = 2
        mPresenter.getList(map)
    }

    /**
     * 控制上传
     */
    fun uploadPaper(token: String) {
        if (grade == 0) return
        val cloudList = mutableListOf<CloudListBean>()
        val nullItems = mutableListOf<PaperTypeBean>()
        //查找当前科目、年级的所有考卷(不包括云书库)
        val types = PaperTypeDaoManager.getInstance().queryAllByNoIsCloud()
        for (item in types) {
            val papers = PaperDaoManager.getInstance().queryAll(item.course, item.typeId)
            val paperContents = PaperContentDaoManager.getInstance().queryAll(item.course, item.typeId)
            val path = FileAddress().getPathTestPaper(item.typeId)
            if (FileUtils.isExistContent(path)) {
                FileUploadManager(token).apply {
                    startUpload(path, item.name)
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 3
                            subType = -1
                            subTypeStr = item.course
                            date = System.currentTimeMillis()
                            grade = item.grade
                            listJson = Gson().toJson(item)
                            contentJson = Gson().toJson(papers)
                            contentSubtypeJson = Gson().toJson(paperContents)
                            downloadUrl = it
                        })
                        if (cloudList.size == types.size - nullItems.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                //没有考卷内容不上传
                nullItems.add(item)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        paperTypes.clear()
        mAdapter?.notifyDataSetChanged()
        setClearExamPaper()
        setSystemControlClear()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                lazyLoad()
            }
        }
    }

    override fun onRefreshData() {
        if (rg_group?.childCount==0){
            lazyLoad()
        }
        else{
            fetchData()
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }
}