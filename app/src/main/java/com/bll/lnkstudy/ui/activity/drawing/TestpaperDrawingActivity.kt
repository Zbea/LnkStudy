package com.bll.lnkstudy.ui.activity.drawing

import android.widget.ImageView
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_correct_score.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import java.io.File


/**
 * 考卷（考完提交后保存的考卷）
 */
class TestpaperDrawingActivity: BaseDrawingActivity(){

    private var course=""
    private var typeId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()
    private var paper: PaperBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var page = 0//页码
    private var paperContentCount=0//这次考卷所有内容大小

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        course=intent.getStringExtra("course").toString()
        typeId=intent.getIntExtra("typeId",0)
        currentPosition=intent.getIntExtra("page",DEFAULT_PAGE)

        daoManager= PaperDaoManager.getInstance()
        daoContentManager= PaperContentDaoManager.getInstance()

        papers= daoManager?.queryAll(course,typeId) as MutableList<PaperBean>

    }

    override fun initView() {
        disMissView(iv_draft,iv_btn)
        setPWEnabled(false)
        if (papers.size>0){
            if (currentPosition==DEFAULT_PAGE)
                currentPosition=papers.size-1
            onChangeContent()
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
        DrawingCatalogDialog(this, getCurrentScreenPos(),list).builder().setOnDialogClickListener { position ->
            if (currentPosition!=position){
                currentPosition = papers[position].page
                page = 0
                onChangeContent()
            }
        }
    }

    override fun onPageUp() {
        if (isExpand&&page>1){
            page-=2
            onChangeContent()
        }
        else if (!isExpand&&page>0){
            page-=1
            onChangeContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                onChangeContent()
            }
        }
    }

    override fun onPageDown() {
        if (isExpand&&page+2<paperContentCount){
            page+=2
            onChangeContent()
        }
        else if(!isExpand&&page+1<paperContentCount){
            page+=1
            onChangeContent()
        }
        else{
            //切换目录
            currentPosition+=1
            page=0
            onChangeContent()
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        //展屏时，如果当前考卷内容为最后一张且这次目录内容不止1张，则页码前移一位
        if (isExpand){
            if (page==paperContentCount-1&&paperContentCount>1){
                page-=1
            }
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    private fun onChangeContent(){
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]

        paperContents= daoContentManager?.queryByID(paper?.contentId!!) as MutableList<PaperContentBean>
        paperContentCount=paperContents.size

        if (currentPosition!=oldPosition){
            setScoreDetails(paper!!)
            page=0
        }

        oldPosition=currentPosition

        tv_page_total.text="$paperContentCount"
        tv_page_total_a.text="$paperContentCount"

        if (isExpand){
            setElikLoadPath(page,v_content_a)
            tv_page_a.text="${page+1}"
            if (page+1<paperContentCount){
                setElikLoadPath(page+1,v_content_b)
                tv_page.text="${page+1+1}"
            }
            else{
                //不显示 ，不能手写
                v_content_b.setImageResource(0)
                tv_page.text=""
            }
        }
        else{
            setElikLoadPath(page,v_content_b)
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
        if (item.correctJson?.isNotEmpty() == true&&correctMode>0){
            setScoreListDetails(item.correctJson)
        }
        else{
            disMissView(rv_list_multi,rv_list_score)
        }
    }

    //加载图片
    private fun setElikLoadPath(index: Int, view:ImageView) {
        val testPaperContent=paperContents[index]
        GlideUtils.setImageFile(this,File(testPaperContent.path),view)
//        elik.setLoadFilePath(testPaperContent.drawPath,true)
    }


}