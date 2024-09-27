package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.presenter.TeachingVideoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.TeachListActivity
import com.bll.lnkstudy.ui.adapter.TeachCourseAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_page_number.tv_page_current
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list

/**
 * 教学
 */
class TeachFragment : BaseMainFragment(),IContractView.ITeachingVideoView {

    private val mPresenter=TeachingVideoPresenter(this,1)
    private var mAdapter: TeachCourseAdapter? = null
    private var videoType:TeachingVideoType?=null
    private var lists = mutableListOf<ItemList>()//列表数据
    private var flags=0//0课程 1其他
    private val map= mutableMapOf<Int,List<ItemList>>()

    override fun onList(list: TeachingVideoList?) {
    }
    override fun onType(type: TeachingVideoType) {
        if (videoType!=type){
            videoType=type
            SPUtil.putObj("videoType",type)
            initTab()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[7])
        pageSize=8

        initRecyclerView()

        initDialog(1)
    }

    override fun lazyLoad() {
        pageIndex=1
        fetchCommonData()
        if(NetworkUtil(requireActivity()).isNetworkConnected()){
            mPresenter.getType()
        }
        else{
            if (videoType==null&&SPUtil.getObj("videoType",TeachingVideoType::class.java)!=null){
                videoType=SPUtil.getObj("videoType",TeachingVideoType::class.java)
                initTab()
            }
        }
    }

    //设置头部索引
    private fun initTab() {
        itemTabTypes.clear()
        itemTabTypes.add(ItemTypeBean().apply {
            title="课程"
            isCheck=true
        })
        for (i in videoType?.types!!.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title= videoType?.types!![i].desc
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)

        lists= MethodManager.getItemLists("courses")
        pageNumberView()
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex=1
        lists = if (position==0){
            flags=0
            MethodManager.getItemLists("courses")
        } else{
            flags=1
            videoType?.subType?.get(position.toString()) as MutableList<ItemList>
        }
        pageNumberView()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = TeachCourseAdapter(R.layout.item_teach_course, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 2)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(activity, 25f)))
            setOnItemClickListener { _, _, position ->
                if (!MethodManager.getSchoolPermissionAllow(1)){
                    showToast(1,"学校该时间不允许查看义教视频")
                }
                else{
                    if (MethodManager.getParentPermissionAllow(1)){
                        val intent= Intent(activity, TeachListActivity::class.java).setFlags(flags)
                        val bundle= Bundle()
                        bundle.putSerializable("item", data[position])
                        intent.putExtra("bundle", bundle)
                        customStartActivity(intent)
                    }
                    else{
                        showToast(1,"家长该时间不允许查看义教视频")
                    }
                }
            }
        }
    }

    //翻页处理
    private fun pageNumberView(){
        map.clear()
        val pageTotal= lists.size
        setPageNumber(pageTotal)
        pageCount= kotlin.math.ceil(pageTotal.toDouble() / pageSize).toInt()
        var toIndex=pageSize
        for(i in 0 until pageCount){
            val index=i*pageSize
            if(index+pageSize>pageTotal){
                toIndex=pageTotal-index
            }
            val newList = lists.subList(index,index+toIndex)
            map[i+1]=newList
        }
        fetchData()
    }

    override fun fetchData() {
        if (map[pageIndex]!=null){
            lists= (map[pageIndex] as MutableList<ItemList>?)!!
            tv_page_current.text=pageIndex.toString()
        }
        mAdapter?.setNewData(lists)
    }

    override fun onNetworkConnectionSuccess() {
        lazyLoad()
    }
}