package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.ui.adapter.MyPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_my_painting_list.*
import java.io.File

class PaintingListActivity:BaseAppCompatActivity() {

    private var titleStr=""
    private var type=0 //type=0 type=1书法
    private var time=0
    private var paintingType=0
    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyPaintingAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_my_painting_list
    }

    override fun initData() {
        pageSize=6
        titleStr= intent.getStringExtra("title").toString()
        time=intent.getIntExtra("time",0)
        paintingType=intent.getIntExtra("paintingType",0)
        type=intent.flags

    }

    override fun initView() {
        setPageTitle(titleStr)
        mAdapter = MyPaintingAdapter(R.layout.item_download_painting, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@PaintingListActivity,2)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(2,DP2PX.dip2px(this@PaintingListActivity,30f),130))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@PaintingListActivity,File(lists[position].paths[0])).builder()
            }
        }

        fetchData()
    }


    override fun fetchData() {
        var total=0
        if (type==0){
            lists= PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType,pageIndex,pageSize)
            total= PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType)
        }
        else{
            lists= PaintingBeanDaoManager.getInstance().queryPaintings(paintingType,pageIndex,pageSize)
            total= PaintingBeanDaoManager.getInstance().queryPaintings(paintingType)
        }
        setPageNumber(total)
        mAdapter?.setNewData(lists)

    }

}