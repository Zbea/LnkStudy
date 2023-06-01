package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.CLASSGROUP_EVENT
import com.bll.lnkstudy.Constants.Companion.COURSE_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.RECEIVE_PAPER_COMMIT_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CourseModuleDialog
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.date.DatePlan
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.presenter.MainPresenter
import com.bll.lnkstudy.mvp.presenter.MessagePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ICommonView
import com.bll.lnkstudy.ui.activity.*
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.activity.date.DateDayListActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.PaperExamDrawingActivity
import com.bll.lnkstudy.ui.adapter.*
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * 首页
 */
class MainFragment : BaseFragment(), IContractView.IMainView, IContractView.IMessageView,ICommonView{

    private val mMainPresenter = MainPresenter(this)
    private val mMessagePresenter=MessagePresenter(this)
    private val mCommonPresenter=CommonPresenter(this)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var classGroupAdapter: MainClassGroupAdapter? = null
    private var mainNoteAdapter: MainNoteAdapter? = null
    private var examPapers = mutableListOf<PaperList.PaperListBean>()
    private var receivePaperAdapter: MainReceivePaperAdapter? = null
    private var positionPaper = 0
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MessageAdapter?=null

    override fun onList(message: Message) {
        if (message.list.isNotEmpty()){
            messages=message.list
            mMessageAdapter?.setNewData(messages)
        }
    }
    override fun onCommitSuccess() {
    }


    override fun onList(commonData: CommonData) {
        if (commonData.grade.isNotEmpty())
            DataBeanManager.grades=commonData.grade
        if (commonData.subject.isNotEmpty())
            DataBeanManager.courses=commonData.subject
    }

    override fun onExam(exam: PaperList?) {
        examPapers= exam?.list as MutableList<PaperList.PaperListBean>
        receivePaperAdapter?.setNewData(examPapers)
        loadPapers()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        setTitle(R.string.main_home_title)

        initDateView()
        initMessageView()
        initClassGroupView()
        initHomeWorkView()
        initCourse()
        initNote()

        tv_search.setOnClickListener {
//            AppUtils.clearAppData(requireContext())
        }

        ll_date.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_group.setOnClickListener {
            customStartActivity(Intent(activity, ClassGroupActivity::class.java))
        }

        ll_note.setOnClickListener {
            (activity as HomeLeftActivity).goToNote()
        }

        ll_course.setOnClickListener {
            val courseType = SPUtil.getInt("courseType")
            customStartActivity(
                Intent(activity, MainCourseActivity::class.java).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .putExtra("courseType", courseType)
            )
        }

    }

    override fun lazyLoad() {
        mCommonPresenter.getCommon()
        findMessages()
        fetchExam()
    }

    //课程表相关处理
    @SuppressLint("WrongConstant")
    private fun initCourse() {
        GlideUtils.setImageNoCacheUrl(activity, Constants.SCREEN_PATH + "/course.png", iv_course)

        iv_course_more.setOnClickListener {
            CourseModuleDialog(requireActivity(), screenPos).builder()
                ?.setOnClickListener { type ->
                    customStartActivity(
                        Intent(activity, MainCourseActivity::class.java)
                            .setFlags(0)
                            .putExtra("courseType", type)
                    )
                }
        }

    }

    //日历相关内容设置
    @SuppressLint("WrongConstant")
    private fun initDateView() {

        val lists = mutableListOf<PopupBean>()
        lists.add(PopupBean(0, getString(R.string.date_plan)))
        lists.add(PopupBean(1, getString(R.string.date_day)))

        tv_date_today.text = SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())
        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null).apply {
            rv_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_plan.adapter = this
            bindToRecyclerView(rv_plan)
        }

        iv_date_more.setOnClickListener {
            PopupClick(requireContext(), lists, iv_date_more, 5).builder().setOnSelectListener {
                if (it.id == 0) {
                    customStartActivity(Intent(requireContext(), DatePlanListActivity::class.java))
                }
                if (it.id == 1) {
                    customStartActivity(Intent(requireContext(), DateDayListActivity::class.java))
                }
            }
        }

        findDateList()

    }

    //消息相关处理
    private fun initMessageView() {
        mMessageAdapter=MessageAdapter(0,R.layout.item_main_message, null).apply {
            rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_message.adapter = this
            bindToRecyclerView(rv_main_message)
            setOnItemClickListener { adapter, view, position ->
                MessageDetailsDialog(requireContext(), screenPos, messages[position]).builder()
            }
        }
    }

    //班群管理
    private fun initClassGroupView() {
        classGroupAdapter = MainClassGroupAdapter(R.layout.item_main_classgroup, null).apply {
            rv_main_group.layoutManager = LinearLayoutManager(context)//创建布局管理
            rv_main_group.adapter = this
            bindToRecyclerView(rv_main_group)
        }
    }

    //作业相关
    private fun initHomeWorkView() {

        receivePaperAdapter = MainReceivePaperAdapter(R.layout.item_main_receivepaper, examPapers).apply {
            rv_main_receivePaper.layoutManager = GridLayoutManager(activity, 2)
            rv_main_receivePaper.adapter = this
            bindToRecyclerView(rv_main_receivePaper)
            rv_main_receivePaper.addItemDecoration(SpaceGridItemDeco(2, 10))
            setOnItemClickListener { adapter, view, position ->
                positionPaper = position
                val paper = examPapers[positionPaper]
                val files = FileUtils.getFiles(paper.path)
                if (files==null){
                    showLoading()
                    loadPapers()
                    return@setOnItemClickListener
                }
                val paths = mutableListOf<String>()
                for (file in files) {
                    paths.add(file.path)
                }
                if (files.size == paper.imageUrl.split(",").toTypedArray().size) {
                    val bundle = Bundle()
                    bundle.putSerializable("receivePaper", paper)
                    val intent = Intent(activity, PaperExamDrawingActivity::class.java)
                    intent.putStringArrayListExtra("imagePaths", paths as ArrayList<String>?)
                    intent.putExtra("outImageStr", paper.path)
                    intent.putExtra("bundle", bundle)
                    intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                    customStartActivity(intent)
                } else {
                    showLoading()
                    loadPapers()
                }
            }
        }
    }

    //作业相关
    private fun initNote() {
        mainNoteAdapter = MainNoteAdapter(R.layout.item_main_note, null).apply {
            rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
            setOnItemClickListener { adapter, view, position ->
                //跳转手绘
                val intent = Intent(activity, NoteDrawingActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("note", mainNoteAdapter?.data?.get(position))
                intent.putExtra("bundle", bundle)
                customStartActivity(intent)
            }
        }
        findNotes()
    }

    /**
     * 通过当天时间查找本地dateEvent事件集合
     */
    private fun findDateList() {
        val planList = DateEventGreenDaoManager.getInstance().queryAllDateEvent(0)
        val plans = mutableListOf<DatePlan>()
        for (item in planList) {
            plans.addAll(item.plans)
        }
        mPlanAdapter?.setNewData(plans)
    }

    private fun findNotes() {
        var notes = NotebookDaoManager.getInstance().queryAll()
        if (notes.size > 6) {
            notes = notes.subList(0, 6)
        }
        mainNoteAdapter?.setNewData(notes)
    }

    /**
     * 获取当前考试
     */
    private fun fetchExam() {
        val map= HashMap<String, Any>()
        map["size"] = 100
        map["type"] = 2
        mMainPresenter.getExam(map)
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=4
        map["type"]=2
        mMessagePresenter.getList(map,false)
    }

    //下载收到的图片
    private fun loadPapers() {
        for (item in examPapers) {
            //设置路径
            val file = File(FileAddress().getPathTestPaper(item.examId, item.id))
            item.path = file.path
            val files = FileUtils.getFiles(file.path)
            val images=item.imageUrl.split(",").toTypedArray()
            if (files == null || files.size != images.size) {
                val imageDownLoad = ImageDownLoadUtils(activity,images, file.path)
                imageDownLoad.startDownload()
                imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                    override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                        hideLoading()
                    }
                    override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                        hideLoading()
                        imageDownLoad.reloadImage()
                    }
                })
            }
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            DATE_EVENT -> {
                findDateList()
            }
            COURSE_EVENT -> {
                initCourse()
            }
            NOTE_BOOK_MANAGER_EVENT -> {
                findNotes() //用于删除笔记本后 刷新列表
            }
            NOTE_EVENT -> {
                findNotes()
            }
            RECEIVE_PAPER_COMMIT_EVENT -> {
                receivePaperAdapter?.remove(positionPaper)
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
            CLASSGROUP_EVENT->{
                classGroupAdapter?.setNewData(DataBeanManager.classGroups)
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}