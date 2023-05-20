package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteTypeBeanDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.NoteTypeBean
import com.bll.lnkstudy.ui.adapter.NoteBookManagerAdapter
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class NoteTypeManagerActivity : BaseAppCompatActivity() {

    private var noteTypes= mutableListOf<NoteTypeBean>()
    private var mAdapter: NoteBookManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_notetype_manager
    }

    override fun initData() {
        noteTypes= NoteTypeBeanDaoManager.getInstance().queryAll()
    }

    override fun initView() {
        setPageTitle(R.string.note_manage_str)

        initRecyclerView()
    }


    private fun initRecyclerView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = NoteBookManagerAdapter(R.layout.item_notebook_manager, noteTypes).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                val noteType=noteTypes[position]
                this@NoteTypeManagerActivity.position=position
                if (view.id==R.id.iv_edit){
                    editNoteBook(noteType)
                }
                if (view.id==R.id.iv_delete){
                    setDeleteView()
                }
                if (view.id==R.id.iv_top){
                    val date=noteTypes[0].date
                    noteType.date=date-1000
                    NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteType)
                    Collections.swap(noteTypes,position,0)
                    setNotify()
                    DataUpdateManager.editDataUpdate(4,noteType.id.toInt(),0,1,Gson().toJson(noteType))
                }
            }
        }

    }

    //设置刷新通知
    private fun setNotify(){
        mAdapter?.notifyDataSetChanged()
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
        EventBus.getDefault().post(Constants.NOTE_EVENT)//更新全局通知
    }

    //删除
    private fun setDeleteView(){
        CommonDialog(this).setContent(R.string.notebook_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val noteType=noteTypes[position]
                noteTypes.removeAt(position)
                //删除笔记本
                NoteTypeBeanDaoManager.getInstance().deleteBean(noteType)

                val notebooks=NotebookDaoManager.getInstance().queryAll(noteType.name)
                //删除该笔记分类中的所有笔记本及其内容
                for (note in notebooks){
                    //删除当前笔记本增量更新
                    DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),1,1)
                    //获取所有内容
                    val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                    //删除当前笔记本内容增量更新
                    for (item in noteContents){
                        DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),2,1)
                    }
                    //本地笔记本以及笔记内容数据
                    NotebookDaoManager.getInstance().deleteBean(note)
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path= FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                }
                setNotify()
                DataUpdateManager.deleteDateUpdate(4,noteType.id.toInt(),0,1)
            }

        })
    }


    //修改笔记本
    private fun editNoteBook(noteType:NoteTypeBean){
        InputContentDialog(this,getCurrentScreenPos(),noteType.name).builder()?.setOnDialogClickListener { string ->
            noteType.name=string
            noteTypes[position].name = string
            NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteType)
            setNotify()
            DataUpdateManager.editDataUpdate(4,noteType.id.toInt(),0,1,Gson().toJson(noteType))
        }
    }

}