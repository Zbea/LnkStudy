package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkDetailsDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetailsBean
import com.bll.lnkstudy.mvp.model.paper.ExamScoreItem
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicScoreAdapter
import com.bll.lnkstudy.utils.*
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_correct.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class CorrectActivity: BaseDrawingActivity(), IContractView.IFileUploadView {

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
                    map["score"]=tv_total_score.text.toString().toInt()
                    map["question"]=scoreListToJson(currentScores)
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
        showToast(R.string.toast_commit_success)
        when (state) {
            2 -> {
                val homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(commitItem?.course, commitItem?.typeId!!)
                for (index in commitItem?.contents!!) {
                    val homework = homeworks[index]
                    homework.state = 1
                    homework.title = commitItem?.title
                    homework.contentId = commitItem?.messageId!!
                    homework.commitDate = System.currentTimeMillis()
                    homework.score=tv_total_score.text.toString().toInt()
                    homework.correctJson=scoreListToJson(currentScores)
                    homework.commitJson=""
                    HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                    DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2, Gson().toJson(homework))
                }
            }
            4 -> {
                for (page in commitItem?.contents!!){
                    val item= HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(commitItem?.bookId!!,page)
                    item.score=tv_total_score.text.toString().toInt()
                    item.correctJson=scoreListToJson(currentScores)
                    item.commitJson=""
                    HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(item)
                    //更新增量数据
                    DataUpdateManager.editDataUpdate(7, item.id.toInt(),2,item.bookId ,Gson().toJson(item))
                }
            }
            1 -> {
                val paper=HomeworkPaperDaoManager.getInstance().queryByContentID(commitItem?.messageId!!)
                paper.state=1
                paper.correctJson=scoreListToJson(currentScores)
                paper.score=tv_total_score.text.toString().toInt()
                paper.commitJson=""
                HomeworkPaperDaoManager.getInstance()?.insertOrReplace(paper)
                //更新目录增量数据
                DataUpdateManager.editDataUpdate(2, paper?.contentId!!, 2, paper.typeId, Gson().toJson(paper))
            }
        }

        //添加提交详情
        HomeworkDetailsDaoManager.getInstance().insertOrReplace(HomeworkDetailsBean().apply {
            type=1
            studentTaskId=commitItem?.messageId!!
            content=commitItem?.title
            homeworkTypeStr=commitItem?.typeName
            course=commitItem?.course
            time=System.currentTimeMillis()
        })

        //设置批改完成通知
        setResult(10001, Intent())
        finish()
    }


    override fun layoutId(): Int {
        return R.layout.ac_correct
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
        setPWEnabled(false)

//        tv_score_label.text=if (scoreMode==1) "赋分批改框" else "对错批改框"

        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0,-DP2PX.dip2px(this,100f))
        }
        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0,DP2PX.dip2px(this,100f))
        }

        tv_correct_save.setOnClickListener {
            if (!tv_total_score.text.isNullOrEmpty()){
                if (NetworkUtil(this).isNetworkConnected()){
                    mUploadPresenter.getToken()
                }
                else{
                    showNetworkDialog()
                }
            }
        }

        tv_total_score.setOnClickListener {
            InputContentDialog(this@CorrectActivity,3,"",1).builder().setOnDialogClickListener(){
                tv_total_score.text=it
            }
        }

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
                    val item=currentScores[position]
                    when(view.id){
                        R.id.tv_score->{
                            if (scoreMode==1){
                                InputContentDialog(this@CorrectActivity,3,"最大${item.label}",1).builder().setOnDialogClickListener(){
                                    if (item.label!=it.toInt()){
                                        item.result=0
                                    }
                                    item.score= it
                                    notifyItemChanged(position)
                                    setTotalScore()
                                }
                            }
                        }
                        R.id.iv_result->{
                            if (item.result==0){
                                item.result=1
                            }
                            else{
                                item.result=0
                            }
                            if (scoreMode==1){
                                item.score= (item.result*item.label).toString()
                            }
                            else{
                                item.score=item.result.toString()
                            }
                            notifyItemChanged(position)
                            setTotalScore()
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceGridItemDeco(2,DP2PX.dip2px(this@CorrectActivity,15f)))
            }
        }
        else{
            rv_list_score.layoutManager = LinearLayoutManager(this)
            mTopicMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,scoreMode,currentScores).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setCustomItemChildClickListener{ position, view, childPos ->
                    val scoreItem=currentScores[position]
                    val childItem=scoreItem.childScores[childPos]
                    when(view.id){
                        R.id.tv_score->{
                            if (scoreMode==1){
                                InputContentDialog(this@CorrectActivity,3,"最大${childItem.label}",1).builder().setOnDialogClickListener(){
                                    if (childItem.label!=it.toInt()){
                                        childItem.result=0
                                    }
                                    childItem.score= it
                                    //获取小题总分
                                    var scoreTotal=0
                                    for (item in scoreItem.childScores){
                                        scoreTotal+=MethodManager.getScore(item.score)
                                    }
                                    scoreItem.score=scoreTotal.toString()
                                    notifyItemChanged(position)
                                    setTotalScore()
                                }
                            }
                        }
                        R.id.iv_result->{
                            if (childItem.result==0){
                                childItem.result=1
                            }
                            else{
                                childItem.result=0
                            }
                            if (scoreMode==1){
                                childItem.score= (childItem.result*childItem.label).toString()
                                //获取小题总分
                                var scoreTotal=0
                                for (item in scoreItem.childScores){
                                    scoreTotal+= MethodManager.getScore(item.score)
                                }
                                scoreItem.score=scoreTotal.toString()
                            }
                            else{
                                childItem.score=childItem.result.toString()
                                var totalRight=0
                                for (item in scoreItem.childScores){
                                    if (item.result==1)
                                        totalRight+= 1
                                }
                                scoreItem.score=totalRight.toString()
                            }
                            notifyItemChanged(position)
                            setTotalScore()
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@CorrectActivity,15f)))
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
            GlideUtils.setImageUrl(this, images[posImage],v_content_a)
            if (posImage+1<getImageSize()){
                GlideUtils.setImageUrl(this,images[posImage+1],v_content_b)
            }
            else{
                v_content_b?.setImageResource(0)
            }
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            GlideUtils.setImageUrl(this, images[posImage],v_content_b)
            tv_page.text="${posImage+1}"
        }
    }


    /**
     * 总分变化
     */
    private fun setTotalScore(){
        if (tv_total_score!=null){
            var total=0
            for (item in currentScores){
                total+=MethodManager.getScore(item.score)
            }
            tv_total_score.text=total.toString()
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

    override fun onNetworkConnectionSuccess() {
        mUploadPresenter.getToken()
    }
}