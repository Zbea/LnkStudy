package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.dialog.SchoolSelectDialog
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SchoolPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISchoolView
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity : BaseAppCompatActivity(), IContractView.IAccountInfoView ,ISchoolView{

    private val mSchoolPresenter=SchoolPresenter(this)
    private val presenter = AccountInfoPresenter(this)
    private var nickname = ""

    private var popupGradeWindow: PopupList? = null
    private var grades = mutableListOf<PopupBean>()
    private var grade = 1
    private var schools= mutableListOf<SchoolBean>()
    private var school=0
    private var schoolBean:SchoolBean?=null

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
        EventBus.getDefault().post(Constants.USER_EVENT)
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
        grades = DataBeanManager.popupGrades
        school=mUser?.schoolId!!
        mSchoolPresenter.getCommonSchool()
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
            if (grade != 0 && grades.size > 0){
                tv_grade_str.text = grades[grade - 1].name
            }
            tv_provinces.text = schoolProvince
            tv_city.text = schoolCity
            tv_school_name.text = schoolName
            tv_area.text = schoolArea
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
                        val intent = Intent(this@AccountInfoActivity, AccountLoginActivity::class.java)
                        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 3)
                        startActivity(intent)
                        ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
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
        SchoolSelectDialog(this, getCurrentScreenPos(),schools).builder().setOnDialogClickListener {
            school=it
            if (school==mUser?.schoolId)
                return@setOnDialogClickListener
            presenter.editSchool(it)
            for (item in schools){
                if (item.id==school)
                    schoolBean=item
            }
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
    }
}