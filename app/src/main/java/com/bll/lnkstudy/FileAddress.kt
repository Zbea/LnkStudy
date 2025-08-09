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
import com.bll.lnkstudy.MethodManager.getAccountId
import java.io.File

class FileAddress {

    fun getLauncherPath():String{
        return  getPathApk("lnkStudy")
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

    fun getPathHomeworkBook(fileName: String):String{
        return "$HOMEWORK_PATH/${getAccountId()}/homeworkBook/$fileName"
    }
    fun getPathHomeworkBookDraw(fileName: String):String{
        return "$HOMEWORK_PATH/${getAccountId()}/homeworkBook/${fileName}/drawContent"
    }

    fun getPathHomeworkBookDrawPath(bookDraw: String,page:Int):String{
        return bookDraw+"/${page+1}"
    }

    /**
     * 教辅本手写地址
     */
    fun getPathHomeworkBookDrawFile(bookDraw: String,page:Int):String{
        return bookDraw+"/${page+1}/draw.png"
    }

    /**
     * 教辅本合图地址
     */
    fun getPathHomeworkBookCorrectFile(bookDraw: String,page:Int):String{
        return bookDraw+"/${page+1}/correct.png"
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
        return "$BOOK_PATH/${getAccountId()}/${fileName}book"
    }
    fun getPathBookPath(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/${fileName}"
    }
    /**
     * 书籍手写地址
     * /storage/emulated/0/Notes
     */
    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/${fileName}/draw"
    }

    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/${fileName}book"
    }
    fun getPathTextBookPath(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/${fileName}"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/${fileName}/draw"
    }
    fun getPathTextBookAnnotation(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/${fileName}/annotation"
    }
    fun getPathTextBookAnnotation(fileName: String,page:Int):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/${fileName}/annotation/${page}"
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
        return "$TESTPAPER_PATH/${getAccountId()}/$course/$categoryId/$contentId"
    }

    /**
     * 测试卷分类路径
     * categoryId分类id
     */
    fun getPathTestPaper(course:String,categoryId:Int):String{
        return "$TESTPAPER_PATH/${getAccountId()}/$course/$categoryId"
    }

    /**
     * 作业文件夹路径
     */
    fun getPathHomework(course:String,typeId:Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/$course/$typeId"
    }

    /**
     * 作业保存路径
     */
    fun getPathHomework(course:String, typeId:Int?, contentId: Int?):String{
        return "$HOMEWORK_PATH/${getAccountId()}/$course/$typeId/$contentId"
    }

    /**
     * 作业保存路径
     */
    fun getPathHomework(course:String, typeId:Int?, contentId: Int?,index:Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/$course/$typeId/$contentId/$index"
    }

    /**
     * 作业手写合图路径
     */
    fun getPathHomeworkDrawingMerge(path:String):String{
        return File(path).parent+"/merge.png"
    }

    /**
     * 自批手写保存路径
     */
    fun getPathHomeworkCorrect(contentId: Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/$contentId"
    }

    /**
     * 自批手写保存路径
     */
    fun getPathHomeworkCorrect(contentId: Int,index: Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/$contentId/$index.png"
    }

    /**
     * 笔记文件夹地址
     */
    fun getPathNote(type: String?,noteBookStr: String?):String{
        return "$NOTE_PATH/${getAccountId()}/$type/$noteBookStr"
    }

    /**
     * 壁纸、书画
     */
    fun getPathImage(type:String, contentId: Int):String{
        return "$IMAGE_PATH/${getAccountId()}/$type/$contentId"
    }

    /**
     * 画本、书法文件夹地址 type=0 画本 type=1 书法
     */
    fun getPathPaintingDraw(type:Int, cloudId: Int):String{
        return "$PAINTING_PATH/${getAccountId()}/${if (type==0) "hb" else "sf" }/$cloudId"
    }

    /**
     * 日历保存地址
     */
    fun getPathDate(dateStr:String):String{
        return "$IMAGE_PATH/${getAccountId()}/date/$dateStr"
    }

    /**
     * 草稿纸
     */
    fun getPathDraft():String{
        return "$IMAGE_PATH/${getAccountId()}/draft"
    }

    /**
     * 随笔文件路径
     */
    fun getPathFreeNote(title:String):String{
        return "$FREE_NOTE_PATH/${getAccountId()}/$title"
    }

    /**
     * 计划总览路径
     */
    fun getPathPlan(year:Int,month:Int):String{
        return "$IMAGE_PATH/${getAccountId()}/month/$year$month"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(startTime:String):String{
        return "$IMAGE_PATH/${getAccountId()}/week/$startTime"
    }

    /**
     * 日记路径
     */
    fun getPathDiary(time:String):String{
        return "$DIARY_PATH/${getAccountId()}/$time"
    }

    /**
     * 截图
     */
    fun getPathScreen(typeStr: String):String{
        return "$SCREEN_PATH/${getAccountId()}/$typeStr"
    }

    /**
     * 截图
     */
    fun getPathScreenHomework(typeStr: String,grade:Int):String{
        return "$SCREEN_PATH/${getAccountId()}/$typeStr(${DataBeanManager.getGradeStr(grade)})"
    }

    /**
     * 日历背景下载地址
     */
    fun getPathCalender(fileName: String):String{
        return "$IMAGE_PATH/${getAccountId()}/calender/$fileName"
    }

    /**
     * 七牛上传记录地址
     */
    fun getPathRecorder():String{
        return "$IMAGE_PATH/${getAccountId()}/recorder"
    }

    fun getPathDocument():String{
        return "$IMAGE_PATH/${getAccountId()}"
    }
    fun getPathDocument(name:String):String{
        return "$IMAGE_PATH/${getAccountId()}/$name"
    }
}