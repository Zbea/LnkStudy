package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.SchoolPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.ISchoolView
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import com.google.gson.Gson
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
    private var notePassword:NotePassword?=null

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

        notePassword=SPUtil.getObj("${mUser?.accountId}notePassword", NotePassword::class.java)

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
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

        if (notePassword!=null){
            showView(tv_check_pad)
            if (notePassword?.isSet == true){
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
     * 设置查看密码
     */
    private fun setPassword(){
        if (notePassword==null){
            NotebookSetPasswordDialog(this).builder().setOnDialogClickListener{
                notePassword=it
                showView(tv_check_pad)
                btn_psd_check.text=getString(R.string.set_password)
                SPUtil.putObj("${mUser?.accountId}notePassword",notePassword!!)
                EventBus.getDefault().post(Constants.PASSWORD_EVENT)
            }
        }
        else{
            NotebookPasswordDialog(this).builder()?.setOnDialogClickListener{
                notePassword?.isSet=!notePassword?.isSet!!
                btn_psd_check.text=if (notePassword?.isSet==true) getString(R.string.cancel_password)
                                   else getString(R.string.set_password)
                SPUtil.putObj("${mUser?.accountId}notePassword",notePassword!!)
                //更新增量更新
                DataUpdateManager.editDataUpdate(10,1,1,1, Gson().toJson(notePassword))
                EventBus.getDefault().post(Constants.PASSWORD_EVENT)
            }
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