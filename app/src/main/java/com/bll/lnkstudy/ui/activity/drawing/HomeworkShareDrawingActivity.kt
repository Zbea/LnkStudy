package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.ResultStandardDetailsDialog
import com.bll.lnkstudy.dialog.ScoreDetailsDialog
import com.bll.lnkstudy.manager.HomeworkShareDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.util.Timer
import java.util.TimerTask
import java.util.stream.Collectors


/**
 * 作业卷提交
 */
class HomeworkShareDrawingActivity: BaseDrawingActivity(){

    private var homeworkType:HomeworkTypeBean?=null
    private var papers= mutableListOf<HomeworkShareBean>()
    private var paper: HomeworkShareBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var oldSubType=0
    private var page = 0//页码

    private var exoPlayer: ExoPlayer? = null
    private var timer: Timer? = null
    private var speed=1f
    private var isReadyRecorder=false

    private var iv_play:ImageView?=null
    private var tv_play:TextView?=null
    private var tv_start_time:TextView?=null
    private var tv_end_time:TextView?=null
    private var progressBar: ProgressBar?=null
    private var tv_speed_0_5:TextView?=null
    private var tv_speed_1:TextView?=null
    private var tv_speed_1_5:TextView?=null
    private var tv_speed_2:TextView?=null
    private var tv_speed_2_5:TextView?=null

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)

        papers=HomeworkShareDaoManager.getInstance().queryAll(homeworkType?.typeId!!)
        if(papers.size>0){
            currentPosition=papers.size-1
            onContent()
        }
        else{
            setDisableTouchInput(true)
        }
    }

    override fun initView() {
        disMissView(iv_btn)

        setDisableTouchInput(true)

        iv_play=findViewById(R.id.iv_play)
        tv_play=findViewById(R.id.tv_play)
        tv_start_time=findViewById(R.id.tv_start_time)
        tv_end_time=findViewById(R.id.tv_end_time)
        progressBar=findViewById(R.id.progressBar)
        tv_speed_0_5=findViewById(R.id.tv_speed_0_5)
        tv_speed_1=findViewById(R.id.tv_speed_1)
        tv_speed_1_5=findViewById(R.id.tv_speed_1_5)
        tv_speed_2=findViewById(R.id.tv_speed_2)
        tv_speed_2_5=findViewById(R.id.tv_speed_2_5)

        iv_score.setOnClickListener {
            if(paper?.question.isNullOrEmpty()||paper?.question!!.length>20){
                val answerImages=if (paper?.answerUrl.isNullOrEmpty()){
                    mutableListOf()
                }
                else{
                    paper!!.answerUrl?.split(",") as MutableList<String>
                }
                ScoreDetailsDialog(this,paper!!.title,paper!!.score,paper!!.questionType,paper!!.questionMode,answerImages,
                    paper!!.question).builder()
            }
            else{
                val items=DataBeanManager.getResultStandardItems(paper!!.subType,paper!!.commonName,paper!!.questionType).stream().collect(Collectors.toList())
                ResultStandardDetailsDialog(this,paper!!.title,paper!!.score,if (homeworkType!!.state==10)10 else paper!!.questionType,paper!!.question,items).builder()
            }
        }

        iv_play?.setOnClickListener {
            if (exoPlayer != null) {
                if (!isReadyRecorder){
                    showToast("录音未加载完成")
                    return@setOnClickListener
                }
                if (exoPlayer?.isPlaying == true) {
                    exoPlayer?.pause()
                    timer?.cancel()
                    changeMediaView(false)
                } else {
                    exoPlayer?.play()
                    startTimer()
                    changeMediaView(true)
                }
            }
        }

        tv_speed_0_5?.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(0.5f)
                setSpeedView(tv_speed_0_5)
            }
        }
        tv_speed_1?.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(1f)
                setSpeedView(tv_speed_1)
            }
        }
        tv_speed_1_5?.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(1.5f)
                setSpeedView(tv_speed_1_5)
            }
        }
        tv_speed_2?.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(2f)
                setSpeedView(tv_speed_2)
            }
        }
        tv_speed_2_5?.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(2.5f)
                setSpeedView(tv_speed_2_5)
            }
        }

    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name="(${item.name})"+item.title
            itemList.page=papers.indexOf(item)
            itemList.isDelete=true
            list.add(itemList)
        }
        list.reverse()
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (currentPosition!=pageNumber){
                    currentPosition = pageNumber
                    oldPosition=-1
                    page = 0
                    onContent()
                }
            }
            override fun onDelete(position: Int) {
                val item=papers[position]
                HomeworkShareDaoManager.getInstance().deleteBean(item)
                DataUpdateManager.deleteDateUpdate(2,item.id.toInt(), 2,item.typeId)
                DataUpdateManager.deleteDateUpdate(2,item.id.toInt(),3,item.typeId)
                FileUtils.delete(item.filePath)
                papers.removeAt(position)
                pageCount-=1
                if (position<=currentPosition){
                    currentPosition-=1
                    if (papers.isEmpty()){
                        setContentImageClear()
                    }
                    else{
                        page=0
                        onContent()
                    }
                }
            }
        })
    }

    override fun onPageDown() {
        val count=if (isExpand) pageCount-2 else pageCount-1
        if (page<count){
            page+=if (isExpand)2 else 1
            onContent()
        }
        else{
            if (currentPosition<papers.size-1){
                currentPosition+=1
                page=0
                onContent()
            }
        }
    }

    override fun onPageUp() {
        if (page>0){
            page-=if (isExpand)2 else 1
            onContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        //单屏时只有一页无法展开
        if (!isExpand&&pageCount==1)
            return
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    private fun onChangeRecord(){
        if (screenPos == Constants.SCREEN_LEFT) {
            ll_draw_content?.removeAllViews()
            ll_draw_content?.addView(v_content_a)
            ll_draw_content?.addView(ll_page_content_a)
            ll_draw_content?.addView(v_content_b)
            ll_draw_content?.addView(ll_record)
            ll_draw_content?.addView(ll_page_content_b)
            disMissView(ll_page_content_a, v_content_a,v_content_b)
            showView(ll_page_content_b, ll_record)
        } else if (screenPos == Constants.SCREEN_RIGHT) {
            ll_draw_content?.removeAllViews()
            ll_draw_content?.addView(ll_page_content_b)
            ll_draw_content?.addView(v_content_a)
            ll_draw_content?.addView(ll_page_content_a)
            ll_draw_content?.addView(v_content_b)
            ll_draw_content?.addView(ll_record)
            disMissView(ll_page_content_a, v_content_a,v_content_b)
            showView(ll_page_content_b, ll_record)
        }
    }

    override fun onContent() {
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]
        if (paper?.subType==3){
            isExpand=false
            pageCount=1
            if (oldSubType!=3)
                onChangeRecord()
        }
        else{
            disMissView(ll_record)
            pageCount=paper!!.paths.size
            if (oldSubType==3)
                onChangeExpandView()

            if (isExpand&&pageCount==1){
                onChangeExpandContent()
                return
            }

            if (isExpand&&page>pageCount-2)
                page=pageCount-2
            if (page<0)
                page=0
        }

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        if (paper?.subType==3){
            speed=1f
            isReadyRecorder=false
            exoPlayer = ExoPlayer.Builder(this).build()
            exoPlayer?.setMediaItem(MediaItem.fromUri(paper?.examUrl!!))
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            isReadyRecorder=true
                            val totalTime = exoPlayer?.duration!!.toInt()/1000
                            tv_end_time?.text = DateUtils.secondToString(totalTime)
                            progressBar?.max = totalTime
                        }
                        Player.STATE_ENDED -> {
                            tv_start_time?.text = "00:00"
                            exoPlayer?.pause()
                            exoPlayer?.seekTo(0)
                            progressBar?.progress = 0
                            changeMediaView(false)
                            timer?.cancel()
                        }
                    }
                }
            })
            exoPlayer?.prepare()
            tv_start_time?.text = "00:00"
            changeMediaView(false)

            tv_page_a.text="1"
            tv_page.text="1"
        }
        else{
            if (isExpand){
                setElikLoadPath(page,elik_a!!,v_content_a!!)
                setElikLoadPath(page+1,elik_b!!,v_content_b!!)
                if (screenPos==Constants.SCREEN_RIGHT){
                    tv_page_a.text="${page+1}"
                    tv_page.text="${page+1+1}"
                }
                else{
                    tv_page.text="${page+1}"
                    tv_page_a.text="${page+1+1}"
                }
            }
            else{
                setElikLoadPath(page,elik_b!!,v_content_b!!)
                tv_page.text="${page+1}"
            }
        }


        if (currentPosition!=oldPosition){
            if (paper?.question?.isNotEmpty() == true){
                showView(iv_score)
            }
            else{
                disMissView(iv_score)
            }
        }
        //用来判断重复加载
        oldPosition=currentPosition
        oldSubType=paper?.subType!!
    }


    //加载图片
    private fun setElikLoadPath(index: Int, elik:EinkPWInterface, view:ImageView) {
        val path=paper!!.paths[index]
        MethodManager.setImageFile(path,view)
        elik.setLoadFilePath(paper!!.drawPaths[index],true)
    }

    override fun onElikSava_a() {
        DataUpdateManager.createDataUpdateState(2,paper?.id!!.toInt(),3,homeworkType?.typeId!!,9,"","${paper?.filePath!!}/draw/")
    }

    override fun onElikSava_b() {
        DataUpdateManager.createDataUpdateState(2,paper?.id!!.toInt(),3,homeworkType?.typeId!!,9,"","${paper?.filePath!!}/draw/")
    }

    private fun setSpeedView(tvSpeed: TextView?) {
        if (!isReadyRecorder){
            showToast("录音未加载完成")
            return
        }
        tv_speed_0_5?.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_0_5?.setTextColor(getColor(R.color.black))
        tv_speed_1?.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_1?.setTextColor(getColor(R.color.black))
        tv_speed_1_5?.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_1_5?.setTextColor(getColor(R.color.black))
        tv_speed_2?.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_2?.setTextColor(getColor(R.color.black))
        tv_speed_2_5?.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_2_5?.setTextColor(getColor(R.color.black))
        tvSpeed?.background = getDrawable(R.drawable.bg_black_solid_5dp_corner)
        tvSpeed?.setTextColor(getColor(R.color.white))
    }

    private fun setSpeed(speed: Float) {
        this.speed=speed
        exoPlayer?.setPlaybackSpeed(speed)
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
            timer?.cancel()
            exoPlayer?.play()
            startTimer()
        }
    }

    /**
     * 更改播放view状态
     */
    private fun changeMediaView(boolean: Boolean) {
        if (boolean) {
            iv_play?.setImageResource(R.mipmap.icon_record_pause)
            tv_play?.text = "暂停"
        } else {
            iv_play?.setImageResource(R.mipmap.icon_record_play)
            tv_play?.text = "播放"
        }
    }

    private fun startTimer() {
        val periodTime = (1000/speed).toLong()
        Thread {
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        val currentTime=exoPlayer?.currentPosition!!.toInt()/1000
                        progressBar?.progress = currentTime
                        tv_start_time?.text = DateUtils.secondToString(currentTime)
                    }
                }
            }, 0, periodTime)
        }.start()
    }


    private fun release() {
        timer?.cancel()
        if (exoPlayer != null) {
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

}