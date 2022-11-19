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

            val PAGE_SIZE=12
            val WIDTH=1404
            val HEIGHT=1872

        const val URL_BASE = "https://api2.bailianlong.com/v1/"
//        const val URL_BASE = "http://192.168.101.187:10800/v1/"
        val AUTH = "Authorization"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip").path
        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK").path
        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val BOOK_PATH = MyApplication.mContext.getExternalFilesDir("BookFile").path

        val CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        val BOOK_PICTURE_FILES = "contents" //图片资源的最确路径

        //截图保存目录
        val SCREEN_PATH = MyApplication.mContext.getExternalFilesDir("Screen").path

        //收到题卷地址
        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("TestPaper").path
        //作业保存目录
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("HomeWork").path
        //录音保存目录
        val RECORD_PATH = MyApplication.mContext.getExternalFilesDir("Record").path
        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("Note").path
        //画本保存目录
        val PAINTING_PATH = MyApplication.mContext.getExternalFilesDir("Painting").path
        //壁纸、书画目录
        val IMAGE_PATH = MyApplication.mContext.getExternalFilesDir("Image").path

        val PATH_SF = "file:///android_asset/sf/"

        //eventbus通知标志
        val AUTO_UPLOAD_EVENT="AutoUploadEvent"
        val DATE_EVENT="DateEvent"
        val BOOK_EVENT="BookEvent"//书籍下载通知
        val TEXT_BOOK_EVENT="TextBookEvent"//课本下载通知
        val BOOK_HOMEWORK_EVENT="BookHomeworkEvent"//作业下载通知
        val APP_EVENT="APPEvent"//应用下载安装通知
        val COURSE_EVENT="CourseEvent"
        val NOTE_BOOK_MANAGER_EVENT="NoteBookManagerEvent"
        val NOTE_EVENT="NoteEvent"
        val RECORD_EVENT="RecordEvent"
        val RECEIVE_PAPER_COMMIT_EVENT="ReceivePaperCommit"
        val SCREEN_EVENT="AutoScreenEvent"
    }
    
}


