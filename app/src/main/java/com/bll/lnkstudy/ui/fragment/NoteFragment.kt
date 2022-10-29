package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.AUTO_UPLOAD_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.NoteAddDialog
import com.bll.lnkstudy.dialog.NoteBookAddDialog
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.BaseTypeBeanDaoManager
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.NoteGreenDaoManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.ui.activity.NoteBookManagerActivity
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity
import com.bll.lnkstudy.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkstudy.ui.adapter.NoteAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseFragment() {
    private var popWindowList: PopWindowList? = null
    private var popWindowBeans = mutableListOf<PopWindowBean>()
    private var dialog: NoteAddDialog? = null
    private var noteBooks = mutableListOf<BaseTypeBean>()
    private var notes = mutableListOf<Note>()
    private var type = 0 //当前笔记本类型
    private var mAdapter: NoteAdapter? = null
    private var mAdapterType: BookCaseTypeAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var positionType = 0//当前笔记本标记
    private var isDown = false //是否向下打开

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {

        var popWindowBean = PopWindowBean()
        popWindowBean.name = "新建笔记本"
        popWindowBean.isCheck = true
        var popWindowBean1 = PopWindowBean()
        popWindowBean1.name = "笔记本管理"
        popWindowBean1.isCheck = false

        popWindowBeans.add(popWindowBean)
        popWindowBeans.add(popWindowBean1)

        EventBus.getDefault().register(this)

        setTitle("笔记")
        showView(iv_manager)

        bindClick()

        initTab()

        initRecyclerView()
        initData()
    }

    override fun lazyLoad() {
    }

    private fun initData() {

        noteBooks = DataBeanManager.getIncetance().noteBook
        var noBooks = BaseTypeBeanDaoManager.getInstance(activity).queryAll()
        if (noBooks.size < 1) {
            val baseTypeBean = BaseTypeBean()
            baseTypeBean.name = "金句彩段"
            baseTypeBean.typeId = 2
            baseTypeBean.date = System.currentTimeMillis()
            noBooks.add(baseTypeBean)
            BaseTypeBeanDaoManager.getInstance(activity).insertOrReplace(baseTypeBean)

            val baseTypeBean1 = BaseTypeBean()
            baseTypeBean1.name = "典型题型"
            baseTypeBean1.typeId = 3
            baseTypeBean1.date = System.currentTimeMillis() + 1000
            noBooks.add(baseTypeBean1)
            BaseTypeBeanDaoManager.getInstance(activity).insertOrReplace(baseTypeBean1)
        }
        noteBooks.addAll(noBooks)

        if (positionType < noteBooks.size) {
            noteBooks[positionType].isCheck = true
        } else {
            positionType = 0
            noteBooks[positionType].isCheck = true
        }

        if (!isDown) {
            if (noteBooks.size > 5) {
                if (positionType >= 5) {
                    noteBooks[positionType].isCheck = false
                    positionType = 0
                    noteBooks[positionType].isCheck = true
                }
                noteBooks = noteBooks.subList(0, 5)
            }
        }

        mAdapterType?.setNewData(noteBooks)
        type = noteBooks[positionType].typeId

        findDatas()

    }

    //查找数据
    private fun findDatas() {
        notes = NoteGreenDaoManager.getInstance(activity).queryAllNote(type)
        mAdapter?.setNewData(notes)
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NoteAdapter(R.layout.item_note, notes)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->

            gotoIntent(notes[position])

        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            if (view.id == R.id.iv_edit) {
                editNote(notes[position].title)
            }
            if (view.id == R.id.iv_delete) {
                setDeleteNote()
            }

        }
    }

    //设置头部索引
    private fun initTab() {

        rv_type.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
        mAdapterType = BookCaseTypeAdapter(R.layout.item_bookcase_type, noteBooks)
        rv_type.adapter = mAdapterType
        mAdapterType?.bindToRecyclerView(rv_type)
        rv_type.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(activity,22f),20))
        mAdapterType?.setOnItemClickListener { adapter, view, position ->
            noteBooks[positionType]?.isCheck = false
            positionType = position
            noteBooks[positionType]?.isCheck = true
            mAdapterType?.notifyDataSetChanged()

            type = noteBooks[positionType]?.typeId
            findDatas()

        }

        iv_down.setOnClickListener {
            if (isDown) {
                isDown = false
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_down)
            } else {
                isDown = true
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_up)
            }
            initData()
        }

    }

    private fun bindClick() {

        tv_add.setOnClickListener {
            dialog = NoteAddDialog(requireContext(),screenPos).builder()
            dialog?.setOnDialogClickListener { type ->
                resId = when (type) {
                    1 -> {
                        ToolUtils.getImageResStr(activity, R.mipmap.icon_note_details_bg_1)
                    }
                    2 -> {
                        ToolUtils.getImageResStr(activity, R.mipmap.icon_note_details_bg_2)
                    }
                    3 -> {
                        ToolUtils.getImageResStr(activity, R.mipmap.icon_note_details_bg_3)
                    }
                    4 -> {
                        ToolUtils.getImageResStr(activity, R.mipmap.icon_note_details_bg_4)
                    }
                    5 -> {
                        ToolUtils.getImageResStr(activity, R.mipmap.icon_note_details_bg_5)
                    }
                    else -> {
                        ToolUtils.getImageResStr(activity, 0)
                    }
                }
                addNote()
            }
        }

        iv_manager?.setOnClickListener {
            setTopSelectView()
        }


    }

    /**
     * 跳转笔记写作
     */
    private fun gotoIntent(note:Note){
        var intent = Intent(activity, NoteDrawingActivity::class.java)
        var bundle = Bundle()
        bundle.putSerializable("note", note)
        intent.putExtra("noteBundle", bundle)
        customStartActivity(intent)
    }

    //新建笔记
    private fun addNote() {
        NoteBookAddDialog(requireContext(), screenPos,"新建笔记", "", "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                val time = System.currentTimeMillis()
                var note = Note()
                note.title = string
                note.date = time
                note.type = type
                note.resId = resId
                //跳转
                gotoIntent(note)

                dialog?.dismiss()
            }
    }

    //修改笔记
    private fun editNote(content: String) {
        NoteBookAddDialog(requireContext(), screenPos,"重命名", content, "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                notes[position].title = string
                mAdapter?.notifyDataSetChanged()
                NoteGreenDaoManager.getInstance(activity).insertOrReplaceNote(notes[position])
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun setDeleteNote() {
        CommonDialog(activity,screenPos).setContent("确定要删除笔记？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    var note = notes[position]
                    notes.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    NoteGreenDaoManager.getInstance(activity).deleteNote(note)
                    FileUtils.deleteFile(File(note.path))
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                }

            })
    }


    //顶部弹出选择
    private fun setTopSelectView() {
        if (popWindowList == null) {
            popWindowList =
                PopWindowList(requireActivity(), popWindowBeans, iv_manager, 20).builder()
            popWindowList?.setOnSelectListener { item ->
                if (item.name == "新建笔记本") {
                    addNoteBook()
                } else {
                    customStartActivity(Intent(activity, NoteBookManagerActivity::class.java))
                }
            }
        } else {
            popWindowList?.show()
        }
    }


    //新建笔记本
    private fun addNoteBook() {
        NoteBookAddDialog(requireContext(), screenPos,"新建笔记本", "", "请输入笔记本").builder()
            ?.setOnDialogClickListener { string ->
                var noteBook = BaseTypeBean()
                noteBook.name = string
                noteBook.typeId = noteBooks.size
                noteBooks.add(noteBook)
                BaseTypeBeanDaoManager.getInstance(activity).insertOrReplace(noteBook)
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
            initData()
        }
        if (msgFlag == NOTE_EVENT) {
            findDatas()
        }
        if (msgFlag == AUTO_UPLOAD_EVENT) {
            autoZip()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}