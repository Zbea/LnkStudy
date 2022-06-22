package com.bll.lnkstudy

import android.os.Environment

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛
/**
 * desc: 常量  分辨率为 1404x1872，屏幕尺寸为 10.3
 */
class Constants private constructor() {

    companion object {

        const val URL_BASE = "https://api2.bailianlong.com/v1/"
        val AUTH = "Authorization"

        //zip文件的目录
        val ZIP_FILE = Environment.DIRECTORY_PICTURES
        //apk文件的目录
        val APK_FILE = "APK"
        //zip解压后的目录
        val ZIP_BOOK_FILE = "BookFile"
        ///storage/emulated/0/Android/data/yourPackageName/files/Pictures
        val TEXTBOOK_PATH = MyApplication.mContext.getExternalFilesDir(ZIP_FILE).path
        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir(APK_FILE).path
        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val ZIP_BOOK_PATH = MyApplication.mContext.getExternalFilesDir(ZIP_BOOK_FILE).path

        //截图保存目录
        val SCREEN_PATH = MyApplication.mContext.getExternalFilesDir("Screen").path

        val CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        val PICTURE_FILES = "contents" //图片资源的最确路径

        val PATH_SF = "file:///android_asset/sf/"

        //eventbus通知标志
        val DATE_EVENT="DateEvent"
        val BOOK_EVENT="BookEvent"
        val TEXT_BOOK_EVENT="TextBookEvent"
        val AFTER_SCHOOL_EVENT="AfterSchoolEvent"
        val COURSE_EVENT="CourseEvent"
        val NOTE_BOOK_MANAGER_EVENT="NoteBookManagerEvent"
    }
    
}


