package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.HomeworkMessageDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.CourseItem
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.activity.drawing.FileDrawingActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.*
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock


/**
 * 作业分为老师作业本、家长作业本（不同的接口处理）
 * （作业分类、作业内容、作业卷目录在创建以及数据更新时候都需要创建、修改增量更新）
 */
class HomeworkFragment : BaseMainFragment(), IHomeworkView {

    private var mCourseItem: CourseItem? = null
    private var mCourse = ""//当前科目
    private val lock = ReentrantLock()
    private var countDownTasks: CountDownLatch? = null //异步完成后操作
    private val mPresenter = HomeworkPresenter(this)
    private var mAdapter: HomeworkAdapter? = null
    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()//当前页分类
    private var onlineTeacherTypes = mutableListOf<HomeworkTypeBean>()
    private var onlineParentTypes = mutableListOf<ParentTypeBean>()
    private var position = 0


    override fun onTypeList(list: MutableList<HomeworkTypeBean>) {
        //老师分类数据没有变化不执行保存操作
        if (onlineTeacherTypes != list) {
            onlineTeacherTypes = list
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
                        DataUpdateManager.createDataUpdateState(2, item.typeId, 1,  item.state, Gson().toJson(item))
                    }
                } else {
                    item.contentResId = DataBeanManager.getHomeWorkContentStr(mCourse, mUser?.grade!!)
                    item.course = mCourse
                    item.createStatus = 1
                    if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(item.typeId)) {
                        item.id = null
                        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                        //创建增量数据
                        DataUpdateManager.createDataUpdateState(2, item.typeId, 1, item.state, Gson().toJson(item))
                    }
                }
            }
        }
        countDownTasks?.countDown()
    }

    override fun onTypeParentList(list: MutableList<ParentTypeBean>) {
        //家长分类数据没有变化不执行保存操作
        if (onlineParentTypes != list) {
            onlineParentTypes = list
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
                    DataUpdateManager.createDataUpdateState(2, homeworkTypeBean.typeId, 1, homeworkTypeBean.state, Gson().toJson(homeworkTypeBean))
                }
            }
        }
        countDownTasks?.countDown()
    }

    override fun onMessageList(map: Map<String, HomeworkMessage>) {
        for (item in homeworkTypes) {
            if (item.createStatus == 1) {
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
    }

    override fun onParentMessageList(map: MutableMap<String, ParentHomeworkMessage>) {
        for (item in homeworkTypes) {
            if (item.createStatus == 2) {
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

    override fun onDownloadSuccess() {
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(courseItem: CourseItem): HomeworkFragment {
        val fragment = HomeworkFragment()
        val bundle = Bundle()
        bundle.putSerializable("courseItem", courseItem)
        fragment.arguments = bundle
        return fragment
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        mCourseItem = arguments?.getSerializable("courseItem") as CourseItem
        mCourse = mCourseItem?.subject!!
        pageSize = 9

        initRecyclerView()
    }

    override fun lazyLoad() {
        pageIndex = 1
        fetchHomeworkType()
    }

    private fun initRecyclerView() {

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 30f),
            DP2PX.dip2px(requireActivity(), 35f),
            DP2PX.dip2px(requireActivity(), 30f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        mAdapter = HomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 32f), 25))
            setOnItemClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (item.isPg) {
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
                            val intent = Intent(requireActivity(), HomeworkBookStoreActivity::class.java)
                            intent.putExtra("bookId", item.bookId)
                            customStartActivity(intent)
                        }
                    }
                    5->{
                        customStartActivity(Intent(requireActivity(), FileDrawingActivity::class.java)
                            .putExtra("pageIndex",Constants.DEFAULT_PAGE)
                            .putExtra("pagePath",FileAddress().getPathScreenHomework(item.name, item.grade))
                        )
                    }
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (view.id == R.id.ll_message) {
                    if (item.isMessage) {
                        item.isMessage = false
                        notifyItemChanged(position)
                    }
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
                CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }

                    override fun ok() {
                        val item = homeworkTypes[position]
                        if (item.state==5){
                            FileUtils.deleteFileSkipMy(File(FileAddress().getPathScreenHomework(item.name,item.grade)))
                        }
                        else{
                            deleteHomework()
                        }
                    }
                })
                true
            }
        }
    }

    /**
     * 查询本地是否存在题卷本
     */
    private fun getLocalHomeworkBookType(item: HomeworkTypeBean): HomeworkTypeBean? {
        val types = HomeworkTypeDaoManager.getInstance().queryAllByState(mCourse, grade, 4)
        var homeworkTypeBean: HomeworkTypeBean? = null
        for (homeTypeBean in types) {
            if (homeTypeBean.name == item.name && homeTypeBean.bookId == item.bookId) {
                homeworkTypeBean = homeTypeBean
            }
        }
        return homeworkTypeBean
    }

    /**
     * 长按删除作业
     */
    private fun deleteHomework() {
        val item = homeworkTypes[position]
        HomeworkTypeDaoManager.getInstance().deleteBean(item)
        //删除增量更新
        DataUpdateManager.deleteDateUpdate(2, item.typeId, 1)
        when (item.state) {
            1 -> {
                val homePapers = HomeworkPaperDaoManager.getInstance().queryAll(mCourse, item.typeId)
                val path = FileAddress().getPathHomework(mCourse, item.typeId)
                FileUtils.deleteFile(File(path))

                for (paper in homePapers) {
                    val contentPapers = HomeworkPaperContentDaoManager.getInstance().queryByID(paper.contentId)
                    DataUpdateManager.deleteDateUpdate(2, paper.contentId,2,paper.typeId)
                    for (content in contentPapers) {
                        //创建增量数据
                        DataUpdateManager.deleteDateUpdate(2, content.id.toInt(), 3)
                    }
                }
            }
            2 -> {
                //删除作本内容
                val items = HomeworkContentDaoManager.getInstance().queryAllByType(mCourse, item.typeId)
                HomeworkContentDaoManager.getInstance().deleteBeans(items)
                //删除本地文件
                val path = FileAddress().getPathHomework(mCourse, item.typeId)
                FileUtils.deleteFile(File(path))

                //删除增量内容（普通作业本）
                for (homeContent in items) {
                    //删除增量更新
                    DataUpdateManager.deleteDateUpdate(2, homeContent.id.toInt(),2,item.typeId)
                }
            }
            3 -> {
                val recordBeans = RecordDaoManager.getInstance().queryAllByCourse(mCourse, item.typeId)
                val path = FileAddress().getPathRecord(mCourse, item.typeId)
                FileUtils.deleteFile(File(path))
                for (bean in recordBeans) {
                    RecordDaoManager.getInstance().deleteBean(bean)
                    //修改本地增量更新
                    DataUpdateManager.deleteDateUpdate(2, bean.id.toInt(),2,item.typeId,)
                }
            }
            4 -> {
                val homeworkBook = HomeworkBookDaoManager.getInstance().queryBookByID(item.bookId)
                if (homeworkBook != null) {
                    //删除文件
                    FileUtils.deleteFile(File(homeworkBook.bookPath))
                    HomeworkBookDaoManager.getInstance().delete(homeworkBook)
                    HomeworkBookCorrectDaoManager.getInstance().delete(homeworkBook.bookId)
                    //删除增量更新
                    DataUpdateManager.deleteDateUpdate(7, item.bookId, 1)
                    DataUpdateManager.deleteDateUpdate(7, item.bookId, 2)
                }
            }
        }
        mAdapter?.remove(position)
    }


    //选择内容背景
    fun addContentModule() {
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
                    addHomeWorkType(item)
                }
        } else {
            val item = HomeworkTypeBean()
            item.contentResId = ToolUtils.getImageResStr(activity, list[0].resContentId)
            addHomeWorkType(item)
        }
    }

    //添加作业本
    private fun addHomeWorkType(item: HomeworkTypeBean) {
        InputContentDialog(requireContext(), screenPos, getString(R.string.homework_create_hint)).builder()
            .setOnDialogClickListener { string ->
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
                DataUpdateManager.createDataUpdateState(2, item.typeId, 1,  item.state, Gson().toJson(item))
                if (homeworkTypes.size == 9) {
                    pageIndex += 1
                }
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
                //下载完成后 请求
                mPresenter.downloadParent(item.id)
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
                            lock.lock()
                            //更新增量数据
                            for (homework in homeworkContents) {
                                DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2,homework.homeworkTypeId)
                            }
                            val typeBean = HomeworkTypeDaoManager.getInstance().queryByParentTypeId(item.parentHomeworkId)
                            //添加批改详情
                            HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
                                type = 2
                                studentTaskId = item.id
                                content = item.content
                                homeworkTypeStr = typeBean.name
                                course = typeBean.course
                                time = System.currentTimeMillis()
                            })
                            lock.unlock()
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
            val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
            //本地不存在时，结束本次下载
            if (homeworkBookBean == null) {
                mPresenter.commitDownload(item.id)
                continue
            }
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
                            lock.lock()
                            //下载完成后 请求
                            mPresenter.downloadParent(item.id)
                            //添加批改详情
                            HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
                                type = 2
                                studentTaskId = item.id
                                content = item.content
                                homeworkTypeStr = typeBean.name
                                course = typeBean.course
                                time = System.currentTimeMillis()
                            })
                            lock.unlock()
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
            val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
            //本地不存在时，结束本次下载
            if (homeworkBookBean == null) {
                mPresenter.commitDownload(item.id)
                continue
            }
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            for (i in item.page.split(",")) {
                val path = getIndexFile(homeworkBookBean, i.toInt())?.path!!
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
                            lock.lock()
                            //保存本次题卷本批改详情
                            val bookCorrectBean = HomeworkBookCorrectBean()
                            bookCorrectBean.homeworkTitle = item.title
                            bookCorrectBean.bookId = typeBean.bookId
                            bookCorrectBean.pages = item.page
                            bookCorrectBean.score = item.score
                            bookCorrectBean.correctMode = item.questionType
                            bookCorrectBean.correctJson = item.question
                            bookCorrectBean.scoreMode=item.questionMode
                            bookCorrectBean.answerUrl=item.answerUrl
                            bookCorrectBean.state=2//已完成
                            val id=HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                            //更新增量数据
                            DataUpdateManager.createDataUpdate(7, id.toInt(),2,homeworkBookBean.bookId ,Gson().toJson(bookCorrectBean),"")
                            //添加批改详情
                            HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
                                type = 2
                                studentTaskId = item.id
                                content = item.title
                                homeworkTypeStr = typeBean.name
                                course = typeBean.course
                                time = System.currentTimeMillis()
                            })
                            lock.unlock()
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
        val listFiles = FileUtils.getAscFiles(path)
        return if (listFiles.size > index) listFiles[index] else null
    }

    /**
     * 作业本 下载图片
     */
    private fun loadHomeworkImage(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id)
            if (homeworkContents.isNullOrEmpty()) {
                //下载完成后 请求
                mPresenter.commitDownload(item.id)
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
                            lock.lock()
                            //更新增量数据
                            for (homework in homeworkContents) {
                                homework.state = item.status
                                homework.score = item.score
                                homework.correctJson = item.question
                                homework.correctMode = item.questionType
                                HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                                DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2,item.typeId)
                            }
                            val homework = homeworkContents[0]
                            //添加批改详情
                            HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
                                type = 2
                                studentTaskId = item.id
                                content = homework.title
                                homeworkTypeStr = homework.typeStr
                                course = homework.course
                                time = System.currentTimeMillis()
                            })
                            lock.unlock()
                        }

                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
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
                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        }

                        override fun completed(task: BaseDownloadTask?) {
                            mPresenter.commitDownload(item.id)
                            val paperDaoManager = HomeworkPaperDaoManager.getInstance()
                            val paperContentDaoManager = HomeworkPaperContentDaoManager.getInstance()
                            //已完成
                            if (item.sendStatus == 2) {
                                val paper = paperDaoManager.queryByContentID(item.id)
                                if (paper != null) {
                                    paper.state = 2
                                    paper.score = item.score
                                    paper.correctJson = item.question
                                    paper.answerUrl=item.answerUrl
                                    paperDaoManager.insertOrReplace(paper)
                                    //更新目录增量数据
                                    DataUpdateManager.editDataUpdate(2, item.id, 2, item.typeId, Gson().toJson(paper))
                                    //获取本次作业的所有作业卷内容
                                    val contentPapers = paperContentDaoManager.queryByID(item.id)
                                    //更新作业卷内容增量数据
                                    for (contentPaper in contentPapers) {
                                        DataUpdateManager.editDataUpdate(2, contentPaper.id.toInt(), 3)
                                    }
                                    //添加批改详情
                                    HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
                                        type = 2
                                        studentTaskId = item.id
                                        content = paper.title
                                        homeworkTypeStr = paper.typeName
                                        course = paper.course
                                        time = System.currentTimeMillis()
                                    })
                                }

                            } else {
                                val contentBean = paperDaoManager.queryByContentID(item.id)
                                if (contentBean != null) return//避免重复下载
                                //查找到之前已经存储的数据、用于页码计算
                                val homeworkPapers = paperDaoManager.queryAll(item.subject, item.typeId) as MutableList<*>
                                val paperContents = paperContentDaoManager.queryAll(item.subject, item.typeId) as MutableList<*>
                                //创建作业卷目录
                                val paper = HomeworkPaperBean().apply {
                                    contentId = item.id
                                    course = item.subject
                                    typeId = item.typeId
                                    typeName = item.typeName
                                    title = item.title
                                    path = pathStr
                                    page = paperContents.size //子内容的第一个页码位置
                                    index = homeworkPapers.size //作业位置
                                    endTime = item.endTime //提交时间
                                    answerUrl=item.answerUrl
                                    isSelfCorrect=item.selfBatchStatus==1
                                    correctJson=item.question
                                    correctMode=item.questionType
                                    scoreMode=item.questionMode
                                }
                                paperDaoManager.insertOrReplace(paper)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2, item.id, 2,paper.typeId, Gson().toJson(paper),"")

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
                                    DataUpdateManager.createDataUpdate(2, id.toInt(), 3, Gson().toJson(paperContent), pathStr)
                                }
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
     * 请求作业分类
     */
    private fun fetchHomeworkType() {
        if (NetworkUtil(requireContext()).isNetworkConnected()) {
            countDownTasks = CountDownLatch(2)

            val map = HashMap<String, Any>()
            map["size"] = 100
            map["grade"] = mUser?.grade!!
            map["type"] = 2
            map["userId"] = mCourseItem?.userId!!
            mPresenter.getTypeList(map)

            val parentMap = HashMap<String, Any>()
            parentMap["subject"] = DataBeanManager.getCourseId(mCourse)
            parentMap["childId"] = mUser?.accountId!!
            mPresenter.getTypeParentList(parentMap)

            //等待两个请求完成后刷新列表
            Thread {
                countDownTasks?.await()
                requireActivity().runOnUiThread {
                    fetchData()
                }
                countDownTasks = null
            }.start()
        } else {
            fetchData()
        }
    }

    override fun fetchData() {
        val types = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse, pageIndex, pageSize)
        if (homeworkTypes != types) {
            val totalTypes = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse)
            homeworkTypes = types
            setPageNumber(totalTypes.size)
            mAdapter?.setNewData(homeworkTypes)
        }
        if (NetworkUtil(requireActivity()).isNetworkConnected())
            fetchMessage()
    }

    fun clearData() {
        onlineParentTypes.clear()
        onlineTeacherTypes.clear()
        homeworkTypes.clear()
        mAdapter?.setNewData(homeworkTypes)
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

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                lazyLoad()
            }
            Constants.HOMEWORK_BOOK_EVENT -> {
                fetchData()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onNetworkConnectionSuccess() {
        fetchHomeworkType()
    }

}