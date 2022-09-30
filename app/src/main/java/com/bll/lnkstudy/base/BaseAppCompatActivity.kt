package com.bll.lnkstudy.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


abstract class BaseAppCompatActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView {

    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var ivBack: ImageView? = null
    var tvPageTitle: TextView? = null
    var tvMyCollect: TextView? = null
    var ivSave: ImageView? = null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var llSearch:LinearLayout?=null

    open fun navigationToFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_layout, fragment, (fragment as Any?)!!.javaClass.simpleName)
                .addToBackStack(null).commitAllowingStateLoss()
        }
    }

    open fun popToStack(fragment: Fragment?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        fragmentManager.popBackStack()
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSaveState=savedInstanceState
        setContentView(layoutId())
        initCommonTitle()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        }

        val decorView = window.decorView
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
//        val uiOptions = (0
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide naviagtion,and whow again when touch
//                or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        decorView.systemUiVisibility = uiOptions

        mDialog = ProgressDialog(this)
        initData()
        initView()

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
        ivSave = findViewById(R.id.iv_save)
        tvPageTitle = findViewById(R.id.tv_title)
        tvMyCollect = findViewById(R.id.tv_myCollect)
        if (ivBack != null) {
            ivBack!!.setOnClickListener { finish() }
        }
        llSearch= findViewById(R.id.ll_search)

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
            showView(llSearch)
        }
        else{
            disMissView(llSearch)
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
     * 跳转书籍详情
     */
    fun gotoBookDetails(book: Book){
        if (ActivityManager.getInstance().checkBookIDisExist(book.id))
        {
            SToast.showText("本书已经打开,请勿重新开打")
        }
        else{
            var intent=Intent(this, BookDetailsActivity::class.java)
            intent.putExtra("book_id",book.id)
            startActivity(intent)
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
     * 打卡软键盘
     */
    fun openKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(this)
    }

    fun showToast(s:String){
        SToast.showText(s)
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
        SToast.showText("连接超时,请重新登陆")
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")

        Handler().postDelayed(Runnable {
            startActivity(Intent(this, AccountLoginActivity::class.java))
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
    }

}


