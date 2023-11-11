package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.PastHomeworkActivity
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_homework.*
import java.io.File
import java.util.concurrent.CountDownLatch


/**
 * 作业分为老师作业本、家长作业本（不同的接口处理）
 * （作业分类、作业内容、作业卷目录在创建以及数据更新时候都需要创建、修改增量更新）
 */
class HomeworkFragment : BaseFragment(), IHomeworkView {

    private var countDownTasks:CountDownLatch?=null //异步完成后操作
    private val mPresenter = HomeworkPresenter(this)
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var mAdapter: HomeworkAdapter? = null
    private var mCourse = ""//当前科目
    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()//当前页分类
    private var popupType = 0
    private var position = 0
    private val longItems = mutableListOf<ItemList>()

    override fun onTypeList(list: MutableList<HomeworkTypeBean>) {
        //遍历查询作业本是否保存
        for (item in list) {
            if (item.state == 4) {//如果是题卷本，遍历查询本地是否已经下载，关联
                val homeworkTypeBean = getLocalHomeworkBookType(item)
                if (homeworkTypeBean != null) {
                    if (homeworkTypeBean.createStatus == 0) {
                        homeworkTypeBean.typeId = item.typeId
                        homeworkTypeBean.createStatus = 1
                        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    }
                } else {
                    item.id = null
                    item.course = mCourse
                    item.createStatus = 1
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    //创建增量数据
                    DataUpdateManager.createDataUpdate(2, item.typeId, 1, item.typeId, item.state, Gson().toJson(item))
                }
            } else {
                item.contentResId = DataBeanManager.getHomeWorkContentStr(mCourse, mUser?.grade!!)
                item.course = mCourse
                item.createStatus = 1
                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(item.typeId)) {
                    item.id = null
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    //创建增量数据
                    DataUpdateManager.createDataUpdate(2, item.typeId, 1, item.typeId, item.state, Gson().toJson(item))
                }
            }
        }
        countDownTasks?.countDown()
    }

    override fun onTypeParentList(list: MutableList<ParentTypeBean>) {
        for (item in list) {
            if (!HomeworkTypeDaoManager.getInstance().isExistParentType(item.id)) {
                val homeworkTypeBean = HomeworkTypeBean().apply {
                    userId = item.parentId
                    name = item.name
                    grade = mUser?.grade!!
                    typeId = ToolUtils.getDateId() + item.id
                    parentTypeId = item.id
                    state = if (item.type == 1) 2 else 4
                    date = item.time
                    contentResId = if (item.type == 1) DataBeanManager.getHomeWorkContentStr(mCourse, mUser?.grade!!) else ""
                    course = mCourse
                    bgResId = item.imageUrl
                    bookId = item.bookId
                    createStatus = 2
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                //创建增量数据
                DataUpdateManager.createDataUpdate(2, homeworkTypeBean.typeId, 1, homeworkTypeBean.typeId, homeworkTypeBean.state, Gson().toJson(homeworkTypeBean))
            }
        }
        countDownTasks?.countDown()
    }

    override fun onMessageList(map: Map<String, HomeworkMessage>) {
        for (item in homeworkTypes) {
            val bean = map[item.typeId.toString()]
            if (bean != null) {
                item.messages = bean.list
                item.isMessage = bean.total > item.messageTotal
                item.messageTotal = bean.total
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                if (item.isMessage)
                    mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
            }
        }
    }

    override fun onParentMessageList(map: MutableMap<String, ParentHomeworkMessage>) {
        for (item in homeworkTypes) {
            val bean = map[item.parentTypeId.toString()]
            if (bean != null) {
                item.parents = bean.list
                item.isMessage = bean.total > item.messageTotal
                item.messageTotal = bean.total
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                if (item.isMessage)
                    mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
            }
        }
    }

    //下载老师发送作业卷或者老师批改下发作业
    override fun onListReel(map: Map<String, HomeworkPaperList>) {
        for (item in homeworkTypes) {
            val bean = map[item.typeId.toString()]
            if (bean != null) {
                val reels = bean.list!!
                //遍历查询
                for (reel in reels) {
                    if (reel.sendStatus == 2) {
                        item.isPg = true
                    } else {
                        item.isMessage = true
                    }
                }
                mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
                //下载老师传过来的作业
                when (item.state) {
                    1 -> {
                        loadHomeworkPaperImage(reels)
                    }
                    2 -> {
                        loadHomeworkImage(reels)
                    }
                    4 -> {
                        loadHomeworkBook(reels)
                    }
                }
            }
        }
    }

    //下载家长批改下发
    override fun onParentReel(map: MutableMap<String, MutableList<ParentHomeworkBean>>) {
        for (item in homeworkTypes) {
            val reels = map[item.parentTypeId.toString()]
            if (!reels.isNullOrEmpty()) {
                item.isPg = true
                mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
                //下载家长传过来的作业
                when (item.state) {
                    2 -> {
                        loadParentHomeworkImage(reels)
                    }
                    4 -> {
                        loadParentHomeworkBook(reels)
                    }
                }
            }
        }
    }

    override fun onDetails(details: MutableList<HomeworkDetails.HomeworkDetailBean>) {
        HomeworkCommitDetailsDialog(requireActivity(), screenPos, popupType, details).builder()
    }

    override fun onDownloadSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        pageSize = 9
        setTitle(R.string.main_homework_title)
        showView(iv_manager)

        longItems.add(ItemList().apply {
            name = "删除"
            resId = R.mipmap.icon_setting_delete
        })
        longItems.add(ItemList().apply {
            name = "换肤"
            resId = R.mipmap.icon_setting_skin
        })

        popWindowBeans.add(PopupBean(0, getString(R.string.homework_commit_details_str), true))
        popWindowBeans.add(PopupBean(1, getString(R.string.homework_correct_details_str), false))
        popWindowBeans.add(PopupBean(2, getString(R.string.homework_create_str), false))
        popWindowBeans.add(PopupBean(3, getString(R.string.homework_book_str), false))
        popWindowBeans.add(PopupBean(4, getString(R.string.homework_old_homework_str), false))

        iv_manager.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            popupType = 0
                            mPresenter.getCommitDetailList()
                        }
                        1 -> {
                            popupType = 1
                            mPresenter.getCorrectDetailList()
                        }
                        2 -> {
                            if (DataBeanManager.classGroups().size > 0) {
                                addCover()
                            }
                        }
                        3 -> {
                            if (DataBeanManager.classGroups().size > 0) {
                                customStartActivity(Intent(requireActivity(), HomeworkBookStoreActivity::class.java))
                            }
                        }
                        4 -> {
                            customStartActivity(Intent(requireActivity(), PastHomeworkActivity::class.java))
                        }
                    }
                }
        }

        initRecyclerView()
        initTab()

    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView() {
        mAdapter = HomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 30))
            setOnItemClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (item.isPg){
                    item.isPg = false
                    notifyItemChanged(position)
                }
                when (item.state) {
                    1 -> {
                        MethodManager.gotoHomeworkReelDrawing(requireActivity(), item, Constants.DEFAULT_PAGE)
                    }
                    2 -> {
                        MethodManager.gotoHomeworkDrawing(requireActivity(), item, Constants.DEFAULT_PAGE)
                    }
                    3 -> {
                        MethodManager.gotoHomeworkRecord(requireActivity(), item)
                    }
                    4 -> {
                        if (HomeworkBookDaoManager.getInstance().isExist(item.bookId)) {
                            MethodManager.gotoHomeworkBookDetails(requireActivity(), item)
                        } else {
                            showToast(screenPos, R.string.toast_homework_unDownload)
                        }
                    }
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (item.isMessage){
                    item.isMessage = false
                    notifyItemChanged(position)
                }
                if (view.id == R.id.ll_message) {
                    if (item.createStatus == 1) {
                        if (!item.messages.isNullOrEmpty()) {
                            HomeworkMessageDialog(requireActivity(), screenPos, item.name, item.createStatus, item.messages).builder()
                        }
                    } else {
                        if (!item.parents.isNullOrEmpty()) {
                            HomeworkMessageDialog(requireActivity(), screenPos, item.name, item.createStatus, item.parents).builder()
                        }
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@HomeworkFragment.position = position
                showHomeworkManage()
                true
            }
        }
    }

    //设置头部索引
    private fun initTab() {
        mCourse = ""
        rg_group.removeAllViews()
        rg_group.setOnCheckedChangeListener(null)
        val classGroups = DataBeanManager.classGroups()
        if (classGroups.size > 0) {
            mCourse = classGroups[0].subject
            for (i in classGroups.indices) {
                rg_group.addView(getRadioButton(i, classGroups[i].subject, classGroups.size - 1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                mCourse = classGroups[id].subject
                fetchHomeworkType()
            }
            fetchHomeworkType()
        } else {
            homeworkTypes.clear()
            mAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * 查询本地是否存在题卷本
     */
    private fun getLocalHomeworkBookType(item: HomeworkTypeBean): HomeworkTypeBean? {
        val types = HomeworkTypeDaoManager.getInstance().queryAllByState(mCourse, grade,4)
        var homeworkTypeBean: HomeworkTypeBean? = null
        for (homeTypeBean in types) {
            if (homeTypeBean.name == item.name && homeTypeBean.bookId == item.bookId) {
                homeworkTypeBean = homeTypeBean
            }
        }
        return homeworkTypeBean
    }

    /**
     * 长按展示作业换肤、删除
     */
    private fun showHomeworkManage() {
        val item = homeworkTypes[position]

        LongClickManageDialog(requireActivity(), 2, item.name, longItems).builder()
            .setOnDialogClickListener {
                if (it == 0) {
                    //删除本地当前作业本
                    HomeworkTypeDaoManager.getInstance().deleteBean(item)
                    when (item.state) {
                        1 -> {
                            val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(mCourse, item.typeId)
                            val path = FileAddress().getPathHomework(mCourse, item.typeId)
                            FileUtils.deleteFile(File(path))

                            for (paper in homePapers) {
                                val contentPapers = HomeworkPaperContentDaoManager.getInstance().queryByID(paper.contentId)
                                DataUpdateManager.deleteDateUpdate(2, paper.contentId, 2, item.typeId)
                                for (content in contentPapers) {
                                    //创建增量数据
                                    DataUpdateManager.deleteDateUpdate(2, content.id.toInt(), 3, item.typeId)
                                }
                            }
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(2, item.typeId, 1, item.typeId)
                        }
                        2 -> {
                            //删除作本内容
                            val items = HomeworkContentDaoManager.getInstance().queryAllByType(mCourse, item.typeId)
                            HomeworkContentDaoManager.getInstance().deleteBeans(items)
                            //删除本地文件
                            val path = FileAddress().getPathHomework(mCourse, item.typeId)
                            FileUtils.deleteFile(File(path))

                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(2, item.typeId, 1, item.typeId)
                            //删除增量内容（普通作业本）
                            for (homeContent in items) {
                                //删除增量更新
                                DataUpdateManager.deleteDateUpdate(2, homeContent.id.toInt(), 2, item.typeId)
                            }
                        }
                        3 -> {
                            val recordBeans = RecordDaoManager.getInstance().queryAllByCourse(mCourse, item.typeId)
                            val path = FileAddress().getPathRecord(mCourse, item.typeId)
                            FileUtils.deleteFile(File(path))
                            for (bean in recordBeans) {
                                RecordDaoManager.getInstance().deleteBean(bean)
                                //修改本地增量更新
                                DataUpdateManager.deleteDateUpdate(2, bean.id.toInt(), 2, bean.typeId)
                            }
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(2, item.typeId, 1, item.typeId)
                        }
                        4 -> {
                            val homeworkBook = HomeworkBookDaoManager.getInstance().queryBookByID(item.bookId)
                            if (homeworkBook != null) {
                                //删除文件
                                FileUtils.deleteFile(File(homeworkBook.bookPath))
                                HomeworkBookDaoManager.getInstance().delete(homeworkBook)
                                //删除增量更新
                                DataUpdateManager.deleteDateUpdate(8, item.bookId, 1, item.bookId)
                                DataUpdateManager.deleteDateUpdate(8, item.bookId, 2, item.bookId)
                            }
                        }
                    }
                    mAdapter?.remove(position)
                } else {
                    if (item.state != 4) {
                        val list = DataBeanManager.homeworkCover
                        ModuleAddDialog(requireContext(), screenPos, getString(R.string.homework_cover_module_str), list).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                item.bgResId = ToolUtils.getImageResStr(activity, moduleBean.resId)
                                mAdapter?.notifyItemChanged(position)
                                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                            }
                    }
                }
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

    //添加作业本
    private fun addHomeWorkType(item: HomeworkTypeBean) {
        InputContentDialog(requireContext(), screenPos, getString(R.string.homework_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                item.apply {
                    name = string
                    date = System.currentTimeMillis()
                    typeId = ToolUtils.getDateId()
                    course = mCourse
                    createStatus = 0
                    state = 2
                    grade = mUser?.grade!!
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                //创建增量数据
                DataUpdateManager.createDataUpdate(2, item.typeId, 1, item.typeId, item.state, Gson().toJson(item))
                fetchData()
            }
    }


    /**
     * 下载家长作业本图片
     */
    private fun loadParentHomeworkImage(beans: MutableList<ParentHomeworkBean>) {
        for (item in beans) {
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id)
            if (homeworkContents.isNullOrEmpty()) {
                continue
            }
            for (homework in homeworkContents) {
                paths.add(homework.path)
            }
            //获得下载地址
            val images = item.changeUrl.split(",").toMutableList()
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
                .startMultiTaskDownLoad(
                    object : FileMultitaskDownManager.MultiTaskCallBack {
                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun completed(task: BaseDownloadTask?) {
                            //下载完成后 请求
                            mPresenter.downloadParent(item.id)
                            //更新增量数据
                            for (homework in homeworkContents) {
                                DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2, homework.homeworkTypeId)
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
     * 题卷本 下载图片
     */
    private fun loadParentHomeworkBook(beans: MutableList<ParentHomeworkBean>) {
        for (item in beans) {
            val typeBean = HomeworkTypeDaoManager.getInstance().queryByParentTypeId(item.parentHomeworkId)
            val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId) ?: continue
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            for (i in item.pageStr.split(",")) {
                val path = getIndexFile(homeworkBookBean, i.toInt() - 1)?.path!!
                paths.add(path)
            }
            //获得下载地址
            val images = item.changeUrl.split(",").toMutableList()
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
                .startMultiTaskDownLoad(
                    object : FileMultitaskDownManager.MultiTaskCallBack {
                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun completed(task: BaseDownloadTask?) {
                            //下载完成后 请求
                            mPresenter.downloadParent(item.id)
                            //更新增量数据
                            DataUpdateManager.editDataUpdate(8, homeworkBookBean.bookId, 2, homeworkBookBean.bookId)
                        }
                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        }
                    })
        }
    }


    /**
     * 题卷本 下载图片
     */
    private fun loadHomeworkBook(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            val typeBean = HomeworkTypeDaoManager.getInstance().queryByTypeId(item.typeId)
            val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId) ?: continue
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            for (i in item.page.split(",")) {
                val path = getIndexFile(homeworkBookBean, i.toInt() - 1)?.path!!
                paths.add(path)
            }
            //获得下载地址
            val images = item.submitUrl.split(",").toMutableList()
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
                .startMultiTaskDownLoad(
                    object : FileMultitaskDownManager.MultiTaskCallBack {
                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun completed(task: BaseDownloadTask?) {
                            //下载完成后 请求
                            mPresenter.commitDownload(item.id)
                            //更新增量数据
                            DataUpdateManager.editDataUpdate(8, homeworkBookBean.bookId, 2, homeworkBookBean.bookId)
                        }
                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        }
                    })
        }
    }

    /**
     * 获得题卷本图片地址
     */
    private fun getIndexFile(bookBean: HomeworkBookBean, index: Int): File? {
        val path = FileAddress().getPathTextbookPicture(bookBean.bookPath)
        val listFiles = FileUtils.getFiles(path)
        return if (listFiles != null) listFiles[index] else null
    }

    /**
     * 作业本 下载图片
     */
    private fun loadHomeworkImage(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id)
            if (homeworkContents.isNullOrEmpty()) {
                continue
            }
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            for (homework in homeworkContents) {
                paths.add(homework.path)
            }
            //获得下载地址
            val images = item.submitUrl.split(",").toMutableList()
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
                .startMultiTaskDownLoad(
                    object : FileMultitaskDownManager.MultiTaskCallBack {
                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }
                        override fun completed(task: BaseDownloadTask?) {
                            //下载完成后 请求
                            mPresenter.commitDownload(item.id)
                            //更新增量数据
                            for (homework in homeworkContents) {
                                DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2, homework.homeworkTypeId)
                            }
                        }
                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int ) {
                        }
                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        }
                    })
        }
    }

    /**
     * 作业卷下载图片、将图片保存到作业
     */
    private fun loadHomeworkPaperImage(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            //设置路径 作业卷路径
            val pathStr = FileAddress().getPathHomework(mCourse, item.typeId, item.id)
            //学生未提交
            val images = if (item.sendStatus == 2) {
                item.submitUrl.split(",").toMutableList()
            } else {
                item.imageUrl.split(",").toMutableList()
            }
            val paths = mutableListOf<String>()
            for (i in images.indices) {
                val path = "$pathStr/${i + 1}.png"
                paths.add(path)
            }
            FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
                .startMultiTaskDownLoad(
                    object : FileMultitaskDownManager.MultiTaskCallBack {
                        override fun progress(
                            task: BaseDownloadTask?,
                            soFarBytes: Int,
                            totalBytes: Int
                        ) {
                        }
                        override fun completed(task: BaseDownloadTask?) {
                            mPresenter.commitDownload(item.id)
                            val paperDaoManager = HomeworkPaperDaoManager.getInstance()
                            val paperContentDaoManager =
                                HomeworkPaperContentDaoManager.getInstance()
                            if (item.sendStatus == 2) {
                                val paper = paperDaoManager.queryByContentID(item.id)
                                if (paper != null) {
                                    paper.isPg = true
                                    paper.state = item.status
                                    paperDaoManager.insertOrReplace(paper)
                                    //获取本次作业的所有作业卷内容
                                    val contentPapers = paperContentDaoManager.queryByID(item.id)
                                    //更新目录增量数据
                                    DataUpdateManager.editDataUpdate(2, item.id, 2, item.typeId, Gson().toJson(paper))
                                    //更新作业卷内容增量数据
                                    for (contentPaper in contentPapers) {
                                        DataUpdateManager.editDataUpdate(2, contentPaper.id.toInt(), 3, contentPaper.typeId)
                                    }
                                }
                            } else {
                                val contentBean = paperDaoManager.queryByContentID(item.id)
                                if (contentBean != null) return//避免重复下载
                                //查找到之前已经存储的数据、用于页码计算
                                val papers = paperDaoManager.queryAll(item.subject, item.typeId) as MutableList<*>
                                val paperContents = paperContentDaoManager.queryAll(item.subject, item.typeId) as MutableList<*>
                                //创建作业卷目录
                                val paper = HomeworkPaperBean().apply {
                                    contentId = item.id
                                    course = item.subject
                                    typeId = item.typeId
                                    type = item.typeName
                                    title = item.title
                                    path = pathStr
                                    page = paperContents.size //子内容的第一个页码位置
                                    index = papers.size //作业位置
                                    endTime = item.endTime //提交时间
                                    isPg = false
                                    isCommit = item.submitStatus == 0
                                    state = item.status
                                }
                                paperDaoManager.insertOrReplace(paper)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2, item.id, 2, item.typeId, 1, Gson().toJson(item))

                                for (i in paths.indices) {
                                    //创建作业卷内容
                                    val paperContent = HomeworkPaperContentBean().apply {
                                        course = item.subject
                                        typeId = item.typeId
                                        contentId = item.id
                                        path = paths[i]
                                        drawPath = "$pathStr/${i + 1}/draw.tch"
                                        page = paperContents.size + i
                                    }
                                    val id = paperContentDaoManager.insertOrReplaceGetId(paperContent)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(2, id.toInt(), 3, item.typeId, 1, Gson().toJson(paperContent), pathStr)
                                }
                            }
                        }
                        override fun paused(
                            task: BaseDownloadTask?,
                            soFarBytes: Int,
                            totalBytes: Int
                        ) {
                        }
                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        }
                    })
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.CLASSGROUP_EVENT -> {
                initTab()
            }
            Constants.HOMEWORK_BOOK_EVENT -> {
                fetchData()
            }
            Constants.APP_REFRESH_EVENT ->{
                fetchHomeworkType()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchHomeworkType()
    }

    /**
     * 请求作业分类
     */
    private fun fetchHomeworkType() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {
            countDownTasks= CountDownLatch(2)

            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 2
            map["userId"] = DataBeanManager.getClassGroupTeacherId(mCourse)
            mPresenter.getTypeList(map)

            val parentMap = HashMap<String, Any>()
            parentMap["subject"] = DataBeanManager.getCourseId(mCourse)
            parentMap["childId"] = mUser?.accountId!!
            mPresenter.getTypeParentList(parentMap)

            //等待两个请求完成后刷新列表
            Thread{
                countDownTasks?.await()
                requireActivity().runOnUiThread {
                    fetchData()
                }
            }.start()

        } else {
            fetchData()
        }
    }

    override fun fetchData() {
        val totalTypes = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse, grade)
        homeworkTypes = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse, grade, pageIndex, pageSize)
        setPageNumber(totalTypes.size)
        mAdapter?.setNewData(homeworkTypes)
        if (NetworkUtil(requireActivity()).isNetworkConnected())
            fetchMessage()
    }

    /**
     * 遍历所有作业本，获取对应作业本消息
     * 获取作业卷最新的老师下发
     */
    private fun fetchMessage() {
        val list = arrayListOf<HomeworkRequestArguments>()
        for (item in homeworkTypes) {
            if (item.createStatus == 1 && item.state != 1) {
                list.add(HomeworkRequestArguments().apply {
                    size = 10
                    grade = this@HomeworkFragment.grade
                    commonTypeId = item.typeId
                    id = item.typeId
                })
            }
        }
        val map = HashMap<String, Any>()
        map["studentDto"] = list
        mPresenter.getList(map)

        list.clear()
        for (item in homeworkTypes) {
            if (item.createStatus == 1) {
                list.add(HomeworkRequestArguments().apply {
                    id = item.typeId
                    subType = item.state
                })
            }
        }
        val mapPaper = HashMap<String, Any>()
        mapPaper["commonDto"] = list
        mPresenter.getReelList(mapPaper)

        val arrayIds = arrayListOf<Int>()
        for (item in homeworkTypes) {
            if (item.createStatus == 2) {
                arrayIds.add(item.parentTypeId)
            }
        }
        val mapParentMessage = HashMap<String, Any>()
        mapParentMessage["ids"] = arrayIds
        mapParentMessage["size"] = 10
        mPresenter.getParentMessage(mapParentMessage)
        mapParentMessage.remove("size")
        mPresenter.getParentReel(mapParentMessage)
    }

    /**
     * 上传
     */
    fun upload(token: String) {
        if (grade == 0) return
        val cloudList = mutableListOf<CloudListBean>()
        //空内容不上传
        val nullItems = mutableListOf<HomeworkTypeBean>()
        for (classGroup in DataBeanManager.classGroups()) {
            //查找当前科目的数据（不包括云存储）
            val types = HomeworkTypeDaoManager.getInstance().queryAllByCourse(classGroup.subject)
            for (typeBean in types) {
                when (typeBean.state) {
                    1 -> {
                        val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                        val homeworkContents = HomeworkPaperContentDaoManager.getInstance().queryAll(typeBean.course, typeBean.typeId)
                        val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                        if (FileUtils.isExistContent(path)) {
                            FileUploadManager(token).apply {
                                startUpload(path, typeBean.name)
                                setCallBack {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subType = 1
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homePapers)
                                        contentSubtypeJson = Gson().toJson(homeworkContents)
                                        downloadUrl = it
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                            }
                        } else {
                            nullItems.add(typeBean)
                        }
                    }
                    2 -> {
                        val homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(typeBean.course, typeBean.typeId)
                        val path = FileAddress().getPathHomework(typeBean.course, typeBean.typeId)
                        if (FileUtils.isExistContent(path)) {
                            FileUploadManager(token).apply {
                                startUpload(path, typeBean.name)
                                setCallBack {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subType = 2
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(homeworks)
                                        downloadUrl = it
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                            }

                        } else {
                            nullItems.add(typeBean)
                        }
                    }
                    3 -> {
                        val records = RecordDaoManager.getInstance().queryAllByCourse(typeBean.course, typeBean.typeId)
                        val path = FileAddress().getPathRecord(typeBean.course, typeBean.typeId)
                        if (FileUtils.isExistContent(path)) {
                            FileUploadManager(token).apply {
                                startUpload(path, typeBean.name)
                                setCallBack {
                                    cloudList.add(CloudListBean().apply {
                                        this.type = 2
                                        subType = 3
                                        subTypeStr = typeBean.course
                                        date = typeBean.date
                                        grade = typeBean.grade
                                        listJson = Gson().toJson(typeBean)
                                        contentJson = Gson().toJson(records)
                                        downloadUrl = it
                                    })
                                    startUpload(cloudList, nullItems)
                                }
                            }
                        } else {
                            nullItems.add(typeBean)
                        }
                    }
                    4 -> {
                        val homeworkBook = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
                        //判断题卷本是否已下载题卷书籍
                        if (homeworkBook == null) {
                            nullItems.add(typeBean)
                        } else {
                            //判读是否存在手写内容
                            if (File(homeworkBook.bookDrawPath).exists()) {
                                FileUploadManager(token).apply {
                                    startUpload(homeworkBook.bookPath, File(homeworkBook.bookPath).name)
                                    setCallBack {
                                        cloudList.add(CloudListBean().apply {
                                            this.type = 2
                                            subType = 4
                                            subTypeStr = typeBean.course
                                            date = typeBean.date
                                            grade = typeBean.grade
                                            listJson = Gson().toJson(typeBean)
                                            contentJson = Gson().toJson(homeworkBook)
                                            downloadUrl = it
                                            zipUrl = homeworkBook.bodyUrl
                                            bookId = typeBean.bookId
                                        })
                                        startUpload(cloudList, nullItems)
                                    }
                                }
                            } else {
                                cloudList.add(CloudListBean().apply {
                                    this.type = 2
                                    subType = 4
                                    subTypeStr = typeBean.course
                                    date = typeBean.date
                                    grade = typeBean.grade
                                    listJson = Gson().toJson(typeBean)
                                    contentJson = Gson().toJson(homeworkBook)
                                    zipUrl = homeworkBook.bodyUrl
                                    bookId = typeBean.bookId
                                })
                                startUpload(cloudList, nullItems)
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 开始上传到云书库
     */
    private fun startUpload(list: MutableList<CloudListBean>, nullList: MutableList<HomeworkTypeBean>) {
        if (list.size == HomeworkTypeDaoManager.getInstance().queryAllExcludeCloud().size - nullList.size)
            mCloudUploadPresenter.upload(list)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        homeworkTypes.clear()
        mAdapter?.notifyDataSetChanged()
        setClearHomework()
        setSystemControlClear()
    }

}