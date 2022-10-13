package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.View
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_homework_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class HomeworkDrawingActivity : BaseActivity() {

    private var elik_a: EinkPWInterface? = null
    private var elik_b: EinkPWInterface? = null

    private var isExpand = false //是否是全屏

    private var courseId = 0 //科目id
    private var homeworkTypeId = 0//作业分组id
    private var homeworkType: HomeworkType? = null

    private var homework: Homework? = null //新创建作业
    private var homeworkContent: HomeworkContent? = null//当前作业内容
    private var homeworkContent_a: HomeworkContent? = null//a屏作业

    private var homeworkLists = mutableListOf<Homework>() //所有作业
    private var homeworkContentLists = mutableListOf<HomeworkContent>() //所有作业内容

    private var page = 0//页码
    private var currentPosition = 0//目录位置
    private var messages= mutableListOf<HomeworkMessage>()

    override fun layoutId(): Int {
        return R.layout.ac_homework_drawing
    }

    override fun initData() {
        var bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkType
        homeworkTypeId = homeworkType?.type!!
        courseId=homeworkType?.courseId!!

        homeworkLists = HomeworkDaoManager.getInstance(this).queryAllByType(courseId, homeworkTypeId)
        homeworkContentLists = HomeworkContentDaoManager.getInstance(this).queryAllByType(courseId, homeworkTypeId)

        if (homeworkLists.size > 0) {

            currentPosition = homeworkLists.size - 1
            //未做完作业继续 页面最后一张
            homework = homeworkLists[currentPosition]

            homeworkContent = homeworkContentLists[homeworkContentLists.size - 1]

            page = homeworkContentLists.size - 1

        } else {
            newHomeWork()
            newHomeWorkContent()
            page = 0
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

        v_content_a.setImageResource(ToolUtils.getImageResId(this,homeworkType?.resId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this,homeworkType?.resId))//设置背景
        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace

        changeContent()

        tv_title.setOnClickListener {
            var title=tv_title.text.toString()
            InputContentDialog(this,getCurrentScreenPos(),title).builder()?.setOnDialogClickListener(object :
                InputContentDialog.OnDialogClickListener {
                override fun onClick(string: String) {
                    tv_title.text=string
                    homework?.title = string
                    homeworkLists[currentPosition].title = string
                    HomeworkDaoManager.getInstance(this@HomeworkDrawingActivity).insertOrReplace(homework)
                }

            })

        }

        btn_page_down.setOnClickListener {

            val total=homeworkContentLists.size-1
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
            isExpand=!isExpand
            moveToScreen(isExpand)
            changeExpandView()
            changeContent()
        }

        iv_btn.setOnClickListener {
            showPopWindowBtn()
        }

    }

    private fun changeExpandView(){
        v_content_b.visibility = if(isExpand) View.VISIBLE else View.GONE
        tv_page_b.visibility = if(isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility=if(isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var list= mutableListOf<ListBean>()
        for (item in homeworkLists){
            val listBean= ListBean()
            listBean.name=item.title
            listBean.page=item.page
            list.add(listBean)
        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (currentPosition != position) {
                    currentPosition = position
                    page = homeworkLists[position].page
                    changeContent()
                }
            }
        })
    }

    //翻页内容更新切换
    private fun changeContent() {

        homeworkContent = homeworkContentLists[page]

        if (isExpand) {
            if (page > 0) {
                homeworkContent_a = homeworkContentLists[page - 1]
            } else {
                if (homeworkContentLists.size > 1) {
                    page=1
                    homeworkContent = homeworkContentLists[page]
                    homeworkContent_a = homeworkContentLists[page-1]
                } else {
                    homeworkContent_a = null
                }
            }
        } else {
            homeworkContent_a = null
        }

        homework = HomeworkDaoManager.getInstance(this).queryByID(homeworkContent?.homeworkId)
        currentPosition = homework?.index!!//当前作业位置（下标）

        //切换页面内容的一些变化
        tv_title.text=homework?.title
        if (homework?.title.isNullOrEmpty())
        {
            tv_title.hint="输入标题"
        }
        tv_title.isClickable = homework?.isSave != true

        updateUI()
    }

    //更新绘图以及页码
    private fun updateUI() {
        val pageTotal = homeworkContentLists.size
        if (isExpand) {
            updateImage(elik_b!!, homeworkContent?.path!!)
            tv_page_b.text = (page + 1).toString()

            if (homeworkContent_a != null) {
                v_content_a.setImageResource(ToolUtils.getImageResId(this,homeworkType?.resId))
                updateImage(elik_a!!, homeworkContent_a?.path!!)
                tv_page_a.text = "$page"
            }
            else{
                //当只存在一个页面全屏展示时候 a屏不显示东西不能手写
                v_content_a.setImageResource(0)
                elik_a?.setPWEnabled(false)
                tv_page_a.text = ""
            }
        } else {
            v_content_a.setImageResource(ToolUtils.getImageResId(this,homeworkType?.resId))
            updateImage(elik_a!!, homeworkContent?.path!!)
            tv_page_a.text = (page + 1).toString()
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

    //创建新的作业
    private fun newHomeWork() {

        currentPosition = homeworkLists.size

        homework = Homework()
        homework?.index = currentPosition
        homework?.courseId = courseId
        homework?.startDate = System.currentTimeMillis()
        homework?.homeworkTypeId = homeworkType?.type
        homework?.bgResId = homeworkType?.resId
        homework?.state = 0

        homework?.page = homeworkContentLists.size //设置作业页码 作业内容的第一个

        HomeworkDaoManager.getInstance(this).insertOrReplace(homework)
        homework?.id = HomeworkDaoManager.getInstance(this).insertId

        homework?.path = FileAddress().getPathHomework(courseId,homeworkType?.type,homework?.id?.toInt())

        homeworkLists.add(homework!!)
    }

    //创建新的作业内容
    private fun newHomeWorkContent() {

        homework=homeworkLists[homeworkLists.size-1]
        var date = DateUtils.longToString(System.currentTimeMillis())

        homeworkContent = HomeworkContent()
        homeworkContent?.courseId = courseId
        homeworkContent?.date = System.currentTimeMillis()
        homeworkContent?.homeworkTypeId = homework?.homeworkTypeId
        homeworkContent?.bgResId = homework?.bgResId
        homeworkContent?.homeworkId = homework?.id

        homeworkContent?.path = "${homework?.path}/$date.tch"
        homeworkContent?.page = homeworkContentLists.size

        page = homeworkContentLists.size
        homeworkContentLists.add(homeworkContent!!)

        HomeworkContentDaoManager.getInstance(this).insertOrReplace(homeworkContent)

    }

    private fun showPopWindowBtn() {
        var popWindowDrawingButton = if (homework?.isSave == false) {
            PopWindowDrawingButton(this, iv_btn, 0, -350)
        } else if (homework?.isSave == true && homework?.state == 0) {
            PopWindowDrawingButton(this, iv_btn, 1, -200)
        } else {
            return
        }
        popWindowDrawingButton.builder()
        popWindowDrawingButton.setOnSelectListener(object : PopWindowDrawingButton.OnClickListener {
                override fun onClick(type: Int) {
                    if (type == 1) {//保存
                        if (homework?.isSave == false) {
                            save()
                        }
                    }
                    if (type == 2) {//提交
                        if (homework?.state == 0) {
                            commit()
                        }
                    }
                    if (type == 3) {
                        if (homework?.isSave == false) {
                            delete()
                        }
                    }
                }
            })
    }

    //保存这次作业
    private fun save() {
        val titleStr = tv_title.text.toString()
        if (titleStr.isNullOrEmpty())
        {
            showToast("请先输入标题")
            return
        }
        saveNewHomework()

        newHomeWork()
        newHomeWorkContent()

        changeContent()
    }

    //保存新增作业
    private fun saveNewHomework() {
        homework?.title = tv_title.text.toString()
        homework?.isSave = true
        homework?.endDate = System.currentTimeMillis()

        homeworkLists[currentPosition] = homework!!

        HomeworkDaoManager.getInstance(this).insertOrReplace(homework)
    }

    //作业提交
    private fun commit() {
        //如果这是最后一个作业 先保存
        if (currentPosition == homeworkLists.size - 1) {

            val titleStr = tv_title.text.toString()
            if (titleStr.isNullOrEmpty())
            {
                showToast("请先输入标题")
                return
            }
            homework?.state = 1
            homeworkLists[currentPosition].state = 1
            saveNewHomework()
//            selectorHomework()

        } else {
            homework?.state = 1
            homeworkLists[currentPosition].state = 1
            HomeworkDaoManager.getInstance(this@HomeworkDrawingActivity).insertOrReplace(homework)
//            selectorHomework()
        }
    }

    private var selectorDialog:HomeworkMessageSelectorDialog?=null
    /**
     * 提交 选择提交那次作业
     */
    private fun selectorHomework(){
        if(selectorDialog==null){
            selectorDialog= HomeworkMessageSelectorDialog(this,getCurrentScreenPos(),messages).builder()
            selectorDialog?.setOnDialogClickListener {
                if (currentPosition == homeworkLists.size - 1) {
                    newHomeWork()
                    newHomeWorkContent()
                    changeContent()
                }
            }
        }
        else{
            selectorDialog?.show()
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
        homeworkContentLists.remove(homeworkContent)
        var pathName = FileUtils.getFileName(File(homeworkContent?.path).name).toString()
        FileUtils.deleteFile(homework?.path, pathName)//删除文件

        var homeworkContents = HomeworkContentDaoManager.getInstance(this).queryByID(homework?.id)

        if (homeworkContents.size == 0) {

            HomeworkDaoManager.getInstance(this).deleteBean(homework)
            homeworkLists.remove(homework)

            FileUtils.deleteFile(File(homework?.path))//删除文件夹中的文件

        }
        if (page>0){
            page -= 1
        }else{
            newHomeWork()
            newHomeWorkContent()
        }
        changeContent()

    }

   override fun changeScreenPage() {
        if (isExpand){
            isExpand=!isExpand
            moveToScreen(isExpand)
            changeExpandView()
            changeContent()
        }
    }

}