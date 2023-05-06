package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Area
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SToast
import com.google.gson.Gson


class AccountEditSchoolDialog(private val context: Context, private val screenPos:Int,private val mUser:User) {

    private var provinces= mutableListOf<PopupBean>()
    private var citys= mutableListOf<PopupBean>()
    private var provinceStr=""
    private var cityStr=""

    fun builder(): AccountEditSchoolDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_account_edit_school)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos==3){
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        }
        dialog.show()

        val citysStr = FileUtils.readFileContent(context.resources.assets.open("city.json"))
        val area = Gson().fromJson(citysStr, Area::class.java)

        for (i in area.provinces.indices){
            provinces.add(PopupBean(i,area.provinces[i].provinceName,i==0))
        }

        for (i in area.provinces[0].citys.indices){
            citys.add(PopupBean(i,area.provinces[0].citys[i].citysName,i==0))
        }

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val et_area = dialog.findViewById<EditText>(R.id.et_area)
        et_area.setText(mUser.addrInfo)
        val et_school_name = dialog.findViewById<EditText>(R.id.et_school_name)
        et_school_name.setText(mUser.schoolName)
        val tv_province = dialog.findViewById<TextView>(R.id.tv_province)
        val tv_city = dialog.findViewById<TextView>(R.id.tv_city)
        if (mUser.addr.isNotEmpty()){
            provinceStr=mUser.addr.split(",")[0]
            cityStr=mUser.addr.split(",")[1]
            tv_province.text = mUser.addr.split(",")[0]
            tv_city.text = mUser.addr.split(",")[1]
        }

        tv_province.setOnClickListener {
            PopupList(context,provinces,tv_province,tv_province.width,5).builder()
                .setOnSelectListener{
                    provinceStr=it.name
                    tv_province.text=provinceStr
                    citys.clear()
                    for (i in area.provinces[it.id].citys.indices){
                        citys.add(PopupBean(i,area.provinces[it.id].citys[i].citysName,i==0))
                    }
                }
        }

        tv_city.setOnClickListener {
            PopupList(context,citys,tv_city,tv_city.width,5).builder()
                .setOnSelectListener{
                    cityStr=it.name
                    tv_city.text=cityStr
                }
        }


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val areaStr=et_area.text.toString()
            val schoolName=et_school_name.text.toString()
            if (provinceStr.isEmpty()||cityStr.isEmpty()){
                SToast.showText(screenPos,R.string.toast_input_city)
                return@setOnClickListener
            }
            if (areaStr.isEmpty()){
                SToast.showText(screenPos,R.string.toast_input_school_address)
                return@setOnClickListener
            }
            if (schoolName.isEmpty()){
                SToast.showText(screenPos,R.string.toast_input_school_name)
                return@setOnClickListener
            }
            dialog.dismiss()
            listener?.onClick("$provinceStr,$cityStr",areaStr,schoolName)

        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(provinceStr:String,areaStr: String,schoolName:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}