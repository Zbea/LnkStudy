package com.bll.lnkstudy.base

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.presenter.ControlMessagePresenter
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.ui.activity.HomeLeftActivity
import com.bll.lnkstudy.ui.activity.PaintingTypeListActivity
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.activity.drawing.*
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import kotlin.math.ceil


abstract class BaseFragment : Fragment(), IContractView.ICloudUploadView
    ,IContractView.IControlMessageView ,IContractView.IDataUpdateView, IContractView.IClassGroupView, EasyPermissions.PermissionCallbacks, IBaseView {

    val mDownMapPool = HashMap<Int, ImageDownLoadUtils>()//下载管理
    val mCloudUploadPresenter= CloudUploadPresenter(this)
    val mControlMessagePresenter= ControlMessagePresenter(this)
    val mDataUploadPresenter=DataUpdatePresenter(this)
    val mClassGroupPresenter = ClassGroupPresenter(this)
    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    var mView:View?=null
    var mDialog: ProgressDialog? = null
    var mUser=SPUtil.getObj("user",User::class.java)
    var accountId=SPUtil.getObj("user",User::class.java)?.accountId
    var screenPos=0
    var grade=0

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据

    var isRequestClassGroup=false//页面是否请求

    //控制消息回调
    override fun onControlMessage(controlMessages: MutableList<ControlMessage>) {
        //发送全局老师控制删除
        if (controlMessages.size>0){
            EventBus.getDefault().post(Constants.CONTROL_MESSAGE_EVENT)
            val list= mutableListOf<Int>()
            for (item in controlMessages){
                list.add(item.id)
            }
            mControlMessagePresenter.deleteControlMessage(list)
        }
    }
    override fun onDeleteMessage() {
    }

    override fun onSystemControlClear(controlMessages: MutableList<ControlMessage>) {
        if (controlMessages.size>0){
            EventBus.getDefault().post(Constants.CONTROL_CLEAR_EVENT)
            val list= mutableListOf<Int>()
            for (item in controlMessages){
                list.add(item.id)
            }
            SPUtil.putListInt("ControlClear",list)
        }
    }
    override fun onDeleteSystemClear() {
    }

    override fun onEditGradeSuccess() {
    }

    //云端上传回调
    override fun onSuccess(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    //增量更新回调
    override fun onSuccess() {
    }
    override fun onList(list: MutableList<DataUpdateBean>?) {
    }

    //班级回调
    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        if (DataBeanManager.classGroups != classGroups){
            DataBeanManager.classGroups=classGroups
            EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
        }
    }
    override fun onQuit() {
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null != mView) {
            container?.removeView(container)
        } else {
            mView = inflater.inflate(getLayoutId(), container,false)
        }
        return mView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        isViewPrepare = true
        grade=mUser?.grade!!
        initCommonTitle()
        initView()
        if (activity is HomeLeftActivity)
            screenPos=(activity as HomeLeftActivity).getCurrentScreenPos()
        mDialog = ProgressDialog(activity,screenPos)
        lazyLoadDataIfPrepared()
    }

    /**
     * 设置页面是否请求班群信息
     */
    fun setClassGroupRequest(isBoolean: Boolean){
        this.isRequestClassGroup=isBoolean
    }

    /**
     * 获取控制信息指令
     */
    private fun onFetchControl(){
        mControlMessagePresenter.getControlMessage()
        mControlMessagePresenter.getSystemControlClear()
        if (isRequestClassGroup)
            mClassGroupPresenter.getClassGroupList(false)
    }

    private fun lazyLoadDataIfPrepared() {
        if (isViewPrepare && !hasLoadData) {
            onFetchControl()
            lazyLoad()
            hasLoadData = true
        }
    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(activity)
    }

    fun showToast(screen:Int ,s:String){
        SToast.showText(screen,s)
    }

    fun showToast(screen:Int ,sId:Int){
        SToast.showText(screen,sId)
    }

    fun showLog(s:String){
        Log.d("debug",s)
    }
    fun showLog(sId:Int){
        Log.d("debug",getString(sId))
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    private fun initCommonTitle() {

        btn_page_up?.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                fetchData()
            }
        }

        btn_page_down?.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }

    }

    fun setTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    fun setTitle(titleId: Int) {
        tv_title?.setText(titleId)
    }


    fun showSearch(isShow:Boolean) {
        if (isShow){
            showView(tv_search)
        }
        else{
            disMissView(tv_search)
        }
    }


    /**
     * 显示view
     */
    protected fun showView(view: View?) {
        if (view != null && view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }

    /**
     * 显示view
     */
    protected fun showView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
            }
        }
    }


    /**
     * 消失view
     */
    protected fun disMissView(view: View?) {
        if (view != null && view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    /**
     * 消失view
     */
    protected fun disMissView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.GONE) {
                view.visibility = View.GONE
            }
        }
    }

    /**
     * 设置翻页
     */
    fun setPageNumber(total:Int){
        if (ll_page_number!=null){
            pageCount = ceil(total.toDouble() / pageSize).toInt()
            if (total == 0) {
                disMissView(ll_page_number)
            } else {
                tv_page_current.text = pageIndex.toString()
                tv_page_total.text = pageCount.toString()
                showView(ll_page_number)
            }
        }
    }

    fun getRadioButton(i:Int,str:String,max:Int):RadioButton{
        val radioButton =
            layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
        radioButton.text = str
        radioButton.id = i
        radioButton.isChecked = i == 0
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            DP2PX.dip2px(activity, 45f))

        layoutParams.marginEnd = if (i == max) 0 else DP2PX.dip2px(activity, 44f)
        radioButton.layoutParams = layoutParams

        return radioButton
    }

    fun getRadioButton(i:Int,check:Int,str:String,max:Int):RadioButton{
        val radioButton =
            layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
        radioButton.text = str
        radioButton.id = i
        radioButton.isChecked = i == check
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            DP2PX.dip2px(activity, 45f))

        layoutParams.marginEnd = if (i == max) 0 else DP2PX.dip2px(activity, 44f)
        radioButton.layoutParams = layoutParams

        return radioButton
    }

    /**
     * 跳转书籍详情
     */
    fun gotoTextBookDetails(id: Int){
        ActivityManager.getInstance().checkBookIDisExist(id)
        val intent=Intent(activity, BookDetailsActivity::class.java)
        intent.putExtra("book_id",id)
        customStartActivity1(intent)
    }

    /**
     * 跳转作业本
     */
    fun gotoHomeworkDrawing(item: HomeworkTypeBean){
        ActivityManager.getInstance().checkHomeworkDrawingisExist(item)
        val bundle= Bundle()
        bundle.putSerializable("homework",item)
        val intent=Intent(context, HomeworkDrawingActivity::class.java)
        intent.putExtra("homeworkBundle",bundle)
        customStartActivity1(intent)
    }

    /**
     * 跳转画本
     */
    fun gotoPaintingDrawing(type: Int){
        val items=PaintingTypeDaoManager.getInstance().queryAllByType(type)
        //当前年级 手写书画分类为null则创建
        var item=PaintingTypeDaoManager.getInstance().queryAllByGrade(type,grade)
        if (item==null) {
            val date=System.currentTimeMillis()
            item=PaintingTypeBean()
            item.type = type
            item.grade = grade
            item.date = date
            val id=PaintingTypeDaoManager.getInstance().insertOrReplaceGetId(item)
            //创建本地画本增量更新
            DataUpdateManager.createDataUpdate(5,id.toInt(),1, 1, Gson().toJson(item))
        }

        if (items.size>1){
            val intent=Intent(activity, PaintingTypeListActivity::class.java)
            intent.flags=type
            customStartActivity(intent)
        } else{
            ActivityManager.getInstance().checkPaintingDrawingIsExist(type)
            val intent=Intent(context, PaintingDrawingActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("painting",item)
            intent.putExtra("paintingBundle",bundle)
            customStartActivity1(intent)
        }

    }

    /**
     * 跳转考卷
     */
    fun gotoPaperDrawing(mCourse:String,mTypeId:Int){
        ActivityManager.getInstance().checkPaperDrawingIsExist(mCourse,mTypeId)
        val intent=Intent(activity, PaperDrawingActivity::class.java)
        intent.putExtra("course",mCourse)
        intent.putExtra("typeId",mTypeId)
        customStartActivity1(intent)
    }

    /**
     * 跳转作业卷
     */
    fun gotoHomeworkReelDrawing(item: HomeworkTypeBean){
        ActivityManager.getInstance().checkHomeworkPaperDrawingIsExist(item.course,item.typeId)
        val intent=Intent(activity, HomeworkPaperDrawingActivity::class.java)
        val bundle= Bundle()
        bundle.putSerializable("homework",item)
        intent.putExtra("bundle",bundle)
        customStartActivity1(intent)
    }

    /**
     * 跳转录音
     */
    fun gotoHomeworkRecord(item:HomeworkTypeBean){
        val bundle= Bundle()
        bundle.putSerializable("homework",item)
        val intent=Intent(activity, RecordListActivity::class.java)
        intent.putExtra("homeworkBundle",bundle)
        customStartActivity(intent)
    }

    /**
     * 跳转阅读器
     */
    fun gotoBookDetails(bookBean: BookBean){
        bookBean.isLook=true
        bookBean.time=System.currentTimeMillis()
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean)
        EventBus.getDefault().post(Constants.BOOK_EVENT)
        val toolApps= AppDaoManager.getInstance().queryAll()

        val result = JSONArray()
        for (item in toolApps){
            val jsonObject = JSONObject()
            try {
                jsonObject.put("appName", item.appName)
                jsonObject.put("packageName", item.packageName)
            } catch (_: JSONException) {
            }
            result.put(jsonObject)
        }
        val intent = Intent()
        intent.action = "com.geniatech.reader.action.VIEW_BOOK_PATH"
        intent.setPackage("com.geniatech.knote.reader")
        intent.putExtra("path", bookBean.bookPath)
        intent.putExtra("tool", result.toString())
        intent.putExtra("key_book_id",bookBean.bookId.toString())
        intent.putExtra("bookName", bookBean.bookName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", if (screenPos==3)2 else screenPos)
        startActivity(intent)
    }

    /**
     * 跳转笔记写作
     */
    fun gotoIntent(note: Note){
        note.date=System.currentTimeMillis()
        NoteDaoManager.getInstance().insertOrReplace(note)
        EventBus.getDefault().post(Constants.NOTE_EVENT)

        val intent = Intent(activity, NoteDrawingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("note", note)
        intent.putExtra("bundle", bundle)
        customStartActivity(intent)
    }

    /**
     * 跳转活动(关闭已经打开的)
     */
    fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
    }

    /**
     * 跳转活动
     */
    private fun customStartActivity1(intent: Intent){
        startActivity(intent)
    }

    fun deleteDoneTask(task: ImageDownLoadUtils?) {
        if (mDownMapPool.isNotEmpty()) {
            //拿出map中的键值对
            val entries = mDownMapPool.entries
            val iterator = entries.iterator();
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<*, *>
                val entity = entry.value
                if (task == entity) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * 清空作业本
     */
    fun setClearHomework(){
        //删除所有作业分类
        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有朗读
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        HomeworkPaperContentDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.HOMEWORK_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(2)
        val map=HashMap<String,Any>()
        map["type"]=2
        mDataUploadPresenter.onDeleteData(map)
    }

    /**
     * 清空考卷
     */
    fun setClearPaper(){
        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()
        PaperContentDaoManager.getInstance().clear()
        FileUtils.deleteFile(File(Constants.TESTPAPER_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(3)
        val map=HashMap<String,Any>()
        map["type"]=3
        mDataUploadPresenter.onDeleteData(map)
    }

    /**
     * 系统控制（在上传完成后删除作业、考卷，升年级）
     */
    fun setSystemControlClear(){
        val homeworkTypes=HomeworkTypeDaoManager.getInstance().queryAll()
        val paperTypes=PaperTypeDaoManager.getInstance().queryAll()
        if (homeworkTypes.isNullOrEmpty()&&paperTypes.isNullOrEmpty()){
            //考卷上传完之后升年级
            mUser?.grade=grade+1
            SPUtil.putObj("user", mUser!!)
            EventBus.getDefault().post(Constants.USER_EVENT)
            mControlMessagePresenter.editGrade(mUser?.grade!!)
            //上传完之后 删除控制删除消息
            val controlClearIds=SPUtil.getListInt("ControlClear")
            mControlMessagePresenter.deleteSystemClearMessage(controlClearIds)
        }
    }

    /**
     * 重写要申请权限的Activity或者Fragment的onRequestPermissionsResult()方法，
     * 在里面调用EasyPermissions.onRequestPermissionsResult()，实现回调。
     *
     * @param requestCode  权限请求的识别码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 当权限被成功申请的时候执行回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("EasyPermissions", "获取成功的权限$perms")
    }

    /**
     * 当权限申请失败的时候执行的回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        //处理权限名字字符串
        val sb = StringBuffer()
        for (str in perms) {
            sb.append(str)
            sb.append("\n")
        }
        sb.replace(sb.length - 2, sb.length, "")
        //用户点击拒绝并不在询问时候调用
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            Toast.makeText(activity, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(requireActivity())
                    .setRationale("此功能需要" + sb + "权限，否则无法正常使用，是否打开设置")
                    .setPositiveButton("好")
                    .setNegativeButton("不行")
                    .build()
                    .show()
        }
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        if (mView==null||activity==null)return
        showToast(screenPos,R.string.login_timeout)
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")
        Handler().postDelayed(Runnable {
            val intent=Intent(activity, AccountLoginActivity::class.java)
            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
            startActivity(intent)
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
    }
    override fun hideLoading() {
        if (mView==null||activity==null)return
        mDialog?.dismiss()
    }
    override fun showLoading() {
        mDialog?.show()
    }
    override fun fail(msg: String) {
        if (mView==null||activity==null)return
        showToast(screenPos,msg)
    }
    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
    }
    override fun onComplete() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            onRefreshData()
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.USER_EVENT->{
                mUser= SPUtil.getObj("user", User::class.java)
                grade=mUser?.grade!!
                setClearHomework()
                setClearPaper()
            }
            else->{
                onEventBusMessage(msgFlag)
            }
        }
    }

    /**
     * 收到eventbus事件处理
     */
    open fun onEventBusMessage(msgFlag: String){

    }

    /**
     * 页面切换刷新数据
     */
    open fun onRefreshData(){
        onFetchControl()
    }

    open fun fetchData(){

    }

    /**
     * 上传成功(书籍云id)
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
        if (!cloudIds.isNullOrEmpty())
        {
            mCloudUploadPresenter.deleteCloud(cloudIds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
