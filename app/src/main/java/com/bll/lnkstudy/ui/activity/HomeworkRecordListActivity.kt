package com.bll.lnkstudy.ui.activity

import android.media.MediaPlayer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ResultStandardDetailsDialog
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.ui.adapter.RecordAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_page_number.ll_page_number
import kotlinx.android.synthetic.main.common_title.iv_manager
import java.io.File
import java.util.stream.Collectors

class HomeworkRecordListActivity : BaseAppCompatActivity(){

    private var mAdapter: RecordAdapter? = null
    private var course = ""
    private var homeworkType: HomeworkTypeBean? = null
    private var recordBeans = mutableListOf<RecordBean>()
    private var currentPos = -1//当前点击位置
    private var position = 0//当前点击位置
    private var mediaPlayer: MediaPlayer? = null

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)
        course=homeworkType?.course!!
    }


    override fun initView() {
        setPageTitle(course+getString(R.string.record_read))
        disMissView(ll_page_number)
        setImageManager(R.mipmap.icon_add)

        iv_manager.setOnClickListener {
            MethodManager.gotoHomeworkRecord(this,homeworkType,Constants.DEFAULT_PAGE)
        }

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f), DP2PX.dip2px(this,30f), DP2PX.dip2px(this,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = RecordAdapter(R.layout.item_homework_record, recordBeans)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            when(view.id){
                R.id.iv_record->{
                    setPlay()
                }
                R.id.iv_edit->{
                    edit()
                }
                R.id.iv_delete->{
                    delete()
                }
                R.id.tv_result->{
                    val item=recordBeans[position]
                    val items=DataBeanManager.getResultStandardItems(3,"",item.correctModule) .stream().collect(Collectors.toList())
                    ResultStandardDetailsDialog(this,item.title,item.score,item.correctModule,item.question,items).builder()
                }
            }
        }

        fetchData()
    }

    //点击播放
    private fun setPlay(){
        val path=recordBeans[position].path
        if (!File(path).exists())return
        if (currentPos == position) {
            if (mediaPlayer!!.isPlaying) {
                pause(position)
            } else {
                mediaPlayer?.start()
                recordBeans[position].state=1
                mAdapter?.notifyItemChanged(position)//刷新为播放状态
            }
        } else {
            if (currentPos!=-1){
                if (mediaPlayer!!.isPlaying) {
                    pause(currentPos)
                }
                release()
            }
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

    //修改笔记
    private fun edit(){
        val recordBean=recordBeans[position]
        InputContentDialog(this,getCurrentScreenPos(),recordBean.title).builder().setOnDialogClickListener { string ->
            recordBean.title=string
            mAdapter?.notifyItemChanged(position)
            RecordDaoManager.getInstance().insertOrReplace(recordBean)
            refreshDataUpdate(recordBean)
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
                    DataUpdateManager.deleteDateUpdate(2,recordBean.id.toInt(),2,homeworkType?.typeId!!)
                }
            })
    }

    /**
     * 刷新增量更新
     */
    private fun refreshDataUpdate(recordBean: RecordBean){
        RecordDaoManager.getInstance().insertOrReplace(recordBean)
        //修改本地增量更新
        DataUpdateManager.editDataUpdate(2,recordBean.id.toInt(),2,recordBean.homeworkTypeId,Gson().toJson(recordBean))
    }

    override fun fetchData() {
        val total = RecordDaoManager.getInstance().queryAllByCourse(course,homeworkType?.typeId!!).size
        setPageNumber(total)
        recordBeans=RecordDaoManager.getInstance().queryAllByCourse(course,homeworkType?.typeId!!,pageIndex,pageSize)
        mAdapter?.setNewData(recordBeans)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.RECORD_EVENT) {
            pageIndex=1
            fetchData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

}