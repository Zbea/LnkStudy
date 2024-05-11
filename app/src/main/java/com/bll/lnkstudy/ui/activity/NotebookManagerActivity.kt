package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.ui.adapter.NoteBookManagerAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_list.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class NotebookManagerActivity : BaseAppCompatActivity() {

    private var notebooks= mutableListOf<ItemTypeBean>()
    private var mAdapter: NoteBookManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        notebooks= ItemTypeDaoManager.getInstance().queryAll(2)
    }

    override fun initView() {
        setPageTitle(R.string.note_manage_str)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f),
            DP2PX.dip2px(this,100f),DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = NoteBookManagerAdapter(R.layout.item_notebook_manager, notebooks).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                val notebook=notebooks[position]
                this@NotebookManagerActivity.position=position
                if (view.id==R.id.iv_edit){
                    editNotebook(notebook.title)
                }
                if (view.id==R.id.iv_delete){
                    deleteNotebook()
                }
                if (view.id==R.id.iv_top){
                    val date=notebooks[0].date
                    notebook.date=date-1000
                    ItemTypeDaoManager.getInstance().insertOrReplace(notebook)
                    Collections.swap(notebooks,position,0)
                    setNotify()
                    DataUpdateManager.editDataUpdate(4,notebook.id.toInt(),1,2,Gson().toJson(notebook))
                }
            }
        }

    }

    //设置刷新通知
    private fun setNotify(){
        mAdapter?.notifyDataSetChanged()
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
    }

    //删除
    private fun deleteNotebook(){
        CommonDialog(this).setContent(R.string.notebook_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val noteBook=notebooks[position]
                notebooks.removeAt(position)
                //删除笔记本
                ItemTypeDaoManager.getInstance().deleteBean(noteBook)
                DataUpdateManager.deleteDateUpdate(4,noteBook.id.toInt(),1,2)
                val notes= NoteDaoManager.getInstance().queryAll(noteBook.title)
                //删除该笔记分类中的所有笔记本及其内容
                for (note in notes){
                    //删除当前笔记本增量更新
                    DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),2,2)
                    //获取所有内容
                    val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                    //删除当前笔记本内容增量更新
                    for (item in noteContents){
                        DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),3,2)
                    }
                    //本地笔记本以及笔记内容数据
                    NoteDaoManager.getInstance().deleteBean(note)
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path= FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                }
                setNotify()
            }

        })
    }


    //修改笔记本
    private fun editNotebook(name: String){
        InputContentDialog(this,getCurrentScreenPos(),name).builder().setOnDialogClickListener { string ->
            if (ItemTypeDaoManager.getInstance().isExist(string,2)){
                showToast(R.string.toast_existed)
                return@setOnDialogClickListener
            }
            val notebook=notebooks[position]
            //修改笔记本所有笔记以及内容
            val notes=NoteDaoManager.getInstance().queryAll(notebook.title)
            for (note in notes){
                val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                for (noteContent in noteContents){
                    noteContent.typeStr=string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                    //修改增量更新
                    DataUpdateManager.editDataUpdate(4,noteContent.id.toInt(),3,2,Gson().toJson(noteContent))
                }
                note.typeStr=string
                NoteDaoManager.getInstance().insertOrReplace(note)
                //修改增量更新
                DataUpdateManager.editDataUpdate(4,note.id.toInt(),2,2,Gson().toJson(note))
            }
            notebook.title = string
            ItemTypeDaoManager.getInstance().insertOrReplace(notebook)
            setNotify()
            //修改增量更新
            DataUpdateManager.editDataUpdate(4,notebook.id.toInt(),1,2,Gson().toJson(notebook))
        }
    }

}