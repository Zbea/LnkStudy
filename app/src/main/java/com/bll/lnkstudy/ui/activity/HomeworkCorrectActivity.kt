package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.NumberDialog
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.drawing.HomeworkBookDetailsActivity
import com.bll.lnkstudy.ui.activity.drawing.HomeworkDrawingActivity
import com.bll.lnkstudy.ui.activity.drawing.HomeworkPaperDrawingActivity
import com.bll.lnkstudy.ui.adapter.TopicMultistageScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicTwoScoreAdapter
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ScoreItemUtils
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
import kotlinx.android.synthetic.main.ac_homework_correct.tv_correct_total_score
import kotlinx.android.synthetic.main.ac_homework_correct.tv_page_current
import kotlinx.android.synthetic.main.ac_homework_correct.tv_page_total_bottom
import kotlinx.android.synthetic.main.ac_homework_correct.tv_standartTime
import kotlinx.android.synthetic.main.ac_homework_correct.tv_takeTime
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


class HomeworkCorrectActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private var commitItem: HomeworkCommitInfoItem? = null
    private val mUploadPresenter = FileUploadPresenter(this, 3)
    private var state = 0//作业本分类
    private var posImage = 0
    private var posAnswer = 0
    private var images = mutableListOf<String>()
    private var takeTime = 0L

    var correctMode = 0
    var scoreMode = 0 //1赋分，2对错
    var answerImages = mutableListOf<String>()//答题地址
    var initScores = mutableListOf<ScoreItem>()
    var currentScores = mutableListOf<ScoreItem>()
    var mTopicScoreAdapter: TopicScoreAdapter?=null
    var mTopicTwoScoreAdapter: TopicTwoScoreAdapter?=null
    var mTopicMultistageScoreAdapter: TopicMultistageScoreAdapter?=null

    override fun onToken(token: String) {
        showLoading()
        FileImageUploadManager(token, images).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    hideLoading()
                    val map = HashMap<String, Any>()
                    map["studentTaskId"] = commitItem?.messageId!!
                    map["studentUrl"] = ToolUtils.getImagesStr(urls)
                    map["commonTypeId"] = commitItem?.typeId!!
                    map["takeTime"] = takeTime
                    if (state == 4) {
                        map["page"] = ToolUtils.getImagesStr(commitItem?.contents!!)
                    }
                    map["score"] = tv_correct_total_score.text.toString().toDouble()
                    map["question"] = Gson().toJson(initScores)
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
        showToastLong(if (commitItem?.submitState==0)"作业提交成功" else "作业已完成")

        FileUtils.delete(FileAddress().getPathHomeworkCorrect(commitItem?.messageId!!))

        when (state) {
            2, 6 -> {
                val homeworks = HomeworkContentDaoManager.getInstance().queryAllByContentId(commitItem?.homeworkTypeId!!, commitItem?.messageId!!)
                for (homework in homeworks) {
                    val mergePath=FileAddress().getPathHomeworkDrawingMerge(homework.path)
                    if (FileUtils.isExist(mergePath)){
                        if (commitItem?.submitState==1){
                            homework.title=commitItem?.title//不提交成功后改标题
                            homework.state=2
                        }
                        homework.isHomework = false
                        homework.date = System.currentTimeMillis()
                        homework.score = tv_correct_total_score.text.toString().toDouble()
                        homework.correctJson = Gson().toJson(initScores)
                        homework.answerUrl = commitItem?.answerUrl
                        homework.correctMode = commitItem?.correctMode!!
                        homework.scoreMode = commitItem?.scoreMode!!
                        homework.commitJson = ""
                        HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
                        DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2, homework.homeworkTypeId, Gson().toJson(homework))
                    }
                    else{
                        HomeworkContentDaoManager.getInstance().deleteBean(homework)
                        DataUpdateManager.deleteDateUpdate(2,homework.id.toInt(),2,homework.homeworkTypeId)
                    }
                }
                ActivityManager.getInstance().finishActivity(HomeworkDrawingActivity::class.java.name)
            }

            4 -> {
                for (page in commitItem?.contents!!) {
                    val item = HomeworkBookCorrectDaoManager.getInstance().queryCorrectBean(commitItem?.bookId!!, page)
                    if (commitItem?.submitState==1){
                        item.state=2
                    }
                    item.score = tv_correct_total_score.text.toString().toDouble()
                    item.correctJson = Gson().toJson(initScores)
                    item.commitJson = ""
                    HomeworkBookCorrectDaoManager.getInstance().insertOrReplace(item)
                    //更新增量数据
                    DataUpdateManager.editDataUpdate(7, item.id.toInt(), 1, item.bookId, Gson().toJson(item))
                }
                ActivityManager.getInstance().finishActivity(HomeworkBookDetailsActivity::class.java.name)
            }

            1 -> {
                val paper = HomeworkPaperDaoManager.getInstance().queryByContentID(commitItem?.messageId!!)
                if (commitItem?.submitState==1){
                    paper.state=2
                }
                paper.isHomework = false
                paper.date = System.currentTimeMillis()
                paper.correctJson = Gson().toJson(initScores)
                paper.score = tv_correct_total_score.text.toString().toDouble()
                paper.commitJson = ""
                HomeworkPaperDaoManager.getInstance()?.insertOrReplace(paper)
                //更新目录增量数据
                DataUpdateManager.editDataUpdate(2, paper?.contentId!!, 2, paper.typeId, Gson().toJson(paper))

                FileUtils.deleteFile(File(paper.filePath + "/draw/"))
                FileUtils.deleteFile(File(paper.filePath + "/merge/"))
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
        commitItem = intent.getBundleExtra("bundle")?.getSerializable("homeworkCommit") as HomeworkCommitInfoItem
        screenPos = Constants.SCREEN_LEFT
        correctMode = commitItem!!.correctMode
        scoreMode = commitItem!!.scoreMode
        answerImages = commitItem!!.answerUrl.split(",") as MutableList<String>
        initScores = ScoreItemUtils.questionToList(commitItem!!.correctJson, false)
        currentScores = ScoreItemUtils.jsonListToModuleList(correctMode, ScoreItemUtils.questionToList(commitItem!!.correctJson, true))
        images = commitItem!!.paths
        state = commitItem?.state!!
        takeTime = commitItem?.takeTime!!
    }

    override fun initView() {
        disMissView(iv_btn, iv_tool, iv_catalog, iv_draft)
        setDisableTouchInput(true)

        if (commitItem?.standardTime!! > 0) {
            showView(tv_standartTime)
            tv_standartTime.text = "标准：" + commitItem?.standardTime + "分钟"
        }
        tv_takeTime.text = "用时：" + DateUtils.longToMinute(commitItem?.takeTime!!) + "分钟"

        tv_correct_save.text=if (commitItem?.submitState==0) "提交" else "保存"
        tv_correct_save.setOnClickListener {
            if (!tv_correct_total_score.text.isNullOrEmpty()) {
                if (!NetworkUtil.isNetworkConnected()) {
                    showToast("网络连接失败")
                    return@setOnClickListener
                }
                showLoading()
                initScores=ScoreItemUtils.updateInitListData(initScores, currentScores,correctMode)
                if (commitItem?.submitState==0){
                    mUploadPresenter.getToken()
                }
                else{
                    val map = HashMap<String, Any>()
                    map["studentTaskId"] = commitItem?.messageId!!
                    mUploadPresenter.commitHomework(map)
                }
            }
        }

//        tv_correct_total_score.setOnClickListener {
//            NumberDialog(this,getCurrentScreenPos(),"请输入总分").builder().setDialogClickListener{
//                tv_total_score.text= it.toString()
//            }
//        }

        setAnswerView()
        setScoreListView()

        onChangeExpandView()
        onContent()
    }

    /**
     * 设置答案view
     */
    private fun setAnswerView() {
        iv_answer_up.setOnClickListener {
            sv_answer.scrollBy(0, -DP2PX.dip2px(this, 100f))
        }
        iv_answer_down.setOnClickListener {
            sv_answer.scrollBy(0, DP2PX.dip2px(this, 100f))
        }

        btn_page_up_bottom.setOnClickListener {
            if (posAnswer > 0) {
                posAnswer -= 1
                GlideUtils.setImageUrl(this, answerImages[posAnswer], iv_answer)
                setAnswerPageView()
            }
        }

        btn_page_down_bottom.setOnClickListener {
            if (posAnswer < answerImages.size - 1) {
                posAnswer += 1
                GlideUtils.setImageUrl(this, answerImages[posAnswer], iv_answer)
                setAnswerPageView()
            }
        }

        GlideUtils.setImageUrl(this, answerImages[posAnswer], iv_answer)
        setAnswerPageView()
    }


    /**
     * 设置批改详情小题列表
     */
    private fun setScoreListView() {
        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0, -DP2PX.dip2px(this, 200f))
        }
        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0, DP2PX.dip2px(this, 200f))
        }

        when (correctMode) {
            1, 2 -> {
                rv_list_score.layoutManager = GridLayoutManager(this, 3)
                mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_score, scoreMode, currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        setChangeItemScore(view, position)
                    }
                    rv_list_score.addItemDecoration(SpaceGridItemDeco(3, DP2PX.dip2px(this@HomeworkCorrectActivity, 15f)))
                }
            }
            3, 4, 5 -> {
                rv_list_score.layoutManager = LinearLayoutManager(this)
                mTopicTwoScoreAdapter = TopicTwoScoreAdapter(if (correctMode == 5) R.layout.item_topic_multi_score else R.layout.item_topic_two_score, scoreMode, currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        val item = currentScores[position]
                        if (item.childScores.isNullOrEmpty()) {
                            setChangeItemScore(view, position)
                        }
                    }
                    setCustomItemChildClickListener { view, position, childAdapter, childPos ->
                        val scoreItem = currentScores[position]
                        val childItem = scoreItem.childScores[childPos]
                        when (view.id) {
                            R.id.tv_score -> {
                                if (scoreMode == 1) {
                                    NumberDialog(this@HomeworkCorrectActivity, 2, "最大输入${childItem.label}", childItem.label).builder().setDialogClickListener {
                                        childItem.score = it
                                        childItem.result = ScoreItemUtils.getItemScoreResult(childItem)
                                        childAdapter.notifyItemChanged(childPos, "updateScore")

                                        scoreItem.score = ScoreItemUtils.getItemScoreTotal(scoreItem.childScores)
                                        scoreItem.result = ScoreItemUtils.getItemScoreResult(scoreItem)

                                        notifyItemChanged(position, "updateScore")
                                        setTotalScore()
                                    }
                                }
                            }
                            R.id.iv_result -> {
                                if (childItem.result == 0) {
                                    childItem.result = 1
                                } else {
                                    childItem.result = 0
                                }
                                childItem.score = childItem.result * childItem.label
                                childAdapter.notifyItemChanged(childPos, "updateScore")

                                scoreItem.score = ScoreItemUtils.getItemScoreTotal(scoreItem.childScores)
                                scoreItem.result = ScoreItemUtils.getItemScoreResult(scoreItem)

                                notifyItemChanged(position, "updateScore")
                                setTotalScore()
                            }
                        }
                    }
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@HomeworkCorrectActivity, 15f)))
                }
            }
            6, 7 -> {
                val sharedPool = RecyclerView.RecycledViewPool()
                rv_list_score.setRecycledViewPool(sharedPool)
                rv_list_score.layoutManager = LinearLayoutManager(this)
                mTopicMultistageScoreAdapter = TopicMultistageScoreAdapter(R.layout.item_topic_two_score, scoreMode, currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        val item = currentScores[position]
                        if (item.childScores.isNullOrEmpty()) {
                            setChangeItemScore(view, position)
                        }
                    }
                    setCustomItemChildClickListener(object : TopicMultistageScoreAdapter.OnItemChildClickListener {
                        override fun onParentClick(view: View, position: Int, twoAdapter: TopicTwoScoreAdapter, parentPosition: Int) {
                            val rootItem = currentScores[position]
                            val parentItem = rootItem.childScores[parentPosition]
                            if (parentItem.childScores.isNullOrEmpty()) {
                                when (view.id) {
                                    R.id.tv_score -> {
                                        if (scoreMode == 1) {
                                            NumberDialog(this@HomeworkCorrectActivity, 2, "最大输入${parentItem.label}", parentItem.label).builder().setDialogClickListener {
                                                parentItem.score = it
                                                parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                                twoAdapter.notifyItemChanged(parentPosition, "updateScore")

                                                rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                                rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                                notifyItemChanged(position, "updateScore")
                                                setTotalScore()
                                            }
                                        }
                                    }

                                    R.id.iv_result -> {
                                        if (parentItem.result == 0) {
                                            parentItem.result = 1
                                        } else {
                                            parentItem.result = 0
                                        }
                                        parentItem.score = parentItem.result * parentItem.label
                                        twoAdapter.notifyItemChanged(parentPosition, "updateScore")

                                        rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                        rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                        notifyItemChanged(position, "updateScore")
                                        setTotalScore()
                                    }
                                }
                            }
                        }
                        override fun onChildClick(view: View, position: Int, twoAdapter: TopicTwoScoreAdapter, parentPosition: Int, childAdapter: TopicTwoScoreAdapter.ChildAdapter, childPos: Int) {
                            val rootItem = currentScores[position]
                            val parentItem = rootItem.childScores[parentPosition]
                            val childItem = parentItem.childScores[childPos]
                            when (view.id) {
                                R.id.tv_score -> {
                                    if (scoreMode == 1) {
                                        NumberDialog(this@HomeworkCorrectActivity, 2, "最大输入${childItem.label}", childItem.label).builder().setDialogClickListener {
                                            childItem.score = it
                                            childItem.result = ScoreItemUtils.getItemScoreResult(childItem)
                                            childAdapter.notifyItemChanged(childPos, "updateScore")

                                            parentItem.score = ScoreItemUtils.getItemScoreTotal(parentItem.childScores)
                                            parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                            twoAdapter.notifyItemChanged(parentPosition, "updateScore")

                                            rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                            rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                            notifyItemChanged(position, "updateScore")
                                            setTotalScore()
                                        }
                                    }
                                }
                                R.id.iv_result -> {
                                    if (childItem.result == 0) {
                                        childItem.result = 1
                                    } else {
                                        childItem.result = 0
                                    }
                                    childItem.score = childItem.result * childItem.label
                                    childAdapter.notifyItemChanged(childPos, "updateScore")

                                    parentItem.score = ScoreItemUtils.getItemScoreTotal(parentItem.childScores)
                                    parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                    twoAdapter.notifyItemChanged(parentPosition, "updateScore")

                                    rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                    rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                    notifyItemChanged(position, "updateScore")
                                    setTotalScore()
                                }
                            }
                        }
                    })
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@HomeworkCorrectActivity, 15f)))
                }
            }
        }
    }

    /**
     * 大题数据变化
     */
    private fun setChangeItemScore(view: View, position: Int) {
        val item = currentScores[position]
        when (view.id) {
            R.id.tv_score -> {
                if (scoreMode == 1) {
                    NumberDialog(this, 2, "最大输入${item.label}", item.label).builder().setDialogClickListener {
                        item.score = it
                        item.result = ScoreItemUtils.getItemScoreResult(item)
                        when (correctMode) {
                            1, 2 -> {
                                mTopicScoreAdapter?.notifyItemChanged(position)
                            }
                            3, 4, 5 -> {
                                mTopicTwoScoreAdapter?.notifyItemChanged(position,"updateScore")
                            }
                            6, 7 -> {
                                mTopicMultistageScoreAdapter?.notifyItemChanged(position,"updateScore")
                            }
                        }
                        setTotalScore()
                    }
                }
            }

            R.id.iv_result -> {
                if (item.result == 0) {
                    item.result = 1
                } else {
                    item.result = 0
                }
                item.score = item.result * item.label
                when (correctMode) {
                    1, 2 -> {
                        mTopicScoreAdapter?.notifyItemChanged(position)
                    }

                    3, 4, 5 -> {
                        mTopicTwoScoreAdapter?.notifyItemChanged(position)
                    }

                    6, 7 -> {
                        mTopicMultistageScoreAdapter?.notifyItemChanged(position)
                    }
                }
                setTotalScore()
            }
        }
    }

    /**
     * 总分变化
     */
    private fun setTotalScore() {
        var total = 0.0
        for (item in currentScores) {
            total += item.score
        }
        tv_correct_total_score.text = total.toString()
    }


    override fun onChangeExpandContent() {
        if (getImageSize() == 1)
            return
        changeErasure()
        isExpand = !isExpand
        onChangeExpandView()
        onContent()
    }

    override fun onPageDown() {
        if (posImage < getImageSize() - 1) {
            posImage += if (isExpand) 2 else 1
        }
        onContent()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= if (isExpand) 2 else 1
        }
        onContent()
    }

    override fun onContent() {
        if (isExpand && posImage > getImageSize() - 2)
            posImage = getImageSize() - 2
        if (isExpand && posImage < 0)
            posImage = 0

        tv_page_total.text = "${getImageSize()}"
        tv_page_total_a.text = "${getImageSize()}"

        if (isExpand) {
            GlideUtils.setImageNoCacheUrl(this, images[posImage], v_content_a)
            elik_a?.setLoadFilePath(FileAddress().getPathHomeworkCorrect(commitItem?.messageId!!,posImage+1), true)
            GlideUtils.setImageNoCacheUrl(this, images[posImage + 1], v_content_b)
            elik_b?.setLoadFilePath(FileAddress().getPathHomeworkCorrect(commitItem?.messageId!!,posImage+1+1), true)
            tv_page.text = "${posImage + 1}"
            tv_page_a.text = "${posImage + 1 + 1}"
        } else {
            GlideUtils.setImageNoCacheUrl(this, images[posImage], v_content_b)
            elik_b?.setLoadFilePath(FileAddress().getPathHomeworkCorrect(commitItem?.messageId!!,posImage+1), true)
            tv_page.text = "${posImage + 1}"
        }
    }

    /**
     * 设置答案页面
     */
    private fun setAnswerPageView() {
        tv_page_current.text = "${posAnswer + 1}"
        tv_page_total_bottom.text = "${answerImages.size}"
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize(): Int {
        if (images.isEmpty())
            return 0
        return images.size
    }

}