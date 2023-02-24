package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseAppCompatActivity(), IContractView.IAccountInfoView {

    private val presenter=AccountInfoPresenter(this)
    private var nickname=""

    private var popupGradeWindow: PopupList?=null
    private var grades= mutableListOf<PopupBean>()
    private var grade=1

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
        showToast("修改成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }

    override fun onEditGradeSuccess() {
        showToast("修改成功")
        mUser?.grade=grade
        tv_grade.text = DataBeanManager.grades[grade-1].desc
    }


    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        grades=DataBeanManager.popupGrades
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_account_id.text=accountId.toString()
            tv_phone.text =  telNumber.substring(0,3)+"****"+telNumber.substring(7,11)
            tv_birthday.text=DateUtils.intToStringDataNoHour(birthdayTime)
            tv_parent.text=parentName
            tv_parent_name.text=parentNickname
            tv_parent_phone.text=parentTel
            tv_address.text=parentAddr
            if(grade>0)
                tv_grade.text=grades[grade-1].name
        }

        btn_edit_psd.setOnClickListener {
            customStartActivity(Intent(this,AccountRegisterActivity::class.java).setFlags(2))
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_grade.setOnClickListener {
            selectorGrade()
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
     * 年级选择
     */
    private fun selectorGrade(){
        if (popupGradeWindow==null)
        {
            popupGradeWindow= PopupList(this,grades,btn_edit_grade,15).builder()
            popupGradeWindow?.setOnSelectListener { item ->
                tv_grade.text=item.name
                grade=item.id
                presenter.editGrade(grade)
            }
        }
        else{
            popupGradeWindow?.show()
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
        SPUtil.putObj("user", mUser!!)
    }
}