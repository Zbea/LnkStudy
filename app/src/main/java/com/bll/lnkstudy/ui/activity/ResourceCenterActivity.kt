package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.ui.fragment.resource.AppDownloadFragment
import com.bll.lnkstudy.ui.fragment.resource.CalenderDownloadFragment
import com.bll.lnkstudy.ui.fragment.resource.PaintingDownloadFragment
import com.bll.lnkstudy.ui.fragment.resource.WallpaperDownloadFragment
import kotlinx.android.synthetic.main.common_title.tv_course
import kotlinx.android.synthetic.main.common_title.tv_grade
import kotlinx.android.synthetic.main.common_title.tv_subgrade
import kotlinx.android.synthetic.main.common_title.tv_supply

class ResourceCenterActivity:BaseAppCompatActivity() {

    private var lastFragment: Fragment? = null
    private var toolFragment: AppDownloadFragment? = null
    private var wallpaperFragment: WallpaperDownloadFragment? = null
    private var paintingFragment: PaintingDownloadFragment? = null
    private var calenderFragment: CalenderDownloadFragment? = null

    private var popSupplys= mutableListOf<PopupBean>()
    private var popTimes= mutableListOf<PopupBean>()
    private var popPaintings= mutableListOf<PopupBean>()
    private var popWallpaperTypes= mutableListOf<PopupBean>()

    private var supply=0
    private var dynasty=0
    private var paintingType=0

    override fun layoutId(): Int {
        return  R.layout.ac_resource
    }

    override fun initData() {
        popSupplys=DataBeanManager.supplys
        supply=popSupplys[0].id
        tv_supply.text=popSupplys[0].name

        popPaintings=DataBeanManager.popupPainting()
        paintingType=popPaintings[0].id
        tv_course.text=popPaintings[0].name

        popTimes=DataBeanManager.popupDynasty()
        dynasty=popTimes[0].id
        tv_grade.text=popTimes[0].name

        popWallpaperTypes=DataBeanManager.popupTypeGrades()
        if (popWallpaperTypes.size>0)
            tv_subgrade.text=popWallpaperTypes[DataBeanManager.getTypeGradePos()].name
    }

    override fun initView() {
        setPageTitle(R.string.resource_center_str)
        showView(tv_supply)

        toolFragment=AppDownloadFragment().newInstance(2)
        wallpaperFragment = WallpaperDownloadFragment()
        paintingFragment = PaintingDownloadFragment()
        calenderFragment = CalenderDownloadFragment()

        switchFragment(lastFragment, toolFragment)
        initTab()

        tv_supply.setOnClickListener {
            PopupList(this,popSupplys,tv_supply,tv_supply.width,5).builder().setOnSelectListener {
                tv_supply.text = it.name
                toolFragment?.changeSupply(it.id)
                wallpaperFragment?.changeSupply(it.id)
                paintingFragment?.changeSupply(it.id)
                calenderFragment?.changeSupply(it.id)
            }
        }

        tv_course.setOnClickListener {
            PopupList(this,popPaintings,tv_course,tv_course.width,5).builder().setOnSelectListener{
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
            PopupList(this,popTimes,tv_grade,tv_grade.width,5).builder().setOnSelectListener{
                tv_grade.text=it.name
                dynasty=it.id
                paintingFragment?.changeDynasty(dynasty)
            }
        }

        tv_subgrade.setOnClickListener {
            PopupList(this,popWallpaperTypes,tv_subgrade,tv_subgrade.width,5).builder().setOnSelectListener{
                tv_subgrade.text=it.name
                wallpaperFragment?.changeType(it.id)
                calenderFragment?.changeType(it.id)
            }
        }

    }

    private fun initTab(){
        for (i in DataBeanManager.resources.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=DataBeanManager.resources[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when(position){
            0->{
                disMissView(tv_course,tv_grade,tv_subgrade)
                switchFragment(lastFragment, toolFragment)
            }
            1->{
                disMissView(tv_course,tv_grade)
                showView(tv_subgrade)
                switchFragment(lastFragment, wallpaperFragment)
            }
            2->{
                showView(tv_course,tv_grade)
                disMissView(tv_subgrade)
                switchFragment(lastFragment, paintingFragment)
            }
            3->{
                disMissView(tv_course,tv_grade)
                showView(tv_subgrade)
                switchFragment(lastFragment, calenderFragment)
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

    override fun initChangeScreenData() {
        toolFragment?.initChangeScreenData()
        wallpaperFragment?.initChangeScreenData()
        paintingFragment?.initChangeScreenData()
        calenderFragment?.initChangeScreenData()
    }

}