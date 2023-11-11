package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.LoginPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import kotlinx.android.synthetic.main.ac_account_login_user.*

class AccountLoginActivity:BaseAppCompatActivity(), IContractView.ILoginView {

    private val presenter=LoginPresenter(this)
    private var token=""

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

        gotoMainActivity()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_login_user
    }

    override fun initData() {
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        ed_user.setText("zhufeng")
        ed_psw.setText("123456")

        tv_register.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(0), 0)
        }

        tv_find_psd.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(1), 0)
        }

        btn_login.setOnClickListener {
            val account = ed_user.text.toString()
            val password = MD5Utils.digest(ed_psw.text.toString())

            val map=HashMap<String,Any>()
            map ["account"]=account
            map ["password"]=password
            map ["role"]= 2
            presenter.login(map)
        }


        val tokenStr=SPUtil.getString("token")

        if (tokenStr.isNotEmpty() && mUser!=null)
        {
            gotoMainActivity()
        }

    }

    private fun gotoMainActivity(){
        val intent=Intent(this,MainActivity::class.java)
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
        intent.flags=Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
        ActivityManager.getInstance().finishOthers(MainActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtil(this).isNetworkConnected()) {
            disMissView(tv_tips)
        } else {
            showView(tv_tips)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            ed_user.setText(data?.getStringExtra("user"))
            ed_psw.setText(data?.getStringExtra("psw"))
        }
    }


}