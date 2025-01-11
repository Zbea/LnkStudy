package com.bll.lnkstudy.ui.fragment

import android.content.Intent
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
import com.bll.lnkstudy.dialog.HomeworkMessageDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList
import com.bll.lnkstudy.mvp.model.homework.HomeworkRequestArguments
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.ParentTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.activity.drawing.FileDrawingActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import java.io.File
import java.util.concurrent.CountDownLatch


/**
 * 作业分为老师作业本、家长作业本（不同的接口处理）
 * （作业分类、作业内容、作业卷目录在创建以及数据更新时候都需要创建、修改增量更新）
 */
class HomeworkFragment : BaseMainFragment(), IHomeworkView {
    private val mPresenter = HomeworkPresenter(this)
    private var mCourse = ""//当前科目
    private var countDownTasks: CountDownLatch? = null //异步完成后操作
    private var mAdapter: HomeworkAdapter? = null
    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()//当前页分类
    private var position = 0

    override fun onTypeList(list: MutableList<HomeworkTypeBean>) {
        //获取老师创建作业分类id，用来验证本地作业分类是否可删
        val createTypeIds = mutableListOf<Int>()
        //遍历查询作业本是否保存，以及保存后的作业名称修改
        for (item in list) {
            //是否是自动生成的作业本，自动生成的作业本不同老师公用
            if (item.autoState == 1) {
                val localItem = HomeworkTypeDaoManager.getInstance().queryByAutoName(item.name, mCourse, item.grade)
                if (localItem == null) {
                    item.typeId = ToolUtils.getDateId()
                    insertHomeworkType(item)
                } else {
                    if (localItem.createStatus == 0) {
                        editHomeworkTypeCreate(localItem, 2)
                    }
                }
            } else {
                createTypeIds.add(item.typeId)
                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(item.typeId)) {
                    if (item.state == 4) {
                        val homeworkTypeBean = getLocalHomeworkBookType(item)
                        //如果存在本地题卷本，则删除原来的，重新添加
                        if (homeworkTypeBean != null) {
                            HomeworkTypeDaoManager.getInstance().deleteBean(homeworkTypeBean)
                            //删除原来的本地数据
                            DataUpdateManager.deleteDateUpdate(2, item.typeId, 1)
                        }
                    }
                    insertHomeworkType(item)
                } else {
                    val homeworkTypeBean = HomeworkTypeDaoManager.getInstance().queryByTypeId(item.typeId)
                    if (homeworkTypeBean.createStatus == 0) {
                        editHomeworkTypeCreate(homeworkTypeBean, 2)
                    }
                    else{
                        if (homeworkTypeBean.name != item.name) {
                            editHomeworkTypeName(homeworkTypeBean, item.name)
                        }
                    }
                }
            }
        }

        //验证本地老师自动生成作业本，线上是否还存在，不存在转为非线上状态
        val autoTypes = HomeworkTypeDaoManager.getInstance().queryAllByCreate(mCourse, 2, 1)
        for (item in autoTypes) {
            if (!isAutoExistLineType(item, list)) {
                editHomeworkTypeCreate(item, 0)
            }
        }

        //验证本地老师创建作业本，线上是否还存在，不存在转为非线上状态
        val localTypes = HomeworkTypeDaoManager.getInstance().queryAllByCreate(mCourse, 2, 0)
        for (item in localTypes) {
            if (!createTypeIds.contains(item.typeId)) {
                editHomeworkTypeCreate(item, 0)
            }
        }

        countDownTasks?.countDown()
    }

    override fun onTypeParentList(list: MutableList<ParentTypeBean>) {
        val createTypeIds = mutableListOf<Int>()
        for (item in list) {
            createTypeIds.add(item.id)
            val localTypeBean = HomeworkTypeDaoManager.getInstance().queryByParentTypeId(item.id,grade)
            if (localTypeBean==null) {
                val homeworkTypeBean = HomeworkTypeBean().apply {
                    teacherId = item.parentId
                    name = item.name
                    grade = this@HomeworkFragment.grade
                    typeId = item.id
                    state = if (item.type == 1) 2 else 4
                    date = System.currentTimeMillis()
                    contentResId = if (item.type == 1) DataBeanManager.getHomeWorkContentStr(mCourse, this@HomeworkFragment.grade) else ""
                    course = mCourse
                    bgResId = item.imageUrl
                    bookId = item.bookId
                    createStatus = 1
                    fromStatus=1
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                //创建增量数据
                DataUpdateManager.createDataUpdate(2, homeworkTypeBean.typeId, 1, Gson().toJson(homeworkTypeBean))
            } else {
                if (localTypeBean.createStatus==0){
                    editHomeworkTypeCreate(localTypeBean,1)
                }
                else{
                    if (localTypeBean.name != item.name) {
                        editHomeworkTypeName(localTypeBean,item.name)
                    }
                }
            }
        }

        //把所有除开当前年级的家长作业本改为非在线状态
        val allTypes=HomeworkTypeDaoManager.getInstance().queryAllParentByExceptGrade(grade)
        for (item in allTypes){
            editHomeworkTypeCreate(item,0)
        }

        //判断所有当前在线状态的家长作业本是线上还存在，不存在改为非线上状态
        val localTypes = HomeworkTypeDaoManager.getInstance().queryAllByCreate(mCourse, 1, 0)
        for (item in localTypes) {
            if (!createTypeIds.contains(item.typeId)) {
                editHomeworkTypeCreate(item,0)
            }
        }

        countDownTasks?.countDown()
    }

    override fun onMessageList(map: Map<String, HomeworkMessageList>) {
        for (item in homeworkTypes) {
            if (item.createStatus == 2) {
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

    override fun onParentMessageList(map: MutableMap<String, ParentHomeworkMessageList>) {
        for (item in homeworkTypes) {
            if (item.createStatus == 1) {
                val bean = map[item.typeId.toString()]
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
    override fun onPaperList(list: HomeworkPaperList) {
        for (item in list.list) {
            when (item.subType) {
                1 -> {
                    loadHomeworkPaperImage(item)
                }
                4->{
                    loadHomeworkBook(item)
                }
                2,6->{
                    loadHomeworkImage(item)
                }
            }
        }
    }

    //下载家长批改下发
    override fun onParentReel(list: ParentHomeworkMessageList) {
        for (item in list.list){
            when (item.type) {
                1 -> {
                    loadParentHomeworkImage(item)
                }
                2 -> {
                    loadParentHomeworkBook(item)
                }
            }
        }
    }

    override fun onDownloadSuccess() {
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(courseItem: String): HomeworkFragment {
        val fragment = HomeworkFragment()
        val bundle = Bundle()
        bundle.putString("courseItem", courseItem)
        fragment.arguments = bundle
        return fragment
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        mCourse = arguments?.getString("courseItem").toString()
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
                    2,6 -> {
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
                    5 -> {
                        customStartActivity(
                            Intent(requireActivity(), FileDrawingActivity::class.java)
                                .putExtra("pageIndex", Constants.DEFAULT_PAGE)
                                .putExtra("pagePath", FileAddress().getPathScreenHomework(item.name, item.grade))
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
                    if (item.createStatus == 2) {
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
                val item = homeworkTypes[position]
                //当前年级的老师、家长、本地错题本无法删除
                if (item.createStatus != 0 && !item.isCloud) {
                    return@setOnItemLongClickListener true
                }
                onLongClick()
                true
            }
        }
    }

    /**
     * 添加作业本
     */
    private fun insertHomeworkType(item: HomeworkTypeBean) {
        item.id = null
        item.course = mCourse
        item.createStatus = 2
        item.fromStatus=2
        item.date=System.currentTimeMillis()
        if (item.state == 6) {
            item.contentResId = ToolUtils.getImageResStr(requireActivity(), R.mipmap.icon_homework_content_yw_lzb)
        } else {
            if (item.name=="作文作业本"){
                item.contentResId = ToolUtils.getImageResStr(requireActivity(), if (item.grade>6) R.mipmap.icon_homework_content_yw_zxzwb else R.mipmap.icon_homework_content_yw_zwb)
            }
            else{
                item.contentResId = DataBeanManager.getHomeWorkContentStr(mCourse, item.grade)
            }
        }
        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
        //创建增量数据
        DataUpdateManager.createDataUpdate(2, item.typeId, 1, Gson().toJson(item))
    }

    /**
     * 当作业本低于当前学生年级或者老师作业本已经删除，则改变作业本创建状态
     */
    private fun editHomeworkTypeCreate(item: HomeworkTypeBean, createStatus: Int) {
        item.createStatus = createStatus
        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
        //修改增量更新
        DataUpdateManager.editDataUpdate(2, item.typeId, 1, Gson().toJson(item))
    }

    /**
     * 作业本名称改变
     */
    private fun editHomeworkTypeName(item: HomeworkTypeBean, name: String) {
        item.name = name
        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
        //修改增量更新
        DataUpdateManager.editDataUpdate(2, item.typeId, 1, Gson().toJson(item))
    }

    /**
     * 查询本地是否存在题卷本
     */
    private fun getLocalHomeworkBookType(item: HomeworkTypeBean): HomeworkTypeBean? {
        val types = HomeworkTypeDaoManager.getInstance().queryAllByBook(mCourse, item.grade)
        var homeworkTypeBean: HomeworkTypeBean? = null
        for (homeTypeBean in types) {
            if (homeTypeBean.name == item.name && homeTypeBean.bookId == item.bookId) {
                homeworkTypeBean = homeTypeBean
            }
        }
        return homeworkTypeBean
    }

    /**
     * 验证本地自动生成数据是否线上还存在
     */
    private fun isAutoExistLineType(item: HomeworkTypeBean, list: List<HomeworkTypeBean>): Boolean {
        var isExist = false
        for (lineItem in list) {
            if (lineItem.autoState == 1) {
                if (lineItem.name == item.name && lineItem.grade == item.grade) {
                    isExist = true
                }
            }
        }
        return isExist
    }

    private fun onLongClick() {
        val item = homeworkTypes[position]
        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = "删除"
            resId = R.mipmap.icon_setting_delete
        })
        beans.add(ItemList().apply {
            name = "置顶"
            resId = R.mipmap.icon_setting_top
        })
        beans.add(ItemList().apply {
            name = "重命名"
            resId = R.mipmap.icon_setting_edit
        })
        LongClickManageDialog(requireActivity(), 2, item.name, beans).builder()
            .setOnDialogClickListener { position ->
                when (position) {
                    0 -> {
                        deleteHomework()
                    }

                    1 -> {
                        val items = HomeworkTypeDaoManager.getInstance().queryAllByLocal(mCourse)
                        if (items.size > 1) {
                            item.date = items[0].date - 1000
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                            //修改增量更新
                            DataUpdateManager.editDataUpdate(2, item.typeId, 1, Gson().toJson(item))
                            pageIndex = 1
                            fetchData()
                        }
                    }

                    2 -> {
                        InputContentDialog(requireActivity(), 2, item.name).builder().setOnDialogClickListener {
                            editHomeworkTypeName(item, it)
                            mAdapter?.notifyItemChanged(this@HomeworkFragment.position)
                        }
                    }
                }
            }
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
                    DataUpdateManager.deleteDateUpdate(2, paper.contentId, 2, paper.typeId)
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
                    DataUpdateManager.deleteDateUpdate(2, homeContent.id.toInt(), 2, item.typeId)
                }
            }
            3 -> {
                val recordBeans = RecordDaoManager.getInstance().queryAllByCourse(mCourse, item.typeId)
                val path = FileAddress().getPathHomework(mCourse, item.typeId)
                FileUtils.deleteFile(File(path))
                for (bean in recordBeans) {
                    RecordDaoManager.getInstance().deleteBean(bean)
                    //修改本地增量更新
                    DataUpdateManager.deleteDateUpdate(2, bean.id.toInt(), 2, item.typeId)
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
            5 -> {
                FileUtils.deleteFileSkipMy(File(FileAddress().getPathScreenHomework(item.name, item.grade)))
            }
        }
        mAdapter?.remove(position)
        fetchData()
    }


    //选择内容背景
    fun addContentModule() {
        val list = when (mCourse) {
            "语文" -> {
                DataBeanManager.getYw(grade)
            }

            "数学" -> {
                DataBeanManager.getSx(grade)
            }

            "英语" -> {
                DataBeanManager.getYy(grade)
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
                    grade = this@HomeworkFragment.grade
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                //创建增量数据
                DataUpdateManager.createDataUpdate(2, item.typeId, 1, Gson().toJson(item))
                if (homeworkTypes.size == 9) {
                    pageIndex += 1
                }
                fetchData()
            }
    }

    /**
     * 下载家长作业本图片
     */
    private fun loadParentHomeworkImage(item: ParentHomeworkMessageList.ParentHomeworkBean) {
        if (!HomeworkTypeDaoManager.getInstance().isExistParentType(item.typeId,grade)){
            return
        }
        val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id, item.typeId)
        val images = item.changeUrl.split(",").toMutableList()
        if (homeworkContents.isNullOrEmpty()) {
            for (i in images){
                homeworkContents.add(newHomeWorkContent(1,item.typeId,item.id,item.typeId))
            }
        }
        val paths = mutableListOf<String>()
        for (homework in homeworkContents) {
            paths.add(homework.path)
        }
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
            .startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        refreshView(item.typeId,2)
                        mPresenter.downloadParent(item.id)

                        //更新增量数据
                        for (homework in homeworkContents) {
                            homework.state = 2
                            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                            DataUpdateManager.editDataUpdateState(2, homework.id.toInt(), 2, homework.homeworkTypeId, 2, Gson().toJson(homework))
                        }
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    /**
     * 题卷本 下载图片
     */
    private fun loadParentHomeworkBook(item: ParentHomeworkMessageList.ParentHomeworkBean) {
        val typeBean = HomeworkTypeDaoManager.getInstance().queryByParentTypeId(item.typeId,grade)
        val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId) ?: return
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
                        refreshView(item.typeId,2)
                        //下载完成后 请求
                        mPresenter.downloadParent(item.id)

                        for (page in item.pageStr.split(",")) {
                            if (HomeworkBookCorrectDaoManager.getInstance().isExist(typeBean.bookId, page.toInt())) {
                                val bookCorrectBean = HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(typeBean.bookId, page.toInt())
                                bookCorrectBean.state = 2
                                bookCorrectBean.homeworkTitle = item.title
                                HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(bookCorrectBean)
                                DataUpdateManager.editDataUpdate(7, bookCorrectBean.id.toInt(), 2, typeBean.bookId, Gson().toJson(bookCorrectBean))
                            } else {
                                val bookCorrectBean = HomeworkBookCorrectBean()
                                bookCorrectBean.startTime = System.currentTimeMillis()
                                bookCorrectBean.homeworkTitle = item.title
                                bookCorrectBean.bookId = typeBean.bookId
                                bookCorrectBean.page = page.toInt()
                                bookCorrectBean.state = 2//已完成
                                val id = HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                                //更新增量数据
                                DataUpdateManager.createDataUpdate(7, id.toInt(), 2, homeworkBookBean.bookId, Gson().toJson(bookCorrectBean), "")
                            }
                        }
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }


    /**
     * 题卷本 下载图片
     */
    private fun loadHomeworkBook(item:HomeworkPaperList.HomeworkPaperListBean) {
        val typeBean = HomeworkTypeDaoManager.getInstance().queryByTypeId(item.typeId)
        val homeworkBookBean = HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId) ?: return
        //拿到对应作业的所有本地图片地址
        val paths = mutableListOf<String>()
        for (page in item.page.split(",")) {
            paths.add(getIndexFile(homeworkBookBean, page.toInt())?.path!!)
        }
        //获得下载地址
        val images = item.submitUrl.split(",").toMutableList()
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
            .startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        refreshView(item.typeId,2)
                        //下载完成后 请求
                        mPresenter.downloadCompletePaper(item.id)

                        for (page in item.page.split(",")) {
                            if (HomeworkBookCorrectDaoManager.getInstance().isExist(typeBean.bookId, page.toInt())) {
                                val bookCorrectBean = HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(typeBean.bookId, page.toInt())
                                bookCorrectBean.state = 2
                                bookCorrectBean.homeworkTitle = item.title
                                bookCorrectBean.score = item.score
                                bookCorrectBean.correctMode = item.questionType
                                bookCorrectBean.correctJson = item.question
                                bookCorrectBean.scoreMode = item.questionMode
                                bookCorrectBean.answerUrl = item.answerUrl
                                HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(bookCorrectBean)
                                DataUpdateManager.editDataUpdate(7, bookCorrectBean.id.toInt(), 2, typeBean.bookId, Gson().toJson(bookCorrectBean))
                            } else {
                                val bookCorrectBean = HomeworkBookCorrectBean()
                                bookCorrectBean.homeworkTitle = item.title
                                bookCorrectBean.bookId = typeBean.bookId
                                bookCorrectBean.page = page.toInt()
                                bookCorrectBean.score = item.score
                                bookCorrectBean.correctMode = item.questionType
                                bookCorrectBean.correctJson = item.question
                                bookCorrectBean.scoreMode = item.questionMode
                                bookCorrectBean.answerUrl = item.answerUrl
                                bookCorrectBean.state = 2//已完成
                                val id = HomeworkBookCorrectDaoManager.getInstance().insertOrReplaceGetId(bookCorrectBean)
                                //更新增量数据
                                DataUpdateManager.createDataUpdate(7, id.toInt(), 2, homeworkBookBean.bookId, Gson().toJson(bookCorrectBean), "")
                            }
                        }
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    /**
     * 获得题卷本图片地址
     */
    private fun getIndexFile(bookBean: HomeworkBookBean, page: Int): File? {
        val path = FileAddress().getPathHomeworkBookPicture(bookBean.bookPath)
        val listFiles = FileUtils.getAscFiles(path)
        return if (listFiles.size > page) listFiles[page] else null
    }

    /**
     * 作业本 下载图片
     */
    private fun loadHomeworkImage(item: HomeworkPaperList.HomeworkPaperListBean) {
        var homeworkTypeId=item.typeId
        //如果是默认创建的作业本,拿到本地作业本
        if (item.autoState==1){
            val homeworkTypeBean= HomeworkTypeDaoManager.getInstance().queryByAutoName(item.typeName,mCourse,item.grade) ?: return
            homeworkTypeId=homeworkTypeBean.typeId
        }
        else{
            if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(item.typeId)){
                return
            }
        }

        //获得下载地址
        val images = item.submitUrl.split(",").toMutableList()
        val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id, homeworkTypeId)
        //当本地作业本内容已经被删除时，重新创建新内容
        if (homeworkContents.isNullOrEmpty()) {
            for (i in images){
                homeworkContents.add(newHomeWorkContent(2,homeworkTypeId,item.id,item.typeId))
            }
        }

        //拿到对应作业的所有本地图片地址
        val paths = mutableListOf<String>()
        for (homework in homeworkContents) {
            paths.add(homework.path)
        }

        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
            .startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        refreshView(homeworkTypeId,2)
                        mPresenter.downloadCompletePaper(item.id)

                        //更新增量数据
                        for (homework in homeworkContents) {
                            homework.state = item.status
                            homework.score = item.score
                            homework.correctJson = item.question
                            homework.correctMode = item.questionType
                            homework.scoreMode=item.questionMode
                            homework.answerUrl = item.answerUrl
                            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                            DataUpdateManager.editDataUpdateState(2, homework.id.toInt(), 2, homework.homeworkTypeId, 2, Gson().toJson(homework))
                        }
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    /**
     * 创建本地作业本
     */
    private fun newHomeWorkContent(createStatus: Int,homeworkTypeId:Int,contentId:Int,typeId: Int):HomeworkContentBean {
        val homeworkTypeBean=if (createStatus==2)HomeworkTypeDaoManager.getInstance().queryByTypeId(homeworkTypeId) else HomeworkTypeDaoManager.getInstance().queryByParentTypeId(homeworkTypeId,grade)
        val homeworks=HomeworkContentDaoManager.getInstance().queryAllByType(homeworkTypeBean.course,homeworkTypeId)
        val path = FileAddress().getPathHomework(homeworkTypeBean.course, homeworkTypeId, homeworks.size+1)
        val currentTime=System.currentTimeMillis()

        val homeworkContent = HomeworkContentBean()
        homeworkContent.course = homeworkTypeBean.course
        homeworkContent.date = currentTime
        homeworkContent.homeworkTypeId = homeworkTypeId
        homeworkContent.typeId=typeId
        homeworkContent.typeName = homeworkTypeBean.name
        homeworkContent.title = getString(R.string.unnamed) + (homeworks.size + 1)
        homeworkContent.path = "$path/${DateUtils.longToString(currentTime)}.png"
        homeworkContent.page = homeworks.size
        homeworkContent.contentId=contentId

        val id = HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContent)
        homeworkContent.id=id
        DataUpdateManager.createDataUpdateState(2, id.toInt(), 2,homeworkTypeId ,homeworkTypeBean.state, Gson().toJson(homeworkContent), homeworkContent.path)
        return homeworkContent
    }

    /**
     * 作业卷下载图片、将图片保存到作业
     */
    private fun loadHomeworkPaperImage(item: HomeworkPaperList.HomeworkPaperListBean) {
        var homeworkTypeId=item.typeId
        //如果是默认创建的作业本
        if (item.autoState==1){
            val homeworkTypeBean= HomeworkTypeDaoManager.getInstance().queryByAutoName(item.typeName,mCourse,item.grade) ?: return
            homeworkTypeId=homeworkTypeBean.typeId
        }
        else{
            if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(item.typeId)){
                return
            }
        }
        //设置路径 作业卷路径
        val pathStr = FileAddress().getPathHomework(mCourse, homeworkTypeId, item.id)
        //学生未提交
        val images = if (item.sendStatus == 2) {
            item.submitUrl.split(",").toMutableList()
        } else {
            item.imageUrl.split(",").toMutableList()
        }
        val paths = mutableListOf<String>()
        val drawPaths = mutableListOf<String>()
        for (i in images.indices) {
            paths.add("$pathStr/${i + 1}.png")
            drawPaths.add("$pathStr/${i + 1}draw.png")
        }
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(paths)
            .startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        refreshView(homeworkTypeId,item.sendStatus)
                        mPresenter.downloadCompletePaper(item.id)

                        val localPaper = HomeworkPaperDaoManager.getInstance().queryByContentID(item.id)
                        if(localPaper==null){
                            //查找到之前已经存储的数据、用于页码计算
                            val homeworkPapers = HomeworkPaperDaoManager.getInstance().queryAll(item.subject, item.typeId) as MutableList<*>
                            //创建作业卷目录
                            val paper = HomeworkPaperBean().apply {
                                contentId = item.id
                                course = item.subject
                                this.homeworkTypeId = homeworkTypeId
                                typeId = item.typeId
                                typeName = item.typeName
                                title = item.title
                                filePath = pathStr
                                this.paths = paths
                                this.drawPaths = drawPaths
                                page = homeworkPapers.size
                                endTime = item.endTime //提交时间
                                answerUrl = item.answerUrl
                                isSelfCorrect = item.selfBatchStatus == 1
                                correctJson = item.question
                                correctMode = item.questionType
                                scoreMode = item.questionMode
                                if (item.sendStatus==2){
                                    state = 2
                                    score = item.score
                                    correctJson = item.question
                                    answerUrl = item.answerUrl
                                }
                            }
                            HomeworkPaperDaoManager.getInstance().insertOrReplace(paper)
                            //创建增量数据
                            DataUpdateManager.createDataUpdateState(2, item.id, 2, homeworkTypeId, 1, Gson().toJson(paper), pathStr)
                        }
                        else{
                            if (item.sendStatus==2){
                                localPaper.state = 2
                                localPaper.score = item.score
                                localPaper.correctJson = item.question
                                localPaper.answerUrl = item.answerUrl
                                HomeworkPaperDaoManager.getInstance().insertOrReplace(localPaper)
                                //更新目录增量数据
                                DataUpdateManager.editDataUpdate(2, item.id, 2, homeworkTypeId, Gson().toJson(localPaper))
                            }
                        }
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    /**
     * 刷新收到作业标题
     */
    private fun refreshView(typeId:Int,state:Int){
        for (item in homeworkTypes){
            if (item.typeId==typeId){
                if (state==2){
                    item.isPg=true
                }
                else{
                    item.isMessage=true
                }
                mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
            }
        }
    }

    /**
     * 请求作业分类
     */
    private fun fetchHomeworkType() {
        if (NetworkUtil(MyApplication.mContext).isNetworkConnected()&&grade>0&&DataBeanManager.getCourseId(mCourse)>0) {
            countDownTasks = CountDownLatch(2)
            val map = HashMap<String, Any>()
            map["type"] = 2
            map["subject"] = DataBeanManager.getCourseId(mCourse)
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
        val totalTypes = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse)
        setPageNumber(totalTypes.size)

        homeworkTypes = HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse, pageIndex, pageSize)
        mAdapter?.setNewData(homeworkTypes)

        if (NetworkUtil(MyApplication.mContext).isNetworkConnected())
            fetchMessage()
    }

    /**
     * 遍历所有作业本，获取对应作业本消息
     * 获取作业卷最新的老师下发
     */
    private fun fetchMessage() {
        val arrayIds = arrayListOf<Int>()
        for (item in homeworkTypes) {
            if (item.createStatus == 1) {
                arrayIds.add(item.typeId)
            }
        }
        val mapParentMessage = HashMap<String, Any>()
        mapParentMessage["ids"] = arrayIds
        mapParentMessage["size"] = 10
        mPresenter.getParentMessage(mapParentMessage)

        val mapCorrectParent=HashMap<String, Any>()
        mapCorrectParent["subject"]=DataBeanManager.getCourseId(mCourse)
        mPresenter.getParentReel(mapCorrectParent)

        val list = arrayListOf<HomeworkRequestArguments>()
        for (item in homeworkTypes) {
            if (item.createStatus == 2 && item.state != 1) {
                if (item.autoState == 1) {
                    list.add(HomeworkRequestArguments().apply {
                        id = item.typeId
                        size = 10
                        grade = item.grade
                        name = item.name
                        subject = DataBeanManager.getCourseId(mCourse)
                    })
                } else {
                    list.add(HomeworkRequestArguments().apply {
                        id = item.typeId
                        size = 10
                        commonTypeId = item.typeId
                    })
                }
            }
        }
        val map = HashMap<String, Any>()
        map["studentDto"] = list
        mPresenter.getMessageList(map)

        val mapPaper = HashMap<String, Any>()
        mapPaper["subject"] = mCourse
        mPresenter.getPaperList(mapPaper)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.HOMEWORK_BOOK_EVENT -> {
                fetchData()
            }
        }
    }

    override fun onRefreshData() {
        fetchHomeworkType()
    }

    override fun onNetworkConnectionSuccess() {
        fetchHomeworkType()
    }

}