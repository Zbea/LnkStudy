package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.PaintingLinerSelectDialog
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.GlideUtils
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
        typeId= paintingTypeBean?.typeId!!
        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(0,grade)

        if (!paintingLists.isNullOrEmpty()) {
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
                linerDialog!!.setOnSelectListener(object : PaintingLinerSelectDialog.OnSelectListener {
                    override fun setWidth(width: Int) {
                        elik_a?.penSettingWidth=width
                        elik_b?.penSettingWidth=width
                    }
                    override fun setDrawType(drawType: Int) {
                        elik_a?.drawObjectType=drawType
                        elik_b?.drawObjectType=drawType
                    }
                    override fun setOpenRule(isOPen: Boolean) {
                        if (isOPen){
                            setBg(R.mipmap.icon_painting_draw_hb)
                        }
                        else{
                            setBg(0)
                        }
                        SPUtil.putBoolean(Constants.SP_PAINTING_RULE_SET,isOPen)
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
        val list= mutableListOf<ItemList>()
        for (item in paintingLists){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            itemList.isEdit=true
            list.add(itemList)
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
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
            else if (page==total-1){
                if (isDrawLastContent()){
                    newPaintingContent()
                    onContent()
                }
                else{
                    page=total
                    onContent()
                }
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
        return File(contentBean.path).exists()
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
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean_a!!.id.toInt(),2)
    }

    override fun onElikSava_b() {
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean!!.id.toInt(),2)
    }

    private fun setBg(resId:Int){
        GlideUtils.setImageUrl(this,resId,v_content_a)
        GlideUtils.setImageUrl(this,resId,v_content_b)
    }

    //创建新的作业内容
    private fun newPaintingContent() {
        val date=System.currentTimeMillis()
        val path = paintingTypeBean?.path
        val fileName = DateUtils.longToString(date)

        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title=getString(R.string.drawing)+(paintingLists.size+1)
        paintingDrawingBean?.type = 0
        paintingDrawingBean?.date = date
        paintingDrawingBean?.path = "$path/$fileName.png"
        paintingDrawingBean?.grade=grade
        page = paintingLists.size
        paintingDrawingBean?.page=page
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
        SPUtil.putBoolean(Constants.SP_PAINTING_RULE_SET,false)
        EventBus.getDefault().post(Constants.PAINTING_RULE_IMAGE_SET_EVENT)
    }
}