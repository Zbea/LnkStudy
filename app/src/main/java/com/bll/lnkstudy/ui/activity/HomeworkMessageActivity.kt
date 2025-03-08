package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.ui.adapter.HomeworkMessageAdapter
import com.bll.lnkstudy.utils.DP2PX
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
        val list=if(homeworkType?.createStatus==2) homeworkType?.messages!! else homeworkType?.parents!!
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@HomeworkMessageActivity,50f),
            DP2PX.dip2px(this@HomeworkMessageActivity,30f),
            DP2PX.dip2px(this@HomeworkMessageActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkMessageAdapter(R.layout.item_homework_message_all,list,homeworkType?.createStatus!!).apply {
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
                }

            }
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.HOMEWORK_MESSAGE_COMMIT_EVENT) {
            mAdapter?.remove(messageIndex)
            setResult(Constants.RESULT_10002, Intent().setFlags(messageIndex))
        }
    }

}