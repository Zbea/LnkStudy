package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File


/**
 * 考卷（考完提交后保存的考卷）
 */
class PaperDrawingActivity: BaseDrawingActivity(){

    private var course=""
    private var typeId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()
    private var paper: PaperBean?=null

    private var currentPosition=0
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
        setDrawingTitleClick(false)
        setPWEnabled(false)
        if (papers.size>0){
            if (currentPosition==DEFAULT_PAGE)
                currentPosition=papers.size-1
            changeContent()
        }
        changeExpandView()

        iv_expand_left.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_right.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            onChangeExpandContent()
        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }
    }

    override fun onPageUp() {
        if (isExpand&&page>1){
            page-=2
            changeContent()
        }
        else if (!isExpand&&page>0){
            page-=1
            changeContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                changeContent()
            }
        }
    }

    override fun onPageDown() {
        if (isExpand&&page+2<paperContentCount){
            page+=2
            changeContent()
        }
        else if(!isExpand&&page+1<paperContentCount){
            page+=1
            changeContent()
        }
        else{
            //切换目录
            currentPosition+=1
            page=0
            changeContent()
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
        changeExpandView()
        changeContent()
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            list.add(itemList)
        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            currentPosition = papers[position].index
            page = 0
            changeContent()
        }
    }

    /**
     * 设置是否可以手写
     */
    private fun setPWEnabled(boolean: Boolean){
        elik_a?.setPWEnabled(boolean)
        elik_b?.setPWEnabled(boolean)
    }


    //内容切换
    private fun changeContent(){
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]

        paperContents= daoContentManager?.queryByID(paper?.contentId!!) as MutableList<PaperContentBean>
        paperContentCount=paperContents.size

        tv_title_a.text=paper?.title
        tv_title_b.text=paper?.title

        if (isExpand){
            loadImage(page,elik_a!!,v_content_a)
            tv_page_a.text="${paperContents[page].page+1}"

            if (page+1<paperContentCount){
                loadImage(page+1,elik_b!!,v_content_b)
                tv_page_b.text="${paperContents[page+1].page+1}"
            }
            else{
                //不显示 ，不能手写
                v_content_b.setImageResource(0)
                tv_page_b.text=""
            }
        }
        else{
            loadImage(page,elik_b!!,v_content_b)
            tv_page_b.text="${paperContents[page].page+1}"
        }
    }


    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view:ImageView) {
        val testPaperContent=paperContents[index]
        GlideUtils.setImageFileNoCache(this,File(testPaperContent.path),view)

        elik.setLoadFilePath(testPaperContent.drawPath,true)
        elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik.saveBitmap(true) {}
            }
        })
    }

    override fun changeScreenPage() {
        if (isExpand){
            onChangeExpandContent()
        }
    }

}