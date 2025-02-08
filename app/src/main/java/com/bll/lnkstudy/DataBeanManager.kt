package com.bll.lnkstudy

import com.bll.lnkstudy.MyApplication.Companion.mContext
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.date.DateRemind
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.utils.ToolUtils
import java.util.*

object DataBeanManager {

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
    var resources = arrayOf("实用工具","锁屏壁纸","历代书画","跳页日历")

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
                list.add(PopupBean(typeGrades[i].type, typeGrades[i].desc, i == popupTypePos()))
            }
            return list
        }

    /**
     * 获取位置
     */
    fun popupTypePos(): Int
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
    fun getHomeWorkContentStr(courseStr: String, grade: Int): String {
        val resId = when (courseStr) {
            "语文"-> {
                if (grade >6) {
                    R.mipmap.icon_homework_content_other_zxlxb
                }
                else if (grade<4){
                    R.mipmap.icon_homework_content_yw_tzb
                }
                else {
                    R.mipmap.icon_homework_content_other_lxb
                }
            }
            "数学" -> {
                if (grade < 7) {
                    R.mipmap.icon_homework_content_sx_sxb
                } else {
                    R.mipmap.icon_homework_content_other_zxlxb
                }
            }
            "英语" -> {
                if (grade < 7) {
                    R.mipmap.icon_homework_content_yy_yyb
                } else {
                    R.mipmap.icon_homework_content_yy_zxyyb
                }
            }
            else -> {
                R.mipmap.icon_homework_content_other_lxb
            }
        }

        return ToolUtils.getImageResStr(mContext, resId)
    }

    //语文作业本
    fun getYw(grade: Int): MutableList<Module> {
        val list= mutableListOf<Module>()
        if (grade <= 3) {
            val module = Module().apply {
                name = mContext.getString(R.string.homework_type_pytzb)
                resId = R.mipmap.icon_homework_module_yw_pytzb
                resContentId = R.mipmap.icon_homework_content_yw_pytzb
            }
            val module1 = Module().apply {
               name = mContext.getString(R.string.homework_type_tzb)
                resId = R.mipmap.icon_homework_module_yw_tzb
                resContentId = R.mipmap.icon_homework_content_yw_tzb
            }
            val module2 = Module().apply {
                name = mContext.getString(R.string.homework_type_pyb)
                resId = R.mipmap.icon_homework_module_yw_pyb
                resContentId = R.mipmap.icon_homework_content_yw_pyb
            }
            val module3 = Module().apply {
               name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_module_yw_zwb
                resContentId = R.mipmap.icon_homework_content_yw_zwb
            }
            val module4 = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_module_other_lxb
                resContentId = R.mipmap.icon_homework_content_other_lxb
            }
            list.add(module)
            list.add(module1)
            list.add(module2)
            list.add(module3)
            list.add(module4)
        } else if (grade in 4..6) {
            val module = Module().apply {
                name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_module_yw_zwb
                resContentId = R.mipmap.icon_homework_content_yw_zwb
            }
            val module1 = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_module_other_lxb
                resContentId = R.mipmap.icon_homework_content_other_lxb
            }
            list.add(module)
            list.add(module1)
        } else {
            val module = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_module_other_lxb
                resContentId = R.mipmap.icon_homework_content_other_zxlxb
            }
            val module1 = Module().apply {
                name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_module_yw_zwb
                resContentId = R.mipmap.icon_homework_content_yw_zxzwb
            }
            list.add(module1)
            list.add(module)
        }
        return list
    }

    //数学作业本
    fun getSx(grade: Int): MutableList<Module> {
        val list=mutableListOf<Module>()
        if (grade < 7) {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_sxb)
                resId = R.mipmap.icon_homework_module_sx_sxb
                resContentId = R.mipmap.icon_homework_content_sx_sxb
            })
        } else {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_sxb)
                resId = R.mipmap.icon_homework_module_other_lxb
                resContentId = R.mipmap.icon_homework_content_other_zxlxb
            })
        }
        return list
    }

    //英语作业本
    fun getYy(grade: Int): MutableList<Module> {
        val list=mutableListOf<Module>()
        if (grade < 7) {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_yyb)
                resId = R.mipmap.icon_homework_module_yy_yyb
                resContentId = R.mipmap.icon_homework_content_yy_yyb
            })
        } else {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_yyb)
                resId = R.mipmap.icon_homework_module_yy_yyb
                resContentId = R.mipmap.icon_homework_content_yy_zxyyb
            })
        }
        return list
    }

    //其他本子
    val other: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_module_other_lxb
                resContentId = R.mipmap.icon_homework_content_other_lxb
            })
            return list
        }

    //日记内容选择
    val noteModuleDiary: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_diary_details_bg_1
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_diary_details_bg_2
            })
            return list
        }

    val freenoteModules: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_freenote_bg_1
            })
            return list
        }

    //笔记本内容选择
    val noteModuleBook: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_yyb)
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_tzb)
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
            })
            return list
        }

    //书法模板
    val sfModule: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
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

}