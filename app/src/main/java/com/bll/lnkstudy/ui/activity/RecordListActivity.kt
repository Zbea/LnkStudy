package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.HomeworkMessageSelectorDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.RecordAdapter
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.File

class RecordListActivity : BaseAppCompatActivity() , IContractView.IFileUploadView{

    private lateinit var mUploadPresenter:FileUploadPresenter
    private var mAdapter: RecordAdapter? = null
    private var course = ""
    private var homeworkType: HomeworkTypeBean? = null
    private var recordBeans = mutableListOf<RecordBean>()
    private var currentPos = -1//当前点击位置
    private var position = 0//当前点击位置
    private var mediaPlayer: MediaPlayer? = null
    private var pops= mutableListOf<PopupBean>()
    private var messages= mutableListOf<HomeworkMessage.MessageBean>()
    private var messageId=0
    private var messageIndex=0
    private var commitPaths= mutableListOf<String>()

    override fun onToken(token: String) {
        showLoading()
        FileImageUploadManager(token,commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=messageId
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = homeworkType?.typeId!!
                    mUploadPresenter.commit(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }
    override fun onSuccess(urls: MutableList<String>?) {
    }
    override fun onCommitSuccess() {
        val messageBean=messages[messageIndex]
        messages.removeAt(messageIndex)
        showToast(R.string.toast_commit_success)
        recordBeans[position].isCommit=true
        mAdapter?.notifyDataSetChanged()
        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            type=1
            studentTaskId=messageBean.studentTaskId
            content=messageBean.title
            homeworkTypeStr=homeworkType?.name
            course=homeworkType?.course
            time=System.currentTimeMillis()
        })
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeData()
        val bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkTypeBean
        course=homeworkType?.course!!
        val list=homeworkType?.messages

        if (!list.isNullOrEmpty()){
            for (item in list){
                if (item.endTime>0&&item.status==3){
                    messages.add(item)
                }
            }
        }

        pops.add(PopupBean(0,getString(R.string.edit),R.mipmap.icon_notebook_edit))
        pops.add(PopupBean(1,getString(R.string.delete),R.mipmap.icon_delete))
    }

    override fun initChangeData() {
        mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(course+getString(R.string.record_read))

        disMissView(ll_page_number)
        if (homeworkType?.isCloud == false){
            showView(iv_manager)
            iv_manager.setImageResource(R.mipmap.icon_group_add)
        }

        iv_manager.setOnClickListener {
            addRecord()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = RecordAdapter(R.layout.item_homework_record, recordBeans)
        rv_list.adapter = mAdapter
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f),DP2PX.dip2px(this,50f),20)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id == R.id.iv_record) {
                setPlay()
            }
            if (view.id == R.id.iv_setting){
                setSetting(view)
            }
            if (view.id==R.id.tv_save){
                if (messages.size==0)
                    return@setOnItemChildClickListener
                if (NetworkUtil(this).isNetworkConnected()){
                        commit()
                }
                else{
                    showNetworkDialog()
                }
            }

        }

        findDatas()
    }


    private fun findDatas() {
        recordBeans = RecordDaoManager.getInstance().queryAllByCourse(course,homeworkType?.typeId!!)
        mAdapter?.setNewData(recordBeans)
    }


    //添加听读
    private fun addRecord() {
        val time = System.currentTimeMillis()
        val item = RecordBean()
        item.date = time
        item.course = course
        item.typeId=homeworkType?.typeId!!
        item.typeStr=homeworkType?.name

        val bundle = Bundle()
        bundle.putSerializable("record", item)
        customStartActivity(Intent(this@RecordListActivity, RecordActivity::class.java).putExtra("recordBundle", bundle))
    }

    //点击播放
    private fun setPlay(){
        val path=recordBeans[position].path
        if (!File(path).exists())return
        if (currentPos == position) {
            if (mediaPlayer?.isPlaying == true) {
                pause(position)
            } else {
                mediaPlayer?.start()
                recordBeans[position].state=1
                mAdapter?.notifyItemChanged(position)//刷新为播放状态
            }
        } else {
            if (mediaPlayer?.isPlaying == true) {
                pause(currentPos)
            }
            release()
            play(path)
        }
        currentPos = position
    }

    private fun release(){
        if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun play(path:String){
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.setOnCompletionListener {
            recordBeans[position].state=0
            mAdapter?.notifyItemChanged(position)//刷新为结束状态
        }
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        recordBeans[position].state=1
        mAdapter?.notifyItemChanged(position)//刷新为播放状态
    }

    private fun pause(pos:Int){
        mediaPlayer?.pause()
        recordBeans[pos].state=0
        mAdapter?.notifyItemChanged(pos)//刷新为结束状态
    }


    private fun setSetting(view : View){
        PopupClick(this,pops,view,0).builder().setOnSelectListener{
            if (it.id == 0) {
                edit()
            }
            if (it.id == 0) {
                delete()
            }
        }

    }

    //修改笔记
    private fun edit(){
        val recordBean=recordBeans[position]
        InputContentDialog(this,getCurrentScreenPos(),recordBean.title).builder()?.setOnDialogClickListener { string ->
            recordBean.title=string
            mAdapter?.notifyDataSetChanged()
            RecordDaoManager.getInstance().insertOrReplace(recordBean)
            //修改本地增量更新
            DataUpdateManager.editDataUpdate(2,recordBean.id.toInt(),2,recordBean.typeId,Gson().toJson(recordBean))
        }
    }

    //删除
    private fun delete(){
        CommonDialog(this).setContent(R.string.item_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val recordBean=recordBeans[position]
                    mAdapter?.remove(position)
                    RecordDaoManager.getInstance().deleteBean(recordBean)
                    FileUtils.deleteFile(File(recordBean.path))
                    //修改本地增量更新
                    DataUpdateManager.deleteDateUpdate(2,recordBean.id.toInt(),2,recordBean.typeId)
                }
            })
    }

    /**
     * 提交录音
     */
    private fun commit(){
        commitPaths.clear()
        val items= mutableListOf<ItemList>()
        for (item in messages){
            items.add(ItemList().apply {
                id=item.studentTaskId
                name=item.title
            })
        }
        HomeworkMessageSelectorDialog(this, screenPos, items).builder()
            ?.setOnDialogClickListener {position,it->
                messageId=it.id
                messageIndex=position
                commitPaths.add(recordBeans[position].path)
                mUploadPresenter.getToken()
            }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.RECORD_EVENT) {
            findDatas()
        }
    }

    override fun onNetworkConnectionSuccess() {
        commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        closeNetwork()
    }

}