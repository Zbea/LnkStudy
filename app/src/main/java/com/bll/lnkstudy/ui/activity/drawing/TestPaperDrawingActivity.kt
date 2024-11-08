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
    private var typeId=0//分组id
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
        typeId=intent.getIntExtra("typeId",0)
        currentPosition=intent.getIntExtra("page",DEFAULT_PAGE)

        daoManager= PaperDaoManager.getInstance()

        papers= daoManager?.queryAll(course,typeId) as MutableList<PaperBean>

    }

    override fun initView() {
        disMissView(iv_btn)
        if (papers.size>0){
            if (currentPosition==DEFAULT_PAGE)
                currentPosition=papers.size-1
            onContent()
        }
        else{
            setPWEnabled(false)
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
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (currentPosition!=papers[position].page){
                    currentPosition = papers[position].page
                    oldPosition=-1
                    page = 0
                    onContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
            }
        })
    }

    override fun onPageUp() {
        if (isExpand&&page>0){
            page-=2
            onContent()
        }
        else if (!isExpand&&page>0){
            page-=1
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
        if (isExpand&&page<pageCount-1){
            page+=2
            onContent()
        }
        else if(!isExpand&&page<pageCount-1){
            page+=1
            onContent()
        }
        else{
            //切换目录
            currentPosition+=1
            page=0
            onContent()
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        //展屏时，如果当前考卷内容为最后一张且这次目录内容不止1张，则页码前移一位
        if (isExpand){
            if (page==pageCount-1&&pageCount>1){
                page-=1
            }
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent(){
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]
        pageCount=paper?.paths!!.size

        if (currentPosition!=oldPosition){
            setScoreDetails(paper!!)
            page=0
        }
        oldPosition=currentPosition

        if (page<0){
            page=0
        }
        if (page>pageCount-1){
            page=pageCount-1
        }

        if (isExpand){
            setElikLoadPath(elik_a!!,page,v_content_a!!)
            if (page+1<pageCount){
                setElikLoadPath(elik_b!!,page+1,v_content_b!!)
                elik_b?.setPWEnabled(true)
            }
            else{
                //不显示 ，不能手写
                v_content_b?.setImageResource(0)
                elik_b?.setPWEnabled(false)
            }
            if (screenPos== Constants.SCREEN_LEFT){
                tv_page.text="${page+1}"
                tv_page_a.text=if (page+1<pageCount)"${page+1+1}" else ""
            }
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text="${page+1}"
                tv_page.text=if (page+1<pageCount)"${page+1+1}" else ""
            }
        }
        else{
            setElikLoadPath(elik_b!!,page,v_content_b!!)
            elik_b?.setPWEnabled(true)
            tv_page.text="${page+1}"
        }

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"
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