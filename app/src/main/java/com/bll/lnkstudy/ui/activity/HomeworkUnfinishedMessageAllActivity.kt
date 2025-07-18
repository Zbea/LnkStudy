package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.adapter.HomeworkMessageAllAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_page_number.ll_page_number
import org.greenrobot.eventbus.EventBus

/**
 * 作业本未做作业通知
 */
class HomeworkUnfinishedMessageAllActivity:BaseAppCompatActivity(),IContractView.IHomeworkView {
    private val mPresenter = HomeworkPresenter(this)
    private var homeworkType: HomeworkTypeBean? = null
    private var mAdapter: HomeworkMessageAllAdapter?=null
    private var messageIndex=0

    override fun onMessageAll(list: MutableList<HomeworkMessageList.MessageBean>) {
        DataBeanManager.homeworkMessages=list
        setData(list)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle("未完成作业通知")

        disMissView(ll_page_number)
        initRecyclerView()

        if (NetworkUtil.isNetworkConnected()){
            fetchData()
        }
        else{
            setData(DataBeanManager.homeworkMessages)
        }
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@HomeworkUnfinishedMessageAllActivity,50f),
            DP2PX.dip2px(this@HomeworkUnfinishedMessageAllActivity,20f),
            DP2PX.dip2px(this@HomeworkUnfinishedMessageAllActivity,50f),20)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkMessageAllAdapter(null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnChildClickListener{ item->
                messageIndex=item.position
                val messageBean=item.messageBean
                val typeId=if (messageBean.addType==1)MethodManager.getHomeworkAutoTypeId(messageBean.typeName,messageBean.subject) else messageBean.typeId
                homeworkType=HomeworkTypeDaoManager.getInstance().queryByTypeId(typeId)
                if (homeworkType==null){
                    showToast("本地未生成作业本，请前往作业对应科目自动生成作业本")
                    return@setOnChildClickListener
                }
                when(homeworkType?.state) {
                    3 -> {
                        MethodManager.gotoHomeworkRecord(this@HomeworkUnfinishedMessageAllActivity, homeworkType, messageBean)
                    }
                    1, 7 -> {
                        if (HomeworkPaperDaoManager.getInstance().queryByContentID(messageBean.contendId) != null) {
                            MethodManager.gotoHomeworkReelDrawing(this@HomeworkUnfinishedMessageAllActivity, homeworkType, Constants.DEFAULT_PAGE, messageBean)
                        } else {
                            showLoading()
                            loadHomeworkPaperImage(messageBean)
                        }
                    }
                    4 -> {
                        if (HomeworkBookDaoManager.getInstance().isExist(homeworkType!!.bookId)) {
                            MethodManager.gotoHomeworkBookDetails(this@HomeworkUnfinishedMessageAllActivity, homeworkType, messageBean)
                        } else {
                            //下载教辅
                            val intent = Intent(this@HomeworkUnfinishedMessageAllActivity, HomeworkBookStoreActivity::class.java)
                            intent.putExtra("bookId", homeworkType!!.bookId)
                            customStartActivity(intent)
                        }
                    }
                    else -> {
                        MethodManager.gotoHomeworkDrawing(this@HomeworkUnfinishedMessageAllActivity, homeworkType!!, Constants.DEFAULT_PAGE, messageBean)
                    }
                }
            }
        }
    }

    private fun setData(list: MutableList<HomeworkMessageList.MessageBean>){
        val items= mutableListOf<CatalogParentBean>()
        for (item in list){
            val timeStr= DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime))
            if (items.isEmpty()){
                val parentItem= CatalogParentBean()
                parentItem.title=timeStr
                parentItem.addSubItem(getChildItem(item,list.indexOf(item)))
                items.add(parentItem)
            }
            else{
                var parentItem= items.last()
                if (parentItem.title==timeStr){
                    parentItem.addSubItem(getChildItem(item,list.indexOf(item)))
                }
                else{
                    parentItem.subItems.last().isLast=true
                    parentItem= CatalogParentBean()
                    parentItem.title=timeStr
                    parentItem.addSubItem(getChildItem(item,list.indexOf(item)))
                    items.add(parentItem)
                }
            }
        }
        mAdapter?.setNewData(items as List<MultiItemEntity>)
        mAdapter?.expandAll()
    }

    private fun getChildItem(item:HomeworkMessageList.MessageBean,position:Int):CatalogChildBean{
        val childBean=CatalogChildBean()
        childBean.position=position
        childBean.messageBean=item
        return childBean
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

                        MethodManager.gotoHomeworkReelDrawing(this@HomeworkUnfinishedMessageAllActivity,homeworkType,Constants.DEFAULT_PAGE,item)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.HOMEWORK_MESSAGE_COMMIT_EVENT) {
            if (messageIndex<DataBeanManager.homeworkMessages.size)
                DataBeanManager.homeworkMessages.removeAt(messageIndex)
            if (DataBeanManager.homeworkMessages.isEmpty()){
                finish()
            }
            else{
                fetchData()
            }
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["grade"]=grade
        mPresenter.getMessageAll(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(Constants.HOMEWORK_MESSAGE_TIPS_EVENT)
    }

}