package com.bll.lnkstudy

import android.annotation.SuppressLint
import com.bll.lnkstudy.MyApplication.Companion.mContext
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.utils.ToolUtils

object DataBeanManager {

    var courses= mutableListOf<String>()
    var grades= mutableListOf<Grade>()

    private val listTitle = arrayOf(
        "首页",
        "书架",
        "课本",
        "作业",
        "考卷",
        "笔记",
        "书画",
        "义教"
    )
    val bookStoreType = arrayOf(
        "教材",
        "古籍",
        "自然科学",
        "社会科学",
        "思维科学",
        "运动才艺"
    )
    val textbookType = arrayOf(
        "我的课本",
        "我的课辅",
        "参考教材",
        "往期教材"
    )
    private val dateRemind = arrayOf(1, 3, 5, 7, 10, 15)
    val bookType = arrayOf(
        "诗经楚辞", "唐诗宋词", "古代经典",
        "四大名著", "中国科技", "小说散文",
        "外国原著", "历史地理", "政治经济",
        "军事战略", "科学技术", "运动才艺"
    ) //书籍分类
    val YEARS = arrayOf(
        "汉朝", "唐朝", "宋朝", "元朝", "明朝", "清朝", "近代", "当代"
    )
    val PAINTING = arrayOf(
        "毛笔书法", "山水画", "花鸟画", "人物画", "素描画", "硬笔书法"
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
            name = listTitle[0]
        }

        val h1 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sj_check)
            checked = false
            name = listTitle[1]
        }

        val h2 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_kb)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_kb_check)
            checked = false
            name = listTitle[2]
        }

        val h3 = MainList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_main_zy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_zy_check)
            checked = false
            name = listTitle[3]
        }

        val h4 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_ks)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_ks_check)
            checked = false
            name = listTitle[4]
        }

        val h5 = MainList().apply {
           icon = mContext.getDrawable(R.mipmap.icon_main_bj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_bj_check)
            checked = false
            name = listTitle[5]
        }

        val h6 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sh)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sh_check)
            checked = false
            name = listTitle[6]
        }

        val h7 = MainList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_yj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_yj_check)
            checked = false
            name = listTitle[7]
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
                    remind = i.toString() + "天"
                    remindIn = i
                    isCheck = i == 1
                })
            }
            return list
        }

    val weeks: MutableList<DateWeek>
        get() {
            val list= mutableListOf<DateWeek>()
            list.add(DateWeek("周一", "MO", 2, false))
            list.add(DateWeek("周二", "TU", 3, false))
            list.add(DateWeek("周三", "WE", 4, false))
            list.add(DateWeek("周四", "TH", 5, false))
            list.add(DateWeek("周五", "FR", 6, false))
            list.add(DateWeek("周六", "SA", 7, false))
            list.add(DateWeek("周日", "SU", 8, false))
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
     * 作业分类列表
     *
     * @return
     */
    fun getHomeWorkTypes(courseStr: String, grade: Int): MutableList<HomeworkTypeBean> {
        val resId = when (courseStr) {
            "语文" -> {
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

        val list= ArrayList<HomeworkTypeBean>()
        list.add(HomeworkTypeBean().apply {
            name = "课堂作业本"
            typeId = 0
            course = courseStr
            bgResId = ToolUtils.getImageResStr(mContext, R.mipmap.icon_homework_cover_1)
            contentResId = ToolUtils.getImageResStr(mContext, resId)
        })
        list.add(HomeworkTypeBean().apply {
            name = "课外作业本"
            typeId = 1
            course = courseStr
            bgResId = ToolUtils.getImageResStr(mContext, R.mipmap.icon_homework_cover_2)
            contentResId = ToolUtils.getImageResStr(mContext, resId)
        })
        list.add(HomeworkTypeBean().apply {
            name = "课堂题卷本"
            typeId = 2
            state = 2
            course = courseStr
            bgResId = ToolUtils.getImageResStr(mContext, R.mipmap.icon_homework_cover_3)
            contentResId = ToolUtils.getImageResStr(mContext, resId)
        })

        if (courseStr == "语文" || courseStr == "英语") {
            list.add(HomeworkTypeBean().apply {
                name = "课堂题卷本"
                typeId = 3
                state = 1
                course = courseStr
                bgResId = ToolUtils.getImageResStr(mContext, R.mipmap.icon_homework_cover_4)
            })
        }
        return list
    }

    //语文作业本
    fun getYw(grade: Int): MutableList<Module> {
        val list= mutableListOf<Module>()
        if (grade <= 3) {
            val module = Module().apply {
                name = "拼音田字本"
                resId = R.mipmap.icon_homework_yw_pytzb_1
                resContentId = R.mipmap.icon_homework_yw_pytzb
            }
            val module1 = Module().apply {
               name = "田字本"
                resId = R.mipmap.icon_homework_yw_tzb_1
                resContentId = R.mipmap.icon_homework_yw_tzb
            }
            val module2 = Module().apply {
                name = "拼音本"
                resId = R.mipmap.icon_homework_yw_pyb_1
                resContentId = R.mipmap.icon_homework_yw_pyb
            }
            val module3 = Module().apply {
               name = "作文本"
                resId = R.mipmap.icon_homework_yw_zwb_1
                resContentId = R.mipmap.icon_homework_yw_zwb
            }
            val module4 = Module().apply {
                name = "练习本"
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
                name = "作文本"
                resId = R.mipmap.icon_homework_yw_zwb_1
                resContentId = R.mipmap.icon_homework_yw_zwb
            }
            val module1 = Module().apply {
                name = "练习本"
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_xxlxb
            }
            list.add(module)
            list.add(module1)
        } else {
            val module = Module().apply {
                name = "练习本"
                resId = R.mipmap.icon_homework_other_lxb_1
                resContentId = R.mipmap.icon_homework_other_lxb
            }
            val module1 = Module().apply {
                name = "作文本"
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
                name = "数学本"
                resId = R.mipmap.icon_homework_sx_sxb_1
                resContentId = R.mipmap.icon_homework_sx_sxb
            })
        } else {
            list.add(Module().apply {
                name = "数学本"
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
                name = "英语本"
                resId = R.mipmap.icon_homework_yy_xxyyb_1
                resContentId = R.mipmap.icon_homework_yy_xxyyb
            })
        } else {
            list.add(Module().apply {
                name = "英语本"
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
                name = "练习本"
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

    val appBaseList: List<AppBean>
        get() {
            val apps= mutableListOf<AppBean>()
            apps.add(AppBean().apply {
                appId = 0
                appName = "应用"
                image = mContext.getDrawable(R.mipmap.icon_app_center)
                isBase = true
            })
            apps.add(AppBean().apply {
                appId = 1
                appName = "壁纸"
                image = mContext.getDrawable(R.mipmap.icon_app_wallpaper)
                isBase = true
            })
            apps.add(AppBean().apply {
                appId = 2
                appName = "书画"
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
                name = "我的日记"
                typeId = 0
            })
            list.add(BaseTypeBean().apply {
                name = "金句彩段"
                typeId = 1
            })
            list.add(BaseTypeBean().apply {
                name = "典型题型"
                typeId = 2
            })
            return list
        }

    //日记内容选择
    val noteModuleDiary: MutableList<Module>
        get() {
            val list= mutableListOf<Module>()
            list.add(Module().apply {
                name = "横格本"
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_6
            })
            list.add(Module().apply {
                name = "方格本"
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
                name = "空白本"
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(Module().apply {
                name = "横格本"
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(Module().apply {
                name = "方格本"
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(Module().apply {
                name = "英语本"
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(Module().apply {
                name = "田字本"
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(Module().apply {
                name = "五线谱"
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
            })
            return list
        }

}