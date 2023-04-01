package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.View
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommit
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class HomeworkDrawingActivity : BaseDrawingActivity(),IContractView.IFileUploadView {

    private val mUploadPresenter= FileUploadPresenter(this)
    private var course = ""//科目
    private var homeworkTypeId = 0//作业分组id
    private var homeworkType: HomeworkTypeBean? = null

    private var homeworkContent: HomeworkContentBean? = null//当前作业内容
    private var homeworkContent_a: HomeworkContentBean? = null//a屏作业

    private var homeworks = mutableListOf<HomeworkContentBean>() //所有作业内容

    private var page = 0//页码
    private var messages= mutableListOf<HomeworkMessage.MessageBean>()
    private var drawingCommitDialog:DrawingCommitDialog?=null
    private var homeworkCommit:HomeworkCommit?=null

    override fun onSuccess(urls: MutableList<String>?) {
        val map= HashMap<String, Any>()
        map["studentTaskId"]=homeworkCommit?.messageId!!
        map["studentUrl"]=ToolUtils.getImagesStr(urls)
        mUploadPresenter.commit(map)
    }
    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        for (i in homeworkCommit?.contents!!){
            val homework=homeworks[i-1]
            homework.state=1
            homework.contentId=homeworkCommit?.messageId!!
            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
        }
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkTypeBean
        homeworkTypeId = homeworkType?.typeId!!
        course=homeworkType?.course!!
        val list=homeworkType?.message?.list
        if (!list.isNullOrEmpty()){
            for (item in list){
                if (item.endTime>0&&item.status==3){
                    messages.add(item)
                }
            }
        }

        homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(course, homeworkTypeId)

        if (homeworks.size > 0) {
            homeworkContent = homeworks[homeworks.size - 1]
            page = homeworks.size - 1
        } else {
            newHomeWorkContent()
        }

    }

    override fun initView() {
        changeContent()

        btn_page_down.setOnClickListener {
            val total=homeworks.size-1
            if(isExpand){
                when(page){
                    total->{
                        newHomeWorkContent()
                        newHomeWorkContent()
                        page=total
                    }
                    total-1->{
                        newHomeWorkContent()
                        page=total
                    }
                    else->{
                        page+=2
                    }
                }
            }
            else{
                when(page){
                    total->{
                        newHomeWorkContent()
                    }
                    else->{
                        page += 1
                    }
                }
            }
            changeContent()
        }

        btn_page_up.setOnClickListener {
            if(isExpand){
                if (page>2){
                    page-=2
                    changeContent()
                }
                else if (page==2){//当页面不够翻两页时
                    page=1
                    changeContent()
                }
            }else{
                if (page>0){
                    page-=1
                    changeContent()
                }
            }

        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand.setOnClickListener {
            if (homeworks.size==1){
                newHomeWorkContent()
            }
            changeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            changeExpandContent()
        }

        iv_btn.setOnClickListener {
            commit()
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

    private fun changeExpandView(){
        iv_expand.visibility=if (isExpand) View.GONE else View.VISIBLE
        v_content_a.visibility = if(isExpand) View.VISIBLE else View.GONE
        ll_page_content_a.visibility = if(isExpand) View.VISIBLE else View.GONE
        v_empty.visibility=if(isExpand) View.VISIBLE else View.GONE
        if (isExpand){
            if (screenPos==1){
                showView(iv_expand_a)
                disMissView(iv_expand_b)
            }
            else{
                showView(iv_expand_b)
                disMissView(iv_expand_a)
            }
        }
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var titleStr=""
        val list= mutableListOf<ItemList>()
        for (item in homeworks){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(itemList)
            }

        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            page = list[position].page
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent() {

        homeworkContent = homeworks[page]

        if (isExpand) {
            if (page > 0) {
                homeworkContent_a = homeworks[page - 1]
            }
            if (page==0){
                page=1
                homeworkContent = homeworks[page]
                homeworkContent_a = homeworks[page-1]
            }
        } else {
            homeworkContent_a = null
        }


        tv_title_b.text=homeworkContent?.title
        if (isExpand){
            tv_title_a.text=homeworkContent_a?.title
        }

        updateUI()
    }

    //更新绘图以及页码
    private fun updateUI() {
        //已提交后不能手写，显示合图后的图片
        elik_b?.setPWEnabled(homeworkContent?.state==0)
        if (homeworkContent?.state==0){
            updateImage(elik_b!!, homeworkContent?.filePath!!)
            v_content_b.setImageResource(ToolUtils.getImageResId(this,homeworkType?.contentResId))//设置背景
        }
        else{
            GlideUtils.setImageFile(this,File(homeworkContent?.filePath),v_content_b)
        }
        tv_page_b.text = (page + 1).toString()

        if (isExpand) {
            if (homeworkContent_a != null) {
                elik_a?.setPWEnabled(homeworkContent_a?.state==0)
                if (homeworkContent_a?.state==0){
                    updateImage(elik_a!!, homeworkContent_a?.filePath!!)
                    v_content_a.setImageResource(ToolUtils.getImageResId(this,homeworkType?.contentResId))//设置背景
                }
                else{
                    GlideUtils.setImageFileNoCache(this,File(homeworkContent_a?.filePath),v_content_a)
                }
                tv_page_a.text = "$page"
            }
        }

    }

    //保存绘图以及更新手绘
    private fun updateImage(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path.replace("png","tch"), true)
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


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path=FileAddress().getPathHomework(course,homeworkType?.typeId,homeworks.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        homeworkContent =
            HomeworkContentBean()
        homeworkContent?.course = course
        homeworkContent?.date = System.currentTimeMillis()
        homeworkContent?.homeworkTypeId = homeworkType?.typeId
        homeworkContent?.bgResId = homeworkType?.bgResId

        homeworkContent?.title=getString(R.string.unnamed)+(homeworks.size+1)
        homeworkContent?.folderPath=path
        homeworkContent?.filePath = "$path/$pathName.png"
        homeworkContent?.pathName=pathName
        homeworkContent?.page = homeworks.size
        homeworkContent?.state=0

        page = homeworks.size

        HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent)
        val id=HomeworkContentDaoManager.getInstance().insertId
        homeworkContent?.id=id

        homeworks.add(homeworkContent!!)
    }


    //作业提交
    private fun commit() {
        if (messages.size==0|| homeworkContent?.state!=0)return
        if (drawingCommitDialog==null){
            drawingCommitDialog= DrawingCommitDialog(this,getCurrentScreenPos(),messages).builder()
            drawingCommitDialog?.setOnDialogClickListener {
                homeworkCommit=it
                val paths= mutableListOf<String>()
                for (i in it.contents){
                    val homework=homeworks[i-1]
                    paths.add(saveImage(homework))
                }
                mUploadPresenter.upload(paths)
            }
        }
        else{
            drawingCommitDialog?.show()
        }
    }

    /**
     * 合图
     */
    private fun saveImage(homework: HomeworkContentBean):String{
        val resId=ToolUtils.getImageResId(this,homeworkType?.contentResId)
        val oldBitmap=BitmapFactory.decodeResource(resources,resId)

        val drawPath = homework.filePath
        val drawBitmap = BitmapFactory.decodeFile(drawPath)
        if (drawBitmap != null) {
            val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
            BitmapUtils.saveBmpGallery(this, mergeBitmap, drawPath)
        }
        else{
            BitmapUtils.saveBmpGallery(this, oldBitmap, drawPath)
        }
        FileUtils.deleteFile(File(drawPath.replace("png","tch")))
        return drawPath
    }

   override fun changeScreenPage() {
        if (isExpand){
            changeExpandContent()
        }
    }

    override fun setDrawingTitle_a(title:String) {
        homeworkContent_a?.title = title
        homeworks[page-1].title = title
        HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent_a)
    }

    override fun setDrawingTitle_b(title:String) {
        homeworkContent?.title = title
        homeworks[page].title = title
        HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent)
    }




}