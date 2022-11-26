package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.NoteGreenDaoManager
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.mvp.model.Notebook
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_note_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseActivity() {

    private var type = 0
    private var noteBook: Notebook? = null
    private var note: Note? = null//当前内容
    private var note_a: Note? = null//a屏内容
    private var notes = mutableListOf<Note>() //所有内容
    private var page = 0//页码


    override fun layoutId(): Int {
        return R.layout.ac_note_drawing
    }

    override fun initData() {
        var bundle = intent.getBundleExtra("bundle")
        noteBook = bundle?.getSerializable("note") as Notebook
        type = noteBook?.type!!

        notes = NoteGreenDaoManager.getInstance().queryAll(type,noteBook?.id!!)

        if (notes.size > 0) {
            note = notes[notes.size - 1]
            page = notes.size - 1
        } else {
            newNoteContent()
        }

    }


    override fun initView() {

        v_content_a.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace

        changeContent()

        tv_title_a.setOnClickListener {
            var title=tv_title_a.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_a.text = string
                note_a?.title = string
                notes[page-1].title = string
                NoteGreenDaoManager.getInstance().insertOrReplaceNote(note_a)
            }
        }

        tv_title_b.setOnClickListener {
            var title=tv_title_b.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_b.text = string
                note?.title = string
                notes[page].title = string
                NoteGreenDaoManager.getInstance().insertOrReplaceNote(note)
            }
        }

        btn_page_down.setOnClickListener {
            val total=notes.size-1
            if(isExpand){
                when(page){
                    total->{
                        newNoteContent()
                        newNoteContent()
                        page==total
                    }
                    total-1->{
                        newNoteContent()
                        page==total
                    }
                    else->{
                        page+=2
                    }
                }
            }
            else{
                when(page){
                    total->{
                        newNoteContent()
                    }
                    else->{
                        page += 1
                    }
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

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand.setOnClickListener {
            if (notes.size==1){
                newNoteContent()
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

    private fun changeExpandView(){
        iv_expand.visibility=if (isExpand) View.GONE else View.VISIBLE
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
        var titleStr=""
        var list= mutableListOf<ListBean>()
        for (item in notes){
            val listBean= ListBean()
            listBean.name=item.title
            listBean.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(listBean)
            }

        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            page = notes[position].page
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent() {

        note = notes[page]

        if (isExpand) {
            if (page > 0) {
                note_a = notes[page - 1]
            }
            if (page==0){
                page=1
                note = notes[page]
                note_a = notes[page-1]
            }
        } else {
            note_a = null
        }


        tv_title_b.text=note?.title
        if (isExpand){
            tv_title_a.text=note_a?.title
        }

        updateUI()
    }

    //更新绘图以及页码
    private fun updateUI() {

        updateImage(elik_b!!, note?.filePath!!)
        tv_page_b.text = (page + 1).toString()

        if (isExpand) {
            if (note_a != null) {
                updateImage(elik_a!!, note_a?.filePath!!)
                tv_page_a.text = "$page"
            }
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
    private fun newNoteContent() {

        val path=FileAddress().getPathNote(type,noteBook?.id,notes.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        note = Note()
        note?.date = System.currentTimeMillis()
        note?.type=type
        note?.notebookId = noteBook?.id
        note?.resId = noteBook?.contentResId

        note?.title="未命名${notes.size+1}"
        note?.folderPath=path
        note?.filePath = "$path/$pathName.tch"
        note?.pathName=pathName
        note?.page = notes.size

        page = notes.size

        NoteGreenDaoManager.getInstance().insertOrReplaceNote(note)
        val id=NoteGreenDaoManager.getInstance().insertId
        note?.id=id

        notes.add(note!!)
    }



    //删除当前作业内容
    private fun delete() {
        CommonDialog(this,getCurrentScreenPos()).setContent("确认删除笔记？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                NoteGreenDaoManager.getInstance().deleteNote(note)
                notes.remove(note)
                FileUtils.deleteFile(note?.folderPath, note?.pathName)//删除文件
            }
        })
    }


   override fun changeScreenPage() {
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