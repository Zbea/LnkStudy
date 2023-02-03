package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.PopWindowDrawingButton
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.DataList
import com.bll.lnkstudy.mvp.model.PaintingDrawingBean
import com.bll.lnkstudy.mvp.model.PopWindowData
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_painting_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class PaintingDrawingActivity : BaseActivity() {

    private var type = 0
    private var popWindowDrawingButton: PopWindowDrawingButton? = null

    private var paintingDrawingBean: PaintingDrawingBean? = null//当前作业内容
    private var paintingDrawingBean_a: PaintingDrawingBean? = null//a屏作业

    private var paintingLists = mutableListOf<PaintingDrawingBean>() //所有作业内容

    private var page = 0//页码
    private var resId=0

    override fun layoutId(): Int {
        return R.layout.ac_painting_drawing
    }

    override fun initData() {

        type = intent.flags

        paintingLists = PaintingDrawingDaoManager.getInstance().queryAllByType(type)

        if (paintingLists.size > 0) {

            paintingDrawingBean = paintingLists[paintingLists.size - 1]

            page = paintingLists.size - 1

        } else {
            newHomeWorkContent()
        }

    }

    override fun initView() {

        resId = if (type==1){
            R.mipmap.icon_painting_bg_sf
        } else{
            0
        }

        setBg()

        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace

        changeContent()

        tv_title_a.setOnClickListener {
            var title=tv_title_a.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_a.text = string
                paintingDrawingBean_a?.title = string
                paintingLists[page-1].title = string
                PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean_a)
            }
        }

        tv_title_b.setOnClickListener {
            var title=tv_title_b.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_b.text = string
                paintingDrawingBean?.title = string
                paintingLists[page].title = string
                PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)
            }
        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        btn_page_down.setOnClickListener {
            val total=paintingLists.size-1
            if(isExpand){

                when(page){
                    total->{
                        newHomeWorkContent()
                        newHomeWorkContent()
                        page==total
                    }
                    total-1->{
                        newHomeWorkContent()
                        page==total
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

        btn_page_up.setOnClickListener {
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

        iv_expand.setOnClickListener {
            if (paintingLists.size==1){
                newHomeWorkContent()
            }
            changeExpandContent()
        }

        iv_expand_a.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            changeExpandContent()
        }


        iv_btn.setOnClickListener {
            showPopWindowBtn()
        }


    }

    /**
     * 切换屏幕
     */
    private fun changeExpandContent(){
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    /**
     * 切换屏幕view变化
     */
    private fun changeExpandView(){
        iv_expand.visibility = if(isExpand) View.GONE else View.VISIBLE
        v_content_a.visibility = if(isExpand) View.VISIBLE else View.GONE
        ll_page_content_a.visibility = if(isExpand) View.VISIBLE else View.GONE
        v_empty.visibility=if(isExpand) View.VISIBLE else View.GONE
        if (isExpand){
            if (screenPos==1){
                showView(iv_expand_a)
                disMissView(iv_expand_b)
            }
            else{
                showView(iv_expand_b)
                disMissView(iv_expand_a)
            }
        }
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var list= mutableListOf<DataList>()
        for (item in paintingLists){
            val dataList= DataList()
            dataList.name=item.title
            dataList.page=item.page
            list.add(dataList)
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

        tv_title_b.text=paintingDrawingBean?.title
        updateImage(elik_b!!, paintingDrawingBean?.path!!)
        tv_page_b.text = (page + 1).toString()

        //切换页面内容的一些变化
        if (isExpand) {
            if (paintingDrawingBean_a != null) {
                tv_title_a.text=paintingDrawingBean_a?.title
                v_content_a.setImageResource(resId)
                updateImage(elik_a!!, paintingDrawingBean_a?.path!!)
                tv_page_a.text = "$page"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun updateImage(elik: EinkPWInterface, path: String) {
        elik.setPWEnabled(true)
        elik.setLoadFilePath(path, true)
        elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik.saveBitmap(true) {}
            }

        })
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path = Constants.PAINTING_PATH + "/$mUserId/$type"
        val date = DateUtils.longToString(System.currentTimeMillis())

        paintingDrawingBean = PaintingDrawingBean()
        paintingDrawingBean?.title=if (type==0)"画本${paintingLists.size+1}" else "书法${paintingLists.size+1}"
        paintingDrawingBean?.type = type
        paintingDrawingBean?.date = System.currentTimeMillis()
        paintingDrawingBean?.path = "$path/$date.tch"
        page = paintingLists.size
        paintingDrawingBean?.page=page
        paintingLists.add(paintingDrawingBean!!)

        PaintingDrawingDaoManager.getInstance().insertOrReplace(paintingDrawingBean)

    }


    //
    private fun showPopWindowBtn() {
        val pops= mutableListOf<PopWindowData>()
        pops.add(PopWindowData(0, "删除", false))
        if (type == 0) {
            pops.add(PopWindowData(1, "规矩图", false))
        }
        if (popWindowDrawingButton == null) {
            popWindowDrawingButton = PopWindowDrawingButton(this, iv_btn, pops).builder()
            popWindowDrawingButton?.setOnSelectListener { item ->
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
            popWindowDrawingButton?.show()
        }
    }


    //确认删除
    private fun delete() {
        CommonDialog(this,getCurrentScreenPos()).setContent("确认删除？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }

            override fun ok() {
                deleteContent()
            }
        })
    }

    //删除作业
    private fun deleteContent() {

        PaintingDrawingDaoManager.getInstance().deleteBean(paintingDrawingBean)
        paintingLists.remove(paintingDrawingBean)
        val file = File(paintingDrawingBean?.path)
        val pathName = FileUtils.getFileName(file.name)
        FileUtils.deleteFile(file.parent, pathName)//删除文件
        if (page>0){
            page -= 1
        }
        else{
            newHomeWorkContent()
        }
        changeContent()

    }

    override fun changeScreenPage() {
        super.changeScreenPage()
        if (isExpand){
            changeExpandContent()
        }
    }

    override fun onErasure() {
        if (isExpand){
            elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
        else{
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
    }


}