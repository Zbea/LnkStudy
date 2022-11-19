package com.bll.lnkstudy.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.LoginPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_login_user.*
import pub.devrel.easypermissions.EasyPermissions

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
        val intent=Intent(this,LauncherActivity::class.java)
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
        startActivity(intent)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_login_user
    }

    override fun initData() {
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        EasyPermissions.requestPermissions(this,"请求权限",1,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECORD_AUDIO
        )

        ed_user.setText("gq")
        ed_psw.setText("123456")


        tv_register.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(0), 0)
        }

        tv_find_psd.setOnClickListener {
            startActivityForResult(Intent(this, AccountRegisterActivity::class.java).setFlags(1), 0)
        }

        btn_login.setOnClickListener {

            var account = ed_user.text.toString()
            var password = MD5Utils.digest("" + (System.currentTimeMillis() / 1000).toInt() + "_" + MD5Utils.digest(ed_psw.text.toString()))
            var timestamp = (System.currentTimeMillis() / 1000).toInt()
            var role = 2

            var map=HashMap<String,String>()
            map ["account"]=account
            map ["password"]=password
            map ["timestamp"]= timestamp.toString()
            map ["role"]= role.toString()

            presenter.login(account,password, timestamp,role)

        }

        val tokenStr=SPUtil.getString("token")

        if (!tokenStr.isNullOrEmpty() && mUser!=null)
        {
            val intent=Intent(this,LauncherActivity::class.java)
            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtil.isNetworkAvailable(this)) {
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