package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteTypeBeanDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.NotePassword
import com.bll.lnkstudy.mvp.model.NoteTypeBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.NoteTypeManagerActivity
import com.bll.lnkstudy.ui.activity.drawing.NoteDrawingActivity
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.*
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
    private var popWindowMoreBeans = mutableListOf<PopupBean>()
    private var noteTypes = mutableListOf<NoteTypeBean>()
    private var noteBooks = mutableListOf<NotebookBean>()
    private var positionType = 0//当前笔记本标记
    private var typeStr = "" //当前笔记本类型
    private var mAdapter: NotebookAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var isDiary=false
    private var typeId=0

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

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        initRecyclerView()
        findTabs()

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                    }
                    1 -> {
                        addNoteBookType()
                    }
                    else -> {
                        val list=if (positionType==0) DataBeanManager.noteModuleDiary else DataBeanManager.noteModuleBook
                        ModuleAddDialog(requireContext(),screenPos,getString(R.string.note_module_str),list).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                                createNotebook()
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
                val notebook=noteBooks[position]
                if (positionType==0){
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
        rg_group.removeAllViews()
        for (i in noteTypes.indices) {
            rg_group.addView(getRadioButton(i,positionType, noteTypes[i].name, noteTypes.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId=if (positionType==0)0 else 1
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
        noteTypes.addAll(NoteTypeBeanDaoManager.getInstance().queryAll())
        if (positionType>noteTypes.size){
            positionType=0
        }
        typeStr = noteTypes[positionType].name
        initTab()
        fetchData()
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
                editDataUpdate(note.id.toInt(),note)
            }
            else -> if(noteBooks[position].isEncrypt){
                NotebookDeletePasswordDialog(requireContext(),screenPos).builder()?.setOnDialogClickListener{
                    val note = noteBooks[position]
                    note.isEncrypt=false
                    note.encrypt=""
                    mAdapter?.notifyDataSetChanged()
                    NotebookDaoManager.getInstance().insertOrReplace(note)
                    editDataUpdate(note.id.toInt(),note)
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
                            editDataUpdate(note.id.toInt(),note)
                        }
                    })
            }
        }
    }


    //新建笔记
    private fun createNotebook() {
        val note = NotebookBean()
        val typeStr=noteTypes[positionType].name
        note.grade=if (positionType==0) DateUtils.getYear() else grade
        if (positionType==0) NotebookAddDiaryDialog(requireContext(), screenPos).builder()
            ?.setOnDialogClickListener{ name,dateStr->
                if (NotebookDaoManager.getInstance().isExist(typeStr,name)){
                    showToast(screenPos,R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = name
                note.createDate = System.currentTimeMillis()
                note.typeStr = typeStr
                note.dateStr=dateStr
                note.contentResId=resId
                pageIndex=1
                val id=NotebookDaoManager.getInstance().insertOrReplaceGetId(note)
                fetchData()
                //新建笔记本增量更新
                DataUpdateManager.createDataUpdate(4,id.toInt(),1,typeId,Gson().toJson(note))
            }
        else InputContentDialog(requireContext(), screenPos, getString(R.string.note_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                if (NotebookDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(screenPos,R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.createDate = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId=resId
                pageIndex=1
                val id=NotebookDaoManager.getInstance().insertOrReplaceGetId(note)
                EventBus.getDefault().post(NOTE_EVENT)
                //新建笔记本增量更新
                DataUpdateManager.createDataUpdate(4,id.toInt(),1,typeId,Gson().toJson(note))
            }
    }

    //修改笔记
    private fun editNotebook(content: String) {
        InputContentDialog(requireContext(), screenPos, content).builder()
            ?.setOnDialogClickListener { string ->
                val notebookBean=noteBooks[position]
                notebookBean.title = string
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(notebookBean)
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                editDataUpdate(notebookBean.id.toInt(),notebookBean)
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
                    //获取当前笔记本的所有内容
                    val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                    //删除笔记本
                    NotebookDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path=FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                    //删除当前笔记本增量更新
                    DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),1,typeId)
                    //删除当前笔记本内容增量更新
                    for (item in noteContents){
                        DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),2,typeId)
                    }
                }
            })
    }

    //新建笔记分类
    private fun addNoteBookType() {
        InputContentDialog(requireContext(), screenPos,getString(R.string.notebook_create_hint)).builder()
            ?.setOnDialogClickListener { string ->
                if (NoteTypeBeanDaoManager.getInstance().isExist(string)){
                    showToast(screenPos,R.string.toast_existed)
                }
                else{
                    val noteBook = NoteTypeBean().apply {
                        name = string
                        typeId = System.currentTimeMillis().toInt()
                    }
                    noteTypes.add(noteBook)
                    val id=NoteTypeBeanDaoManager.getInstance().insertOrReplaceGetId(noteBook)
                    //创建笔记分类增量更新
                    DataUpdateManager.createDataUpdate(4,id.toInt(),0,1,Gson().toJson(noteBook))
                    initTab()
                }
            }
    }

    /**
     * 修改增量更新数据
     */
    private fun editDataUpdate(id:Int,item:NotebookBean){
        DataUpdateManager.editDataUpdate(4,id,1,typeId,Gson().toJson(item))
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == NOTE_BOOK_MANAGER_EVENT) {
            findTabs()
        }
        if (msgFlag == NOTE_EVENT) {
            fetchData()
        }
    }

    /**
     * 上传笔记
     */
    fun uploadNote(token:String,isDiary:Boolean){
        if (grade==0) return
        this.isDiary=isDiary
        //获取不同类型下的笔记分类
        val types= mutableListOf<NoteTypeBean>()
        if (isDiary){
            types.add(noteTypes[0])
        }
        else{
            types.addAll(noteTypes.subList(1,noteTypes.size))
        }

        val cloudList= mutableListOf<CloudListBean>()
        for (noteType in types){
            //查找到这个分类的所有内容，然后遍历上传所有内容
            val notes=NotebookDaoManager.getInstance().queryAll(noteType.name)
            for (item in notes){
                val path=FileAddress().getPathNote(item.grade,noteType.name,item.title)
                val fileName=item.title
                item.isEncrypt=false
                item.encrypt=""
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
                            if(isDiary){
                                if (cloudList.size==notes.size)
                                    mCloudUploadPresenter.upload(cloudList)
                            }
                            else{
                                //当加入上传的内容等于全部需要上传时候，则上传
                                if (cloudList.size==NotebookDaoManager.getInstance().queryAllSize())
                                    mCloudUploadPresenter.upload(cloudList)
                            }
                        }
                    }
                }
                else{
                    cloudList.add(CloudListBean().apply {
                        type=4
                        subType=-1
                        subTypeStr=item.typeStr
                        date=System.currentTimeMillis()
                        grade=item.grade
                        listJson=Gson().toJson(item)
                        downloadUrl="null"
                    })
                    if(isDiary){
                        if (cloudList.size==notes.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                    else{
                        //当加入上传的内容等于全部需要上传时候，则上传
                        if (cloudList.size==NotebookDaoManager.getInstance().queryAllSize())
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            }
        }

    }

    override fun fetchData() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(typeStr,pageIndex,pageSize)
        val total= NotebookDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(noteBooks)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        //将已经上传过的笔记从云书库删除
        val ids= mutableListOf<Int>()
        if (isDiary){
            val notes=NotebookDaoManager.getInstance().queryAll(noteTypes[0].name)
            //删除该笔记分类中的所有笔记本及其内容
            for (note in notes){
                if (note.isCloud){
                    ids.add(note.cloudId)
                }
                NotebookDaoManager.getInstance().deleteBean(note)
                NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                val path= FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                FileUtils.deleteFile(File(path))
            }
            //清除本地增量数据
            DataUpdateManager.clearDataUpdate(4,0)
            val map=HashMap<String,Any>()
            map["type"]=4
            map["typeId"]=0
            mDataUploadPresenter.onDeleteData(map)
        }
        else{
            for (i in 1 until noteTypes.size){
                val notes=NotebookDaoManager.getInstance().queryAll(noteTypes[i].name)
                //删除该笔记分类中的所有笔记本及其内容
                for (note in notes){
                    if (note.isCloud){
                        ids.add(note.cloudId)
                    }
                    NotebookDaoManager.getInstance().deleteBean(note)
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path= FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                }
            }
            NoteTypeBeanDaoManager.getInstance().clear()
            //清除本地增量数据
            DataUpdateManager.clearDataUpdate(4,1)
            val map=HashMap<String,Any>()
            map["type"]=4
            map["typeId"]=1
            mDataUploadPresenter.onDeleteData(map)
        }
        if (ids.size>0)
            mCloudUploadPresenter.deleteCloud(ids)
        EventBus.getDefault().post(NOTE_BOOK_MANAGER_EVENT)
    }

}