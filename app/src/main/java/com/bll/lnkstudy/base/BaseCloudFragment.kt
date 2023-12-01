package com.bll.lnkstudy.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.dialog.ProgressNetworkDialog
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.presenter.CloudPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.CloudStorageActivity
import com.bll.lnkstudy.utils.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil


abstract class BaseCloudFragment : Fragment(), IContractView.ICloudView , IBaseView {

    val mCloudPresenter= CloudPresenter(this,getScreenPosition())
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
    var mNetworkDialog: ProgressNetworkDialog?=null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var screenPos=0
    var grade=0
    var types= mutableListOf<String>()

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据

    override fun onList(item: CloudList) {
        onCloudList(item)
    }
    override fun onType(types: MutableList<String>) {
        onCloudType(types)
    }
    override fun onDelete() {
        onCloudDelete()
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

        getScreenPosition()

        mDialog = ProgressDialog(requireActivity(),getScreenPosition())
        mNetworkDialog=ProgressNetworkDialog(requireActivity(),getScreenPosition())

        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
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
     * 更改年级
     */
    open fun changeGrade(grade: Int){
        this.grade=grade
        pageIndex=1
        fetchData()
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
     * 获取当前屏幕位置
     */
    fun getScreenPosition():Int{
        if (activity is CloudStorageActivity){
            screenPos=(activity as CloudStorageActivity).getCurrentScreenPos()
        }
        return screenPos
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

    fun hideNetworkDialog() {
        mNetworkDialog?.dismiss()
    }
    fun showNetworkDialog() {
        mNetworkDialog?.show()
        NetworkUtil(requireActivity()).toggleNetwork(true)
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        if (mView==null||activity==null)return
        showToast(screenPos,R.string.login_timeout)
        MethodManager.logout(requireActivity())
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
            refreshData()
        }
    }

    /**
     * 页面切换刷新数据
     */
    open fun refreshData(){

    }
    /**
     * 请求数据
     */
    open fun fetchData(){

    }
    /**
     * 获取云数据
     */
    open fun onCloudList(item: CloudList){

    }
    /**
     * 获取云分类
     */
    open fun onCloudType(types: MutableList<String>){

    }
    /**
     * 删除云数据
     */
    open fun onCloudDelete(){

    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.USER_CHANGE_EVENT->{
                mUser= SPUtil.getObj("user", User::class.java)
                grade=mUser?.grade!!
                EventBus.getDefault().post(Constants.HOMEWORK_BOOK_EVENT)
            }
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                hideNetworkDialog()
                onNetworkConnectionSuccess()
            }
            Constants.NETWORK_CONNECTION_FAIL_EVENT->{
                hideNetworkDialog()
                showToast(getScreenPosition(),R.string.net_work_error)
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
}
