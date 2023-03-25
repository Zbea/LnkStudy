package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.NotebookAddDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.ui.adapter.RecordAdapter
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RecordListActivity : BaseAppCompatActivity() {

    private var mAdapter: RecordAdapter? = null
    private var course = ""
    private var recordBeans = mutableListOf<RecordBean>()
    private var currentPos = 0//当前点击位置
    private var position = 0//当前点击位置
    private var mediaPlayer: MediaPlayer? = null
    private var pops= mutableListOf<PopupBean>()

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        course = intent.getStringExtra("course").toString()

        pops.add(PopupBean(0,getString(R.string.edit),R.mipmap.icon_notebook_edit))
        pops.add(PopupBean(1,getString(R.string.delete),R.mipmap.icon_delete))
    }

    override fun initView() {
        setPageTitle(course+getString(R.string.record_read))
        EventBus.getDefault().register(this)

        showView(iv_manager)
        iv_manager.setImageResource(R.mipmap.icon_group_add)
        disMissView(ll_page_number)

        iv_manager.setOnClickListener {
            addRecord()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = RecordAdapter(R.layout.item_record, recordBeans)
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

        }

        findDatas()
    }


    private fun findDatas() {
        recordBeans = RecordDaoManager.getInstance().queryAllByCourse(course)
        mAdapter?.setNewData(recordBeans)
    }


    //添加听读
    private fun addRecord() {
        val time = System.currentTimeMillis()
        var item = RecordBean()
        item.date = time
        item.course = course

        var bundle = Bundle()
        bundle.putSerializable("record", item)
        customStartActivity(
            Intent(this@RecordListActivity, RecordActivity::class.java)
                .putExtra("record", bundle)
        )
    }

    //点击播放
    private fun setPlay(){
        if (currentPos == position) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordBeans[position].path)
                mediaPlayer?.setOnCompletionListener {
                    recordBeans[position].state=0
                    mAdapter?.notifyDataSetChanged()//刷新为结束状态
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                recordBeans[position].state=1
                mAdapter?.notifyDataSetChanged()//刷新为播放状态
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    recordBeans[position].state=0
                    mAdapter?.notifyDataSetChanged()//刷新为结束状态
                } else {
                    mediaPlayer?.start()
                    recordBeans[position].state=1
                    mAdapter?.notifyDataSetChanged()//刷新为播放状态
                }
            }
        } else {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(recordBeans[position].path)
            mediaPlayer?.setOnCompletionListener {
                recordBeans[position].state=0
                mAdapter?.notifyDataSetChanged()//刷新为结束状态
            }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            recordBeans[position].state=1
            mAdapter?.notifyDataSetChanged()//刷新为播放状态
        }
        currentPos = position
    }


    private fun setSetting(view : View){
        PopupClick(this,pops,view,5).builder().setOnSelectListener{
            if (it.id == 0) {
                edit(recordBeans[position].title)
            }
            if (it.id == 0) {
                delete()
            }
        }

    }

    //修改笔记
    private fun edit(content:String){
        NotebookAddDialog(this,getCurrentScreenPos(),getString(R.string.rename),content,getString(R.string.toast_input_title)).builder()?.setOnDialogClickListener { string ->
            recordBeans[position].title = string
            mAdapter?.notifyDataSetChanged()
            RecordDaoManager.getInstance()
                .insertOrReplace(recordBeans[position])
        }
    }

    //删除
    private fun delete(){
        CommonDialog(this).setContent(R.string.item_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    var item=recordBeans[position]
                    recordBeans.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    RecordDaoManager.getInstance().deleteBean(item)
                }

            })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == Constants.RECORD_EVENT) {
            findDatas()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }


}