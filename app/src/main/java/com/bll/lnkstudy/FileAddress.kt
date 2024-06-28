package com.bll.lnkstudy

import com.bll.lnkstudy.Constants.Companion.BOOK_DRAW_PATH
import com.bll.lnkstudy.Constants.Companion.BOOK_PATH
import com.bll.lnkstudy.Constants.Companion.DIARY_PATH
import com.bll.lnkstudy.Constants.Companion.FREE_NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkstudy.Constants.Companion.IMAGE_PATH
import com.bll.lnkstudy.Constants.Companion.NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.PAINTING_PATH
import com.bll.lnkstudy.Constants.Companion.SCREEN_PATH
import com.bll.lnkstudy.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_CATALOG_TXT
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_CONTENTS
import com.bll.lnkstudy.Constants.Companion.ZIP_PATH
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.SPUtil
import java.io.File

class FileAddress {

    private val mUserId=SPUtil.getObj("user", User::class.java)?.accountId.toString()


    fun getPathHomeworkBook(fileName: String):String{
        return "$HOMEWORK_PATH/$mUserId/homeworkBook/$fileName"
    }
    fun getPathHomeworkBookDraw(fileName: String):String{
        return "$HOMEWORK_PATH/$mUserId/homeworkBook/${fileName}/drawContent"
    }

    /**
     * 教材目录地址
     */
    fun getPathTextbookCatalog(path:String):String{
        return path + File.separator + TEXTBOOK_CATALOG_TXT
    }
    /**
     * 教材图片地址
     */
    fun getPathTextbookPicture(path:String):String{
        return path + File.separator + TEXTBOOK_CONTENTS
    }

    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return ZIP_PATH+File.separator + fileName + ".zip"
    }

    /**
     * 书籍地址
     * /storage/emulated/0/Books
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/$mUserId/$fileName"
    }
    /**
     * 书籍手写地址
     * /storage/emulated/0/Notes
     */
    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_DRAW_PATH/$mUserId/$fileName"
    }

    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return Constants.APK_PATH+ File.separator + fileName + ".apk"
    }

    /**
     * 测试卷下载地址
     * categoryId分类id contentId内容id
     */
    fun getPathTestPaper(categoryId:Int,contentId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/testPaperType$categoryId/$contentId"
    }

    /**
     * 测试卷分类路径
     * categoryId分类id
     */
    fun getPathTestPaper(categoryId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/testPaperType$categoryId"
    }

    /**
     * 考试卷下载地址
     */
    fun getPathExam(categoryId:Int,contentId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/examType$categoryId/$contentId"
    }

    /**
     * 作业文件夹路径
     */
    fun getPathHomework(course:String, typeId:Int):String{
        return "$HOMEWORK_PATH/$mUserId/$course/$typeId"
    }
    /**
     * 朗读作业文件夹路径
     */
    fun getPathRecord(course:String, typeId:Int):String{
        return "$HOMEWORK_PATH/$mUserId/$course/$typeId"
    }
    /**
     * 作业保存路径
     */
    fun getPathHomework(course:String, typeId:Int?, contentId: Int?):String{
        return "$HOMEWORK_PATH/$mUserId/$course/$typeId/$contentId"
    }

    /**
     * 笔记文件夹地址
     */
    fun getPathNote(grade:Int,type: String?,noteBookStr: String?):String{
        return "$NOTE_PATH/$mUserId/$grade/$type/$noteBookStr"
    }

    /**
     * 笔记保存地址（详情）
     */
    fun getPathNote(grade:Int,type: String?,noteBookStr: String?,date:Long):String{
        return "$NOTE_PATH/$mUserId/$grade/$type/$noteBookStr/$date"
    }

    /**
     * 壁纸、书画
     */
    fun getPathImage(type:String, contentId: Int):String{
        return "$IMAGE_PATH/$mUserId/$type/$contentId"
    }

    /**
     * 画本、书法文件夹地址
     */
    fun getPathPainting(type:Int, grade: Int):String{
        return "$PAINTING_PATH/$mUserId/$type/$grade"
    }

    /**
     * 画本、书法内容具体路径
     */
    fun getPathPainting(type:Int, grade: Int,date: Long):String{
        return "$PAINTING_PATH/$mUserId/$type/$grade/$date"
    }

    /**
     * 日历保存地址
     */
    fun getPathDate(dateStr:String):String{
        return "$IMAGE_PATH/${mUserId}/date/$dateStr"
    }

    /**
     * 草稿纸
     */
    fun getPathDraft():String{
        return "$IMAGE_PATH/$mUserId/draft"
    }

    /**
     * 随笔文件路径
     */
    fun getPathFreeNote(title:String):String{
        return "$FREE_NOTE_PATH/${mUserId}/$title"
    }

    /**
     * 计划总览路径
     */
    fun getPathPlan(year:Int,month:Int):String{
        return "$IMAGE_PATH/${mUserId}/month/$year$month"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(startTime:String):String{
        return "$IMAGE_PATH/${mUserId}/week/$startTime"
    }

    /**
     * 日记路径
     */
    fun getPathDiary(time:String):String{
        return "$DIARY_PATH/${mUserId}/$time"
    }

    /**
     * 截图
     */
    fun getPathScreen(typeStr: String):String{
        return "$SCREEN_PATH/${mUserId}/$typeStr"
    }
    /**
     * 截图手写
     */
    fun getPathScreenDrawing(typeStr: String,index:Int):String{
        return "$SCREEN_PATH/${mUserId}/$typeStr/drawing/$index.tch"
    }
    /**
     * 截图
     */
    fun getPathScreenHomework(typeStr: String,grade:Int):String{
        return "$SCREEN_PATH/${mUserId}/$typeStr(${DataBeanManager.getGradeStr(grade)})"
    }

    /**
     * 日历背景下载地址
     */
    fun getPathCalender(fileName: String):String{
        return "$IMAGE_PATH/${mUserId}/calender/$fileName"
    }

    /**
     * 七牛上传记录地址
     */
    fun getPathRecorder():String{
        return "$IMAGE_PATH/${mUserId}/recorder"
    }

    /**
     * 系统更新地址
     */
    fun getPathSystemUpdate(fileName: String):String{
        return "/data/media/0/update${fileName}.zip"
    }
}