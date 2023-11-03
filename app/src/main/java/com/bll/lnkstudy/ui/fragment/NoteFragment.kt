package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.PASSWORD_EVENT
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.PrivacyPassword
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.model.note.Notebook
import com.bll.lnkstudy.ui.activity.NotebookManagerActivity
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseFragment(){

    private var popWindowBeans = mutableListOf<PopupBean>()
    private var noteTypes = mutableListOf<Notebook>()
    private var notes = mutableListOf<Note>()
    private var positionType = 0//当前笔记本标记
    private var typeStr = "" //当前笔记本类型
    private var mAdapter: NotebookAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var privacyPassword=SPUtil.getObj("${mUser?.accountId}notePassword",
        PrivacyPassword::class.java)

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        pageSize=10

        popWindowBeans.add(PopupBean(0, getString(R.string.note_manage_str), true))
        popWindowBeans.add(PopupBean(1, getString(R.string.notebook_create_str), false))
        popWindowBeans.add(PopupBean(2, getString(R.string.note_create_str), false))

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        initRecyclerView()
        findTabs()

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NotebookManagerActivity::class.java))
                    }
                    1 -> {
                        addNotebook()
                    }
                    else -> {
                        ModuleAddDialog(requireContext(),screenPos,getString(R.string.note_module_str),DataBeanManager.noteModuleBook).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                                createNote()
                            }
                    }
                }
            }
        }
    }

    override fun lazyLoad() {
    }


    private fun initRecyclerView() {
        mAdapter = NotebookAdapter(1,R.layout.item_note, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val note=notes[position]
                if (positionType==0&&privacyPassword!=null&&privacyPassword?.isSet==true&&!note.isCancelPassword){
                    PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                        MethodManager.gotoNoteDrawing(requireActivity(),note,0, Constants.DEFAULT_PAGE)
                    }
                }
                else{
                    MethodManager.gotoNoteDrawing(requireActivity(),note,0, Constants.DEFAULT_PAGE)
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@NoteFragment.position = position
                val note=notes[position]
                when(view.id){
                    R.id.iv_password->{
                        PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                            note.isCancelPassword=!note.isCancelPassword
                            mAdapter?.notifyItemChanged(position)
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            editDataUpdate(note.id.toInt(),note)
                        }
                    }
                    R.id.iv_edit->{
                        editNote(note.title)
                    }
                    R.id.iv_delete->{
                        deleteNote()
                    }
                }
            }
        }

    }

    //设置头部索引
    private fun initTab() {
        rg_group.removeAllViews()
        for (i in noteTypes.indices) {
            rg_group.addView(getRadioButton(i,positionType, noteTypes[i].name, noteTypes.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            positionType=id
            typeStr=noteTypes[positionType].name
            pageIndex=1
            fetchData()
        }
    }

    /**
     * tab数据设置
     */
    private fun findTabs() {
        noteTypes = DataBeanManager.noteBook
        noteTypes.addAll(NotebookDaoManager.getInstance().queryAll())
        if (positionType>=noteTypes.size){
            positionType=0
        }
        typeStr = noteTypes[positionType].name
        initTab()
        fetchData()
    }

    //新建笔记
    private fun createNote() {
        val note = Note()
        note.grade=grade
        InputContentDialog(requireContext(), screenPos, getString(R.string.note_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(screenPos,R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.date = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId=resId
                pageIndex=1
                val id= NoteDaoManager.getInstance().insertOrReplaceGetId(note)
                EventBus.getDefault().post(NOTE_EVENT)
                //新建笔记本增量更新
                DataUpdateManager.createDataUpdate(4,id.toInt(),2,positionType,Gson().toJson(note))
            }
    }

    //修改笔记
    private fun editNote(content: String) {
        InputContentDialog(requireContext(), screenPos, content).builder()
            ?.setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(screenPos,R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                val note=notes[position]
                //查询笔记内容
                val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                for (noteContent in noteContents){
                    noteContent.noteTitle=string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                    //修改增量更新
                    DataUpdateManager.editDataUpdate(4,noteContent.id.toInt(),3,2,Gson().toJson(noteContent))
                }
                note.title = string
                NoteDaoManager.getInstance().insertOrReplace(note)
                mAdapter?.notifyDataSetChanged()

                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                editDataUpdate(note.id.toInt(),note)
            }
    }

    //删除
    private fun deleteNote() {
        CommonDialog(requireActivity(),2).setContent(R.string.note_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val note = notes[position]
                    notes.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    //获取当前笔记本的所有内容
                    val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                    //删除笔记本
                    NoteDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path=FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                    //删除当前笔记本增量更新
                    DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),2,positionType)
                    //删除当前笔记本内容增量更新
                    for (item in noteContents){
                        DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),3,positionType)
                    }
                }
            })
    }

    //新建笔记分类
    private fun addNotebook() {
        InputContentDialog(requireContext(), screenPos,getString(R.string.notebook_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                if (NotebookDaoManager.getInstance().isExist(string)){
                    showToast(screenPos,R.string.toast_existed)
                }
                else{
                    val noteBook = Notebook().apply {
                        name = string
                        typeId = ToolUtils.getDateId()
                        date=System.currentTimeMillis()
                    }
                    noteTypes.add(noteBook)
                    val id= NotebookDaoManager.getInstance().insertOrReplaceGetId(noteBook)
                    //创建笔记分类增量更新
                    DataUpdateManager.createDataUpdate(4,id.toInt(),1,2,Gson().toJson(noteBook))
                    initTab()
                }
            }
    }

    /**
     * 修改增量更新数据
     */
    private fun editDataUpdate(id:Int,item: Note){
        DataUpdateManager.editDataUpdate(4,id,2,positionType,Gson().toJson(item))
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == NOTE_BOOK_MANAGER_EVENT) {
            findTabs()
        }
        if (msgFlag == NOTE_EVENT) {
            fetchData()
        }
        if (msgFlag== PASSWORD_EVENT){
            privacyPassword=SPUtil.getObj("${mUser?.accountId}notePassword",
                PrivacyPassword::class.java)
        }
    }

    /**
     * 上传笔记
     */
    fun uploadNote(token:String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        val nullItems= mutableListOf<Note>()
        for (noteType in noteTypes){
            //查找到这个分类的所有内容，然后遍历上传所有内容
            val notes= NoteDaoManager.getInstance().queryAll(noteType.name)
            for (item in notes){
                val path=FileAddress().getPathNote(item.grade,noteType.name,item.title)
                val fileName=item.title
                //获取笔记所有内容
                val noteContents = NoteContentDaoManager.getInstance().queryAll(item.typeStr,item.title,item.grade)
                //如果此笔记还没有开始书写，则不用上传源文件
                if (noteContents.size>0){
                    FileUploadManager(token).apply {
                        startUpload(path,fileName)
                        setCallBack{
                            cloudList.add(CloudListBean().apply {
                                type=4
                                subType=-1
                                subTypeStr=item.typeStr
                                date=System.currentTimeMillis()
                                grade=item.grade
                                listJson=Gson().toJson(item)
                                contentJson= Gson().toJson(noteContents)
                                downloadUrl=it
                            })
                            //当加入上传的内容等于全部需要上传时候，则上传
                            if (cloudList.size== NoteDaoManager.getInstance().queryNotes().size-nullItems.size)
                                mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
                else{
                    //没有内容不上传
                    nullItems.add(item)
                }
            }
        }

    }

    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr,pageIndex,pageSize)
        val total= NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        for (item in notes){
            item.isSet = positionType==0&&privacyPassword!=null&&privacyPassword?.isSet==true
        }
        mAdapter?.setNewData(notes)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        //将已经上传过的笔记从云书库删除
        val ids= mutableListOf<Int>()
        for (i in 0 until noteTypes.size){
            val notes= NoteDaoManager.getInstance().queryAll(noteTypes[i].name)
            //删除该笔记分类中的所有笔记本及其内容
            for (note in notes){
                if (note.isCloud){
                    ids.add(note.cloudId)
                }
                NoteDaoManager.getInstance().deleteBean(note)
                NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                val path= FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                FileUtils.deleteFile(File(path))
            }
        }
        NotebookDaoManager.getInstance().clear()
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(4,2)
        val map=HashMap<String,Any>()
        map["type"]=4
        mDataUploadPresenter.onDeleteData(map)
        if (ids.size>0)
            mCloudUploadPresenter.deleteCloud(ids)
        EventBus.getDefault().post(NOTE_BOOK_MANAGER_EVENT)
    }

}