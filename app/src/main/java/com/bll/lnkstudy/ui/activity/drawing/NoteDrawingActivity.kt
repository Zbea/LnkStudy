package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.model.note.NoteContentBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseDrawingActivity() {

    private var type =""
    private var typeId=0
    private var noteBook: Note? = null
    private var noteContent: NoteContentBean? = null//当前内容
    private var note_Content_a: NoteContentBean? = null//a屏内容
    private var noteContents = mutableListOf<NoteContentBean>() //所有内容
    private var page = 0//页码
    private var grade=1

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("noteBundle")
        noteBook = bundle?.getSerializable("note") as Note
        page=intent.getIntExtra("page",DEFAULT_PAGE)
        type = noteBook?.typeStr.toString()
        grade=noteBook?.grade!!
        typeId=if (type==getString(R.string.note_tab_diary)) 1 else 2


        noteContents = NoteContentDaoManager.getInstance().queryAll(type,noteBook?.title,grade)

        if (noteContents.size > 0) {
            if (page==DEFAULT_PAGE)
                page = noteContents.size - 1
            noteContent = noteContents[page]
        } else {
            newNoteContent()
        }
    }


    override fun initView() {

        v_content_a.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景

        changeExpandView()
        changeContent()

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand_left.setOnClickListener {
            if (noteContents.size==1){
                newNoteContent()
            }
            onChangeExpandContent()
        }
        iv_expand_right.setOnClickListener {
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
                    page=total
                }
                total-1->{
                    newNoteContent()
                    page=total
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

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var titleStr=""
        val list= mutableListOf<ItemList>()
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

        setElikLoadPath(elik_b!!, noteContent!!)
        tv_page_b.text = (page + 1).toString()

        if (isExpand) {
            if (note_Content_a != null) {
                setElikLoadPath(elik_a!!, note_Content_a!!)
                tv_page_a.text = "$page"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, noteContentBean: NoteContentBean) {
        elik.setLoadFilePath(noteContentBean.filePath, true)
    }

    override fun onElikSava_a() {
        saveElik(elik_a!!,note_Content_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!,noteContent!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface,item: NoteContentBean){
        elik.saveBitmap(true) {}
        DataUpdateManager.editDataUpdate(4,item.id.toInt(),3,typeId)
    }

    //创建新的作业内容
    private fun newNoteContent() {

        val date=System.currentTimeMillis()
        val path=FileAddress().getPathNote(grade,type,noteBook?.title,date)
        val pathName = DateUtils.longToString(date)

        noteContent = NoteContentBean()
        noteContent?.date=date
        noteContent?.typeStr=type
        noteContent?.noteTitle = noteBook?.title
        noteContent?.resId = noteBook?.contentResId
        noteContent?.grade=grade
        noteContent?.title=getString(R.string.unnamed)+(noteContents.size+1)
        noteContent?.filePath = "$path/$pathName.tch"
        noteContent?.page = noteContents.size
        page = noteContents.size

        val id=NoteContentDaoManager.getInstance().insertOrReplaceGetId(noteContent)
        noteContent?.id=id
        noteContents.add(noteContent!!)

        DataUpdateManager.createDataUpdate(4,id.toInt(),3,typeId,Gson().toJson(noteContent),path)
    }

    override fun setDrawingTitle_a(title: String) {
        note_Content_a?.title = title
        noteContents[page-1].title = title
        NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_a)
        DataUpdateManager.editDataUpdate(4,note_Content_a?.id!!.toInt(),3,typeId,Gson().toJson(note_Content_a))
    }

    override fun setDrawingTitle_b(title: String) {
        noteContent?.title = title
        noteContents[page].title = title
        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
        DataUpdateManager.editDataUpdate(4,noteContent?.id!!.toInt(),3,typeId,Gson().toJson(noteContent))
    }

}