package com.bll.lnkstudy

import com.bll.lnkstudy.MyApplication.Companion.mContext
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.mvp.model.date.DateRemind
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.utils.ToolUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.util.*

object DataBeanManager {

    var isRuleImage=false//是否设置规矩图

    var homeworkMessages= mutableListOf<Any>()

     val listTitle = arrayOf(
        "首页","书架",
        "课本","作业",
        "试卷","笔记",
        "书画","视教",
        "学情","日记"
    )
    val teachingType = arrayOf(
        mContext.getString(R.string.textbook_tab_my),mContext.getString(R.string.textbook_tab_assist),
        mContext.getString(R.string.textbook_tab_other),mContext.getString(R.string.homework_other_book)
    )
    val textbookType = arrayOf(
        mContext.getString(R.string.textbook_tab_my),mContext.getString(R.string.textbook_tab_assist),
        mContext.getString(R.string.textbook_tab_other),mContext.getString(R.string.textbook_tab_old)
    )
    val searchType = arrayOf(
        mContext.getString(R.string.search_bookcase_str),mContext.getString(R.string.search_textbook_str),
        mContext.getString(R.string.search_homework_str),mContext.getString(R.string.search_exam_str),mContext.getString(R.string.search_note_str),"书画"
    )
    private val dateRemind = arrayOf(1, 3, 5, 7, 10, 15)
    val bookType = arrayOf(
        "诗经楚辞", "唐诗宋词", "古代经典",
        "四大名著", "中国科技", "小说散文",
        "外国原著", "历史地理", "政治经济",
        "军事战略", "科学技术", "运动才艺"
    )
//    val bookType = arrayOf(
//        R.string.book_tab_sjcc,R.string.book_tab_tssc,
//        R.string.book_tab_gdjd,R.string.book_tab_sdmz,
//        R.string.book_tab_zgkj,R.string.book_tab_xssw,
//        R.string.book_tab_wgyz,R.string.book_tab_lsdl,
//        R.string.book_tab_zzjj,R.string.book_tab_jszl,
//        R.string.book_tab_kxjs,R.string.book_tab_ydcy
//    ) //书籍分类
    val dynastys = arrayOf(
        R.string.age_han,R.string.age_tang,
        R.string.age_song,R.string.age_yuan,
        R.string.age_ming,R.string.age_qing,
        R.string.age_jin,R.string.age_dan
    )
    var PAINTING = arrayOf(
        mContext.getString(R.string.painting_mbsf),mContext.getString(R.string.painting_ssh),
        mContext.getString(R.string.painting_hnh),mContext.getString(R.string.painting_rwh),
        mContext.getString(R.string.painting_smh),mContext.getString(R.string.painting_ybsf)
    )
    var resources = arrayOf("我的工具","锁屏壁纸","历代书画","跳页日历")

    fun popupGrades(): MutableList<PopupBean>
       {
           val grades=MethodManager.getItemLists("grades")
            val list= mutableListOf<PopupBean>()
            for (i in grades.indices){
                list.add(PopupBean(grades[i].type, grades[i].desc, i == 0))
            }
            return list
        }

    fun popupGrades(grade: Int): MutableList<PopupBean> {
        val grades=MethodManager.getItemLists("grades")
        val list = mutableListOf<PopupBean>()
        for (item in grades) {
            list.add(PopupBean(item.type, item.desc, item.type == grade))
        }
        return list
    }

    fun popupGradeThans(grade: Int): MutableList<PopupBean> {
        val grades=MethodManager.getItemLists("grades")
        val list = mutableListOf<PopupBean>()
        for (item in grades) {
            if(item.type>=grade){
                list.add(PopupBean(item.type, item.desc, item.type == grade))
            }
        }
        return list
    }

    //学期选择
    fun popupSemesters(): MutableList<PopupBean>{
        val list = mutableListOf<PopupBean>()
        list.add(PopupBean(1, mContext.getString(R.string.semester_last),true))
        list.add(PopupBean(2,mContext.getString(R.string.semester_next),false))
        return list
    }

    fun popupSemesters(semester:Int):MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (item in popupSemesters()){
            list.add(PopupBean(item.id, item.name, item.id == semester))
        }
        return list
    }


    /**
     * 获取当前选中年级
     */
    fun getGradeStr(grade: Int): String {
        val grades=MethodManager.getItemLists("grades")
        var cls=""
        for (item in grades) {
            if (item.type == grade){
                cls=item.desc
            }
        }
        return cls
    }

    fun getBookVersionStr(version: Int): String {
        val versions=MethodManager.getItemLists("bookVersions")
        var cls=""
        for (item in versions) {
            if (item.type == version){
                cls=item.desc
            }
        }
        return cls
    }


    fun popupTypeGrades(): MutableList<PopupBean>
       {
            val typeGrades=MethodManager.getItemLists("typeGrades")
            val list= mutableListOf<PopupBean>()
            for (i in typeGrades.indices){
                list.add(PopupBean(typeGrades[i].type, typeGrades[i].desc, i == getTypeGradePos()))
            }
            return list
        }

    /**
     * 获取位置
     */
    fun getTypeGradePos(): Int
    {
        val grade=MethodManager.getUser()?.grade!!
        val type=if (grade<4){
            0
        }
        else if (grade in 4..6){
            1
        }
        else if (grade in 7..9){
            2
        }
        else{
            3
        }
        return type
    }

    val popupCourses: MutableList<PopupBean>
        get() {
            val courses=MethodManager.getItemLists("courses")
            val list= mutableListOf<PopupBean>()
            for (i in courses.indices){
                list.add(PopupBean(courses[i].type, courses[i].desc, i == 0))
            }
            return list
        }

    fun popupCourses(course:Int): MutableList<PopupBean>{
        val courses=MethodManager.getItemLists("courses")
        val list= mutableListOf<PopupBean>()
        for (item in courses){
            list.add(PopupBean(item.type, item.desc, item.type == course))
        }
        return list
    }

    /**
     * 获取选中科目
     */
    fun getCourseStr(course:Int):String{
        val courses=MethodManager.getItemLists("courses")
        var courseStr=""
        for (item in courses){
            if (course==item.type){
                courseStr=item.desc
            }
        }
        return courseStr
    }

    /**
     * 获取科目id
     */
    fun getCourseId(course:String):Int{
        val courses=MethodManager.getItemLists("courses")
        var courseId=0
        for (item in courses){
            if (course==item.desc){
                courseId=item.type
            }
        }
        return courseId
    }

    fun popupDynasty():MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (i in dynastys.indices){
            list.add(PopupBean(i + 1, mContext.getString(dynastys[i]), i == 0))
        }
        return list
    }

    fun popupDynastyNow():MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (i in 6 until dynastys.size){
            list.add(PopupBean(i + 1, mContext.getString(dynastys[i]), i == 6))
        }
        return list
    }

    fun popupPainting():MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (i in PAINTING.indices){
            list.add(PopupBean(i + 1, PAINTING[i], i == 0))
        }
        return list
    }

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexDataCloud(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            isCheck = true
            name = listTitle[1]
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_textbook)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_textbook_check)
            name = listTitle[2]
        }

        val h3 = ItemList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_tab_homework)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_homework_check)
            name = listTitle[3]
        }

        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_paper)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_paper_check)
            name = listTitle[4]
        }

        val h5 = ItemList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = listTitle[5]
        }

        val h6 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_sh)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_sh_check)
            name = listTitle[6]
        }

        val h7 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_diary)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_diary_check)
            name = listTitle[9]
        }
        list.add(h1)
        list.add(h2)
        list.add(h3)
        list.add(h4)
        list.add(h5)
        list.add(h6)
        list.add(h7)
        return list
    }


    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexDataLeft(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        val h0 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = listTitle[0]
        }

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            name = listTitle[1]
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_textbook)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_textbook_check)
            name = listTitle[2]
        }

        val h3 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_video)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_video_check)
            name = listTitle[7]
        }
        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_learn_condition)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_learn_condition_check)
            name = listTitle[8]
        }
        list.add(h0)
        list.add(h1)
        list.add(h2)
        list.add(h3)
        list.add(h4)
        return list
    }


    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexDataRight(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        val h0 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = listTitle[0]
        }

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_homework)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_homework_check)
            name = listTitle[3]
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_paper)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_paper_check)
            name = listTitle[4]
        }

        val h3 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = listTitle[5]
        }

        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_sh)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_sh_check)
            name = listTitle[6]
        }
        list.add(h0)
        list.add(h1)
        list.add(h2)
        list.add(h3)
        list.add(h4)
        return list
    }


    val remind: MutableList<DateRemind>
        get() {
            val list= mutableListOf<DateRemind>()
            for (i in dateRemind) {
                list.add(DateRemind().apply {
                    remind = i.toString() + mContext.getString(R.string.day)
                    remindIn = i
                    isCheck = i == 1
                })
            }
            return list
        }

    val weeks: MutableList<DateWeek>
        get() {
            val list= mutableListOf<DateWeek>()
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_1
                    ), "MO", 2, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_2
                    ), "TU", 3, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_3
                    ), "WE", 4, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_4
                    ), "TH", 5, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_5
                    ), "FR", 6, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_6
                    ), "SA", 7, false
                )
            )
            list.add(
                DateWeek(
                    mContext.getString(
                        R.string.week_7
                    ), "SU", 8, false
                )
            )
            return list
        }


    /**
     * 获取老师下发作业本对应的内容默认背景图
     * @return
     */
    fun getHomeWorkContentStr(courseStr: String, grade: Int,name:String): String {
        var resId=0
        when (courseStr) {
            "语文"-> {
                resId=when(name){
                    "练字作业本"->{
                        getYwLzb()
                    }
                    "作文作业本"->{
                        getYwZwb(grade)
                    }
                    else->{
                        getYwYwb(grade)
                    }
                }
            }
            "数学" -> {
                resId=getSx(grade)
            }
            "英语" -> {
                resId=getYy(grade)
            }
            else -> {
                resId=other(grade)
            }
        }

        return ToolUtils.getImageResStr(mContext, resId)
    }

    //创建语文作业本
    fun createYw(grade: Int): MutableList<ModuleBean> {
        val list= mutableListOf<ModuleBean>()
        if (grade < 4) {
            val moduleBean1 = ModuleBean().apply {
               name = mContext.getString(R.string.homework_type_tzb)
                resId = R.mipmap.icon_homework_module_yw_tzb
                resContentId = R.mipmap.icon_homework_content_yw_tzb_cx
            }
            val moduleBean2 = ModuleBean().apply {
                name = mContext.getString(R.string.homework_type_pyb)
                resId = R.mipmap.icon_homework_module_yw_pyb
                resContentId = R.mipmap.icon_homework_content_yw_pyb_cx
            }
            val moduleBean3 = ModuleBean().apply {
                name = "拼音田字本"
                resId = R.mipmap.icon_homework_module_yw_pytzb
                resContentId = R.mipmap.icon_homework_content_yw_ywb_cx
            }
            val moduleBean4 = ModuleBean().apply {
               name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_module_yw_zwb_cx
                resContentId = R.mipmap.icon_homework_content_yw_zwb_gx
            }
            val moduleBean5 = ModuleBean().apply {
                name = "方格本"
                resId = R.mipmap.icon_note_module_fg_10
                resContentId = R.mipmap.icon_note_content_fg_10
            }
            val moduleBean6 = ModuleBean().apply {
                name = "横格本"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
            list.add(moduleBean3)
            list.add(moduleBean4)
            list.add(moduleBean5)
            list.add(moduleBean6)
        } else if (grade in 4..6) {
            val moduleBean1 = ModuleBean().apply {
                name = "横格本-11mm"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            }
            val moduleBean2 = ModuleBean().apply {
                name = "横格本-9mm"
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            }
            val moduleBean3 = ModuleBean().apply {
                name = "方格本-10mm"
                resId = R.mipmap.icon_note_module_fg_10
                resContentId = R.mipmap.icon_note_content_fg_10
            }
            val moduleBean4 = ModuleBean().apply {
                name = "方格本-8.5mm"
                resId = R.mipmap.icon_note_module_fg_8_5
                resContentId = R.mipmap.icon_note_content_fg_8_5
            }
            val moduleBean5 = ModuleBean().apply {
                name = "作文本-10mm"
                resId = R.mipmap.icon_homework_module_yw_zwb_cx
                resContentId = R.mipmap.icon_homework_content_yw_zwb_gx
            }
            val moduleBean6 = ModuleBean().apply {
                name = "作文本-8.5mm"
                resId = R.mipmap.icon_homework_module_yw_zwb_zx
                resContentId = R.mipmap.icon_homework_content_yw_zwb_zx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
            list.add(moduleBean3)
            list.add(moduleBean4)
            list.add(moduleBean5)
            list.add(moduleBean6)
        } else {
            val moduleBean1 = ModuleBean().apply {
                name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_module_yw_zwb_zx
                resContentId = R.mipmap.icon_homework_content_yw_zwb_zx
            }
            val moduleBean2 = ModuleBean().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_module_yw_ywb
                resContentId = R.mipmap.icon_homework_content_yw_ywb_gx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
        }
        return list
    }

    //创建数学作业本
    fun createSx(grade: Int): MutableList<ModuleBean> {
        val list= mutableListOf<ModuleBean>()
        if (grade < 4) {
            val moduleBean1 = ModuleBean().apply {
                name = "格式数学本"
                resId = R.mipmap.icon_homework_module_sx_cx
                resContentId = R.mipmap.icon_homework_content_sx_sxb_cx
            }
            val moduleBean2 = ModuleBean().apply {
                name = "空白数学本"
                resId = R.mipmap.icon_homework_module_sx_gx
                resContentId = R.mipmap.icon_homework_content_sx_sxb_gx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
        } else if (grade in 4..6) {
            val moduleBean1 = ModuleBean().apply {
                name = "横格本-11mm"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            }
            val moduleBean2 = ModuleBean().apply {
                name = "横格本-9mm"
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            }
            val moduleBean3 = ModuleBean().apply {
                name = "格式数学本"
                resId = R.mipmap.icon_homework_module_sx_cx
                resContentId = R.mipmap.icon_homework_content_sx_sxb_cx
            }
            val moduleBean4 = ModuleBean().apply {
                name = "空白数学本"
                resId = R.mipmap.icon_homework_module_sx_gx
                resContentId = R.mipmap.icon_homework_content_sx_sxb_gx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
            list.add(moduleBean3)
            list.add(moduleBean4)
        } else {
            val moduleBean1 = ModuleBean().apply {
                name = "横格本-11mm"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            }
            val moduleBean2 = ModuleBean().apply {
                name = "横格本-9mm"
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
        }
        return list
    }

    //创建英语作业本
    fun createYy(grade: Int): MutableList<ModuleBean> {
        val list= mutableListOf<ModuleBean>()
        if (grade < 4) {
            val moduleBean1 = ModuleBean().apply {
                name = "英语本-3.5mm"
                resId = R.mipmap.icon_note_module_yy_3_5
                resContentId = R.mipmap.icon_homework_content_yy_yyb_gx
            }
            val moduleBean2 = ModuleBean().apply {
                name = "英语本-3mm"
                resId = R.mipmap.icon_note_module_yy_3
                resContentId = R.mipmap.icon_homework_content_yy_yyb_zx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
        } else if (grade in 4..6) {
            val moduleBean1 = ModuleBean().apply {
                name = "横格本-11mm"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            }
            val moduleBean2 = ModuleBean().apply {
                name = "横格本-9mm"
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            }
            val moduleBean3 = ModuleBean().apply {
                name = "英语本-3.5mm"
                resId = R.mipmap.icon_note_module_yy_3_5
                resContentId = R.mipmap.icon_homework_content_yy_yyb_gx
            }
            val moduleBean4 = ModuleBean().apply {
                name = "英语本-3mm"
                resId = R.mipmap.icon_note_module_yy_3
                resContentId = R.mipmap.icon_homework_content_yy_yyb_zx
            }
            list.add(moduleBean1)
            list.add(moduleBean2)
            list.add(moduleBean3)
            list.add(moduleBean4)
        } else {
            val moduleBean1 = ModuleBean().apply {
                name = "英语本"
                resId = R.mipmap.icon_note_module_yy_3
                resContentId = R.mipmap.icon_homework_content_yy_yyb_zx
            }
            list.add(moduleBean1)
        }
        return list
    }

    //语文语文本
    fun getYwYwb(grade: Int): Int {
        return if (grade<4){
            R.mipmap.icon_homework_content_yw_ywb_cx
             }
            else {
            R.mipmap.icon_homework_content_yw_ywb_gx
        }
    }

    //语文练字本
    fun getYwLzb(): Int {
        return R.mipmap.icon_homework_content_yw_lzb
    }

    //语文作文本
    fun getYwZwb(grade: Int): Int {
        return if (grade<7){
            R.mipmap.icon_homework_content_yw_zwb_gx
        } else {
            R.mipmap.icon_homework_content_yw_zwb_zx
        }
    }

    //数学作业本
    fun getSx(grade: Int): Int {
        return if (grade<4){
            R.mipmap.icon_homework_content_sx_sxb_cx
        }
        else if (grade in 4..6) {
            R.mipmap.icon_homework_content_sx_sxb_gx
        } else {
            R.mipmap.icon_homework_content_sx_sxb_zx
        }
    }

    //英语作业本
    fun getYy(grade: Int): Int {
        return if (grade<7){
            R.mipmap.icon_homework_content_yy_yyb_gx
        } else {
            R.mipmap.icon_homework_content_yy_yyb_zx
        }
    }

    //其他本子
    fun other(grade: Int): Int{
        return if (grade<7){
            R.mipmap.icon_homework_content_other_gx
        } else {
            R.mipmap.icon_homework_content_other_zx
        }
    }

    //日记内容选择
    val diaryModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_11)
                resId = R.mipmap.icon_diary_module_bg_1
                resContentId = R.mipmap.icon_diary_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_9)
                resId = R.mipmap.icon_diary_module_bg_3
                resContentId = R.mipmap.icon_diary_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_10)
                resId = R.mipmap.icon_diary_module_bg_2
                resContentId = R.mipmap.icon_diary_details_bg_2
            })
            list.add(ModuleBean().apply {
                name =mContext.getString(R.string.diary_type_fgb_8_5)
                resId = R.mipmap.icon_diary_module_bg_4
                resContentId = R.mipmap.icon_diary_details_bg_4
            })
            return list
        }

    val freenoteModuleBeans: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_black_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_freenote_bg_1
            })
            return list
        }

    //笔记本内容选择
    val noteModuleBeanBooks: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_11)
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_hgb_9)
                resId = R.mipmap.icon_note_module_hg_9
                resContentId = R.mipmap.icon_note_content_hg_9
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_10)
                resId = R.mipmap.icon_note_module_fg_10
                resContentId = R.mipmap.icon_note_content_fg_10
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.diary_type_fgb_8_5)
                resId = R.mipmap.icon_note_module_fg_8_5
                resContentId = R.mipmap.icon_note_content_fg_8_5
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb)
                resId = R.mipmap.icon_note_module_yy_3_5
                resContentId = R.mipmap.icon_note_content_yy_3_5
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_wxp
                resContentId = R.mipmap.icon_note_content_wxp
            })
            return list
        }

    //书法模板
    val sfModuleBeans: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name="空白本"
                resId = R.drawable.bg_black_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name="方格本"
                resId = R.mipmap.icon_note_module_fg_10
                resContentId = R.mipmap.icon_note_content_fg_10
            })
            list.add(ModuleBean().apply {
                name="横格本"
                resId = R.mipmap.icon_note_module_hg_11
                resContentId = R.mipmap.icon_note_content_hg_11
            })
            list.add(ModuleBean().apply {
                name="英语本"
                resId = R.mipmap.icon_note_module_yy_3_5
                resContentId = R.mipmap.icon_note_content_yy_3_5
            })
            return list
        }

    /**
     * 市场
     */
    val supplys: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(1, mContext.getString(R.string.official_str),true))
            list.add(PopupBean(2,mContext.getString(R.string.thirdParty_str),false))
            return list
        }

    /**
     * 书籍书库分类
     */
    fun bookStoreTypes(): MutableList<ItemList>{
        val list = mutableListOf<ItemList>()
        list.add(ItemList().apply {
            type=1
            desc=mContext.getString(R.string.book_tab_gj)
        })
        list.add(ItemList().apply {
            type=2
            desc=mContext.getString(R.string.book_tab_zrkx)
        })
        list.add(ItemList().apply {
            type=3
            desc=mContext.getString(R.string.book_tab_shkx)
        })
        list.add(ItemList().apply {
            type=4
            desc=mContext.getString(R.string.book_tab_sxkx)
        })
        list.add(ItemList().apply {
            type=5
            desc=mContext.getString(R.string.book_tab_yscn)
        })
        list.add(ItemList().apply {
            type=6
            desc=mContext.getString(R.string.book_tab_ydjk)
        })
        return list
    }

    fun operatingGuideInfo():List<MultiItemEntity>{
        val list= mutableListOf<MultiItemEntity>()
        val types= mutableListOf("一、主页面","二、管理中心","三、作业书籍","四、学习工具")
        val mainStrs= mutableListOf("注册","物理按键功能按钮","状态栏按钮","窗口功能","账户/班群","书架/作业","课本/试卷","视教/笔记","学情/书画")
        val managerStrs= mutableListOf("管理中心","书城","资源","云书库","设置/声音","设置/一键功能","远程在校","我的壁纸","我的钱包")
        val bookStrs= mutableListOf("课本/作业","书籍/作业卷","教辅本/作文本","朗读作业本/错题本","作业提交流程","作业自行批改","测验卷/考卷","日记本/随笔","我的画本/我的书法")
        val toolStrs= mutableListOf("学习计划创建","学习计划列表","我的工具","我的日历","截屏","几何绘图")
        val childTypes= mutableListOf(mainStrs,managerStrs,bookStrs,toolStrs)
        for (type in types){
            val index=types.indexOf(type)
            val catalogParentBean = CatalogParentBean()
            catalogParentBean.title=type
            for (childType in childTypes[index]){
                val catalogChildBean = CatalogChildBean()
                catalogChildBean.title = childType
                catalogChildBean.parentPosition=index
                catalogChildBean.pageNumber = childTypes[index].indexOf(childType)+1
                catalogParentBean.addSubItem(catalogChildBean)
            }
            list.add(catalogParentBean)
        }
        return list
    }

}