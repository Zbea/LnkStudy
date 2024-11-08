package com.bll.lnkstudy.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.CloudStorageActivity
import com.bll.lnkstudy.ui.activity.MainActivity
import com.bll.lnkstudy.ui.activity.ResourceCenterActivity
import com.bll.lnkstudy.ui.adapter.TabTypeAdapter
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import com.bll.lnkstudy.widget.FlowLayoutManager
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.common_fragment_title.tv_search
import kotlinx.android.synthetic.main.common_fragment_title.tv_title
import kotlinx.android.synthetic.main.common_page_number.btn_page_down
import kotlinx.android.synthetic.main.common_page_number.btn_page_up
import kotlinx.android.synthetic.main.common_page_number.ll_page_number
import kotlinx.android.synthetic.main.common_page_number.tv_page_current
import kotlinx.android.synthetic.main.common_page_number.tv_page_total_bottom
import kotlinx.android.synthetic.main.fragment_list_tab.rv_tab
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil


abstract class BaseFragment : Fragment(),IContractView.ICommonView, IBaseView{

    val mCommonPresenter= CommonPresenter(this)
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
    var screenPos=1
    var grade=mUser?.grade!!

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据
    var mTabTypeAdapter: TabTypeAdapter?=null
    var itemTabTypes= mutableListOf<ItemTypeBean>()

    override fun onList(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty()&&commonData.grade!=MethodManager.getItemLists("grades")){
            MethodManager.saveItemLists("grades",commonData.grade)
        }
        if (!commonData.subject.isNullOrEmpty()&&commonData.subject!=MethodManager.getItemLists("courses")){
            MethodManager.saveItemLists("courses",commonData.subject)
        }
        if (!commonData.typeGrade.isNullOrEmpty()&&commonData.typeGrade!=MethodManager.getItemLists("typeGrades")){
            MethodManager.saveItemLists("typeGrades",commonData.typeGrade)
        }
        if (!commonData.version.isNullOrEmpty()&&commonData.version!=MethodManager.getItemLists("bookVersions")){
            MethodManager.saveItemLists("bookVersions",commonData.version)
        }
    }
    override fun onListSchools(list: MutableList<SchoolBean>) {
        if (list.size!= DataBeanManager.schools.size)
            DataBeanManager.schools=list
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
        initCommonTitle()

        if (rv_tab!=null){
            initTabView()
        }

        initView()

        getScreenPosition()
        initDialog()

        lazyLoadDataIfPrepared()
    }


    private fun lazyLoadDataIfPrepared() {
        if (isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    private fun initDialog(){
        mDialog = ProgressDialog(requireActivity(),getScreenPosition(),0)
    }

    fun initDialog(screen:Int){
        mDialog = ProgressDialog(requireActivity(),screen,0)
    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(activity)
    }

    fun showToast(s:String){
        SToast.showText(getScreenPosition(),s)
    }

    fun showToast(sId:Int){
        SToast.showText(getScreenPosition(),sId)
    }

    fun showToast(screen: Int,s:String){
        SToast.showText(screen,s)
    }

    fun showToast(screen: Int,sId:Int){
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
     * 获取当前屏幕位置
     */
    protected fun getScreenPosition():Int{
        if (activity is MainActivity){
            screenPos=(activity as MainActivity).getCurrentScreenPos()
        }
        if (activity is ResourceCenterActivity){
            screenPos=(activity as ResourceCenterActivity).getCurrentScreenPos()
        }
        if (activity is CloudStorageActivity){
            screenPos=(activity as CloudStorageActivity).getCurrentScreenPos()
        }
        return screenPos
    }

    protected fun fetchCommonData(){
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            mCommonPresenter.getCommon()
            mCommonPresenter.getCommonSchool()
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

    /**
     * 跳转活动(关闭已经打开的)
     */
    protected fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
    }

    /**
     * 重新初始化屏幕位置
     */
    open fun initChangeScreenData(){
        initDialog()
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        if (mView==null||activity==null)return
        showToast(R.string.login_timeout)
        MethodManager.logout(requireActivity())
    }
    override fun hideLoading() {
        mDialog?.dismiss()
    }
    override fun showLoading() {
        mDialog?.show()
    }
    override fun fail(screen: Int, msg: String) {
        if (mView==null||activity==null)return
        showToast(screen,msg)
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

    /**
     * 页面切换刷新数据
     */
    open fun onRefreshData(){
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.USER_CHANGE_EVENT->{
                mUser= SPUtil.getObj("user", User::class.java)
                grade=mUser?.grade!!
            }
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                onNetworkConnectionSuccess()
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

    /**
     * 主数据请求（翻页）
     */
    open fun fetchData(){
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
