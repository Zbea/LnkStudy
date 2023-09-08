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
class Constants{

    companion object {

        const val WIDTH = 1404
        const val HEIGHT = 1872

//        const val URL_BASE = "https://api2.qinglanmb.com/v1/"
        const val URL_BASE = "http://192.168.101.100:10800/v1/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH= MyApplication.mContext.getExternalFilesDir("Zip")!!.path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH= MyApplication.mContext.getExternalFilesDir("APK")!!.path
        val BOOK_PATH= Environment.getExternalStorageDirectory().absolutePath+"/Books"
        val BOOK_DRAW_PATH= Environment.getExternalStorageDirectory().absolutePath+"/Notes"
        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val TEXTBOOK_PATH= MyApplication.mContext.getExternalFilesDir("TextBookFile")!!.path

        const val TEXTBOOK_CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        const val TEXTBOOK_CONTENTS = "contents" //图片资源的最确路径


        //截图保存目录
        val SCREEN_PATH= MyApplication.mContext.getExternalFilesDir("Screen")!!.path

        //收到题卷地址
        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("TestPaper")!!.path

        //作业保存目录
        val HOMEWORK_PATH= MyApplication.mContext.getExternalFilesDir("HomeWork")!!.path

        //笔记保存目录
        val NOTE_PATH= MyApplication.mContext.getExternalFilesDir("Note")!!.path

        //画本保存目录
        val PAINTING_PATH= MyApplication.mContext.getExternalFilesDir("Painting")!!.path

        //壁纸、书画、工具栏应用图标目录
        val IMAGE_PATH= MyApplication.mContext.getExternalFilesDir("Image")!!.path

        //断点记录文件保存的文件夹
        val RECORDER_PATH= MyApplication.mContext.getExternalFilesDir("Recorder")!!.path
        val FREE_NOTE_PATH = MyApplication.mContext.getExternalFilesDir("FreeNote")?.path

        //eventbus通知标志
        const val AUTO_UPLOAD_1MONTH_EVENT = "AutoUploadEvent1Month"
        const val AUTO_UPLOAD_9MONTH_EVENT = "AutoUploadEvent9Month"
        const val AUTO_UPLOAD_EVENT = "AutoUploadEvent"
        const val DATE_EVENT = "DateEvent"
        const val BOOK_EVENT = "BookEvent"//书籍下载通知
        const val TEXT_BOOK_EVENT = "TextBookEvent"//课本下载通知
        const val HOMEWORK_BOOK_EVENT = "HomeworkBookEvent"//课本下载通知
        const val APP_EVENT = "APPEvent"//应用下载安装通知
        const val COURSE_EVENT = "CourseEvent"
        const val CLASSGROUP_EVENT = "ClassGroupEvent"
        const val NOTE_BOOK_MANAGER_EVENT = "NoteBookManagerEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val RECORD_EVENT = "RecordEvent"
        const val RECEIVE_PAPER_COMMIT_EVENT = "ReceivePaperCommit"
        const val SCREEN_EVENT = "AutoScreenEvent"
        const val VIDEO_EVENT = "VideoEvent"
        const val MESSAGE_EVENT = "MessageEvent"
        const val CONTROL_MESSAGE_EVENT = "ControlMessageEvent"
        const val USER_EVENT = "UserEvent"
        const val CONTROL_CLEAR_EVENT = "ControlClearEvent"
        const val DATA_DOWNLOAD_EVENT = "DataDownload"
        const val DATA_RENT_EVENT = "DataRent"
        const val DATA_CLEAT_EVENT="DataClear"
        const val EXAM_TIME_EVENT="ExamTime"//考试到时自动提交广播
        const val MAIN_HOMEWORK_NOTICE_EVENT="MainHomeworkNoticeEvent"//作业通知清除广播

        //定时任务标识
        const val ACTION_UPLOAD = "com.bll.lnkstudy.upload"
        const val ACTION_UPLOAD_1MONTH = "com.bll.lnkstudy.upload.1month"
        const val ACTION_UPLOAD_9MONTH = "com.bll.lnkstudy.upload.9month"
        const val ACTION_VIDEO = "com.bll.lnkstudy.raw"
        const val ACTION_EXAM_TIME = "com.bll.lnkstudy.exam.time"
    }

}


