package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.ui.adapter.RecordAdapter
import kotlinx.android.synthetic.main.ac_record_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RecordListActivity : BaseActivity() {

    private var mAdapter: RecordAdapter? = null
    private var courseId = 0
    private var recordBeans = mutableListOf<RecordBean>()
    private var currentPos = 0//当前点击位置
    private var mediaPlayer: MediaPlayer? = null

    override fun layoutId(): Int {
        return R.layout.ac_record_list
    }

    override fun initData() {
        courseId = intent.getIntExtra("courseId", 0)
    }

    override fun initView() {
        var course=DataBeanManager.getIncetance().courses[courseId].name
        setPageTitle("$course 朗读录音")
        EventBus.getDefault().register(this)


        iv_add.setOnClickListener {
            addRecord()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = RecordAdapter(R.layout.item_record, recordBeans)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_record) {
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
        }

        findDatas()
    }


    private fun findDatas() {
        recordBeans = RecordDaoManager.getInstance(this).queryAllByCourseId(courseId)
        mAdapter?.setNewData(recordBeans)
    }


    //添加听读
    private fun addRecord() {
        val time = System.currentTimeMillis()
        var item = RecordBean()
        item.date = time
        item.courseId = courseId

        var bundle = Bundle()
        bundle.putSerializable("record", item)
        startActivity(
            Intent(this@RecordListActivity, RecordActivity::class.java)
                .putExtra("record", bundle)
        )
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