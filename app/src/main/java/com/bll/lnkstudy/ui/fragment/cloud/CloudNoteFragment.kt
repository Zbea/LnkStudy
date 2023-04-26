package com.bll.lnkstudy.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NotebookDaoManager
import com.bll.lnkstudy.mvp.model.NoteContentBean
import com.bll.lnkstudy.mvp.model.NotebookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.presenter.cloud.CloudPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ICloudView
import com.bll.lnkstudy.ui.activity.BookCollectActivity
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
class CloudNoteFragment:BaseFragment(),ICloudView {

    private var mPresenter= CloudPresenter(this)
    var noteType=0
    private var noteTypeStr=""
    private var types= mutableListOf<String>()
    private var mAdapter:NotebookAdapter?=null
    private var notes= mutableListOf<NotebookBean>()
    private var position=0

    override fun onList(item: CloudList) {
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

    override fun onType(types: MutableList<String>) {
        for (str in types){
            if (!this.types.contains(str))
            {
                this.types.add(str)
            }
        }
        initTab()
    }

    override fun onDelete() {
        mAdapter?.remove(position)
    }

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
        mPresenter.getType()
        fetchData()
    }

    private fun initTab(){
        for (i in types.indices) {
            rg_group.addView(getRadioButton(i ,types[i],types.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            noteType=id
            if (noteType==0){
                grade=(activity as BookCollectActivity).year
                (activity as BookCollectActivity).showYearView()
            }
            else{
                grade=(activity as BookCollectActivity).grade
                (activity as BookCollectActivity).closeYearView()
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
                mPresenter.deleteCloud(ids)
                true
            }
        }
    }

    /**
     * 下载笔记
     */
    private fun downloadNote(item: NotebookBean){
        //没有存储内容的笔记直接添加
        if (item.downloadUrl=="null")
        {
            NotebookDaoManager.getInstance().insert(item)
            return
        }
        showLoading()
        val zipPath = FileAddress().getPathZip(File(item.downloadUrl).name)
        val zipFile = File(zipPath)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fileTargetPath=FileAddress().getPathNote(item.typeStr,item.title,item.grade)

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
                                //添加笔记
                                NotebookDaoManager.getInstance().insert(item)
                                //添加笔记内容
                                val jsonArray= JsonParser().parse(item.contentJson).asJsonArray
                                for (json in jsonArray){
                                    val contentBean=Gson().fromJson(json, NoteContentBean::class.java)
                                    NoteContentDaoManager.getInstance().insertNote(contentBean)
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
        mPresenter.getList(map)
    }

}