package com.bll.lnkstudy

import android.annotation.SuppressLint
import com.bll.lnkstudy.MyApplication.Companion.mContext
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.date.DateRemind
import com.bll.lnkstudy.mvp.model.date.DateWeek
import com.bll.lnkstudy.utils.ToolUtils
import java.util.*

object DataBeanManager {

    var courses= mutableListOf<String>()
    var classGroups= mutableListOf<ClassGroup>()
    var grades= mutableListOf<Grade>()

    private val listTitle = arrayOf(
        R.string.main_home_title,R.string.main_bookcase_title,
        R.string.main_textbook_title,R.string.main_homework_title,
        R.string.main_testpaper_title,R.string.main_note_title,
        R.string.main_painting_title,R.string.main_teach_title
    )
    val textbookType = arrayOf(
        mContext.getString(R.string.textbook_tab_text),mContext.getString(R.string.textbook_tab_course),
        mContext.getString(R.string.textbook_tab_teaching),mContext.getString(R.string.textbook_tab_oldteaching)
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
    val YEARS = arrayOf(
        R.string.age_han,R.string.age_tang,
        R.string.age_song,R.string.age_yuan,
        R.string.age_ming,R.string.age_qing,
        R.string.age_jin,R.string.age_dan
    )
    val PAINTING = arrayOf(
        R.string.painting_mbsf,R.string.painting_ssh,
        R.string.painting_hnh,R.string.painting_rwh,
        R.string.painting_smh,R.string.painting_ybsf
    )

    val popupGrades: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in grades.indices){
                list.add(PopupBean(grades[i].type, grades[i].desc, i == 0))
            }
            return list
        }

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getIndexData(): MutableList<MainList> {
        val list = mutableListOf<MainList>()
        val h0 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sy_check)
            checked = true
            name = mContext.getString(listTitle[0])
        }

        val h1 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sj_check)
            checked = false
            name = mContext.getString(listTitle[1])
        }

        val h2 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_kb)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_kb_check)
            checked = false
            name = mContext.getString(listTitle[2])
        }

        val h3 = MainList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_main_zy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_zy_check)
            checked = false
            name = mContext.getString(listTitle[3])
        }

        val h4 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_ks)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_ks_check)
            checked = false
            name = mContext.getString(listTitle[4])
        }

        val h5 = MainList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_main_bj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_bj_check)
            checked = false
            name = mContext.getString(listTitle[5])
        }

        val h6 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sh)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sh_check)
            checked = false
            name = mContext.getString(listTitle[6])
        }

        val h7 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_yj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_yj_check)
            checked = false
            name = mContext.getString(listTitle[7])
        }

        list.add(h0)
        list.add(h1)
        list.add(h2)
        list.add(h3)
        list.add(h4)
        list.add(h5)
        list.add(h6)
        list.add(h7)
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

    val message: MutableList<MessageList>
        get() {
            val list= mutableListOf<MessageList>()
            list.add(MessageList().apply {
                name = "语文周老师"
                createTime = "2020-6-2"
                content = "上交语文作业"
            })
            list.add(MessageList().apply {
                name = "数学老师"
                createTime = "2020-6-2"
                content ="上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业上交语文作业"
            })
            list.add(MessageList().apply {
                name = "妈妈"
                createTime = "2020-6-2"
                content = "回家吃饭"
            })
            return list
        }


    /**
     * 获取老师下发作业本对应的内容默认背景图
     * @return
     */
    fun getHomeWorkContentStr(courseStr: String, grade: Int): String {
        val resId = when (courseStr) {
            "语文"-> {
                if (grade < 7) {
                    R.mipmap.icon_homework_other_xxlxb
                } else {
                    R.mipmap.icon_homework_other_lxb
                }
            }
            "数学" -> {
                if (grade < 7) {
                    R.mipmap.icon_homework_sx_sxb
                } else {
                    R.mipmap.icon_homework_other_lxb
                }
            }
            "英语" -> {
                if (grade < 7) {
                    R.mipmap.icon_homework_yy_xxyyb
                } else {
                    R.mipmap.icon_homework_yy_zxyyb
                }
            }
            else -> {
                R.mipmap.icon_homework_other_lxb
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
                resId = R.mipmap.icon_homework_yw_pytzb_1
                resContentId = R.mipmap.icon_homework_yw_pytzb
            }
            val module1 = Module().apply {
               name = mContext.getString(R.string.homework_type_tzb)
                resId = R.mipmap.icon_homework_yw_tzb_1
                resContentId = R.mipmap.icon_homework_yw_tzb
            }
            val module2 = Module().apply {
                name = mContext.getString(R.string.homework_type_pyb)
                resId = R.mipmap.icon_homework_yw_pyb_1
                resContentId = R.mipmap.icon_homework_yw_pyb
            }
            val module3 = Module().apply {
               name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_yw_zwb_1
                resContentId = R.mipmap.icon_homework_yw_zwb
            }
            val module4 = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_xxlxb
            }
            list.add(module)
            list.add(module1)
            list.add(module2)
            list.add(module3)
            list.add(module4)
        } else if (grade in 4..6) {
            val module = Module().apply {
                name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_yw_zwb_1
                resContentId = R.mipmap.icon_homework_yw_zwb
            }
            val module1 = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_xxlxb
            }
            list.add(module)
            list.add(module1)
        } else {
            val module = Module().apply {
                name = mContext.getString(R.string.homework_type_lxb)
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_lxb
            }
            val module1 = Module().apply {
                name = mContext.getString(R.string.homework_type_zwb)
                resId = R.mipmap.icon_homework_yw_zwb_1
                resContentId = R.mipmap.icon_homework_yw_zxzwb
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
                resId = R.mipmap.icon_homework_sx_sxb_1
                resContentId = R.mipmap.icon_homework_sx_sxb
            })
        } else {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_sxb)
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_lxb
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
                resId = R.mipmap.icon_homework_yy_xxyyb_1
                resContentId = R.mipmap.icon_homework_yy_xxyyb
            })
        } else {
            list.add(Module().apply {
                name = mContext.getString(R.string.homework_type_yyb)
                resId = R.mipmap.icon_homework_yy_zxyyb_1
                resContentId = R.mipmap.icon_homework_yy_zxyyb
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
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_lxb
            })
            return list
        }

    //封面
    val homeworkCover: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                resId = R.mipmap.icon_homework_cover_1
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_homework_cover_2
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_homework_cover_3
            })
            list.add(Module().apply {
                resId = R.mipmap.icon_homework_cover_4
            })
            return list
        }

    /**
     * 老师下发作业本随机得到背景图
     */
    fun getHomeworkCoverStr(): String {
        val covers = homeworkCover
        val index = Random().nextInt(covers.size)
        return ToolUtils.getImageResStr(mContext, covers[index].resId)
    }

    val appBaseList: List<AppBean>
        get() {
            val apps= mutableListOf<AppBean>()
            apps.add(AppBean().apply {
                appId = 0
                appName = mContext.getString(R.string.download_app)
                image = mContext.getDrawable(R.mipmap.icon_app_center)
                isBase = true
            })
            apps.add(AppBean().apply {
                appId = 1
                appName = mContext.getString(R.string.download_wallpaper)
                image = mContext.getDrawable(R.mipmap.icon_app_wallpaper)
                isBase = true
            })
            apps.add(AppBean().apply {
                appId = 2
                appName = mContext.getString(R.string.download_painting)
                image = mContext.getDrawable(R.mipmap.icon_app_painting)
                isBase = true
            })
            return apps
        }

    //基础笔记分类
    val noteBook: MutableList<BaseTypeBean>
        get() {
            val list= mutableListOf<BaseTypeBean>()
            list.add(BaseTypeBean().apply {
                name = mContext.getString(R.string.note_tab_diary)
                typeId = 0
            })
            list.add(BaseTypeBean().apply {
                name = mContext.getString(R.string.note_tab_article)
                typeId = 1
            })
            list.add(BaseTypeBean().apply {
                name = mContext.getString(R.string.note_tab_topic)
                typeId = 2
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
                resContentId = R.mipmap.icon_note_details_bg_6
            })
            list.add(Module().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_7
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


    //学期选择
    val semesters: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(0, mContext.getString(R.string.semester_last),true))
            list.add(PopupBean(1,mContext.getString(R.string.semester_next),false))
            return list
        }

}