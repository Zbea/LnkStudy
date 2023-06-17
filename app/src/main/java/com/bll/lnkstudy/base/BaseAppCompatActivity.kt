package com.bll.lnkstudy.base

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.ui.activity.book.BookStoreActivity
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.activity.book.TextBookStoreActivity
import com.bll.lnkstudy.ui.activity.drawing.*
import com.bll.lnkstudy.utils.*
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_bookstore_type.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.ceil


abstract class BaseAppCompatActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView {

    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据

    open fun navigationToFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_layout, fragment, (fragment as Any?)!!.javaClass.simpleName)
                .addToBackStack(null).commitAllowingStateLoss()
        }
    }

    open fun popToStack(fragment: Fragment?) {
        val fragmentManager= supportFragmentManager
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        fragmentManager.popBackStack()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSaveState=savedInstanceState
        setContentView(layoutId())
        initCommonTitle()

        screenPos=getCurrentScreenPos()
        showLog(localClassName+"当前屏幕：$screenPos")

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.color_transparent))
        }

        mDialog = ProgressDialog(this,screenPos)
        initData()
        initView()
        initBookType()

    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    private fun initCommonTitle() {
        iv_back?.setOnClickListener { finish() }

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

    /**
     * 书城分类
     */
    private fun initBookType(){
        iv_jc?.setOnClickListener {
            customStartActivity(Intent(this, TextBookStoreActivity::class.java))
        }

        iv_gj?.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_gj))
        }

        iv_zrkx?.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_zrkx))
        }

        iv_shkx?.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_shkx))
        }

        iv_swkx?.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_sxkx))
        }

        iv_ydcy?.setOnClickListener {
            gotoBookStore(getString(R.string.book_tab_ydcy))
        }
    }

    fun showBackView(isShow:Boolean) {
        if (isShow){
            showView(iv_back)
        }
        else{
            disMissView(iv_back)
        }
    }

    fun showSearchView(isShow:Boolean) {
        if (isShow){
            showView(ll_search)
        }
        else{
            disMissView(ll_search)
        }
    }

    fun showSaveView() {
        showView(iv_save)
    }

    fun setPageTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    fun setPageTitle(titleId: Int) {
        tv_title?.setText(titleId)
    }

    fun setPageSetting(setId:Int){
        showView(tv_setting)
        tv_setting?.setText(setId)
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
     * 单双屏展开
     */
    fun moveToScreen(isExpand:Boolean){
        moveToScreenPanel(if (isExpand) 3 else screenPos )
    }

    /**
     * 得到当前屏幕位置
     */
    fun getCurrentScreenPos():Int{
        return getCurrentScreenPanel()
    }

    /**
     * 跳转书籍详情
     */
    fun gotoTextBookDetails(id: Int){
        ActivityManager.getInstance().checkBookIDisExist(id)
        val intent=Intent(this, BookDetailsActivity::class.java)
        intent.putExtra("book_id",id)
        customStartActivity1(intent)
    }

   private fun gotoBookStore(type: String){
        val intent=Intent(this, BookStoreActivity::class.java)
        intent.putExtra("category",type)
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
        val intent = Intent()
        intent.action = "com.geniatech.reader.action.VIEW_BOOK_PATH"
        intent.setPackage("com.geniatech.knote.reader")
        intent.putExtra("path", bookBean.bookPath)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", if (screenPos==3)2 else screenPos)
        startActivity(intent)
    }

    /**
     * 跳转作业本
     */
    fun gotoHomeworkDrawing(item: HomeworkTypeBean,page: Int){
        ActivityManager.getInstance().checkHomeworkDrawingisExist(item)
        val bundle= Bundle()
        bundle.putSerializable("homework",item)
        val intent=Intent(this, HomeworkDrawingActivity::class.java)
        intent.putExtra("homeworkBundle",bundle)
        intent.putExtra("page",page)
        customStartActivity1(intent)
    }

    /**
     * 跳转考卷
     */
    fun gotoPaperDrawing(mCourse:String,mTypeId:Int,page: Int){
        ActivityManager.getInstance().checkPaperDrawingIsExist(mCourse,mTypeId)
        val intent= Intent(this, PaperDrawingActivity::class.java)
        intent.putExtra("course",mCourse)
        intent.putExtra("typeId",mTypeId)
        intent.putExtra("page",mTypeId)
        customStartActivity1(intent)
    }

    /**
     * 跳转日记
     */
    fun gotoNote(noteBook: NotebookBean, page:Int){
        val intent = Intent(this, NoteDrawingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("note", noteBook)
        intent.putExtra("bundle", bundle)
        intent.putExtra("page",page)
        customStartActivity(intent)
    }

    /**
     * 跳转作业卷
     */
    fun gotoHomeworkReelDrawing(mCourse:String,mTypeId:Int,page: Int){
        ActivityManager.getInstance().checkHomeworkPaperDrawingIsExist(mCourse,mTypeId)
        val intent=Intent(this, HomeworkPaperDrawingActivity::class.java)
        intent.putExtra("course",mCourse)
        intent.putExtra("typeId",mTypeId)
        intent.putExtra("page",page)
        customStartActivity1(intent)
    }

    /**
     * 跳转录音
     */
    fun gotoHomeworkRecord(item:HomeworkTypeBean){
        val bundle= Bundle()
        bundle.putSerializable("homework",item)
        val intent=Intent(this, RecordListActivity::class.java)
        intent.putExtra("homeworkBundle",bundle)
        customStartActivity(intent)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected fun setStatusBarColor(statusColor: Int) {
        val window = window
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏颜色
        window.statusBarColor = statusColor
        //设置系统状态栏处于可见状态
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //让view不根据系统窗口来调整自己的布局
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(this)
    }

    fun showToast(s:String){
        SToast.showText(getCurrentScreenPos(),s)
    }

    fun showToast(sId:Int){
        SToast.showText(getCurrentScreenPos(),sId)
    }

    fun showLog(s:String){
        Log.d("debug",s)
    }
    fun showLog(sId:Int){
        Log.d("debug",getString(sId))
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
    fun customStartActivity1(intent: Intent){
        startActivity(intent)
    }

    fun getRadioButton(i:Int,str:String,max:Int): RadioButton {
        val radioButton =
            layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
        radioButton.text = str
        radioButton.id = i
        radioButton.isChecked = i == 0
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            DP2PX.dip2px(this, 45f))

        layoutParams.marginEnd = if (i == max) 0 else DP2PX.dip2px(this, 44f)
        radioButton.layoutParams = layoutParams

        return radioButton
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
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(this)
                    .setRationale("此功能需要" + sb + "权限，否则无法正常使用，是否打开设置")
                    .setPositiveButton("好")
                    .setNegativeButton("不行")
                    .build()
                    .show()
        }
    }

    fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。")
        // 拒绝, 退出应用
        builder.setNegativeButton("取消") { dialog, which ->

        }
        builder.setPositiveButton("确定") { dialog, which -> startAppSettings() }

        builder.setCancelable(false)
        builder.show()
    }

    /**
     * 启动应用的设置
     */
    private fun startAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + "com.bll.lnkstudy")
        startActivity(intent)
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        showToast(R.string.login_timeout)
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")

        Handler().postDelayed(Runnable {
            val intent=Intent(this, AccountLoginActivity::class.java)
            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
            startActivity(intent)
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
    }

    override fun hideLoading() {
        mDialog?.dismiss()
    }

    override fun showLoading() {
        mDialog?.show()
    }

    override fun fail(msg: String) {
        showToast(msg)
    }

    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog(R.string.connect_server_timeout)
    }
    override fun onComplete() {
        showLog(R.string.request_success)
    }

    override fun onPause() {
        super.onPause()
        mDialog!!.dismiss()
        hideKeyboard()
    }

    open fun fetchData(){

    }

}


