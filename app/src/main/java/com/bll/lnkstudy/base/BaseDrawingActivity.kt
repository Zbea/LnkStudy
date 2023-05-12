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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.mvp.model.AppBean
import com.bll.lnkstudy.mvp.model.EventBusData
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.AccountLoginActivity
import com.bll.lnkstudy.ui.activity.drawing.DraftDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.PaperExamDrawingActivity
import com.bll.lnkstudy.utils.*
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import kotlinx.android.synthetic.main.common_drawing_geometry.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


abstract class BaseDrawingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView {

    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var toolApps= mutableListOf<AppBean>()
    var isExpand=false
    var elik_a: EinkPWInterface? = null
    var elik_b: EinkPWInterface? = null
    var isErasure=false
    var isTitleClick=true//标题是否可以编辑
    private var isLongPress=false

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

        if (v_content_a!=null && v_content_b!=null){
            elik_a = v_content_a?.pwInterFace
            elik_b = v_content_b?.pwInterFace
        }

        getAppTool()

        mDialog = ProgressDialog(this,screenPos)
        initData()
        initView()

        initGeometryView()
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

        iv_back?.setOnClickListener { finish() }

        iv_tool_left?.setOnClickListener {
            if (getCurrentScreenPos()==3)//全屏时点击左按钮在左
            {
                showDialogAppTool(1)
            }
            else{
                showDialogAppTool(0)
            }
        }

        iv_tool_right?.setOnClickListener {
            if (getCurrentScreenPos()==3)
            {
                showDialogAppTool(2)
            }
            else{
                showDialogAppTool(0)
            }
        }

        iv_erasure?.setOnClickListener {
            isErasure=!isErasure
            if (isErasure){
                iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure_big)
                onErasure()
            }
            else{
                stopErasure()
            }
        }

        iv_draft?.setOnClickListener {
            startActivity(Intent(this,DraftDrawingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        tv_title_a?.setOnClickListener {
            if (isTitleClick){
                val title=tv_title_a.text.toString()
                var type=getCurrentScreenPos()
                if (type==3)
                    type=1
                InputContentDialog(this,type,title).builder()?.setOnDialogClickListener { string ->
                    tv_title_a.text = string
                    setDrawingTitle_a(string)
                }
            }
        }

        tv_title_b?.setOnClickListener {
            if (isTitleClick){
                val title=tv_title_b.text.toString()
                var type=getCurrentScreenPos()
                if (type==3)
                    type=2
                InputContentDialog(this,type,title).builder()?.setOnDialogClickListener { string ->
                    tv_title_b.text = string
                    setDrawingTitle_b(string)
                }
            }
        }

        btn_page_up?.setOnClickListener {
            onPageUp()
        }

        btn_page_down?.setOnClickListener {
            onPageDown()
        }

    }

    /**
     * 几何绘图
     */
    private fun initGeometryView(){

        iv_geometry?.setOnClickListener {
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            disMissView(iv_geometry)
        }

        iv_line?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_LINE)
        }

        iv_rectangle?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RECTANGLE)
        }

        ll_circle?.setOnClickListener {
            val pops= mutableListOf<PopupBean>()
            pops.add(PopupBean(0,getString(R.string.circle_1),R.mipmap.icon_geometry_circle_1))
            pops.add(PopupBean(1,getString(R.string.circle_2),R.mipmap.icon_geometry_circle_2))
            pops.add(PopupBean(2,getString(R.string.circle_3),R.mipmap.icon_geometry_circle_3))
            PopupClick(this,pops,tv_circle,5).builder().setOnSelectListener{
                when(it.id){
                    0->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE)
                    1->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE2)
                    else->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE3)
                }
            }
        }

        iv_arc?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ARC)
        }

        iv_oval?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_OVAL)
        }

        iv_vertical?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_VERTICALLINE)
        }

        iv_parabola?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_PARABOLA)
        }

        iv_angle?.setOnClickListener {
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ANGLE)
        }

        iv_axis?.setOnClickListener {
            DrawingGeometryAxisDialog(this,getCurrentScreenPos()).builder().setOnDialogClickListener {
                    isScale, value, type ->
                elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_AXIS
                elik_a?.setDrawAxisProperty(type, value, isScale)
                elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_AXIS
                elik_b?.setDrawAxisProperty(type, value, isScale)
            }
        }

        tv_gray_line?.setOnClickListener {
            val pops= mutableListOf<PopupBean>()
            pops.add(PopupBean(0,getString(R.string.line_black),false))
            pops.add(PopupBean(1,getString(R.string.line_gray),false))
            pops.add(PopupBean(2,getString(R.string.line_dotted),false))
            PopupClick(this,pops,tv_gray_line,5).builder().setOnSelectListener{
                tv_gray_line.text=it.name
                when(it.id){
                    0->{

                    }
                    1->{

                    }
                    else->{

                    }
                }
            }
        }

        tv_reduce?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry)
            showView(iv_geometry)
            setViewElikUnable(iv_geometry)
        }

        tv_out?.setOnClickListener {
            if (this.localClassName == PaperExamDrawingActivity::class.java.name) return@setOnClickListener
            setDrawing()
            disMissView(ll_geometry,iv_geometry)
        }

    }

    /**
     * 设置笔类型
     */
    private fun setDrawOjectType(type:Int){
        elik_a?.drawObjectType = type
        elik_b?.drawObjectType = type
    }

    /**
     * 设置标题是否可以编辑
     */
    fun setDrawingTitleClick(boolean: Boolean){
        isTitleClick=boolean
    }

    /**
     * 获取工具应用
     */
    fun getAppTool(){
        toolApps= AppDaoManager.getInstance().queryAll()
        //从数据库中拿到应用集合 遍历查询已存储的应用是否已经卸载 卸载删除 没有卸载则拿到对应图标
        val it=toolApps.iterator()
        while (it.hasNext()){
            val item=it.next()
            if (isAppContains(item,getLocalApp())){
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
        var drawable: Drawable?=null
        for (ite in getLocalApp()){
            if (ite.packageName.equals(item.packageName))
            {
                drawable=ite.image
            }
        }
        return drawable
    }

    /**
     * 拿到本地全部应用
     */
    fun getLocalApp():List<AppBean>{
        return AppUtils.scanLocalInstallAppList(this)
    }

    /**
     * 工具栏弹窗
     */
    private fun showDialogAppTool(scree:Int){
        val tools= mutableListOf<AppBean>()
        tools.add(AppBean().apply {
            appName=getString(R.string.geometry_title_str)
            image=resources.getDrawable(R.mipmap.icon_app_geometry)
//            packageName="com.android.htfyunnote"
        })
        tools.addAll(toolApps)
        AppToolDialog(this,scree,tools).builder()?.setDialogClickListener{ pos->
            if (pos==0){
                setViewElikUnable(ll_geometry)
                showView(ll_geometry)
            }
        }
    }

    /**
     * 设置不能手写
     */
    fun setViewElikUnable(view:View){
        elik_a?.addOnTopView(view)
        elik_b?.addOnTopView(view)
    }

    /**
     * 下一页
     */
    open fun onPageDown(){
    }

    /**
     * 上一页
     */
    open fun onPageUp(){
    }

    /**
     * 设置擦除
     */
    private fun onErasure(){
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
    }

    /**
     * 结束擦除
     * （在展平、收屏时候都结束擦除）
     */
    private fun stopErasure(){
        iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure)
        setDrawing()
    }

    /**
     * 恢复手写
      */
    private fun setDrawing(){
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN)
    }

    /**
     * 自动收屏后自动取消橡皮擦
     */
    fun changeErasure(){
        if (isErasure){
            isErasure=false
            stopErasure()
        }
    }

    /**
     * 标题a操作
     */
    open fun setDrawingTitle_a(title:String){
    }

    /**
     * 标题a操作
     */
    open fun setDrawingTitle_b(title:String){
    }

    /**
     * 单双屏切换
     */
    open fun onChangeExpandContent(){

    }

    fun showBackView(isShow:Boolean) {
        if (isShow){
            showView(iv_back)
        }
        else{
            disMissView(iv_back)
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
        ActivityManager.getInstance().finishActivity(intent.component?.className)
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
        mDialog!!.show()
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(bean: EventBusData) {
        if (bean.event== Constants.SCREEN_EVENT){
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


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (!isLongPress){
            when(keyCode){
                KeyEvent.KEYCODE_PAGE_DOWN->{
                    onPageDown()
                }
                KeyEvent.KEYCODE_PAGE_UP->{
                    onPageUp()
                }
            }
        }
        else{
            isLongPress=false
        }
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        isLongPress=true
        when(keyCode){
            KeyEvent.KEYCODE_PAGE_DOWN->{
                //切换成右屏
                isExpand=true
                onChangeExpandContent()
                moveToScreen(2)
            }
            KeyEvent.KEYCODE_PAGE_UP->{
                //切换成左屏
                isExpand=true
                onChangeExpandContent()
                moveToScreen(1)
            }
        }
        return super.onKeyLongPress(keyCode, event)
    }


}


