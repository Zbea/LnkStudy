package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.mvp.model.CalenderItemBean
import com.bll.lnkstudy.ui.adapter.CalenderMyAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn
import org.greenrobot.eventbus.EventBus
import java.io.File

class CalenderMyActivity:BaseAppCompatActivity(){
    private var items= mutableListOf<CalenderItemBean>()
    private var mAdapter: CalenderMyAdapter?=null
    private var position=-1

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
    }
    override fun initView() {
        setPageTitle("我的日历")
        showView(tv_btn)

        tv_btn.text="设为日历"
        tv_btn.setOnClickListener {
            if (position>=0){
                val item=items[position]
                CalenderDaoManager.getInstance().setSetFalse()
                item.isSet=true
                CalenderDaoManager.getInstance().insertOrReplace(item)
                showToast("设置成功")
                EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
        }

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,30f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = CalenderMyAdapter(R.layout.item_calender_my ,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco(4,  90))
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                val urls=item.previewUrl.split(",")
                ImageDialog(this@CalenderMyActivity,urls).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CalenderMyActivity.position=position
                if (view.id==R.id.cb_check){
                    for (item in items){
                        item.isCheck=false
                    }
                    val item=items[position]
                    item.isCheck=true
                    mAdapter?.notifyDataSetChanged()
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                delete(position)
                true
            }
        }
    }

    private fun delete(pos:Int){
        CommonDialog(this).setContent("确定删除？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[pos]
                FileUtils.deleteFile(File(item.path))
                CalenderDaoManager.getInstance().deleteBean(item)
                mAdapter?.remove(pos)
                if (item.isSet)
                    EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
        })
    }

    override fun fetchData() {
        val count=CalenderDaoManager.getInstance().queryList().size
        items=CalenderDaoManager.getInstance().queryList(pageIndex,pageSize)
        setPageNumber(count)
        for (item in items){
            if (item.isSet){
                item.isCheck=true
            }
        }
        mAdapter?.setNewData(items)
    }

}