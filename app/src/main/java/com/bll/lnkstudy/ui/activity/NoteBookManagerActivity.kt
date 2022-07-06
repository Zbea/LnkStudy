package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.NoteBookAddDialog
import com.bll.lnkstudy.manager.NoteBookGreenDaoManager
import com.bll.lnkstudy.manager.NoteGreenDaoManager
import com.bll.lnkstudy.mvp.model.NoteBook
import com.bll.lnkstudy.ui.adapter.NoteBookManagerAdapter
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class NoteBookManagerActivity : BaseActivity() {

    private var noteBooks= mutableListOf<NoteBook>()
    private var mAdapter: NoteBookManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_notebook_manager
    }

    override fun initData() {
        noteBooks= NoteBookGreenDaoManager.getInstance(this).queryAllNote()
    }

    override fun initView() {
        setPageTitle("笔记本管理")

        initRecyclerView()
    }


    private fun initRecyclerView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = NoteBookManagerAdapter(R.layout.item_notebook_manager, noteBooks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNoteBook(noteBooks[position].name)
            }
            if (view.id==R.id.iv_delete){
                setDeleteView()
            }
            if (view.id==R.id.iv_top){
                Collections.swap(noteBooks,position,0)
                setNotify()
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
        CommonDialog(this).setContent("确定要删除该条笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                var noteBook=noteBooks[position]
                noteBooks.removeAt(position)
                //删除笔记本
                NoteBookGreenDaoManager.getInstance(this@NoteBookManagerActivity).deleteNote(noteBook)
                //删除笔记本下的所有笔记
                NoteGreenDaoManager.getInstance(this@NoteBookManagerActivity).deleteType(noteBook.type)
                setNotify()
            }

        })
    }

    //修改笔记本
    private fun editNoteBook(content:String){
        NoteBookAddDialog(this,"重命名",content,"请输入笔记本").builder()?.setOnDialogClickListener(object :
            NoteBookAddDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                noteBooks[position].name=string
                NoteBookGreenDaoManager.getInstance(this@NoteBookManagerActivity).insertOrReplaceNote(noteBooks[position])
                setNotify()
            }
        })
    }

}