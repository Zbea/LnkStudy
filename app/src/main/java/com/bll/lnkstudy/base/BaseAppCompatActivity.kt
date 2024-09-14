package com.bll.lnkstudy.base

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.book.BookStoreActivity
import com.bll.lnkstudy.ui.adapter.TabTypeAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.widget.FlowLayoutManager
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_list_tab.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.ceil


abstract class BaseAppCompatActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView, IContractView.ICommonView {

    private var mCommonPresenter=CommonPresenter(this)
    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mNetworkDialog: ProgressDialog?=null
    var mSaveState:Bundle?=null
    var mUser=SPUtil.getObj("user",User::class.java)

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据
    var isClickExpend=false //是否是单双屏切换
    var mTabTypeAdapter: TabTypeAdapter?=null
    var itemTabTypes= mutableListOf<ItemTypeBean>()

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

    override fun onList(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
            MethodManager.saveItemLists("grades",commonData.grade)
        if (!commonData.subject.isNullOrEmpty())
            MethodManager.saveItemLists("courses",commonData.subject)
        if (!commonData.typeGrade.isNullOrEmpty())
            MethodManager.saveItemLists("typeGrades",commonData.typeGrade)
        if (!commonData.version.isNullOrEmpty())
            MethodManager.saveItemLists("bookVersions",commonData.version)
        onCommonData()
    }

    open fun onCommonData(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSaveState=savedInstanceState
        setContentView(layoutId())

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )){
            EasyPermissions.requestPermissions(this,"请求权限",1,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )
        }
        EventBus.getDefault().register(this)
        screenPos=getCurrentScreenPos()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.color_transparent))
        }

        if (rv_tab!=null){
            initTabView()
        }

        fetchCommonData()
        initCreate()
        initDialog()
        initData()
        initView()
        initCommonTitle()
    }

    /**
     * 初始化onCreate
     */
    open fun initCreate(){
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

    private fun initDialog(){
        mDialog = ProgressDialog(this,getCurrentScreenPos(),0)
        mNetworkDialog= ProgressDialog(this,getCurrentScreenPos(),1)
    }

    fun initDialog(screen:Int){
        mDialog = ProgressDialog(this,screen,0)
        mNetworkDialog= ProgressDialog(this,screen,1)
    }

    protected fun fetchCommonData(){
        if (NetworkUtil(this).isNetworkConnected()&&MethodManager.getItemLists("grades").size==0)
            mCommonPresenter.getCommon()
    }

    protected fun setPageTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    protected fun setPageTitle(titleId: Int) {
        tv_title?.setText(titleId)
    }

    protected fun setPageSetting(setId:Int){
        showView(tv_setting)
        tv_setting?.setText(setId)
    }
    protected fun setImageManager(setId:Int){
        showView(iv_manager)
        iv_manager.setImageResource(setId)
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

    private fun initTabView(){
        rv_tab.layoutManager = FlowLayoutManager()//创建布局管理
        mTabTypeAdapter = TabTypeAdapter(R.layout.item_tab_type, null).apply {
            rv_tab.adapter = this
            bindToRecyclerView(rv_tab)
            setOnItemClickListener { adapter, view, position ->
                for (item in mTabTypeAdapter?.data!!){
                    item.isCheck=false
                }
                val item=mTabTypeAdapter?.data!![position]
                item.isCheck=true
                mTabTypeAdapter?.notifyDataSetChanged()

                onTabClickListener(view,position)
            }
        }
    }

    /**
     * tab点击监听
     */
    open fun onTabClickListener(view:View, position:Int){

    }

    /**
     * 得到当前屏幕位置
     */
    fun getCurrentScreenPos():Int{
        return getCurrentScreenPanel()
    }

    /**
     * 设置dialog可以点击消失
     */
    protected fun setDialogOutside(boolean: Boolean){
        mDialog?.setCanceledOutside(boolean)
        mNetworkDialog?.setCanceledOutside(boolean)
    }

    protected fun hideNetworkDialog() {
        mNetworkDialog?.dismiss()
    }

    protected fun showNetworkDialog() {
        mNetworkDialog?.show()
        NetworkUtil(this).toggleNetwork(true)
    }

    protected fun gotoBookStore(type: Int){
        val intent=Intent(this, BookStoreActivity::class.java)
        intent.flags=type
        customStartActivity(intent)
    }

    /**
     * 设置翻页
     */
    protected fun setPageNumber(total:Int){
        if (ll_page_number!=null){
            pageCount = ceil(total.toDouble() / pageSize).toInt()
            if (total == 0) {
                disMissView(ll_page_number)
            } else {
                tv_page_current.text = pageIndex.toString()
                tv_page_total_bottom.text = pageCount.toString()
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
    protected fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(this)
    }

    protected fun showToast(s:String){
        SToast.showText(getCurrentScreenPos(),s)
    }

    protected fun showToast(sId:Int){
        SToast.showText(getCurrentScreenPos(),sId)
    }

    fun showToast(screen: Int,s:String){
        SToast.showText(screen,s)
    }

    fun showToast(screen: Int,sId:Int){
        SToast.showText(screen,sId)
    }

    protected fun showLog(s:String){
        Log.d("debug",s)
    }
    protected fun showLog(sId:Int){
        Log.d("debug",getString(sId))
    }

    /**
     * 跳转活动(关闭已经打开的)
     */
    protected fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
    }

    protected fun closeNetwork(){
        NetworkUtil(this).toggleNetwork(false)
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

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        showToast(R.string.login_timeout)
        MethodManager.logout(this)
    }

    override fun hideLoading() {
        mDialog?.dismiss()
    }

    override fun showLoading() {
        mDialog?.show()
    }

    override fun fail(screen: Int,msg: String) {
        showToast(screen,msg)
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

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                hideNetworkDialog()
                onNetworkConnectionSuccess()
            }
            Constants.NETWORK_CONNECTION_FAIL_EVENT->{
                hideNetworkDialog()
            }
            else->{
                onEventBusMessage(msgFlag)
            }
        }
    }

    /**
     * 网络连接成功处理事件
     */
    open fun onNetworkConnectionSuccess(){
    }

    /**
     * 收到eventbus事件处理
     */
    open fun onEventBusMessage(msgFlag: String){
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!isClickExpend){
            screenPos=getCurrentScreenPos()
        }
        initDialog()
        initChangeScreenData()
        isClickExpend=false
    }

    /**
     * 切屏后，重新初始化数据（用于数据请求弹框显示正确的位置）
     */
    open fun initChangeScreenData(){
    }
}


