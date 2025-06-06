package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogDialog
import com.bll.lnkstudy.dialog.ResultStandardDetailsDialog
import com.bll.lnkstudy.dialog.ScoreDetailsDialog
import com.bll.lnkstudy.manager.HomeworkShareDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_drawing.iv_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.util.stream.Collectors


/**
 * 作业卷提交
 */
class HomeworkShareDrawingActivity: BaseDrawingActivity(){

    private var homeworkType:HomeworkTypeBean?=null
    private var papers= mutableListOf<HomeworkShareBean>()
    private var paper: HomeworkShareBean?=null

    private var currentPosition=0
    private var oldPosition=-1
    private var page = 0//页码


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        homeworkType = MethodManager.getHomeworkTypeBundle(intent)

        papers=HomeworkShareDaoManager.getInstance().queryAll(homeworkType?.typeId!!)
        if(papers.size>0){
            if (currentPosition == DEFAULT_PAGE)
                currentPosition=papers.size-1
            onContent()
        }
        else{
            setDisableTouchInput(true)
        }
    }

    override fun initView() {
        disMissView(iv_btn)
        if (homeworkType?.isCloud==true)
            setDisableTouchInput(true)

        iv_score.setOnClickListener {
            if (paper?.type==1&&paper?.subType!=1){
                val items=DataBeanManager.getResultStandardItems(paper!!.subType,paper!!.commonName,1).stream().collect(Collectors.toList())
                ResultStandardDetailsDialog(this,paper!!.title,paper!!.score,1,paper!!.question,items).builder()
            }
            else{
                val answerImages= paper!!.answerUrl?.split(",") as MutableList<String>
                ScoreDetailsDialog(this,paper!!.title,paper!!.score,paper!!.questionType,paper!!.questionMode,answerImages,paper!!.question).builder()
            }
        }
    }

    override fun onCatalog() {
        val list= mutableListOf<ItemList>()
        for (item in papers){
            val itemList= ItemList()
            itemList.name="(${item.name})"+item.title
            itemList.page=papers.indexOf(item)
            list.add(itemList)
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (currentPosition!=pageNumber){
                    currentPosition = pageNumber
                    oldPosition=-1
                    page = 0
                    onContent()
                }
            }
            override fun onDelete(position: Int) {
                val item=papers[position]
                HomeworkShareDaoManager.getInstance().deleteBean(item)
                DataUpdateManager.deleteDateUpdate(2,item.id.toInt(), 2,item.typeId)
                DataUpdateManager.deleteDateUpdate(2,item.id.toInt(),3,item.typeId)
                FileUtils.delete(item.filePath)
                papers.removeAt(position)
                pageCount-=1
                if (position<=currentPosition){
                    currentPosition-=1
                    if (papers.isEmpty()){
                        setContentImageClear()
                    }
                    else{
                        page=0
                        onContent()
                    }
                }
            }
        })
    }

    override fun onPageDown() {
        val count=if (isExpand) pageCount-2 else pageCount-1
        if (page<count){
            page+=if (isExpand)2 else 1
            onContent()
        }
        else{
            if (currentPosition<papers.size-1){
                currentPosition+=1
                page=0
                onContent()
            }
        }
    }

    override fun onPageUp() {
        if (page>0){
            page-=if (isExpand)2 else 1
            onContent()
        }
        else{
            if (currentPosition>0){
                currentPosition-=1
                page=0
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        //单屏时只有一页无法展开
        if (!isExpand&&pageCount==1)
            return
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onContent() {
        if(papers.size==0||currentPosition>=papers.size)
            return
        paper=papers[currentPosition]
        pageCount=paper!!.paths.size

        if (isExpand&&pageCount==1){
            onChangeExpandContent()
            return
        }

        if (isExpand&&page>pageCount-2)
            page=pageCount-2
        if (page<0)
            page=0

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        if (isExpand){
            setElikLoadPath(page,elik_a!!,v_content_a!!)
            setElikLoadPath(page+1,elik_b!!,v_content_b!!)
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text="${page+1}"
                tv_page.text="${page+1+1}"
            }
            else{
                tv_page.text="${page+1}"
                tv_page_a.text="${page+1+1}"
            }
        }
        else{
            setElikLoadPath(page,elik_b!!,v_content_b!!)
            tv_page.text="${page+1}"
        }

        if (currentPosition!=oldPosition){
            if (paper?.question?.isNotEmpty() == true){
                showView(iv_score)
            }
            else{
                disMissView(iv_score)
            }
        }
        //用来判断重复加载
        oldPosition=currentPosition
    }

    
    //加载图片
    private fun setElikLoadPath(index: Int, elik:EinkPWInterface, view:ImageView) {
        val path=paper!!.paths[index]
        MethodManager.setImageFile(path,view)
        elik.setLoadFilePath(paper!!.drawPaths[index],true)
    }

    override fun onElikSava_a() {
        DataUpdateManager.createDataUpdateState(2,paper?.id!!.toInt(),3,homeworkType?.typeId!!,9,"","${paper?.filePath!!}/draw/")
    }

    override fun onElikSava_b() {
        DataUpdateManager.createDataUpdateState(2,paper?.id!!.toInt(),3,homeworkType?.typeId!!,9,"","${paper?.filePath!!}/draw/")
    }

}