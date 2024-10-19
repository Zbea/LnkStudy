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
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_diary.ll_date
import kotlinx.android.synthetic.main.ac_diary.tv_digest
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total

class DiaryActivity:BaseDrawingActivity() {

    private var uploadId=0
    private var nowLong=0L//当前时间
    private var diaryBean:DiaryBean?=null
    private var images = mutableListOf<String>()//手写地址
    private var posImage=0
    private var bgRes=""

    override fun layoutId(): Int {
        return R.layout.ac_diary
    }

    override fun initData() {
        uploadId=intent.flags
        nowLong=DateUtils.getStartOfDayInMillis()

        if (uploadId==0){
            diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,uploadId)
            if (diaryBean==null){
                initCurrentDiaryBean()
            }
            changeContent()
        }
        else{
            diaryBean=DiaryDaoManager.getInstance().queryBean(uploadId)
            if (diaryBean!=null){
                nowLong=diaryBean?.date!!
                changeContent()
            }
        }
    }

    override fun initView() {
        disMissView(iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        elik_b?.addOnTopView(ll_date)
        elik_b?.addOnTopView(tv_digest)

        iv_up.setOnClickListener {
            val lastDiaryBean=DiaryDaoManager.getInstance().queryBeanByDate(nowLong,0,uploadId)
            if (lastDiaryBean!=null){
                saveDiary()
                diaryBean=lastDiaryBean
                nowLong=lastDiaryBean.date
                changeContent()
            }
        }

        iv_down.setOnClickListener {
            val nextDiaryBean=DiaryDaoManager.getInstance().queryBeanByDate(nowLong,1,uploadId)
            if (nextDiaryBean!=null){
                saveDiary()
                diaryBean=nextDiaryBean
                nowLong=nextDiaryBean.date
                changeContent()
            }
            else{
                //本地日记：当最新的当天还没有保存时，可以切换到当天
                if (uploadId==0){
                    if (nowLong<DateUtils.getStartOfDayInMillis()){
                        saveDiary()
                        nowLong=DateUtils.getStartOfDayInMillis()
                        initCurrentDiaryBean()
                        changeContent()
                    }
                }
            }
        }

        tv_date.setOnClickListener {
            CalendarDiaryDialog(this,getCurrentScreenPos(),uploadId).builder().setOnDateListener{
                saveDiary()
                nowLong=it
                diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,uploadId)
                changeContent()
            }
        }

        iv_btn.setOnClickListener {
            ModuleAddDialog(this,getCurrentScreenPos(),getString(R.string.diary_module_str), DataBeanManager.noteModuleDiary).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    diaryBean?.bgRes=bgRes
                    setBg()
                    SPUtil.putString(Constants.SP_DIARY_BG_SET,bgRes)

                }
        }

        tv_digest.setOnClickListener {
            InputContentDialog(this,getCurrentScreenPos(),if (diaryBean?.title.isNullOrEmpty()) "输入摘要" else diaryBean?.title!!).builder().setOnDialogClickListener{
                diaryBean?.title=it
            }
        }

    }

    /**
     * 初始化
     */
    private fun initCurrentDiaryBean(){
        bgRes= SPUtil.getString(Constants.SP_DIARY_BG_SET).ifEmpty { ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1) }
        diaryBean= DiaryBean()
        diaryBean?.date=nowLong
        diaryBean?.year=DateUtils.getYear()
        diaryBean?.month=DateUtils.getMonth()
        diaryBean?.bgRes=bgRes
    }

    override fun onChangeExpandContent() {
        changeErasure()
        //云书库下载日记，如果只存在一页不能全屏
        if (uploadId>0&&diaryBean?.paths?.size==1){
            return
        }
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    override fun onCatalog() {
        val diaryBeans=DiaryDaoManager.getInstance().queryListByTitle(uploadId)
        CatalogDiaryDialog(this,screenPos,getCurrentScreenPos(),diaryBeans).builder().setOnDialogClickListener { position ->
            diaryBean = diaryBeans[position]
            if (nowLong != diaryBean?.date) {
                saveDiary()
                nowLong = diaryBean?.date!!
                changeContent()
            }
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
        setBg()
        tv_date.text=DateUtils.longToStringWeek(nowLong)
        setPWEnabled(!diaryBean?.isUpload!!)
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

    private fun setBg(){
        v_content_a?.setBackgroundResource(ToolUtils.getImageResId(this, bgRes))
        v_content_b?.setBackgroundResource(ToolUtils.getImageResId(this, bgRes))
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong))
        if (FileUtils.isExistContent(path)){
            diaryBean?.paths = images
            diaryBean?.page=posImage
            DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
        }
    }

    override fun onPause() {
        super.onPause()
        saveDiary()
    }
}