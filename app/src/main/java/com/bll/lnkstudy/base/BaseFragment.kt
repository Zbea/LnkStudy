package com.bll.lnkstudy.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.ProgressDialog
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.net.ExceptionHandle
import com.bll.lnkstudy.net.IBaseView
import com.bll.lnkstudy.ui.activity.*
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SToast
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


abstract class BaseFragment : Fragment(), EasyPermissions.PermissionCallbacks, IBaseView {

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
    var tvPageTitle: TextView? = null
    var ivBack: ImageView? = null
    var ivHomework: ImageView? = null
    var ivManagers: ImageView? = null
    var mDialog: ProgressDialog? = null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var llSearch: LinearLayout?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null != mView) {
            val parent: ViewGroup? = container
            parent?.removeView(parent)
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
        isViewPrepare = true
        initCommonTitle()
        initView()
        mDialog = ProgressDialog(activity)
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

    fun showToast(s:String){
        SToast.showText(s)
    }

    fun showLog(s:String){
        Log.d("debug",s)
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

    @SuppressLint("WrongViewCast")
    fun initCommonTitle() {
        tvPageTitle = requireView().findViewById(R.id.tv_title)
        ivBack=requireView().findViewById(R.id.iv_back)
        ivHomework=requireView().findViewById(R.id.iv_homework)
        ivManagers = requireView().findViewById(R.id.iv_note_manager)
        llSearch= requireView().findViewById(R.id.ll_search)
    }

    fun setTitle(pageTitle: String) {
        if (tvPageTitle != null) {
            tvPageTitle?.text = pageTitle
        }
    }

    fun showSearch(isShow:Boolean) {
        if (isShow){
            showView(llSearch)
        }
        else{
            disMissView(llSearch)
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

    fun showHomeworkView() {
        showView(ivHomework)
    }

    fun showNoteView() {
        showView(ivManagers)
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
            showToast("本书已经打开,请勿重复打开")

        }
        else{
            var intent=Intent(activity, BookDetailsActivity::class.java)
            intent.putExtra("book_id",book.id)
            startActivity(intent)
        }
    }

    /**
     * 跳转作业本
     */
    fun gotoHomeworkDrawing(item:HomeworkType){
        if (ActivityManager.getInstance().checkHomeworkDrawingisExist(item)){
            showToast(item.name+"已经打开,请勿重复打开")
        }
        else{
            var bundle= Bundle()
            bundle.putSerializable("homework",item)
            var intent=Intent(context, HomeworkDrawingActivity::class.java)
            intent.putExtra("homeworkBundle",bundle)
            startActivity(intent)
        }
    }

    /**
     * 跳转画本
     */
    fun gotoPaintingDrawing(type: Int){
        if (ActivityManager.getInstance().checkPaintingDrawingIsExist(type))
        {
            showToast("画本已经打开,请勿重复打开")
        }
        else{
            var intent=Intent(activity, PaintingDrawingActivity::class.java)
            intent.flags=type
            startActivity(intent)
        }
    }

    /**
     * 跳转考卷
     */
    fun gotoPaperDrawing(flags: Int,mCourseId:Int,mTypeId:Int){
        if (ActivityManager.getInstance().checkPaperDrawingIsExist(flags,mCourseId,mTypeId))
        {
            showToast("页面已经打开,请勿重复打开")
        }
        else{
            var intent=Intent(activity, PaperDrawingActivity::class.java)
            intent.putExtra("courseId",mCourseId)
            intent.putExtra("categoryId",mTypeId)
            intent.flags=flags
            if (flags==1)
                intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
            startActivity(intent)
        }
    }

    /**
     * 跳转笔记书写页面
     */
    fun gotoDrawActivity(note: Note) {

        if (ActivityManager.getInstance().checkNoteDrawing(note))
        {
            showToast("笔记已经打开,请勿重复打开")
        }
        else{
            var intent = Intent(activity, NoteDrawingActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable("note", note)
            intent.putExtra("noteBundle", bundle)
            startActivity(intent)
        }
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
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            Toast.makeText(activity, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(requireActivity())
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
        if (mView==null||activity==null)return
        SToast.showText("连接超时,请重新登陆")
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")
        Handler().postDelayed(Runnable {
            startActivity(Intent(activity, AccountLoginActivity::class.java))
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
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
        SToast.showText( msg)
    }
    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
    }
    override fun onComplete() {
    }


}
