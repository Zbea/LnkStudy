package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.mvp.model.HomeworkContent
import com.bll.lnkstudy.mvp.model.HomeworkMessage
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.mvp.model.ListBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_homework_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class HomeworkDrawingActivity : BaseActivity() {

    private var courseId = 0 //科目id
    private var homeworkTypeId = 0//作业分组id
    private var homeworkType: HomeworkType? = null

    private var homeworkContent: HomeworkContent? = null//当前作业内容
    private var homeworkContent_a: HomeworkContent? = null//a屏作业

    private var homeworks = mutableListOf<HomeworkContent>() //所有作业内容

    private var page = 0//页码
    private var messages= mutableListOf<HomeworkMessage>()
    private var drawingCommitDialog:DrawingCommitDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_homework_drawing
    }

    override fun initData() {
        var bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkType
        homeworkTypeId = homeworkType?.typeId!!
        courseId=homeworkType?.courseId!!

        homeworks = HomeworkContentDaoManager.getInstance(this).queryAllByType(courseId, homeworkTypeId)

        if (homeworks.size > 0) {
            homeworkContent = homeworks[homeworks.size - 1]
            page = homeworks.size - 1
        } else {
            newHomeWorkContent()
        }
        getMessageDatas()
    }

    private fun getMessageDatas(){
        val homeworkMessage= HomeworkMessage()
        homeworkMessage.id=0
        homeworkMessage.title="语文家庭作业1、3、5页"
        homeworkMessage.date=System.currentTimeMillis()
        homeworkMessage.course="语文"
        homeworkMessage.courseId=0
        homeworkMessage.state=0
        homeworkMessage.homeworkTypeId=0

        val homeworkMessage1= HomeworkMessage()
        homeworkMessage1.id=1
        homeworkMessage1.title="数学作业"
        homeworkMessage1.date=System.currentTimeMillis()
        homeworkMessage1.course="数学"
        homeworkMessage1.courseId=1
        homeworkMessage1.state=1
        homeworkMessage1.homeworkTypeId=2
        homeworkMessage1.isPg=true

        val homeworkMessage2= HomeworkMessage()
        homeworkMessage2.id=2
        homeworkMessage2.title="数学作业112"
        homeworkMessage2.date=System.currentTimeMillis()
        homeworkMessage2.course="数学"
        homeworkMessage2.courseId=1
        homeworkMessage2.state=1
        homeworkMessage2.homeworkTypeId=3
        homeworkMessage2.isPg=false

        messages.add(homeworkMessage)
        messages.add(homeworkMessage1)
        messages.add(homeworkMessage2)

    }

    override fun initView() {

        v_content_a.setImageResource(ToolUtils.getImageResId(this,homeworkType?.contentResId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this,homeworkType?.contentResId))//设置背景
        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace

        changeContent()

        tv_title_a.setOnClickListener {
            if (homeworkContent_a!=null&&homeworkContent_a?.state==0){
                var title=tv_title_a.text.toString()
                InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                    tv_title_a.text = string
                    homeworkContent_a?.title = string
                    homeworks[page-1].title = string
                    HomeworkContentDaoManager.getInstance(this).insertOrReplace(homeworkContent_a)
                }
            }
        }

        tv_title_b.setOnClickListener {
            if (homeworkContent?.state==0){
                var title=tv_title_b.text.toString()
                InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener { string ->
                    tv_title_b.text = string
                    homeworkContent?.title = string
                    homeworks[page].title = string
                    HomeworkContentDaoManager.getInstance(this).insertOrReplace(homeworkContent)
                }
            }
        }

        btn_page_down.setOnClickListener {
            val total=homeworks.size-1
            if(isExpand){
                when(page){
                    total->{
                        newHomeWorkContent()
                        newHomeWorkContent()
                        page==total
                    }
                    total-1->{
                        newHomeWorkContent()
                        page==total
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
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var titleStr=""
        var list= mutableListOf<ListBean>()
        for (item in homeworks){
            val listBean= ListBean()
            listBean.name=item.title
            listBean.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(listBean)
            }

        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            page = homeworks[position].page
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
        val pageTotal = homeworks.size

        updateImage(elik_b!!, homeworkContent?.filePath!!)
        tv_page_b.text = (page + 1).toString()

        if (isExpand) {
            if (homeworkContent_a != null) {
                updateImage(elik_a!!, homeworkContent_a?.filePath!!)
                tv_page_a.text = "$page"
            }
        }

    }

    //保存绘图以及更新手绘
    private fun updateImage(elik: EinkPWInterface, path: String) {
        elik?.setPWEnabled(true)
        elik?.setLoadFilePath(path, true)
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


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path=FileAddress().getPathHomework(courseId,homeworkType?.typeId,homeworks.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        homeworkContent = HomeworkContent()
        homeworkContent?.courseId = courseId
        homeworkContent?.date = System.currentTimeMillis()
        homeworkContent?.homeworkTypeId = homeworkType?.typeId
        homeworkContent?.bgResId = homeworkType?.bgResId

        homeworkContent?.title="未命名${homeworks.size+1}"
        homeworkContent?.folderPath=path
        homeworkContent?.filePath = "$path/$pathName.tch"
        homeworkContent?.pathName=pathName
        homeworkContent?.page = homeworks.size
        homeworkContent?.state=0

        page = homeworks.size

        HomeworkContentDaoManager.getInstance(this).insertOrReplace(homeworkContent)
        val id=HomeworkContentDaoManager.getInstance(this).insertId
        homeworkContent?.id=id

        homeworks.add(homeworkContent!!)
    }


    //作业提交
    private fun commit() {
        if (drawingCommitDialog==null){
            drawingCommitDialog= DrawingCommitDialog(this,getCurrentScreenPos(),messages).builder()
            drawingCommitDialog?.setOnDialogClickListener {
                if (it.contents.size>0){
                    for (i in it.contents)
                    {
                        val pos=i-1
                        if (pos<homeworks.size){
                            val item=homeworks[pos]
                            item.title=it.title
                            item.state=1
                            HomeworkContentDaoManager.getInstance(this).insertOrReplace(item)
                        }
                    }
                    changeContent()

                }
            }
        }
        else{
            drawingCommitDialog?.show()
        }
    }

    //删除当前作业内容
    private fun delete() {
        CommonDialog(this,getCurrentScreenPos()).setContent("确认删除作业内容？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                deleteContent()
            }
        })
    }

    //删除作业
    private fun deleteContent() {

        HomeworkContentDaoManager.getInstance(this).deleteBean(homeworkContent)
        homeworks.remove(homeworkContent)
        FileUtils.deleteFile(homeworkContent?.folderPath, homeworkContent?.pathName)//删除文件



    }

   override fun changeScreenPage() {
        if (isExpand){
            changeExpandContent()
        }
    }

    override fun onErasure() {
        if (isExpand){
            elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
        else{
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
    }

}