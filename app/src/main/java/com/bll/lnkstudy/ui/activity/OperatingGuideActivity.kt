package com.bll.lnkstudy.ui.activity

import android.view.View
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupOperatingGuideCatalog
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_operating_guide.iv_content
import kotlinx.android.synthetic.main.common_title.tv_setting

class OperatingGuideActivity :BaseAppCompatActivity() {

    private var path=""
    private var baseUrl="file:///android_asset/"
    private var popCatalog:PopupOperatingGuideCatalog?=null

    override fun layoutId(): Int {
        return R.layout.ac_operating_guide
    }

    override fun initData() {
        pageSize=1
        val types= mutableListOf("主页面","管理中心","作业书籍","学习工具","特别说明")
        val paths= mutableListOf("main","manager","book","tool","info")
        for (i in types.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=types[i]
                path=paths[i]
                isCheck=i==0
            })
        }

        path=paths[0]
    }

    override fun initView() {
        setPageTitle("操作手册")
        setPageSetting("目录")

        tv_setting.setOnClickListener {
            if (popCatalog==null){
                popCatalog=PopupOperatingGuideCatalog(this,tv_setting).builder()
                popCatalog?.setOnSelectListener{ position,page->
                    for (item in itemTabTypes){
                        item.isCheck=false
                    }
                    itemTabTypes[position].isCheck=true
                    path=itemTabTypes[position].path
                    mTabTypeAdapter?.setNewData(itemTabTypes)
                    pageIndex=page
                    fetchData()
                }
            }
            else{
                popCatalog?.show()
            }
        }

        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        path=itemTabTypes[position].path
        pageIndex=1
        fetchData()
    }


    override fun fetchData() {
        val list= assets.list(path)!!
        setPageNumber(list.size)
        val images= mutableListOf<String>()
        for (name in list){
            images.add(baseUrl+"${path}/"+name)
        }
        GlideUtils.setImageNoCacheUrl(this,images[pageIndex-1],iv_content)
    }
}