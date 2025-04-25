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
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.ui.activity.book.HomeworkBookStoreActivity
import com.bll.lnkstudy.ui.adapter.HomeworkMessageAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_list.rv_list

/**
 * 作业本未做作业通知
 */
class HomeworkMessageActivity:BaseAppCompatActivity() {

    private var homeworkType: HomeworkTypeBean? = null
    private var mAdapter: HomeworkMessageAdapter?=null
    private var messageIndex=Constants.DEFAULT_PAGE

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)
    }


    override fun initView() {
        setPageTitle(homeworkType?.name!!+"  作业通知")

        initRecyclerView()
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@HomeworkMessageActivity,50f),
            DP2PX.dip2px(this@HomeworkMessageActivity,30f),
            DP2PX.dip2px(this@HomeworkMessageActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkMessageAdapter(R.layout.item_homework_message_all,homeworkType?.messages!!,homeworkType?.createStatus!!).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                messageIndex=position
                when(homeworkType?.state){
                    2,6->{
                        MethodManager.gotoHomeworkDrawing(this@HomeworkMessageActivity,homeworkType!!,Constants.DEFAULT_PAGE,position)
                    }
                    3->{
                        MethodManager.gotoHomeworkRecord(this@HomeworkMessageActivity,homeworkType,messageIndex)
                    }
                    1->{
                        val messageBean=mAdapter?.data!![position] as HomeworkMessageList.MessageBean
                        if (HomeworkPaperDaoManager.getInstance().queryByContentID(messageBean.contendId)!=null){
                            MethodManager.gotoHomeworkReelDrawing(this@HomeworkMessageActivity,homeworkType,Constants.DEFAULT_PAGE,messageIndex)
                        }
                        else{
                            showLoading()
                            loadHomeworkPaperImage(messageBean)
                        }
                    }
                    4->{
                        if (HomeworkBookDaoManager.getInstance().isExist(homeworkType?.bookId!!)) {
                            MethodManager.gotoHomeworkBookDetails(this@HomeworkMessageActivity, homeworkType,messageIndex)
                        } else {
                            val intent = Intent(this@HomeworkMessageActivity, HomeworkBookStoreActivity::class.java)
                            intent.putExtra("bookId", homeworkType?.bookId!!)
                            customStartActivity(intent)
                        }
                    }
                }

            }
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
                        showToast("下载完成")
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
                        DataUpdateManager.createDataUpdateState(2, item.contendId, 2, homeworkTypeId, 1, Gson().toJson(paper), pathStr)
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    }
                })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.HOMEWORK_MESSAGE_COMMIT_EVENT) {
            mAdapter?.remove(messageIndex)
            DataBeanManager.homeworkMessages= mAdapter?.data!!
            setResult(Constants.RESULT_10001, Intent())
            if (mAdapter?.data.isNullOrEmpty())
                finish()
        }
    }

}