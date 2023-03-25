package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.presenter.TeachingVideoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.TeachListActivity
import com.bll.lnkstudy.ui.adapter.TeachCourseAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_teach.*
import java.lang.Math.ceil

/**
 * 教学
 */
class TeachFragment : BaseFragment(),IContractView.ITeachingVideoView {

    private val mPresenter=TeachingVideoPresenter(this)
    private var mAdapter: TeachCourseAdapter? = null
    private var courseType:TeachingVideoType?=null
    private var videoType:TeachingVideoType?=null
    private var lists = mutableListOf<ItemList>()//列表数据
    private var tabs= mutableListOf<ItemList>()//tab分类
    private var flags=0//0课程 1其他
    private val map= mutableMapOf<Int,List<ItemList>>()

    override fun onList(list: TeachingVideoList?) {

    }
    override fun onCourse(type: TeachingVideoType) {
        courseType=type
        lists=type.types
        pageNumberView()
    }

    override fun onType(type: TeachingVideoType?) {
        videoType=type
        tabs.addAll(type?.types!!)
        initTab()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_teach
    }

    override fun initView() {
        setTitle(R.string.main_teach_title)
        pageSize=8

        tabs.add(ItemList().apply {
            type=0
            desc=getString(R.string.course)
        })

        mAdapter = TeachCourseAdapter(R.layout.item_teach_course, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 2)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(activity, 30f)))
            setOnItemClickListener { _, _, position ->
                val intent= Intent(activity, TeachListActivity::class.java).setFlags(flags)
                val bundle= Bundle()
                bundle.putSerializable("item", data[position])
                bundle.putSerializable("type",if (flags==0) courseType else videoType)
                intent.putExtra("bundle", bundle)
                customStartActivity(intent)
            }
        }

    }

    override fun lazyLoad() {
        mPresenter.getCourseType()
        mPresenter.getType()
    }

    //设置头部索引
    private fun initTab() {
        for (i in tabs.indices) {
            rg_group.addView(getRadioButton(i ,tabs[i].desc,tabs.size-1))
        }
        rg_group.setOnCheckedChangeListener { _, i ->
            pageIndex=1
            lists = if (i==0){
                flags=0
                courseType?.types as MutableList<ItemList>
            } else{
                flags=1
                videoType?.subType?.get(i.toString()) as MutableList<ItemList>
            }
            pageNumberView()
        }
    }

    //翻页处理
    private fun pageNumberView(){
        val pageTotal= lists.size
        setPageNumber(pageTotal)
        pageCount= ceil(pageTotal.toDouble() / pageSize).toInt()
        var toIndex=pageSize
        for(i in 0 until pageCount){
            var index=i*pageSize
            if(index+pageSize>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            val newList = lists.subList(index,index+toIndex)
            map[i+1]=newList
        }
        fetchData()
    }


    override fun refreshData() {
        if (courseType==null){
            mPresenter.getCourseType()
        }
        if (videoType==null){
            mPresenter.getType()
        }
    }

    override fun fetchData() {
        lists= (map[pageIndex] as MutableList<ItemList>?)!!
        mAdapter?.setNewData(lists)
        tv_page_current.text=pageIndex.toString()
    }

}