package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.cloud.CloudHomeworkList
import com.bll.lnkstudy.mvp.presenter.cloud.CloudHomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICloudHomeworkView
import com.bll.lnkstudy.ui.adapter.cloud.CloudHomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.fragment_homework.*
import kotlinx.android.synthetic.main.fragment_painting.*
import java.util.*

class CloudHomeworkFragment:BaseFragment(),ICloudHomeworkView {

    private val mPresenter=CloudHomeworkPresenter(this)
    private var mAdapter:CloudHomeworkAdapter?=null
    private var homeworkTypes= mutableListOf<CloudHomeworkList.CloudHomeworkTypeBean>()
    private var course=""

    override fun onType(item: CloudHomeworkList?) {
        setPageNumber(item?.total!!)
        homeworkTypes=item.list
        for (item in homeworkTypes){
            item.bgResId=getHomeworkCoverStr()
        }
        mAdapter?.setNewData(homeworkTypes)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=9
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

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,20f),DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity, 3)
        mAdapter = CloudHomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 40))
        }
    }

    /**
     * 老师下发作业本随机得到背景图
     */
    private fun getHomeworkCoverStr(): String {
        val covers = DataBeanManager.homeworkCover
        val index = Random().nextInt(covers.size)
        return ToolUtils.getImageResStr(MyApplication.mContext, covers[index].resId)
    }


    override fun refreshData() {
        fetchData()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["grade"] = grade
        map["type"] = 2
        map["userId"] = 37154748
        mPresenter.getType(map)
    }


}