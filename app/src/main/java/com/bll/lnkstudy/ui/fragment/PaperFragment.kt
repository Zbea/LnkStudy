package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.PaperTypeBean
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_testpaper.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 考卷
 */
class PaperFragment : BaseFragment(){

    private var mAdapter:PaperTypeAdapter?=null
    private var items= mutableListOf<PaperTypeBean>()
    private var course=""//课程

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setTitle("考卷")

        EventBus.getDefault().register(this)
        initRecyclerView()
        initTab()
    }

    override fun lazyLoad() {
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView(){

        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type,items)
        rv_list.layoutManager = GridLayoutManager(activity,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,80))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoPaperDrawing(1,course,items[position].type)
        }

    }

    //设置头部索引
    private fun initTab(){
        rg_group.removeAllViews()
        val courses= DataBeanManager.getIncetance().courses
        if (courses.size>0){
            course=courses[0]
            for (i in courses.indices) {
                rg_group.addView(getRadioButton(i ,courses[i],courses.size-1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                course=courses[id]
                findData()
            }
            findData()
        }
    }

    private fun findData(){
        items=PaperTypeDaoManager.getInstance().queryAll(course)
        mAdapter?.setNewData(items)
    }

    /**
     * 自动压缩zip
     */
    private fun autoZip() {

        ZipUtils.zip(Constants.TESTPAPER_PATH + "/$mUserId", "testPaper", object : ZipUtils.ZipCallback {
            override fun onStart() {
                showLog("testPaper开始打包上传")
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish(success: Boolean) {
                showLog(success.toString())
            }
            override fun onError(msg: String?) {
                showLog(msg!!)
            }
        })
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == Constants.AUTO_UPLOAD_EVENT) {
            autoZip()
        }
        if (msgFlag==Constants.COURSE_EVENT){
            //刷新科目
            initTab()
        }
        if (msgFlag==Constants.RECEIVE_PAPER_COMMIT_EVENT){
            findData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}