package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.PopupDrawingManage
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PaintingDrawingBean
import com.bll.lnkstudy.mvp.model.PaintingTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var grade=0
    private var type=0
    private var paintingTypeBean:PaintingTypeBean?=null
    private var popupDrawingManage: PopupDrawingManage? = null
    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业
    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容
    private var page = 0//页码
    private var resId=0

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
            newHomeWorkContent()
        }

    }

    override fun initView() {

        if (type==1){
            resId = R.mipmap.icon_painting_bg_sf
        }

        setBg()

        changeExpandView()
        changeContent()

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand_left.setOnClickListener {
            if (paintingLists.size==1){
                newHomeWorkContent()
            }
            onChangeExpandContent()
        }
        iv_expand_right.setOnClickListener {
            if (paintingLists.size==1){
                newHomeWorkContent()
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
                    newHomeWorkContent()
                    newHomeWorkContent()
                    page=total
                }
                total-1->{
                    newHomeWorkContent()
                    page=total
                }
                else->{
                    page+=2
                }
            }
        }
        else{
            if (page >=total) {
                newHomeWorkContent()
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
    private fun setBg(){
        v_content_a.setImageResource(resId)
        v_content_b.setImageResource(resId)
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

        tv_title_b.text=paintingDrawingBean?.title
        updateImage(elik_b!!, paintingDrawingBean!!)
        tv_page_b.text = (page + 1).toString()

        //切换页面内容的一些变化
        if (isExpand) {
            if (paintingDrawingBean_a != null) {
                tv_title_a.text=paintingDrawingBean_a?.title
                v_content_a.setImageResource(resId)
                updateImage(elik_a!!, paintingDrawingBean_a!!)
                tv_page_a.text = "$page"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun updateImage(elik: EinkPWInterface, bean: PaintingDrawingBean) {
        elik.setLoadFilePath(bean.path, true)
        elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik.saveBitmap(true) {}
                DataUpdateManager.editDataUpdate(5,bean.id.toInt(),2,1)
            }
        })
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {
        val date=System.currentTimeMillis()
        val path = FileAddress().getPathPainting(type,grade,date)
        val fileName = DateUtils.longToString(date)

        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title=if (type==0)
            getString(R.string.drawing)+(paintingLists.size+1) else getString(R.string.calligraphy)+(paintingLists.size+1)
        paintingDrawingBean?.type = type
        paintingDrawingBean?.date = System.currentTimeMillis()
        paintingDrawingBean?.path = "$path/$fileName.tch"
        paintingDrawingBean?.grade=grade
        page = paintingLists.size
        paintingDrawingBean?.page=page
        paintingLists.add(paintingDrawingBean!!)

        val id=PaintingDrawingDaoManager.getInstance().insertOrReplaceGetId(paintingDrawingBean)
        //创建本地画本增量更新
        DataUpdateManager.createDataUpdate(5,id.toInt(),2,1, Gson().toJson(paintingDrawingBean),path)
    }

    //
    private fun showPopWindowBtn() {
        val pops= mutableListOf<PopupBean>()
        pops.add(PopupBean(0, getString(R.string.delete), false))
        if (type == 0) {
            pops.add(PopupBean(1, getString(R.string.gplot), false))
        }
        if (popupDrawingManage == null) {
            popupDrawingManage = PopupDrawingManage(this, iv_btn, pops).builder()
            popupDrawingManage?.setOnSelectListener { item ->
                if (item.id == 0) {
                    delete()
                }
                if (item.id == 1) {
                    resId = if (resId == 0) {
                        R.mipmap.icon_painting_bg_hb
                    } else {
                        0
                    }
                    setBg()
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
            newHomeWorkContent()
        }
        changeContent()

    }

    override fun changeScreenPage() {
        if (isExpand){
            onChangeExpandContent()
        }
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