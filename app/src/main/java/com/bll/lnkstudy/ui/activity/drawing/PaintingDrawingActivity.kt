package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.utils.DateUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var grade=0
    private var paintingTypeBean: ItemTypeBean?=null
    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业
    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容
    private var page = 0//页码
    private var resId=0

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
        disMissView(iv_draft,iv_btn)
        onChangeContent()

        tv_page_a.setOnClickListener {
            InputContentDialog(this,2,paintingDrawingBean?.title!!).builder().setOnDialogClickListener { string ->
                paintingDrawingBean?.title = string
                paintingLists[page].title = string
                PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
                DataUpdateManager.editDataUpdate(5,paintingDrawingBean?.id!!.toInt(),2,1, Gson().toJson(paintingDrawingBean))
            }
        }

        tv_page.setOnClickListener {
            if (isExpand){
                InputContentDialog(this,1,paintingDrawingBean_a?.title!!).builder().setOnDialogClickListener { string ->
                    paintingDrawingBean_a?.title = string
                    paintingLists[page-1].title = string
                    PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean_a)
                    DataUpdateManager.editDataUpdate(5,paintingDrawingBean_a?.id!!.toInt(),2,1, Gson().toJson(paintingDrawingBean_a))
                }
            }else{
                InputContentDialog(this,1,paintingDrawingBean?.title!!).builder().setOnDialogClickListener { string ->
                    paintingDrawingBean?.title = string
                    paintingLists[page].title = string
                    PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
                    DataUpdateManager.editDataUpdate(5,paintingDrawingBean?.id!!.toInt(),2,1, Gson().toJson(paintingDrawingBean))
                }
            }
        }

    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in paintingLists){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        DrawingCatalogDialog(this,list).builder().setOnDialogClickListener { position ->
            if (page != position) {
                page = position
                onChangeContent()
            }
        }
    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                onChangeContent()
            }
            else if (page==2){//当页面不够翻两页时
                page=1
                onChangeContent()
            }
        }else{
            if (page>0){
                page-=1
                onChangeContent()
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
        onChangeContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        if (paintingLists.size==1&&isExpand){
            newPaintingContent()
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    private fun onChangeContent() {
        paintingDrawingBean = paintingLists[page]

        if (isExpand) {
            if (page > 0) {
                paintingDrawingBean_a = paintingLists[page - 1]
            }
            else{
                page = 1
                paintingDrawingBean = paintingLists[page]
                paintingDrawingBean_a = paintingLists[page-1]
            }
        } else {
            paintingDrawingBean_a = null
        }

        setElikLoadPath(elik_b!!, paintingDrawingBean!!)
        tv_page.text = "${page+1}/${paintingLists.size}"

        //切换页面内容的一些变化
        if (isExpand) {
            v_content_a.setImageResource(resId)
            setElikLoadPath(elik_a!!, paintingDrawingBean_a!!)
            tv_page.text = "${page}/${paintingLists.size}"
            tv_page_a.text = "${page+1}/${paintingLists.size}"
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, bean: PaintingDrawingBean) {
        elik.setLoadFilePath(bean.path, true)
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
        DataUpdateManager.editDataUpdate(5,item.id.toInt(),2,1)
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
        paintingDrawingBean?.path = "$path/$fileName.tch"
        paintingDrawingBean?.grade=grade
        paintingDrawingBean?.bgRes=""
        page = paintingLists.size
        paintingDrawingBean?.page=page
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2,1, Gson().toJson(paintingDrawingBean),path)
    }

}