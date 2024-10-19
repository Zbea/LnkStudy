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
import com.bll.lnkstudy.ui.adapter.MyWallpaperAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn
import java.io.File

class WallpaperMyActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyWallpaperAdapter?=null
    private var leftPath=""
    private var rightPath=""
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
    }

    override fun initView() {
        setPageTitle("我的壁纸")
        showView(tv_btn)
        tv_btn.text = "设为壁纸"

        initRecyclerView()

        tv_btn.setOnClickListener {
            if (leftPath.isEmpty()&&rightPath.isEmpty())
                return@setOnClickListener
            if(File(leftPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby",leftPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff",leftPath)
            }
            if(File(rightPath).exists()){
                android.os.SystemProperties.set("xsys.eink.standby1",rightPath)
//                android.os.SystemProperties.set("xsys.eink.poweroff1",rightPath)
            }
        }

        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,20f), DP2PX.dip2px(this,50f),
            DP2PX.dip2px(this,20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = MyWallpaperAdapter(R.layout.item_my_wallpaper, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@WallpaperMyActivity,4)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this@WallpaperMyActivity,19f),0))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperMyActivity, arrayListOf(lists[position].paths[0])).builder()
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            //用来确定翻页后选中的位置
            val wallpaperItem=lists[position]
            if (view.id==R.id.cb_left){
                if(wallpaperItem.isLeft){
                    wallpaperItem.isLeft=false
                    leftPath=""
                }
                else{
                    for (item in lists){
                        item.isLeft=false
                    }
                    wallpaperItem.isLeft=true
                    leftPath=wallpaperItem.paths[0]
                }
            }
            if (view.id==R.id.cb_right){
                if (wallpaperItem.isRight){
                    wallpaperItem.isRight=false
                    rightPath=""
                }
                else{
                    for (item in lists){
                        item.isRight=false
                    }
                    wallpaperItem.isRight=true
                    rightPath=wallpaperItem.paths[0]
                }
            }
            mAdapter?.notifyDataSetChanged()
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            this.position=position
            delete()
            true
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