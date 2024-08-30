package com.bll.lnkstudy.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CalendarDiaryDialog
import com.bll.lnkstudy.dialog.CatalogDiaryDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.mvp.model.DiaryBean
import com.bll.lnkstudy.utils.*
import kotlinx.android.synthetic.main.ac_diary.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

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
        disMissView(iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        elik_b?.addOnTopView(ll_date)
        elik_b?.addOnTopView(tv_digest)

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
                    v_content_b?.setImageResource(ToolUtils.getImageResId(this, bgRes))
                    v_content_a?.setImageResource(ToolUtils.getImageResId(this, bgRes))
                    SPUtil.putString("dirayBgRes",bgRes)
                }
        }

        tv_digest.setOnClickListener {
            InputContentDialog(this,getCurrentScreenPos(),if (diaryBean?.title.isNullOrEmpty()) "输入摘要" else diaryBean?.title!!).builder().setOnDialogClickListener{
                diaryBean?.title=it
            }
        }

        changeContent()
    }

    /**
     * 初始化
     */
    private fun initCurrentDiaryBean(){
        bgRes= SPUtil.getString("dirayBgRes").ifEmpty { ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1) }
        diaryBean= DiaryBean()
        diaryBean?.date=nowLong
        diaryBean?.year=DateUtils.getYear()
        diaryBean?.month=DateUtils.getMonth()
        diaryBean?.bgRes=bgRes
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onCatalog() {
        val diaryBeans=DiaryDaoManager.getInstance().queryListByTitle()
        CatalogDiaryDialog(this,screenPos,getCurrentScreenPos(),diaryBeans).builder().setOnDialogClickListener{
            saveDiary()
            diaryBean=diaryBeans[it]
            nowLong=diaryBean?.date!!
            changeContent()
        }
    }

    override fun onPageDown() {
        posImage += if (isExpand)2 else 1
        onContent()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= if (isExpand)2 else 1
            onContent()
        }
    }

    /**
     * 切换日记
     */
    private fun changeContent(){
        bgRes=diaryBean?.bgRes.toString()
        images= diaryBean?.paths as MutableList<String>
        posImage=diaryBean?.page!!
        onContent()
    }

    /**
     * 显示内容
     */
    override fun onContent() {
        if (isExpand){
            if (posImage<1){
                posImage=1
            }
        }
        else{
            if (posImage<0){
                posImage=0
            }
        }

        tv_date.text=DateUtils.longToStringWeek(nowLong)

        v_content_b?.setImageResource(ToolUtils.getImageResId(this, bgRes))
        v_content_a?.setImageResource(ToolUtils.getImageResId(this, bgRes))

        setPWEnabled(!diaryBean?.isUpload!!)
        val path = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage + 1}.png"
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        if (diaryBean?.isUpload!!){
            GlideUtils.setImageUrl(this, path, v_content_b)
        }
        else{
            setEinkImage(elik_b!!,path)
        }
        tv_page.text = "${posImage + 1}"

        if (isExpand){
            val path_a = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage}.png"
            //判断路径是否已经创建
            if (!images.contains(path_a)) {
                images.add(path_a)
            }
            if (diaryBean?.isUpload!!){
                GlideUtils.setImageUrl(this, path_a, v_content_a)
            }
            else{
                setEinkImage(elik_a!!,path_a)
            }

            if (screenPos== Constants.SCREEN_LEFT){
                tv_page_a.text = "${posImage + 1}"
                tv_page.text = "$posImage"
            }
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page.text = "${posImage + 1}"
                tv_page_a.text = "$posImage"
            }
        }

        tv_page_total.text="${images.size}"
        tv_page_total_a.text="${images.size}"
    }

    private fun setEinkImage(eink:EinkPWInterface,path:String){
        eink.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
        elik_a?.saveBitmap(true) {}
    }

    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong))
        if (FileUtils.isExistContent(path)){
            diaryBean?.paths = images
            diaryBean?.page=posImage
            DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDiary()
    }

}