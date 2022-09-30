package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.PaintingDaoManager
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.mvp.model.PaintingBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_painting_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class PaintingDrawingActivity : BaseActivity() {

    private var type = 0
    private var popWindowDrawingButton: PopWindowDrawingButton? = null
    private var elik_a: EinkPWInterface? = null
    private var elik_b: EinkPWInterface? = null
    private var popWindow: PopWindowDrawSetting? = null

    private var paintingBean: PaintingBean? = null//当前作业内容
    private var paintingBean_a: PaintingBean? = null//a屏作业

    private var paintingLists = mutableListOf<PaintingBean>() //所有作业内容

    private var isExpand = false //是否是全屏

    private var page = 0//页码
    private var resId=0


    override fun layoutId(): Int {
        return R.layout.ac_painting_drawing
    }

    override fun initData() {

        type = intent.flags

        paintingLists = PaintingDaoManager.getInstance(this).queryAllByType(type)

        if (paintingLists.size > 0) {

            paintingBean = paintingLists[paintingLists.size - 1]

            page = paintingLists.size - 1

        } else {

            newHomeWorkContent()
            page = 0
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

        tv_title.setOnClickListener {
            var title=tv_title.text.toString()
            InputContentDialog(this,title).builder()?.setOnDialogClickListener(object :
                InputContentDialog.OnDialogClickListener {
                override fun onClick(string: String) {
                    tv_title.text=string
                    paintingBean?.title = string
                    paintingLists[page].title = string
                    PaintingDaoManager.getInstance(this@PaintingDrawingActivity).insertOrReplace(paintingBean)
                }

            })

        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        btn_page_down.setOnClickListener {

            if (page + 1 == paintingLists.size) {
                newHomeWorkContent()
            } else {
                page += 1
            }
            changeContent()
        }

        btn_page_up.setOnClickListener {
            //全屏时 page最小为1
            val min=if (isExpand) 1 else 0
            if (page > min) {
                page -= 1
                changeContent()
            }
        }

        iv_expand.setOnClickListener {
            isExpand=!isExpand
            moveToScreen(isExpand)
            changeExpandView()
            changeContent()
        }


        iv_btn.setOnClickListener {
            showPopWindowBtn()
        }


    }

    private fun changeExpandView(){
        v_content_b.visibility = if(isExpand) View.VISIBLE else View.GONE
        tv_page_b.visibility = if(isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var list= mutableListOf<ListBean>()
        for (item in paintingLists){
            val listBean=ListBean()
            listBean.name=item.title
            listBean.page=item.page
            list.add(listBean)
        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!= position) {
                    page = position
                    changeContent()
                }
            }
        })
    }


    //设置背景图
    private fun setBg(){
        v_content_a.setImageResource(resId)
        v_content_b.setImageResource(resId)
    }

    //翻页内容更新切换
    private fun changeContent() {

        paintingBean = paintingLists[page]

        if (isExpand) {
            if (page > 0) {
                paintingBean_a = paintingLists[page - 1]
            } else {
                if (paintingLists.size > 1) {
                    paintingBean = paintingLists[page + 1]
                    paintingBean_a = paintingLists[page]
                    page = 1
                } else {
                    paintingBean_a = null
                }
            }
        } else {
            paintingBean_a = null
        }

        //切换页面内容的一些变化
        tv_title.text=paintingBean?.title
        if (paintingBean?.title.isNullOrEmpty())
        {
            tv_title.hint="输入标题"
        }

        if (isExpand) {
            updateImage(elik_b!!, paintingBean?.path!!)
            tv_page_b.text = (page + 1).toString()

            if (paintingBean_a != null) {
                v_content_a.setImageResource(resId)
                updateImage(elik_a!!, paintingBean_a?.path!!)
                tv_page_a.text = "$page"
            }
            else{
                //当只存在一个页面全屏展示时候 a屏不显示东西不能手写
                v_content_a.setImageResource(0)
                elik_a?.setPWEnabled(false)
                tv_page_a.text = ""
            }

        } else {
            v_content_a.setImageResource(resId)
            updateImage(elik_a!!, paintingBean?.path!!)
            tv_page_a.text = (page + 1).toString()
        }
    }

    //保存绘图以及更新手绘
    private fun updateImage(elik: EinkPWInterface, path: String) {
        elik?.setPWEnabled(true)
        elik?.setLoadFilePath(path, true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }

        })
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path = Constants.PAINTING_PATH + "/$mUserId/$type"
        val date = DateUtils.longToString(System.currentTimeMillis())

        paintingBean = PaintingBean()
        paintingBean?.type = type
        paintingBean?.date = System.currentTimeMillis()
        paintingBean?.path = "$path/$date.tch"
        page = paintingLists.size
        paintingBean?.page=page
        paintingLists.add(paintingBean!!)

        PaintingDaoManager.getInstance(this).insertOrReplace(paintingBean)

    }


    //
    private fun showPopWindowBtn() {
        val flag = if (type == 0) 2 else 3
        val yoff=if (type == 0) -270 else -200
        if (popWindowDrawingButton == null) {
            popWindowDrawingButton = PopWindowDrawingButton(this, iv_btn, flag,yoff).builder()
            popWindowDrawingButton?.setOnSelectListener(object : PopWindowDrawingButton.OnClickListener {
                override fun onClick(type: Int) {
                    if (type==3){
                        delete()
                    }
                    if (type==4){
                        resId = if (resId==0){
                            R.mipmap.icon_painting_bg_hb
                        } else{
                            0
                        }
                        setBg()
                    }
                }
            })
        } else {
            popWindowDrawingButton?.show()
        }
    }


    //手绘设置
    private fun drawSetting() {
        if (popWindow == null) {
            popWindow = PopWindowDrawSetting(this, iv_catalog).builder()
            popWindow?.setOnSelectListener(object : PopWindowDrawSetting.OnSelectListener {
                override fun onSelect(type: Int) {
                    if (type == 1) {
                        elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
                        elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
                    }
                    if (type == 2) {
                        elik_a?.clearContent(null, true, true)
                        elik_b?.clearContent(null, true, true)
                        if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                    }
                    if (type == 3) {
                        if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik_a?.penSettingWidth = 2

                        if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik_b?.penSettingWidth = 2
                    }
                    if (type == 4) {
                        if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik_a?.penSettingWidth = 6

                        if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik_b?.penSettingWidth = 6
                    }

                }
            })
        } else {
            if (popWindow?.isShow() == true) {
                popWindow?.dismiss()
                if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                    elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                }
                if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                    elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                }
            } else {
                popWindow?.show()
            }

        }
    }

    //确认删除
    private fun delete() {
        if (paintingLists.size > 1) {
            CommonDialog(this).setContent("确认删除？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    deleteContent()
                }
            })
        }
    }

    //删除作业
    private fun deleteContent() {

        PaintingDaoManager.getInstance(this).deleteBean(paintingBean)
        paintingLists.remove(paintingBean)
        val file = File(paintingBean?.path)
        val pathName = FileUtils.getFileName(file.name)
        FileUtils.deleteFile(file.parent, pathName)//删除文件
        if (page>0){
            page -= 1
        }
        changeContent()

    }

    override fun onPause() {
        super.onPause()
        if (isExpand){
            isExpand=!isExpand
            moveToScreen(isExpand)
            changeExpandView()
            changeContent()
        }
    }


}