package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.ui.adapter.WallpaperMyAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn
import java.io.File

class WallpaperMyActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:WallpaperMyAdapter?=null
    private var leftPath=""
    private var rightPath=""

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=6
    }

    override fun initView() {
        setPageTitle("我的壁纸")
        showView(tv_btn)
        tv_btn.text = "设为壁纸"

        initRecyclerView()

        tv_btn.setOnClickListener {
            if (leftPath.isEmpty()&&rightPath.isEmpty()){
                showToast("设置失败")
                return@setOnClickListener
            }
            if(File(leftPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby",leftPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff",leftPath)
            }
            if(File(rightPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby1",rightPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff1",rightPath)
            }
            showToast("设置成功")
        }

        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,30f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = WallpaperMyAdapter(R.layout.item_my_wallpaper, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@WallpaperMyActivity,2)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco(2,90))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperMyActivity, lists[position].paths).builder()
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                for (item in lists){
                    item.isCheck=false
                }
                val item=lists[position]
                item.isCheck=true
                leftPath=item.paths[0]
                rightPath=item.paths[1]
                mAdapter?.notifyDataSetChanged()
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            delete(position)
            true
        }
    }

    private fun delete(position:Int){
        CommonDialog(this).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=lists[position]
                PaintingBeanDaoManager.getInstance().deleteBean(item)
                val path= FileAddress().getPathImage("wallpaper" ,item.contentId)
                FileUtils.deleteFile(File(path))
                mAdapter?.remove(position)
            }
        })
    }

    override fun fetchData() {
        val totals= PaintingBeanDaoManager.getInstance().queryWallpapers()
        setPageNumber(totals.size)
        lists=PaintingBeanDaoManager.getInstance().queryWallpapers(pageIndex,pageSize)
        mAdapter?.setNewData(lists)
    }

}