package com.bll.lnkstudy.ui.activity

import androidx.fragment.app.Fragment
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.ui.fragment.resource.AppDownloadMainFragment
import com.bll.lnkstudy.ui.fragment.resource.CalenderDownloadMainFragment
import com.bll.lnkstudy.ui.fragment.resource.PaintingDownloadMainFragment
import com.bll.lnkstudy.ui.fragment.resource.WallpaperDownloadMainFragment
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.common_title.*

class ResourceCenterActivity:BaseAppCompatActivity() {

    private var lastFragment: Fragment? = null
    private var appFragment: AppDownloadMainFragment? = null
    private var toolFragment: AppDownloadMainFragment? = null
    private var wallpaperFragment: WallpaperDownloadMainFragment? = null
    private var paintingFragment: PaintingDownloadMainFragment? = null
    private var calenderFragment: CalenderDownloadMainFragment? = null

    private var popSupplys= mutableListOf<PopupBean>()
    private var popTimes= mutableListOf<PopupBean>()
    private var popPaintings= mutableListOf<PopupBean>()

    private var positionTab=0
    private var supply=0
    private var dynasty=0
    private var paintingType=0

    override fun layoutId(): Int {
        return  R.layout.ac_resource
    }

    override fun initData() {
        popSupplys=DataBeanManager.supplys
        supply=popSupplys[0].id
        tv_province.text=popSupplys[0].name

        popPaintings=DataBeanManager.popupPainting()
        paintingType=popPaintings[0].id
        tv_course.text=popPaintings[0].name

        popTimes=DataBeanManager.popupDynasty()
        dynasty=popTimes[0].id
        tv_grade.text=popTimes[0].name
    }

    override fun initView() {
        setPageTitle(R.string.resource_center_str)
        showView(tv_province)

        appFragment = AppDownloadMainFragment().newInstance(1)
        toolFragment=AppDownloadMainFragment().newInstance(2)
        wallpaperFragment = WallpaperDownloadMainFragment()
        paintingFragment = PaintingDownloadMainFragment()
        calenderFragment = CalenderDownloadMainFragment()

        switchFragment(lastFragment, appFragment)
        initTab()

        tv_province.setOnClickListener {
            PopupList(this,popSupplys,tv_province,tv_province.width,0).builder().setOnSelectListener {
                tv_province.text = it.name
                appFragment?.changeSupply(it.id)
                toolFragment?.changeSupply(it.id)
                wallpaperFragment?.changeSupply(it.id)
                paintingFragment?.changeSupply(it.id)
                calenderFragment?.changeSupply(it.id)
            }
        }

        tv_course.setOnClickListener {
            PopupList(this,popPaintings,tv_course,tv_course.width,0).builder().setOnSelectListener{
                tv_course.text=it.name
                paintingType=it.id
                paintingFragment?.changePainting(paintingType)
                popTimes = if (paintingType==5||paintingType==6){
                    DataBeanManager.popupDynastyNow()
                } else{
                    DataBeanManager.popupDynasty()
                }
                dynasty=popTimes[0].id
                tv_grade.text=popTimes[0].name
                paintingFragment?.changeDynasty(dynasty)
            }
        }

        tv_grade.setOnClickListener {
            PopupList(this,popTimes,tv_grade,tv_grade.width,0).builder().setOnSelectListener{
                tv_grade.text=it.name
                dynasty=it.id
                paintingFragment?.changeDynasty(dynasty)
            }
        }

    }

    private fun initTab(){
        val tabs=DataBeanManager.resources
        for (i in tabs.indices) {
            rg_group.addView(getRadioButton(i, tabs[i], tabs.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            positionTab=id
            when(id){
                0->{
                    disMissView(tv_course,tv_grade)
                    switchFragment(lastFragment, appFragment)
                }
                1->{
                    disMissView(tv_course,tv_grade)
                    switchFragment(lastFragment, toolFragment)
                }
                2->{
                    disMissView(tv_course,tv_grade)
                    switchFragment(lastFragment, wallpaperFragment)
                }
                3->{
                    showView(tv_course,tv_grade)
                    switchFragment(lastFragment, paintingFragment)
                }
                4->{
                    disMissView(tv_course,tv_grade)
                    switchFragment(lastFragment, calenderFragment)
                }
            }
        }
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    override fun onCommonData() {
        appFragment?.changeInitData()
        toolFragment?.changeInitData()
        wallpaperFragment?.changeInitData()
        paintingFragment?.changeInitData()
        calenderFragment?.changeInitData()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeNetwork()
    }

}