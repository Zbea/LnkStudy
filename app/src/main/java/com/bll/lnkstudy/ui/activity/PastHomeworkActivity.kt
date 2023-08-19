package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity
import com.bll.lnkstudy.ui.adapter.PastHomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.fragment_homework.*

class PastHomeworkActivity:BaseAppCompatActivity() {

    private var homeworkTypes = mutableListOf<HomeworkTypeBean>()//当前页分类
    private var mAdapter:PastHomeworkAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_list
    }
    override fun initData() {
        pageSize=9
    }
    override fun initView() {
        setPageTitle(R.string.homework_old_homework_str)
        initRecyclerView()
        fetchData()
    }

    private fun initRecyclerView() {

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,60f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = PastHomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@PastHomeworkActivity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(this@PastHomeworkActivity, 30f), 100))
            setOnItemClickListener { adapter, view, position ->
                val item = homeworkTypes[position]
                when (item.state) {
                    1 -> {
                        gotoHomeworkReelDrawing(item,-1)
                    }
                    2 -> {
                        gotoHomeworkDrawing(item,-1)
                    }
                    3 -> {
                        gotoHomeworkRecord(item)
                    }
                    4->{
                        if (HomeworkBookDaoManager.getInstance().isExist(item.bookId)){
                            val intent= Intent(this@PastHomeworkActivity, HomeworkBookDetailsActivity::class.java)
                            val bundle= Bundle()
                            bundle.putSerializable("homework",item)
                            intent.putExtra("homeworkBundle",bundle)
                            intent.putExtra("android.intent.extra.KEEP_FOCUS",true)
                            customStartActivity(intent)
                        }
                        else{
                            showToast(R.string.toast_homework_unDownload)
                        }
                    }
                }
            }

        }
    }

    override fun fetchData() {
        val total=HomeworkTypeDaoManager.getInstance().queryAllByCloud(true).size
        homeworkTypes=HomeworkTypeDaoManager.getInstance().queryAllByCloud(true,pageIndex,pageSize)
        setPageNumber(total)
        mAdapter?.setNewData(homeworkTypes)
    }
}