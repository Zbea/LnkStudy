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
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.BaseTypeBean
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.Notebook
import com.bll.lnkstudy.mvp.model.PopWindowBean
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
    private var popWindowList: PopWindowList? = null
    private var popWindowBeans = mutableListOf<PopWindowBean>()
    private var popWindowMoreBeans = mutableListOf<PopWindowBean>()
    private var dialog: NoteAddDialog? = null
    private var noteTypes = mutableListOf<BaseTypeBean>()
    private var noteBooks = mutableListOf<Notebook>()
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

        popWindowBeans.add(PopWindowBean(0,"新建笔记本",true))
        popWindowBeans.add(PopWindowBean(1,"笔记本管理",false))

        popWindowMoreBeans.add(PopWindowBean(0,"重命名",true))
        popWindowMoreBeans.add(PopWindowBean(1,"删除",false))

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

        noteTypes = DataBeanManager.getIncetance().noteBook
        noteTypes.addAll( BaseTypeBeanDaoManager.getInstance().queryAll())

        if (positionType < noteTypes.size) {
            noteTypes[positionType].isCheck = true
        } else {
            positionType = 0
            noteTypes[positionType].isCheck = true
        }

        if (!isDown) {
            if (noteTypes.size > 5) {
                if (positionType >= 5) {
                    noteTypes[positionType].isCheck = false
                    positionType = 0
                    noteTypes[positionType].isCheck = true
                }
                noteTypes = noteTypes.subList(0, 5)
            }
        }

        mAdapterType?.setNewData(noteTypes)
        type = noteTypes[positionType].typeId

        findDatas()

    }

    //查找数据
    private fun findDatas() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(type)
        mAdapter?.setNewData(noteBooks)
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NotebookAdapter(R.layout.item_note, noteBooks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
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
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            if (view.id == R.id.iv_encrypt) {
                setPassword()
            }
            if (view.id == R.id.iv_more) {
                PopWindowList(requireActivity(), popWindowMoreBeans, view, 0).builder()
                ?.setOnSelectListener { item ->
                    if (item.id == 0) {
                        editNotebook(noteBooks[position].title)
                    } else {
                        deleteNotebook()
                    }
                }
            }

        }
    }

    //设置头部索引
    private fun initTab() {

        rv_type.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
        mAdapterType = BookCaseTypeAdapter(R.layout.item_bookcase_type, noteTypes)
        rv_type.adapter = mAdapterType
        mAdapterType?.bindToRecyclerView(rv_type)
        rv_type.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(activity,22f),20))
        mAdapterType?.setOnItemClickListener { adapter, view, position ->
            noteTypes[positionType]?.isCheck = false
            positionType = position
            noteTypes[positionType]?.isCheck = true
            mAdapterType?.notifyDataSetChanged()

            type = noteTypes[positionType]?.typeId
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
            dialog = NoteAddDialog(requireContext(),screenPos,type).builder()
            dialog?.setOnDialogClickListener { moduleBean ->
                resId=ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                createNotebook()
            }
        }

        iv_manager?.setOnClickListener {
            setTopSelectView()
        }


    }

    /**
     * 跳转笔记写作
     */
    private fun gotoIntent(note: Notebook){
        var intent = Intent(activity, NoteDrawingActivity::class.java)
        var bundle = Bundle()
        bundle.putSerializable("note", note)
        intent.putExtra("bundle", bundle)
        customStartActivity(intent)
    }

    /**
     * 日记设置密码
     */
    private fun setPassword(){
        val notePassword=SPUtil.getObj("notePassword",NotePassword::class.java)
        if (notePassword==null){
            NotebookSetPasswordDialog(requireContext(),screenPos).builder()?.setOnDialogClickListener {
                var note = noteBooks[position]
                note.isEncrypt=true
                note.encrypt=SPUtil.getObj("notePassword",NotePassword::class.java)?.password
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(note)
            }
        }
        else{
            if(noteBooks[position].isEncrypt){
                CommonDialog(activity,screenPos).setContent("确定删除密码？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            var note = noteBooks[position]
                            note.isEncrypt=false
                            note.encrypt=""
                            mAdapter?.notifyDataSetChanged()
                            NotebookDaoManager.getInstance().insertOrReplace(note)
                        }

                    })
            }
            else{
                CommonDialog(activity,screenPos).setContent("确定设置密码？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            var note = noteBooks[position]
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
        if (type==0){
            NotebookAddDiaryDialog(requireContext(), screenPos).builder()
                ?.setOnDialogClickListener{ name,dateStr->
                    var note = Notebook()
                    note.title = name
                    note.createDate = System.currentTimeMillis()
                    note.type = type
                    note.dateStr=dateStr
                    note.contentResId=resId
                    NotebookDaoManager.getInstance().insertOrReplace(note)
                    note.id=NotebookDaoManager.getInstance().insertId
                    noteBooks.add(0,note)
                    mAdapter?.setNewData(noteBooks)
                    //跳转
                    gotoIntent(note)
                }
        }
        else{
            NotebookAddDialog(requireContext(), screenPos,"新建笔记本", "", "请输入笔记标题").builder()
                ?.setOnDialogClickListener { string ->
                    var note = Notebook()
                    note.title = string
                    note.createDate = System.currentTimeMillis()
                    note.type = type
                    note.contentResId=resId
                    NotebookDaoManager.getInstance().insertOrReplace(note)
                    note.id=NotebookDaoManager.getInstance().insertId
                    noteBooks.add(0,note)
                    mAdapter?.setNewData(noteBooks)
                    EventBus.getDefault().post(NOTE_EVENT)
                    //跳转
                    gotoIntent(note)
                }
        }

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
        CommonDialog(activity,screenPos).setContent("确定要删除笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    var note = noteBooks[position]
                    noteBooks.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    NotebookDaoManager.getInstance().deleteBean(note)
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
                if (item.id == 0) {
                    addNoteBookType()
                } else {
                    customStartActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                }
            }
        } else {
            popWindowList?.show()
        }
    }


    //新建笔记分类
    private fun addNoteBookType() {
        NotebookAddDialog(requireContext(), screenPos,"新建笔记分类", "", "输入笔记分类").builder()
            ?.setOnDialogClickListener { string ->
                var noteBook = BaseTypeBean()
                noteBook.name = string
                noteBook.typeId = noteTypes.size
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