package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.PaintingLinerSelectDialog
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.utils.DateUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var grade=0
    private var paintingTypeBean: ItemTypeBean?=null
    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业
    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容
    private var page = 0//页码
    private var linerDialog:PaintingLinerSelectDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        paintingTypeBean=intent.getBundleExtra("paintingBundle")?.getSerializable("painting") as ItemTypeBean
        grade=paintingTypeBean?.grade!!
        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(0,grade)

        if (paintingLists.size > 0) {
            paintingDrawingBean = paintingLists[paintingLists.size - 1]
            page = paintingLists.size - 1
        } else {
            newPaintingContent()
        }

    }

    override fun initView() {
        disMissView(iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_setting)

        iv_btn.setOnClickListener {
            if (linerDialog==null){
                linerDialog=PaintingLinerSelectDialog(this).builder()
                linerDialog!!.setOnSelectListener{
                    elik_a?.penSettingWidth=it
                    elik_b?.penSettingWidth=it
                }
            }
            else{
                linerDialog?.show()
            }
        }

        onContent()
    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in paintingLists){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        DrawingCatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=paintingLists[position].page){
                    page = paintingLists[position].page
                    onContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
                val item=paintingLists[position]
                item.title=title
                PaintingDrawingDaoManager.getInstance().insertOrReplace(item)
                editDataUpdate(item)
            }
        })
    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                onContent()
            }
            else if (page==2){//当页面不够翻两页时
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
            when(page){
                total->{
                    newPaintingContent()
                    newPaintingContent()
                    page=paintingLists.size-1
                }
                total-1->{
                    newPaintingContent()
                    page=paintingLists.size-1
                }
                else->{
                    page+=2
                }
            }
        }
        else{
            if (page >=total) {
                newPaintingContent()
                page=paintingLists.size-1
            } else {
                page += 1
            }
        }
        onContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        if (paintingLists.size==1&&isExpand){
            newPaintingContent()
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent() {
        paintingDrawingBean = paintingLists[page]
        if (isExpand) {
            if (page<=0){
                page = 1
                paintingDrawingBean = paintingLists[page]
            }
            paintingDrawingBean_a = paintingLists[page-1]
        }

        tv_page_total.text="${paintingLists.size}"
        tv_page_total_a.text="${paintingLists.size}"

        setElikLoadPath(elik_b!!, paintingDrawingBean!!.path)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, paintingDrawingBean_a!!.path)
            if (screenPos== Constants.SCREEN_LEFT){
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
                tv_page.text="${page+1}"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
        saveElik(elik_a!!,paintingDrawingBean_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!,paintingDrawingBean!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface,item: PaintingDrawingBean){
        elik.saveBitmap(true) {}
        DataUpdateManager.editDataUpdate(5,item.id.toInt(),2)
    }


    //创建新的作业内容
    private fun newPaintingContent() {
        val date=System.currentTimeMillis()
        val path = FileAddress().getPathPainting(0,grade,date)
        val fileName = DateUtils.longToString(date)

        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title=getString(R.string.drawing)+(paintingLists.size+1)
        paintingDrawingBean?.type = 0
        paintingDrawingBean?.date = System.currentTimeMillis()
        paintingDrawingBean?.path = "$path/$fileName.png"
        paintingDrawingBean?.grade=grade
        paintingDrawingBean?.bgRes=""
        page = paintingLists.size
        paintingDrawingBean?.page=page
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2, Gson().toJson(paintingDrawingBean),path)
    }

    /**
     * 修改增量更新
     */
    private fun editDataUpdate(item: PaintingDrawingBean){
        DataUpdateManager.editDataUpdate(5,item.id!!.toInt(),2, Gson().toJson(item))
    }


}