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
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SmsPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISmsView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
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
    private var type=0

    override fun getAccount(user: User) {
        setAccountInfo()
    }

    override fun onSms() {
        showToast("短信发送成功")
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
        tv_phone.text=getPhoneStr(phone)
        btn_edit_phone.text="修改号码"
        showView(tv_btn)
    }
    override fun onEditBirthday() {
        mUser?.birthdayTime=birthday
        tv_birthday.text=DateUtils.intToStringDataNoHour(birthday)
    }
    override fun onEditNameSuccess() {
        mUser?.nickname = nickname
        tv_name.text = nickname
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
    }
    override fun onEditParent() {
        mUser?.parentName=tv_parent.text.toString()
        mUser?.parentNickname=tv_parent_name.text.toString()
        mUser?.parentTel=tv_parent_phone.text.toString()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()

        if (NetworkUtil.isNetworkConnected()){
            presenter?.accounts()
        }
        else{
            school=mUser?.schoolId!!
            birthday=mUser?.birthdayTime!!
        }
    }

    override fun initChangeScreenData() {
        presenter = AccountInfoPresenter(this,getCurrentScreenPos())
        smsPresenter=SmsPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.my_account)

        if (ToolUtils.isPhoneNum(mUser?.telNumber)){
            showView(tv_btn)
        }

        setAccountInfo()

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
        btn_edit_phone.text=if (ToolUtils.isPhoneNum(mUser?.telNumber)) "修改号码" else "绑定号码"
        btn_edit_phone.setOnClickListener {
            if (!ToolUtils.isPhoneNum(mUser?.telNumber)){
                AccountEditPhoneDialog(this).builder().setOnDialogClickListener(object : AccountEditPhoneDialog.OnDialogClickListener {
                    override fun onClick(code: String, phone: String) {
                        this@AccountInfoActivity.phone=phone
                        presenter?.bindPhone(code, phone)
                    }
                    override fun onPhone(phone: String) {
                        type=0
                        smsPresenter?.sms(phone)
                    }
                })
            }else{
                getSms(3)
            }
        }

        btn_edit_school.setOnClickListener {
            getSms(4)
        }

        btn_edit_parent.setOnClickListener {
            getSms(5)
        }

        btn_logout.setOnClickListener {
            if (mUser?.telNumber.isNullOrEmpty()){
                logout()
            }else{
                getSms(6)
            }
        }

    }

    private fun setAccountInfo(){
        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text = getPhoneStr(telNumber)
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthdayTime)
            tv_parent.text = parentName
            tv_parent_name.text = parentNickname
            tv_parent_phone.text = parentTel
            tv_provinces.text = schoolProvince
            tv_city.text = schoolCity
            tv_school_name.text = schoolName
            tv_area.text = schoolArea
        }
    }

    private fun getPhoneStr(phone:String):String{
        return if (ToolUtils.isPhoneNum(phone)) phone.substring(0, 3) + "****" + phone.substring(7, 11) else ""
    }

    private fun getSms(type:Int){
        this.type=type
        if (!ToolUtils.isPhoneNum(mUser?.telNumber)){
            showToast("请先绑定手机号")
        }
        else{
            AccountEditPhoneDialog(this,mUser?.telNumber!!).builder().setOnDialogClickListener(object : AccountEditPhoneDialog.OnDialogClickListener {
                override fun onClick(code: String, phone: String) {
                    smsPresenter?.checkPhone(code)
                }
                override fun onPhone(phone: String) {
                    smsPresenter?.sms(phone)
                }
            })
        }
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
            6->{
                logout()
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

    private fun logout(){
        CommonDialog(this).setContent(R.string.account_is_logout_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun ok() {
                    MethodManager.logout(this@AccountInfoActivity)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        saveUser()
    }
}