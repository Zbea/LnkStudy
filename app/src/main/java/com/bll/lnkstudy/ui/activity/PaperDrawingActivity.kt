package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.Paper
import com.bll.lnkstudy.mvp.model.PaperContent
import com.bll.lnkstudy.ui.adapter.PaperCatalogAdapter
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_testpaper_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*


class PaperDrawingActivity:BaseActivity() {
    private var mUserId=0
    private var type=0//0作业 1考卷
    private var mCourseId=0
    private var mCatalogId=0//分组id
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<Paper>()
    private var paperContents= mutableListOf<PaperContent>()
    private var paper: Paper?=null

    private var mAdapter: PaperCatalogAdapter? = null

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
        type=intent.flags
        isExpand= type == 1
        mCourseId=intent.getIntExtra("courseId",0)
        mCatalogId=intent.getIntExtra("categoryId",0)
        mUserId=mUser?.id!!
        pageCount=paperContents.size

        daoManager= PaperDaoManager.getInstance(this)
        daoContentManager= PaperContentDaoManager.getInstance(this)

        papers= daoManager?.queryAll(mUserId,type,mCourseId,mCatalogId) as MutableList<Paper>
        if (papers.size>0)
            currentPosition=papers.size-1

    }

    override fun initView() {

        elik_a=v_content_a.pwInterFace
        elik_b=v_content_b.pwInterFace

        changeExpandView()

        if(papers.size>0)
            changeContent()

        initRecyclerCatalog()

        bindClick()
    }

    //目录列表
    private fun initRecyclerCatalog() {
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = PaperCatalogAdapter(R.layout.item_catalog_parent, papers)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            currentPosition = papers[position].index
            page=0
            changeContent()

            disMissView(ll_catalog)
            setPWEnabled(true)
        }
    }

    //单屏、全屏内容切换
    private fun changeExpandView(){
        tv_page_a.visibility = View.VISIBLE
        tv_page_b.visibility = if (isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility=if (isExpand) View.VISIBLE else View.GONE
        ll_content_b.visibility=if (isExpand) View.VISIBLE else View.GONE
    }

    private fun bindClick(){

        iv_expand.setOnClickListener {
            isExpand= !isExpand
            changeExpandView()
        }

        iv_catalog.setOnClickListener {
            if (ll_catalog.visibility== View.GONE){
                showView(ll_catalog)
                setPWEnabled(false)
            }
            else{
                disMissView(ll_catalog)
                setPWEnabled(true)
            }
        }


        btn_page_up.setOnClickListener {
            disMissView(ll_catalog)
            setPWEnabled(true)

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
            disMissView(ll_catalog)
            setPWEnabled(true)

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

    //设置手绘是否可以绘制
    private fun setPWEnabled(boolean: Boolean){
        elik_a?.setPWEnabled(boolean)
        elik_b?.setPWEnabled(boolean)
    }


    //内容切换
    private fun changeContent(){

        paper=papers[currentPosition]

        paperContents= daoContentManager?.queryByID(mUserId,paper?.id!!) as MutableList<PaperContent>

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