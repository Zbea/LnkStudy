package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.TextView
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CalendarDiaryDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_diary.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import java.io.File

class DiaryActivity:BaseDrawingActivity() {

    private var nowLong=0L//当前时间
    private var diaryBean:DiaryBean?=null
    private var images = mutableListOf<String>()//手写地址
    private var posImage=0
    private var bgRes=""

    override fun layoutId(): Int {
        return R.layout.ac_diary
    }

    override fun initData() {
        nowLong=DateUtils.getStartOfDayInMillis()

        diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong)
        if (diaryBean==null){
            initCurrentDiaryBean()
        }
    }

    override fun initView() {
        disMissView(iv_catalog,iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        elik_b?.addOnTopView(ll_date)

        iv_up.setOnClickListener {
            val lastDiaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,0)
            if (lastDiaryBean!=null){
                saveDiary()
                diaryBean=lastDiaryBean
                nowLong=lastDiaryBean.date
                changeContent()
            }
        }

        iv_down.setOnClickListener {
            val nextDiaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,1)
            if (nextDiaryBean!=null){
                saveDiary()
                diaryBean=nextDiaryBean
                nowLong=nextDiaryBean.date
                changeContent()
            }
            else{
                if (nowLong<DateUtils.getStartOfDayInMillis()){
                    nowLong=DateUtils.getStartOfDayInMillis()
                    initCurrentDiaryBean()
                    changeContent()
                }
            }
        }

        tv_date.setOnClickListener {
            CalendarDiaryDialog(this,getCurrentScreenPos()).builder().setOnDateListener{
                saveDiary()
                nowLong=it
                diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong)
                if (diaryBean==null){
                    initCurrentDiaryBean()
                }
                changeContent()
            }
        }

        iv_btn.setOnClickListener {
            ModuleAddDialog(this,getCurrentScreenPos(),getString(R.string.diary_module_str), DataBeanManager.noteModuleDiary).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    diaryBean?.bgRes=bgRes
                    v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
                }
        }

        changeContent()
    }

    /**
     * 初始化
     */
    private fun initCurrentDiaryBean(){
        bgRes=ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1)
        diaryBean= DiaryBean()
        diaryBean?.date=nowLong
        diaryBean?.title=DateUtils.longToStringDataNoYear(nowLong)
        diaryBean?.year=DateUtils.getYear()
        diaryBean?.month=DateUtils.getMonth()
        diaryBean?.bgRes=bgRes
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        setContentImage()
    }


    override fun onPageDown() {
        posImage += if (isExpand)2 else 1
        setContentImage()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= if (isExpand)2 else 1
            if (posImage<0){
                posImage=0
            }
            setContentImage()
        }
    }

    /**
     * 切换日记
     */
    private fun changeContent(){
        bgRes=diaryBean?.bgRes.toString()
        images= diaryBean?.paths as MutableList<String>
        posImage=diaryBean?.page!!
        setContentImage()
    }

    /**
     * 显示内容
     */
    private fun setContentImage() {
        tv_date.text=DateUtils.longToStringWeek(nowLong)

        v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
        v_content_a.setImageResource(ToolUtils.getImageResId(this, bgRes))

        if (isExpand){
            val path = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage + 1}.tch"
            setEinkImage(elik_a!!,tv_page_a,posImage,path)

            val path_b = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage + 1+1}.tch"
            setEinkImage(elik_b!!,tv_page,posImage+1,path_b)
        }
        else{
            val path = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage + 1}.tch"
            setEinkImage(elik_b!!,tv_page,posImage,path)
        }

    }

    private fun setEinkImage(eink:EinkPWInterface,tv:TextView,page:Int,path:String){
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        tv.text = "${page + 1}"
        tv_page_total.text="${images.size}"
        tv_page_total_a.text="${images.size}"
        eink.setLoadFilePath(path, true)
    }


    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong))
        if (!File(path).list().isNullOrEmpty()){
            diaryBean?.paths = images
            diaryBean?.page=posImage
            val id=DiaryDaoManager.getInstance().insertOrReplaceGetId(diaryBean)
            DataUpdateManager.createDataUpdate(8,id.toInt(),1,Gson().toJson(diaryBean),path)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDiary()
    }

}