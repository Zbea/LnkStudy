package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.mvp.model.Paper
import com.bll.lnkstudy.mvp.model.PaperContent
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_testpaper_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*


class PaperDrawingActivity:BaseActivity() {

    private var flags=0//0作业 1考卷
    private var mCourseId=0
    private var mCatalogId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<Paper>()
    private var paperContents= mutableListOf<PaperContent>()
    private var paper: Paper?=null

    private var currentPosition=0
    private var page = 0//页码
    private var pageCount=0

    private var isExpand=false

    private var elik_a: EinkPWInterface?=null
    private var elik_b: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_drawing
    }

    override fun initData() {
        flags=intent.flags
        isExpand= flags == 1
        mCourseId=intent.getIntExtra("courseId",0)
        mCatalogId=intent.getIntExtra("categoryId",0)
        pageCount=paperContents.size

        daoManager= PaperDaoManager.getInstance(this)
        daoContentManager= PaperContentDaoManager.getInstance(this)

        papers= daoManager?.queryAll(flags,mCourseId,mCatalogId) as MutableList<Paper>

        showLog(papers.size.toString())

        if (papers.size>0)
            currentPosition=papers.size-1

    }

    override fun initView() {

        if(papers.size>0){
            elik_a=v_content_a.pwInterFace
            elik_b=v_content_b.pwInterFace
            changeContent()
        }
        changeExpandView()
        bindClick()
    }


    //单屏、全屏内容切换
    private fun changeExpandView(){
        tv_page_a.visibility = View.VISIBLE
        tv_page_b.visibility = if (isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility=if (isExpand) View.VISIBLE else View.GONE
        v_content_b.visibility=if (isExpand) View.VISIBLE else View.GONE
        ll_content_b.visibility=if (isExpand) View.VISIBLE else View.GONE
    }

    private fun bindClick(){

        iv_expand.setOnClickListener {
            isExpand= !isExpand
            changeExpandView()
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
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var list= mutableListOf<ListBean>()
        for (item in papers){
            val listBean= ListBean()
            listBean.name=item.title
            listBean.page=item.page
            list.add(listBean)
        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                currentPosition = papers[position].index
                page=0
                changeContent()
            }
        })
    }


    //内容切换
    private fun changeContent(){

        paper=papers[currentPosition]

        paperContents= daoContentManager?.queryByID(paper?.id!!) as MutableList<PaperContent>

        pageCount=paperContents.size

        tv_title.text=paper?.title

        loadImage(page,elik_a!!,v_content_a)
        tv_page_a.text="${paperContents[page].page+1}"
        if (isExpand){
            if (page+1<pageCount){
                loadImage(page+1,elik_b!!,v_content_b)
                tv_page_b.text="${paperContents[page+1].page+1}"
            }
            else{
                unloadImage(v_content_b)
                tv_page_b.text=""
            }
        }

    }


    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view:ImageView) {

        var testPaperContent=paperContents[index]
        GlideUtils.setImageUrl(this,testPaperContent.path,view)

        elik?.setLoadFilePath(testPaperContent.drawPath,true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }

        })
    }

    //不显示不能手写
    private fun unloadImage(view:ImageView){
        view.setImageResource(0)
    }


}