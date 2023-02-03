package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseAppCompatActivity(), IContractView.IAccountInfoView {

    private val presenter=AccountInfoPresenter(this)
    private var nickname=""

    override fun onLogout() {
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")
        Handler().postDelayed(Runnable {
            val intent=Intent(this, AccountLoginActivity::class.java)
            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
            startActivity(intent)
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
    }

    override fun onEditNameSuccess() {
        showToast("修改姓名成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }


    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        tv_user.text = mUser?.account
        tv_name.text = mUser?.nickname
        tv_phone.text =  mUser?.telNumber?.substring(0,3)+"****"+mUser?.telNumber?.substring(7,11)
        tv_birthday.text=DateUtils.intToStringDataNoHour(mUser?.birthdayTime?.times(1000) ?: 0)
        tv_parent.text=mUser?.parentName
        tv_parent_name.text=mUser?.parentNickname
        tv_parent_phone.text=mUser?.parentTel
        tv_address.text=mUser?.parentAddr

        btn_edit_psd.setOnClickListener {
            customStartActivity(Intent(this,AccountRegisterActivity::class.java).setFlags(2))
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent("确认退出登录？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    presenter.logout()
                }
            })
        }

    }

    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,screenPos,tv_name.text.toString()).builder()?.setOnDialogClickListener { string ->
            nickname = string
            presenter.editName(nickname)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }


}