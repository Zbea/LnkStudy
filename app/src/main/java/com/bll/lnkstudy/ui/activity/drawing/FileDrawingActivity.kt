package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.BitmapFactory
import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFileDrawingActivity
import com.bll.lnkstudy.utils.FileUtils
import kotlinx.android.synthetic.main.ac_drawing.tv_page_a
import kotlinx.android.synthetic.main.ac_drawing_file.*
import kotlinx.android.synthetic.main.common_drawing_tool.*


class FileDrawingActivity : BaseFileDrawingActivity() {

    private var path=""

    override fun layoutId(): Int {
        return R.layout.ac_drawing_file
    }

    override fun initData() {
        pageIndex = intent.getIntExtra("pageIndex", 0)
        path= intent.getStringExtra("pagePath").toString()
        pageCount=FileUtils.getFiles(path).size
        if (pageIndex==Constants.DEFAULT_PAGE)
            pageIndex=pageCount-1
    }

    override fun initView() {
        disMissView(iv_draft,iv_btn,iv_catalog)

        onChangeContent()
    }

    override fun onPageUp() {
        if (isExpand) {
            if (pageIndex > 1) {
                pageIndex -= 2
                onChangeContent()
            } else {
                pageIndex = 1
                onChangeContent()
            }
        } else {
            if (pageIndex > 0) {
                pageIndex -= 1
                onChangeContent()
            }
        }
    }

    override fun onPageDown() {
        if (isExpand){
            if (pageIndex<pageCount-2){
                pageIndex+=2
                onChangeContent()
            }
            else if (pageIndex==pageCount-2){
                pageIndex=pageCount-1
                onChangeContent()
            }
        }
        else{
            if (pageIndex<pageCount-1){
                pageIndex+=1
                onChangeContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        if (pageCount>1){
            changeErasure()
            isExpand=!isExpand
            moveToScreen(isExpand)
            onChangeExpandView()
            onChangeContent()
        }
    }

    /**
     * 更新内容
     */
    private fun onChangeContent() {
        if (pageCount==0)
            return
        if (pageIndex>=pageCount){
            pageIndex=pageCount-1
            return
        }
        if (pageIndex==0&&isExpand){
            pageIndex=1
        }

        tv_page.text = "${pageIndex+1}/$pageCount"
        loadPicture(pageIndex, elik_b!!, iv_content_b)
        if (isExpand) {
            loadPicture(pageIndex-1, elik_a!!, iv_content_a)
            tv_page.text = "${pageIndex}/$pageCount"
            tv_page_a.text = "${pageIndex+1}/$pageCount"
        }

    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val files = FileUtils.getFiles(path)
        if (index<files.size){
            val showFile=files[index]
            if (showFile != null) {
                val myBitmap= BitmapFactory.decodeFile(showFile.absolutePath)
                view.setImageBitmap(myBitmap)
                elik.setLoadFilePath(getDrawingPath(index+1), true)
            }
        }
    }

    override fun onElikSava_a() {
        saveElik(elik_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface){
        elik.saveBitmap(true) {}
    }

    /**
     * 得到提错本的手写路径
     */
    private fun getDrawingPath(index: Int):String{
        return "$path/drawing/$index.tch"
    }

}