package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.mvp.model.TestPaperType
import com.bll.lnkstudy.ui.adapter.TestPaperTypeAdapter
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
class TestPaperFragment : BaseFragment(){

    private var popWindowBeans = mutableListOf<PopWindowBean>()

    private var mAdapter:TestPaperTypeAdapter?=null
    private var items= mutableListOf<TestPaperType>()
    private var course:CourseBean?=null//课程id

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setTitle("考卷")

        EventBus.getDefault().register(this)
        initData()
        initRecyclerView()
        initTab()


    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        val courses= DataBeanManager.getIncetance().courses
        course=courses[0]

        for (i in courses.indices) {
            rg_group.addView(getRadioButton(i ,courses[i].name,courses.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            course=courses[id]
        }
    }

    private fun initData(){
        var testPaper= TestPaperType()
        testPaper.isPg=true
        testPaper.name="单元"
        testPaper.namePath="dy"
        testPaper.type=0
        items.add(testPaper)

        var testPaper3=TestPaperType()
        testPaper3.isPg=true
        testPaper3.name="阶段"
        testPaper3.namePath="jd"
        testPaper3.type=1
        items.add(testPaper3)

        var testPaper4=TestPaperType()
        testPaper4.isPg=true
        testPaper4.name="学期"
        testPaper4.namePath="xq"
        testPaper4.type=2
        items.add(testPaper4)
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView(){

        mAdapter = TestPaperTypeAdapter(R.layout.item_testpaper_type,items)
        rv_list.layoutManager = GridLayoutManager(activity,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,80))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoPaperDrawing(1,course?.courseId!!,items[position].type)
        }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}