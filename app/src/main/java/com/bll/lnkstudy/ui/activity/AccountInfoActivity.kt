package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.AccountEditSchoolDialog
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
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView {

    private val presenter = AccountInfoPresenter(this)
    private var nickname = ""

    private var popupGradeWindow: PopupList? = null
    private var grades = mutableListOf<PopupBean>()
    private var grade = 0
    private var schoolName = ""
    private var provinceStr = ""
    private var areaStr = ""

    override fun onLogout() {

    }

    override fun onEditNameSuccess() {
        showToast(R.string.toast_edit_success)
        mUser?.nickname = nickname
        tv_name.text = nickname
    }

    override fun onEditGradeSuccess() {
        showToast(R.string.toast_edit_success)
        mUser?.grade = grade
        tv_grade_str.text = DataBeanManager.grades[grade - 1].desc
    }

    override fun onEditSchoolSuccess() {
        showToast(R.string.toast_edit_success)
        mUser?.addr = provinceStr
        mUser?.addrInfo = areaStr
        mUser?.schoolName = schoolName
        tv_province.text = provinceStr.split(",")[0]
        tv_city.text = provinceStr.split(",")[1]
        tv_school_name.text = schoolName
        tv_area.text = areaStr
    }


    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        grades = DataBeanManager.popupGrades
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle(R.string.my_account)

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_account_id.text = accountId.toString()
            tv_phone.text = telNumber.substring(0, 3) + "****" + telNumber.substring(7, 11)
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthdayTime)
            tv_parent.text = parentName
            tv_parent_name.text = parentNickname
            tv_parent_phone.text = parentTel
            tv_address.text = parentAddr
            if (addr.isNotEmpty()) {
                tv_province.text = addr.split(",")[0]
                tv_city.text = addr.split(",")[1]
            }
            tv_school_name.text = schoolName
            tv_area.text = addrInfo
            tv_grade_str.text = grades[grade - 1].name
        }

        btn_edit_psd.setOnClickListener {
            customStartActivity(Intent(this, AccountRegisterActivity::class.java).setFlags(2))
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_grade.setOnClickListener {
            selectorGrade()
        }

        btn_edit_school.setOnClickListener {
            editSchool()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent(R.string.account_is_logout_tips).builder()
                .setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }

                    override fun ok() {
                        SPUtil.putString("token", "")
                        SPUtil.removeObj("user")
                        Handler().postDelayed(Runnable {
                            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
                            val intent = Intent(this@AccountInfoActivity, AccountLoginActivity::class.java)
                            intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                            startActivity(intent)
                            finish()
                        }, 500)
                    }
                })
        }

    }

    /**
     * 年级选择
     */
    private fun selectorGrade() {
        if (popupGradeWindow == null) {
            popupGradeWindow = PopupList(this, grades, btn_edit_grade, 15).builder()
            popupGradeWindow?.setOnSelectListener { item ->
                tv_grade_str.text = item.name
                grade = item.id
                presenter.editGrade(grade)
            }
        } else {
            popupGradeWindow?.show()
        }
    }

    /**
     * 修改学校
     */
    private fun editSchool() {
        AccountEditSchoolDialog(this, getCurrentScreenPos(), mUser!!).builder()
            ?.setOnDialogClickListener { provinceStr: String, areaStr: String, schoolName: String ->
                this.provinceStr = provinceStr
                this.areaStr = areaStr
                this.schoolName = schoolName
                val map = HashMap<String, Any>()
                map["addr"] = provinceStr
                map["addrInfo"] = areaStr
                map["schoolName"] = schoolName
                presenter.editSchool(map)
            }
    }

    /**
     * 修改名称
     */
    private fun editName() {
        InputContentDialog(this, screenPos, tv_name.text.toString()).builder()
            ?.setOnDialogClickListener { string ->
                nickname = string
                presenter.editName(nickname)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtil.putObj("user", mUser!!)
        EventBus.getDefault().post(Constants.USER_EVENT)
    }
}