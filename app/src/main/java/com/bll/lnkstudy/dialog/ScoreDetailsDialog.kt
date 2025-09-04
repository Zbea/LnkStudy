package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.ResultStandardItem.ResultChildItem
import com.bll.lnkstudy.mvp.model.paper.ScoreItem
import com.bll.lnkstudy.ui.adapter.TopicMultistageScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicResultStandardAdapter
import com.bll.lnkstudy.ui.adapter.TopicScoreAdapter
import com.bll.lnkstudy.ui.adapter.TopicTwoScoreAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.ScoreItemUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.bll.lnkstudy.widget.SpaceItemDeco
import java.util.stream.Collectors

class ScoreDetailsDialog(val context: Context, private val title:String, private val score:Double,
                         private val correctMode:Int,private val scoreMode:Int,private val answerImages:MutableList<String>,
                         private val correctJson:String) {

    fun builder(): ScoreDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.common_correct_score)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 800f)) / 2
        dialog.show()

        var currentScores= mutableListOf<ScoreItem>()
        var currentResults= mutableListOf<ResultChildItem>()
        if (correctMode >0) {
            currentScores = ScoreItemUtils.jsonListToModuleList(correctJson,correctMode)
        }
        else{
            currentResults=DataBeanManager.getResultChildItems().stream().collect(Collectors.toList())
            for (item in currentResults){
                if (item.sort==score.toInt()){
                    item.isCheck=true
                }
            }
        }

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        val iv_score_up=dialog.findViewById<ImageView>(R.id.iv_score_up)
        val iv_score_down=dialog.findViewById<ImageView>(R.id.iv_score_down)

        val tvTitle=dialog.findViewById<TextView>(R.id.tv_title)
        tvTitle.text=title

        val tvScore=dialog.findViewById<TextView>(R.id.tv_score)
        tvScore.text= DataBeanManager.getResultStandardStr(score,correctMode)

        val tvAnswer=dialog.findViewById<TextView>(R.id.tv_answer)
        tvAnswer.visibility=if (answerImages.isEmpty()) View.GONE else View.VISIBLE
        tvAnswer.setOnClickListener {
            ImageDialog(context, answerImages).builder()
        }

        val rv_list_score = dialog.findViewById<RecyclerView>(R.id.rv_list)

        when(correctMode){
            1,2->{
                rv_list_score.layoutManager = GridLayoutManager(context,3)
                TopicScoreAdapter(R.layout.item_topic_score,scoreMode,currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    rv_list_score.addItemDecoration(SpaceGridItemDeco(3,DP2PX.dip2px(context,15f)))
                }
            }
            3,4,5->{
                rv_list_score.layoutManager = LinearLayoutManager(context)
                TopicTwoScoreAdapter(if(correctMode==5)R.layout.item_topic_multi_score else R.layout.item_topic_two_score,scoreMode,currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(context,15f)))
                }
            }
            6,7->{
                rv_list_score.layoutManager = LinearLayoutManager(context)
                val sharedPool = RecyclerView.RecycledViewPool()
                rv_list_score.setRecycledViewPool(sharedPool)
                TopicMultistageScoreAdapter(R.layout.item_topic_two_score,scoreMode,currentScores).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(context,15f)))
                }
            }
            else->{
                rv_list_score.layoutManager = GridLayoutManager(context, 3)
                TopicResultStandardAdapter(R.layout.item_homework_result_standard_child,currentResults).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                }
            }
        }

        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0,-DP2PX.dip2px(context,300f))
        }

        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0, DP2PX.dip2px(context,300f))
        }

        return this
    }
}