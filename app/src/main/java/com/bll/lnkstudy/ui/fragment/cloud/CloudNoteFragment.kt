package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseCloudFragment
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteTypeBeanDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.NoteContentBean
import com.bll.lnkstudy.mvp.model.NoteTypeBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.ui.activity.CloudStorageActivity
import com.bll.lnkstudy.ui.adapter.NotebookAdapter
import com.bll.lnkstudy.utils.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_painting.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 我的日记 grade指年份 其他grade指年级
 */
class CloudNoteFragment: BaseCloudFragment() {

    var noteType=0
    private var noteTypeStr=""
    private var types= mutableListOf<String>()
    private var mAdapter:NotebookAdapter?=null
    private var notes= mutableListOf<NotebookBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        pageSize=10
        grade=DateUtils.getYear()
        types.add(getString(R.string.note_tab_diary))
        types.add(getString(R.string.note_tab_article))
        types.add(getString(R.string.note_tab_topic))
        noteTypeStr=types[0]
        initRecyclerView()
    }

    override fun lazyLoad() {
        mCloudPresenter.getType()
        fetchData()
    }

    private fun initTab(){
        for (i in types.indices) {
            rg_group.addView(getRadioButton(i ,types[i],types.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            noteType=id
            if (noteType==0){
                grade=(activity as CloudStorageActivity).year
                (activity as CloudStorageActivity).showYearView()
            }
            else{
                grade=(activity as CloudStorageActivity).grade
                (activity as CloudStorageActivity).closeYearView()
            }
            noteTypeStr=types[id]
            pageIndex=1
            fetchData()
        }
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DP2PX.dip2px(activity,25f), 0,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        mAdapter = NotebookAdapter(0,R.layout.item_note, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                val notebook=notes[position]
                val item=NotebookDaoManager.getInstance().queryNote(notebook.cloudId)
                if (item==null){
                    downloadNote(notebook)
                }
                else{
                    showToast(screenPos,R.string.toast_downloaded)
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudNoteFragment.position=position
                val ids= mutableListOf<Int>()
                ids.add(notes[position].cloudId)
                mCloudPresenter.deleteCloud(ids)
                true
            }
        }
    }

    /**
     * 下载笔记
     */
    private fun downloadNote(item: NotebookBean){
        item.id=null//设置数据库id为null用于重新加入
        //没有存储内容的笔记直接添加
        if (item.downloadUrl=="null")
        {
            addNote(item)
            return
        }
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        val zipFile = File(zipPath)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fileTargetPath=FileAddress().getPathNote(item.grade,item.typeStr,item.title)
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, fileTargetPath, object : ZipUtils.ZipCallback {
                        override fun onFinish(success: Boolean) {
                            if (success) {
                                val typeId=if(item.typeStr==getString(R.string.note_tab_diary)) 1 else 2
                                addNote(item)
                                //添加笔记内容
                                val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                for (json in jsonArray){
                                    val contentBean=Gson().fromJson(json, NoteContentBean::class.java)
                                    contentBean.id=null//设置数据库id为null用于重新加入
                                    val id=NoteContentDaoManager.getInstance().insertOrReplaceGetId(contentBean)
                                    //新建笔记内容增量更新
                                    DataUpdateManager.createDataUpdate(4,id.toInt(),3,typeId
                                        ,Gson().toJson(contentBean),File(contentBean.filePath).parent)
                                }
                                //删掉本地zip文件
                                FileUtils.deleteFile(File(zipPath))
                                Handler().postDelayed({
                                    EventBus.getDefault().post(Constants.NOTE_EVENT)
                                    showToast(screenPos,R.string.book_download_success)
                                    hideLoading()
                                },500)
                            }
                        }

                        override fun onProgress(percentDone: Int) {
                        }

                        override fun onError(msg: String?) {
                            showToast(screenPos,msg!!)
                            hideLoading()
                        }

                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(screenPos, R.string.book_download_fail)
                }
            })
    }

    /**
     * 添加笔记（如果下载的笔记分类本地不存在则添加）
     */
    private fun addNote(item: NotebookBean){
        val typeId=if(item.typeStr==getString(R.string.note_tab_diary)) 1 else 2
        if (!NoteTypeBeanDaoManager.getInstance().isExist(item.typeStr)){
            val noteBook = NoteTypeBean().apply {
                name = item.typeStr
                this.typeId = System.currentTimeMillis().toInt()
            }
            val id= NoteTypeBeanDaoManager.getInstance().insertOrReplaceGetId(noteBook)
            //创建笔记分类增量更新
            DataUpdateManager.createDataUpdate(4,id.toInt(),1,noteBook.typeId,Gson().toJson(noteBook))
        }
        val id=NotebookDaoManager.getInstance().insertOrReplaceGetId(item)
        //新建笔记本增量更新
        DataUpdateManager.createDataUpdate(4,id.toInt(),2,typeId,Gson().toJson(item))
    }

    fun changeYear(year:Int){
        grade=year
        fetchData()
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 4
        map["grade"] = grade
        map["subTypeStr"] = noteTypeStr
        mCloudPresenter.getList(map)
    }

    override fun onCloudType(types: MutableList<String>) {
        for (str in types){
            if (!this.types.contains(str))
            {
                this.types.add(str)
            }
        }
        initTab()
    }

    override fun onCloudList(item: CloudList) {
        setPageNumber(item.total)
        notes.clear()
        for (item in item.list){
            if (item.listJson.isNotEmpty()){
                val notebookBean= Gson().fromJson(item.listJson, NotebookBean::class.java)
                notebookBean.cloudId=item.id
                notebookBean.isCloud=true
                notebookBean.downloadUrl=item.downloadUrl
                notebookBean.contentJson=item.contentJson
                notes.add(notebookBean)
            }
        }
        mAdapter?.setNewData(notes)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

}