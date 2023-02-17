package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.NotebookAddDialog
import com.bll.lnkstudy.manager.BaseTypeBeanDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.ui.adapter.NoteBookManagerAdapter
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class NoteTypeManagerActivity : BaseAppCompatActivity() {

    private var noteTypes= mutableListOf<BaseTypeBean>()
    private var mAdapter: NoteBookManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_notetype_manager
    }

    override fun initData() {
        noteTypes= BaseTypeBeanDaoManager.getInstance().queryAll()
    }

    override fun initView() {
        setPageTitle("笔记本管理")

        initRecyclerView()
    }


    private fun initRecyclerView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = NoteBookManagerAdapter(R.layout.item_notebook_manager, noteTypes)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNoteBook(noteTypes[position].name)
            }
            if (view.id==R.id.iv_delete){
                setDeleteView()
            }
            if (view.id==R.id.iv_top){
                val date=noteTypes[0].date
                noteTypes[position].date=date-1000
                BaseTypeBeanDaoManager.getInstance().insertOrReplace(noteTypes[position])
                Collections.swap(noteTypes,position,0)
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
                val noteType=noteTypes[position]
                noteTypes.removeAt(position)
                //删除笔记本
                BaseTypeBeanDaoManager.getInstance().deleteBean(noteType)

                val notebooks=NotebookDaoManager.getInstance().queryAll(noteType.typeId)
                //删除该笔记分类中的所有笔记本及其内容
                for (note in notebooks){
                    NotebookDaoManager.getInstance().deleteBean(note)
                    NoteContentDaoManager.getInstance().deleteType(note.type,note.id)
                }

                setNotify()
            }

        })
    }

    //修改笔记本
    private fun editNoteBook(content:String){
        NotebookAddDialog(this,getCurrentScreenPos(),"重命名",content,"请输入笔记本").builder()?.setOnDialogClickListener { string ->
            noteTypes[position].name = string
            BaseTypeBeanDaoManager.getInstance()
                .insertOrReplace(noteTypes[position])
            setNotify()
        }
    }

}