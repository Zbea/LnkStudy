package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_TAB_MANAGER_EVENT
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.ui.activity.NotebookManagerActivity
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_list_tab.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseMainFragment(){

    private var popWindowBeans = mutableListOf<PopupBean>()
    private var noteTypes = mutableListOf<ItemTypeBean>()
    private var notes = mutableListOf<Note>()
    private var positionType = 0//当前笔记本标记
    private var typeStr = "" //当前笔记本类型
    private var mAdapter: NotebookAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var privacyPassword=MethodManager.getPrivacyPassword(1)

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[5])
        pageSize=10

        popWindowBeans.add(PopupBean(0, getString(R.string.note_manage_str)))
        popWindowBeans.add(PopupBean(1, getString(R.string.notebook_create_str)))

        showView(iv_manager)

        initRecyclerView()

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NotebookManagerActivity::class.java))
                    }
                    1 -> {
                        addNotebook()
                    }
                }
            }
        }

        initTabs()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DP2PX.dip2px(requireActivity(),25f), 0,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = NotebookAdapter(1,R.layout.item_note, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val note=notes[position]
                if (positionType==0&&privacyPassword!=null&&!note.isCancelPassword){
                    PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
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
                        if (privacyPassword==null){
                            PrivacyPasswordCreateDialog(requireActivity(),1).builder().setOnDialogClickListener{
                                privacyPassword=it
                                mAdapter?.notifyDataSetChanged()
                                showToast("密本密码设置成功")
                            }
                        }
                        else{
                            val titleStr=if (note.isCancelPassword) "确定设置密码？" else "确定取消密码？"
                            CommonDialog(requireActivity()).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    PrivacyPasswordDialog(requireActivity(),1).builder().setOnDialogClickListener{
                                        note.isCancelPassword=!note.isCancelPassword
                                        NoteDaoManager.getInstance().insertOrReplace(note)
                                        mAdapter?.notifyItemChanged(position)
                                    }
                                }
                            })
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
        val view =requireActivity().layoutInflater.inflate(R.layout.common_add_view,null)
        view.setOnClickListener {
            ModuleAddDialog(requireContext(),screenPos,getString(R.string.note_module_str),DataBeanManager.noteModuleBook).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                    createNote()
                }
        }
        mAdapter?.addFooterView(view)

    }

    /**
     * tab数据设置
     */
    private fun initTabs() {
        pageIndex=1
        noteTypes= ItemTypeDaoManager.getInstance().queryAll(2)
        noteTypes.add(0,ItemTypeBean().apply {
            title = getString(R.string.note_tab_diary)
        })
        if (positionType>=noteTypes.size){
            positionType=0
        }
        for (item in noteTypes){
            item.isCheck=false
        }
        noteTypes[positionType].isCheck=true
        typeStr = noteTypes[positionType].title
        mTabTypeAdapter?.setNewData(noteTypes)

        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        positionType=position
        typeStr=noteTypes[position].title
        pageIndex=1
        fetchData()
    }

    //新建笔记
    private fun createNote() {
        val note = Note()
        note.grade=grade
        InputContentDialog(requireContext(), screenPos, getString(R.string.note_create_hint)).builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.date = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId=resId
                val id= NoteDaoManager.getInstance().insertOrReplaceGetId(note)
                if (notes.size==10){
                    pageIndex+=1
                }
                EventBus.getDefault().post(NOTE_EVENT)
                //新建笔记本增量更新
                DataUpdateManager.createDataUpdate(4,id.toInt(),2,Gson().toJson(note))
            }
    }

    //修改笔记
    private fun editNote(content: String) {
        InputContentDialog(requireContext(), screenPos, content).builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                val note=notes[position]
                //查询笔记内容
                val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                for (noteContent in noteContents){
                    noteContent.noteTitle=string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                    //修改增量更新
                    DataUpdateManager.editDataUpdate(4,noteContent.id.toInt(),3,Gson().toJson(noteContent))
                }
                note.title = string
                NoteDaoManager.getInstance().insertOrReplace(note)

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
                    //获取当前笔记本的所有内容
                    val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title,note.grade)
                    //删除笔记本
                    NoteDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title,note.grade)
                    val path=FileAddress().getPathNote(note.grade,note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))

                    //删除当前笔记本增量更新
                    DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),2)
                    //删除当前笔记本内容增量更新
                    for (item in noteContents){
                        DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),3)
                    }
                    notes.remove(note)
                    if (pageIndex>1&&notes.size==0){
                        pageIndex-=1
                    }
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                }
            })
    }

    //新建笔记分类
    private fun addNotebook() {
        InputContentDialog(requireContext(), 2,getString(R.string.notebook_create_hint)).builder()
            .setOnDialogClickListener { string ->
                if (ItemTypeDaoManager.getInstance().isExist(string,2)){
                    showToast(R.string.toast_existed)
                }
                else{
                    val noteBook = ItemTypeBean().apply {
                        title = string
                        type = 2
                        date=System.currentTimeMillis()
                    }
                    val id= ItemTypeDaoManager.getInstance().insertOrReplaceGetId(noteBook)
                    //创建笔记分类增量更新
                    DataUpdateManager.createDataUpdate(4,id.toInt(),1,Gson().toJson(noteBook))
                    mTabTypeAdapter?.addData(noteBook)
                }
            }
    }

    /**
     * 修改增量更新数据
     */
    private fun editDataUpdate(id:Int,item: Note){
        DataUpdateManager.editDataUpdate(4,id,2,Gson().toJson(item))
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
            val notes= NoteDaoManager.getInstance().queryAll(noteType.title)
            for (item in notes){
                val path=FileAddress().getPathNote(item.grade,noteType.title,item.title)
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
        mAdapter?.setNewData(notes)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        ItemTypeDaoManager.getInstance().clear(2)
        NoteDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()
        FileUtils.deleteFile(File(Constants.NOTE_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(4)
        val map=HashMap<String,Any>()
        map["type"]=4
        mDataUploadPresenter.onDeleteData(map)
        EventBus.getDefault().post(NOTE_TAB_MANAGER_EVENT)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            NOTE_TAB_MANAGER_EVENT -> {
                initTabs()
            }
            NOTE_EVENT -> {
                fetchData()
            }
        }
    }

}