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
        const val HEIGHT = 1872 //38->52 50->69
        const val halfYear = 90 * 24 * 60 * 60 * 1000L
        const val dayLong = 24 * 60 * 60 * 1000L
        const val weekTime = 7 * 24 * 60 * 60 * 1000L
        const val DEFAULT_PAGE = -1
        const val SCREEN_LEFT = 1//左屏
        const val SCREEN_RIGHT = 2//右屏
        const val SCREEN_FULL = 3//全屏
        const val STATUS_BAR_SHOW = 2147483647//永不消失
        const val DEBUG="debug"

//                const val URL_BASE = "https://api2.qinglanmb.com/v1/"
        const val URL_BASE = "http://192.168.101.100:10800/v1/"
//        const val RELEASE_BASE_URL = "http://www.htfyun.com.cn:8080/"
//        const val RELEASE_BASE_URL = "http://sys.qinglanmb.com:8080/"
            const val RELEASE_BASE_URL = "https://api2.qinglanmb.com/v1/"
            const val UPDATE_URL="http://cdn.qinglanmb.com/"

        //storage/sdcard/0
        val BOOK_PATH = Environment.getExternalStoragePublicDirectory("Books").absolutePath
        val SCREEN_PATH = Environment.getExternalStoragePublicDirectory("Screenshots").absolutePath
        val DOCUMENT_PATH = Environment.getExternalStoragePublicDirectory("Documents").absolutePath

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip")!!.path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK")!!.path
            //课本作业
        val TEXTBOOK_PATH = MyApplication.mContext.getExternalFilesDir("TextBook")!!.path
        //收到题卷地址
        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("TestPaper")!!.path

        //作业保存目录
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("HomeWork")!!.path

        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("Note")!!.path

        //画本保存目录
        val PAINTING_PATH = MyApplication.mContext.getExternalFilesDir("Painting")!!.path
        val IMAGE_PATH = MyApplication.mContext.getExternalFilesDir("Image")!!.path

        val FREE_NOTE_PATH = MyApplication.mContext.getExternalFilesDir("FreeNote")?.path
        val DIARY_PATH = MyApplication.mContext.getExternalFilesDir("Diary")?.path

        //eventbus通知标志
        const val AUTO_REFRESH_EVENT = "AutoRefreshEvent" //每天刷新
        const val REFRESH_EVENT = "RefreshEvent" //刷新全部页面
        const val AUTO_UPLOAD_LAST_SEMESTER_EVENT = "AutoUploadEventLastSemester" //上学期开学
        const val AUTO_UPLOAD_NEXT_SEMESTER_EVENT = "AutoUploadEventNextSemester" //下学期开学
        const val AUTO_UPLOAD_YEAR_EVENT = "AutoUploadEventYEAR" //每年上传
        const val AUTO_UPLOAD_EVENT = "AutoUploadEvent"//每天三点自动上传
        const val DIARY_UPLOAD_EVENT = "DiaryUploadEvent"//日记上传
        const val DATE_DAY_EVENT = "DateDayEvent"//重要日子刷新
        const val DATE_EVENT = "DateEvent"
        const val BOOK_EVENT = "BookEvent"//书籍下载通知
        const val BOOK_TYPE_EVENT = "BookTypeEvent"//书籍下载通知
        const val TEXT_BOOK_EVENT = "TextBookEvent"//课本下载通知
        const val HOMEWORK_BOOK_EVENT = "HomeworkBookEvent"//课本下载通知
        const val APP_INSTALL_EVENT = "AppInstallEvent"//应用下载安装通知
        const val APP_UNINSTALL_EVENT = "AppUninstallEvent"//应用卸载安装通知
        const val APP_INSERT_EVENT = "AppInsertEvent"//工具保存通知
        const val CLASSGROUP_REFRESH_EVENT = "ClassGroupRefreshEvent"//班群更新通知
        const val COURSEITEM_EVENT = "ClassItemEvent" //学生科目刷新
        const val NOTE_TAB_MANAGER_EVENT = "NoteBookManagerEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val RECORD_EVENT = "RecordEvent"//语音作业本录音通知
        const val MESSAGE_COMMIT_EVENT = "MessageCommitEvent"//发送消息提交通知刷新
        const val USER_CHANGE_EVENT = "UserChangeEvent" //个人信息变化
        const val USER_CHANGE_GRADE_EVENT = "UserChangeGradeEvent" //年级变化
        const val EXAM_COMMIT_EVENT = "ExamPaperCommit"//考试提交通知
        const val EXAM_TIME_EVENT = "ExamTime"//考试到时自动提交广播
        const val MAIN_HOMEWORK_NOTICE_CLEAR_EVENT = "MainHomeworkNoticeClearEvent"//作业通知清除广播
        const val NETWORK_CONNECTION_COMPLETE_EVENT = "NetworkConnectionCompleteEvent"//网络连接成功
        const val NETWORK_CONNECTION_FAIL_EVENT = "NetworkConnectionFailEvent"//网络连接断开
        const val WIFI_CONNECTION_FAIL_EVENT = "WIFIConnectionFailEvent"//wifi连接断开
        const val DATE_DRAWING_EVENT = "DateDrawingEvent"//日程手写结束
        const val CALENDER_SET_EVENT = "CalenderSetEvent"//台历设置
        const val SCREENSHOT_MANAGER_EVENT = "ScreenshotManagerEvent"//截图管理刷新
        const val SETTING_DOWNLOAD_EVENT = "DataDownload"
        const val SETTING_RENT_EVENT = "DataRent"
        const val SETTING_CLEAT_EVENT = "DataClear"
        const val PAINTING_RULE_IMAGE_SET_EVENT = "PaintingRuleImageSetEvent"//规矩图设置
        const val HOMEWORK_MESSAGE_COMMIT_EVENT = "HomeworkMessageCommitEvent"//作业消息提交成功
        const val HOMEWORK_MESSAGE_TIPS_EVENT = "HomeworkMessageTipsEvent"//作业消息标识
        const val CLEAR_HOMEWORK_EVENT="ClearHomeworkEvent"//清除作业通知
        const val DOCUMENT_DOWNLOAD_EVENT="DocumentDownloadEvent"//资料下载通知

        //定时任务标识
        const val ACTION_DAY_REFRESH = "com.bll.lnkstudy.refresh"//每天0刷新
        const val ACTION_UPLOAD_15 = "com.bll.lnkstudy.upload_15"
        const val ACTION_UPLOAD_NEXT_SEMESTER = "com.bll.lnkstudy.upload.next.semester"
        const val ACTION_UPLOAD_LAST_SEMESTER = "com.bll.lnkstudy.upload.last.semester"
        const val ACTION_UPLOAD_YEAR = "com.bll.lnkstudy.upload.year"
        const val ACTION_EXAM_TIME = "com.bll.lnkstudy.exam.time"

        //广播
        const val LOGIN_BROADCAST_EVENT = "com.bll.lnkstudy.account.login"
        const val LOGOUT_BROADCAST_EVENT = "com.bll.lnkstudy.account.logout"
        const val EXAM_MODE_BROADCAST_EVENT = "com.bll.lnkstudy.exam.mode"
        const val APP_NET_REFRESH = "com.htfyun.blackwhitebar.refresh"
        const val PACKAGE_READER = "com.geniatech.knote.reader"
        const val PACKAGE_GEOMETRY = "com.geometry"
        const val PACKAGE_SYSTEM_UPDATE = "com.htfyun.firmwareupdate"
        const val PACKAGE_PPT= "com.htfyun.dualdocreader"

        const val INTENT_SCREEN_LABEL = "android.intent.extra.LAUNCH_SCREEN"//打开页面在那个屏
        const val INTENT_DRAWING_FOCUS = "android.intent.extra.KEEP_FOCUS"//手写设置焦点
        const val PERSIST_OTA_SN_PREFIX = "persist.ota.sn.prefix"
        const val SN = "SN"
        const val KEY = "Key"
        const val VERSION_NO = "VersionNO"

        const val SP_PRIVACY_PW_DIARY = "PrivacyPasswordDiary"//私密日记密码
        const val SP_PRIVACY_PW_NOTE = "PrivacyPasswordNote"//密本密码
        const val SP_WEEK_DATE_LIST = "weekDateEvent"//学习计划星期所选
        const val SP_DATE_LIST = "dateDateEvent"//学习计划日期所选
        const val SP_PAINTING_DRAW_TYPE = "PaintingDrawTYpe"//画笔类型
        const val SP_DIARY_BG_SET ="dirayBgRes"//日记
        const val SP_COURSE_URL ="courseUrl"//课程表地址
        const val SP_PARENT_PERMISSION ="parentPermission"//家长权限设置
        const val SP_SCHOOL_PERMISSION ="schoolPermission"//学校权限设置
        const val SP_EXAM_MODE ="examMode"//考试模式
        const val SP_MESSAGE_TOTAL ="messageTotal"//考试模式
    }

}


