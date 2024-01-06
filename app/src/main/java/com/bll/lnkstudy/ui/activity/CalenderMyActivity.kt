package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.mvp.model.CalenderItemBean
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.ui.adapter.CalenderListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_list.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class CalenderMyActivity:BaseAppCompatActivity(){

    private var items= mutableListOf<CalenderItemBean>()
    private var mAdapter:CalenderListAdapter?=null
    private var longBeans = mutableListOf<ItemList>()
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12

        longBeans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name="设置"
            resId=R.mipmap.icon_setting_set
        })
    }
    override fun initView() {
        setPageTitle("我的台历")

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,28f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = CalenderListAdapter(R.layout.item_calendar, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(this@CalenderMyActivity, 20f)
                , DP2PX.dip2px(this@CalenderMyActivity, 60f)))
            setOnItemClickListener { adapter, view, position ->
                onLongClick()
            }
            setOnItemChildClickListener { adapter, view, position ->
                val item=items[position]
                if (view.id==R.id.tv_preview){
                    val urls=item.previewUrl.split(",")
                    ImageDialog(this@CalenderMyActivity,urls).builder()
                }
            }
        }
    }

    private fun onLongClick() {
        val item=items[position]
        LongClickManageDialog(this,getCurrentScreenPos(), item.title,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    FileUtils.deleteFile(File(item.path))
                    CalenderDaoManager.getInstance().deleteBean(item)
                    mAdapter?.remove(position)
                }
                else{
                    CalenderDaoManager.getInstance().setSetFalse()
                    item.isSet=true
                    CalenderDaoManager.getInstance().insertOrReplace(item)
                }
                EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
    }

    override fun fetchData() {
        val count=CalenderDaoManager.getInstance().queryList().size
        items=CalenderDaoManager.getInstance().queryList(pageIndex,pageSize)
        setPageNumber(count)
        mAdapter?.setNewData(items)
    }

}