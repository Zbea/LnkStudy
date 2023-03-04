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
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.NoteContentBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseActivity() {

    private var type = 0
    private var noteBook: NotebookBean? = null
    private var noteContent: NoteContentBean? = null//当前内容
    private var note_Content_a: NoteContentBean? = null//a屏内容
    private var noteContents = mutableListOf<NoteContentBean>() //所有内容
    private var page = 0//页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        var bundle = intent.getBundleExtra("bundle")
        noteBook = bundle?.getSerializable("note") as NotebookBean
        type = noteBook?.type!!

        noteContents = NoteContentDaoManager.getInstance().queryAll(type,noteBook?.id!!)

        if (noteContents.size > 0) {
            noteContent = noteContents[noteContents.size - 1]
            page = noteContents.size - 1
        } else {
            newNoteContent()
        }

    }


    override fun initView() {

        v_content_a.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景

        changeContent()

        tv_title_a.setOnClickListener {
            var title=tv_title_a.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_a.text = string
                note_Content_a?.title = string
                noteContents[page-1].title = string
                NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_a)
            }
        }

        tv_title_b.setOnClickListener {
            var title=tv_title_b.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                tv_title_b.text = string
                noteContent?.title = string
                noteContents[page].title = string
                NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
            }
        }

        btn_page_down.setOnClickListener {
            val total=noteContents.size-1
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
            if (noteContents.size==1){
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
        var list= mutableListOf<ItemList>()
        for (item in noteContents){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(itemList)
            }

        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            page = noteContents[position].page
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent() {

        noteContent = noteContents[page]

        if (isExpand) {
            if (page > 0) {
                note_Content_a = noteContents[page - 1]
            }
            if (page==0){
                page=1
                noteContent = noteContents[page]
                note_Content_a = noteContents[page-1]
            }
        } else {
            note_Content_a = null
        }


        tv_title_b.text=noteContent?.title
        if (isExpand){
            tv_title_a.text=note_Content_a?.title
        }

        updateUI()
    }

    //更新绘图以及页码
    private fun updateUI() {

        updateImage(elik_b!!, noteContent?.filePath!!)
        tv_page_b.text = (page + 1).toString()

        if (isExpand) {
            if (note_Content_a != null) {
                updateImage(elik_a!!, note_Content_a?.filePath!!)
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

        val path=FileAddress().getPathNote(type,noteBook?.id,noteContents.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        noteContent = NoteContentBean()
        noteContent?.date = System.currentTimeMillis()
        noteContent?.type=type
        noteContent?.notebookId = noteBook?.id
        noteContent?.resId = noteBook?.contentResId

        noteContent?.title="未命名${noteContents.size+1}"
        noteContent?.folderPath=path
        noteContent?.filePath = "$path/$pathName.tch"
        noteContent?.pathName=pathName
        noteContent?.page = noteContents.size

        page = noteContents.size

        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
        val id= NoteContentDaoManager.getInstance().insertId
        noteContent?.id=id

        noteContents.add(noteContent!!)
    }



    //删除当前作业内容
    private fun delete() {
        CommonDialog(this,getCurrentScreenPos()).setContent("确认删除笔记？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                NoteContentDaoManager.getInstance().deleteNote(noteContent)
                noteContents.remove(noteContent)
                FileUtils.deleteFile(noteContent?.folderPath, noteContent?.pathName)//删除文件
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