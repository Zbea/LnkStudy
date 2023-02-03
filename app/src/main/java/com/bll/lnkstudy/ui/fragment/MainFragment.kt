package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.COURSE_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.RECEIVE_PAPER_COMMIT_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CourseModuleDialog
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.dialog.PopWindowDateClick
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.model.DatePlan
import com.bll.lnkstudy.mvp.model.ReceivePaper
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.*
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.activity.date.DateDayListActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.MainReceivePaperDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity
import com.bll.lnkstudy.ui.adapter.*
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * 首页
 */
class MainFragment : BaseFragment(),IContractView.IClassGroupView {

    private var classGroupPresenter=ClassGroupPresenter(this)
    private var mPlanAdapter: MainDatePlanAdapter? = null

    private var popWindow: PopWindowDateClick? = null
    private var classGroupAdapter:MainClassGroupAdapter?=null
    private var groups= mutableListOf<ClassGroup>()
    private var mainNoteAdapter: MainNoteAdapter? = null
    private var receivePapers= mutableListOf<ReceivePaper>()
    private var receivePaperAdapter :MainReceivePaperAdapter?=null
    private var positionPaper=0

    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups:List<ClassGroup>) {
        groups= classGroups as MutableList<ClassGroup>
        classGroupAdapter?.setNewData(groups)
    }
    override fun onQuit() {
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("首页")

        onClickView()

        initDateView()
        initMessageView()
        initClassGroupView()
        initHomeWorkView()
        initCourse()
        initNote()

    }

    override fun lazyLoad() {
        classGroupPresenter.getClassGroupList(false)
    }

    @SuppressLint("WrongConstant")
    private fun onClickView() {
        tv_search.setOnClickListener {
//            AppUtils.clearAppData(requireContext())
            customStartActivity(Intent(activity, CampusModeActivity::class.java))
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
            (activity as MainActivity).goToNote()
        }

        ll_course.setOnClickListener {
            val courseType = SPUtil.getInt("courseType")
            customStartActivity(Intent(activity, MainCourseActivity::class.java).setFlags(1)
                .putExtra("courseType", courseType)
            )
        }

    }

    //课程表相关处理
    @SuppressLint("WrongConstant")
    private fun initCourse() {
        GlideUtils.setImageNoCacheUrl(activity,Constants.SCREEN_PATH + "/course.png",iv_course)

        iv_course_more.setOnClickListener {
            CourseModuleDialog(requireActivity(),screenPos).builder()
                ?.setOnClickListener { type ->
                    customStartActivity(Intent(activity, MainCourseActivity::class.java)
                        .setFlags(0)
                        .putExtra("courseType", type)
                )
            }
        }

    }

    //日历相关内容设置
    @SuppressLint("WrongConstant")
    private fun initDateView() {

        tv_date_today.text = SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())

        rv_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null)
        rv_plan.adapter = mPlanAdapter
        mPlanAdapter?.bindToRecyclerView(rv_plan)

        iv_date_more.setOnClickListener {
            if (popWindow == null) {
                popWindow=PopWindowDateClick(requireContext(),iv_date_more).builder()
                popWindow ?.setOnClickListener {
                    if (it==0){
                        customStartActivity(Intent(requireContext(), DatePlanListActivity::class.java))
                    }
                    if (it==1){
                        customStartActivity(Intent(requireContext(), DateDayListActivity::class.java))
                    }
                }
            } else {
                popWindow?.show()
            }

        }

        findDateList()

    }

    //消息相关处理
    private fun initMessageView() {
        val messageDatas = DataBeanManager.getIncetance().message
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        var messageAdapter = MainMessageAdapter(R.layout.item_main_message, messageDatas)
        rv_main_message.adapter = messageAdapter
        messageAdapter?.bindToRecyclerView(rv_main_message)
        rv_main_message.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        messageAdapter?.setOnItemClickListener { adapter, view, position ->
            messageDatas[position].isLook = true
            messageAdapter?.notifyDataSetChanged()
            MessageDetailsDialog(requireContext(),screenPos, messageDatas[position]).builder()
                ?.setOnDismissListener {
                    messageAdapter?.remove(position)
                }
        }
        messageAdapter?.setType(1)
    }

    //班群管理
    private fun initClassGroupView() {

        rv_main_group.layoutManager = LinearLayoutManager(context)//创建布局管理
        classGroupAdapter = MainClassGroupAdapter(R.layout.item_main_classgroup, null)
        rv_main_group.adapter = classGroupAdapter
        classGroupAdapter?.bindToRecyclerView(rv_main_group)
        classGroupAdapter?.setOnItemChildClickListener { adapter, view, position ->
        }

    }

    //作业相关
    private fun initHomeWorkView() {
        findReceivePapers()
        receivePaperAdapter = MainReceivePaperAdapter(R.layout.item_main_receivepaper, receivePapers)
        rv_main_receivePaper.layoutManager = GridLayoutManager(activity, 2)
        rv_main_receivePaper.adapter = receivePaperAdapter
        receivePaperAdapter?.bindToRecyclerView(rv_main_receivePaper)
        rv_main_receivePaper?.addItemDecoration(SpaceGridItemDeco(0, 10))
        receivePaperAdapter?.setOnItemClickListener { adapter, view, position ->
            positionPaper=position
            val paper=receivePapers[positionPaper]
            val files= FileUtils.getFiles(paper.path)
            val paths= mutableListOf<String>()
            for (file in files){
                paths.add(file.path)
            }
            if (files.size==paper.images.size) {
                var bundle=Bundle()
                bundle.putSerializable("receivePaper",paper)
                var intent= Intent(activity, MainReceivePaperDrawingActivity::class.java)
                intent.putStringArrayListExtra("imagePaths", paths as ArrayList<String>?)
                intent.putExtra("outImageStr",paper.path)
                intent.putExtra("bundle",bundle)
                intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                customStartActivity(intent)
            }
            else{
                showLoading()
                loadPapers()
            }
        }

    }

    //作业相关
    private fun initNote(){

        mainNoteAdapter = MainNoteAdapter(R.layout.item_main_note, null)
        rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_main_note.adapter = mainNoteAdapter
        mainNoteAdapter?.bindToRecyclerView(rv_main_note)
        mainNoteAdapter?.setOnItemClickListener { adapter, view, position ->
            //跳转手绘
            var intent=Intent(activity, NoteDrawingActivity::class.java)
            var bundle= Bundle()
            bundle.putSerializable("note", mainNoteAdapter?.data?.get(position))
            intent.putExtra("bundle",bundle)
            customStartActivity(intent)
        }

        findNotes()
    }


    /**
     * 通过当天时间查找本地dateEvent事件集合
     */
    private fun findDateList() {
        val planList = DateEventGreenDaoManager.getInstance().queryAllDateEvent(0)
        val plans= mutableListOf<DatePlan>()
        for (item in planList){
            plans.addAll(item.plans)
        }
        mPlanAdapter?.setNewData(plans)
    }


    private fun findNotes(){
        var notes= NotebookDaoManager.getInstance().queryAll()
        if (notes.size>6){
            notes=notes.subList(0,6)
        }
        mainNoteAdapter?.setNewData(notes)
    }

    private fun findReceivePapers(){

        var receivePaper1= ReceivePaper()
        receivePaper1.id=1
        receivePaper1.title="数学期中考试"
        receivePaper1.course="数学"
        receivePaper1.courseId=1
        receivePaper1.category="期中考试"
        receivePaper1.categoryId=1
        receivePaper1.createDate=System.currentTimeMillis()

        var receivePaper2= ReceivePaper()
        receivePaper2.id=2
        receivePaper2.title="英语期中考试"
        receivePaper2.course="英语"
        receivePaper2.courseId=1
        receivePaper2.category="期中考试"
        receivePaper2.categoryId=1
        receivePaper2.createDate=System.currentTimeMillis()
        receivePaper2.images= arrayOf(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710",
            "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-11%2F13%2Fa7590e12-844e-482c-aeb7-f06a8b248c6b%2Fa7590e12-844e-482c-aeb7-f06a8b248c6b1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659771383&t=800602d745210c44e69f6f4e274f30b5"
        )
        receivePapers.add(receivePaper1)
        receivePapers.add(receivePaper2)

        loadPapers()
    }

    //下载收到的图片
    private fun loadPapers(){
        for (item in receivePapers){
            //设置路径
            val file=File(FileAddress().getPathTestPaper(item.categoryId,item.id))
            item.path=file.path
            val files= FileUtils.getFiles(file.path)
            if (files==null||files.size!=item.images.size){
                val imageDownLoad= ImageDownLoadUtils(activity,item.images,file.path)
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

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == DATE_EVENT) {
            findDateList()
        }
        if (msgFlag == COURSE_EVENT) {
            initCourse()
        }
        if (msgFlag== NOTE_BOOK_MANAGER_EVENT){
            findNotes() //用于删除笔记本后 刷新列表
        }
        if (msgFlag==NOTE_EVENT){
            findNotes()
        }
        if (msgFlag== RECEIVE_PAPER_COMMIT_EVENT){
            receivePapers.removeAt(positionPaper)
            receivePaperAdapter?.setNewData(receivePapers)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        loadPapers()
        classGroupPresenter.getClassGroupList(false)
    }



}