package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.EinkPWInterface
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.paper.PaperBean
import com.bll.lnkstudy.mvp.model.paper.PaperContentBean
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 考卷考试页面
 */
class PaperExamDrawingActivity : BaseDrawingActivity(), View.OnClickListener
    ,IContractView.IFileUploadView{

    private val mUploadPresenter=FileUploadPresenter(this)
    private var type=1//1考卷0作业
    private var course=""
    private var commonTypeId=0
    private var daoManager: PaperDaoManager?=null
    private var daoContentManager: PaperContentDaoManager?=null
    private var papers= mutableListOf<PaperBean>()
    private var paperContents= mutableListOf<PaperContentBean>()

    private var exam: PaperList.PaperListBean?=null
    private var outImageStr = ""
    private var paths = mutableListOf<String>()
    private var drawPaths = mutableListOf<String>()
    private var commitPaths = mutableListOf<String>()

    private var pageCount = 0
    private var page = 0 //当前页码

    override fun onSuccess(urls: MutableList<String>?) {
        val map= HashMap<String, Any>()
        map["studentTaskId"]=exam?.id!!
        map["studentUrl"]=ToolUtils.getImagesStr(urls)
        mUploadPresenter.commit(map)
    }
    override fun onCommitSuccess() {
        savePaper()
        EventBus.getDefault().post(Constants.RECEIVE_PAPER_COMMIT_EVENT)
        finish()
    }


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        isExpand=true

        exam=intent.getBundleExtra("bundle")?.getSerializable("receivePaper") as PaperList.PaperListBean
        outImageStr = intent.getStringExtra("outImageStr").toString()
        paths = intent.getStringArrayListExtra("imagePaths")!!
        pageCount = paths.size

        course=exam?.subject!!
        commonTypeId=exam?.commonTypeId!!

        daoManager= PaperDaoManager.getInstance()
        daoContentManager= PaperContentDaoManager.getInstance()

        //获取之前所有收到的考卷，用来排序
        papers= daoManager?.queryAll(type,course,commonTypeId) as MutableList<PaperBean>
        paperContents= daoContentManager?.queryAll(type,course,commonTypeId) as MutableList<PaperContentBean>

        for (i in 0 until paths.size){
            drawPaths.add("$outImageStr/${i+1}/draw.tch")
        }

    }

    override fun initView() {
        setDrawingTitleClick(false)
        showView(iv_geometry)
        setViewElikUnable(iv_geometry)

        iv_btn.setOnClickListener(this)
        btn_page_up.setOnClickListener(this)
        btn_page_down.setOnClickListener(this)

        changeExpandView()

        tv_title_a.text=exam?.title

        changeContent()
    }

    //单屏、全屏内容切换
    private fun changeExpandView(){
        showView(v_content_a,ll_page_content_a,v_empty)
        disMissView(iv_expand,iv_tool_left,iv_tool_right)
    }

    override fun onClick(view: View?) {
        if (view == iv_btn) {
            CommonDialog(this).setContent(R.string.toast_commit_ok).setDialogClickListener(
                object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        showLoading()
                        commit()
                    }
                })
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
        //提交失败后，已经合图之后避免重复合图
        if (commitPaths.size!=paths.size){
            for (i in 0 until paths.size) {
                val path = paths[i] //当前原图路径
                val drawPath = drawPaths[i].replace("tch","png") //当前绘图路径

                val oldBitmap = BitmapFactory.decodeFile(path)
                val drawBitmap = BitmapFactory.decodeFile(drawPath)
                if (drawBitmap != null) {
                    val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                    BitmapUtils.saveBmpGallery(this, mergeBitmap, path)
                }
                commitPaths.add(path)
            }
        }
        Handler().postDelayed({
            mUploadPresenter.upload(commitPaths)
        },500)
    }

    /**
     *  提交完成后，将考卷保存在本地试卷里面
     */
    private fun savePaper(){
        //保存本次考试
        val paper= PaperBean().apply {
            contentId=exam?.id!!
            type=this@PaperExamDrawingActivity.type
            course=this@PaperExamDrawingActivity.course
            categoryId=this@PaperExamDrawingActivity.commonTypeId
            category=exam?.examName
            title=exam?.title
            path=exam?.path
            page=paperContents.size
            index=papers.size
            state=1
            createDate=exam?.date!!*1000
            images= exam?.imageUrl?.split(",")?.toTypedArray().toString()
        }
        daoManager?.insertOrReplace(paper)

        for (i in paths.indices){
            //合图完毕之后删除 手写
            FileUtils.deleteFile(File("$outImageStr/${i+1}/"))

            //保存本次考试的试卷内容
            val paperContent= PaperContentBean()
                .apply {
                    type=this@PaperExamDrawingActivity.type
                    course=this@PaperExamDrawingActivity.course
                    categoryId=this@PaperExamDrawingActivity.commonTypeId
                    contentId=paper.contentId
                    path=paths[i]
                    drawPath=drawPaths[i]
                    date=exam?.date!!*1000
                    page=paperContents.size+i
                }
            daoContentManager?.insertOrReplace(paperContent)
        }
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
                elik_b?.setPWEnabled(false)
                v_content_b.setImageResource(0)
                tv_page_b.text=""
            }
        }

    }

    //加载图片
    private fun loadImage(index: Int,elik:EinkPWInterface,view: ImageView) {
        elik.setPWEnabled(true)

        GlideUtils.setImageUrl(this,paths[index],view)

        elik.setLoadFilePath(drawPaths[index],true)
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

    override fun onBackPressed() {
    }



}