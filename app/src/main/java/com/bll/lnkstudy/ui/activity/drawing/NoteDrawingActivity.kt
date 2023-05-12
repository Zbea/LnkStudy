package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.View
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.NoteContentBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseDrawingActivity() {

    private var type = ""
    private var noteBook: NotebookBean? = null
    private var noteContent: NoteContentBean? = null//当前内容
    private var note_Content_a: NoteContentBean? = null//a屏内容
    private var noteContents = mutableListOf<NoteContentBean>() //所有内容
    private var page = 0//页码
    private var grade=1

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("bundle")
        noteBook = bundle?.getSerializable("note") as NotebookBean
        type = noteBook?.typeStr.toString()
        grade=noteBook?.grade!!

        noteContents = NoteContentDaoManager.getInstance().queryAll(type,noteBook?.title,grade)

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

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand.setOnClickListener {
            if (noteContents.size==1){
                newNoteContent()
            }
            onChangeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            onChangeExpandContent()
        }

    }

    override fun onPageDown() {
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

    override fun onChangeExpandContent() {
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
    private fun newNoteContent() {

        val path=FileAddress().getPathNote(type,noteBook?.title,grade)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        noteContent = NoteContentBean()
        noteContent?.date = System.currentTimeMillis()
        noteContent?.typeStr=type
        noteContent?.notebookTitle = noteBook?.title
        noteContent?.resId = noteBook?.contentResId
        noteContent?.grade=grade
        noteContent?.title=getString(R.string.unnamed)+(noteContents.size+1)
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

   override fun changeScreenPage() {
        if (isExpand){
            onChangeExpandContent()
        }
    }

    override fun setDrawingTitle_a(title: String) {
        note_Content_a?.title = title
        noteContents[page-1].title = title
        NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_a)
    }

    override fun setDrawingTitle_b(title: String) {
        noteContent?.title = title
        noteContents[page].title = title
        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
    }

}