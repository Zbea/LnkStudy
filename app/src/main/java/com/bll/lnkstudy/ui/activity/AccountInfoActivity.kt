package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.PrivacyPassword
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SchoolPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISchoolView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_account_info.*
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView ,ISchoolView{

    private var mSchoolPresenter:SchoolPresenter?=null
    private var presenter:AccountInfoPresenter?=null
    private var nickname = ""

    private var grades = mutableListOf<PopupBean>()
    private var grade = 1
    private var schools= mutableListOf<SchoolBean>()
    private var school=0
    private var schoolBean:SchoolBean?=null
    private var privacyPassword: PrivacyPassword?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null

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
        tv_grade_str.text = DataBeanManager.getGradeStr(grade)
        EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
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

    override fun onListSchools(list: MutableList<SchoolBean>) {
        schools=list
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeData()
        grades = DataBeanManager.popupGrades(grade)
        school=mUser?.schoolId!!
        if (NetworkUtil(this).isNetworkConnected())
            mSchoolPresenter?.getCommonSchool()
    }

    override fun initChangeData() {
        mSchoolPresenter=SchoolPresenter(this,getCurrentScreenPos())
        presenter = AccountInfoPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.my_account)

        privacyPassword=MethodManager.getPrivacyPassword()

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text = telNumber.substring(0, 3) + "****" + telNumber.substring(7, 11)
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthdayTime)
            tv_parent.text = parentName
            tv_parent_name.text = parentNickname
            tv_parent_phone.text = parentTel
            if (grade != 0 && grades.size > 0){
                tv_grade_str.text = DataBeanManager.getGradeStr(grade)
            }
            tv_provinces.text = schoolProvince
            tv_city.text = schoolCity
            tv_school_name.text = schoolName
            tv_area.text = schoolArea
        }

        if (privacyPassword!=null){
            showView(tv_check_pad)
            if (privacyPassword?.isSet == true){
                btn_psd_check.text=getString(R.string.cancel_password)
            }
            else{
                btn_psd_check.text=getString(R.string.set_password)
            }
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

        btn_psd_check.setOnClickListener {
            setPassword()
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

    }

    /**
     * 设置查看密码
     */
    private fun setPassword(){
        if (privacyPassword==null){
            PrivacyPasswordCreateDialog(this).builder().setOnDialogClickListener{
                privacyPassword=it
                showView(tv_check_pad)
                btn_psd_check.text=getString(R.string.cancel_password)
                MethodManager.savePrivacyPassword(privacyPassword)
                EventBus.getDefault().post(Constants.PASSWORD_EVENT)
            }
        }
        else{
            PrivacyPasswordDialog(this).builder()?.setOnDialogClickListener{
                privacyPassword?.isSet=!privacyPassword?.isSet!!
                btn_psd_check.text=if (privacyPassword?.isSet==true) getString(R.string.cancel_password)
                                   else getString(R.string.set_password)
                MethodManager.savePrivacyPassword(privacyPassword)
                //更新增量更新
                DataUpdateManager.editDataUpdate(10,1,1,1, Gson().toJson(privacyPassword))
                EventBus.getDefault().post(Constants.PASSWORD_EVENT)
            }
        }

    }

    /**
     * 年级选择
     */
    private fun selectorGrade() {
        PopupList(this, grades, btn_edit_grade, 15).builder().setOnSelectListener { item ->
            tv_grade_str.text = item.name
            grade = item.id
            presenter?.editGrade(grade)
        }
    }

    /**
     * 修改学校
     */
    private fun editSchool() {
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,getCurrentScreenPos(),schools).builder()
            schoolSelectDialog?.setOnDialogClickListener{
                school=it.id
                if (school==mUser?.schoolId)
                    return@setOnDialogClickListener
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

    /**
     * 修改名称
     */
    private fun editName() {
        InputContentDialog(this, screenPos, tv_name.text.toString()).builder()
            ?.setOnDialogClickListener { string ->
                nickname = string
                presenter?.editName(nickname)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtil.putObj("user", mUser!!)
    }
}