package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.BaseTypeBeanDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.ui.activity.NoteTypeManagerActivity
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 笔记
 */
class NoteFragment : BaseFragment() {
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var popWindowMoreBeans = mutableListOf<PopupBean>()
    private var noteTypes = mutableListOf<BaseTypeBean>()
    private var noteBooks = mutableListOf<NotebookBean>()
    private var type = 0 //当前笔记本类型
    private var mAdapter: NotebookAdapter? = null
    private var mAdapterType: BookCaseTypeAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var positionType = 0//当前笔记本标记
    private var isDown = false //是否向下打开

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        pageSize=10

        popWindowBeans.add(PopupBean(0, getString(R.string.note_manage_str), true))
        popWindowBeans.add(PopupBean(1, getString(R.string.notebook_create_str), false))
        popWindowBeans.add(PopupBean(2, getString(R.string.note_create_str), false))

        popWindowMoreBeans.add(PopupBean(0, getString(R.string.rename), true))
        popWindowMoreBeans.add(PopupBean(1, getString(R.string.delete), false))

        EventBus.getDefault().register(this)

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        bindClick()

        initTab()

        initRecyclerView()
        findTabs()
    }

    override fun lazyLoad() {
    }


    private fun initRecyclerView() {
        mAdapter = NotebookAdapter(R.layout.item_note, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val notebook=noteBooks[position]
                if (type==0){
                    if (notebook.isEncrypt){
                        NotebookPasswordDialog(requireContext(),screenPos).builder()?.setOnDialogClickListener{
                            gotoIntent(notebook)
                        }
                    }
                    else{
                        gotoIntent(notebook)
                    }

                }else{
                    gotoIntent(notebook)
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@NoteFragment.position = position
                if (view.id == R.id.iv_encrypt) {
                    setPassword()
                }
                if (view.id == R.id.iv_more) {
                    PopupList(requireActivity(), popWindowMoreBeans, view, 0).builder()
                        .setOnSelectListener { item ->
                            when (item.id) {
                                0 -> {
                                    editNotebook(noteBooks[position].title)
                                }
                                else -> {
                                    deleteNotebook()
                                }
                            }
                        }
                }

            }
        }

    }

    //设置头部索引
    private fun initTab() {
        mAdapterType = BookCaseTypeAdapter(R.layout.item_bookcase_type, noteTypes).apply {
            rv_type.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
            rv_type.adapter = this
            bindToRecyclerView(rv_type)
            rv_type.addItemDecoration(SpaceGridItemDeco1(5,DP2PX.dip2px(activity,22f),20))
            setOnItemClickListener { _, _, position ->
                noteTypes[positionType].isCheck = false
                positionType = position
                noteTypes[positionType].isCheck = true
                notifyDataSetChanged()
                type = noteTypes[positionType].typeId
                pageIndex=1
                pageCount=1
                fetchData()
            }
        }

    }

    private fun bindClick() {

        iv_manager?.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                    }
                    1 -> {
                        addNoteBookType()
                    }
                    else -> {
                        val list=if (type==0) DataBeanManager.noteModuleDiary else DataBeanManager.noteModuleBook
                        ModuleAddDialog(requireContext(),screenPos,getString(R.string.note_module_str),list).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                                createNotebook()
                            }
                    }
                }
            }
        }

        iv_down.setOnClickListener {
            if (isDown) {
                isDown = false
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_down)
            } else {
                isDown = true
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_up)
            }
            findTabs()
        }

    }

    /**
     * tab数据设置
     */
    private fun findTabs() {
        noteTypes = DataBeanManager.noteBook
        noteTypes.addAll( BaseTypeBeanDaoManager.getInstance().queryAll())
        setAllCheckFalse(noteTypes)

        //删除tab后当前下标超出
        if (noteTypes.size<=positionType){
            positionType = 0
        }

        //不展开 下标超过4
        if (!isDown&&positionType>4){
            positionType = 0
        }

        noteTypes[positionType].isCheck = true
        if (!isDown&&noteTypes.size>5){
            noteTypes = noteTypes.subList(0, 5)
        }

        mAdapterType?.setNewData(noteTypes)
        type = noteTypes[positionType].typeId
        fetchData()
    }


    //设置所有数据为不选中
    private fun setAllCheckFalse(tabs:List<BaseTypeBean>){
        for (item in tabs){
            item.isCheck=false
        }
    }

    /**
     * 跳转笔记写作
     */
    private fun gotoIntent(note: NotebookBean){
        val intent = Intent(activity, NoteDrawingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("note", note)
        intent.putExtra("bundle", bundle)
        customStartActivity(intent)
    }

    /**
     * 日记设置密码
     */
    private fun setPassword(){
        when (val notePassword=SPUtil.getObj("notePassword",NotePassword::class.java)) {
            null -> NotebookSetPasswordDialog(requireContext(),screenPos).builder().setOnDialogClickListener {
                val note = noteBooks[position]
                note.isEncrypt=true
                note.encrypt=SPUtil.getObj("notePassword",NotePassword::class.java)?.password
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(note)
            }
            else -> if(noteBooks[position].isEncrypt){
                NotebookDeletePasswordDialog(requireContext(),screenPos).builder()?.setOnDialogClickListener{
                    val note = noteBooks[position]
                    note.isEncrypt=false
                    note.encrypt=""
                    mAdapter?.notifyDataSetChanged()
                    NotebookDaoManager.getInstance().insertOrReplace(note)
                }
            }
            else{
                CommonDialog(requireActivity(),screenPos).setContent(R.string.note_is_set_diary_password_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            val note = noteBooks[position]
                            note.isEncrypt=true
                            note.encrypt=notePassword.password
                            mAdapter?.notifyDataSetChanged()
                            NotebookDaoManager.getInstance().insertOrReplace(note)
                        }

                    })
            }
        }
    }


    //新建笔记
    private fun createNotebook() {
        val note = NotebookBean()
        if (type==0) NotebookAddDiaryDialog(requireContext(), screenPos).builder()
            ?.setOnDialogClickListener{ name,dateStr->
                note.title = name
                note.createDate = System.currentTimeMillis()
                note.type = type
                note.dateStr=dateStr
                note.contentResId=resId
                pageIndex=1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                fetchData()
            }
        else InputContentDialog(requireContext(), screenPos, getString(R.string.note_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                note.title = string
                note.createDate = System.currentTimeMillis()
                note.type = type
                note.contentResId=resId
                pageIndex=1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
//        //跳转
//        gotoIntent(note)
    }

    //修改笔记
    private fun editNotebook(content: String) {
        InputContentDialog(requireContext(), screenPos, content).builder()
            ?.setOnDialogClickListener { string ->
                noteBooks[position].title = string
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(noteBooks[position])
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun deleteNotebook() {
        CommonDialog(requireActivity(),screenPos).setContent(R.string.note_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    val note = noteBooks[position]
                    noteBooks.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    //删除笔记本
                    NotebookDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.type,note.id)
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                }
            })
    }

    //新建笔记分类
    private fun addNoteBookType() {
        InputContentDialog(requireContext(), screenPos,getString(R.string.notebook_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                val noteBook = BaseTypeBean().apply {
                    name = string
                    typeId = noteTypes.size
                }
                noteTypes.add(noteBook)
                BaseTypeBeanDaoManager.getInstance().insertOrReplace(noteBook)
                mAdapterType?.notifyDataSetChanged()
            }
    }

    /**
     * 自动压缩zip
     */
    private fun autoZip() {

        ZipUtils.zip(Constants.NOTE_PATH + "/$mUserId", "note", object : ZipUtils.ZipCallback {
            override fun onStart() {
                showLog("note开始打包上传")
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish(success: Boolean) {
                showLog("onFinish note:$success")
            }
            override fun onError(msg: String?) {
                showLog("onError note:$msg")
            }
        })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == NOTE_BOOK_MANAGER_EVENT) {
            findTabs()
        }
        if (msgFlag == NOTE_EVENT) {
            fetchData()
        }
        if (msgFlag == AUTO_UPLOAD_EVENT) {
            autoZip()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(type,pageIndex,10)
        val total= NotebookDaoManager.getInstance().queryAll(type)
        setPageNumber(total.size)
        mAdapter?.setNewData(noteBooks)
    }

}