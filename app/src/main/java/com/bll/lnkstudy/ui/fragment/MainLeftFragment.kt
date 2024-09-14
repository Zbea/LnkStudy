package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.RecoverySystem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.AUTO_REFRESH_EVENT
import com.bll.lnkstudy.Constants.Companion.CALENDER_SET_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_DRAWING_EVENT
import com.bll.lnkstudy.Constants.Companion.DATE_EVENT
import com.bll.lnkstudy.Constants.Companion.MAIN_HOMEWORK_NOTICE_CLEAR_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.AppUpdateDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.SystemUpdateInfo
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.mvp.model.date.DateEventBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolItemBean
import com.bll.lnkstudy.mvp.presenter.MainLeftPresenter
import com.bll.lnkstudy.mvp.presenter.SystemManagerPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IMainLeftView
import com.bll.lnkstudy.mvp.view.IContractView.ISystemView
import com.bll.lnkstudy.ui.activity.ScreenshotListActivity
import com.bll.lnkstudy.ui.activity.date.DateActivity
import com.bll.lnkstudy.ui.activity.date.DateDayListActivity
import com.bll.lnkstudy.ui.activity.date.DateEventActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanListActivity
import com.bll.lnkstudy.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkstudy.ui.adapter.MainDatePlanAdapter
import com.bll.lnkstudy.ui.adapter.MainHomeworkNoticeAdapter
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.DeviceUtil
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.HtRecoverySystem
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.date.CalenderUtils
import com.google.gson.Gson
import com.htfy.params.ServerParams
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_main_left.iv_calender
import kotlinx.android.synthetic.main.fragment_main_left.iv_change
import kotlinx.android.synthetic.main.fragment_main_left.iv_close
import kotlinx.android.synthetic.main.fragment_main_left.iv_date
import kotlinx.android.synthetic.main.fragment_main_left.iv_date_more
import kotlinx.android.synthetic.main.fragment_main_left.ll_notice
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_note
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_notice
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_plan
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_today
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_content
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_end_time
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_name
import kotlinx.android.synthetic.main.fragment_main_left.tv_notice_time
import kotlinx.android.synthetic.main.fragment_main_left.tv_plan
import kotlinx.android.synthetic.main.fragment_main_left.tv_planover
import kotlinx.android.synthetic.main.fragment_main_left.tv_screenshot
import kotlinx.android.synthetic.main.fragment_main_left.v_down
import kotlinx.android.synthetic.main.fragment_main_left.v_up
import java.io.File
import java.util.Random


/**
 * 首页
 */
class MainLeftFragment : BaseMainFragment(), IMainLeftView, ISystemView {
    private val mSystemPresenter = SystemManagerPresenter(this, 1)
    private val mMainLeftPresenter = MainLeftPresenter(this, 1)
    private var mPlanAdapter: MainDatePlanAdapter? = null
    private var correctAdapter: MainHomeworkNoticeAdapter? = null
    private var mNoticeAdapter: MainHomeworkNoticeAdapter? = null
    private var nowDate = 0L
    private var nowDayPos = 1
    private var calenderPath = ""
    private var dateEvents = mutableListOf<DateEventBean>()
    private var updateDialog: AppUpdateDialog? = null
    private var isChange = false
    private var isShow = false//是否存在台历
    private var noticeItems = mutableListOf<HomeworkNoticeList.HomeworkNoticeBean>()

    override fun onUpdateInfo(item: SystemUpdateInfo) {
        updateDialog = AppUpdateDialog(requireActivity(), 2, item).builder()
        downLoadStartSystem(item)
    }

    override fun onAppUpdate(item: AppUpdateBean) {
        if (item.versionCode > AppUtils.getVersionCode(requireActivity())) {
            updateDialog = AppUpdateDialog(requireActivity(), 1, item).builder()
            downLoadStart(item)
        }
    }

    override fun onCorrect(list: HomeworkNoticeList) {
        correctAdapter?.setNewData(list.list)
    }

    override fun onType(type: TeachingVideoType) {
        SPUtil.putObj("videoType", type)
    }

    override fun onParentPermission(permissionParentBean: PermissionParentBean) {
        SPUtil.putObj("parentPermission", permissionParentBean)
    }

    override fun onSchoolPermission(permissionSchoolBean: PermissionSchoolBean) {
        if (permissionSchoolBean.config.isNotEmpty()) {
            val item = Gson().fromJson(permissionSchoolBean.config, PermissionSchoolItemBean::class.java)
            SPUtil.putObj("schoolPermission", item)
        } else {
            SPUtil.removeObj("schoolPermission")
        }
    }

    override fun onHomeworkNotice(list: HomeworkNoticeList) {
        if (list.list.isNotEmpty()) {
            noticeItems = list.list
            mNoticeAdapter?.setNewData(noticeItems)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[0])

        initPlanView()
        initNoticeView()
        initCorrect()

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        iv_date.setOnClickListener {
            val intent = Intent(requireActivity(), DateEventActivity::class.java)
            intent.putExtra("date", nowDate)
            customStartActivity(intent)
        }

        iv_date_more.setOnClickListener {
            customStartActivity(Intent(activity, DateDayListActivity::class.java))
        }

        tv_plan.setOnClickListener {
            customStartActivity(Intent(activity, DatePlanListActivity::class.java))
        }

//        tv_plan.setOnClickListener {
//            if (dateEvents.isEmpty())
//                return@setOnClickListener
//            val item =dateEvents[0]
//            val intent=Intent(requireActivity(), DatePlanDetailsActivity::class.java)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            val bundle = Bundle()
//            bundle.putSerializable("dateEvent", item)
//            intent.putExtra("bundle", bundle)
//            customStartActivity(intent)
//        }

        v_up.setOnClickListener {
            nowDate -= Constants.dayLong
            setDateView()
            if (isShow && nowDayPos > 1) {
                nowDayPos -= 1
                setCalenderBg()
            }
        }

        v_down.setOnClickListener {
            nowDate += Constants.dayLong
            setDateView()
            if (isShow && nowDayPos <= 366) {
                nowDayPos += 1
                setCalenderBg()
            }
        }

        iv_change.setOnClickListener {
            isChange = !isChange
            if (isChange) {
                showView(iv_calender)
            } else {
                disMissView(iv_calender)
            }
        }

        iv_change.setOnLongClickListener {
            val boolean = SPUtil.getBoolean("isShowCalender")
            val titleStr = if (boolean) "默认显示日程？" else "默认显示台历？"
            CommonDialog(requireActivity(), 1).setContent(titleStr).builder().onDialogClickListener = object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    if (boolean) {
                        SPUtil.putBoolean("isShowCalender", false)
                        disMissView(iv_calender)
                    } else {
                        SPUtil.putBoolean("isShowCalender", true)
                        showView(iv_calender)
                    }
                }
            }
            return@setOnLongClickListener true
        }

        tv_screenshot.setOnClickListener {
            customStartActivity(Intent(activity, ScreenshotListActivity::class.java))
        }

        tv_planover.setOnClickListener {
            customStartActivity(Intent(activity, PlanOverviewActivity::class.java))
        }

        initDialog(1)
    }

    override fun lazyLoad() {
        //删除系统更新文件
        val path=FileAddress().getPathSystemUpdate(DeviceUtil.getOtaProductVersion())
        if (File(path).exists()){
            File(path).delete()
        }

        nowDate = DateUtils.getStartOfDayInMillis()
        setDateView()
        showCalenderView()
        findDataPlan()
        fetchData()
    }

    override fun fetchData() {
        fetchCommonData()
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {

            val systemUpdateMap = HashMap<String, String>()
            systemUpdateMap[Constants.SN] = DeviceUtil.getOtaSerialNumber()
            systemUpdateMap[Constants.KEY] = ServerParams.getInstance().GetHtMd5Key(DeviceUtil.getOtaSerialNumber())
            systemUpdateMap[Constants.VERSION_NO] = DeviceUtil.getOtaProductVersion() //getProductVersion();
            mSystemPresenter.checkSystemUpdate(systemUpdateMap)

            mMainLeftPresenter.getHomeworkNotice()
            mMainLeftPresenter.getAppUpdate()
            mMainLeftPresenter.active()
            mMainLeftPresenter.getCorrectNotice()
            mMainLeftPresenter.getTeachingType()
            mMainLeftPresenter.getParentPermission()
            mMainLeftPresenter.getSchoolPermission()
        }
    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView() {
//        val solar= Solar()
//        solar.solarYear= DateUtils.getYear()
//        solar.solarMonth=DateUtils.getMonth()
//        solar.solarDay=DateUtils.getDay()
//        val lunar= LunarSolarConverter.SolarToLunar(solar)
//
//        val str = if (!solar.solar24Term.isNullOrEmpty()) {
//            "24节气   "+solar.solar24Term
//        } else {
//            if (!solar.solarFestivalName.isNullOrEmpty()) {
//                "节日  "+solar.solarFestivalName
//            } else {
//                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
//                    "节日   "+lunar.lunarFestivalName
//                }
//                else{
//                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
//                }
//            }
//        }
        tv_date_today.text = DateUtils.longToStringWeek(nowDate)

        val path = FileAddress().getPathDate(DateUtils.longToStringCalender(nowDate)) + "/draw.png"
        if (File(path).exists()) {
//            GlideUtils.setImageNoCacheRoundUrl(activity,path,iv_date,20)
            val myBitmap = BitmapFactory.decodeFile(path)
            iv_date.setImageBitmap(myBitmap)
        } else {
            iv_date.setImageResource(0)
        }
    }

    /**
     * 是否显示台历
     */
    private fun showCalenderView() {
        val item = CalenderDaoManager.getInstance().queryCalenderBean()
        isShow = item != null
        if (isShow) {
            calenderPath = item.path
            showView(iv_change, iv_calender)
            setCalenderView()
        } else {
            isChange = false
            disMissView(iv_change, iv_calender)
        }
    }

    /**
     * 设置台历内容
     */
    private fun setCalenderView() {
        if (isShow) {
            val calenderUtils = CalenderUtils(DateUtils.longToStringDataNoHour(nowDate))
            nowDayPos = calenderUtils.elapsedTime()
            setCalenderBg()
            if (SPUtil.getBoolean("isShowCalender")) {
                isChange = true
                showView(iv_calender)
            } else {
                isChange = false
                disMissView(iv_calender)
            }
        }
    }

    /**
     * 设置台历图片
     */
    private fun setCalenderBg() {
        val listFiles = FileUtils.getFiles(calenderPath)
        if (listFiles.size > 0) {
            val file = if (listFiles.size > nowDayPos - 1) {
                listFiles[nowDayPos - 1]
            } else {
                listFiles[Random().nextInt(listFiles.size)]
//                listFiles[listFiles.size - 1]
            }
            GlideUtils.setImageFileRound(requireActivity(), file, iv_calender, 15)
        }
    }

    //今日计划
    private fun initPlanView() {
        mPlanAdapter = MainDatePlanAdapter(R.layout.item_main_date_plan, null).apply {
            rv_main_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_plan.adapter = this
            bindToRecyclerView(rv_main_plan)
        }
    }

    /**
     * 作业通知
     */
    private fun initNoticeView() {
        mNoticeAdapter = MainHomeworkNoticeAdapter(R.layout.item_main_homework_notice, null).apply {
            rv_main_notice.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_notice.adapter = this
            bindToRecyclerView(rv_main_notice)
        }
        mNoticeAdapter?.setOnItemClickListener { adapter, view, position ->
            showView(ll_notice)
            setNoticeShow(position)
        }
    }

    /**
     * 展示作业详情
     */
    private fun setNoticeShow(position: Int) {
        val item = noticeItems[position]

        tv_notice_name?.text = item.name + " (${item.typeName})"
        tv_notice_time?.text = "发送时间：" + DateUtils.longToStringDataNoYear(item.time)
        if (item.endTime > 0) {
            showView(tv_notice_end_time)
            tv_notice_end_time?.text = "提交时间：" + DateUtils.longToStringWeek(item.endTime)
        }
        tv_notice_content?.text = "通知内容：${item.title}"

        iv_close.setOnClickListener {
            disMissView(ll_notice)
        }
    }


    //批改详情
    private fun initCorrect() {
        correctAdapter = MainHomeworkNoticeAdapter(R.layout.item_main_homework_notice, null).apply {
            rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
        }
    }

    //下载应用
    private fun downLoadStart(bean: AppUpdateBean) {
        val targetFileStr = FileAddress().getPathApk("lnkstudy")
        FileDownManager.with(requireActivity()).create(bean.downloadUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
            FileDownManager.SingleTaskCallBack {
            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                if (task != null && task.isRunning) {
                    requireActivity().runOnUiThread {
                        val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M") + "/" +
                                ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                        updateDialog?.setUpdateBtn(s)
                    }
                }
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun completed(task: BaseDownloadTask?) {
                updateDialog?.dismiss()
                AppUtils.installApp(requireActivity(), targetFileStr)
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                updateDialog?.dismiss()
            }
        })
    }

    //下载系统
    private fun downLoadStartSystem(bean: SystemUpdateInfo) {
        val path=FileAddress().getPathSystemUpdate(bean.version)
        if (File(path).exists()){
            setSystemUpdate(path)
            return
        }
        FileBigDownManager.with(requireActivity()).create(bean.otaUrl).setPath(path).startSingleTaskDownLoad(object :
            FileBigDownManager.SingleTaskCallBack {
            override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                if (task != null && task.isRunning) {
                    requireActivity().runOnUiThread {
                        val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M") + "/" +
                                ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                        updateDialog?.setUpdateBtn(s)
                    }
                }
            }

            override fun completed(task: BaseDownloadTask?) {
                setSystemUpdate(path)
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                updateDialog?.dismiss()
            }
        })
    }

    /**
     * 下载完毕开始系统更新
     */
    private fun setSystemUpdate(path:String){
        updateDialog?.setUpdateInfo()
        Thread {
            try {
                RecoverySystem.verifyPackage(File(path), {
                    if (it == 100) {
                        HtRecoverySystem.installPackage(requireActivity(), File(path))
                    }
                }, null)
            }
            catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 查找学习计划
     */
    private fun findDataPlan() {
//        val dates=DateEventGreenDaoManager.getInstance().queryAllDateEvent(1)
//        for (item in dates){
//            if (item.maxLong<nowDate){
//                val selectDate=SPUtil.getListLong("dateDateEvent")
//                selectDate.removeAll(item.dates)
//                SPUtil.putListLong("dateDateEvent",selectDate)
//                DateEventGreenDaoManager.getInstance().deleteDateEvent(item)
//            }
//        }

        val years = DateUtils.longToStringDataNoHour(nowDate)
        val dateBean = DateBean()
        dateBean.year = years[0].toInt()
        dateBean.month = years[1].toInt()
        dateBean.day = years[2].toInt()
        dateBean.time = nowDate
        dateBean.week = DateUtils.getWeek(nowDate)

        dateEvents = DateEventGreenDaoManager.getInstance().queryAllDateEvent(dateBean)
        if (dateEvents.size > 0) {
            mPlanAdapter?.setNewData(dateEvents[0].plans)
        } else {
            mPlanAdapter?.setNewData(null)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            AUTO_REFRESH_EVENT -> {
                lazyLoad()
            }
            DATE_EVENT -> {
                findDataPlan()
            }
            MAIN_HOMEWORK_NOTICE_CLEAR_EVENT -> {
                mMainLeftPresenter.deleteHomeworkNotice()
                mMainLeftPresenter.deleteCorrectNotice()
            }
            CALENDER_SET_EVENT -> {
                showCalenderView()
            }
            DATE_DRAWING_EVENT -> {
                setDateView()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}