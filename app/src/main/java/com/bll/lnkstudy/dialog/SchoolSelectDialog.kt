package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Area
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.SToast
import com.google.gson.Gson

/**
 * 学校选择
 */
class SchoolSelectDialog(val context: Context,val screenPos:Int,private val beans:MutableList<SchoolBean>){

    private var provinces= mutableListOf<PopupBean>()
    private var citys= mutableListOf<PopupBean>()
    private var schools= mutableListOf<PopupBean>()
    private var provinceStr=""
    private var cityStr=""
    private var area:Area?=null
    private var school=0

    fun builder(): SchoolSelectDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_school)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val areaStr = FileUtils.readFileContent(context.resources.assets.open("city.json"))
        area = Gson().fromJson(areaStr, Area::class.java)
        for (i in area?.provinces!!.indices){
            provinces.add(PopupBean(i,area?.provinces!![i].provinceName,i==0))
        }
        for (i in area?.provinces!![0].citys.indices){
            citys.add(PopupBean(i,area?.provinces!![0].citys[i].citysName,i==0))
        }
        provinceStr=provinces[0].name
        cityStr=citys[0].name
        getSchool()

        val tvCancel=dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvOk=dialog.findViewById<TextView>(R.id.tv_ok)
        val tvProvince=dialog.findViewById<TextView>(R.id.tv_province)
        tvProvince.text=provinceStr
        val tvCity=dialog.findViewById<TextView>(R.id.tv_city)
        tvCity.text=cityStr
        val tvSchool=dialog.findViewById<TextView>(R.id.tv_school)
        tvProvince.setOnClickListener {
            PopupList(context,provinces,tvProvince,tvProvince.width,5).builder().setOnSelectListener{
                provinceStr=it.name
                citys.clear()
                for (i in area?.provinces!![it.id].citys.indices){
                    citys.add(PopupBean(i,area?.provinces!![it.id].citys[i].citysName,i==0))
                }
                cityStr=citys[0].name
                tvProvince.text=provinceStr
                tvCity.text=cityStr
                getSchool()
            }
        }
        tvCity.setOnClickListener {
            PopupList(context,citys,tvCity,tvCity.width,5).builder().setOnSelectListener{
                cityStr=it.name
                tvCity.text=cityStr
                getSchool()
            }
        }
        tvSchool.setOnClickListener {
            PopupList(context,schools,tvSchool,tvSchool.width,5).builder().setOnSelectListener{
                school=it.id
                tvSchool.text=it.name
            }
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            if (school>0){
                onClickListener?.onSelect(school)
                dialog.dismiss()
            }
            else{
                SToast.showText(screenPos,R.string.toast_select_school)
            }
        }
        return this
    }

    private fun getSchool(){
        schools.clear()
        for (item in beans){
            if (item.province==provinceStr&&item.city==cityStr){
                schools.add(PopupBean(item.id,item.name))
            }
        }
    }


    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSelect(id: Int)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}