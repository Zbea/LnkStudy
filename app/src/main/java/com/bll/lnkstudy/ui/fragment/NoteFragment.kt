package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.NOTE_EVENT
import com.bll.lnkstudy.Constants.Companion.NOTE_TAB_MANAGER_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleItemDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkstudy.dialog.PrivacyPasswordDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.presenter.QiniuPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.NotebookManagerActivity
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseMainFragment(),IContractView.IQiniuView{
    private val presenter= QiniuPresenter(this,2)
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var notes = mutableListOf<Note>()
    private var tabPos = 0//当前笔记本标记
    private var typeStr = "" //当前笔记本类型
    private var mAdapter: NotebookAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var privacyPassword=MethodManager.getPrivacyPassword(1)

    override fun onToken(token: String) {
        showLoading()
        uploadNote(token)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[5])
        showView(iv_manager)
        pageSize=14

        popWindowBeans.add(PopupBean(0, getString(R.string.note_manage_str)))
        popWindowBeans.add(PopupBean(1, getString(R.string.notebook_create_str)))

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popWindowBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(activity, NotebookManagerActivity::class.java))
                    }
                    1 -> {
                        createNotebook()
                    }
                }
            }
        }

        initRecyclerView()
        initTabs()
    }

    override fun lazyLoad() {
    }

    /**
     * tab数据设置
     */
    private fun initTabs() {
        pageIndex=1
        itemTabTypes= ItemTypeDaoManager.getInstance().queryAll(2)
        itemTabTypes.add(0,ItemTypeBean().apply {
            title = getString(R.string.note_tab_diary)
        })
        if (tabPos>=itemTabTypes.size){
            tabPos=0
        }
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        typeStr = itemTabTypes[tabPos].title
        mTabTypeAdapter?.setNewData(itemTabTypes)

        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos=position
        typeStr=itemTabTypes[position].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DP2PX.dip2px(requireActivity(),25f), 0,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = NotebookAdapter(R.layout.item_note, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val note=notes[position]
                if (tabPos==0&&privacyPassword!=null&&!note.isCancelPassword){
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
                        setPasswordNote()
                    }
                    R.id.iv_edit->{
                        editNote()
                    }
                    R.id.iv_delete->{
                        CommonDialog(requireActivity(),2).setContent("确定删除${note.title}？").builder()
                            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    deleteNote()
                                }
                            })
                    }
                    R.id.iv_upload->{
                        val path=FileAddress().getPathNote(note.typeStr,note.title)
                        if (!FileUtils.isExistContent(path)){
                            showToast("${note.title}暂无内容，无需上传")
                            return@setOnItemChildClickListener
                        }
                        CommonDialog(requireActivity()).setContent("上传${note.title}到云书库？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                presenter.getToken(true)
                            }
                        })
                    }
                }
            }
        }
        val view =requireActivity().layoutInflater.inflate(R.layout.common_add_view,null)
        view.setOnClickListener {
            ModuleItemDialog(requireContext(),screenPos,getString(R.string.note_module_str),DataBeanManager.noteModuleBeanBooks).builder()
                .setOnDialogClickListener { moduleBean ->
                    resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                    createNote()
                }
        }
        mAdapter?.addFooterView(view)

    }

    //新建笔记本
    private fun createNotebook() {
        InputContentDialog(requireContext(), 2,getString(R.string.notebook_create_hint)).builder()
            .setOnDialogClickListener { string ->
                if (ItemTypeDaoManager.getInstance().isExist(2,string)){
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
                    DataUpdateManager.createDataUpdate(4,id.toInt(),1,id.toInt(),Gson().toJson(noteBook))
                    mTabTypeAdapter?.addData(noteBook)
                }
            }
    }

    //新建主题
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
                //新建笔记本增量更新
                DataUpdateManager.createDataUpdate(4,id.toInt(),2,id.toInt(),Gson().toJson(note))
                if (notes.size==10){
                    pageIndex+=1
                    fetchData()
                }
                else{
                    mAdapter?.addData(0,note)
                }
            }
    }

    //修改主题
    private fun editNote() {
        val note=notes[position]
        InputContentDialog(requireContext(), screenPos, note.title).builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                //查询笔记内容
                val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title)
                for (noteContent in noteContents){
                    noteContent.noteTitle=string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                    //修改增量更新
                    DataUpdateManager.editDataUpdate(4,noteContent.id.toInt(),3,noteContent.id.toInt(),Gson().toJson(noteContent))
                }
                note.title = string
                NoteDaoManager.getInstance().insertOrReplace(note)

                mAdapter?.notifyItemChanged(position)
                editDataUpdate(note.id.toInt(),note)
            }
    }

    //删除主题
    private fun deleteNote() {
        val note = notes[position]
        //删除主题
        NoteDaoManager.getInstance().deleteBean(note)
        val path=FileAddress().getPathNote(note.typeStr,note.title)
        FileUtils.deleteFile(File(path))
        //删除主题增量更新
        DataUpdateManager.deleteDateUpdate(4,note.id.toInt(),2,note.id.toInt())

        val noteContents=NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title)
        //删除主题内容
        NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title)
        //删除内容增量更新
        for (item in noteContents){
            DataUpdateManager.deleteDateUpdate(4,item.id.toInt(),3,item.id.toInt())
        }

        mAdapter?.remove(position)

        if (notes.size==0){
            if (pageIndex>1){
                pageIndex-=1
                fetchData()
            }
            else{
                setPageNumber(0)
            }
        }
    }

    /**
     * 设置密码
     */
    private fun setPasswordNote(){
        val note=notes[position]
        if (privacyPassword==null){
            PrivacyPasswordCreateDialog(requireActivity(),1).builder().setOnDialogClickListener{
                privacyPassword=it
                mAdapter?.notifyItemChanged(position)
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

    /**
     * 修改增量更新数据
     */
    private fun editDataUpdate(id:Int,item: Note){
        DataUpdateManager.editDataUpdate(4,id,2,id,Gson().toJson(item))
    }

    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr,pageIndex,pageSize)
        val total= NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(notes)
    }

    /**
     * 上传笔记
     */
    private fun uploadNote(token:String){
        val cloudList= mutableListOf<CloudListBean>()
        val note=notes[position]
        val path=FileAddress().getPathNote(note.typeStr,note.title)
        //获取笔记所有内容
        val noteContents = NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title)
        FileUploadManager(token).apply {
            startZipUpload(path,note.title)
            setCallBack{
                cloudList.add(CloudListBean().apply {
                    type=4
                    title=note.title
                    subTypeStr=note.typeStr
                    date=System.currentTimeMillis()
                    grade=note.grade
                    listJson=Gson().toJson(note)
                    contentJson= Gson().toJson(noteContents)
                    downloadUrl=it
                })
                mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        deleteNote()
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