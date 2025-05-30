package com.bll.lnkstudy.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.model.note.NoteContentBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.presenter.QiniuPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.*
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.utils.date.Lunar
import com.bll.lnkstudy.utils.date.LunarSolarConverter
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_main.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class MainActivity : BaseAppCompatActivity(), IContractView.IQiniuView, IContractView.IDataUpdateView {

    private val mQiniuPresenter = QiniuPresenter(this)
    private val mDataUpdatePresenter = DataUpdatePresenter(this)
    private var eventType = ""

    var mainLeftFragment: MainLeftFragment? = null
    var bookcaseFragment: BookcaseFragment? = null
    var textbookFragment: TextbookFragment? = null
    var teachFragment: TeachFragment? = null
    var learningConditionFragment: LearningConditionFragment? = null

    var mainRightFragment: MainRightFragment? = null
    var paperFragment: TestPaperManageFragment? = null
    var homeworkFragment: HomeworkManageFragment? = null
    var noteFragment: NoteFragment? = null
    var paintingFragment: PaintingFragment? = null

    private var leftPosition = 0
    private var mAdapterLeft: MainListAdapter? = null
    private var leftFragment: Fragment? = null

    private var rightPosition = 0
    private var mAdapterRight: MainListAdapter? = null
    private var rightFragment: Fragment? = null

    private val myBroadcastReceiver=MyBroadcastReceiver()

    override fun onToken(token: String) {
        when (eventType) {
            Constants.AUTO_UPLOAD_EVENT -> {
                bookcaseFragment?.upload(token)
                uploadDataUpdate(token)
            }
            Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT -> {
                textbookFragment?.uploadTextBook(token)
            }
            Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT -> {
                textbookFragment?.uploadTextBook(token)
            }
            Constants.AUTO_UPLOAD_YEAR_EVENT->{
                mainRightFragment?.uploadDiary(token,true)
            }
            Constants.DIARY_UPLOAD_EVENT->{
                mainRightFragment?.uploadDiary(token,false)
            }
            Constants.USER_CHANGE_GRADE_EVENT->{
                if (grade>0){
                    homeworkFragment?.upload(token)
                    paperFragment?.uploadPaper(token)
                }
            }
        }

    }

    //增量更新回调
    override fun onSuccess() {
    }

    override fun onList(list: MutableList<DataUpdateBean>) {
        Collections.sort(list, Comparator { p0, p1 ->
            return@Comparator p0.type - p1.type
        })
        clearDataUpdate()
        download(list)
    }

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        initStartDate()
    }

    override fun initView() {

        val intentFilter=IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(myBroadcastReceiver,intentFilter)

        val isTips=SPUtil.getBoolean("SpecificationTips")
        if (!isTips){
            showView(ll_tips)
        }

        mainLeftFragment = MainLeftFragment()
        bookcaseFragment = BookcaseFragment()
        textbookFragment = TextbookFragment()
        paperFragment = TestPaperManageFragment()
        homeworkFragment = HomeworkManageFragment()
        noteFragment = NoteFragment()
        paintingFragment = PaintingFragment()
        teachFragment = TeachFragment()
        mainRightFragment = MainRightFragment()
        learningConditionFragment = LearningConditionFragment()

        switchFragment(1, mainLeftFragment)
        switchFragment(2, mainRightFragment)

        mAdapterLeft = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataLeft()).apply {
            rv_list_a.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_a.adapter = this
            bindToRecyclerView(rv_list_a)
            setOnItemClickListener { adapter, view, position ->
                updateItem(leftPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(1,mainLeftFragment)//首页
                    1 -> switchFragment(1,bookcaseFragment)//书架
                    2 -> switchFragment(1,textbookFragment)//课本
                    3 -> switchFragment(1,teachFragment)//义教
                    4 -> switchFragment(1,learningConditionFragment)//应用
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
                    0 -> switchFragment(2,  mainRightFragment)
                    1 -> switchFragment(2,  homeworkFragment)
                    2 -> switchFragment(2,  paperFragment)
                    3 -> switchFragment(2,  noteFragment)
                    4 -> switchFragment(2,  paintingFragment)
                }
                rightPosition = position
            }
        }

        startRemind()
        startRemind15()
        startRemind1Month()
        startRemind9Month()
        startRemind1Year()

        iv_user_a.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }
        iv_classgroup.setOnClickListener {
            customStartActivity(Intent(this, ClassGroupActivity::class.java))
        }

        ll_tips.setOnClickListener {
            disMissView(ll_tips)
            SPUtil.putBoolean("SpecificationTips",true)
        }
    }

    private fun switchFragment(type: Int, to: Fragment?) {
        val from = if (type == 1) {
            leftFragment
        } else {
            rightFragment
        }
        if (from != to) {
            if (type == 1) {
                leftFragment = to
            } else {
                rightFragment = to
            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(if (type == 1) R.id.frame_layout_a else R.id.frame_layout_b, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    /**
     * 初始化数据
     */
    private fun initStartDate(){
        if (ItemTypeDaoManager.getInstance().queryAll(5).size==0){
            val strings = DataBeanManager.bookType
            for (i in strings.indices) {
                val item = ItemTypeBean()
                item.type=5
                item.title = strings[i]
                item.date=System.currentTimeMillis()
                ItemTypeDaoManager.getInstance().insertOrReplace(item)
            }
        }

        //创建截屏默认文件夹
        val path = FileAddress().getPathScreen("未分类")
        if (!File(path).exists()) {
            FileUtils.mkdirs(path)
        }
    }

    /**
     * 开始每天定时自动刷新
     */
    private fun startRemind() {

        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis

            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_DAY_REFRESH
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }
    }

    /**
     * 开始每天定时任务 下午三点
     */
    private fun startRemind15() {
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

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_15
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }
    }

    /**
     * 每年8月31 13点执行
     */
    private fun startRemind9Month() {
        val allDay = if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
        val date = allDay * 24 * 60 * 60 * 1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH, 7)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 13)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis() > selectLong) {
                set(Calendar.YEAR, DateUtils.getYear() + 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_LAST_SEMESTER
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 每年1月初10 13点执行
     */
    private fun startRemind1Month() {
        val allDay = if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
        val date = allDay * 24 * 60 * 60 * 1000L
        //农历转阳历
        val lunar = Lunar()
        lunar.isleap = DateUtils.isleap()
        lunar.lunarYear = DateUtils.getYear()
        lunar.lunarMonth = 1
        lunar.lunarDay = 10
        val solar = LunarSolarConverter.LunarToSolar(lunar)

        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH, solar.solarMonth-1)
            set(Calendar.DAY_OF_MONTH, solar.solarDay)
            set(Calendar.HOUR_OF_DAY, 13)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis() > selectLong) {
                set(Calendar.YEAR, DateUtils.getYear() + 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_NEXT_SEMESTER
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 每年1月1 9点执行
     */
    private fun startRemind1Year() {
        val allDay = if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
        val date = allDay * 24 * 60 * 60 * 1000L

        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis() > selectLong) {
                set(Calendar.YEAR, DateUtils.getYear() + 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_YEAR
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }

    }

    /**
     * 一键清除
     */
    private fun clearData() {
        SPUtil.removeObj(Constants.SP_PRIVACY_PW_DIARY)
        SPUtil.removeObj(Constants.SP_PRIVACY_PW_NOTE)
        SPUtil.removeObj(Constants.SP_SCHOOL_PERMISSION)
        SPUtil.removeObj(Constants.SP_PARENT_PERMISSION)
        SPUtil.putListInt(Constants.SP_WEEK_DATE_LIST, mutableListOf())
        SPUtil.putListLong(Constants.SP_DATE_LIST, mutableListOf())
        SPUtil.putString(Constants.SP_DIARY_BG_SET,"")
        SPUtil.putString(Constants.SP_COURSE_URL,"")
        SPUtil.putBoolean(Constants.SP_EXAM_MODE,false)

        MyApplication.mDaoSession?.clear()
        DataUpdateDaoManager.getInstance().clear()
        FreeNoteDaoManager.getInstance().clear()
        DiaryDaoManager.getInstance().clear()
        BookGreenDaoManager.getInstance().clear()
        TextbookGreenDaoManager.getInstance().clear()

        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有录音
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()
        HomeworkBookCorrectDaoManager.getInstance().clear()

        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()

        NoteDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()

        PaintingDrawingDaoManager.getInstance().clear()
        PaintingBeanDaoManager.getInstance().clear()

        DateEventGreenDaoManager.getInstance().clear()
        AppDaoManager.getInstance().clear()

        ItemTypeDaoManager.getInstance().clear()
        CalenderDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.BOOK_PATH))
        FileUtils.deleteFile(File(Constants.SCREEN_PATH))
        FileUtils.deleteFile(File(Constants.ZIP_PATH).parentFile)

        Glide.get(this).clearMemory()
        Thread{
            Glide.get(this).clearDiskCache()
        }.start()
        MethodManager.logout(this)
    }

    /**
     * 清除增量更新
     */
    private fun clearDataUpdate(){
        DataUpdateDaoManager.getInstance().clear()

        BookGreenDaoManager.getInstance().clear()
        TextbookGreenDaoManager.getInstance().clear()

        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有录音
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()
        HomeworkBookCorrectDaoManager.getInstance().clear()

        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()

        NoteDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()

        PaintingDrawingDaoManager.getInstance().clear()

        //删除笔记分类
        ItemTypeDaoManager.getInstance().clear(2)
        //删除画本分类
        ItemTypeDaoManager.getInstance().clear(3)
        //删除书法分类
        ItemTypeDaoManager.getInstance().clear(4)

        FileUtils.deleteFile(File(Constants.BOOK_PATH))
        FileUtils.deleteFile(File(Constants.TESTPAPER_PATH))
        FileUtils.deleteFile(File(Constants.HOMEWORK_PATH))
        FileUtils.deleteFile(File(Constants.NOTE_PATH))
        FileUtils.deleteFile(File(Constants.PAINTING_PATH))
    }

    /**
     * 一键下载
     */
    private fun download(list: MutableList<DataUpdateBean>) {
        for (item in list) {
            when (item.type) {
                1 -> {
                    if (item.contentType==1){
                        downloadTextBook(item)
                    }
                    else{
                        downloadTextBookDrawing(item)
                    }
                }
                2 -> {
                    when (item.contentType) {
                        1 -> {
                            val homeworkType = Gson().fromJson(item.listJson, HomeworkTypeBean::class.java)
                            if (homeworkType.autoState==1){
                                val localItem = HomeworkTypeDaoManager.getInstance().queryByAutoName(homeworkType.name, homeworkType.course, homeworkType.grade)
                                if (localItem==null){
                                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkType)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(2, homeworkType.typeId, 1,item.listJson)
                                    DataUpdateManager.editDataUpdateUpload(2,homeworkType.typeId,1)
                                }
                            }
                            else{
                                if (!HomeworkTypeDaoManager.getInstance().isExistHomeworkType(homeworkType.typeId)){
                                    HomeworkTypeDaoManager.getInstance().insertOrReplace(homeworkType)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(2, homeworkType.typeId, 1,item.listJson)
                                    DataUpdateManager.editDataUpdateUpload(2,homeworkType.typeId,1)
                                }
                            }
                        }
                        2 -> {
                            when (item.state) {
                                1,7 -> {
                                    downloadHomeworkPaper(item)
                                }
                                2,6 -> {//作业本内容
                                    downloadHomework(item)
                                }
                                3 -> {//朗读本内容
                                    downloadHomeworkRecord(item)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    when (item.contentType) {
                        1 -> {
                            val paperType = Gson().fromJson(item.listJson, PaperTypeBean::class.java)
                            if (paperType.autoState==1){
                                val localItem= PaperTypeDaoManager.getInstance().queryByName(paperType.name,paperType.course,paperType.grade)
                                if (localItem==null){
                                    PaperTypeDaoManager.getInstance().insertOrReplace(paperType)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(3, paperType.typeId, 1, item.listJson)
                                    DataUpdateManager.editDataUpdateUpload(3,paperType.typeId,1)
                                }
                            }
                            else{
                                if (!PaperTypeDaoManager.getInstance().isExistPaperType(item.typeId)) {
                                    PaperTypeDaoManager.getInstance().insertOrReplace(paperType)
                                    //创建增量数据
                                    DataUpdateManager.createDataUpdate(3, paperType.typeId, 1, item.listJson)
                                    DataUpdateManager.editDataUpdateUpload(3,paperType.typeId,1)
                                }
                            }
                        }
                        2 -> {
                            downloadPaper(item)
                        }
                    }
                }
                4 -> {
                    when (item.contentType) {
                        1 -> {
                            val itemTypeBean = Gson().fromJson(item.listJson, ItemTypeBean::class.java)
                            ItemTypeDaoManager.getInstance().insertOrReplace(itemTypeBean)
                            EventBus.getDefault().post(Constants.NOTE_TAB_MANAGER_EVENT)
                            //创建笔记分类增量更新
                            DataUpdateManager.createDataUpdate(4,item.uid,1,item.listJson)
                            DataUpdateManager.editDataUpdateUpload(4,item.uid,1)
                        }
                        2 -> {
                            val note = Gson().fromJson(item.listJson, Note::class.java)
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            EventBus.getDefault().post(Constants.NOTE_EVENT)
                            //新建笔记本增量更新
                            DataUpdateManager.createDataUpdate(4,item.uid,2,item.listJson)
                            DataUpdateManager.editDataUpdateUpload(4,item.uid,2)
                        }
                        3 -> {
                            downloadNote(item)
                        }
                    }
                }
                5 -> {
                    when (item.contentType) {
                        1 -> {
                            val typeBean = Gson().fromJson(item.listJson, ItemTypeBean::class.java)
                            ItemTypeDaoManager.getInstance().insertOrReplace(typeBean)
                            //创建本地画本增量更新
                            DataUpdateManager.createDataUpdate(5,item.uid,1, item.listJson)
                            DataUpdateManager.editDataUpdateUpload(5,item.uid,1)
                        }
                        2 -> {
                            downloadPaintingLocal(item)
                        }
                    }
                }
                6 -> {
                    if (item.contentType == 1) {
                        downloadBook(item)
                    } else {
                        downloadBookDraw(item)
                    }
                }
                7 -> {
                    downloadHomeworkBookDrawing(item)
                }
            }
        }
    }

    /**
     * 下载书籍(书籍item.path为null)
     */
    private fun downloadBook(item: DataUpdateBean) {
        val bookBean = Gson().fromJson(item.listJson, BookBean::class.java)
        FileDownManager.with(this).create(item.downloadUrl).setPath(bookBean.bookPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean)
                    //创建增量更新
                    DataUpdateManager.createDataUpdateSource(6,item.uid,1, item.listJson,item.downloadUrl)
                    DataUpdateManager.editDataUpdateUpload(6,item.uid,1)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 书籍手写
     */
    private fun downloadBookDraw(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
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
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            //创建增量更新
                            DataUpdateManager.createDataUpdateDrawing(6,item.uid,2,item.downloadUrl)
                            DataUpdateManager.editDataUpdateUpload(6,item.uid,2)
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
    private fun downloadTextBook(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
        val bookBean = Gson().fromJson(item.listJson, TextbookBean::class.java)
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, bookBean.bookPath, object : IZipCallback {
                        override fun onFinish() {
                            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean)
                            //创建增量更新
                            DataUpdateManager.createDataUpdateSource(1,item.uid,1,Gson().toJson(bookBean),bookBean.downloadUrl)
                            //更改为已上传
                            DataUpdateManager.editDataUpdateUpload(1,item.uid, item.contentType)
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
     * 下载课本手写
     */
    private fun downloadTextBookDrawing(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
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
                            //创建增量更新
                            DataUpdateManager.createDataUpdateDrawing(1,item.uid,2,item.path)
                            //更改为已上传
                            DataUpdateManager.editDataUpdateUpload(1,item.uid, item.contentType)
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
    private fun downloadHomeworkBookDrawing(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
        FileDownManager.with(this).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath,  item.path , object : IZipCallback {
                        override fun onFinish() {
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            val correctBean = Gson().fromJson(item.listJson, HomeworkBookCorrectBean::class.java)
                            HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(correctBean)

                            DataUpdateManager.createDataUpdate(7,item.uid,1,item.typeId,item.listJson,item.path)
                            DataUpdateManager.editDataUpdateUpload(7,item.uid,1,item.typeId)
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
    private fun downloadHomeworkPaper(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
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
                            //本次作业卷
                            val homeworkPaperBean = Gson().fromJson(item.listJson, HomeworkPaperBean::class.java)
                            HomeworkPaperDaoManager.getInstance().insertOrReplace(homeworkPaperBean)
                            //创建增量数据
                            DataUpdateManager.createDataUpdateState(2, item.uid, 2,item.typeId,item.state,item.listJson,item.path)
                            DataUpdateManager.editDataUpdateUpload(2,item.uid,2,item.typeId)
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
    private fun downloadHomework(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
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
                            val bean = Gson().fromJson(item.listJson, HomeworkContentBean::class.java)
                            HomeworkContentDaoManager.getInstance().insertOrReplace(bean)
                            DataUpdateManager.createDataUpdateState(2, item.uid, 2,item.typeId ,item.state, item.listJson, item.path)
                            DataUpdateManager.editDataUpdateUpload(2,item.uid,2,item.typeId)
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
    private fun downloadHomeworkRecord(item: DataUpdateBean) {
        FileDownManager.with(this).create(item.downloadUrl).setPath(item.path)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val bean = Gson().fromJson(item.listJson, RecordBean::class.java)
                    RecordDaoManager.getInstance().insertOrReplace(bean)
                    //创建增量数据
                    DataUpdateManager.createDataUpdateState(2, item.uid, 2,item.typeId ,item.state, item.listJson, item.path)
                    DataUpdateManager.editDataUpdateUpload(2,item.uid,2,item.typeId)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    /**
     * 下载考卷内容
     */
    private fun downloadPaper(item: DataUpdateBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
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
                            val paperBean = Gson().fromJson(item.listJson, PaperBean::class.java)
                            PaperDaoManager.getInstance().insertOrReplace(paperBean)
                            DataUpdateManager.createDataUpdate(3,item.uid,2,item.typeId,item.listJson,paperBean.filePath)
                            DataUpdateManager.editDataUpdateUpload(3,item.uid,2,item.typeId)
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
    private fun downloadNote(item: DataUpdateBean) {
        FileDownManager.with(this).create(item.downloadUrl).setPath(item.path)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val contentBean = Gson().fromJson(item.listJson, NoteContentBean::class.java)
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(contentBean)
                    //新建笔记本增量更新
                    DataUpdateManager.createDataUpdate(4,item.uid,3,item.listJson,item.path)
                    DataUpdateManager.editDataUpdateUpload(4,item.uid,3)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }


    /**
     * 下载本地书画
     */
    private fun downloadPaintingLocal(item: DataUpdateBean) {
        FileDownManager.with(this).create(item.downloadUrl).setPath(item.path)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val drawingBean = Gson().fromJson(item.listJson, PaintingDrawingBean::class.java)
                    PaintingDrawingDaoManager.getInstance().insertOrReplace(drawingBean)
                    //创建本地画本增量更新
                    DataUpdateManager.createDataUpdate(5,item.uid,2,item.typeId, item.listJson,item.path)
                    DataUpdateManager.editDataUpdateUpload(5,item.uid,2,item.typeId)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }


    /**
     * 每天上传增量数据
     */
    private fun uploadDataUpdate(token: String) {
        val items=DataUpdateDaoManager.getInstance().queryList()
        for (item in items) {
            if (item.isDelete) {
                val map = HashMap<String, Any>()
                map["type"] = item.type
                map["uid"] = item.uid
                map["contentType"] = item.contentType
                map["typeId"] = item.typeId
                mDataUpdatePresenter.onDeleteData(map)
                //上传删除本地增量更新
                DataUpdateDaoManager.getInstance().deleteBean(item.type, item.uid, item.contentType,item.typeId)
                continue
            }
            val map = HashMap<String, Any>()
            map["type"] = item.type
            map["uid"] = item.uid
            map["contentType"] = item.contentType
            map["typeId"] = item.typeId
            map["listJson"] = item.listJson
            map["downloadUrl"] = item.downloadUrl
            map["state"] = item.state
            if (item.path.isNullOrEmpty()) {
                mDataUpdatePresenter.onAddData(map)
                item.isUpload=true
                DataUpdateDaoManager.getInstance().insertOrReplace(item)
            } else {
                if (File(item.path).isDirectory){
                    FileUploadManager(token).apply {
                        startZipUpload(item.path, item.id.toString())
                        setCallBack {
                            map["path"]=item.path
                            map["downloadUrl"] = it
                            mDataUpdatePresenter.onAddData(map)
                            item.isUpload=true
                            DataUpdateDaoManager.getInstance().insertOrReplace(item)
                        }
                    }
                }
                else{
                    FileUploadManager(token).apply {
                        startUpload(item.path)
                        setCallBack {
                            map["path"]=item.path
                            map["downloadUrl"] = it
                            mDataUpdatePresenter.onAddData(map)
                            item.isUpload=true
                            DataUpdateDaoManager.getInstance().insertOrReplace(item)
                        }
                    }
                }
            }
        }
    }

    /**
     * 每学期清除数据
     */
    private fun clearSemesterData(){
        //清除作业通知（每学期上学开始）
        EventBus.getDefault().post(Constants.MAIN_HOMEWORK_NOTICE_CLEAR_EVENT)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.AUTO_UPLOAD_EVENT -> {
                if (!NetworkUtil.isNetworkConnected()){
                    NetworkUtil.toggleNetwork(true)
                }
                eventType = Constants.AUTO_UPLOAD_EVENT
                Handler().postDelayed({
                    mQiniuPresenter.getToken()
                }, 30 * 1000)
            }
            Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT -> {
                if (!NetworkUtil.isNetworkConnected()){
                    NetworkUtil.toggleNetwork(true)
                }
                eventType = Constants.AUTO_UPLOAD_LAST_SEMESTER_EVENT
                Handler().postDelayed({
                    mQiniuPresenter.getToken()
                }, 30 * 1000)
                clearSemesterData()
            }
            Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT -> {
                if (!NetworkUtil.isNetworkConnected()){
                    NetworkUtil.toggleNetwork(true)
                }
                eventType = Constants.AUTO_UPLOAD_NEXT_SEMESTER_EVENT
                Handler().postDelayed({
                    mQiniuPresenter.getToken()
                }, 30 * 1000)
                clearSemesterData()
            }
            Constants.USER_CHANGE_GRADE_EVENT -> {
                //年级变化清空增量更新
                mDataUpdatePresenter.clearData()
                eventType = Constants.USER_CHANGE_GRADE_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.AUTO_UPLOAD_YEAR_EVENT -> {
                if (!NetworkUtil.isNetworkConnected()){
                    NetworkUtil.toggleNetwork(true)
                }
                eventType = Constants.AUTO_UPLOAD_YEAR_EVENT
                Handler().postDelayed({
                    mQiniuPresenter.getToken()
                }, 30 * 1000)
            }
            Constants.DIARY_UPLOAD_EVENT -> {
                eventType = Constants.DIARY_UPLOAD_EVENT
                mQiniuPresenter.getToken()
            }
            Constants.SETTING_DOWNLOAD_EVENT -> {
                mDataUpdatePresenter.onList()
            }
            Constants.SETTING_RENT_EVENT -> {
                val map = HashMap<String, Any>()
                map["type"] = arrayOf(1, 2, 3, 7)
                mDataUpdatePresenter.onList(map)
            }
            Constants.SETTING_CLEAT_EVENT -> {
                clearData()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_APANEL_BACK || keyCode == KeyEvent.KEYCODE_BPANEL_BACK) {
            false
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        setExamMode(false)
    }
}