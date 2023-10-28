package com.bll.lnkstudy.base

import android.content.Intent
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
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.ControlMessage
import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.presenter.ControlMessagePresenter
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.MainActivity
import com.bll.lnkstudy.ui.activity.ResourceCenterActivity
import com.bll.lnkstudy.utils.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import kotlin.math.ceil


abstract class BaseFragment : Fragment(), IContractView.ICloudUploadView
    ,IContractView.IControlMessageView ,IContractView.IDataUpdateView, IBaseView {

    val mCloudUploadPresenter= CloudUploadPresenter(this)
    val mControlMessagePresenter= ControlMessagePresenter(this)
    val mDataUploadPresenter=DataUpdatePresenter(this)
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
    var grade=0

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据

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
        mDialog = ProgressDialog(activity,screenPos)

        lazyLoadDataIfPrepared()
    }


    /**
     * 获取控制信息指令
     */
    private fun onFetchControl(){
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            mControlMessagePresenter.getSystemControlClear()
        }
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
     * 获取当前屏幕位置
     */
    fun getScreenPosition():Int{
        if (activity is MainActivity){
            screenPos=(activity as MainActivity).getCurrentScreenPos()
        }
        if (activity is ResourceCenterActivity){
            screenPos=(activity as ResourceCenterActivity).getCurrentScreenPos()
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

    /**
     * 跳转活动(关闭已经打开的)
     */
    fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
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
    fun setClearExamPaper(){
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
            EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
            mControlMessagePresenter.editGrade(mUser?.grade!!)
            //上传完之后 删除控制删除消息
            val controlClearIds=SPUtil.getListInt("ControlClear")
            mControlMessagePresenter.deleteSystemClearMessage(controlClearIds)
        }
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
            onRefreshData()
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.USER_CHANGE_EVENT->{
                mUser= SPUtil.getObj("user", User::class.java)
                grade=mUser?.grade!!
                setClearHomework()
                setClearExamPaper()
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
