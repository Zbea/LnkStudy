package com.bll.lnkstudy.ui.fragment.cloud

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.mvp.model.ItemTypeBean

class CloudPaintingManagerFragment: BaseCloudFragment() {

    private var lastFragment: Fragment? = null
    private var paintingFragment=CloudPaintingFragment()
    private var paintingDrawingFragment=CloudPaintingDrawingFragment().newInstance("我的画本")
    private var calligraphyDrawingFragment=CloudPaintingDrawingFragment().newInstance("我的书法")


    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_painting_manage
    }

    override fun initView() {
        switchFragment(lastFragment,paintingFragment)

        initTab()
    }

    override fun lazyLoad() {
    }


    private fun initTab(){
        val strs= arrayListOf("我的书画","我的画本","我的书法")
        for (str in strs) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=str
                isCheck=strs.indexOf(str)==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        switchFragment(lastFragment, when(position){0->paintingFragment 1->paintingDrawingFragment else->calligraphyDrawingFragment})
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager!!
            val ft = fm.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.fl_content_painting, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

}