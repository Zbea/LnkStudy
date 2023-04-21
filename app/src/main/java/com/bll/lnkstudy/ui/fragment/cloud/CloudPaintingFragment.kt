package com.bll.lnkstudy.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.cloud.CloudPaintingList
import com.bll.lnkstudy.mvp.presenter.cloud.CloudPaintingPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICloudPaintingView
import com.bll.lnkstudy.ui.activity.BookCollectActivity
import com.bll.lnkstudy.ui.adapter.cloud.CloudPaintingAdapter
import com.bll.lnkstudy.ui.adapter.cloud.CloudPaintingLocalAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_painting_list.rv_list
import kotlinx.android.synthetic.main.fragment_content.*
import kotlinx.android.synthetic.main.fragment_painting.*

class CloudPaintingFragment : BaseFragment(),ICloudPaintingView {

    private val mPresenter=CloudPaintingPresenter(this)
    private var typeStr = ""
    var typeId = 0
    private var dynasty = 0

    private var paintings= mutableListOf<CloudPaintingList.PaintingListBean>()
    private var mAdapter:CloudPaintingAdapter?=null
    private var mLocalAdapter:CloudPaintingLocalAdapter?=null


    override fun onList(item: CloudPaintingList?) {
        setPageNumber(item?.total!!)
        paintings=item.list
        if (typeId==6||typeId==7)
        {
            mLocalAdapter?.setNewData(paintings)
        }
        else{
            mAdapter?.setNewData(paintings)
        }

    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=6
        initTab()
        initRecyclerPaintingView()
        initRecyclerLocalView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab() {
        val types = mutableListOf<String>()
        types.addAll(DataBeanManager.PAINTING.toList())
        types.add(getString(R.string.my_drawing_str))
        types.add(getString(R.string.my_calligraphy_str))
        typeStr = types[0]
        for (i in types.indices) {
            rg_group.addView(getRadioButton(i, types[i], types.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                0, 1, 2, 3, 4, 5 -> {
                    pageSize=6
                    showView(rv_list)
                    disMissView(rv_local)
                    (activity as BookCollectActivity).showDynastyView()

                }
                else -> {
                    pageSize=9
                    showView(rv_local)
                    disMissView(rv_list)
                    (activity as BookCollectActivity).closeDynastyView()
                }
            }
            typeId = id
            typeStr = types[id]
            pageIndex = 1
            fetchData()
        }
    }

    /**
     * 线上书画
     */
    private fun initRecyclerPaintingView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,50f),
            DP2PX.dip2px(activity,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
        mAdapter = CloudPaintingAdapter(R.layout.item_download_painting,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(2, DP2PX.dip2px(activity,20f),100))
            setOnItemClickListener { adapter, view, position ->

            }
        }
    }

    /**
     * 本地画本、书法
     */
    private fun initRecyclerLocalView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity, 20f),
            DP2PX.dip2px(activity, 40f),
            DP2PX.dip2px(activity, 20f), 0
        )
        layoutParams.weight = 1f
        rv_local.layoutParams = layoutParams
        rv_local.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mLocalAdapter = CloudPaintingLocalAdapter(R.layout.item_painting_type, null).apply {
            rv_local.adapter = this
            bindToRecyclerView(rv_local)
            rv_local.addItemDecoration(SpaceGridItemDeco(3, 60))
            setOnItemClickListener { adapter, view, position ->

            }
        }
    }

    /**
     * 主activity切换朝代
     */
    fun changeDynasty(dynasty: Int) {
        this.dynasty = dynasty
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