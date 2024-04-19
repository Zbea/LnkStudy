package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.dialog.ItemSelectorDialog
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.ui.adapter.ScreenshotAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.math.ceil

/**
 * 书架分类
 */
class ScreenshotListActivity : BaseAppCompatActivity() {

    private var mAdapter: ScreenshotAdapter? = null
    private var pos = 0
    private var longBeans = mutableListOf<ItemList>()
    private var listMap=HashMap<Int,MutableList<File>>()

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        longBeans.add(ItemList().apply {
            name=getString(R.string.delete)
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name=getString(R.string.setting)
            resId=R.mipmap.icon_setting_set
        })
    }

    override fun initView() {
        pageSize = 12
        setPageTitle(R.string.screenshot_list_str)

        initRecycleView()
        getFetchFiles()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,28f), DP2PX.dip2px(this,60f),DP2PX.dip2px(this,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = ScreenshotAdapter(R.layout.item_screenshot, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(this@ScreenshotListActivity, 22f), DP2PX.dip2px(this@ScreenshotListActivity, 70f)))
            setOnItemClickListener { adapter, view, position ->
                val file=mAdapter?.getItem(position)
                ImageDialog(this@ScreenshotListActivity, arrayListOf(file?.path!!)) .builder()
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                pos = position
                onLongClick()
                true
            }
        }

    }

    //删除书架书籍
    private fun onLongClick() {
        val file=mAdapter?.data?.get(pos)
        LongClickManageDialog(this, getCurrentScreenPos(),file?.name!!,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    FileUtils.deleteFile(file)
                    mAdapter?.remove(pos)
                }
                else{
                    val types= ItemTypeDaoManager.getInstance().queryAll(1)
                    val lists= mutableListOf<ItemList>()
                    for (ite in types){
                        lists.add(ItemList(types.indexOf(ite),ite.title))
                    }
                    ItemSelectorDialog(this,"设置分类",lists).builder().setOnDialogClickListener{
                        FileUtils.copyFile(file.path,types[it].path+"/"+file.name)
                        FileUtils.deleteFile(file)
                        mAdapter?.remove(pos)
                        EventBus.getDefault().post(Constants.SCREENSHOT_MANAGER_EVENT)
                    }
                }
            }
    }

    /**
     * 获取本地文件
     */
    private fun getFetchFiles(){
        pageIndex=1
        val path=FileAddress().getPathScreen("未分类")
        val files= FileUtils.getFiles(path)
        setPageNumber(files.size)

        val pageTotal=files.size //全部数量
        val count = ceil(pageTotal.toDouble()/pageSize).toInt()//总共页码
        var toIndex=pageSize
        for(i in 0 until count){
            val index=i*pageSize
            if(index+pageSize>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            val newList = files.subList(index,index+toIndex)
            listMap[i+1]=newList
        }

        if (files.size>0)
            fetchData()
    }

    override fun fetchData() {
        mAdapter?.setNewData(listMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }


}