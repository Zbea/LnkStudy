package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.PATH_SF
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.ui.adapter.ListAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_page_number.*

class ListActivity:BaseAppCompatActivity() {

    private var titleStr=""
    private var type=0 //type=1书画类型 type=2书法
    private var lists= mutableListOf<ListBean>()
    private var mAdapter:ListAdapter?=null
    private var pageIndex=1 //当前页码
    private var listMap=HashMap<Int,MutableList<ListBean>>()//将所有数据按30个分页

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        titleStr= intent.getStringExtra("title").toString()
        type=intent.getIntExtra("type",0)
        setPageTitle(titleStr)
        if (type==1){
            for (i in 0..30){
                var item=ListBean()
                lists.add(item)
            }
        }
        if (type==2){
            var path=assets.list("sf")
            if (path != null) {
                for (s in path){
                    var item=ListBean()
                    item.url=PATH_SF+s
                    lists.add(item)
                }
            }
        }

    }

    override fun initView() {

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = ListAdapter(R.layout.item_painting, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(0,125))
        pageNumberView()

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=lists.size //全部数量
        var pageNum=12
        var pageCount=Math.ceil((pageTotal.toDouble()/pageNum)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        var toIndex=pageNum
        for(i in 0 until pageCount){
            var index=i*pageNum
            if(index+pageNum>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = lists.subList(index,index+toIndex)
            listMap[i+1]=newList
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                upDateUI()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                upDateUI()
            }
        }

    }

    //刷新数据
    private fun upDateUI()
    {
        mAdapter?.setNewData(listMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}