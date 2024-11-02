package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.LoginPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_login_user.btn_login
import kotlinx.android.synthetic.main.ac_account_login_user.ed_psw
import kotlinx.android.synthetic.main.ac_account_login_user.ed_user
import kotlinx.android.synthetic.main.ac_account_login_user.ll_tips
import kotlinx.android.synthetic.main.ac_account_login_user.tv_find_psd
import kotlinx.android.synthetic.main.ac_account_login_user.tv_register

class AccountLoginActivity:BaseAppCompatActivity(), IContractView.ILoginView {

    private val presenter=LoginPresenter(this,1)
    private var token=""
    private var statusBarValue=0

    override fun getLogin(user: User?) {
        token= user?.token.toString()
        SPUtil.putString("token",token)
        presenter.accounts()
    }

    override fun getAccount(user: User?) {
        user?.token=token
        SPUtil.putObj("user",user!!)

        val intent = Intent()
        intent.putExtra("token", token)
        intent.putExtra("userId", user.accountId)
        intent.action = Constants.LOGIN_BROADCAST_EVENT
        sendBroadcast(intent)
        //刷新公共方法中的登录信息
        MethodManager.getUser()
        gotoMainActivity()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_login_user
    }

    override fun initData() {
        initDialog(1)
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        val account=SPUtil.getString("account")

        statusBarValue=MethodManager.getStatusBarValue()
        MethodManager.setStatusBarValue(Constants.STATUS_BAR_SHOW)

        ed_user.setText(account)

        tv_register.setOnClickListener {
            activityResultLauncher.launch(Intent(this, AccountRegisterActivity::class.java).setFlags(0))
        }

        tv_find_psd.setOnClickListener {
            activityResultLauncher.launch(Intent(this, AccountRegisterActivity::class.java).setFlags(1))
        }

        btn_login.setOnClickListener {
            val account = ed_user.text.toString()
            val psdStr=ed_psw.text.toString()
            val password = MD5Utils.digest(psdStr)

            SPUtil.putString("account",account)

            val map=HashMap<String,Any>()
            map ["account"]=account
            map ["password"]=password
            map ["role"]= 2
            presenter.login(map)
        }

        if (MethodManager.isLogin()){
            gotoMainActivity()
        }
    }

    private fun gotoMainActivity(){
        MethodManager.setStatusBarValue(statusBarValue)
        val intent=Intent(this,MainActivity::class.java)
        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
        intent.flags=Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
        ActivityManager.getInstance().finishOthers(MainActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtil(this).isNetworkConnected()) {
            fetchCommonData()
            disMissView(ll_tips)
        }
        else{
            showView(ll_tips)
        }
    }

    /**
     * 开始通知回调
     */
    private val activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode==Activity.RESULT_OK){
            val data=it.data
            ed_user.setText(data?.getStringExtra("user"))
            ed_psw.setText(data?.getStringExtra("psw"))
        }
    }

    override fun onNetworkConnectionSuccess() {
        fetchCommonData()
    }
}