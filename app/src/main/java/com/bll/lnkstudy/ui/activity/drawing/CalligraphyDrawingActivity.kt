package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.ModuleItemDialog
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class CalligraphyDrawingActivity : BaseDrawingActivity() {

    private var typeId=0
    private var path=""
    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业
    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容
    private var page = 0//页码
    private var resId_b=0
    private var resId_a=0

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        typeId= intent.flags
        path=FileAddress().getPathPaintingDraw(1,typeId)
        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(1,typeId)

        if (paintingLists.isNotEmpty()) {
            paintingDrawingBean = paintingLists.last()
            page = paintingLists.size - 1
        } else {
            newPaintingContent()
        }

    }

    override fun initView() {
        disMissView(iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        //云书库下载不可手写，不可更换背景
        if (typeId!=0){
            disMissView(iv_btn)
        }
        setDisableTouchInput(typeId!=0)

        iv_btn.setOnClickListener {
            ModuleItemDialog(this,getCurrentScreenPos(),getString(R.string.sf_module_str), DataBeanManager.sfModuleBeans).builder()
                .setOnDialogClickListener { moduleBean ->
                    paintingDrawingBean?.bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
                    editDataUpdate(paintingDrawingBean!!)
                    resId_b=moduleBean.resContentId
                    setBg_b()
                    if (isExpand){
                        paintingDrawingBean_a?.bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                        PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean_a)
                        editDataUpdate(paintingDrawingBean_a!!)
                        resId_a=moduleBean.resContentId
                        setBg_a()
                    }
                }
        }

        onContent()
    }

    override fun onCatalog() {
        var titleStr = ""
        val list= mutableListOf<ItemList>()
        for (item in paintingLists){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=paintingLists.indexOf(item)
            itemList.isEdit=true
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }
        }
        list.reverse()
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,true).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (page!=pageNumber){
                    page = pageNumber
                    onContent()
                }
            }
            override fun onEdit(title: String, pages: List<Int>) {
                for (page in pages){
                    val item=paintingLists[page]
                    item.title=title
                    PaintingDrawingDaoManager.getInstance().insertOrReplace(item)
                    editDataUpdate(item)
                }
            }
        })
    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                onContent()
            }
            else if (page==2){
                page=1
                onContent()
            }
        }else{
            if (page>0){
                page-=1
                onContent()
            }
        }
    }

    override fun onPageDown() {
        val total=paintingLists.size-1
        if(isExpand){
            if (page<total-1){
                page+=2
                onContent()
            }
            else{
                if (isDrawLastContent()){
                    newPaintingContent()
                }
                page=paintingLists.size - 1
                onContent()
            }
        }
        else{
            if (page==total) {
                if (isDrawLastContent()){
                    newPaintingContent()
                    onContent()
                }
            } else {
                page += 1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        if (paintingLists.size==1){
            //如果最后一张已写,则可以在全屏时创建新的
            if (isDrawLastContent()){
                newPaintingContent()
            }
            else{
                return
            }
        }
        if (page==0){
            page=1
        }
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    //设置背景图
    private fun setBg_a(){
        MethodManager.setImageResource(this,resId_a,v_content_a)
    }

    private fun setBg_b(){
        MethodManager.setImageResource(this,resId_b,v_content_b)
    }

    /**
     * 最新content是否已写
     */
    private fun isDrawLastContent():Boolean{
        val contentBean = paintingLists.last()
        return File(contentBean.path).exists() && typeId==0
    }

    override fun onContent() {
        paintingDrawingBean = paintingLists[page]
        if (isExpand) {
            paintingDrawingBean_a = paintingLists[page-1]
        }

        tv_page_total.text="${paintingLists.size}"
        tv_page_total_a.text="${paintingLists.size}"

        resId_b=ToolUtils.getImageResId(this,paintingDrawingBean?.bgRes)
        setBg_b()
        setElikLoadPath(elik_b!!, paintingDrawingBean!!.path)
        tv_page.text = "${page+1}"

        if (isExpand) {
            resId_a=ToolUtils.getImageResId(this,paintingDrawingBean_a?.bgRes)
            setBg_a()
            setElikLoadPath(elik_a!!, paintingDrawingBean_a!!.path)
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
            }
            else{
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean_a!!.id.toInt(),2,paintingDrawingBean_a!!.id.toInt())
    }

    override fun onElikSava_b() {
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean!!.id.toInt(),2,paintingDrawingBean!!.id.toInt())
    }

    //创建新的作业内容
    private fun newPaintingContent() {
        val date=System.currentTimeMillis()
        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title= getString(R.string.calligraphy)+(paintingLists.size+1)
        paintingDrawingBean?.type = 1
        paintingDrawingBean?.date = date
        paintingDrawingBean?.path = "$path/${DateUtils.longToString(date)}.png"
        paintingDrawingBean?.cloudId=typeId
        paintingDrawingBean?.bgRes=ToolUtils.getImageResStr(this, R.mipmap.icon_note_content_fg_10)
        page = paintingLists.size
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2,typeId,Gson().toJson(paintingDrawingBean),paintingDrawingBean?.path!!)
    }

    /**
     * 修改增量更新
     */
    private fun editDataUpdate(item: PaintingDrawingBean){
        DataUpdateManager.editDataUpdate(5,item.id!!.toInt(),2,typeId, Gson().toJson(item))
    }

}