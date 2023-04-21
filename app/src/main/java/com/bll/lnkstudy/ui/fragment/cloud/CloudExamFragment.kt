package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.cloud.CloudExamList
import com.bll.lnkstudy.mvp.presenter.cloud.CloudExamPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICloudExamView
import com.bll.lnkstudy.ui.adapter.cloud.CloudExamTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_testpaper.*

class CloudExamFragment:BaseFragment(),ICloudExamView {

    private val mPresenter=CloudExamPresenter(this)
    private var mAdapter:CloudExamTypeAdapter?=null
    private var types= mutableListOf<CloudExamList.CloudExamTypeBean>()
    private var course=""

    override fun onType(item: CloudExamList) {
        setPageNumber(item.total)
        types=item.list
        mAdapter?.setNewData(types)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=6
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab(){
        val courses= DataBeanManager.courses
        if (courses.size>0){
            course=courses[0].desc
            for (i in courses.indices) {
                rg_group.addView(getRadioButton(i ,courses[i].desc,courses.size-1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                course=courses[id].desc
                pageIndex=1
                fetchData()
            }
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,30f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity, 2)
        mAdapter = CloudExamTypeAdapter(R.layout.item_testpaper_type,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
        }
    }

    override fun refreshData() {
        fetchData()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["grade"] = grade
        map["type"] = 1
        map["userId"] = 37154748
        mPresenter.getType(map)
    }

}