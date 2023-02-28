package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil

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

    private var pageIndex=1
    private var pageTotal=1

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        popWindowBeans.add(PopupBean(0, "笔记本管理", true))
        popWindowBeans.add(PopupBean(1, "新建笔记本", false))
        popWindowBeans.add(PopupBean(2, "新建笔记", false))

        popWindowMoreBeans.add(PopupBean(0, "重命名", true))
        popWindowMoreBeans.add(PopupBean(1, "删除", false))

        EventBus.getDefault().register(this)

        setTitle("笔记")
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
                        ?.setOnSelectListener { item ->
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
                pageTotal=1
                findDatas()
            }
        }

    }

    private fun bindClick() {

        iv_manager?.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder()?.setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                    }
                    1 -> {
                        addNoteBookType()
                    }
                    else -> {
                        val list=if (type==0) DataBeanManager.noteModuleDiary else DataBeanManager.noteModuleBook
                        ModuleAddDialog(requireContext(),screenPos,"笔记模板",list).builder()
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


        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findDatas()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1
                findDatas()
            }
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
        findDatas()
    }

    /**
     * 笔记本数据
     */
    private fun findDatas() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(type,pageIndex,10)
        val total= NotebookDaoManager.getInstance().queryAll(type)
        pageTotal= ceil(total.size.toDouble()/10).toInt()
        mAdapter?.setNewData(noteBooks)
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageTotal.toString()
        ll_page_number.visibility=if (pageTotal==0) View.GONE else View.VISIBLE
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
            null -> NotebookSetPasswordDialog(requireContext(),screenPos).builder()?.setOnDialogClickListener {
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
                CommonDialog(activity,screenPos).setContent("设置日记密码？").builder()
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
        var note = NotebookBean()
        if (type==0) NotebookAddDiaryDialog(requireContext(), screenPos).builder()
            ?.setOnDialogClickListener{ name,dateStr->
                note.title = name
                note.createDate = System.currentTimeMillis()
                note.type = type
                note.dateStr=dateStr
                note.contentResId=resId
                if (noteBooks.size==10)
                    pageIndex+=1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
        else NotebookAddDialog(requireContext(), screenPos,"新建笔记", "", "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                note.title = string
                note.createDate = System.currentTimeMillis()
                note.type = type
                note.contentResId=resId
                if (noteBooks.size==10)
                    pageIndex+=1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
//        //跳转
//        gotoIntent(note)
    }

    //修改笔记
    private fun editNotebook(content: String) {
        NotebookAddDialog(requireContext(), screenPos,"重命名", content, "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                noteBooks[position].title = string
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(noteBooks[position])
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun deleteNotebook() {
        CommonDialog(activity,screenPos).setContent("确定删除笔记？").builder()
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
        NotebookAddDialog(requireContext(), screenPos,"新建笔记分类", "", "输入笔记分类").builder()
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