package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.presenter.TestPaperPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import java.io.File

/**
 * 考卷(获取考卷分类，获取老师批改下发，考卷来源首页的考试)
 */
class TestPaperFragment : BaseMainFragment(), IContractView.IPaperView {
    private var mCourse = ""//当前科目
    private val mPresenter = TestPaperPresenter(this)
    private var mAdapter: PaperTypeAdapter? = null
    private var paperTypes = mutableListOf<PaperTypeBean>()

    override fun onTypeList(list: MutableList<PaperTypeBean>) {
        //获取老师创建作业分类id，用来验证本地作业分类是否可删
        val createTypeIds= mutableListOf<Int>()
        for (item in list) {
            //自动生成的测验卷分类
            if (item.autoState==1){
                val typeId=MethodManager.getTestPaperAutoTypeId(item.name,mCourse)
                createTypeIds.add(typeId)
                val localItem= PaperTypeDaoManager.getInstance().queryByName(item.name,mCourse,item.grade)
                if (localItem==null){
                    item.typeId=typeId
                    insertPaperType(item)
                }
                else{
                    if (localItem.typeId!=typeId){
                        editPaperTypeId(localItem,typeId)
                    }
                }
            }
            else{
                createTypeIds.add(item.typeId)
                if (!PaperTypeDaoManager.getInstance().isExistPaperType(item.typeId)) {
                    insertPaperType(item)
                }
                else{
                    val paperTypeBean= PaperTypeDaoManager.getInstance().queryById(item.typeId)
                    if (paperTypeBean.name!=item.name){
                        editPaperTypeName(paperTypeBean,item.name)
                    }
                }
            }
        }

        val localTypes= PaperTypeDaoManager.getInstance().queryAllByCreate(mCourse,0)
        for (item in localTypes){
            if (createTypeIds.contains(item.typeId)){
                editPaperTypeCreate(item,1)
            }
        }

        val localTypes1= PaperTypeDaoManager.getInstance().queryAllByCreate(mCourse,1)
        for (item in localTypes1){
            if (!createTypeIds.contains(item.typeId)){
                editPaperTypeCreate(item,0)
            }
        }

        fetchData()
    }

    override fun onList(paperList: HomeworkPaperList) {
        loadPapers(paperList.list)
    }

    override fun onDownloadSuccess() {
    }

    override fun onExamList(list: MutableList<ExamCorrectBean>) {
        loadExams(list)
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(courseItem: String): TestPaperFragment {
        val fragment= TestPaperFragment()
        val bundle= Bundle()
        bundle.putString("courseItem",courseItem)
        fragment.arguments=bundle
        return fragment
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=6
        mCourse= arguments?.getString("courseItem") as String

        initRecyclerView()
    }

    override fun lazyLoad() {
        pageIndex=1
        fetchTypes()
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView() {

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type, paperTypes).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 2)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2, 55))
            setOnItemClickListener { adapter, view, position ->
                val item = paperTypes[position]
                item.isCorrect=false
                notifyItemChanged(position)
                MethodManager.gotoPaperDrawing(requireActivity(), item.course, item.typeId, Constants.DEFAULT_PAGE)
            }
            setOnItemLongClickListener { adapter, view, position ->
                val item = paperTypes[position]
                if (item.createStatus != 0&&!item.isCloud){
                    return@setOnItemLongClickListener true
                }
                CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(
                    object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            //删除本地当前作业本
                            PaperTypeDaoManager.getInstance().deleteBean(item)
                            PaperDaoManager.getInstance().delete(item.course, item.typeId)
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(3,item.typeId)
                            val path = FileAddress().getPathTestPaper(mCourse,item.typeId)
                            FileUtils.deleteFile(File(path))
                            remove(position)
                        }
                    })
                true
            }
        }
    }

    private fun insertPaperType(item:PaperTypeBean){
        item.createStatus=1
        item.course = mCourse
        item.date=System.currentTimeMillis()
        PaperTypeDaoManager.getInstance().insertOrReplace(item)
        //创建增量数据
        DataUpdateManager.createDataUpdate(3, item.typeId, 1, item.typeId, Gson().toJson(item))
    }

    private fun editPaperTypeName(item: PaperTypeBean,name:String){
        item.name=name
        PaperTypeDaoManager.getInstance().insertOrReplace(item)
        DataUpdateManager.editDataUpdate(3,item.typeId,1,item.typeId,Gson().toJson(item))
    }

    private fun editPaperTypeCreate(item: PaperTypeBean,createStatus:Int){
        item.createStatus=createStatus
        PaperTypeDaoManager.getInstance().insertOrReplace(item)
        DataUpdateManager.editDataUpdate(3,item.typeId,1,item.typeId,Gson().toJson(item))
    }

    private fun editPaperTypeId(item: PaperTypeBean, typeId: Int) {
        DataUpdateManager.deleteDateUpdate(3,item.typeId)
        item.typeId = typeId
        PaperTypeDaoManager.getInstance().insertOrReplace(item)
        //修改增量更新
        DataUpdateManager.createDataUpdate(3, item.typeId, 1, item.typeId, Gson().toJson(item))
    }

    /**
     * 下载收到的测试卷
     */
    private fun loadPapers(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            var paperTypeId=item.typeId
            if (item.autoState==1){
                val homeworkTypeBean= PaperTypeDaoManager.getInstance().queryByName(item.typeName,mCourse,item.grade) ?: continue
                paperTypeId=homeworkTypeBean.typeId
            }
            else{
                if (!PaperTypeDaoManager.getInstance().isExistPaperType(item.typeId)){
                    continue
                }
            }

            val images = item.submitUrl.split(",").toMutableList()
            val pathStr = FileAddress().getPathTestPaper(mCourse,paperTypeId, item.contendId)
            val paths = mutableListOf<String>()
            val drawPaths = mutableListOf<String>()
            for (i in images.indices) {
                paths.add("$pathStr/${i + 1}.png")
                drawPaths.add("$pathStr/${i + 1}draw.png")
            }
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        refreshView(paperTypeId,item)
                        mPresenter.downloadCompletePaper(item.contendId)
                        savePaperData(paperTypeId,paths,drawPaths,item)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
        }
    }

    /**
     * 下载所有考试卷
     */
    private fun loadExams(list: MutableList<ExamCorrectBean>) {
        for (item in list) {
            val images=item.teacherUrl.split(",").toMutableList()
            val typeId=MethodManager.getTestPaperAutoTypeId("学校考试卷",mCourse)
            item.typeId=typeId

            val pathStr = FileAddress().getPathTestPaper(mCourse,item.typeId, item.id)
            val paths = mutableListOf<String>()
            val drawPaths = mutableListOf<String>()
            for (i in images.indices) {
                paths.add("$pathStr/${i + 1}.png")
                drawPaths.add("$pathStr/${i + 1}draw.png")
            }
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        //刷新考试分类成绩
                        refreshExamView(item)

                        mPresenter.downloadCompleteExam(item.id)
                        saveExamData(paths,drawPaths,item)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
        }
    }

    /**
     *  下载完成后，将考卷保存在本地试卷里面
     */
    private fun savePaperData(paperTypeId: Int,paths:List<String>,drawPaths:List<String>,item:HomeworkPaperList.HomeworkPaperListBean){
        val path=FileAddress().getPathTestPaper(mCourse,paperTypeId, item.contendId)
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=item.contendId
            course=item.subject
            this.paperTypeId=paperTypeId
            this.typeId=item.typeId
            typeName=item.typeName
            grade=item.grade
            title=item.title
            date=System.currentTimeMillis()
            filePath=path
            this.paths=paths
            this.drawPaths=drawPaths
            score=item.score.toString()
            correctJson=item.question
            correctMode=item.questionType
            answerUrl=item.answerUrl
            scoreMode=item.questionMode
        }
        PaperDaoManager.getInstance()?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,paper.contentId,2,paper.paperTypeId,Gson().toJson(paper),paper.filePath)
    }

    /**
     *  下载完成后，将考卷保存在本地试卷里面
     */
    private fun saveExamData(paths:List<String>,drawPaths:List<String>,item:ExamCorrectBean){
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=item.id
            course=mCourse
            paperTypeId=item.typeId
            typeId=item.typeId
            typeName="学校考试卷"
            title=item.typeName
            date=System.currentTimeMillis()
            grade=this@TestPaperFragment.grade
            filePath=FileAddress().getPathTestPaper(mCourse,item.typeId, item.id)
            this.paths=paths
            this.drawPaths=drawPaths
            score=item.score.toString()
            correctJson=item.question
            correctMode=item.questionType
            scoreMode=item.questionMode
            answerUrl=item.answerUrl
        }
        PaperDaoManager.getInstance()?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,paper.contentId,2,paper.paperTypeId,Gson().toJson(paper),paper.filePath)
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshView(paperTypeId:Int,item: HomeworkPaperList.HomeworkPaperListBean) {
        requireActivity().runOnUiThread {
            for (ite in paperTypes) {
                if (ite.typeId == paperTypeId) {
                    ite.score = item.score
                    ite.paperTitle=item.title
                    ite.isCorrect = true
                    mAdapter?.notifyItemChanged(paperTypes.indexOf(ite))
                }
            }
        }
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshExamView(item: ExamCorrectBean) {
        requireActivity().runOnUiThread {
            for (ite in paperTypes) {
                if ( ite.typeId==item.typeId) {
                    ite.score = item.score
                    ite.paperTitle=item.typeName
                    ite.isCorrect = true
                    mAdapter?.notifyItemChanged(paperTypes.indexOf(ite))
                }
            }
        }
    }

    private fun fetchTypes() {
        if (NetworkUtil.isNetworkConnected()&&grade>0&&DataBeanManager.getCourseId(mCourse)>0) {
            val map = HashMap<String, Any>()
            map["type"] = 1
            map["subject"] = DataBeanManager.getCourseId(mCourse)
            mPresenter.getTypeList(map)
        } else {
            fetchData()
        }
    }

    override fun fetchData() {
        val totalTypes = PaperTypeDaoManager.getInstance().queryAllByCourse(mCourse)
        setPageNumber(totalTypes.size)

        paperTypes=PaperTypeDaoManager.getInstance().queryAllByCourse(mCourse,pageIndex,pageSize)
        mAdapter?.setNewData(paperTypes)

        if (NetworkUtil.isNetworkConnected())
            fetchCorrectPaper()
    }

    /**
     * 获取老师批改下发测试卷
     */
    private fun fetchCorrectPaper() {
        if (mCourse.isNotEmpty()){
            val map1 = HashMap<String, Any>()
            map1["subject"]=DataBeanManager.getCourseId(mCourse)
            //获取考试批改下发列表
            mPresenter.getExamList(map1)

            val map = HashMap<String, Any>()
            map["type"]=2
            map["sendStatus"] = 2
            map["subject"]=mCourse
            //获取测试卷批改下发列表
            mPresenter.getPaperList(map)
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchTypes()
    }

    override fun onNetworkConnectionSuccess() {
        fetchTypes()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.MQTT_TESTPAPER_NOTICE_EVENT){
            fetchTypes()
        }
    }
}