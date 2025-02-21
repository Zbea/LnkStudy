package com.bll.lnkstudy

import com.bll.lnkstudy.Constants.Companion.APK_PATH
import com.bll.lnkstudy.Constants.Companion.BOOK_PATH
import com.bll.lnkstudy.Constants.Companion.DIARY_PATH
import com.bll.lnkstudy.Constants.Companion.FREE_NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkstudy.Constants.Companion.IMAGE_PATH
import com.bll.lnkstudy.Constants.Companion.NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.PAINTING_PATH
import com.bll.lnkstudy.Constants.Companion.SCREEN_PATH
import com.bll.lnkstudy.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_PATH
import com.bll.lnkstudy.Constants.Companion.ZIP_PATH
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.SPUtil

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
    fun getPathBookCatalog(path:String):String{
        return "$path/catalog.txt"
    }
    /**
     * 教材图片地址
     */
    fun getPathBookPicture(path:String):String{
        return "$path/contents"
    }

    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return "$ZIP_PATH/$fileName.zip"
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
        return "$BOOK_PATH/$mUserId/${fileName}draw"
    }

    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/${fileName}draw"
    }

    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return "$APK_PATH/$fileName.apk"
    }

    /**
     * 测试卷下载地址
     * categoryId分类id contentId内容id
     */
    fun getPathTestPaper(course:String,categoryId:Int,contentId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/$course/$categoryId/$contentId"
    }

    /**
     * 测试卷分类路径
     * categoryId分类id
     */
    fun getPathTestPaper(course:String,categoryId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/$course/$categoryId"
    }

    /**
     * 作业文件夹路径
     */
    fun getPathHomework(course:String,typeId:Int):String{
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
    fun getPathNote(type: String?,noteBookStr: String?):String{
        return "$NOTE_PATH/$mUserId/$type/$noteBookStr"
    }

    /**
     * 壁纸、书画
     */
    fun getPathImage(type:String, contentId: Int):String{
        return "$IMAGE_PATH/$mUserId/$type/$contentId"
    }

    /**
     * 画本、书法文件夹地址 type=0 画本 type=1 书法
     */
    fun getPathPaintingDraw(type:Int, cloudId: Int):String{
        return "$PAINTING_PATH/$mUserId/${if (type==0) "hb" else "sf" }/$cloudId"
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