package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity
import com.bll.lnkstudy.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicScoreAdapter
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_homework_correct.btn_page_down_bottom
import kotlinx.android.synthetic.main.ac_homework_correct.btn_page_up_bottom
import kotlinx.android.synthetic.main.ac_homework_correct.iv_answer
import kotlinx.android.synthetic.main.ac_homework_correct.iv_answer_down
import kotlinx.android.synthetic.main.ac_homework_correct.iv_answer_up
import kotlinx.android.synthetic.main.ac_homework_correct.iv_score_down
import kotlinx.android.synthetic.main.ac_homework_correct.iv_score_up
import kotlinx.android.synthetic.main.ac_homework_correct.rv_list_score
import kotlinx.android.synthetic.main.ac_homework_correct.sv_answer
import kotlinx.android.synthetic.main.ac_homework_correct.tv_correct_save
import kotlinx.android.synthetic.main.ac_homework_correct.tv_page_current
import kotlinx.android.synthetic.main.ac_homework_correct.tv_page_total_bottom
import kotlinx.android.synthetic.main.ac_homework_correct.tv_standartTime
import kotlinx.android.synthetic.main.ac_homework_correct.tv_takeTime
import kotlinx.android.synthetic.main.ac_homework_correct.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File

class HomeworkCorrectActivity: BaseDrawingActivity(), IContractView.IFileUploadView {

    private var commitItem: HomeworkCommitInfoItem?=null
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private var state=0//作业本分类
    private var posImage = 0
    private var posAnswer= 0
    private var images = mutableListOf<String>()
    private var takeTime=0L

    override fun onToken(token: String) {
        showLoading()
        FileImageUploadManager(token, images).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    hideLoading()
                    val map= HashMap<String, Any>()
                    map["studentTaskId"]=commitItem?.messageId!!
                    map["studentUrl"]= ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = commitItem?.typeId!!
                    map["takeTime"]=takeTime
                    if (state==4){
                        map["page"]= ToolUtils.getImagesStr(commitItem?.contents!!)
                    }
                    map["score"]=tv_total_score.text.toString().toDouble()
                    map["question"]=Gson().toJson(currentScores)
                    mUploadPresenter.commit(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onCommitSuccess() {
        hideLoading()
        showToastLong("作业提交成功")
        when (state) {
            2,6 -> {
                val homeworks = HomeworkContentDaoManager.getInstance().queryAllByContentId(commitItem?.homeworkTypeId!!,commitItem?.messageId!!)
                for (homework in homeworks) {
                    homework.isHomework=false
                    homework.date=System.currentTimeMillis()
                    homework.score=tv_total_score.text.toString().toDouble()
                    homework.correctJson=Gson().toJson(currentScores)
                    homework.answerUrl=commitItem?.answerUrl
                    homework.correctMode=commitItem?.correctMode!!
                    homework.scoreMode=commitItem?.scoreMode!!
                    homework.commitJson=""
                    HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                    DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2,homework.homeworkTypeId, Gson().toJson(homework))
                }
                ActivityManager.getInstance().finishActivity(HomeworkDrawingActivity::class.java.name)
            }
            4 -> {
                for (page in commitItem?.contents!!){
                    val item= HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(commitItem?.bookId!!,page)
                    item.score=tv_total_score.text.toString().toDouble()
                    item.correctJson=Gson().toJson(currentScores)
                    item.commitJson=""
                    HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(item)
                    //更新增量数据
                    DataUpdateManager.editDataUpdate(7, item.id.toInt(),2,item.bookId ,Gson().toJson(item))
                }
                ActivityManager.getInstance().finishActivity(HomeworkBookDetailsActivity::class.java.name)
            }
            1 -> {
                val paper=HomeworkPaperDaoManager.getInstance().queryByContentID(commitItem?.messageId!!)
                paper.isHomework = false
                paper.date=System.currentTimeMillis()
                paper.correctJson=Gson().toJson(currentScores)
                paper.score=tv_total_score.text.toString().toDouble()
                paper.commitJson=""
                HomeworkPaperDaoManager.getInstance()?.insertOrReplace(paper)
                //更新目录增量数据
                DataUpdateManager.editDataUpdate(2, paper?.contentId!!, 2, paper.typeId, Gson().toJson(paper))

                FileUtils.deleteFile(File(paper.filePath+"/draw/"))
                FileUtils.deleteFile(File(paper.filePath+"/merge/"))
                ActivityManager.getInstance().finishActivity(HomeworkPaperDrawingActivity::class.java.name)
            }
        }
        //设置批改完成通知
        EventBus.getDefault().post(Constants.HOMEWORK_MESSAGE_COMMIT_EVENT)
        finish()
    }


    override fun layoutId(): Int {
        return R.layout.ac_homework_correct
    }

    override fun initData() {
        commitItem=intent.getBundleExtra("bundle")?.getSerializable("homeworkCommit") as HomeworkCommitInfoItem
        screenPos=Constants.SCREEN_LEFT
        correctMode=commitItem!!.correctMode
        scoreMode=commitItem!!.scoreMode
        answerImages= commitItem!!.answerUrl.split(",") as MutableList<String>
        currentScores= scoreJsonToList(commitItem!!.correctJson) as MutableList<ExamScoreItem>
        images=commitItem!!.paths
        state=commitItem?.state!!
        takeTime=commitItem?.takeTime!!
    }

    override fun initView() {
        disMissView(iv_btn,iv_tool,iv_catalog,iv_draft)
        setDisableTouchInput(true)

        if (commitItem?.standardTime!!>0){
            showView(tv_standartTime)
            tv_standartTime.text="标准："+commitItem?.standardTime+"分钟"
        }
        tv_takeTime.text="完成："+DateUtils.longToMinute(commitItem?.takeTime!!)+"分钟"

        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0,-DP2PX.dip2px(this,100f))
        }
        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0,DP2PX.dip2px(this,100f))
        }

        tv_correct_save.setOnClickListener {
            if (!tv_total_score.text.isNullOrEmpty()){
                if (!NetworkUtil.isNetworkConnected()){
                    showToast("网络连接失败，无法提交")
                    return@setOnClickListener
                }
                showLoading()
                mUploadPresenter.getToken()
            }
        }

//        tv_total_score.setOnClickListener {
//            NumberDialog(this,getCurrentScreenPos(),"请输入总分").builder().setDialogClickListener{
//                tv_total_score.text= it.toString()
//            }
//        }

        setAnswerView()
        initRecyclerViewScore()
        onChangeExpandView()
        onContent()
    }

    /**
     * 设置答案view
     */
    private fun setAnswerView(){
        iv_answer_up.setOnClickListener {
            sv_answer.scrollBy(0,-DP2PX.dip2px(this,100f))
        }
        iv_answer_down.setOnClickListener {
            sv_answer.scrollBy(0,DP2PX.dip2px(this,100f))
        }

        btn_page_up_bottom.setOnClickListener {
            if (posAnswer>0){
                posAnswer-=1
                GlideUtils.setImageUrl(this,answerImages[posAnswer],iv_answer)
                setAnswerPageView()
            }
        }

        btn_page_down_bottom.setOnClickListener {
            if (posAnswer<answerImages.size-1){
                posAnswer+=1
                GlideUtils.setImageUrl(this,answerImages[posAnswer],iv_answer)
                setAnswerPageView()
            }
        }

        GlideUtils.setImageUrl(this,answerImages[posAnswer],iv_answer)
        setAnswerPageView()
    }

    private fun initRecyclerViewScore(){
        if (correctMode<3){
            rv_list_score.layoutManager = GridLayoutManager(this,2)
            mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_child_score,scoreMode,correctMode,currentScores).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setOnItemChildClickListener { adapter, view, position ->
                    setChangeItemScore(view,position)
                }
                rv_list_score.addItemDecoration(SpaceGridItemDeco(2,DP2PX.dip2px(this@HomeworkCorrectActivity,15f)))
            }
        }
        else{
            rv_list_score.layoutManager = LinearLayoutManager(this)
            mTopicMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,scoreMode,currentScores).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setOnItemChildClickListener { adapter, view, position ->
                    val item=currentScores[position]
                    //批改状态为已提交未批改 且 没有子题目才能执行
                    if (item.childScores.isNullOrEmpty()){
                        setChangeItemScore(view,position)
                    }
                }
                setCustomItemChildClickListener{ position, view, childPos ->
                    val scoreItem=currentScores[position]
                    val childItem=scoreItem.childScores[childPos]
                    when(view.id){
                        R.id.tv_score->{
//                            if (scoreMode==1){
//                                NumberDialog(this@CorrectActivity,2,"最大${childItem.label}",childItem.label).builder().setDialogClickListener{
//                                    if (childItem.label!=it){
//                                        childItem.result=0
//                                    }
//                                    childItem.score= it
//                                    //获取小题总分
//                                    var scoreTotal=0.0
//                                    for (item in scoreItem.childScores){
//                                        scoreTotal+=item.score
//                                    }
//                                    scoreItem.score=scoreTotal
//                                    notifyItemChanged(position)
//                                    setTotalScore()
//                                }
//                            }
                        }
                        R.id.iv_result->{
                            if (childItem.result==0){
                                childItem.result=1
                            }
                            else{
                                childItem.result=0
                            }
                            if (scoreMode==1){
                                childItem.score= childItem.result*childItem.label
                                //获取小题总分
                                var scoreTotal=0.0
                                for (item in scoreItem.childScores){
                                    scoreTotal+= item.score
                                }
                                scoreItem.score=scoreTotal
                            }
                            else{
                                childItem.score= childItem.result.toDouble()
                                var totalRight=0
                                for (item in scoreItem.childScores){
                                    if (item.result==1)
                                        totalRight+= 1
                                }
                                scoreItem.score= totalRight.toDouble()
                            }
                            notifyItemChanged(position)
                            setTotalScore()
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@HomeworkCorrectActivity,15f)))
            }
        }
    }

    /**
     * 大题数据变化
     */
    private fun setChangeItemScore(view: View, position: Int){
        val item=currentScores[position]
        when(view.id){
            R.id.tv_score->{
//                if (scoreMode==1){
//                    NumberDialog(this,2,"最大输入${item.label}",item.label).builder().setDialogClickListener{
//                        if (item.label!=it){
//                            item.result=0
//                        }
//                        item.score= it
//                        setTotalScore()
//                        if (correctMode<3){
//                            mTopicScoreAdapter?.notifyItemChanged(position)
//                        }
//                        else{
//                            mTopicMultiAdapter?.notifyItemChanged(position)
//                        }
//                    }
//                }
            }
            R.id.iv_result->{
                if (item.result==0){
                    item.result=1
                }
                else{
                    item.result=0
                }

                if (scoreMode==1){
                    item.score= item.result*item.label
                }
                else{
                    item.score= item.result.toDouble()
                }
                setTotalScore()
                if (correctMode<3){
                    mTopicScoreAdapter?.notifyItemChanged(position)
                }
                else{
                    mTopicMultiAdapter?.notifyItemChanged(position)
                }
            }
        }
    }


    override fun onChangeExpandContent() {
        if (getImageSize()==1)
            return
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        onContent()
    }

    override fun onPageDown() {
        if (posImage<getImageSize()-1){
            posImage+=if (isExpand)2 else 1
        }
        onContent()
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=if (isExpand)2 else 1
        }
        onContent()
    }

    override fun onContent() {
        if (isExpand&&posImage>getImageSize()-2)
            posImage=getImageSize()-2
        if (isExpand&&posImage<0)
            posImage=0

        tv_page_total.text="${getImageSize()}"
        tv_page_total_a.text="${getImageSize()}"

        if (isExpand){
            GlideUtils.setImageNoCacheUrl(this, images[posImage],v_content_a)
            if (posImage+1<getImageSize()){
                GlideUtils.setImageNoCacheUrl(this,images[posImage+1],v_content_b)
            }
            else{
                v_content_b?.setImageResource(0)
            }
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            GlideUtils.setImageNoCacheUrl(this, images[posImage],v_content_b)
            tv_page.text="${posImage+1}"
        }
    }


    /**
     * 总分变化
     */
    private fun setTotalScore(){
        if (tv_total_score!=null){
            var total=0.0
            for (item in currentScores){
                total+=item.score
            }
            tv_total_score.text= total.toString()
        }
    }

    /**
     * 设置答案页面
     */
    private fun setAnswerPageView(){
        tv_page_current.text="${posAnswer+1}"
        tv_page_total_bottom.text="${answerImages.size}"
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (images.isEmpty())
            return 0
        return images.size
    }

}