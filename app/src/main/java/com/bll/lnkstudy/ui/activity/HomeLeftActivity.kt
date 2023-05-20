package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.presenter.QiniuPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IDataUpdateView
import com.bll.lnkstudy.mvp.view.IContractView.IQiniuView
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.*
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

open class HomeLeftActivity : BaseAppCompatActivity(), IQiniuView, IDataUpdateView {

    private val mQiniuPresenter = QiniuPresenter(this)
    private val mDataUpdatePresenter = DataUpdatePresenter(this)
    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var mData = mutableListOf<MainList>()
    private var lastFragment: Fragment? = null

    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var paperFragment: PaperFragment? = null
    private var homeworkFragment: HomeworkFragment? = null
    private var noteFragment: NoteFragment? = null
    private var paintingFragment: PaintingFragment? = null
    private var teachFragment: TeachFragment? = null
    private var eventType = ""

    override fun onToken(token: String) {
        when (eventType) {
            Constants.AUTO_UPLOAD_EVENT -> {
                paintingFragment?.uploadPainting(token)
                bookcaseFragment?.upload(token)
                uploadDataUpdate(token)
            }
            Constants.ACTION_UPLOAD_1MONTH -> {
                noteFragment?.uploadNote(token, true)
            }
            Constants.ACTION_UPLOAD_9MONTH -> {
                noteFragment?.uploadNote(token, false)
                paintingFragment?.uploadLocalDrawing(token)
            }
            Constants.CONTROL_MESSAGE_EVENT -> {
                textbookFragment?.uploadTextBook(token)
            }
            Constants.CONTROL_CLEAR_EVENT -> {
                paperFragment?.uploadPaper(token)
                homeworkFragment?.upload(token)
            }
        }

    }

    //增量更新回调
    override fun onSuccess() {
    }

    override fun onList(list: MutableList<DataUpdateBean>) {
        download(list)
    }

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        mData = DataBeanManager.getIndexData()
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        //发送通知，全屏自动收屏到主页的另外一边
        EventBus.getDefault().post(EventBusData().apply {
            event = Constants.SCREEN_EVENT
            screen = getCurrentScreenPos()
        })

        mainFragment = MainFragment()
        bookcaseFragment = BookCaseFragment()
        textbookFragment = TextbookFragment()
        paperFragment = PaperFragment()
        homeworkFragment = HomeworkFragment()
        noteFragment = NoteFragment()
        paintingFragment = PaintingFragment()
        teachFragment = TeachFragment()

        switchFragment(lastFragment, mainFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, mData).apply {
            rv_list.layoutManager = LinearLayoutManager(this@HomeLeftActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(lastFragment, mainFragment)//首页
                    1 -> switchFragment(lastFragment, bookcaseFragment)//书架
                    2 -> switchFragment(lastFragment, textbookFragment)//课本
                    3 -> switchFragment(lastFragment, homeworkFragment)//作业
                    4 -> switchFragment(lastFragment, paperFragment)//考卷
                    5 -> switchFragment(lastFragment, noteFragment)//笔记
                    6 -> switchFragment(lastFragment, paintingFragment)//书画
                    7 -> switchFragment(lastFragment, teachFragment)//义教
                }
                lastPosition = position
            }
        }

        iv_user.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }

    }

    //跳转笔记
    fun goToNote() {
        mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
        mHomeAdapter?.updateItem(5, true)//更新新的位置
        switchFragment(lastFragment, noteFragment)
        lastPosition = 5
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when (msgFlag) {
            Constants.AUTO_UPLOAD_EVENT -> {
                eventType = Constants.AUTO_UPLOAD_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.AUTO_UPLOAD_1MONTH_EVENT -> {
                eventType = Constants.AUTO_UPLOAD_1MONTH_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.AUTO_UPLOAD_9MONTH_EVENT -> {
                eventType = Constants.AUTO_UPLOAD_9MONTH_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.CONTROL_MESSAGE_EVENT -> {
                eventType = Constants.CONTROL_MESSAGE_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.CONTROL_CLEAR_EVENT -> {
                eventType = Constants.CONTROL_CLEAR_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.USER_EVENT -> {
                mUser = SPUtil.getObj("user", User::class.java)
            }
            Constants.DATA_UPLOAD_EVENT -> {
                mDataUpdatePresenter.onList()
            }
        }
    }


    /**
     * 一键下载
     */
    private fun download(list: MutableList<DataUpdateBean>) {
        for (item in list) {
            if (item.downloadUrl.isNullOrEmpty()) {
                when (item.type) {
                    2 -> {
                        if (item.contentType==0){
                            val homeworkType=Gson().fromJson(item.listJson, HomeworkTypeBean::class.java)
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkType)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,homeworkType.typeId,0,homeworkType.typeId,item.state
                                ,Gson().toJson(item))
                        }
                        else{
                            //本次作业卷
                            val homeworkPaperBean=Gson().fromJson(item.listJson, HomeworkPaperBean::class.java)
                            HomeworkPaperDaoManager.getInstance().insertOrReplace(homeworkPaperBean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,homeworkPaperBean.contentId,1,homeworkPaperBean.typeId,1
                                ,Gson().toJson(homeworkPaperBean))
                        }
                    }
                    3 -> {
                        if(item.contentType==0){
                            val paperType=Gson().fromJson(item.listJson, PaperTypeBean::class.java)
                            PaperTypeDaoManager.getInstance().insertOrReplace(paperType)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(3,paperType.typeId,0,paperType.typeId,Gson().toJson(item))
                        }
                        else{
                            val paperBean=Gson().fromJson(item.listJson, PaperBean::class.java)
                            PaperDaoManager.getInstance().insertOrReplace(paperBean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(3,paperBean?.contentId!!,1,paperBean.typeId,Gson().toJson(paperBean))
                        }
                    }
                    4 -> {
                        if (item.contentType==0){
                            val noteTypeBean=Gson().fromJson(item.listJson, NoteTypeBean::class.java)
                            NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteTypeBean)
                            //创建笔记分类增量更新
                            DataUpdateManager.createDataUpdate(4,noteTypeBean.id.toInt(),0,1,Gson().toJson(noteTypeBean))
                        }
                        else{
                            val notebookBean=Gson().fromJson(item.listJson, NotebookBean::class.java)
                            val typeId=if (notebookBean.typeStr==getString(R.string.note_tab_diary)) 0 else 1
                            NotebookDaoManager.getInstance().insertOrReplace(notebookBean)
                            //新建笔记本增量更新
                            DataUpdateManager.createDataUpdate(4,notebookBean.id.toInt(),1,typeId,Gson().toJson(notebookBean))
                        }
                    }
                    5 -> {
                        if (item.contentType == 0) {
                            val paintingBean = Gson().fromJson(item.listJson, PaintingBean::class.java)
                            downloadPainting(paintingBean)
                        } else {
                            val typeBean = Gson().fromJson(item.listJson, PaintingTypeBean::class.java)
                            PaintingTypeDaoManager.getInstance().insertOrReplace(typeBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5, typeBean.id.toInt(), 1, 0, Gson().toJson(item))
                        }
                    }
                }
            } else {
                when (item.type) {
                    0 -> {
                        downloadBook(item)
                    }
                    1 -> {
                        downloadTextBook(item)
                    }
                    2 -> {
                        when(item.state){
                            1->{//作业卷内容
                                downloadHomeworkPaper(item)
                            }
                            2->{//作业本内容
                                downloadHomework(item)
                            }
                            3->{//朗读本内容
                                downloadHomeworkRecord(item)
                            }
                        }
                    }
                    3 -> {
                        downloadPaper(item)
                    }
                    4 -> {
                        downloadNote(item)
                    }
                    5 -> {
                        downloadPaintingLocal(item)
                    }
                }
            }
        }
    }

    /**
     * 下载笔记内容
     */
    private fun downloadBook(item: DataUpdateBean){
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                val bean = Gson().fromJson(item.listJson, BookBean::class.java)
                                BookGreenDaoManager.getInstance().insertOrReplaceBook(bean)
                                //创建增量更新
                                DataUpdateManager.createDataUpdateSource(0,bean.id.toInt(),0,bean.bookId
                                    ,Gson().toJson(bean),item.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载课本
     */
    private fun downloadTextBook(item: DataUpdateBean){
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                if(item.contentType==0){
                                    val bean = Gson().fromJson(item.listJson, BookBean::class.java)
                                    BookGreenDaoManager.getInstance().insertOrReplaceBook(bean)
                                    //创建增量更新
                                    DataUpdateManager.createDataUpdateSource(1,bean.id.toInt(),0,bean.bookId
                                        ,Gson().toJson(bean),item.path)
                                }
                                else{
                                    //创建增量更新
                                    DataUpdateManager.createDataUpdateSource(1,item.uid,1,item.typeId
                                        ,item.listJson,item.path)
                                }
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载作业卷内容
     */
    private fun downloadHomeworkPaper(item: DataUpdateBean){
        val bean = Gson().fromJson(item.listJson, HomeworkPaperContentBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                HomeworkPaperContentDaoManager.getInstance().insertOrReplace(bean)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,bean.contentId,2,bean.typeId,1
                                    ,Gson().toJson(bean),item.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }
    /**
     * 下载作业内容
     */
    private fun downloadHomework(item: DataUpdateBean){
        val bean = Gson().fromJson(item.listJson, HomeworkContentBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                HomeworkContentDaoManager.getInstance().insertOrReplace(bean)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,bean.id.toInt(),1,bean.homeworkTypeId,2
                                    ,Gson().toJson(bean),File(bean.path).parent)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }
    /**
     * 下载朗读作业内容
     */
    private fun downloadHomeworkRecord(item: DataUpdateBean){
        val bean = Gson().fromJson(item.listJson, RecordBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                RecordDaoManager.getInstance().insertOrReplace(bean)
                                //创建增量数据
                                DataUpdateManager.createDataUpdate(2,bean.id.toInt(),1,bean?.typeId!!,3
                                    ,Gson().toJson(bean),item.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载考卷内容
     */
    private fun downloadPaper(item: DataUpdateBean){
        val paperContent = Gson().fromJson(item.listJson, PaperContentBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                PaperContentDaoManager.getInstance().insertOrReplace(paperContent)
                                DataUpdateManager.createDataUpdate(3,paperContent.id.toInt(),2,
                                    paperContent.typeId,Gson().toJson(paperContent),paperContent.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载笔记内容
     */
    private fun downloadNote(item: DataUpdateBean){
        val contentBean = Gson().fromJson(item.listJson, NoteContentBean::class.java)
        val typeId=if (contentBean.typeStr==getString(R.string.note_tab_diary)) 0 else 1
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                NoteContentDaoManager.getInstance().insertOrReplaceNote(contentBean)
                                //创建本地画本增量更新
                                DataUpdateManager.createDataUpdate(4,contentBean.id.toInt(),2
                                    ,typeId,Gson().toJson(contentBean),item.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载线上书画
     */
    private fun downloadPainting(item: PaintingBean) {
        val pathStr = FileAddress().getPathImage("painting", item.contentId)
        val images = mutableListOf<String>()
        images.add(item.bodyUrl)
        val imageDownLoad = ImageDownLoadUtils(this, images.toTypedArray(), pathStr)
        imageDownLoad.startDownload()
        imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
            override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                PaintingBeanDaoManager.getInstance().insertOrReplace(item)
                //新建增量更新
                DataUpdateManager.createDataUpdateSource(
                    5,
                    item.id.toInt(),
                    0,
                    item.contentId,
                    Gson().toJson(item),
                    item.bodyUrl
                )
            }

            override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                imageDownLoad.reloadImage()
            }
        })
    }

    /**
     * 下载本地书画
     */
    private fun downloadPaintingLocal(item: DataUpdateBean) {
        val drawingBean = Gson().fromJson(item.listJson, PaintingDrawingBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                PaintingDrawingDaoManager.getInstance().insertOrReplace(drawingBean)
                                //创建本地画本增量更新
                                DataUpdateManager.createDataUpdate(5,drawingBean.id.toInt(),2,0
                                    , Gson().toJson(drawingBean),drawingBean.path)
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                            }
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }


    /**
     * 每天上传增量数据
     */
    private fun uploadDataUpdate(token: String) {
        val startTime = SPUtil.getInt("DataUpdateTime")
        val endTime = System.currentTimeMillis()
        val datas = DataUpdateDaoManager.getInstance().queryList(startTime.toLong(), endTime)
        for (item in datas) {
            val map = HashMap<String, Any>()
            map["type"] = item.type
            map["uid"] = item.uid
            map["contentType"] = item.contentType
            map["typeId"] = item.typeId
            map["listJson"] = item.listJson
            map["sourceUrl"] = item.sourceUrl
            map["isDelete"] = item.isDelete
            if (item.path.isNullOrEmpty()&&!File(item.path).exists()) {
                mDataUpdatePresenter.onAddData(map)
            } else {
                FileUploadManager(token).apply {
                    startUpload(item.path, item.id.toString())
                    setCallBack {
                        map["downloadUrl"] = it
                        mDataUpdatePresenter.onAddData(map)
                    }
                }
            }
            SPUtil.putInt("DataUpdateTime", endTime.toInt())
        }
    }

}