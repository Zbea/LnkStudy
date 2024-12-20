package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.ui.adapter.ItemTypeManagerAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list
import org.greenrobot.eventbus.EventBus

class CourseManagerActivity : BaseAppCompatActivity() {

    private var items = mutableListOf<ItemTypeBean>()
    private var mAdapter: ItemTypeManagerAdapter? = null

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        items = ItemTypeDaoManager.getInstance().queryAll(7)
    }

    override fun initView() {
        setPageTitle("科目排序")

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, 100f), DP2PX.dip2px(this, 20f),
            DP2PX.dip2px(this, 100f), DP2PX.dip2px(this, 20f)
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ItemTypeManagerAdapter(R.layout.item_notebook_manager, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                val item = items[position]
                when (view.id) {
                    R.id.iv_top -> {
                        val date = items[0].date
                        item.date = date - 1000
                        ItemTypeDaoManager.getInstance().insertOrReplace(item)

                        items.sortWith(Comparator { item1, item2 ->
                            return@Comparator item1.date.compareTo(item2.date)
                        })

                        EventBus.getDefault().post(Constants.COURSEITEM_EVENT)
                        notifyDataSetChanged()
                    }
                }
            }
        }

    }


}