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
class Constants{

    companion object {

            //44 50
        const val PAGE_SIZE = 12
        const val WIDTH = 1404
        const val HEIGHT = 1872

//        const val URL_BASE = "https://api2.bailianlong.com/v1/"
        const val URL_BASE = "http://192.168.101.10:10800/v1/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH: String = MyApplication.mContext.getExternalFilesDir("Zip")!!.path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH: String = MyApplication.mContext.getExternalFilesDir("APK")!!.path

        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val BOOK_PATH: String = MyApplication.mContext.getExternalFilesDir("BookFile")!!.path

        const val CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        const val BOOK_PICTURE_FILES = "contents" //图片资源的最确路径

        //截图保存目录
        val SCREEN_PATH: String = MyApplication.mContext.getExternalFilesDir("Screen")!!.path

        //收到题卷地址
        val TESTPAPER_PATH: String = MyApplication.mContext.getExternalFilesDir("TestPaper")!!.path

        //作业保存目录
        val HOMEWORK_PATH: String = MyApplication.mContext.getExternalFilesDir("HomeWork")!!.path

        //录音保存目录
        val RECORD_PATH: String = MyApplication.mContext.getExternalFilesDir("Record")!!.path

        //笔记保存目录
        val NOTE_PATH: String = MyApplication.mContext.getExternalFilesDir("Note")!!.path

        //画本保存目录
        val PAINTING_PATH: String = MyApplication.mContext.getExternalFilesDir("Painting")!!.path

        //壁纸、书画、工具栏应用图标目录
        val IMAGE_PATH: String = MyApplication.mContext.getExternalFilesDir("Image")!!.path

        const val PATH_SF = "file:///android_asset/sf/"

        //eventbus通知标志
        const val AUTO_UPLOAD_EVENT = "AutoUploadEvent"
        const val DATE_EVENT = "DateEvent"
        const val BOOK_EVENT = "BookEvent"//书籍下载通知
        const val TEXT_BOOK_EVENT = "TextBookEvent"//课本下载通知
        const val BOOK_HOMEWORK_EVENT = "BookHomeworkEvent"//作业下载通知
        const val APP_EVENT = "APPEvent"//应用下载安装通知
        const val COURSE_EVENT = "CourseEvent"
        const val NOTE_BOOK_MANAGER_EVENT = "NoteBookManagerEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val RECORD_EVENT = "RecordEvent"
        const val RECEIVE_PAPER_COMMIT_EVENT = "ReceivePaperCommit"
        const val SCREEN_EVENT = "AutoScreenEvent"
        const val VIDEO_EVENT = "VideoEvent"

        //定时任务标识
        const val ACTION_UPLOAD = "com.bll.lnkstudy.upload"
        const val ACTION_VIDEO = "com.bll.lnkstudy.raw"
    }

}


