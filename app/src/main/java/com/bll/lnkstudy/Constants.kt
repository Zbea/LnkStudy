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
 * desc: 常量  分辨率为 1404x1872，屏幕尺寸为 10.3 scale为 1.375
 */
class Constants {

    companion object {

        const val WIDTH = 1404
        const val HEIGHT = 1872
        const val halfYear = 180 * 24 * 60 * 60 * 1000
        const val dayLong = 24 * 60 * 60 * 1000
        const val weekTime = 7 * 24 * 60 * 60 * 1000
        const val DEFAULT_PAGE = -1

//                                const val URL_BASE = "https://api2.qinglanmb.com/v1/"
        const val URL_BASE = "http://192.168.101.100:10800/v1/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip")!!.path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK")!!.path
        val BOOK_PATH = Environment.getExternalStoragePublicDirectory("Books").absolutePath
        val BOOK_DRAW_PATH = Environment.getExternalStoragePublicDirectory("Notes").absolutePath
        val SCREEN_PATH = Environment.getExternalStoragePublicDirectory("Screenshots").absolutePath

        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val TEXTBOOK_PATH = MyApplication.mContext.getExternalFilesDir("TextBookFile")!!.path
        const val TEXTBOOK_CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        const val TEXTBOOK_CONTENTS = "contents" //图片资源的最确路径

        //收到题卷地址
        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("TestPaper")!!.path

        //作业保存目录
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("HomeWork")!!.path

        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("Note")!!.path

        //画本保存目录
        val PAINTING_PATH = MyApplication.mContext.getExternalFilesDir("Painting")!!.path
        val IMAGE_PATH = MyApplication.mContext.getExternalFilesDir("Image")!!.path

        //断点记录文件保存的文件夹
        val RECORDER_PATH = MyApplication.mContext.getExternalFilesDir("Recorder")!!.path
        val FREE_NOTE_PATH = MyApplication.mContext.getExternalFilesDir("FreeNote")?.path

        //eventbus通知标志
        const val REFRESH_EVENT = "RefreshEvent" //刷新页面
        const val AUTO_UPLOAD_LAST_SEMESTER_EVENT = "AutoUploadEventLastSemester" //上学期开学
        const val AUTO_UPLOAD_NEXT_SEMESTER_EVENT = "AutoUploadEventNextSemester" //下学期开学
        const val AUTO_UPLOAD_EVENT = "AutoUploadEvent"//每天三点自动上传
        const val DATE_DAY_EVENT = "DateDayEvent"//重要日子刷新
        const val DATE_EVENT = "DateEvent"
        const val BOOK_EVENT = "BookEvent"//书籍下载通知
        const val TEXT_BOOK_EVENT = "TextBookEvent"//课本下载通知
        const val HOMEWORK_BOOK_EVENT = "HomeworkBookEvent"//课本下载通知
        const val APP_INSTALL_EVENT = "AppInstallEvent"//应用下载安装通知
        const val APP_UNINSTALL_EVENT = "AppUninstallEvent"//应用卸载安装通知
        const val APP_INSERT_EVENT = "AppInsertEvent"//工具保存通知
        const val COURSE_EVENT = "CourseEvent" //课程表保存刷新
        const val CLASSGROUP_EVENT = "ClassGroupEvent" //班群刷新
        const val NOTE_BOOK_MANAGER_EVENT = "NoteBookManagerEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val RECORD_EVENT = "RecordEvent"//语音作业本录音通知
        const val MESSAGE_COMMIT_EVENT = "MessageCommitEvent"//发送消息提交通知刷新
        const val USER_CHANGE_EVENT = "UserChangeEvent" //个人信息变化
        const val EXAM_COMMIT_EVENT = "ExamPaperCommit"//考试提交通知
        const val EXAM_TIME_EVENT = "ExamTime"//考试到时自动提交广播
        const val MAIN_HOMEWORK_NOTICE_EVENT = "MainHomeworkNoticeEvent"//作业通知清除广播
        const val PASSWORD_EVENT = "PrivacyPasswordEvent"//设置隐私密码广播
        const val SCREENSHOT_MANAGER_EVENT = "ScreenshotManagerEvent"//截图管理
        const val NETWORK_CONNECTION_COMPLETE_EVENT = "NetworkConnectionCompleteEvent"//网络连接成功
        const val NETWORK_CONNECTION_FAIL_EVENT = "NetworkConnectionFailEvent"//网络连接断开
        const val DATE_DRAWING_EVENT="DateDrawingEvent"//日程手写结束
        const val CALENDER_SET_EVENT = "CalenderSetEvent"//台历设置

        const val SETTING_DOWNLOAD_EVENT = "DataDownload"
        const val SETTING_RENT_EVENT = "DataRent"
        const val SETTING_CLEAT_EVENT = "DataClear"

        //定时任务标识
        const val ACTION_UPLOAD_8 = "com.bll.lnkstudy.upload_8"
        const val ACTION_UPLOAD_15 = "com.bll.lnkstudy.upload_15"
        const val ACTION_UPLOAD_18 = "com.bll.lnkstudy.upload_18"
        const val ACTION_UPLOAD_NEXT_SEMESTER = "com.bll.lnkstudy.upload.next.semester"
        const val ACTION_UPLOAD_LAST_SEMESTER = "com.bll.lnkstudy.upload.last.semester"
        const val ACTION_EXAM_TIME = "com.bll.lnkstudy.exam.time"

        //广播
        const val LOGIN_BROADCAST_EVENT = "com.bll.lnkstudy.account.login"
        const val LOGOUT_BROADCAST_EVENT = "com.bll.lnkstudy.account.logout"
        const val EXAM_MODE_BROADCAST_EVENT = "com.bll.lnkstudy.exam.mode"

        const val PACKAGE_READER = "com.geniatech.knote.reader"
        const val PACKAGE_GEOMETRY = "com.geometry"
    }

}


