package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.SchoolSelectDialog
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.RegisterOrFindPsdPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_account_register.btn_code
import kotlinx.android.synthetic.main.ac_account_register.btn_register
import kotlinx.android.synthetic.main.ac_account_register.ed_code
import kotlinx.android.synthetic.main.ac_account_register.ed_name
import kotlinx.android.synthetic.main.ac_account_register.ed_password
import kotlinx.android.synthetic.main.ac_account_register.ed_phone
import kotlinx.android.synthetic.main.ac_account_register.ed_user
import kotlinx.android.synthetic.main.ac_account_register.et_parent
import kotlinx.android.synthetic.main.ac_account_register.et_parent_name
import kotlinx.android.synthetic.main.ac_account_register.et_parent_phone
import kotlinx.android.synthetic.main.ac_account_register.ll_date_resiter
import kotlinx.android.synthetic.main.ac_account_register.ll_name
import kotlinx.android.synthetic.main.ac_account_register.ll_school
import kotlinx.android.synthetic.main.ac_account_register.ll_user
import kotlinx.android.synthetic.main.ac_account_register.tv_date
import kotlinx.android.synthetic.main.ac_account_register.tv_school


/**
 *  //2. 帐号规则 4 - 12 位字母、数字
//3. 密码规则 6 - 20 位字母、数字
//4. 姓名规则 2 - 5 位中文
//5. 手机号码规则 11 位有效手机号
//6. 验证码规则数字即可
 */
class AccountRegisterActivity : BaseAppCompatActivity(), IContractView.IRegisterOrFindPsdView {

    private var presenter:RegisterOrFindPsdPresenter?=null
    private var countDownTimer: CountDownTimer? = null
    private var flags = 0
    private var brithday=0L
    private var school=0
    private var schoolSelectDialog:SchoolSelectDialog?=null

    override fun onListSchools(list: MutableList<SchoolBean>) {
        selectorSchool(list)
    }

    override fun onSms() {
        showToast(R.string.toast_message_code_success)
        showCountDownView()
    }

    override fun onRegister() {
        showToast(R.string.toast_register_success)
        setIntent()
    }
    override fun onFindPsd() {
        showToast(R.string.toast_set_password_success)
        setIntent()
    }

    override fun onEditPsd() {
        showToast(R.string.toast_edit_password_success)
        setIntent()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_register
    }

    override fun initData() {
        fetchCommonData()
        initChangeScreenData()
        flags=intent.flags
        if (flags==0){
            if (!NetworkUtil(this).isNetworkConnected()){
                showToast(R.string.net_work_error)
            }
        }
    }

    override fun initChangeScreenData() {
        presenter= RegisterOrFindPsdPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        when (flags) {
            2 -> {
                setPageTitle(R.string.edit_password)
                disMissView(ll_name,ll_date_resiter,ll_user,ll_school)
                btn_register.setText(R.string.commit)
            }
            1 -> {
                setPageTitle(R.string.find_password)
                ed_user.setText(SPUtil.getString("account"))
                disMissView(ll_name,ll_date_resiter,ll_school)
                btn_register.setText(R.string.commit)
            }
            else -> {
                setPageTitle(R.string.register)
            }
        }

        ll_date_resiter.setOnClickListener {
            DateDialog(this).builder().setOnDateListener { dateStr, dateTim ->
                brithday=dateTim
                tv_date.text=dateStr
            }
        }

        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString().trim()
            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            presenter?.sms(phone)
        }

        tv_school.setOnClickListener {
            mCommonPresenter.getCommonSchool()
        }

        btn_register.setOnClickListener {

            val account=ed_user.text.toString().trim()
            val psd=ed_password.text.toString().trim()
            val name=ed_name.text.toString().trim()
            val phone=ed_phone.text.toString().trim()
            val code=ed_code.text.toString().trim()
            val parentName=et_parent_name.text.toString().trim()
            val parent=et_parent.text.toString().trim()
            val parentPhone=et_parent_phone.text.toString().trim()
            val birthdayStr=tv_date.text.toString().trim()

            if (psd.isEmpty()) {
                showToast(R.string.login_input_password_hint)
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                showToast(R.string.toast_input_phone)
                return@setOnClickListener
            }

            if (code.isEmpty()) {
                showToast(R.string.toast_input_message_code)
                return@setOnClickListener
            }

            if (!ToolUtils.isLetterOrDigit(psd, 6, 20)) {
                showToast(getString(R.string.psw_tip))
                return@setOnClickListener
            }

            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }

            when (flags) {
                0 -> {
                    if (account.isEmpty()) {
                        showToast(R.string.toast_input_account)
                        return@setOnClickListener
                    }
                    if (name.isEmpty()) {
                        showToast(R.string.toast_input_name)
                        return@setOnClickListener
                    }
                    if (birthdayStr.isEmpty()) {
                        showToast(R.string.toast_input_birthday)
                        return@setOnClickListener
                    }
                    if (!ToolUtils.isLetterOrDigit(account, 4, 12)) {
                        showToast(getString(R.string.user_tip))
                        return@setOnClickListener
                    }
                    if (parentName.isEmpty()) {
                        showToast(R.string.toast_input_parent)
                        return@setOnClickListener
                    }
                    if (parent.isEmpty()) {
                        showToast(R.string.toast_input_parent_name)
                        return@setOnClickListener
                    }
                    if (parentPhone.isEmpty()) {
                        showToast(R.string.toast_input_parent_phone)
                        return@setOnClickListener
                    }
                    if (school==0){
                        showToast(R.string.toast_select_school)
                        return@setOnClickListener
                    }

                    val map=HashMap<String,Any>()
                    map["account"]=account
                    map["password"]=MD5Utils.digest(psd)
                    map["nickname"]=name
                    map["code"]=code
                    map["telNumber"]=phone
                    map["parentName"]=parentName
                    map["parentNickname"]=parent
                    map["parentTel"]=parentPhone
                    map["birthdayTime"]=brithday
                    map["schoolId"]=school
                    presenter?.register(map)
                }
                1 -> {
                    if (account.isEmpty()) {
                        showToast(R.string.toast_input_account)
                        return@setOnClickListener
                    }
                    presenter?.findPsd("2",account,MD5Utils.digest(psd),phone, code)
                }
                else -> {
                    presenter?.editPsd(MD5Utils.digest(psd),code)
                }
            }

        }

    }

    //验证码倒计时刷新ui
    private fun showCountDownView() {
        btn_code.isEnabled = false
        btn_code.isClickable = false
        countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                runOnUiThread {
                    btn_code.isEnabled = true
                    btn_code.isClickable = true
                    btn_code.setText(R.string.get_message_code)
                }

            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    btn_code.text = "${millisUntilFinished / 1000}s"
                }
            }
        }.start()

    }

    /**
     * 选择学校
     */
    private fun selectorSchool(schools:MutableList<SchoolBean>){
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,schools).builder()
            schoolSelectDialog?.setOnDialogClickListener{
                school=it.id
                tv_school.text=it.name
            }
        }
        else{
            schoolSelectDialog?.show()
        }
    }

    private fun setIntent(){
        val intent = Intent()
        intent.putExtra("user", ed_user.text.toString())
        intent.putExtra("psw", ed_password.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onNetworkConnectionSuccess() {
        fetchCommonData()
    }
}
