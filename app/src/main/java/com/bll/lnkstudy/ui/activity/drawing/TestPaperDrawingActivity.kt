package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.common_correct_score.rv_list_multi
import kotlinx.android.synthetic.main.common_correct_score.rv_list_score
import kotlinx.android.synthetic.main.common_correct_score.tv_answer
import kotlinx.android.synthetic.main.common_correct_score.tv_correct_title
import kotlinx.android.synthetic.main.common_correct_score.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total


/**
 * 考卷（考完提交后保存的考卷）
 */
class TestPaperDrawingActivity: BaseDrawingActivity(){

    private var course=""
    private var paperTypeId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paper: PaperBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var page = 0//页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        course=intent.getStringExtra("course").toString()
        paperTypeId=intent.getIntExtra("typeId",0)
        currentPosition=intent.getIntExtra("page",DEFAULT_PAGE)

        daoManager= PaperDaoManager.getInstance()

        papers= daoManager?.queryAll(course,paperTypeId) as MutableList<PaperBean>

    }

    override fun initView() {
        disMissView(iv_btn)
        if (papers.size>0){
            if (currentPosition==DEFAULT_PAGE)
                currentPosition=papers.size-1
            onContent()
        }
        else{
            setDisableTouchInput(true)
        }
    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,false).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (currentPosition!=pageNumber){
                    currentPosition = pageNumber
                    oldPosition=-1
                    page = 0
                    onContent()
                }
            }
        })
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

    override fun onContent(){
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]
        pageCount=paper?.paths!!.size

        if (isExpand&&pageCount==1){
            onChangeExpandContent()
            return
        }

        if (isExpand&&page>pageCount-2)
            page=pageCount-2
        if (page<0)
            page=0

        if (currentPosition!=oldPosition){
            setScoreDetails(paper!!)
            page=0
        }
        oldPosition=currentPosition

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        if (isExpand){
            setElikLoadPath(elik_a!!,page,v_content_a!!)
            setElikLoadPath(elik_b!!,page+1,v_content_b!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text="${page+1}"
                tv_page.text="${page+1+1}"
            }
            else{
                tv_page.text="${page+1}"
                tv_page_a.text="${page+1+1}"
            }
        }
        else{
            setElikLoadPath(elik_b!!,page,v_content_b!!)
            tv_page.text="${page+1}"
        }
    }

    /**
     * 设置批改详情
     */
    private fun setScoreDetails(item: PaperBean){
        showView(iv_score)
        correctMode=item.correctMode
        tv_correct_title.text=item.title
        tv_total_score.text=item.score
        scoreMode=item.scoreMode
        if (item.answerUrl.isNullOrEmpty()){
            disMissView(tv_answer)
        }
        else{
            answerImages= item.answerUrl?.split(",") as MutableList<String>
            showView(tv_answer)
        }
        if (item.correctJson?.isNotEmpty() == true&&correctMode>0){
            setScoreListDetails(item.correctJson)
        }
        else{
            disMissView(rv_list_multi,rv_list_score)
        }
    }

    //加载图片
    private fun setElikLoadPath( elik: EinkPWInterface ,index: Int, view:ImageView) {
        GlideUtils.setImageCacheUrl(this,paper!!.paths[index],view)
        elik.setLoadFilePath(paper!!.drawPaths[index],true)
    }


}