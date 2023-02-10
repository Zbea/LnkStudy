package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.PaperBean
import com.bll.lnkstudy.mvp.model.PaperContentBean
import com.bll.lnkstudy.mvp.model.PaperTypeBean
import com.bll.lnkstudy.mvp.model.ReceivePaper
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_main_receivepaper_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus

class MainReceivePaperDrawingActivity : BaseActivity(), View.OnClickListener {

    private var type=1//考卷
    private var course=""
    private var mCatalogId=0
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()

    private var receivePaper: ReceivePaper?=null
    private var outImageStr = ""
    private var paths = mutableListOf<String>()
    private var drawPaths = mutableListOf<String>()
    private var commitPaths = mutableListOf<String>()

    private var pageCount = 0
    private var page = 0 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_main_receivepaper_drawing
    }

    override fun initData() {
        isExpand=true

        receivePaper=intent.getBundleExtra("bundle")?.getSerializable("receivePaper") as ReceivePaper
        outImageStr = intent.getStringExtra("outImageStr").toString()
        paths = intent.getStringArrayListExtra("imagePaths")!!
        pageCount = paths.size

        course=receivePaper?.course!!
        mCatalogId=receivePaper?.categoryId!!

        daoManager= PaperDaoManager.getInstance()
        daoContentManager= PaperContentDaoManager.getInstance()

        //获取之前所有收到的考卷，用来排序
        papers= daoManager?.queryAll(type,course,mCatalogId) as MutableList<PaperBean>
        paperContents= daoContentManager?.queryAll(type,course,mCatalogId) as MutableList<PaperContentBean>

        for (i in 0 until paths.size){
            drawPaths.add("$outImageStr/${i+1}/draw.tch")
        }

    }

    override fun initView() {

        iv_btn.setOnClickListener(this)
        btn_page_up.setOnClickListener(this)
        btn_page_down.setOnClickListener(this)

        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace

        changeExpandView()

        tv_title_a.text=receivePaper?.title

        changeContent()
    }

    //单屏、全屏内容切换
    private fun changeExpandView(){
        showView(v_content_a,ll_page_content_a,v_empty)
        disMissView(iv_expand,iv_tool_left,iv_tool_right)
    }

    override fun onClick(view: View?) {

        if (view == iv_btn) {
            showLoading()
            Handler().postDelayed(Runnable {
                commit()
            },500)
        }

        if (view == btn_page_up) {
            if (isExpand){
                if (page>1){
                    page-=2
                }
            }
            else{
                if (page>0){
                    page-=1
                }
            }
            changeContent()
        }
        if (view == btn_page_down) {
            if (isExpand){
                if (page+2<pageCount){
                    page+=2
                }
            }
            else{
                if (page+1<pageCount){
                    page+=1

                }
            }
            changeContent()
        }

    }


    //提交
    private fun commit(){

        for (i in 0 until paths.size) {
            val index = i + 1 //当前名
            val path = paths[i] //当前原图路径
            val drawPath = "$outImageStr/$index/draw.png" //当前绘图路径
            val mergePath = "$outImageStr/$index/"//合并后的路径
            val mergePathStr = "$outImageStr/$index/merge.png"//合并后图片地址

            val oldBitmap = BitmapFactory.decodeFile(path)
            var drawBitmap = if (FileUtils.isExist(drawPath)) BitmapFactory.decodeFile(drawPath) else null
            if (drawBitmap != null) {
                val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge")
            } else {
                showToast("试卷未做完")
                BitmapUtils.saveBmpGallery(this, oldBitmap, mergePath, "merge")
            }
            commitPaths.add(mergePathStr)
        }

        //保存本次考试的 试卷分类
        val paperTypeBean= PaperTypeBean()
        paperTypeBean.course=course
        paperTypeBean.name=receivePaper?.category
        paperTypeBean.type=mCatalogId
        PaperTypeDaoManager.getInstance().insertOrReplace(paperTypeBean)

        //保存本次考试
        val paper= PaperBean()
        paper.contentId=receivePaper?.id!!
        paper.type=type
        paper.course=course
        paper.categoryId=mCatalogId
        paper.category=receivePaper?.category
        paper.title=receivePaper?.title
        paper.path=receivePaper?.path
        paper.page=paperContents.size
        paper.index=papers.size
        paper.createDate=receivePaper?.createDate!!
        paper.images=receivePaper?.images?.toString()
        daoManager?.insertOrReplace(paper)

        for (i in 0 until paths.size){
            //保存本次考试的试卷内容
            val paperContent= PaperContentBean()
            paperContent.type=type
            paperContent.course=course
            paperContent.categoryId=mCatalogId
            paperContent.contentId=paper?.contentId
            paperContent.path=paths[i]
            paperContent.drawPath=drawPaths[i]
            paperContent.date=receivePaper?.createDate!!
            paperContent.page=paperContents.size+i
            daoContentManager?.insertOrReplace(paperContent)
        }

        EventBus.getDefault().post(Constants.RECEIVE_PAPER_COMMIT_EVENT)
        hideLoading()
        finish()

    }

    //内容切换
    private fun changeContent(){

        loadImage(page,elik_a!!,v_content_a)
        tv_page_a.text="${page+1}/$pageCount"

        if (isExpand){
            if (page+1<pageCount){
                loadImage(page+1,elik_b!!,v_content_b)
                tv_page_b.text="${page+1+1}/$pageCount"
            }
            else{
                unloadImage(v_content_b)
                tv_page_b.text=""
            }
        }

    }

    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view: ImageView) {

        GlideUtils.setImageUrl(this,paths[index],view)

        elik.setLoadFilePath(drawPaths[index],true)
        elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
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
    private fun unloadImage(view: ImageView){
        view.setImageResource(0)
    }


    override fun onBackPressed() {
    }

    override fun onErasure() {
        elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
    }

}