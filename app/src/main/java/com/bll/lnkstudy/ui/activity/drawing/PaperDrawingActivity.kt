package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.PaperBean
import com.bll.lnkstudy.mvp.model.PaperContentBean
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*


class PaperDrawingActivity: BaseActivity() {

    private var flags=0//0作业 1考卷
    private var course=""
    private var mCatalogId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()
    private var paper: PaperBean?=null

    private var currentPosition=0
    private var page = 0//页码
    private var pageCount=0

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        flags=intent.flags
        isExpand= flags == 1
        course=intent.getStringExtra("course").toString()
        mCatalogId=intent.getIntExtra("categoryId",0)
        pageCount=paperContents.size

        daoManager= PaperDaoManager.getInstance()
        daoContentManager= PaperContentDaoManager.getInstance()

        papers= daoManager?.queryAll(flags,course,mCatalogId) as MutableList<PaperBean>

    }

    override fun initView() {

        if(papers.size>0){
            currentPosition=papers.size-1
            changeContent()
        }

        changeExpandView()
        bindClick()
    }

    private fun bindClick(){

        iv_expand.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            changeExpandContent()
        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        btn_page_up.setOnClickListener {

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

        btn_page_down.setOnClickListener {

            if (isExpand&&page+2<pageCount){
                page+=2
                changeContent()
            }
            else if (!isExpand&&page+1<pageCount){
                page+=1
                changeContent()
            }
            else{
                if (currentPosition+1<papers.size){
                    currentPosition+=1
                    page=0
                    changeContent()
                }
            }

        }

        iv_btn.setOnClickListener {

        }
    }

    /**
     * 切换屏幕
     */
    private fun changeExpandContent(){
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    //单屏、全屏内容切换
    private fun changeExpandView(){
        showView(ll_page_content_a,v_content_a)
        iv_expand.visibility=if(isExpand) View.GONE else View.VISIBLE
        v_content_b.visibility=if(isExpand) View.VISIBLE else View.GONE
        ll_page_content_b.visibility = if(isExpand) View.VISIBLE else View.GONE
        v_empty.visibility=if(isExpand) View.VISIBLE else View.GONE
        if (flags==0&&isExpand){
            if (screenPos==1){
                showView(iv_expand_a)
                disMissView(iv_expand_b)
            }
            else{
                showView(iv_expand_b)
                disMissView(iv_expand_a)
            }
        }
        else{
            disMissView(iv_expand_a,iv_expand_b)
        }
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var list= mutableListOf<ItemList>()
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

        pageCount=paperContents.size

        tv_title_a.text=paper?.title
        tv_title_b.text=paper?.title
        setPWEnabled(!paper!!.isPg)

        loadImage(page,elik_a!!,v_content_a)
        tv_page_a.text="${paperContents[page].page+1}"
        if (isExpand){
            if (page+1<pageCount){
                loadImage(page+1,elik_b!!,v_content_b)
                tv_page_b.text="${paperContents[page+1].page+1}"
            }
            else{
                //不显示 ，不能手写
                v_content_b.setImageResource(0)
                elik_b?.setPWEnabled(false)
                tv_page_b.text=""
            }
        }

    }


    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view:ImageView) {
        elik.setPWEnabled(true)

        val testPaperContent=paperContents[index]
        GlideUtils.setImageNoCacheUrl(this,testPaperContent.path,view)

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
        if (flags==0&&isExpand){
            changeExpandContent()
        }
    }

    override fun onErasure() {
        if (isExpand){
            elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
        else{
            elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
    }

}