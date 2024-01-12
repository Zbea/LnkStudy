package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.dialog.PopupDrawingManage
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean
import com.bll.lnkstudy.mvp.model.painting.PaintingTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var grade=0
    private var type=0
    private var paintingTypeBean: PaintingTypeBean?=null
    private var popupDrawingManage: PopupDrawingManage? = null
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
        paintingTypeBean=intent.getBundleExtra("paintingBundle")?.getSerializable("painting") as PaintingTypeBean
        grade=paintingTypeBean?.grade!!
        type=paintingTypeBean?.type!!
        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(type,grade)

        if (paintingLists.size > 0) {

            paintingDrawingBean = paintingLists[paintingLists.size - 1]

            page = paintingLists.size - 1

        } else {
            newPaintingContent()
        }

    }

    override fun initView() {
        iv_draft.setImageResource(R.mipmap.icon_drawing_change)
        changeExpandView()
        changeContent()

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand_left.setOnClickListener {
            if (paintingLists.size==1){
                newPaintingContent()
            }
            onChangeExpandContent()
        }
        iv_expand_right.setOnClickListener {
            if (paintingLists.size==1){
                newPaintingContent()
            }
            onChangeExpandContent()
        }

        iv_expand_a.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            onChangeExpandContent()
        }

        iv_btn.setOnClickListener {
            showPopWindowBtn()
        }

        iv_draft.setOnClickListener {
            val titleStr=if (type==1) getString(R.string.sf_module_str) else getString(R.string.painting_module_str)
            ModuleAddDialog(this,3,titleStr, DataBeanManager.sfModule).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    if (type==1){
                        paintingDrawingBean?.bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                        PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
                        resId_b=moduleBean.resContentId
                        setBg_b()
                        if (isExpand){
                            paintingDrawingBean_a?.bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                            PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean_a)
                            resId_a=moduleBean.resContentId
                            setBg_a()
                        }
                    }
                    else{
                        resId_a=moduleBean.resContentId
                        resId_b=moduleBean.resContentId
                        setBg_a()
                        setBg_b()
                    }
                }
        }

    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                changeContent()
            }
            else if (page==2){//当页面不够翻两页时
                page=1
                changeContent()
            }
        }else{
            if (page>0){
                page-=1
                changeContent()
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
        changeContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        val list= mutableListOf<ItemList>()
        for (item in paintingLists){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            if (page != position) {
                page = position
                changeContent()
            }
        }
    }


    //设置背景图
    private fun setBg_a(){
        v_content_a.setImageResource(resId_a)
    }

    private fun setBg_b(){
        v_content_b.setImageResource(resId_b)
    }

    //翻页内容更新切换
    private fun changeContent() {
        paintingDrawingBean = paintingLists[page]

        if (isExpand) {
            if (page > 0) {
                paintingDrawingBean_a = paintingLists[page - 1]
            }
            if (page==0){
                paintingDrawingBean = paintingLists[page + 1]
                paintingDrawingBean_a = paintingLists[page]
                page = 1
            }
        } else {
            paintingDrawingBean_a = null
        }

        if (paintingTypeBean?.isCloud==true){
            elik_a?.setPWEnabled(false)
            elik_b?.setPWEnabled(false)
        }

        if (type==1){
            resId_b=ToolUtils.getImageResId(this,paintingDrawingBean?.bgRes)
            setBg_b()
        }

        tv_title_b.text=paintingDrawingBean?.title
        setElikLoadPath(elik_b!!, paintingDrawingBean!!)
        tv_page_b.text = (page + 1).toString()

        //切换页面内容的一些变化
        if (isExpand) {
            if (paintingDrawingBean_a != null) {
                if (type==1){
                    resId_a=ToolUtils.getImageResId(this,paintingDrawingBean_a?.bgRes)
                    setBg_a()
                }
                tv_title_a.text=paintingDrawingBean_a?.title
                v_content_a.setImageResource(resId_a)
                setElikLoadPath(elik_a!!, paintingDrawingBean_a!!)
                tv_page_a.text = "$page"
            }
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
        val path = FileAddress().getPathPainting(type,grade,date)
        val fileName = DateUtils.longToString(date)

        paintingDrawingBean =
            PaintingDrawingBean()
        paintingDrawingBean?.title=if (type==0)
            getString(R.string.drawing)+(paintingLists.size+1) else getString(R.string.calligraphy)+(paintingLists.size+1)
        paintingDrawingBean?.type = type
        paintingDrawingBean?.date = System.currentTimeMillis()
        paintingDrawingBean?.path = "$path/$fileName.tch"
        paintingDrawingBean?.grade=grade
        paintingDrawingBean?.bgRes="0"
        page = paintingLists.size
        paintingDrawingBean?.page=page
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2,1, Gson().toJson(paintingDrawingBean),path)
    }

    private fun showPopWindowBtn() {
        val pops= mutableListOf<PopupBean>()
        pops.add(PopupBean(0, getString(R.string.delete), false))
        if (type==0)
            pops.add(PopupBean(1, getString(R.string.revocation), false))
        if (popupDrawingManage == null) {
            popupDrawingManage = PopupDrawingManage(this, iv_btn, pops).builder()
            popupDrawingManage?.setOnSelectListener { item ->
                when(item.id){
                    0->{
                        delete()
                    }
                    1->{
                        resId_a=0
                        resId_b=0
                        setBg_a()
                        setBg_b()
                    }
                }
            }
        } else {
            popupDrawingManage?.show()
        }
    }


    //确认删除
    private fun delete() {
        CommonDialog(this,getCurrentScreenPos()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }

            override fun ok() {
                deleteContent()
            }
        })
    }

    //删除内容
    private fun deleteContent() {
        PaintingDrawingDaoManager.getInstance().deleteBean(paintingDrawingBean)
        paintingLists.remove(paintingDrawingBean)
        FileUtils.deleteFile(File(paintingDrawingBean?.path).parentFile)//删除文件
        //删除本地画本增量更新
        DataUpdateManager.deleteDateUpdate(5,paintingDrawingBean?.id!!.toInt(),2,1)
        if (page>0){
            page -= 1
        }
        else{
            newPaintingContent()
        }
        changeContent()

    }

    override fun setDrawingTitle_a(title: String) {
        paintingDrawingBean_a?.title = title
        paintingLists[page-1].title = title
        PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean_a)
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean_a?.id!!.toInt(),2,1, Gson().toJson(paintingDrawingBean_a))
    }

    override fun setDrawingTitle_b(title: String) {
        paintingDrawingBean?.title = title
        paintingLists[page].title = title
        PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
        DataUpdateManager.editDataUpdate(5,paintingDrawingBean?.id!!.toInt(),2,1, Gson().toJson(paintingDrawingBean))
    }

}