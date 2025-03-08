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
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.mvp.model.note.NoteContentBean
import com.bll.lnkstudy.ui.adapter.CloudNoteAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.zip.IZipCallback
import com.bll.lnkstudy.utils.zip.ZipUtils
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

class CloudNoteFragment: BaseCloudFragment() {
    private var mAdapter:CloudNoteAdapter?=null
    private var notes= mutableListOf<Note>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=14
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            fetchData()
        }
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,20f), DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = CloudNoteAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudNoteFragment.position=position
                CommonDialog(requireActivity()).setContent("确定下载？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            downloadItem()
                        }
                    })
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudNoteFragment.position=position
                if (view.id==R.id.iv_delete){
                    CommonDialog(requireActivity(),getScreenPosition()).setContent(R.string.item_is_delete_tips).builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteItem()
                            }
                        })
                }
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(30))
    }

    private fun downloadItem(){
        val note=notes[position]
        if (!NoteDaoManager.getInstance().isExistCloud(note.typeStr,note.title)){
            downloadNote(note)
        }
        else{
            showToast(R.string.toast_downloaded)
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(notes[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载笔记
     */
    private fun downloadNote(item: Note){
        showLoading()
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(item.downloadUrl))
        val fileTargetPath=FileAddress().getPathNote(item.typeStr,item.title)
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
                        override fun onFinish() {
                            if (item.typeStr!="我的密本"&&!ItemTypeDaoManager.getInstance().isExist(2,item.typeStr)){
                                val noteType = ItemTypeBean().apply {
                                    title = item.typeStr
                                    type=2
                                    date=System.currentTimeMillis()
                                }
                                val id= ItemTypeDaoManager.getInstance().insertOrReplaceGetId(noteType)
                                //创建笔记分类增量更新
                                DataUpdateManager.createDataUpdate(4,id.toInt(),1,Gson().toJson(noteType))
                            }
                            //添加笔记
                            item.id=null//设置数据库id为null用于重新加入
                            item.date=System.currentTimeMillis()
                            val id= NoteDaoManager.getInstance().insertOrReplaceGetId(item)
                            //新建笔记本增量更新
                            DataUpdateManager.createDataUpdate(4,id.toInt(),2,Gson().toJson(item))

                            val noteContents=Gson().fromJson(item.contentJson, object : TypeToken<List<NoteContentBean>>() {}.type) as MutableList<NoteContentBean>
                            for (contentBean in noteContents){
                                contentBean.id=null//设置数据库id为null用于重新加入
                                val id=NoteContentDaoManager.getInstance().insertOrReplaceGetId(contentBean)
                                //新建笔记内容增量更新
                                DataUpdateManager.createDataUpdate(4,id.toInt(),3,Gson().toJson(contentBean),contentBean.filePath)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                EventBus.getDefault().post(Constants.NOTE_EVENT)
                                deleteItem()
                                showToast(R.string.book_download_success)
                                hideLoading()
                            },500)
                        }

                        override fun onProgress(percentDone: Int) {
                        }

                        override fun onError(msg: String?) {
                            showToast(msg!!)
                            hideLoading()
                        }

                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast( R.string.book_download_fail)
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 4
        mCloudPresenter.getList(map)
    }

    override fun onCloudType(types: MutableList<String>) {
    }

    override fun onCloudList(cloudList: CloudList) {
        notes.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val note= Gson().fromJson(item.listJson, Note::class.java)
                note.cloudId=item.id
                note.downloadUrl=item.downloadUrl
                note.contentJson=item.contentJson
                notes.add(note)
            }
        }
        mAdapter?.setNewData(notes)
        setPageNumber(cloudList.total)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}