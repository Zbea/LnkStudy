package com.bll.lnkstudy.ui.activity.book

import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.manager.DataUpdateDaoManager
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.MD5Utils
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class TextBookAnnotationActivity: BaseDrawingActivity() {

    private var bookId=0
    private var page=0
    private var paths= mutableListOf<String>()
    private var posImage = 0

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        bookId=intent.getIntExtra("bookId",0)
        page=intent.getIntExtra("page",0)

        setData()
    }

    private fun setData(){
        paths.clear()
        val files=FileUtils.getFiles(getPath())
        for (file in files){
            paths.add(file.path)
        }
        if (paths.isEmpty()){
            paths.add(getPathStr())
        }
    }

    override fun initView() {
        MethodManager.setImageResource(this, R.mipmap.icon_note_content_hg_11,v_content_a)
        MethodManager.setImageResource(this, R.mipmap.icon_note_content_hg_11,v_content_b)

        disMissView(iv_expand,iv_btn,iv_tool,iv_catalog)

        onContent()
    }

    override fun onPageDown() {
        if (posImage==paths.size-1){
            if (isDrawLastContent()){
                paths.add(getPathStr())
                posImage+=1
                onContent()
            }
        }
        else{
            posImage+=1
            onContent()
        }
    }

    override fun onPageUp() {
        if (posImage>0)
            posImage-=1
        onContent()
    }

    override fun onContent() {
        tv_page.text = "${posImage + 1}"
        tv_page_total.text="${paths.size}"
        elik_b?.setLoadFilePath(paths[posImage], true)
    }

    override fun onElikSava_a() {
        refreshDataUpdate()
    }

    override fun onElikSava_b() {
        refreshDataUpdate()
    }

    /**
     * 刷新增量更新
     */
    private fun refreshDataUpdate(){
        val item= DataUpdateDaoManager.getInstance().queryBean(1,bookId,3,bookId)
        if (item==null){
            DataUpdateManager.createDataUpdateDrawing(1,bookId,3,bookId,getPath())
        }
        else{
            DataUpdateManager.editDataUpdate(1,bookId,3,bookId)
        }
    }

    /**
     * 最后一个是否已写
     */
    private fun isDrawLastContent():Boolean{
        return File(paths.last()).exists()
    }

    private fun getPath():String{
        return FileAddress().getPathTextBookAnnotation(MD5Utils.digest(bookId.toString()),page+1)
    }

    private fun getPathStr():String{
        return getPath()+"/${DateUtils.longToString(System.currentTimeMillis())}.png"
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.TEXTBOOK_ANNOTATION_CHANGE_PAGE_EVENT){
            posImage=0
            page=DataBeanManager.textBookAnnotationPage
            setData()
            onContent()
        }
    }

}