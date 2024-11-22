package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.ui.adapter.MyPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import java.io.File

class PaintingListActivity:BaseAppCompatActivity() {

    private var time=0
    private var paintingType=0
    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyPaintingAdapter?=null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        time=intent.getIntExtra("time",0)
        paintingType=intent.getIntExtra("paintingType",0)
    }

    override fun initView() {
        setPageTitle(intent.getStringExtra("title").toString())

        initRecyclerView()

        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, 30f),
            DP2PX.dip2px(this, 60f),
            DP2PX.dip2px(this, 30f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(this@PaintingListActivity, 4)//创建布局管理
        mAdapter = MyPaintingAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val item = lists[position]
                MethodManager.gotoPaintingImage(this@PaintingListActivity,item.contentId,getCurrentScreenPos())
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@PaintingListActivity.position = position
                delete()
                true
            }
            rv_list?.addItemDecoration(SpaceGridItemDeco(4, 90))
        }
    }

    private fun delete(){
        CommonDialog(this).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=lists[position]
                PaintingBeanDaoManager.getInstance().deleteBean(item)
                val path= FileAddress().getPathImage("painting" ,item.contentId)
                FileUtils.deleteFile(File(path))
                mAdapter?.remove(position)
            }
        })
    }

    override fun fetchData() {
        lists =PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType,pageIndex,pageSize)
        val total= PaintingBeanDaoManager.getInstance().queryPaintings(time,paintingType)
        setPageNumber(total)
        mAdapter?.setNewData(lists)
    }

}