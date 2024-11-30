package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
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
import com.bll.lnkstudy.mvp.view.IContractView
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
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView {

    private var presenter:AccountInfoPresenter?=null
    private var nickname = ""

    private var school=0
    private var schoolBean:SchoolBean?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null
    private var phone=""
    private var birthday=0L

    override fun onLogout() {
    }

    override fun onSms() {
        showToast("短信发送成功")
    }

    override fun onCheckSuccess() {
        editPhone()
    }

    override fun onEditPhone() {
        mUser?.telNumber=phone
        tv_phone.text=phone
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
        fetchCommonData()
        initChangeScreenData()
        school=mUser?.schoolId!!
        birthday=mUser?.birthdayTime!!
    }

    override fun initChangeScreenData() {
        presenter = AccountInfoPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.my_account)

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text = telNumber.substring(0, 3) + "****" + telNumber.substring(7, 11)
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthday)
            tv_parent.text = parentName
            tv_parent_name.text = parentNickname
            tv_parent_phone.text = parentTel
            tv_provinces.text = schoolProvince
            tv_city.text = schoolCity
            tv_school_name.text = schoolName
            tv_area.text = schoolArea
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_school.setOnClickListener {
            editSchool()
        }

        btn_edit_birthday.setOnClickListener {
            DateDialog(this,birthday).builder().setOnDateListener { dateStr, dateTim ->
                birthday=dateTim
                presenter?.editBirthday(birthday)
            }
        }

        btn_edit_phone.setOnClickListener {
            presenter?.sms(mUser?.telNumber!!)
            InputContentDialog(this,1,"请输入验证码",1).builder().setOnDialogClickListener{
                presenter?.checkPhone(it)
            }
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent(R.string.account_is_logout_tips).builder()
                .setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        MethodManager.logout(this@AccountInfoActivity)
                    }
                })
        }

        btn_edit_parent.setOnClickListener {
            AccountEditParentDialog(this,mUser!!.parentName,mUser!!.parentNickname,mUser!!.parentTel).builder().setOnDialogClickListener{
                name,nickname,phone->
                tv_parent.text = name
                tv_parent_name.text = nickname
                tv_parent_phone.text = phone
                presenter?.editParent(name, nickname, phone)
            }
        }

    }

    /**
     * 修改学校
     */
    private fun editSchool() {
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,getCurrentScreenPos(),DataBeanManager.schools).builder()
            schoolSelectDialog?.setOnDialogClickListener{
                school=it.id
                presenter?.editSchool(it.id)
                for (item in DataBeanManager.schools){
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
                presenter?.sms(phone)
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

    override fun onDestroy() {
        super.onDestroy()
        SPUtil.putObj("user", mUser!!)
        EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
    }

}