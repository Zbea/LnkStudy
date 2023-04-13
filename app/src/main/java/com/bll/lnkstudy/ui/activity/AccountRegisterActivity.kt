package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.Area
import com.bll.lnkstudy.mvp.model.Grade
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.presenter.CommonPresenter
import com.bll.lnkstudy.mvp.presenter.RegisterOrFindPsdPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.MD5Utils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_account_register.*


/**
 *  //2. 帐号规则 4 - 12 位字母、数字
//3. 密码规则 6 - 20 位字母、数字
//4. 姓名规则 2 - 5 位中文
//5. 手机号码规则 11 位有效手机号
//6. 验证码规则数字即可
 */
class AccountRegisterActivity : BaseAppCompatActivity(),
    IContractView.IRegisterOrFindPsdView,IContractView.ICommonView {

    private val commonPresenter=CommonPresenter(this)
    private val presenter= RegisterOrFindPsdPresenter(this)
    private var countDownTimer: CountDownTimer? = null
    private var flags = 0
    private var area: Area?=null
    private var provinces= mutableListOf<String>()
    private var citys= mutableListOf<String>()
    private var cityAdapter:SpinnerAdapter?=null
    private var provinceStr=""
    private var cityStr=""
    private var brithday=0L

    private var popupGradeWindow:PopupList?=null
    private var grades= mutableListOf<PopupBean>()
    private var grade=1

    override fun onList(grade: MutableList<Grade>) {
        DataBeanManager.grades=grade
        grades=DataBeanManager.popupGrades
        tv_grade.text=grades[0].name
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
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_register
    }

    override fun initData() {
        flags=intent.flags

        val citysStr = FileUtils.readFileContent(resources.assets.open("city.json"))
        area = Gson().fromJson(citysStr, Area::class.java)

        for (item in area?.provinces!!){
            provinces.add(item.provinceName)
        }

        for (item in area?.provinces!![0].citys){
            citys.add(item.citysName)
        }

        grades=DataBeanManager.popupGrades
        if (grades.size>0){
            tv_grade.text=grades[0].name
        }
        else{
            commonPresenter.getGrades()
        }

    }

    override fun initView() {

        when (flags) {
            2 -> {
                setPageTitle(R.string.edit_password)
                disMissView(ll_name,ll_date,ll_user,ll_school)
                btn_register.setText(R.string.commit)
            }
            1 -> {
                setPageTitle(R.string.find_password)
                disMissView(ll_name,ll_date,ll_school)
                btn_register.setText(R.string.commit)
            }
            else -> {
                setPageTitle(R.string.register)
            }
        }
        sp_province.apply {
            dropDownWidth=DP2PX.dip2px(this@AccountRegisterActivity,115f)
            setPopupBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
            val mAdapter=SpinnerAdapter(this@AccountRegisterActivity,provinces)
            adapter=mAdapter
            setSelection(0)
            onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    provinceStr=provinces[p2]
                    citys.clear()
                    for (item in area?.provinces!![p2].citys){
                        citys.add(item.citysName)
                    }
                    cityAdapter?.notifyDataSetChanged()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }

        sp_city.apply {
            dropDownWidth=DP2PX.dip2px(this@AccountRegisterActivity,115f)
            setPopupBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
            cityAdapter=SpinnerAdapter(this@AccountRegisterActivity,citys)
            setSelection(0)
            adapter=cityAdapter
            onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    cityStr=citys[p2]
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        }

        ll_date.setOnClickListener {
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
            presenter.sms(phone)
        }

        tv_grade.setOnClickListener {
            selectorGrade()
        }

        btn_register.setOnClickListener {

            val account=ed_user.text.toString().trim()
            val psd=ed_password.text.toString().trim()
            val name=ed_name.text.toString().trim()
            val phone=ed_phone.text.toString().trim()
            val code=ed_code.text.toString().trim()
            val area=et_area.text.toString().trim()
            val schoolName=et_school_name.text.toString().trim()
            val parentName=et_parent_name.text.toString().trim()
            val parent=et_parent.text.toString().trim()
            val parentPhone=et_parent_phone.text.toString().trim()
            val address=et_address.text.toString().trim()
            val birthdayStr=tv_date.text.toString().trim()

            if (psd.isNullOrEmpty()) {
                showToast(R.string.login_input_password_hint)
                return@setOnClickListener
            }
            if (phone.isNullOrEmpty()) {
                showToast(R.string.toast_input_phone)
                return@setOnClickListener
            }

            if (code.isNullOrEmpty()) {
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
                    if (provinceStr.isEmpty()||cityStr.isEmpty()) {
                        showToast(R.string.toast_input_city)
                        return@setOnClickListener
                    }
                    if (area.isEmpty()) {
                        showToast(R.string.toast_input_school_address)
                        return@setOnClickListener
                    }
                    if (schoolName.isEmpty()) {
                        showToast(R.string.toast_input_school_name)
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
                    if (address.isEmpty()) {
                        showToast(R.string.toast_input_parent_address)
                        return@setOnClickListener
                    }

                    val map=HashMap<String,Any>()
                    map["account"]=account
                    map["password"]=MD5Utils.digest(psd)
                    map["nickname"]=name
                    map["code"]=code
                    map["telNumber"]=phone
                    map["addr"]= "$provinceStr,$cityStr"
                    map["addrInfo"]=area
                    map["schoolName"]=schoolName
                    map["parentName"]=parentName
                    map["parentNickname"]=parent
                    map["parentTel"]=parentPhone
                    map["parentAddr"]=address
                    map["birthdayTime"]=brithday/1000
                    map["grade"]=grade
                    presenter.register(map)
                }
                1 -> {
                    if (account.isEmpty()) {
                        showToast(R.string.toast_input_account)
                        return@setOnClickListener
                    }
                    presenter.findPsd("2",account,MD5Utils.digest(psd),phone, code)
                }
                else -> {
                    presenter.editPsd(MD5Utils.digest(psd),code)
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
     * 年级选择
     */
    private fun selectorGrade(){
        if (popupGradeWindow==null)
        {
            popupGradeWindow= PopupList(this,grades,tv_grade,5).builder()
            popupGradeWindow?.setOnSelectListener { item ->
                tv_grade.text=item.name
                grade=item.id
            }
        }
        else{
            popupGradeWindow?.show()
        }
    }

    private fun setIntent(){
        val intent = Intent()
        intent.putExtra("user", ed_user.text.toString())
        intent.putExtra("psw", ed_password.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    class SpinnerAdapter(val context: Context, val list: List<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return list.size
        }
        override fun getItem(position: Int): Any {
            return list[position]
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val view= LayoutInflater.from(context).inflate(R.layout.item_dropdown,null)
            val tvName=view.findViewById<TextView>(R.id.tv_name)
            tvName.text=list[position]
            tvName.setSingleLine()
            tvName.ellipsize= TextUtils.TruncateAt.END
            tvName.height= DP2PX.dip2px(context,40f)
            return view
        }
    }




}
