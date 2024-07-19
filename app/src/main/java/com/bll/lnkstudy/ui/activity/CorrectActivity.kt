package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicScoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_correct.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_page_number.tv_page_total
import java.io.File

class CorrectActivity: BaseDrawingActivity() {

    private var posImage = 0
    private var posAnswer= 0
    private var images = mutableListOf<String>()

    override fun layoutId(): Int {
        return R.layout.ac_correct
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT

    }

    override fun initView() {
        tv_score_label.text=if (scoreMode==1) "赋分批改框" else "对错批改框"

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

        btn_page_up.setOnClickListener {
            if (posAnswer>0){
                posAnswer-=1
                GlideUtils.setImageUrl(this,answerImages[posAnswer],iv_answer)
                setAnswerPageView()
            }
        }

        btn_page_down.setOnClickListener {
            if (posAnswer>0){
                posAnswer-=1
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
            mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_child_score,scoreMode,correctMode,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setOnItemChildClickListener { adapter, view, position ->
                    val item=currentScores[position]
                    when(view.id){
                        R.id.tv_score->{
                            if (scoreType==1){
                                InputContentDialog(this@CorrectActivity,3,"最大${item.label}",1).builder().setOnDialogClickListener(){
                                    if (item.label!=it.toInt()){
                                        item.result=0
                                    }
                                    item.score= it
                                    notifyItemChanged(position)
                                    setTotalScore(scoreType)
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
                            if (scoreType==1)
                                item.score= (item.result*item.label).toString()
                            notifyItemChanged(position)
                            setTotalScore(scoreType)
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceGridItemDeco(2,20))
            }
        }
        else{
            rv_list_score.layoutManager = LinearLayoutManager(this)
            mTopicMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,scoreMode,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setCustomItemChildClickListener{ position, view, childPos ->
                    val scoreItem=currentScores[position]
                    val childItem=scoreItem.childScores[childPos]
                    when(view.id){
                        R.id.tv_score->{
                            if (scoreType==1){
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
                                    setTotalScore(scoreType)
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
                            if (scoreType==1){
                                childItem.score= (childItem.result*childItem.label).toString()
                                //获取小题总分
                                var scoreTotal=0
                                for (item in scoreItem.childScores){
                                    scoreTotal+= MethodManager.getScore(item.score)
                                }
                                scoreItem.score=scoreTotal.toString()
                            }
                            notifyItemChanged(position)
                            setTotalScore(scoreType)
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceItemDeco(0,0,0,20,false))
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
        if (posImage>0){
            posImage-=if (isExpand)2 else 1
        }
        onContent()
    }

    override fun onPageUp() {
        if (posImage<getImageSize()-1){
            posImage+=if (isExpand)2 else 1
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
            elik_a?.setPWEnabled(true,true)
            elik_b?.setPWEnabled(true,true)

            val masterImage="${getPath()}/${posImage+1}.png"//原图
            GlideUtils.setImageFile(this,File(masterImage),v_content_a)
            val drawPath = getPathDrawStr(posImage+1)
            elik_a?.setLoadFilePath(drawPath, true)

            if (posImage+1<getImageSize()){
                val masterImage_b="${getPath()}/${posImage+1+1}.png"//原图
                GlideUtils.setImageFile(this,File(masterImage_b),v_content_b)
                val drawPath_b = getPathDrawStr(posImage+1+1)
                elik_b?.setLoadFilePath(drawPath_b, true)
            }
            else{
                elik_b?.setPWEnabled(false,false)
                v_content_b?.setImageResource(0)
            }

            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            elik_b?.setPWEnabled(true,true)
            val masterImage="${getPath()}/${posImage+1}.png"//原图
            GlideUtils.setImageFile(this, File(masterImage),v_content_b)
            val drawPath = getPathDrawStr(posImage+1)
            elik_b?.setLoadFilePath(drawPath, true)

            tv_page.text="${posImage+1}"
        }
    }


    /**
     * 总分变化
     */
    private fun setTotalScore(scoreType: Int){
        if (tv_total_score!=null){
            var total=0
            //统计总分
            if (scoreType==1){
                for (item in currentScores){
                    total+=MethodManager.getScore(item.score)
                }
                tv_total_score.text=total.toString()
            }
            else{
                if (correctMode<3){
                    for (item in currentScores){
                        if (item.result==1){
                            total+=1
                        }
                    }
                }
                else{
                    for (item in currentScores){
                        for (childItem in item.childScores){
                            if (childItem.result==1){
                                total+=1
                            }
                        }
                    }
                }
                tv_total_score.text=total.toString()
            }
        }
    }

    /**
     * 设置答案页面
     */
    private fun setAnswerPageView(){
        tv_page_total.text="${answerImages.size}"
        tv_page_current.text="${posAnswer+1}"
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (images.isEmpty())
            return 0
        return images.size
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        val path=""
        return path
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }
}