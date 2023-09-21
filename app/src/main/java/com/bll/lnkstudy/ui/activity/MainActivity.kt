package com.bll.lnkstudy.ui.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
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
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.*
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_launcher.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*

class MainActivity : BaseAppCompatActivity(), IContractView.IQiniuView, IContractView.IDataUpdateView {

    private val mQiniuPresenter = QiniuPresenter(this)
    private val mDataUpdatePresenter = DataUpdatePresenter(this)
    private var eventType = ""

    var mainLeftFragment: MainLeftFragment? = null
    var bookcaseFragment: BookCaseFragment? = null
    var textbookFragment: TextbookFragment? = null
    var teachFragment: TeachFragment? = null
    var classGroupFragment:ClassGroupFragment?=null

    var mainRightFragment: MainRightFragment? = null
    var paperFragment: PaperFragment? = null
    var homeworkFragment: HomeworkFragment? = null
    var noteFragment: NoteFragment? = null
    var paintingFragment: PaintingFragment? = null

    private var leftPosition = 0
    private var mAdapterLeft: MainListAdapter? = null
    private var leftFragment: Fragment? = null

    private var rightPosition = 0
    private var mAdapterRight: MainListAdapter? = null
    private var rightFragment: Fragment? = null

    override fun onToken(token: String) {
        when (eventType) {
            Constants.AUTO_UPLOAD_EVENT -> {
                paintingFragment?.uploadPainting()
                bookcaseFragment?.upload(token)
                uploadDataUpdate(token)
            }
            Constants.ACTION_UPLOAD_1MONTH -> {

            }
            Constants.ACTION_UPLOAD_9MONTH -> {
                noteFragment?.uploadNote(token)
                paintingFragment?.uploadLocalDrawing(token)
            }
            Constants.CONTROL_MESSAGE_EVENT -> {
                textbookFragment?.uploadTextBook(token)
            }
            Constants.CONTROL_CLEAR_EVENT -> {
                homeworkFragment?.upload(token)
                paperFragment?.uploadPaper(token)
            }
        }

    }

    //增量更新回调
    override fun onSuccess() {
        showLog("增量更新上传成功")
    }

    override fun onList(list: MutableList<DataUpdateBean>) {
        Collections.sort(list, Comparator { p0, p1 ->
            return@Comparator p0.type - p1.type
        })
        clearData()
        download(list)
    }

    override fun layoutId(): Int {
        return R.layout.ac_launcher
    }

    override fun initData() {
        val areaJson = FileUtils.readFileContent(resources.assets.open("city.json"))
        val type= object : TypeToken<List<Area>>() {}.type
        DataBeanManager.provinces = Gson().fromJson(areaJson, type)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        EasyPermissions.requestPermissions(
            this, "请求权限", 1,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECORD_AUDIO
        )

        mainLeftFragment = MainLeftFragment()
        bookcaseFragment = BookCaseFragment()
        textbookFragment = TextbookFragment()
        paperFragment = PaperFragment()
        homeworkFragment = HomeworkFragment()
        noteFragment = NoteFragment()
        paintingFragment = PaintingFragment()
        teachFragment = TeachFragment()
        mainRightFragment = MainRightFragment()
        classGroupFragment= ClassGroupFragment()

        switchLeftFragment(leftFragment, mainLeftFragment)
        switchRightFragment(rightFragment,mainRightFragment)

        mAdapterLeft = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataLeft()).apply {
            rv_list_a.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_a.adapter = this
            bindToRecyclerView(rv_list_a)
            setOnItemClickListener { adapter, view, position ->
                updateItem(leftPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchLeftFragment(leftFragment, mainLeftFragment)//首页
                    1 -> switchLeftFragment(leftFragment, bookcaseFragment)//书架
                    2 -> switchLeftFragment(leftFragment, textbookFragment)//课本
                    3 -> switchLeftFragment(leftFragment, classGroupFragment)//班群管理
                    4 -> switchLeftFragment(leftFragment, teachFragment)//义教
                }
                leftPosition = position
            }
        }

        mAdapterRight = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataRight()).apply {
            rv_list_b.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_b.adapter = this
            bindToRecyclerView(rv_list_b)
            setOnItemClickListener { adapter, view, position ->
                updateItem(rightPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchRightFragment(rightFragment, mainRightFragment)
                    1 -> switchRightFragment(rightFragment, homeworkFragment)
                    2 -> switchRightFragment(rightFragment, paperFragment)
                    3 -> switchRightFragment(rightFragment, noteFragment)
                    4 -> switchRightFragment(rightFragment, paintingFragment)
                }
                rightPosition = position
            }
        }

        startRemind()
        startRemind1Month()
        startRemind9Month()

        iv_user_a.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }
        iv_user_b.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }
    }


    //页码跳转
    private fun switchLeftFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            leftFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout_a, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    //页码跳转
    private fun switchRightFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            rightFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout_b, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }


    /**
     * 开始每天定时任务 下午三点
     */
    private fun startRemind() {

         Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis

            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
             alarmManager.setRepeating(
                 AlarmManager.RTC_WAKEUP, selectLong,
                 AlarmManager.INTERVAL_DAY, pendingIntent
             )
        }


    }

    /**
     * 每年9月1 3点执行
     */
    private fun startRemind9Month() {
        val date=365*24*60*60*1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH,8)
            set(Calendar.DAY_OF_MONTH,1)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis()>selectLong){
                set(Calendar.YEAR,DateUtils.getYear()+1)
                selectLong=timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD_9MONTH
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 每年1月1 3点执行
     */
    private fun startRemind1Month() {
        val date=365*24*60*60*1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH,0)
            set(Calendar.DAY_OF_MONTH,1)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis()>selectLong){
                set(Calendar.YEAR,DateUtils.getYear()+1)
                selectLong=timeInMillis
            }

            val intent = Intent(this@MainActivity, AlarmService::class.java)
            intent.action = Constants.ACTION_UPLOAD_1MONTH
            val pendingIntent = PendingIntent.getService(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 一键清除
     */
    private fun clearData(){
        ActivityManager.getInstance().finishOthers(MainActivity::class.java)

        MyApplication.mDaoSession?.clear()
        DataUpdateDaoManager.getInstance().clear()
        FreeNoteDaoManager.getInstance().clear()
        DiaryDaoManager.getInstance().clear()
        BookGreenDaoManager.getInstance().clear()

        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有录音
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        HomeworkPaperContentDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()

        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()
        PaperContentDaoManager.getInstance().clear()

        NotebookDaoManager.getInstance().clear()
        NoteDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()

        PaintingTypeDaoManager.getInstance().clear()
        PaintingDrawingDaoManager.getInstance().clear()
        PaintingBeanDaoManager.getInstance().clear()

        DateEventGreenDaoManager.getInstance().clear()
        CourseGreenDaoManager.getInstance().clear()
        AppDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.ZIP_PATH).parentFile)
        EventBus.getDefault().post(Constants.BOOK_EVENT)
        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
        EventBus.getDefault().post(Constants.NOTE_EVENT)
        EventBus.getDefault().post(Constants.RECORD_EVENT)
        EventBus.getDefault().post(Constants.PASSWORD_EVENT)
        EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
    }

    /**
     * 一键下载
     */
    private fun download(list: MutableList<DataUpdateBean>) {
        for (item in list) {
            when(item.type){
                1->{
                    downloadTextBook(item)
                }
                2->{
                    when (item.contentType) {
                        1 -> {
                            val homeworkType= Gson().fromJson(item.listJson, HomeworkTypeBean::class.java)
                            HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkType)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,homeworkType.typeId,1,homeworkType.typeId,item.state,
                                Gson().toJson(homeworkType))
                        }
                        2 -> {
                            when(item.state){
                                1->{
                                    //本次作业卷
                                    val homeworkPaperBean=
                                        Gson().fromJson(item.listJson, HomeworkPaperBean::class.java)
                                    HomeworkPaperDaoManager.getInstance().insertOrReplace(homeworkPaperBean)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(2,homeworkPaperBean.contentId,2,homeworkPaperBean.typeId,1,
                                        Gson().toJson(homeworkPaperBean))
                                }
                                2->{//作业本内容
                                    downloadHomework(item)
                                }
                                3->{//朗读本内容
                                    downloadHomeworkRecord(item)
                                }
                            }
                        }
                        else -> {
                            //作业卷内容
                            downloadHomeworkPaper(item)
                        }
                    }

                }
                3->{
                    when (item.contentType) {
                        1 -> {
                            val paperType= Gson().fromJson(item.listJson, PaperTypeBean::class.java)
                            PaperTypeDaoManager.getInstance().insertOrReplace(paperType)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(3,paperType.typeId,1,paperType.typeId,
                                Gson().toJson(item))
                        }
                        2 -> {
                            val paperBean= Gson().fromJson(item.listJson, PaperBean::class.java)
                            PaperDaoManager.getInstance().insertOrReplace(paperBean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(3,paperBean?.contentId!!,2,paperBean.typeId,
                                Gson().toJson(paperBean))
                        }
                        else -> {
                            downloadPaper(item)
                        }
                    }
                }
                4->{
                    when(item.contentType){
                        1->{
                            val notebook= Gson().fromJson(item.listJson, Notebook::class.java)
                            NotebookDaoManager.getInstance().insertOrReplace(notebook)
                            //创建笔记分类增量更新
                            DataUpdateManager.createDataUpdate(4,notebook.id.toInt(),1,2,
                                Gson().toJson(notebook))
                        }
                        2->{
                            val note= Gson().fromJson(item.listJson, Note::class.java)
                            val typeId=if (note.typeStr==getString(R.string.note_tab_diary)) 1 else 2
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            //新建笔记本增量更新
                            DataUpdateManager.createDataUpdate(4,note.id.toInt(),2,typeId,
                                Gson().toJson(note))
                        }
                        3->{
                            downloadNote(item)
                        }
                    }
                }
                5->{
                    when(item.contentType){
                        1->{
                            val typeBean = Gson().fromJson(item.listJson, PaintingTypeBean::class.java)
                            PaintingTypeDaoManager.getInstance().insertOrReplace(typeBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5, typeBean.id.toInt(), 1, 1, Gson().toJson(item))
                        }
                        2->{
                            downloadPaintingLocal(item)
                        }
                    }
                }
                6->{
                    if (item.contentType==1){
                        downloadBook(item)
                    }
                    else{
                        downloadBookDraw(item)
                    }
                }
                7->{
                    val paintingBean = Gson().fromJson(item.listJson, PaintingBean::class.java)
                    downloadPainting(paintingBean)
                }
                8->{
                    downloadHomeworkBook(item)
                }
                9->{
                    downloadDiary(item)
                }
                10->{
                    val checkPassword= Gson().fromJson(item.listJson, CheckPassword::class.java)
                    SPUtil.putObj("${mUser?.accountId}notePassword",checkPassword)
                    //创建增量数据(日记密码)
                    DataUpdateManager.createDataUpdate(10,1,1,1, Gson().toJson(checkPassword))
                }
            }
        }
    }

    /**
     * 下载书籍(书籍item.path为null)
     */
    private fun downloadBook(item: DataUpdateBean){
        val bean = Gson().fromJson(item.listJson, BookBean::class.java)
        val formatStr=item.downloadUrl.substring(item.downloadUrl.lastIndexOf("."))
        val fileName = MD5Utils.digest(item.uid.toString())+formatStr//文件名
        val targetFileStr = FileAddress().getPathBook(fileName)

        FileDownManager.with(this).create(item.downloadUrl).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(bean)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(6,bean.bookId,1,bean.bookId
                        , Gson().toJson(bean), bean.downloadUrl)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 书籍手写
     */
    private fun downloadBookDraw(item: DataUpdateBean){
        val fileName=item.uid.toString()
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val path=item.path
                    ZipUtils.unzip(zipPath, path, object : IZipCallback {
                        override fun onFinish() {
                            //创建增量更新
                            DataUpdateManager.createDataUpdate(6,item.uid,2,item.uid
                                ,"",item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
     * 下载课本(课本item.path为null，手写item.path不为null)
     */
    private fun downloadTextBook(item: DataUpdateBean){
        val fileName=item.uid.toString()
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val path=if (item.contentType==1)FileAddress().getPathTextBook(fileName) else item.path
                    ZipUtils.unzip(zipPath, path, object : IZipCallback {
                        override fun onFinish() {
                            if(item.contentType==1){
                                val bean = Gson().fromJson(item.listJson, BookBean::class.java)
                                BookGreenDaoManager.getInstance().insertOrReplaceBook(bean)
                                //创建增量更新
                                DataUpdateManager.createDataUpdateSource(1,bean.bookId,1,bean.bookId
                                    , Gson().toJson(bean),bean.downloadUrl)
                            }
                            else{
                                //创建增量更新
                                DataUpdateManager.createDataUpdate(1,item.uid,2,item.typeId
                                    ,"",item.path)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
     * 下载题卷本
     */
    private fun downloadHomeworkBook(item: DataUpdateBean){
        val fileName=item.uid.toString()
        val zipPath = FileAddress().getPathZip(fileName)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val path=if (item.contentType==1)FileAddress().getPathHomeworkBook(fileName) else item.path
                    ZipUtils.unzip(zipPath, path, object : IZipCallback {
                        override fun onFinish() {
                            if(item.contentType==1){
                                val bean = Gson().fromJson(item.listJson, HomeworkBookBean::class.java)
                                HomeworkBookDaoManager.getInstance().insertOrReplaceBook(bean)
                                //题卷本不存在，创建题卷本
                                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkTypeBook(bean.bookId)){
                                    val homeworkTypeBean=HomeworkTypeBean().apply {
                                        name=bean.bookName
                                        grade=bean.grade
                                        typeId=ToolUtils.getDateId()
                                        state=4
                                        date=System.currentTimeMillis()
                                        course=DataBeanManager.getCourseStr(bean.subject)
                                        bookId=bean.bookId
                                        bgResId=DataBeanManager.getHomeworkCoverStr()
                                        createStatus=0
                                    }
                                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkTypeBean)
                                }

                                //创建增量更新
                                DataUpdateManager.createDataUpdateSource(8,bean.bookId,1,bean.bookId
                                    , Gson().toJson(bean),bean.bodyUrl)
                            }
                            else{
                                //创建增量更新
                                DataUpdateManager.createDataUpdate(8,item.uid,2,item.uid
                                    ,"",item.path)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        if (item.downloadUrl.isNullOrEmpty()){
            HomeworkPaperContentDaoManager.getInstance().insertOrReplace(bean)
            //创建增量数据
            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),3,bean.typeId,1
                , Gson().toJson(bean),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            HomeworkPaperContentDaoManager.getInstance().insertOrReplace(bean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),3,bean.typeId,1
                                , Gson().toJson(bean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        if (item.downloadUrl.isNullOrEmpty()){
            HomeworkContentDaoManager.getInstance().insertOrReplace(bean)
            //创建增量数据
            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),2,bean.homeworkTypeId,2
                , Gson().toJson(bean),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            HomeworkContentDaoManager.getInstance().insertOrReplace(bean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),2,bean.homeworkTypeId,2
                                ,
                                Gson().toJson(bean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        if (item.downloadUrl.isNullOrEmpty()){
            RecordDaoManager.getInstance().insertOrReplace(bean)
            //创建增量数据
            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),2,bean?.typeId!!,3
                , Gson().toJson(bean),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            RecordDaoManager.getInstance().insertOrReplace(bean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdate(2,bean.id.toInt(),2,bean?.typeId!!
                                ,3, Gson().toJson(bean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        if (item.downloadUrl.isNullOrEmpty()){
            PaperContentDaoManager.getInstance().insertOrReplace(paperContent)
            DataUpdateManager.createDataUpdate(3,paperContent.id.toInt(),3,
                paperContent.typeId, Gson().toJson(paperContent),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            PaperContentDaoManager.getInstance().insertOrReplace(paperContent)
                            DataUpdateManager.createDataUpdate(3,paperContent.id.toInt(),3,
                                paperContent.typeId, Gson().toJson(paperContent),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        val typeId=if (contentBean.typeStr==getString(R.string.note_tab_diary)) 1 else 2
        if (item.downloadUrl.isNullOrEmpty()){
            NoteContentDaoManager.getInstance().insertOrReplaceNote(contentBean)
            //创建本地画本增量更新
            DataUpdateManager.createDataUpdate(4,contentBean.id.toInt(),3
                ,typeId, Gson().toJson(contentBean),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            NoteContentDaoManager.getInstance().insertOrReplaceNote(contentBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(4,contentBean.id.toInt(),3
                                ,typeId, Gson().toJson(contentBean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        val pathStr =if (item.type==1){
            FileAddress().getPathImage("wallpaper",item.contentId)
        }
        else{
            FileAddress().getPathImage("painting", item.contentId)
        }
        val images = mutableListOf(item.bodyUrl)
        val savePaths= arrayListOf("$pathStr/1.png")
        FileMultitaskDownManager.with(this).create(images).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    PaintingBeanDaoManager.getInstance().insertOrReplace(item)
                    //新建增量更新
                    DataUpdateManager.createDataUpdateSource(
                        7,
                        item.id.toInt(),
                        1,
                        item.contentId,
                        Gson().toJson(item),
                        item.bodyUrl
                    )
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载本地书画
     */
    private fun downloadPaintingLocal(item: DataUpdateBean) {
        val drawingBean = Gson().fromJson(item.listJson, PaintingDrawingBean::class.java)
        if (item.downloadUrl.isNullOrEmpty()){
            PaintingDrawingDaoManager.getInstance().insertOrReplace(drawingBean)
            //创建本地画本增量更新
            DataUpdateManager.createDataUpdate(5,drawingBean.id.toInt(),2,1
                , Gson().toJson(drawingBean),item.path)
            return
        }
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            PaintingDrawingDaoManager.getInstance().insertOrReplace(drawingBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5,drawingBean.id.toInt(),2,1
                                , Gson().toJson(drawingBean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
     * 下载日记
     */
    private fun downloadDiary(item: DataUpdateBean){
        val contentBean = Gson().fromJson(item.listJson, DiaryBean::class.java)
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, item.path, object : IZipCallback {
                        override fun onFinish() {
                            DiaryDaoManager.getInstance().insertOrReplace(contentBean)
                            //创建增量更新
                            DataUpdateManager.createDataUpdate(9, item.uid,1,item.typeId,
                                Gson().toJson(contentBean),item.path)
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
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
        val items = DataUpdateDaoManager.getInstance().queryList(startTime.toLong(), endTime)
        for (item in items) {
            if (item.isDelete){
                val map = HashMap<String, Any>()
                map["type"] = item.type
                map["uid"] = item.uid
                map["contentType"] = item.contentType
                map["typeId"] = item.typeId
                mDataUpdatePresenter.onDeleteData(map)
                //上传删除本地增量更新
                DataUpdateDaoManager.getInstance().deleteBean(item.type,item.contentType,item.uid,item.typeId)
                continue
            }
            val map = HashMap<String, Any>()
            map["type"] = item.type
            map["uid"] = item.uid
            map["contentType"] = item.contentType
            map["typeId"] = item.typeId
            map["listJson"] = item.listJson
            if (!item.downloadUrl.isNullOrEmpty())
                map["downloadUrl"] = item.downloadUrl
            map["state"]=item.state
            if (item.path.isNullOrEmpty()||!File(item.path).exists()) {
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
        }
        SPUtil.putInt("DataUpdateTime", endTime.toInt())
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when (msgFlag) {
            Constants.AUTO_UPLOAD_EVENT -> {
                mainRightFragment?.deleteDiary()
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
            Constants.DATA_DOWNLOAD_EVENT -> {
                mDataUpdatePresenter.onList()
            }
            Constants.DATA_RENT_EVENT -> {
                val map=HashMap<String,Any>()
                map["type"]= arrayOf(1,2,3,8)
                mDataUpdatePresenter.onList()
            }
            Constants.DATA_CLEAT_EVENT -> {
                clearData()
            }
        }
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}