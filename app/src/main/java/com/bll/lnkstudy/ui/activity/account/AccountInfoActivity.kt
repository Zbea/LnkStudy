package com.bll.lnkstudy.ui.activity.account

import android.annotation.SuppressLint
import android.content.Intent
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.AccountEditParentDialog
import com.bll.lnkstudy.dialog.AccountEditPhoneDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.SchoolSelectDialog
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SmsPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISmsView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_birthday
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_name
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_parent
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_phone
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_school
import kotlinx.android.synthetic.main.ac_account_info.btn_logout
import kotlinx.android.synthetic.main.ac_account_info.tv_area
import kotlinx.android.synthetic.main.ac_account_info.tv_birthday
import kotlinx.android.synthetic.main.ac_account_info.tv_city
import kotlinx.android.synthetic.main.ac_account_info.tv_name
import kotlinx.android.synthetic.main.ac_account_info.tv_parent
import kotlinx.android.synthetic.main.ac_account_info.tv_parent_name
import kotlinx.android.synthetic.main.ac_account_info.tv_parent_phone
import kotlinx.android.synthetic.main.ac_account_info.tv_phone
import kotlinx.android.synthetic.main.ac_account_info.tv_provinces
import kotlinx.android.synthetic.main.ac_account_info.tv_school_name
import kotlinx.android.synthetic.main.ac_account_info.tv_user
import kotlinx.android.synthetic.main.common_title.tv_btn

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView,ISmsView {

    private var presenter:AccountInfoPresenter?=null
    private var smsPresenter:SmsPresenter?=null
    private var nickname = ""
    private var school=0
    private var schoolBean:SchoolBean?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null
    private var phone=""
    private var birthday=0L
    private var type=1

    override fun onSms() {
        showToast("短信发送成功")
        InputContentDialog(this,1,"输入验证码",1).builder().setOnDialogClickListener{
            smsPresenter?.checkPhone(it)
        }
    }
    override fun onCheckSuccess() {
        onClick()
    }

    override fun onListSchools(list: MutableList<SchoolBean>) {
        selectorSchool(list)
    }
    override fun onLogout() {
    }
    override fun onEditPhone() {
        mUser?.telNumber=phone
        tv_phone.text=phone
        saveUser()
    }
    override fun onEditBirthday() {
        mUser?.birthdayTime=birthday
        tv_birthday.text=DateUtils.intToStringDataNoHour(birthday)
        saveUser()
    }
    override fun onEditNameSuccess() {
        mUser?.nickname = nickname
        tv_name.text = nickname
        saveUser()
    }
    override fun onEditSchool() {
        mUser?.schoolId = schoolBean?.id
        mUser?.schoolProvince=schoolBean?.province
        mUser?.schoolCity=schoolBean?.city
        mUser?.schoolArea=schoolBean?.area
        mUser?.schoolName=schoolBean?.schoolName
        tv_provinces.text = schoolBean?.province
        tv_city.text = schoolBean?.city
        tv_school_name.text = schoolBean?.schoolName
        tv_area.text = schoolBean?.area
        saveUser()
    }
    override fun onEditParent() {
        mUser?.parentName=tv_parent.text.toString()
        mUser?.parentNickname=tv_parent_name.text.toString()
        mUser?.parentTel=tv_parent_phone.text.toString()
        saveUser()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()
        school=mUser?.schoolId!!
        birthday=mUser?.birthdayTime!!
    }

    override fun initChangeScreenData() {
        presenter = AccountInfoPresenter(this,getCurrentScreenPos())
        smsPresenter=SmsPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.my_account)
        showView(tv_btn)

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text = if (telNumber.isNotEmpty())telNumber.substring(0, 3) + "****" + telNumber.substring(7, 11) else ""
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthday)
            tv_parent.text = parentName
            tv_parent_name.text = parentNickname
            tv_parent_phone.text = parentTel
            tv_provinces.text = schoolProvince
            tv_city.text = schoolCity
            tv_school_name.text = schoolName
            tv_area.text = schoolArea
        }

        tv_btn.text="一键功能"
        tv_btn.setOnClickListener {
            customStartActivity(Intent(this,AccountSettingDataActivity::class.java))
        }

        btn_edit_name.setOnClickListener {
            getSms(1)
        }

        btn_edit_birthday.setOnClickListener {
            getSms(2)
        }

        btn_edit_phone.setOnClickListener {
            getSms(3)
        }

        btn_edit_school.setOnClickListener {
            getSms(4)
        }

        btn_edit_parent.setOnClickListener {
            getSms(5)
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent(R.string.account_is_logout_tips).builder()
                .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun ok() {
                        MethodManager.logout(this@AccountInfoActivity)
                    }
                })
        }

    }

    private fun getSms(type:Int){
        this.type=type
        smsPresenter?.sms(mUser?.telNumber!!)
    }

    private fun onClick(){
        when(type){
            1->{
                editName()
            }
            2->{
                DateDialog(this,birthday).builder().setOnDateListener { dateStr, dateTim ->
                    birthday=dateTim
                    presenter?.editBirthday(birthday)
                }
            }
            3->{
                editPhone()
            }
            4->{
                mCommonPresenter.getCommonSchool()
            }
            5->{
                AccountEditParentDialog(this,mUser!!.parentName,mUser!!.parentNickname,mUser!!.parentTel).builder().setOnDialogClickListener{
                        name,nickname,phone->
                    tv_parent.text = name
                    tv_parent_name.text = nickname
                    tv_parent_phone.text = phone
                    presenter?.editParent(name, nickname, phone)
                }
            }
        }
    }

    /**
     * 修改学校
     */
    private fun selectorSchool(schools:MutableList<SchoolBean>) {
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,schools).builder()
            schoolSelectDialog?.setOnDialogClickListener{
                school=it.id
                presenter?.editSchool(it.id)
                for (item in schools){
                    if (item.id==school)
                        schoolBean=item
                }
            }
        }
        else{
            schoolSelectDialog?.show()
        }
    }

    private fun editPhone(){
        AccountEditPhoneDialog(this).builder().setOnDialogClickListener(object : AccountEditPhoneDialog.OnDialogClickListener {
            override fun onClick(code: String, phone: String) {
                this@AccountInfoActivity.phone=phone
                presenter?.editPhone(code, phone)
            }
            override fun onPhone(phone: String) {
                smsPresenter?.sms(phone)
            }
        })
    }

    /**
     * 修改名称
     */
    private fun editName() {
        InputContentDialog(this, screenPos, tv_name.text.toString()).builder()
            .setOnDialogClickListener { string ->
                nickname = string
                presenter?.editName(nickname)
            }
    }

    private fun saveUser(){
        SPUtil.putObj("user", mUser!!)
    }

}