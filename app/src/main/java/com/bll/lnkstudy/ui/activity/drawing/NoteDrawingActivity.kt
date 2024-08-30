package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.model.note.NoteContentBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class NoteDrawingActivity : BaseDrawingActivity() {

    private var type =""
    private var noteBook: Note? = null
    private var note_Content_b: NoteContentBean? = null//当前内容
    private var note_Content_a: NoteContentBean? = null//a屏内容
    private var noteContents = mutableListOf<NoteContentBean>() //所有内容
    private var page = 0//页码
    private var grade=1

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id = intent.getLongExtra("noteId",0)
        page=intent.getIntExtra("page",DEFAULT_PAGE)
        noteBook = NoteDaoManager.getInstance().queryBean(id)
        type = noteBook?.typeStr.toString()
        grade=noteBook?.grade!!

        noteContents = NoteContentDaoManager.getInstance().queryAll(type,noteBook?.title,grade)

        if (noteContents.size > 0) {
            if (page==DEFAULT_PAGE)
                page = noteBook!!.page
            note_Content_b = noteContents[page]
        } else {
            newNoteContent()
        }
    }

    override fun initView() {
        disMissView(iv_btn)

        v_content_a?.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        v_content_b?.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景

        onContent()
    }

    override fun onCatalog() {
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
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=noteContents[position].page){
                    page = noteContents[position].page
                    onContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
                val item=noteContents[position]
                item.title=title
                NoteContentDaoManager.getInstance().insertOrReplaceNote(item)
                DataUpdateManager.editDataUpdate(4,item.id.toInt(),3,Gson().toJson(item))
            }
        })
    }

    override fun onPageDown() {
        val total=noteContents.size-1
        if(isExpand){
            when(page){
                total->{
                    newNoteContent()
                    newNoteContent()
                    page=noteContents.size-1
                }
                total-1->{
                    newNoteContent()
                    page=noteContents.size-1
                }
                else->{
                    page+=2
                }
            }
        }
        else{
            if (page >=total) {
                newNoteContent()
                page=noteContents.size-1
            } else {
                page += 1
            }
        }
        onContent()
    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                onContent()
            }
            else if (page==2){//当页面不够翻两页时
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

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        if (noteContents.size==1&&isExpand){
            newNoteContent()
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent() {
        note_Content_b = noteContents[page]

        if (isExpand) {
            if (page<=0){
                page=1
                note_Content_b = noteContents[page]
            }
            note_Content_a = noteContents[page-1]
        }

        tv_page_total.text="${noteContents.size}"
        tv_page_total_a.text="${noteContents.size}"

        setElikLoadPath(elik_b!!, note_Content_b!!.filePath)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, note_Content_a!!.filePath)
            if (screenPos==Constants.SCREEN_LEFT){
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
            if (screenPos==Constants.SCREEN_RIGHT){
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
        saveElik(elik_a!!,note_Content_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!,note_Content_b!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface,item: NoteContentBean){
        elik.saveBitmap(true) {}
        DataUpdateManager.editDataUpdate(4,item.id.toInt(),3)
    }

    //创建新的作业内容
    private fun newNoteContent() {

        val date=System.currentTimeMillis()
        val path=FileAddress().getPathNote(grade,type,noteBook?.title,date)
        val pathName = DateUtils.longToString(date)

        note_Content_b = NoteContentBean()
        note_Content_b?.date=date
        note_Content_b?.typeStr=type
        note_Content_b?.noteTitle = noteBook?.title
        note_Content_b?.resId = noteBook?.contentResId
        note_Content_b?.grade=grade
        note_Content_b?.title=getString(R.string.unnamed)+(noteContents.size+1)
        note_Content_b?.filePath = "$path/$pathName.png"
        note_Content_b?.page = noteContents.size
        page = noteContents.size

        val id=NoteContentDaoManager.getInstance().insertOrReplaceGetId(note_Content_b)
        note_Content_b?.id=id
        noteContents.add(note_Content_b!!)

        DataUpdateManager.createDataUpdate(4,id.toInt(),3,Gson().toJson(note_Content_b),path)
    }

    override fun onDestroy() {
        super.onDestroy()
        noteBook?.page=page
        NoteDaoManager.getInstance().insertOrReplace(noteBook)
    }

}