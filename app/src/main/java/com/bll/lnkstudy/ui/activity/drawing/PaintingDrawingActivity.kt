package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.PaintingLinerSelectDialog
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.SPUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var typeId=0
    private var path=""
    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业
    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容
    private var page = 0//页码
    private var linerDialog:PaintingLinerSelectDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        typeId= intent.flags
        path=FileAddress().getPathPaintingDraw(0,typeId)
        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(0,typeId)

        if (paintingLists.isNotEmpty()) {
            paintingDrawingBean = paintingLists.last()
            page = paintingLists.size - 1
        } else {
            newPaintingContent()
        }

    }

    override fun initView() {
        disMissView(iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_setting)

        setDisableTouchInput(typeId!=0)

        //设置初始笔形
        val drawType=SPUtil.getInt(Constants.SP_PAINTING_DRAW_TYPE)
        setDrawOjectType(if (drawType==0) PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN else drawType)

        iv_btn.setOnClickListener {
            if (linerDialog==null){
                linerDialog=PaintingLinerSelectDialog(this).builder()
                linerDialog!!.setOnSelectListener(object : PaintingLinerSelectDialog.OnSelectListener {
                    override fun setWidth(width: Int) {
                        setDrawWidth(width)
                    }
                    override fun setDrawType(drawType: Int) {
                        setDrawOjectType(drawType)
                        SPUtil.putInt(Constants.SP_PAINTING_DRAW_TYPE,drawType)
                    }
                    override fun setOpenRule(isOPen: Boolean) {
                        if (isOPen){
                            setBg(R.mipmap.icon_painting_draw_hb)
                        }
                        else{
                            setBg(0)
                        }
                        DataBeanManager.isRuleImage=isOPen
                        EventBus.getDefault().post(Constants.PAINTING_RULE_IMAGE_SET_EVENT)
                    }
                })
            }
            else{
                linerDialog?.show()
            }
        }

        onContent()
    }

    override fun onCatalog() {
        var titleStr=""
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

    /**
     * 最新content是否已写
     */
    private fun isDrawLastContent():Boolean{
        val contentBean = paintingLists.last()
        return File(contentBean.path).exists()&&typeId==0
    }

    override fun onContent() {
        paintingDrawingBean = paintingLists[page]
        if (isExpand) {
            paintingDrawingBean_a = paintingLists[page-1]
        }

        tv_page_total.text="${paintingLists.size}"
        tv_page_total_a.text="${paintingLists.size}"

        setElikLoadPath(elik_b!!, paintingDrawingBean!!.path)
        tv_page.text = "${page+1}"
        if (isExpand) {
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

    private fun setBg(resId:Int){
        MethodManager.setImageResource(this,resId,v_content_a)
        MethodManager.setImageResource(this,resId,v_content_b)
    }

    //创建新的作业内容
    private fun newPaintingContent() {
        val date=System.currentTimeMillis()
        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title=getString(R.string.drawing)+(paintingLists.size+1)
        paintingDrawingBean?.type = 0
        paintingDrawingBean?.date = date
        paintingDrawingBean?.path = "$path/${DateUtils.longToString(date)}.png"
        paintingDrawingBean?.cloudId=typeId
        page = paintingLists.size
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2,typeId, Gson().toJson(paintingDrawingBean),paintingDrawingBean?.path!!)
    }

    /**
     * 修改增量更新
     */
    private fun editDataUpdate(item: PaintingDrawingBean){
        DataUpdateManager.editDataUpdate(5,item.id!!.toInt(),2,typeId, Gson().toJson(item))
    }

    override fun onDestroy() {
        super.onDestroy()
        DataBeanManager.isRuleImage=false
        EventBus.getDefault().post(Constants.PAINTING_RULE_IMAGE_SET_EVENT)
    }
}