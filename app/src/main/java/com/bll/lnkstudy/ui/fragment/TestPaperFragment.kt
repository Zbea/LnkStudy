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
import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.CorrectDetailsManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.homework.CorrectDetailsBean
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
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
        for (item in list) {
            if (!isSavePaperType(item)) {
                item.course = mCourse
                PaperTypeDaoManager.getInstance().insertOrReplace(item)
                //创建增量数据
                DataUpdateManager.createDataUpdate(3, item.typeId, 1, Gson().toJson(item))
            }
            else{
                val paperTypeBean= PaperTypeDaoManager.getInstance().queryById(item.typeId)
                if (paperTypeBean.name!=item.name){
                    paperTypeBean.name=item.name
                    PaperTypeDaoManager.getInstance().insertOrReplace(paperTypeBean)
                    DataUpdateManager.editDataUpdate(3,item.typeId,1,Gson().toJson(paperTypeBean))
                }
            }
        }
        fetchData()
    }

    override fun onList(paperList: PaperList?) {
        loadPapers(paperList?.list!!)
    }

    override fun onDeleteSuccess() {
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
                item.isPg=false
                notifyItemChanged(position)
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
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(3,item.typeId)
                            val path = FileAddress().getPathTestPaper(item.typeId)
                            FileUtils.deleteFile(File(path))
                            remove(position)
                        }
                    })
                true
            }
        }
    }


    /**
     * 判断 考卷分类是否已经保存本地
     */
    private fun isSavePaperType(item: PaperTypeBean): Boolean {
        return PaperTypeDaoManager.getInstance().queryById(item.typeId)!=null
    }

    /**
     * 下载收到的测试卷
     */
    private fun loadPapers(papers: MutableList<PaperList.PaperListBean>) {
        for (item in papers) {
            refreshView(item)
            val images = item.submitUrl.split(",").toMutableList()
            val pathStr = FileAddress().getPathTestPaper(item.commonTypeId, item.id)
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
                        mPresenter.downloadCompletePaper(item.id)
                        savePaperData(paths,drawPaths,item)
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
            val images=if (item.teacherUrl.isNotEmpty()){
                item.teacherUrl.split(",").toMutableList()
            }
            else{
                item.studentUrl.split(",").toMutableList()
            }
            val typeId=MethodManager.getExamTypeId(mCourse)
            item.typeId=typeId
            //刷新考试分类成绩
            refreshExamView(item)

            val pathStr = FileAddress().getPathTestPaper(item.typeId, item.id)
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
    private fun savePaperData(paths:List<String>,drawPaths:List<String>,item:PaperList.PaperListBean){
        //获取分类已保存多少次考试，用于页码排序
        val papers= PaperDaoManager.getInstance().queryAll(mCourse,item.commonTypeId)

        //保存本次考试
        val paper= PaperBean().apply {
            contentId=item.id
            course=item.subject
            typeId=item.commonTypeId
            type=item.typeName
            title=item.title
            filePath=FileAddress().getPathTestPaper(item.commonTypeId, item.id)
            this.paths=paths
            this.drawPaths=drawPaths
            page=papers.size
            score=item.score.toString()
            correctJson=item.question
            correctMode=item.questionType
            answerUrl=item.answerUrl
            scoreMode=item.questionMode
        }
        PaperDaoManager.getInstance()?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,paper.contentId,2,paper.typeId,Gson().toJson(paper),paper.filePath)
        //保存批改
        saveCorrectDetails(item.typeName,item.title,item.submitUrl,item.questionType,item.questionMode,item.score,item.question,item.answerUrl)
    }

    /**
     *  下载完成后，将考卷保存在本地试卷里面
     */
    private fun saveExamData(paths:List<String>,drawPaths:List<String>,item:ExamCorrectBean){
        //获取分类已保存多少次考试，用于页码排序
        val papers= PaperDaoManager.getInstance().queryAll(mCourse,item.typeId)
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=item.id
            course=mCourse
            this.typeId=item.typeId
            type="学校考试卷"
            title=item.examName
            filePath=FileAddress().getPathTestPaper(item.typeId, item.id)
            this.paths=paths
            this.drawPaths=drawPaths
            page=papers.size
            score=item.score.toString()
            correctJson=item.question
            correctMode=item.questionType
            scoreMode=item.questionMode
            answerUrl=item.answerUrl
        }
        PaperDaoManager.getInstance()?.insertOrReplace(paper)
        DataUpdateManager.createDataUpdate(3,paper.contentId,2,paper.typeId,Gson().toJson(paper),paper.filePath)
        //保存批改
        saveCorrectDetails("学校考试卷",item.examName,item.teacherUrl,item.questionType,item.questionMode,item.score,item.question,item.answerUrl)
    }

    /**
     * 保存批改详情
     */
    private fun saveCorrectDetails(typeStr:String,title:String,url:String,correctMode:Int,scoreMode:Int,score: Double,correctJson:String,answerUrl:String){
        CorrectDetailsManager.getInstance().insertOrReplace(CorrectDetailsBean().apply {
            type=1
            course=mCourse
            this.typeStr=typeStr
            this.title=title
            date=System.currentTimeMillis()
            this.url=url
            this.correctMode=correctMode
            this.scoreMode=scoreMode
            this.score=score
            this.correctJson=correctJson
            this.answerUrl=answerUrl
        })
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshView(item: PaperList.PaperListBean) {
        for (ite in paperTypes) {
            if (item.subject == ite.course && item.commonTypeId == ite.typeId) {
                ite.score = item.score
                ite.paperTitle=item.title
                ite.isPg = true
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshExamView(item: ExamCorrectBean) {
        for (ite in paperTypes) {
            if (item.typeId == ite.typeId) {
                ite.score = item.score
                ite.paperTitle=item.examName
                ite.isPg = true
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    private fun fetchTypes() {
        if (NetworkUtil(MyApplication.mContext).isNetworkConnected()) {
            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 1
            map["subject"] = DataBeanManager.getCourseId(mCourse)
            mPresenter.getTypeList(map)
        } else {
            fetchData()
        }
    }

    override fun fetchData() {
        val types=PaperTypeDaoManager.getInstance().queryAllByCourse(mCourse,pageIndex,pageSize)
        if (paperTypes != types) {
            val totalTypes = PaperTypeDaoManager.getInstance().queryAllByCourse(mCourse)
            paperTypes=types
            mAdapter?.setNewData(paperTypes)
            setPageNumber(totalTypes.size)
        }
        if (NetworkUtil(MyApplication.mContext).isNetworkConnected())
            fetchCorrectPaper()
    }

    fun clearData(){
        paperTypes.clear()
        mAdapter?.setNewData(paperTypes)
    }

    /**
     * 获取老师批改下发测试卷
     */
    private fun fetchCorrectPaper() {
        val map1 = HashMap<String, Any>()
        map1["subject"]=DataBeanManager.getCourseId(mCourse)
        //获取考试批改下发列表
        mPresenter.getExamList(map1)

        val map = HashMap<String, Any>()
        map["type"]=2
        map["sendStatus"] = 2
        map["subject"]=mCourse
        //获取测试卷批改下发列表
        mPresenter.getPaperCorrectList(map)
    }

    override fun onRefreshData() {
        fetchTypes()
    }

    override fun onNetworkConnectionSuccess() {
        fetchTypes()
    }
}