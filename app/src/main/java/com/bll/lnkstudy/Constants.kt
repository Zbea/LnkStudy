package com.bll.lnkstudy

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

        ///storage/emulated/0/Android/data/yourPackageName/files/Pictures
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip").path
        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK").path
        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val BOOK_PATH = MyApplication.mContext.getExternalFilesDir("BookFile").path

        val CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        val BOOK_PICTURE_FILES = "contents" //图片资源的最确路径

        //截图保存目录
        val SCREEN_PATH = MyApplication.mContext.getExternalFilesDir("screen").path

        //考卷保存目录
        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("testPaper").path
        //作业保存目录
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("homeWork").path
        //录音保存目录
        val RECORD_PATH = MyApplication.mContext.getExternalFilesDir("record").path
        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("note").path
        //画本保存目录
        val PAINTING_PATH = MyApplication.mContext.getExternalFilesDir("painting").path
        val PATH_SF = "file:///android_asset/sf/"

        //eventbus通知标志
        val DATE_EVENT="DateEvent"
        val BOOK_EVENT="BookEvent"
        val TEXT_BOOK_EVENT="TextBookEvent"
        val AFTER_SCHOOL_EVENT="AfterSchoolEvent"
        val COURSE_EVENT="CourseEvent"
        val NOTE_BOOK_MANAGER_EVENT="NoteBookManagerEvent"
        val NOTE_EVENT="NoteEvent"
        val RECORD_EVENT="RecordEvent"
    }
    
}


