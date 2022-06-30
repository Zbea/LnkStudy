package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.NoteAddDialog
import com.bll.lnkstudy.dialog.NoteBookAddDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.NoteBookGreenDaoManager
import com.bll.lnkstudy.manager.NoteGreenDaoManager
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.mvp.model.NoteBook
import com.bll.lnkstudy.ui.activity.NoteBookManagerActivity
import com.bll.lnkstudy.ui.adapter.NoteAdapter
import com.bll.lnkstudy.ui.adapter.NoteBookManagerAdapter
import com.bll.lnkstudy.utils.PopWindowUtil
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.greendao.NoteBookConverter
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 笔记
 */
class NoteFragment : BaseFragment(){

    private var popWindow:PopWindowUtil?=null
    private var dialog:NoteAddDialog?=null
    private var allNoteBooks= mutableListOf<NoteBook>()
    private var noteBooks= mutableListOf<NoteBook>()
    private var notes= mutableListOf<Note>()
    private var type=0 //当前笔记本类型
    private var mAdapter: NoteAdapter? = null
    private var position=0 //当前笔记标记

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {

        EventBus.getDefault().register(this)

        setPageTitle("笔记")
        setDisBackShow()
        setShowNoteAdd()
        bindClick()

        initTab()

        initRecyclerView()
        initData()
    }

    override fun lazyLoad() {
    }

    private fun initData(){
        notes=NoteGreenDaoManager.getInstance(activity).queryAllNote(type)
        mAdapter?.setNewData(notes)
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NoteAdapter(R.layout.item_note, notes)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNote(notes[position].title)
            }
            if (view.id==R.id.iv_delete){
                setDeleteNote()
            }

        }
    }

    //设置头部索引
    private fun initTab(){

        allNoteBooks=DataBeanManager.getIncetance().noteBook
        noteBooks=NoteBookGreenDaoManager.getInstance(activity).queryAllNote()
        if (noteBooks.size<1){
            val noteBook2 = NoteBook()
            noteBook2.name = "金句彩段"
            noteBook2.type=2
            noteBooks.add(noteBook2)
            NoteBookGreenDaoManager.getInstance(activity).insertOrReplaceNote(noteBook2)

            val noteBook3 = NoteBook()
            noteBook3.name = "典型题型"
            noteBook3.type=3
            noteBooks.add(noteBook3)
            NoteBookGreenDaoManager.getInstance(activity).insertOrReplaceNote(noteBook3)
        }
        allNoteBooks.addAll(noteBooks)

        xtab?.removeAllTabs()
        for (notebook in allNoteBooks){
            xtab?.newTab()?.setText(notebook.name)?.let { it -> xtab?.addTab(it) }
        }

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                type= tab?.position!!
                initData()
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }

    private fun bindClick(){
        tv_add.setOnClickListener {
            dialog=NoteAddDialog(requireContext()).builder()
            dialog?.setOnDialogClickListener(object :
                NoteAddDialog.OnDialogClickListener {
                override fun onClick(type:Int) {
                    addNote()
                }
            })
        }

        ivManagers?.setOnClickListener {
            setTopSelectView()
        }

    }

    //新建笔记
    private fun addNote(){
        NoteBookAddDialog(requireContext(),"新建笔记","","请输入笔记标题").builder()?.setOnDialogClickListener(object :
            NoteBookAddDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                var note=Note()
                note.title=string
                note.date=System.currentTimeMillis()
                note.type=type
                notes.add(note)
                mAdapter?.setNewData(notes)
                NoteGreenDaoManager.getInstance(activity).insertOrReplaceNote(note)

                dialog?.dismiss()
            }
        })
    }

    //修改笔记
    private fun editNote(content:String){
        NoteBookAddDialog(requireContext(),"重命名",content,"请输入笔记标题").builder()?.setOnDialogClickListener(object :
            NoteBookAddDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                notes[position].title=string
                mAdapter?.notifyDataSetChanged()
                NoteGreenDaoManager.getInstance(activity).insertOrReplaceNote(notes[position])
            }
        })
    }

    //删除
    private fun setDeleteNote(){
        CommonDialog(activity).setContent("确定要删除笔记？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    var note=notes[position]
                    notes.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    NoteGreenDaoManager.getInstance(activity).deleteNote(note)
                }

            })
    }


    //顶部弹出选择
    private fun setTopSelectView(){
        if (popWindow==null){
            val popView = LayoutInflater.from(activity).inflate(R.layout.popwindow_notebook, null, false)
            val llAdd=popView?.findViewById<LinearLayout>(R.id.ll_add)
            val ivAdd=popView?.findViewById<ImageView>(R.id.iv_select_add)
            val llManager=popView?.findViewById<LinearLayout>(R.id.ll_manager)
            val ivManager=popView?.findViewById<ImageView>(R.id.iv_select_manager)
            llAdd?.setOnClickListener {
                ivAdd?.visibility= View.VISIBLE
                ivManager?.visibility= View.GONE
                popWindow?.dismiss()
                addNoteBook()
            }
            llManager?.setOnClickListener {
                ivAdd?.visibility= View.GONE
                ivManager?.visibility= View.VISIBLE
                popWindow?.dismiss()
                startActivity(Intent(activity,NoteBookManagerActivity::class.java))
            }
            popWindow= PopWindowUtil().makePopupWindow(activity,ivManagers,popView, -180,5, Gravity.LEFT)
            popWindow?.show()
        }
        else{
            popWindow?.show()
        }
    }


    //新建笔记本
    private fun addNoteBook(){
        NoteBookAddDialog(requireContext(),"新建笔记本","","请输入笔记本").builder()?.setOnDialogClickListener(object :
                NoteBookAddDialog.OnDialogClickListener {
                override fun onClick(string: String) {
                    var noteBook=NoteBook()
                    noteBook.name=string
                    noteBook.type=allNoteBooks.size
                    noteBooks.add(noteBook)
                    NoteBookGreenDaoManager.getInstance(activity).insertOrReplaceNote(noteBook)
                    xtab?.newTab()?.setText(string)?.let { it -> xtab?.addTab(it) }
                }
            })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.NOTE_BOOK_MANAGER_EVENT){
            initTab()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}