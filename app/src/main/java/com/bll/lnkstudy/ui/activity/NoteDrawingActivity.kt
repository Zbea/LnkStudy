package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.PopWindowDrawSetting
import com.bll.lnkstudy.dialog.PopWindowDrawingButton
import com.bll.lnkstudy.manager.NoteGreenDaoManager
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_note_draw_details.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus

class NoteDrawingActivity:BaseAppCompatActivity() ,View.OnClickListener{
    private var note:Note?=null
    private var elik: EinkPWInterface?=null
    private var notes= mutableListOf<Note>()

    private var popWindow: PopWindowDrawSetting?=null
    private var path=""//文件夹目录地址
    private var paths= mutableListOf<String>()
    private var currentPath=""//当前图片地址
    private var pos=0//当前下标

    override fun layoutId(): Int {
        return R.layout.ac_note_draw_details
    }

    override fun initData() {
        note=intent.getBundleExtra("notes")?.getSerializable("note") as Note
        note?.nowDate=System.currentTimeMillis()
        notes=NoteGreenDaoManager.getInstance(this).queryAllNote(note?.type!!)
        if (note?.paths!=null){
            paths= note?.paths!!
            path= note?.path.toString()
        }
        else{
            path=FileAddress().getPathNote(note?.type,notes.size+1)
        }
    }

    override fun initView() {
        tv_title.text=note?.title

        disMissView(iv_tool_left)
        disMissView(iv_expand)

        btn_page_down.setOnClickListener(this)
        btn_page_up.setOnClickListener(this)
        iv_btn.setOnClickListener(this)

        v_content.setImageResource(ToolUtils.getImageResId(this,note?.resId))
        elik=v_content.pwInterFace

        setViewChange()

    }

    override fun onClick(view: View?) {
        if (view==btn_page_down){
            pos += 1
            setViewChange()
        }
        if (view==btn_page_up){
            if (pos>0){
                pos-=1
                setViewChange()
            }
        }
        if (view==iv_btn){
            showPopWindowBtn()
        }
    }

    //得到新路径
    private fun getNewPath():String{
        return "$path/${DateUtils.longToString(System.currentTimeMillis())}.tch"
    }


    //查看内容时的变化
    private fun setViewChange(){
        if (pos>=paths.size){ //用来处理 查看笔记时 如果点击新增下一页
            paths.add(getNewPath())
        }
        changePageView()
    }

    private fun changePageView(){
        currentPath=paths[pos]
        elik?.setLoadFilePath(currentPath,true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }

        })
        tv_page_a.text=(pos+1).toString()
    }



    private fun showDrawSetting(){
        if (popWindow==null)
        {
            popWindow=PopWindowDrawSetting(this,null).builder()
            popWindow?.setOnSelectListener(object : PopWindowDrawSetting.OnSelectListener {
                override fun onSelect(type: Int) {
                    if (type==1){
                        elik?.drawObjectType=PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
                    }
                    if (type==2){
                        elik?.clearContent(null,true,true)
                        if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                    }
                    if (type==3){
                        if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik?.penSettingWidth=2
                    }
                    if (type==4){
                        if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                            elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                        }
                        elik?.penSettingWidth=6
                    }

                }
            })
        }
        else{
            if (popWindow?.isShow()==true){
                popWindow?.dismiss()
                if (elik?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                    elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
                }
            }
            else{
                popWindow?.show()
            }

        }
    }

    //点击按钮弹框
    private fun showPopWindowBtn() {

         PopWindowDrawingButton(this, iv_btn, 4,-270).builder()
        ?.setOnSelectListener(object : PopWindowDrawingButton.OnClickListener {
            override fun onClick(type: Int) {
                if (type==1){
                    elik?.saveBitmap(true) {
                        note?.paths=paths
                        note?.path=path
                        NoteGreenDaoManager.getInstance(this@NoteDrawingActivity).insertOrReplaceNote(note)
                        EventBus.getDefault().post(NOTE_EVENT)

                        finish()
                    }
                }

                if (type==3){
                    if (paths.size>1){
                        CommonDialog(this@NoteDrawingActivity).setContent("确定删除当前页？").builder()
                            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    paths.remove(currentPath)
                                    if(pos>0){
                                        pos-=1
                                    }
                                    FileUtils.deleteFile(path,FileUtils.getFileName(currentPath))
                                    changePageView()
                                }
                            })
                    }
                }

            }
        })
    }


}