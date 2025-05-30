package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.homework.ResultStandardItem
import com.bll.lnkstudy.ui.adapter.HomeworkResultStandardAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultStandardDetailsDialog(val context: Context, private val title:String, private val score:Double,private val question:String,private val items:MutableList<ResultStandardItem>) {

    fun builder(): ResultStandardDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.common_correct_result_standard)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 700f)) / 2
        dialog.show()

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val tvTitle=dialog.findViewById<TextView>(R.id.tv_title)
        tvTitle.text=title

        val tvScore=dialog.findViewById<TextView>(R.id.tv_score)
        tvScore.text=getResultStandardStr(score)

        if (question.isNotEmpty()){
            if (question.length<20){
                val types= Gson().fromJson(question, object : TypeToken<MutableList<Int>>() {}.type) as MutableList<Int>
                if (types.size>0){
                    for (ite in items){
                        val i=items.indexOf(ite)
                        val type=types[i]
                        ite.list[type-1].isCheck=true
                    }
                }
            }
        }

        val recyclerview = dialog.findViewById<RecyclerView>(R.id.rv_list)
        recyclerview.layoutManager = LinearLayoutManager(context)//创建布局管理
        HomeworkResultStandardAdapter(R.layout.item_homework_result_standard, items).apply {
            recyclerview.adapter = this
            bindToRecyclerView(recyclerview)
            recyclerview.addItemDecoration(SpaceItemDeco(30))
        }

        return this
    }

    /**
     * 返回标准评分
     * @param score
     * @return
     */
    private fun getResultStandardStr(score: Double): String {
        return if (score == 1.0) {
            "A"
        } else if (score == 2.0) {
            "B"
        } else {
            "C"
        }
    }
}