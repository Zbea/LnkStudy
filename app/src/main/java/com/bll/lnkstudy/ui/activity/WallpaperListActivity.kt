package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataUpdateManager
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
import kotlinx.android.synthetic.main.ac_my_wallpaper_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.File
import kotlin.math.ceil

class WallpaperListActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<PaintingBean>()
    private var mAdapter:MyWallpaperAdapter?=null
    private var listMap=HashMap<Int,MutableList<PaintingBean>>()
    private var leftPath=""
    private var rightPath=""
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_my_wallpaper_list
    }

    override fun initData() {
        pageSize=12
        lists= PaintingBeanDaoManager.getInstance().queryWallpapers()
    }

    override fun initView() {
        setPageTitle(R.string.download_wallpaper)
        setPageSetting(R.string.ok)

        mAdapter = MyWallpaperAdapter(R.layout.item_my_wallpaper, null).apply {
            rv_list.layoutManager = GridLayoutManager(this@WallpaperListActivity,4)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this@WallpaperListActivity,19f),0))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperListActivity, File(lists[position].paths[0])).builder()
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            //用来确定翻页后选中的位置
            val index=(pageIndex-1)* pageSize+position
            val wallpaperItem=lists[index]
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
            val index=(pageIndex-1)* pageSize+position
            this.position=index
            delete()
            true
        }

        pageNumberView()

        tv_setting.setOnClickListener {
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
                //删除增量更新
                DataUpdateManager.deleteDateUpdate(7,item.id.toInt(),1,item.contentId)
                mAdapter?.remove(position)
            }
        })
    }

    //翻页处理
    private fun pageNumberView(){
        val pageTotal=lists.size //全部数量
        val count = ceil(pageTotal.toDouble()/pageSize).toInt()//总共页码
        var toIndex=pageSize
        for(i in 0 until count){
            val index=i*pageSize
            if(index+pageSize>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            val newList = lists.subList(index,index+toIndex)
            listMap[i+1]=newList
        }
        setPageNumber(lists.size)
        if (lists.size>0)
            fetchData()
    }

    override fun fetchData() {
        mAdapter?.setNewData(listMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}