package com.bll.lnkstudy.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.AppToolDialog
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.model.EventBusBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.ui.activity.drawing.DraftActivity
import com.bll.lnkstudy.utils.*
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView {

    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var ivBack: ImageView? = null
    var tvPageTitle: TextView? = null
    var ivSave: ImageView? = null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var tvSearch:TextView?=null
    var ivToolLeft: ImageView? = null
    var ivToolRight: ImageView? = null
    var toolApps= mutableListOf<AppBean>()

    var ivDraft:ImageView?=null
    var ivErasure:ImageView?=null
    var isExpand=false
    var elik_a: EinkPWInterface? = null
    var elik_b: EinkPWInterface? = null
    var isErasure=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSaveState=savedInstanceState
        setContentView(layoutId())
        initCommonTitle()

        EventBus.getDefault().register(this)

        screenPos=getCurrentScreenPos()
        showLog(localClassName+"当前屏幕：$screenPos")

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        }

        getAppTool()

        mDialog = ProgressDialog(this,screenPos)
        initData()
        initView()

    }

    /**
     * 获取工具应用
     */
    fun getAppTool(){
        val appAlls=AppUtils.scanLocalInstallAppList(this)
        toolApps= AppDaoManager.getInstance().queryAll()

        //从数据库中拿到应用集合 遍历查询已存储的应用是否已经卸载 卸载删除 没有卸载则拿到对应图标
        val it=toolApps.iterator()
        while (it.hasNext()){
            val item=it.next()
            if (isAppContains(item,appAlls)){
                item.image=getAppDrawable(item)
            }
            else{
                it.remove()
                AppDaoManager.getInstance().deleteBean(item)
            }
        }
    }

    /**
     * 判断app是否已经存在
     */
    fun isAppContains(item:AppBean,list: List<AppBean>):Boolean{
        var isContain=false
        for (ite in list){
            if (ite.packageName.equals(item.packageName))
            {
                isContain=true
            }
        }
        return isContain
    }

    /**
     * 拿到app对应的应用图标
     */
    private fun getAppDrawable(item:AppBean): Drawable? {
        val appAlls=AppUtils.scanLocalInstallAppList(this)
        var drawable: Drawable?=null
        for (ite in appAlls){
            if (ite.packageName.equals(item.packageName))
            {
                drawable=ite.image
            }
        }
        return drawable
    }

    private fun showDialogAppTool(scree:Int){
        AppToolDialog(this,scree,toolApps).builder()
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

    @SuppressLint("WrongViewCast")
    fun initCommonTitle() {
        ivBack = findViewById(R.id.iv_back)
        if (ivBack != null) {
            ivBack!!.setOnClickListener { finish() }
        }
        ivSave = findViewById(R.id.iv_save)
        tvPageTitle = findViewById(R.id.tv_title)
        tvSearch= findViewById(R.id.tv_search)

        ivToolLeft = findViewById(R.id.iv_tool_left)
        if (ivToolLeft != null) {
            ivToolLeft?.setOnClickListener {
                if (getCurrentScreenPos()==3)//全屏时点击左按钮在左
                {
                    showDialogAppTool(1)
                }
                else{
                    showDialogAppTool(0)
                }
            }
        }

        ivToolRight = findViewById(R.id.iv_tool_right)
        if (ivToolRight != null) {
            ivToolRight?.setOnClickListener {
                if (getCurrentScreenPos()==3)
                {
                    showDialogAppTool(2)
                }
                else{
                    showDialogAppTool(0)
                }
            }
        }

        ivErasure=findViewById(R.id.iv_erasure)
        if (ivErasure!=null){
            ivErasure?.setOnClickListener {
                isErasure=!isErasure
                if (isErasure){
                    onErasure()
                }
                else{
                    stopErasure()
                }
            }
        }

        ivDraft = findViewById(R.id.iv_draft)
        if (ivDraft != null) {
            ivDraft?.setOnClickListener {
                startActivity(Intent(this,DraftActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

    }

    /**
     * 设置擦除
     */
    open fun onErasure(){
    }

    /**
     * 结束擦除
     * （在展平、收屏时候都结束擦除）
     */
    fun stopErasure(){
        if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
        }
        if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
        }
    }

    fun changeErasure(){
        if (isErasure){
            isErasure=false
            stopErasure()
        }
    }

    fun showBackView(isShow:Boolean) {
        if (isShow){
            showView(ivBack)
        }
        else{
            disMissView(ivBack)
        }
    }

    fun showSearchView(isShow:Boolean) {
        if (isShow){
            showView(tvSearch)
        }
        else{
            disMissView(tvSearch)
        }
    }

    fun showSaveView() {
        showView(ivSave)
    }

    fun setPageTitle(pageTitle: String) {
        if (tvPageTitle != null) {
            tvPageTitle!!.text = pageTitle
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
     * 单双屏展开
     */
    fun moveToScreen(isExpand:Boolean){
        moveToScreen(if (isExpand) 3 else screenPos )
    }

    /**
     * 换屏 0默认左屏幕 1左屏幕 2右屏幕 3全屏
     */
    fun moveToScreen(scree: Int){
        moveToScreenPanel(scree)
    }

    /**
     * 得到当前屏幕位置
     */
    fun getCurrentScreenPos():Int{
        return getCurrentScreenPanel()
    }

    /**
     * 跳转活动 已经打开过则关闭
     */
    fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component.className)
        startActivity(intent)
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

    fun showLog(s:String){
        Log.d("debug",s)
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
        showToast("连接超时,请重新登陆")
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
        mDialog!!.show()
    }

    override fun fail(msg: String) {
        showToast(msg)
    }

    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog("服务器连接失败")
    }
    override fun onComplete() {
        showLog("请求完成")
    }

    override fun onPause() {
        super.onPause()
        mDialog!!.dismiss()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(bean: EventBusBean) {
        if (bean.id== Constants.SCREEN_EVENT){
            val screen=bean.screen
            screenPos=when(screen){
                1->2
                2->1
                else->2
            }
            changeScreenPage()
        }
    }

    /**
     * 自动收屏
     */
    open fun changeScreenPage(){
    }

}


