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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView {

    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var ivBack: ImageView? = null
    var tvPageTitle: TextView? = null
    var tvMyCollect: TextView? = null
    var ivSave: ImageView? = null

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
     *  ????????????
     */
    abstract fun layoutId(): Int

    /**
     * ???????????????
     */
    abstract fun initData()

    /**
     * ????????? View
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

    }

    fun setDisBackShow() {
        if (ivBack != null) {
            ivBack!!.visibility = View.GONE
        }
    }

    fun setSaveShow() {
        if (ivSave != null) {
            ivSave!!.visibility = View.VISIBLE
        }
    }

    fun setPageTitle(pageTitle: String) {
        if (tvPageTitle != null) {
            tvPageTitle!!.text = pageTitle
        }
    }
    fun setMyCollect()
    {
        if (tvMyCollect != null) {
            tvMyCollect?.visibility=View.VISIBLE
        }
    }

    /**
     * ??????view
     */
    protected fun showView(view: View?) {
        if (view != null && view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }

    /**
     * ??????view
     */
    protected fun showView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
            }
        }
    }


    /**
     * ??????view
     */
    protected fun disMissView(view: View?) {
        if (view != null && view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    /**
     * ??????view
     */
    protected fun disMissView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.GONE) {
                view.visibility = View.GONE
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected fun setStatusBarColor(statusColor: Int) {
        val window = window
        //?????????????????????
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //??????Flag?????????????????????????????????
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //?????????????????????
        window.statusBarColor = statusColor
        //???????????????????????????????????????
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //???view?????????????????????????????????????????????
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }


    /**
     * ???????????????
     */
    fun openKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * ???????????????
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * ???????????????
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
     * ????????????????????????Activity??????Fragment???onRequestPermissionsResult()?????????
     * ???????????????EasyPermissions.onRequestPermissionsResult()??????????????????
     *
     * @param requestCode  ????????????????????????
     * @param permissions  ???????????????
     * @param grantResults ????????????
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    /**
     * ?????????????????????????????????????????????
     *
     * @param requestCode ????????????????????????
     * @param perms       ????????????????????????
     */
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("EasyPermissions", "?????????????????????$perms")
    }
    /**
     * ?????????????????????????????????????????????
     *
     * @param requestCode ????????????????????????
     * @param perms       ????????????????????????
     */
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        //???????????????????????????
        val sb = StringBuffer()
        for (str in perms) {
            sb.append(str)
            sb.append("\n")
        }
        sb.replace(sb.length - 2, sb.length, "")
        //?????????????????????????????????????????????
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, "???????????????" + sb + "???????????????", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(this)
                    .setRationale("???????????????" + sb + "??????????????????????????????????????????????????????")
                    .setPositiveButton("???")
                    .setNegativeButton("??????")
                    .build()
                    .show()
        }
    }

    fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("??????")
        builder.setMessage("??????????????????????????????????????????\"??????\"-\"??????\"-?????????????????????")
        // ??????, ????????????
        builder.setNegativeButton("??????") { dialog, which ->

        }
        builder.setPositiveButton("??????") { dialog, which -> startAppSettings() }

        builder.setCancelable(false)
        builder.show()
    }

    /**
     * ?????????????????????
     */
    private fun startAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + "com.bll.lnkstudy")
        startActivity(intent)
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        SToast.showText("????????????,???????????????")
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
        showLog(msg)
    }

    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog("?????????????????????")
    }
    override fun onComplete() {
        showLog("????????????")
    }

    override fun onPause() {
        super.onPause()
        mDialog!!.dismiss()
    }

}


