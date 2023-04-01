package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.model.paper.ReceivePaper
import com.bll.lnkstudy.mvp.presenter.TestPaperPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.PaperTypeAdapter
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.ZipUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_testpaper.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 考卷
 */
class PaperFragment : BaseFragment(),IContractView.IPaperView{

    private val mPresenter = TestPaperPresenter(this)
    private var mAdapter:PaperTypeAdapter?=null
    private var items= mutableListOf<PaperTypeBean>()
    private var course=""//课程
    private var receivePapers= mutableListOf<ReceivePaper.PaperBean>()//下载收到的考卷


    override fun onList(receivePaper: ReceivePaper?) {
        receivePapers= receivePaper?.list as MutableList<ReceivePaper.PaperBean>
        loadPapers(receivePapers)
        refreshView()
    }
    override fun onCommitSuccess() {
    }
    override fun onDeleteSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper
    }

    override fun initView() {
        setTitle(R.string.main_testpaper_title)

        EventBus.getDefault().register(this)
        initRecyclerView()
        initTab()
    }

    override fun lazyLoad() {
        fetchData()
    }

    @SuppressLint("WrongConstant")
    private fun initRecyclerView(){
        mAdapter = PaperTypeAdapter(R.layout.item_testpaper_type,items).apply {
            rv_list.layoutManager = GridLayoutManager(activity,2)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
            setOnItemClickListener { adapter, view, position ->
                gotoPaperDrawing(1,course,items[position].type)
            }
        }
    }

    //设置头部索引
    private fun initTab(){
        rg_group.removeAllViews()
        val courses= DataBeanManager.courses
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
        if(receivePapers.size>0){
            refreshView()
        }
    }

    /**
     * 刷新批改分 循环遍历
     */
    private fun refreshView(){
        for (item in receivePapers){
            for (ite in items){
                if (item.subject==ite.course&&item.examId==ite.type){
                    ite.score=item.score
                    ite.isPg=true
                }
                else{
                    ite.isPg=false
                }
            }
        }
        mAdapter?.notifyDataSetChanged()
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


    //下载收到的图片
    private fun loadPapers(papers:MutableList<ReceivePaper.PaperBean>) {
        for (item in papers) {
            //设置路径
            val file = File(FileAddress().getPathTestPaper(item.examId, item.id))
            item.path = file.path
            val images=item.submitUrl.split(",").toTypedArray()
            val imageDownLoad = ImageDownLoadUtils(activity,images, file.path)
            imageDownLoad.startDownload()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    mPresenter.deletePaper(item.id)
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    hideLoading()
//                        imageDownLoad.reloadImage()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun refreshData() {
        findData()
        fetchData()
    }

    override fun fetchData() {
        val map= HashMap<String,Any>()
        map["sendStatus"]=2
        mPresenter.getList(map)
    }

}