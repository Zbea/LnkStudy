package com.bll.lnkstudy

import com.bll.lnkstudy.Constants.Companion.BOOK_PATH
import com.bll.lnkstudy.Constants.Companion.BOOK_PICTURE_FILES
import com.bll.lnkstudy.Constants.Companion.CATALOG_TXT
import com.bll.lnkstudy.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkstudy.Constants.Companion.IMAGE_PATH
import com.bll.lnkstudy.Constants.Companion.NOTE_PATH
import com.bll.lnkstudy.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkstudy.Constants.Companion.ZIP_PATH
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.utils.SPUtil
import java.io.File

class FileAddress {

    val mUserId=SPUtil.getObj("user", User::class.java)?.accountId.toString()

    /**
     * 书籍地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/BookFile/mUserId/fileName
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/$mUserId/$fileName"
    }

    /**
     * 书籍目录地址
     */
    fun getPathBookCatalog(path:String):String{
        return path + File.separator + CATALOG_TXT
    }
    /**
     * 书籍图片地址
     */
    fun getPathBookPicture(path:String):String{
        return path + File.separator + BOOK_PICTURE_FILES
    }
    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return ZIP_PATH+File.separator + fileName + ".zip"
    }

    /**
     * 书籍zip解压地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/BookFile/mUserId/fileName
     */
    fun getPathBookUnzip(fileName:String):String{
        val unzipTargetFile = File(BOOK_PATH, mUserId)
        if (!unzipTargetFile.exists()) {
            unzipTargetFile.mkdir()
        }
        return unzipTargetFile.path + File.separator + fileName
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
        return "$TESTPAPER_PATH/$mUserId/$categoryId/$contentId"
    }

    /**
     * 作业文件夹路径
     */
    fun getPathHomework(course:String, typeId:Int):String{
        return "$HOMEWORK_PATH/$mUserId/$course/$typeId"
    }
    /**
     * 作业保存路径
     */
    fun getPathHomework(course:String, typeId:Int?, contentId: Int?):String{
        return "$HOMEWORK_PATH/$mUserId/$course/$typeId/$contentId"
    }

    /**
     * 笔记保存地址
     */
    fun getPathNote(typeId: Int?,noteBookId: Long?,index:Int):String{
        return "$NOTE_PATH/$mUserId/$typeId/$noteBookId/$index"
    }

    /**
     * 壁纸、书画
     */
    fun getPathImage(type:String, contentId: Int):String{
        return "$IMAGE_PATH/$mUserId/$type/$contentId"
    }

    /**
     * 草稿纸
     */
    fun getPathDraft():String{
        return "$IMAGE_PATH/$mUserId/draft"
    }

    /**
     * 校园模式草稿区
     */
    fun getPathCampusDraft():String{
        return "$IMAGE_PATH/$mUserId/campus"
    }

}