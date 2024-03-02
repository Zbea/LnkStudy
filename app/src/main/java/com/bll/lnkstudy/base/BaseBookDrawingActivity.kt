package com.bll.lnkstudy.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.calalog.CatalogChild
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParent
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.drawing.*
import com.bll.lnkstudy.utils.*
import com.chad.library.adapter.base.entity.MultiItemEntity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_book_details_drawing.*
import kotlinx.android.synthetic.main.ac_book_details_drawing.iv_geometry
import kotlinx.android.synthetic.main.ac_book_details_drawing.ll_geometry
import kotlinx.android.synthetic.main.ac_book_details_drawing.v_content_a
import kotlinx.android.synthetic.main.ac_book_details_drawing.v_content_b
import kotlinx.android.synthetic.main.common_drawing_geometry.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.regex.Pattern


abstract class BaseBookDrawingActivity : AppCompatActivity(), IBaseView {

    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mNetworkDialog:ProgressDialog?=null
    var mSaveState:Bundle?=null
    var mUser=SPUtil.getObj("user",User::class.java)

    var catalogMsg: CatalogMsg? = null
    var catalogs = mutableListOf<MultiItemEntity>()
    var parentItems = mutableListOf<CatalogParent>()
    var childItems = mutableListOf<CatalogChild>()

    var pageCount = 0
    var pageStart=1
    var page = 0 //当前页码

    var isExpand=false
    var elik_a: EinkPWInterface? = null
    var elik_b: EinkPWInterface? = null
    var isErasure=false
    private var circlePos=0
    private var axisPos=0
    private var isGeometry=false//是否处于几何绘图
    private var isParallel=false//是否选中平行
    private var isCurrent=false//当前支持的几何绘图笔形
    private var isScale=false//是否选中刻度
    private var revocationList= mutableListOf<Int>()
    private var currentGeometry=0
    private var currentDrawObj=PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN//当前笔形

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSaveState=savedInstanceState
        setContentView(layoutId())
        initCommonTitle()

        EventBus.getDefault().register(this)

        screenPos=getCurrentScreenPos()
//        showLog(localClassName+"当前屏幕：$screenPos")

        setStatusBarColor(ContextCompat.getColor(this, R.color.white))

        if (v_content_a!=null && v_content_b!=null){
            elik_a = v_content_a?.pwInterFace
            elik_b = v_content_b?.pwInterFace
        }

        mDialog = ProgressDialog(this,getCurrentScreenPos(),0)
        mNetworkDialog= ProgressDialog(this,getCurrentScreenPos(),1)
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

        iv_tool?.setOnClickListener {
            showDialogAppTool(2)
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

        btn_page_up?.setOnClickListener {
            onPageUp()
        }

        btn_page_down?.setOnClickListener {
            onPageDown()
        }

        iv_expand?.setOnClickListener {
            onChangeExpandContent()
        }

        iv_catalog?.setOnClickListener {
            DrawingCatalogDialog(this, catalogs, 1, pageStart).builder()
                ?.setOnDialogClickListener { position ->
                    page = position - 1
                    changeContent()
                }
        }

        ll_hide?.setOnClickListener {
            disMissView(ll_work)
        }

        ll_show?.setOnClickListener {
            showView(ll_work)
        }

    }

    /**
     * 设置页码
     */
    fun setPage(){
        tv_page.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
    }

    /**
     * 几何绘图
     */
    private fun initGeometryView(){

        val popsCircle= mutableListOf<PopupBean>()
        popsCircle.add(PopupBean(0,getString(R.string.circle_1),R.mipmap.icon_geometry_circle_1))
        popsCircle.add(PopupBean(1,getString(R.string.circle_2),R.mipmap.icon_geometry_circle_2))
        popsCircle.add(PopupBean(2,getString(R.string.circle_3),R.mipmap.icon_geometry_circle_3))

        val popsAxis= mutableListOf<PopupBean>()
        popsAxis.add(PopupBean(0,getString(R.string.axis_one),R.mipmap.icon_geometry_axis_1))
        popsAxis.add(PopupBean(1,getString(R.string.axis_two),R.mipmap.icon_geometry_axis_2))
        popsAxis.add(PopupBean(2,getString(R.string.axis_three),R.mipmap.icon_geometry_axis_3))

        val pops= mutableListOf<PopupBean>()
        pops.add(PopupBean(0,getString(R.string.line_black),false))
        pops.add(PopupBean(1,getString(R.string.line_gray),false))
        pops.add(PopupBean(2,getString(R.string.line_dotted),false))

        iv_geometry?.setOnClickListener {
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            disMissView(iv_geometry)
        }

        iv_line?.setOnClickListener {
            setCheckView(ll_line)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_LINE)
            currentGeometry=1
        }

        iv_rectangle?.setOnClickListener {
            setCheckView(ll_rectangle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RECTANGLE)
            currentGeometry=2
        }

        tv_circle?.setOnClickListener {
            PopupClick(this,popsCircle,tv_circle,5).builder().setOnSelectListener{
                iv_circle.setImageResource(it.resId)
                circlePos=it.id
                setEilkCircle()
            }
        }

        iv_circle?.setOnClickListener {
            setEilkCircle()
        }

        iv_arc?.setOnClickListener {
            setCheckView(ll_arc)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ARC)
            currentGeometry=4
        }

        iv_oval?.setOnClickListener {
            setCheckView(ll_oval)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_OVAL)
            currentGeometry=5
        }

        iv_vertical?.setOnClickListener {
            setCheckView(ll_vertical)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_VERTICALLINE)
            currentGeometry=6
        }

        iv_parabola?.setOnClickListener {
            setCheckView(ll_parabola)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_PARABOLA)
            currentGeometry=7
        }

        iv_angle?.setOnClickListener {
            setCheckView(ll_angle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ANGLE)
            currentGeometry=8
        }

        tv_axis?.setOnClickListener {
            PopupClick(this,popsAxis,tv_axis,5).builder().setOnSelectListener{
                iv_axis?.setImageResource(it.resId)
                axisPos=it.id
                setEilkAxis()
            }
        }

        iv_axis?.setOnClickListener {
            setEilkAxis()
        }

        iv_pen?.setOnClickListener {
            setDrawing()
        }

        tv_revocation?.setOnClickListener {
            if (revocationList.size>0){
                val type=revocationList.last()
                if (type==1)
                {
                    elik_a?.unDo()
                }
                else{
                    elik_b?.unDo()
                }
                revocationList.removeLast()
            }
        }

        tv_gray_line?.setOnClickListener {
            if (!isGeometry){
                return@setOnClickListener
            }
            PopupClick(this,pops,tv_gray_line,5).builder().setOnSelectListener{
                tv_gray_line?.text=it.name
                setLine(it.id)
            }
        }

        tv_scale?.setOnClickListener {
            if (isErasure){
                stopErasure()
            }
            tv_scale.isSelected=!tv_scale.isSelected
            isScale=tv_scale.isSelected
            tv_scale.setTextColor(getColor(if (isScale) R.color.white else R.color.black))
            if (currentGeometry==9){
                setEilkAxis()
            }
        }

        tv_parallel?.setOnClickListener {
            if (isErasure){
                stopErasure()
            }
            tv_parallel.isSelected=!isParallel
            isParallel=tv_parallel.isSelected
            tv_parallel.setTextColor(getColor(if (isParallel) R.color.white else R.color.black))
        }

        tv_reduce?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry)
            showView(iv_geometry)
            setViewElikUnable(iv_geometry)
            if (isParallel){
                tv_parallel?.callOnClick()
            }
            if (isScale){
                tv_scale?.callOnClick()
            }
        }

        tv_out?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry,iv_geometry)
            if (isParallel){
                tv_parallel?.callOnClick()
            }
            if (isScale){
                tv_scale?.callOnClick()
            }
        }

        elik_a?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_a?.setShifted(isCurrent&&isParallel)
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(1)
                if (elik_a?.curDrawObjStatus == true){
                    reDrawGeometry(elik_a!!,1)
                }
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                onElikSava_a()
            }
        })

        elik_b?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_b?.setShifted(isCurrent&&isParallel)
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(2)
                if (elik_b?.curDrawObjStatus == true){
                    reDrawGeometry(elik_b!!,2)
                }
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                onElikSava_b()
            }
        })

        this.setTouchAsFocus(true)
    }

    /**
     * 设置刻度重绘
     */
    private fun reDrawGeometry(elik:EinkPWInterface,location: Int){
        if (isErasure)
            return
        if (isScale){
            if (currentGeometry==1||currentGeometry==2||currentGeometry==3||currentGeometry==5||currentGeometry==7||currentGeometry==8||currentGeometry==9){
                GeometryScaleDialog(this,currentGeometry,circlePos,location).builder()
                    ?.setOnDialogClickListener{
                            width, height ->
                        when (currentGeometry) {
                            2, 5 -> {
                                elik.reDrawShape(width,height)
                            }
                            7->{
                                val info=elik.curHandlerInfo
                                elik.reDrawShape(if (setA(info)>0) width else -width ,info.split("&")[1].toFloat())
                            }
                            9 -> {
                                elik.reDrawShape(width,5f)
                            }
                            else -> {
                                elik.reDrawShape(width,-1f)
                            }
                        }
                    }
            }
        }
    }

    /**
     * 获取a值
     */
    private fun setA(info:String):Float{
        val list= mutableListOf<String>()
        val pattern= Pattern.compile("-?\\d+(\\.\\d+)") // 编译正则表达式，匹配连续的数字
        val matcher= pattern.matcher(info) // 创建匹配器对象
        while (matcher.find()){
            list.add(matcher.group())
        }
        return list[0].toFloat()
    }

    /**
     * 画圆
     */
    private fun setEilkCircle(){
        setCheckView(ll_circle)
        when(circlePos){
            0->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE)
            1->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE2)
            else->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE3)
        }
        currentGeometry=3
    }

    /**
     * 画坐标
     */
    private fun setEilkAxis(){
        setCheckView(ll_axis)
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS)
        elik_a?.setDrawAxisProperty(axisPos+1, 10, 5,isScale)
        elik_b?.setDrawAxisProperty(axisPos+1, 10, 5,isScale)
        currentGeometry=9
    }

    /**
     * 设置线
     */
    private fun setLine(type: Int){
        when(type){
            0->{
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(0)
            }
            1->{
                elik_a?.penColor = Color.parseColor("#999999")
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.parseColor("#999999")
                elik_b?.setPaintEffect(0)
            }
            else->{
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(1)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(1)
            }
        }
    }

    /**
     * 设置选中笔形
     */
    private fun setCheckView(view:View){
        if (isErasure){
            stopErasure()
        }
        if (view!=ll_pen){
            isGeometry=true
        }
        //当前支持平行的view
        isCurrent = view==ll_line||view==ll_angle||view==ll_axis
        ll_line?.setBackgroundResource(R.color.color_transparent)
        ll_rectangle?.setBackgroundResource(R.color.color_transparent)
        ll_circle?.setBackgroundResource(R.color.color_transparent)
        ll_arc?.setBackgroundResource(R.color.color_transparent)
        ll_oval?.setBackgroundResource(R.color.color_transparent)
        ll_vertical?.setBackgroundResource(R.color.color_transparent)
        ll_parabola?.setBackgroundResource(R.color.color_transparent)
        ll_angle?.setBackgroundResource(R.color.color_transparent)
        ll_axis?.setBackgroundResource(R.color.color_transparent)
        ll_pen?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner)
    }

    /**
     * 设置笔类型
     */
    private fun setDrawOjectType(type:Int){
        elik_a?.drawObjectType = type
        elik_b?.drawObjectType = type
        if (type!=PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
            currentDrawObj=type
    }


    /**
     * 工具栏弹窗
     */
    private fun showDialogAppTool(location:Int){
        AppToolDialog(this,1,getCurrentScreenPos(),location).builder()?.setDialogClickListener{
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            if (isErasure)
                stopErasure()
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
     * 左屏抬笔
     */
    open fun onElikSava_a(){
    }

    /**
     * 右屏抬笔
     */
    open fun onElikSava_b(){
    }

    /**
     * 单屏、全屏内容切换
     */
    open fun changeExpandView() {
        v_content_a.visibility = if (isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 展示内容变化
     */
    open fun changeContent(){

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
        isErasure=false
        //关闭橡皮擦
        iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure)
        setDrawOjectType(currentDrawObj)
    }

    /**
     * 恢复手写
      */
    private fun setDrawing(){
        setCheckView(ll_pen)
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN)
        currentGeometry=0
        //设置黑线
        setLine(0)
        tv_gray_line?.text=getString(R.string.line_black)

        isGeometry=false
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
     * 单双屏切换
     */
    open fun onChangeExpandContent(){
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

    fun hideNetworkDialog() {
        mNetworkDialog?.dismiss()
    }
    fun showNetworkDialog() {
        mNetworkDialog?.show()
        NetworkUtil(this).toggleNetwork(true)
    }

    fun closeNetwork(){
        NetworkUtil(this).toggleNetwork(false)
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
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                hideNetworkDialog()
                onNetworkConnectionSuccess()
            }
            Constants.NETWORK_CONNECTION_FAIL_EVENT->{
                hideNetworkDialog()
                showToast(R.string.net_work_error)
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


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        //屏蔽长按切焦点造成原手写翻页
        if (getKeyEventStatus()==17||getKeyEventStatus()==34){
            return super.onKeyUp(keyCode, event)
        }
        when(keyCode){
            KeyEvent.KEYCODE_PAGE_DOWN->{
                onPageDown()
            }
            KeyEvent.KEYCODE_PAGE_UP->{
                onPageUp()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

}


