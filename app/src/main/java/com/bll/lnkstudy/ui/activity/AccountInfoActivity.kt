package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.AccountEditParentDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.SchoolSelectDialog
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SchoolPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISchoolView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_name
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_parent
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

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView ,ISchoolView{

    private var mSchoolPresenter:SchoolPresenter?=null
    private var presenter:AccountInfoPresenter?=null
    private var nickname = ""

    private var schools= mutableListOf<SchoolBean>()
    private var school=0
    private var schoolBean:SchoolBean?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null

    override fun onLogout() {
    }

    override fun onEditNameSuccess() {
        showToast(R.string.toast_edit_success)
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

    override fun onListSchools(list: MutableList<SchoolBean>) {
        schools=list
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()
        school=mUser?.schoolId!!
        if (NetworkUtil(this).isNetworkConnected())
            mSchoolPresenter?.getCommonSchool(false)
    }

    override fun initChangeScreenData() {
        mSchoolPresenter=SchoolPresenter(this,getCurrentScreenPos())
        presenter = AccountInfoPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.my_account)

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text = telNumber.substring(0, 3) + "****" + telNumber.substring(7, 11)
            tv_birthday.text = DateUtils.intToStringDataNoHour(birthdayTime)
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
            if (schools.isNotEmpty()){
                editSchool()
            }
            else{
                showToast("数据加载失败，请重新加载")
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
            .setOnDialogClickListener { string ->
                nickname = string
                presenter?.editName(nickname)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtil.putObj("user", mUser!!)
    }

    override fun onNetworkConnectionSuccess() {
        mSchoolPresenter?.getCommonSchool(true)
    }
}