package com.bll.lnkstudy

import com.bll.lnkstudy.Constants.Companion.BOOK_DRAW_PATH
import com.bll.lnkstudy.Constants.Companion.BOOK_PATH
import com.bll.lnkstudy.Constants.Companion.FREE_NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkstudy.Constants.Companion.IMAGE_PATH
import com.bll.lnkstudy.Constants.Companion.NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.PAINTING_PATH
import com.bll.lnkstudy.Constants.Companion.RECORDER_PATH
import com.bll.lnkstudy.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_CATALOG_TXT
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_CONTENTS
import com.bll.lnkstudy.Constants.Companion.TEXTBOOK_PATH
import com.bll.lnkstudy.Constants.Companion.ZIP_PATH
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.SPUtil
import java.io.File

class FileAddress {

    private val mUserId=SPUtil.getObj("user", User::class.java)?.accountId.toString()

    /**
     * 教材地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/BookFile/mUserId/fileName
     */
    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/${fileName}/drawContent"
    }

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
        return "$BOOK_PATH/$fileName"
    }
    /**
     * 书籍手写地址
     * /storage/emulated/0/Notes
     */
    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_DRAW_PATH/$fileName"
    }

    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return Constants.APK_PATH+ File.separator + fileName + ".apk"
    }

    /**
     * 考卷下载地址
     * categoryId分类id contentId内容id
     */
    fun getPathTestPaper(categoryId:Int,contentId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/examType$categoryId/$contentId"
    }

    /**
     * 考卷分类路径
     * categoryId分类id
     */
    fun getPathTestPaper(categoryId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/examType$categoryId"
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
     * 草稿纸
     */
    fun getPathDraft():String{
        return "$IMAGE_PATH/$mUserId/draft"
    }

    /**
     * 文件夹路径
     */
    fun getPathFreeRecord():String{
        return "$RECORDER_PATH/${mUserId}"
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
        return "$IMAGE_PATH/${mUserId}/diary/$time"
    }

}