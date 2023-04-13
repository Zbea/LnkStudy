package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataBeanManager.getHomeworkCoverStr
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetails
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.mvp.model.homework.HomeworkReel
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_homework.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * 作业
 */
class HomeworkFragment : BaseFragment(), IHomeworkView {

    private val mPresenter = HomeworkPresenter(this)
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var mAdapter: HomeworkAdapter? = null

    private var mCourse = ""//当前科目
    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()
    private var popupType=0

    override fun onTypeList(list: MutableList<HomeworkTypeBean>?) {
        homeworkTypes.clear()
        if (!list.isNullOrEmpty()) {
            //遍历查询作业本是否保存
            for (item in list) {
                item.contentResId = DataBeanManager.getHomeWorkContentStr(mCourse, mUser?.grade!!)
                item.course = mCourse
                item.bgResId = getHomeworkCoverStr() //当前作业本背景样式id
                item.date = System.currentTimeMillis()
                if (!isSaveHomework(item)) {
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                }
            }
        }
        getLocalHomeTypes()
        fetchMessage()
    }

    override fun onList(homeworkMessage: HomeworkMessage) {
        if (homeworkMessage.list.isNotEmpty()) {
            for (item in homeworkTypes) {
                //遍历查询所有作业本是否收到新的消息
                if (item.typeId == homeworkMessage.id) {
                    item.message = homeworkMessage
                    item.isMessage = homeworkMessage.total > item.messageTotal
                    item.messageTotal = homeworkMessage.total
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                }
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onListReel(reel: HomeworkReel) {
        val reels= reel.list
        if (reel.list.isNullOrEmpty()) return
        for (item in homeworkTypes) {
            if (item.typeId == reel.typeId) {
                //遍历查询
                for (reel in reels) {
                    if (reel.sendStatus==2){
                        item.isPg=true
                    }
                    else{
                        item.isMessage = true
                    }
                }
            }
        }
        mAdapter?.notifyDataSetChanged()

        if (reel.subType==1){
            loadReelImage(reels)
        }
        else{
            loadImage(reels)
        }

    }

    override fun onDetails(details: MutableList<HomeworkDetails.HomeworkDetailBean>?) {
        if (details != null) {
            HomeworkCommitDetailsDialog(requireActivity(),screenPos,popupType,details).builder()
        }
    }

    override fun onDownloadSuccess() {
    }

    override fun onCommitSuccess() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle(R.string.main_homework_title)
        showView(iv_manager)

        popWindowBeans.add(PopupBean(0, getString(R.string.homework_create_str), true))
        popWindowBeans.add(PopupBean(1, getString(R.string.homework_commit_details_str), false))
        popWindowBeans.add(PopupBean(2, getString(R.string.homework_correct_details_str), false))

        iv_manager.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            addCover()
                        }
                        1 -> {
                            popupType=0
                            mPresenter.getCommitDetailList()
                        }
                        else -> {
                            popupType=1
                            mPresenter.getCorrectDetailList()
                        }
                    }
                }
        }

        initRecyclerView()
        initTab()

        getLocalHomeTypes()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView() {
        mAdapter = HomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 40))
            setOnItemClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                item.isPg = false
                notifyDataSetChanged()
                when (item.state) {
                    3 -> {
                        val bundle= Bundle()
                        bundle.putSerializable("homework",item)
                        val intent=Intent(context, RecordListActivity::class.java)
                        intent.putExtra("homeworkBundle",bundle)
                        customStartActivity(intent)
                    }
                    1 -> {
                        item.isMessage = false
                        notifyDataSetChanged()
                        gotoHomeworkReelDrawing(mCourse, item.typeId)
                    }
                    else -> {
                        gotoHomeworkDrawing(item)
                    }
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (view.id == R.id.ll_message) {
                    if (item.message != null) {
                        HomeworkMessageDialog(requireActivity(), screenPos, item.name, item.message.list).builder()
                        item.isMessage = false
                        notifyDataSetChanged()
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                if (homeworkTypes[position].isCreate) {
                    showHomeworkManage(position)
                }
                true
            }
        }
    }

    //设置头部索引
    private fun initTab() {
        rg_group.removeAllViews()
        mCourse=""
        val classGroups = DataBeanManager.classGroups
        if (classGroups.size > 0) {
            mCourse = classGroups[0].subject
            for (i in classGroups.indices) {
                rg_group.addView(getRadioButton(i, classGroups[i].subject, classGroups.size - 1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                mCourse = classGroups[id].subject
                fetchData()
            }
            fetchData()
        }
        else{
            homeworkTypes.clear()
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 获取本地数据
     */
    private fun getLocalHomeTypes() {
        homeworkTypes.addAll(getLocalHomeworkTypeData(false))
        homeworkTypes.addAll(getLocalHomeworkTypeData(true))
        mAdapter?.setNewData(homeworkTypes)
    }

    /**
     * 获取本地作业本 true自己创建作业 false 老师下发作业本
     */
    private fun getLocalHomeworkTypeData(isCreate: Boolean): MutableList<HomeworkTypeBean> {
        return HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse,isCreate)
    }

    /**
     * 判断 老师下发作业本是否已经保存本地
     */
    private fun isSaveHomework(item: HomeworkTypeBean): Boolean {
        var isSave = false
        for (list in getLocalHomeworkTypeData(false)) {
            if (item.name == list.name && item.typeId == list.typeId && item.course == mCourse) {
                isSave = true
            }
        }
        return isSave
    }


    /**
     * 长按展示作业换肤、删除
     */
    private fun showHomeworkManage(pos: Int) {
        val item = homeworkTypes[pos]
        HomeworkManageDialog(requireContext(), screenPos, item.isCreate).builder()
            .setOnDialogClickListener(object :
                HomeworkManageDialog.OnDialogClickListener {
                override fun onSkin() {

                    val list = DataBeanManager.homeworkCover
                    ModuleAddDialog(requireContext(), screenPos, getString(R.string.homework_cover_module_str), list).builder()
                        ?.setOnDialogClickListener { moduleBean ->
                            item.bgResId = ToolUtils.getImageResStr(activity, moduleBean.resId)
                            mAdapter?.notifyDataSetChanged()
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                        }
                }

                override fun onDelete() {
                    //删除本地当前作业本
                    HomeworkTypeDaoManager.getInstance().deleteBean(item)
                    //删除作本内容
                    HomeworkContentDaoManager.getInstance().deleteAll(mCourse, item.typeId)
                    //删除本地文件
                    val path = FileAddress().getPathHomework(mCourse, item.typeId)
                    FileUtils.deleteFile(File(path))
                    homeworkTypes.removeAt(pos)
                    mAdapter?.notifyDataSetChanged()
                }

            })
    }


    //添加作业本
    private fun addHomeWorkType(item: HomeworkTypeBean) {
        InputContentDialog(requireContext(), screenPos, getString(R.string.homework_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                item.apply {
                    id=System.currentTimeMillis()
                    name = string
                    date = System.currentTimeMillis()
                    typeId = System.currentTimeMillis().toInt()
                    course = mCourse
                    isCreate = true
                    state = 2
                    grade=mUser?.grade!!
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                homeworkTypes.add(item)
                mAdapter?.notifyDataSetChanged()
            }
    }

    //添加封面
    private fun addCover() {
        val list = DataBeanManager.homeworkCover
        ModuleAddDialog(requireContext(), screenPos, getString(R.string.homework_cover_module_str), list).builder()
            ?.setOnDialogClickListener { moduleBean ->
                addContentModule(moduleBean.resId)
            }
    }

    //选择内容背景
    private fun addContentModule(coverResId: Int) {

        val list = when (mCourse) {
            "语文" -> {
                DataBeanManager.getYw(mUser?.grade!!)
            }
            "数学" -> {
                DataBeanManager.getSx(mUser?.grade!!)
            }
            "英语" -> {
                DataBeanManager.getYy(mUser?.grade!!)
            }
            else -> {
                DataBeanManager.other
            }
        }

        if (list.size > 1) {
            ModuleAddDialog(requireContext(), screenPos, getString(R.string.homework_module_str), list).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    val item = HomeworkTypeBean()
                    item.contentResId = ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                    item.bgResId = ToolUtils.getImageResStr(activity, coverResId)
                    addHomeWorkType(item)
                }
        } else {
            val item = HomeworkTypeBean()
            item.contentResId = ToolUtils.getImageResStr(activity, list[0].resContentId)
            item.bgResId = ToolUtils.getImageResStr(activity, coverResId)
            addHomeWorkType(item)
        }
    }

    /**
     * 作业本 下载图片
     */
    private fun loadImage(reels: MutableList<HomeworkReel.HomeworkReelBean>) {
        for (item in reels) {
            //拿到对应作业的所有本地图片地址
            val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id)
            val paths = mutableListOf<String>()
            for (homework in homeworkContents) {
                paths.add(homework.filePath)
            }
            //获得下载地址
            val images = item.submitUrl.split(",").toTypedArray()
            val imageDownLoad = ImageDownLoadUtils(activity, images, paths)
            imageDownLoad.startDownload1()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    //下载完成后 请求
                    mPresenter.commitDownload(item.id)
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    imageDownLoad.reloadImage()
                }
            })
        }
    }

    /**
     * 作业卷下载图片、将图片保存到作业
     */
    private fun loadReelImage(reels: MutableList<HomeworkReel.HomeworkReelBean>) {
        for (item in reels) {
            //设置路径 作业卷路径
            val pathStr = FileAddress().getPathHomework(mCourse, item.typeId, item.id)
            //学生未提交
            val images = if (item.sendStatus == 2) {
                item.submitUrl.split(",").toTypedArray()
            } else {
                item.imageUrl.split(",").toTypedArray()
            }
            val imageDownLoad = ImageDownLoadUtils(activity, images, pathStr)
            imageDownLoad.startDownload()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    mPresenter.commitDownload(item.id)
                    val paperDaoManager = PaperDaoManager.getInstance()
                    val paperContentDaoManager = PaperContentDaoManager.getInstance()
                    if (item.sendStatus == 2) {
                        val paper = paperDaoManager.queryByContentID(item.id)
                        if (paper != null) {
                            paper.isPg = true
                            paper.state=item.status
                            paperDaoManager.insertOrReplace(paper)
                        }
                    } else {
                        val contentBean = paperDaoManager.queryByContentID(item.id)
                        if (contentBean != null) return//避免重复下载
                        //查找到之前已经存储的数据、用于页码计算
                        val papers = paperDaoManager.queryAll(0, item.subject, item.typeId) as MutableList<PaperBean>
                        val paperContents = paperContentDaoManager.queryAll(0, item.subject, item.typeId) as MutableList<PaperContentBean>

                        val paper = PaperBean().apply {
                            contentId = item.id
                            type = 0//作业
                            course = item.subject
                            categoryId = item.typeId
                            category = item.typeName
                            title = item.title
                            path = pathStr
                            page = paperContents.size //子内容的第一个页码位置
                            index = papers.size //作业位置
                            createDate = item.date //下发时间
                            endTime = item.endTime //提交时间
                            this.images = item.imageUrl.split(",").toTypedArray().toString()
                            isPg = false
                            isCommit = item.submitStatus == 0
                            state = item.status
                        }
                        paperDaoManager.insertOrReplace(paper)
                        for (i in 0 until map?.size!!) {
                            val paperContent = PaperContentBean().apply {
                                type = 0
                                course = item.subject
                                categoryId = item.typeId
                                contentId = paper.contentId
                                path = map[i]
                                drawPath = pathStr + "/${i + 1}/draw.tch"
                                date = item.endTime
                                page = paperContents.size + i
                            }
                            paperContentDaoManager.insertOrReplace(paperContent)
                        }
                    }
                }

                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                }
            })
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == Constants.COURSE_EVENT) {
            //刷新科目
            initTab()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun refreshData() {
        fetchData()
        fetchMessage()
    }

    override fun fetchData() {
        val classGroups = DataBeanManager.classGroups
        if (classGroups.size>0){
            var teacherId = 0
            for (classGroup in classGroups) {
                if (classGroup.subject == mCourse) {
                    teacherId = classGroup.teacherId
                }
            }
            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 2
            map["userId"] = teacherId
            mPresenter.getTypeList(map)
        }
    }

    /**
     * 遍历所有作业本，获取对应作业本消息
     * 获取作业卷最新的老师下发
     */
    private fun fetchMessage() {
        for (item in homeworkTypes) {
            if (!item.isCreate&&item.state!=1) {
                val map = HashMap<String, Any>()
                map["size"] = 15
                map["grade"] = mUser?.grade!!
                map["subject"] = mCourse
                map["name"] = item.name
                map["id"] = item.id
                mPresenter.getList(map)
            }
        }

        for (item in homeworkTypes){
            val map = HashMap<String, Any>()
            map["id"] = item.id
            map["subType"] = item.state
            mPresenter.getReelList(map)
        }
    }

}