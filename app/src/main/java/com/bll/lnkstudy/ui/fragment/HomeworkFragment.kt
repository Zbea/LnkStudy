package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.DataBeanManager.getHomeworkCoverStr
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
import com.bll.lnkstudy.ui.activity.PastHomeworkActivity
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_homework.*
import java.io.File


/**
 * 作业 （作业分类、作业内容、作业卷目录在创建以及数据更新时候都需要创建、修改增量更新）
 */
class HomeworkFragment : BaseFragment(), IHomeworkView {

    private val mPresenter = HomeworkPresenter(this)
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var mAdapter: HomeworkAdapter? = null
    private var mCourse = ""//当前科目
    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()//当前页分类
    private var popupType=0

    override fun onTypeList(list: MutableList<HomeworkTypeBean>) {
        //遍历查询作业本是否保存
        for (item in list) {
            if (item.state==4){//如果是题卷本，遍历查询本地是否已经下载，关联
                val homeworkTypeBean=getLocalHomeworkBookType(item)
                if (homeworkTypeBean!=null){
                    if (homeworkTypeBean.createStatus==0){
                        homeworkTypeBean.typeId=item.typeId
                        homeworkTypeBean.createStatus=1
                        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    }
                }
                else{
                    item.course = mCourse
                    item.createStatus=1
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                }
            }
            else{
                item.contentResId = DataBeanManager.getHomeWorkContentStr(mCourse, mUser?.grade!!)
                item.course = mCourse
                item.bgResId = getHomeworkCoverStr() //当前作业本背景样式id
                item.createStatus=1
                if (!isSaveHomework(item)) {
                    HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    //创建增量数据
                    DataUpdateManager.createDataUpdate(2,item.typeId,1,item.typeId,item.state,Gson().toJson(item))
                }
            }
        }
        fetchData()
    }

    override fun onList(map: Map<String, HomeworkMessage>) {
        for (item in homeworkTypes) {
            val bean=map[item.typeId.toString()]
            if (bean!=null){
                item.message = bean
                item.isMessage = bean.total > item.messageTotal
                item.messageTotal = bean.total
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                if (item.isMessage)
                    mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
            }
        }
    }

    override fun onListReel(map: Map<String, HomeworkPaperList>) {
        for (item in homeworkTypes) {
            val bean=map[item.typeId.toString()]
            if (bean!=null){
                val reels=bean.list!!
                //遍历查询
                for (reel in reels) {
                    if (reel.sendStatus==2){
                        item.isPg=true
                    }
                    else{
                        item.isMessage = true
                    }
                }
                mAdapter?.notifyItemChanged(homeworkTypes.indexOf(item))
                //下载老师传过来的作业
                when(bean.subType){
                    1->{
                        loadHomeworkPaperImage(reels)
                    }
                    4->{
                        loadHomeworkBook(reels)
                    }
                    else->{
                        loadHomeworkImage(reels)
                    }
                }
            }
        }
    }

    override fun onDetails(details: MutableList<HomeworkDetails.HomeworkDetailBean>) {
        HomeworkCommitDetailsDialog(requireActivity(),screenPos,popupType,details).builder()
    }

    override fun onDownloadSuccess() {
    }

    override fun onCommitSuccess() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        pageSize=9
        setTitle(R.string.main_homework_title)
        showView(iv_manager)
        setClassGroupRequest(true)

        popWindowBeans.add(PopupBean(0, getString(R.string.homework_commit_details_str),true))
        popWindowBeans.add(PopupBean(1, getString(R.string.homework_correct_details_str),false))
        popWindowBeans.add(PopupBean(2, getString(R.string.homework_create_str),false))
        popWindowBeans.add(PopupBean(3, getString(R.string.homework_book_str),false))
        popWindowBeans.add(PopupBean(4, getString(R.string.homework_old_homework_str),false))

        iv_manager.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            popupType=0
                            mPresenter.getCommitDetailList()
                        }
                        1 -> {
                            popupType=1
                            mPresenter.getCorrectDetailList()
                        }
                        2 -> {
                            if(DataBeanManager.classGroups.size>0){
                                addCover()
                            }
                        }
                        3->{
                            if(DataBeanManager.classGroups.size>0){
                                customStartActivity(Intent(requireActivity(),HomeworkBookStoreActivity::class.java))
                            }
                        }
                        4->{
                            customStartActivity(Intent(requireActivity(),PastHomeworkActivity::class.java))
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
                item.isPg = false
                notifyItemChanged(position)
                when (item.state) {
                    1 -> {
                        item.isMessage = false
                        notifyItemChanged(position)
                        gotoHomeworkReelDrawing(item)
                    }
                    2 -> {
                        gotoHomeworkDrawing(item)
                    }
                    3 -> {
                        gotoHomeworkRecord(item)
                    }
                    4->{
                        if (HomeworkBookDaoManager.getInstance().isExist(item.bookId)){
                            val intent=Intent(context, HomeworkBookDetailsActivity::class.java)
                            val bundle= Bundle()
                            bundle.putSerializable("homework",item)
                            intent.putExtra("homeworkBundle",bundle)
                            customStartActivity(intent)
                        }
                        else{
                            showToast(screenPos,R.string.toast_homework_unDownload)
                        }
                    }
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                if (view.id == R.id.ll_message) {
                    if (item.message != null) {
                        HomeworkMessageDialog(requireActivity(), screenPos, item.name, item.message.list).builder()
                        item.isMessage = false
                        notifyItemChanged(position)
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                showHomeworkManage(position)
                true
            }
        }
    }

    //设置头部索引
    private fun initTab() {
        rg_group.removeAllViews()
        rg_group.setOnCheckedChangeListener(null)
        mCourse=""
        val classGroups = DataBeanManager.classGroups
        if (classGroups.size > 0) {
            mCourse = classGroups[0].subject
            for (i in classGroups.indices) {
                rg_group.addView(getRadioButton(i, classGroups[i].subject, classGroups.size - 1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                mCourse = classGroups[id].subject
                pageIndex=1
                fetchHomeworkType()
            }
            fetchHomeworkType()
        }
        else{
            homeworkTypes.clear()
            mAdapter?.notifyDataSetChanged()
        }
    }


    /**
     * 查询本地是否存在题卷本
     */
    private fun getLocalHomeworkBookType(item: HomeworkTypeBean): HomeworkTypeBean? {
        val types=HomeworkTypeDaoManager.getInstance().queryAllByState(mCourse,4)
        var homeworkTypeBean:HomeworkTypeBean?=null
        for (homeTypeBean in types){
            if (homeTypeBean.name==item.name&&homeTypeBean.bookId==item.bookId){
                homeworkTypeBean=homeTypeBean
            }
        }
        return homeworkTypeBean
    }

    /**
     * 判断 老师下发作业本是否已经保存本地
     */
    private fun isSaveHomework(item: HomeworkTypeBean): Boolean {
        var isSave = false
        val items=HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse)
        for (list in items) {
            if (item.name == list.name && item.typeId == list.typeId) {
                isSave = true
            }
        }
        return isSave
    }

    /**
     * 长按展示作业换肤、删除(自建作业、题卷本可以删除)
     */
    private fun showHomeworkManage(pos: Int) {
        val item = homeworkTypes[pos]
        HomeworkManageDialog(requireContext(), screenPos, item.createStatus==0).builder()
            .setOnDialogClickListener(object :
                HomeworkManageDialog.OnDialogClickListener {
                override fun onSkin() {
                    val list = DataBeanManager.homeworkCover
                    ModuleAddDialog(requireContext(), screenPos, getString(R.string.homework_cover_module_str), list).builder()
                        ?.setOnDialogClickListener { moduleBean ->
                            item.bgResId = ToolUtils.getImageResStr(activity, moduleBean.resId)
                            mAdapter?.notifyItemChanged(pos)
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                        }
                }

                override fun onDelete() {
                    //删除本地当前作业本
                    HomeworkTypeDaoManager.getInstance().deleteBean(item)
                    if (item.state==4){
                        val homeworkBook=HomeworkBookDaoManager.getInstance().queryBookByID(item.bookId)
                        if (homeworkBook!=null){
                            //删除文件
                            FileUtils.deleteFile(File(homeworkBook.bookPath))
                            HomeworkBookDaoManager.getInstance().delete(homeworkBook)
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(8,item.bookId,1,item.bookId)
                            DataUpdateManager.deleteDateUpdate(8,item.bookId,2,item.bookId)
                        }
                    }
                    else{
                        //删除作本内容
                        val items=HomeworkContentDaoManager.getInstance().queryAllByType(mCourse, item.typeId)
                        HomeworkContentDaoManager.getInstance().deleteBeans(items)
                        //删除本地文件
                        val path = FileAddress().getPathHomework(mCourse, item.typeId)
                        FileUtils.deleteFile(File(path))

                        //删除增量更新
                        DataUpdateManager.deleteDateUpdate(2,item.typeId,1,item.typeId)
                        //删除增量内容（普通作业本）
                        for (homeContent in items){
                            //删除增量更新
                            DataUpdateManager.deleteDateUpdate(2,homeContent.id.toInt(),2,item.typeId)
                        }
                    }
                    mAdapter?.remove(pos)
                }
            })
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
                    createStatus = 1
                    state = 2
                    grade=mUser?.grade!!
                }
                HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                fetchData()
                //创建增量数据
                DataUpdateManager.createDataUpdate(2,item.typeId,1,item.typeId,item.state,Gson().toJson(item))
            }
    }


    /**
     * 题卷本 下载图片
     */
    private fun loadHomeworkBook(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            val typeBean= HomeworkTypeDaoManager.getInstance().queryByTypeId(item.typeId)
            val homeworkBookBean=HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            for (i in item.page.split(",")){
                paths.add(getIndexFile(homeworkBookBean,i.toInt()-1)?.path!!)
            }
            //获得下载地址
            val images = item.submitUrl.split(",").toTypedArray()
            val imageDownLoad = ImageDownLoadUtils(activity, images, paths)
            imageDownLoad.startDownload1()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    //下载完成后 请求
                    mPresenter.commitDownload(item.id)
                    deleteDoneTask(imageDownLoad)
                    //更新增量数据
                    DataUpdateManager.editDataUpdate(8,homeworkBookBean.bookId,2,homeworkBookBean.bookId)
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    imageDownLoad.reloadImage()
                }
            })
        }
    }

    /**
     * 获得题卷本图片地址
     */
    private fun getIndexFile(bookBean: HomeworkBookBean,index: Int): File? {
        val path = FileAddress().getPathTextbookPicture(bookBean.bookPath)
        val listFiles = FileUtils.getFiles(path)
        return if (listFiles!=null) listFiles[index] else null
    }

    /**
     * 作业本 下载图片
     */
    private fun loadHomeworkImage(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            //拿到对应作业的所有本地图片地址
            val paths = mutableListOf<String>()
            val homeworkContents = HomeworkContentDaoManager.getInstance().queryAllById(item.id)
            for (homework in homeworkContents) {
                paths.add(homework.path)
            }
            //获得下载地址
            val images = item.submitUrl.split(",").toTypedArray()
            val imageDownLoad = ImageDownLoadUtils(activity, images, paths)
            imageDownLoad.startDownload1()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    //下载完成后 请求
                    mPresenter.commitDownload(item.id)
                    deleteDoneTask(imageDownLoad)
                    //更新增量数据
                    for (homework in homeworkContents) {
                        DataUpdateManager.editDataUpdate(2,homework.id.toInt(),2,homework.homeworkTypeId)
                    }
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
    private fun loadHomeworkPaperImage(papers: MutableList<HomeworkPaperList.HomeworkPaperListBean>) {
        for (item in papers) {
            if (mDownMapPool[item.id]!=null)
                continue
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
                    deleteDoneTask(imageDownLoad)
                    val paperDaoManager = HomeworkPaperDaoManager.getInstance()
                    val paperContentDaoManager = HomeworkPaperContentDaoManager.getInstance()
                    if (item.sendStatus == 2) {
                        val paper = paperDaoManager.queryByContentID(item.id)
                        if (paper != null) {
                            paper.isPg = true
                            paper.state=item.status
                            paperDaoManager.insertOrReplace(paper)
                            //获取本次作业的所有作业卷内容
                            val contentPapers=paperContentDaoManager.queryByID(item.id)
                            //更新目录增量数据
                            DataUpdateManager.editDataUpdate(2,item.id,2,item.typeId,Gson().toJson(paper))
                            //更新作业卷内容增量数据
                            for (contentPaper in contentPapers){
                                DataUpdateManager.editDataUpdate(2,contentPaper.id.toInt(),3,contentPaper.typeId)
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
                        DataUpdateManager.createDataUpdate(2,item.id,2,item.typeId,1,Gson().toJson(item))

                        for (i in 0 until map?.size!!) {
                            //创建作业卷内容
                            val paperContent = HomeworkPaperContentBean().apply {
                                course = item.subject
                                typeId = item.typeId
                                contentId = item.id
                                path = map[i]
                                drawPath = "$pathStr/${i + 1}/draw.tch"
                                page = paperContents.size + i
                            }
                            val id=paperContentDaoManager.insertOrReplaceGetId(paperContent)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,id.toInt(),3,item.typeId,1
                                ,Gson().toJson(paperContent),pathStr)
                        }
                    }
                }

                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                }
            })
            mDownMapPool[item.id]=imageDownLoad
        }
    }

    /**
     * 上传
     */
    fun upload(token:String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        for(classGroup in DataBeanManager.classGroups){
            val types=HomeworkTypeDaoManager.getInstance().queryAllByCourse(classGroup.subject)
            for (typeBean in types){
                //云作业本不上传
                if (!typeBean.isCloud){
                    when(typeBean.state){
                        1->{
                            val homePapers=HomeworkPaperDaoManager.getInstance().queryAll(typeBean.course,typeBean.typeId)
                            val homeworkContents=HomeworkPaperContentDaoManager.getInstance().queryAll(typeBean.course,typeBean.typeId)
                            val path=FileAddress().getPathHomework(typeBean.course,typeBean.typeId)
                            if (File(path).exists()){
                                FileUploadManager(token).apply {
                                    startUpload(path,typeBean.name)
                                    setCallBack{
                                        cloudList.add(CloudListBean().apply {
                                            this.type=2
                                            subType=1
                                            subTypeStr=typeBean.course
                                            date=typeBean.date
                                            grade=typeBean.grade
                                            listJson=Gson().toJson(typeBean)
                                            contentJson= Gson().toJson(homePapers)
                                            contentSubtypeJson=Gson().toJson(homeworkContents)
                                            downloadUrl=it
                                        })
                                        startUpload(cloudList)
                                    }
                                }
                            }
                            else{
                                cloudList.add(CloudListBean().apply {
                                    this.type=2
                                    subType=1
                                    subTypeStr=typeBean.course
                                    date=typeBean.date
                                    grade=typeBean.grade
                                    listJson= Gson().toJson(typeBean)
                                    downloadUrl="null"
                                })
                                startUpload(cloudList)
                            }
                        }
                        2->{
                            val homeworks=HomeworkContentDaoManager.getInstance().queryAllByType(typeBean.course,typeBean.typeId)
                            val path=FileAddress().getPathHomework(typeBean.course,typeBean.typeId)
                            if (File(path).exists()){
                                FileUploadManager(token).apply {
                                    startUpload(path,typeBean.name)
                                    setCallBack{
                                        cloudList.add(CloudListBean().apply {
                                            this.type=2
                                            subType=2
                                            subTypeStr=typeBean.course
                                            date=typeBean.date
                                            grade=typeBean.grade
                                            listJson=Gson().toJson(typeBean)
                                            contentJson= Gson().toJson(homeworks)
                                            downloadUrl=it
                                        })
                                        startUpload(cloudList)
                                    }
                                }

                            }else{
                                cloudList.add(CloudListBean().apply {
                                    this.type=2
                                    subType=2
                                    subTypeStr=typeBean.course
                                    date=typeBean.date
                                    grade=typeBean.grade
                                    listJson= Gson().toJson(typeBean)
                                    downloadUrl="null"
                                })
                                startUpload(cloudList)
                            }
                        }
                        3->{
                            val records=RecordDaoManager.getInstance().queryAllByCourse(typeBean.course,typeBean.typeId)
                            val path=FileAddress().getPathRecord(typeBean.course,typeBean.typeId)
                            if (File(path).exists()){
                                FileUploadManager(token).apply {
                                    startUpload(path,typeBean.name)
                                    setCallBack{
                                        cloudList.add(CloudListBean().apply {
                                            this.type=2
                                            subType=3
                                            subTypeStr=typeBean.course
                                            date=typeBean.date
                                            grade=typeBean.grade
                                            listJson=Gson().toJson(typeBean)
                                            contentJson= Gson().toJson(records)
                                            downloadUrl=it
                                        })
                                        startUpload(cloudList)
                                    }
                                }
                            }
                            else{
                                cloudList.add(CloudListBean().apply {
                                    this.type=2
                                    subType=3
                                    subTypeStr=typeBean.course
                                    date=typeBean.date
                                    grade=typeBean.grade
                                    listJson= Gson().toJson(typeBean)
                                    downloadUrl="null"
                                })
                                startUpload(cloudList)
                            }
                        }
                        4->{
                            val homeworkBook=HomeworkBookDaoManager.getInstance().queryBookByID(typeBean.bookId)
                            //判断题卷本是否已下载题卷书籍
                            if (homeworkBook==null)
                            {
                                cloudList.add(CloudListBean().apply {
                                    this.type=2
                                    subType=4
                                    subTypeStr=typeBean.course
                                    date=typeBean.date
                                    grade=typeBean.grade
                                    listJson=Gson().toJson(typeBean)
                                    downloadUrl="null"
                                    bookId=typeBean.bookId
                                })
                                startUpload(cloudList)
                            }
                            else{
                                //判读是否存在手写内容
                                if (File(homeworkBook.bookDrawPath).exists()){
                                    FileUploadManager(token).apply {
                                        startUpload(homeworkBook.bookDrawPath,File(homeworkBook.bookDrawPath).name)
                                        setCallBack{
                                            cloudList.add(CloudListBean().apply {
                                                this.type=2
                                                subType=4
                                                subTypeStr=typeBean.course
                                                date=typeBean.date
                                                grade=typeBean.grade
                                                listJson=Gson().toJson(typeBean)
                                                contentJson= Gson().toJson(homeworkBook)
                                                downloadUrl=it
                                                zipUrl=homeworkBook.bodyUrl
                                                bookId=typeBean.bookId
                                            })
                                            startUpload(cloudList)
                                        }
                                    }
                                }
                                else{
                                    cloudList.add(CloudListBean().apply {
                                        this.type=2
                                        subType=4
                                        subTypeStr=typeBean.course
                                        date=typeBean.date
                                        grade=typeBean.grade
                                        listJson=Gson().toJson(typeBean)
                                        contentJson= Gson().toJson(homeworkBook)
                                        downloadUrl="null"
                                        zipUrl=homeworkBook.bodyUrl
                                        bookId=typeBean.bookId
                                    })
                                    startUpload(cloudList)
                                }
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
    private fun startUpload(list:MutableList<CloudListBean>){
        if (list.size==HomeworkTypeDaoManager.getInstance().queryAllExcludeCloud().size)
            mCloudUploadPresenter.upload(list)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.CLASSGROUP_EVENT->{
                initTab()
            }
            Constants.HOMEWORK_BOOK_EVENT->{
                fetchData()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchHomeworkType()
    }

    override fun fetchData() {
        homeworkTypes.clear()
        val totalTypes=HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse)
        homeworkTypes=HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse,pageIndex,pageSize)
        setPageNumber(totalTypes.size)
        mAdapter?.setNewData(homeworkTypes)
        fetchMessage()
    }

    /**
     * 请求作业分类
     */
    private fun fetchHomeworkType(){
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
        val map=HashMap<String,Any>()
        val list= arrayListOf<HomeworkRequestArguments>()
        for (item in homeworkTypes) {
            if (item.createStatus==1&&item.state!=1) {
                list.add(HomeworkRequestArguments().apply {
                    size=10
                    grade=this@HomeworkFragment.grade
                    commonTypeId=item.typeId
                    id=item.typeId
                })
            }
        }
        map["studentDto"]=list
        mPresenter.getList(map)

        list.clear()
        for (item in homeworkTypes){
            if (item.createStatus==1){
                list.add(HomeworkRequestArguments().apply {
                    id=item.typeId
                    subType=item.state
                })
            }
        }

        val mapPaper = HashMap<String, Any>()
        mapPaper["commonDto"] = list
        mPresenter.getReelList(mapPaper)
    }


    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        homeworkTypes.clear()
        mAdapter?.notifyDataSetChanged()
        setClearHomework()
        setSystemControlClear()
    }

}