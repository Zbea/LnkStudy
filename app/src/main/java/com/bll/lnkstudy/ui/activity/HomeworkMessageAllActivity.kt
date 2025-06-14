package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.HomeworkMessageAllAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_list.rv_list

/**
 * 作业本未做作业通知
 */
class HomeworkMessageAllActivity:BaseAppCompatActivity(),IContractView.IHomeworkView {
    private val mPresenter = HomeworkPresenter(this)
    private var homeworkType: HomeworkTypeBean? = null
    private var mAdapter: HomeworkMessageAllAdapter?=null
    private var messageIndex=Constants.DEFAULT_PAGE
    val items= mutableListOf<MultiItemEntity>()

    override fun onMessageAll(list: MutableList<HomeworkMessageList.MessageBean>) {
        for (item in list){
            val timeStr=DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime))
            if (items.isEmpty()){
                val parentItem=CatalogParentBean()
                parentItem.title=timeStr
                parentItem.isExpanded=true
                parentItem.addSubItem(getChildItem(item))
                items.add(parentItem)
            }
            else{
                var parentItem=items.last() as CatalogParentBean
                if (parentItem.title==timeStr){
                    parentItem.addSubItem(getChildItem(item))
                }
                else{
                    parentItem=CatalogParentBean()
                    parentItem.title=timeStr
                    parentItem.isExpanded=true
                    parentItem.addSubItem(getChildItem(item))
                    items.add(parentItem)
                }
            }
        }
        showLog(Gson().toJson(items))
        initRecyclerView()
    }

    private fun getChildItem(item:HomeworkMessageList.MessageBean):CatalogChildBean{
        val childBean=CatalogChildBean()
        childBean.title=item.title
        childBean.course=item.subject
        childBean.commonType=item.typeName
        childBean.endTime=item.endTime
//        childBean.messageBean=item
        childBean.minute=item.minute
        childBean.selfBatchStatus=item.selfBatchStatus
        return childBean
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        val map=HashMap<String,Any>()
        map["grade"]=grade
        mPresenter.getMessageAll(map)
    }


    override fun initView() {
        setPageTitle("作业通知")

        initRecyclerView()
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@HomeworkMessageAllActivity,50f),
            DP2PX.dip2px(this@HomeworkMessageAllActivity,20f),
            DP2PX.dip2px(this@HomeworkMessageAllActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkMessageAllAdapter(items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
//            setOnItemClickListener { adapter, view, position ->
//
//                messageIndex=position
//                when(homeworkType?.state){
//                    3->{
//                        MethodManager.gotoHomeworkRecord(this@HomeworkMessageAllActivity,homeworkType,messageIndex)
//                    }
//                    1,7->{
//                        val messageBean=mAdapter?.data!![position] as HomeworkMessageList.MessageBean
//                        if (HomeworkPaperDaoManager.getInstance().queryByContentID(messageBean.contendId)!=null){
//                            MethodManager.gotoHomeworkReelDrawing(this@HomeworkMessageAllActivity,homeworkType,Constants.DEFAULT_PAGE,messageIndex)
//                        }
//                        else{
//                            showLoading()
//                            loadHomeworkPaperImage(messageBean)
//                        }
//                    }
//                    4->{
//                        if (HomeworkBookDaoManager.getInstance().isExist(homeworkType?.bookId!!)) {
//                            MethodManager.gotoHomeworkBookDetails(this@HomeworkMessageAllActivity, homeworkType,messageIndex)
//                        } else {
//                            //下载教辅
//                            val intent = Intent(this@HomeworkMessageAllActivity, HomeworkBookStoreActivity::class.java)
//                            intent.putExtra("bookId", homeworkType?.bookId!!)
//                            customStartActivity(intent)
//                        }
//                    }
//                    else->{
//                        MethodManager.gotoHomeworkDrawing(this@HomeworkMessageAllActivity,homeworkType!!,Constants.DEFAULT_PAGE,position)
//                    }
//                }
//            }
        }
    }

    /**
     * 作业卷下载图片、将图片保存到作业
     */
    private fun loadHomeworkPaperImage(item: HomeworkMessageList.MessageBean) {
        val homeworkTypeId=homeworkType?.typeId!!
        //设置路径 作业卷路径
        val pathStr = FileAddress().getPathHomework(homeworkType?.course!!, homeworkTypeId, item.contendId)
        val images = item.examUrl.split(",").toMutableList()
        val paths = mutableListOf<String>()
        val drawPaths = mutableListOf<String>()
        for (i in images.indices) {
            paths.add("$pathStr/${i + 1}.png")
            drawPaths.add("$pathStr/draw/${i + 1}.png")
        }
        FileMultitaskDownManager.with(this).create(images).setPath(paths)
            .startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        hideLoading()
                        //创建作业卷目录
                        val paper = HomeworkPaperBean().apply {
                            contentId = item.contendId
                            course = item.subject
                            this.homeworkTypeId = homeworkTypeId
                            typeId = item.typeId
                            typeName = item.typeName
                            title = item.title
                            filePath = pathStr
                            isHomework=true
                            date=System.currentTimeMillis()
                            this.paths = paths
                            this.drawPaths = drawPaths
                            endTime = item.endTime //提交时间
                            answerUrl = item.answerUrl
                            isSelfCorrect = item.selfBatchStatus == 1
                            correctJson = item.question
                            correctMode = item.questionType
                            scoreMode = item.questionMode
                        }
                        HomeworkPaperDaoManager.getInstance().insertOrReplace(paper)
                        //创建增量数据
                        DataUpdateManager.createDataUpdateState(2, item.contendId, 2, homeworkTypeId, homeworkType?.state!!, Gson().toJson(paper), pathStr)

                        MethodManager.gotoHomeworkReelDrawing(this@HomeworkMessageAllActivity,homeworkType,Constants.DEFAULT_PAGE,messageIndex)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

//    override fun onEventBusMessage(msgFlag: String) {
//        if (msgFlag == Constants.HOMEWORK_MESSAGE_COMMIT_EVENT) {
//            mAdapter?.remove(messageIndex)
//            DataBeanManager.homeworkMessages= mAdapter?.data!!
//            setResult(Constants.RESULT_10001, Intent())
//            if (mAdapter?.data.isNullOrEmpty())
//                finish()
//        }
//    }

}