package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.ui.adapter.PaintingTypeAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_page_number.ll_page_number

class PaintingDrawingTypeActivity : BaseAppCompatActivity() {

    private var mAdapter: PaintingTypeAdapter? = null
    private var type = 0//0画本1书法
    private var types= mutableListOf<ItemTypeBean>()

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=9
        type = intent.flags
        types=ItemTypeDaoManager.getInstance().queryAll(type)
    }

    override fun initView() {
        setPageTitle(if (type==3) R.string.my_drawing_str else R.string.my_calligraphy_str)
        initRecyclerView()
        disMissView(ll_page_number)
    }

    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(
            DP2PX.dip2px(this, 30f),
            DP2PX.dip2px(this, 60f),
            DP2PX.dip2px(this, 30f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 3)//创建布局管理
        mAdapter = PaintingTypeAdapter(R.layout.item_painting_type, types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val item=types[position]
                MethodManager.gotoPaintingDrawing(this@PaintingDrawingTypeActivity,item, type)
            }
            setOnItemLongClickListener { adapter, view, position ->
                CommonDialog(this@PaintingDrawingTypeActivity).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        val item=types[position]
                        remove(position)
                        MethodManager.deletePaintingDrawing(item)
                    }
                })
                true
            }
        }
        rv_list.addItemDecoration(SpaceGridItemDeco(3, 60))
    }

}