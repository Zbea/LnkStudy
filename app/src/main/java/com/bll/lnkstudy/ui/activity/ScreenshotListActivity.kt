package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ItemSelectorDialog
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.dialog.ScreenshotDetailsDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.ui.adapter.ScreenshotAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.common_title.iv_manager

class ScreenshotListActivity:BaseAppCompatActivity() {

    private var popupBeans = mutableListOf<PopupBean>()
    private var longBeans = mutableListOf<ItemList>()
    private var tabPos=0
    private var mAdapter:ScreenshotAdapter?=null
    private var position=0
    private var totalNum=0
    private var tabPath=""

    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize = 12
        popupBeans.add(PopupBean(0, "管理分类", false))
        popupBeans.add(PopupBean(1, "创建分类", false))
        popupBeans.add(PopupBean(2, "截图明细", false))
    }

    override fun initView() {
        setPageTitle("截图列表")
        showView(iv_manager)

        iv_manager.setOnClickListener {
            PopupClick(this, popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(this,ScreenshotManagerActivity::class.java))
                    }
                    1 -> {
                        InputContentDialog(this, "创建分类").builder().setOnDialogClickListener {
                            if (ItemTypeDaoManager.getInstance().isExist(1,it)) {
                                //创建文件夹
                                showToast("已存在")
                                return@setOnDialogClickListener
                            }
                            val path = FileAddress().getPathScreen(it)
                            FileUtils.mkdirs(path)

                            val bean = ItemTypeBean()
                            bean.type = 1
                            bean.title = it
                            bean.path = path
                            bean.date = System.currentTimeMillis()
                            ItemTypeDaoManager.getInstance().insertOrReplace(bean)
                            mTabTypeAdapter?.addData(bean)
                        }
                    }
                    2->{
                        ScreenshotDetailsDialog(this).builder()
                    }
                }
            }
        }

        initRecycleView()
        initTab()
    }

    private fun initTab() {
        pageIndex=1
        itemTabTypes=ItemTypeDaoManager.getInstance().queryAll(1)
        itemTabTypes.add(0,ItemTypeBean().apply {
            path=FileAddress().getPathScreen("未分类")
            title="未分类"
        })
        if (tabPos>=itemTabTypes.size){
            tabPos=0
        }
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex=1
        tabPos=position
        fetchData()
    }

    private fun initRecycleView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,30f), DP2PX.dip2px(this,40f),
            DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = ScreenshotAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val index=totalNum-1-((pageIndex-1)*pageSize+position)
                MethodManager.gotoScreenFile(this@ScreenshotListActivity,index,tabPath)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@ScreenshotListActivity.position=position
                onLongClick()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(this, 60f)))
    }

    private fun onLongClick() {
        longBeans.clear()
        longBeans.add(ItemList().apply {
            name=getString(R.string.delete)
            resId=R.mipmap.icon_setting_delete
        })
        if (tabPos==0){
            longBeans.add(ItemList().apply {
                name="分类"
                resId=R.mipmap.icon_setting_set
            })
        }
        else{
            longBeans.add(ItemList().apply {
                name=getString(R.string.shiftOut)
                resId=R.mipmap.icon_setting_out
            })
        }
        val file= mAdapter?.data?.get(position)!!
        LongClickManageDialog(this,getCurrentScreenPos(), file.name,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    mAdapter?.remove(position)
                    FileUtils.deleteFile(file)
                    val drawPath=tabPath+"/drawing/${file.name}"
                    FileUtils.delete(drawPath)
                }
                else{
                    if (tabPos==0){
                        val types= ItemTypeDaoManager.getInstance().queryAll(1)
                        val lists= mutableListOf<ItemList>()
                        for (ite in types){
                            lists.add(ItemList(types.indexOf(ite),ite.title))
                        }
                        ItemSelectorDialog(this,"设置分类",lists).builder().setOnDialogClickListener{ pos->
                            FileUtils.copyFile(file.path,types[pos].path+"/"+file.name)
                            mAdapter?.remove(position)
                        }
                    }
                    else{
                        val path=FileAddress().getPathScreen("未分类")
                        FileUtils.copyFile(file.path,path+"/"+file.name)
                        mAdapter?.remove(position)
                    }
                }
            }
    }

    override fun fetchData() {
        tabPath=itemTabTypes[tabPos].path
        totalNum= FileUtils.getDescFiles(tabPath).size
        setPageNumber(totalNum)
        val files= FileUtils.getDescFiles(tabPath,pageIndex, pageSize)
        mAdapter?.setNewData(files)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.SCREENSHOT_MANAGER_EVENT){
            initTab()
        }
    }
}