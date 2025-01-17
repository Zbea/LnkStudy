package com.bll.lnkstudy

import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.AccountQdBean
import com.bll.lnkstudy.mvp.model.AppList
import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.CalenderList
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUserList
import com.bll.lnkstudy.mvp.model.CommonData
import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.mvp.model.SchoolBean
import com.bll.lnkstudy.mvp.model.SystemUpdateInfo
import com.bll.lnkstudy.mvp.model.TeachingVideoList
import com.bll.lnkstudy.mvp.model.TeachingVideoType
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.model.book.BookStore
import com.bll.lnkstudy.mvp.model.book.BookStoreType
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessageList
import com.bll.lnkstudy.mvp.model.homework.ParentTypeBean
import com.bll.lnkstudy.mvp.model.painting.PaintingList
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean
import com.bll.lnkstudy.mvp.model.paper.ExamItem
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean
import com.bll.lnkstudy.mvp.model.textbook.TextbookStore
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.system.BaseResult1
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface APIService{

    /**
     * 检查系统更新
     */
    @POST("Device/CheckUpdate")
    fun RELEASE_CHECK_UPDATE(@Body requestBody: RequestBody): Observable<BaseResult1<SystemUpdateInfo>>
    /**
     * 检查系统更新
     */
    @POST("Device/UpdateInfo")
    fun RELEASE_UPDATE_INFO(@Body requestBody: RequestBody): Observable<BaseResult1<SystemUpdateInfo>>

    @POST("cloud/data/insert")
    fun cloudUpload(@Body requestBody: RequestBody): Observable<BaseResult<MutableList<Int>>>
    /**
     * 获取下载token
     */
    @POST("file/token")
    fun getQiniuToken(): Observable<BaseResult<String>>
    /**
     * 活跃查询
     */
    @GET("accounts/active")
    fun active(): Observable<BaseResult<Any>>
    /**
     * 获取更新
     */
    @GET("app/info/one?type=1")
    fun onAppUpdate(): Observable<BaseResult<AppUpdateBean>>
    /**
     * 获取增量更新列表F
     */
    @GET("student/data/list")
    fun onListDataUpdate(): Observable<BaseResult<MutableList<DataUpdateBean>>>
    /**
     * 获取增量更新列表
     */
    @GET("student/data/list")
    fun onListDataUpdate(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<MutableList<DataUpdateBean>>>
    /**
     * 添加增量更新
     */
    @POST("student/data/insert")
    fun onAddDataUpdate(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 删除增量更新
     */
    @POST("student/data/delete")
    fun onDeleteDataUpdate(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 清空增量更新
     */
    @POST("student/data/deleteAll")
    fun onClearDataUpdate(): Observable<BaseResult<Any>>
    /**
     * 用户登录 "/login"
     */
    @POST("login")
    fun login(@Body requestBody: RequestBody): Observable<BaseResult<User>>
    /**
     * 用户个人信息 "/accounts"
     */
    @GET("accounts")
    fun accounts(): Observable<BaseResult<User>>

    /**
     * 短信信息 "/sms"
     */
    @GET("sms")
    fun getSms(@Query("telNumber") num:String): Observable<BaseResult<Any>>

    /**
     * 注册 "user/createStudent"
     */
    @POST("user/createStudent")
    fun register(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 忘记密码 "/password"
     */
    @POST("password")
    fun findPassword(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改密码 "/accounts/password"
     */
    @PATCH("accounts/password")
    fun editPassword(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改姓名 "/accounts/nickname"
     */
    @PATCH("accounts/nickname")
    fun editName(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 验证手机号
     */
    @POST("accounts/checkCode")
    fun checkPhone(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改电话
     */
    @POST("accounts/changeTel")
    fun editPhone(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改生日
     */
    @POST("accounts/updateInfo")
    fun editBirthday(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改学校信息
     */
    @POST("accounts/changeAddress")
    fun editSchool(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改家长个人信息
     */
    @POST("user/updateParentInfo")
    fun editParent(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 退出登录 "/accounts/logout"
     */
    @POST("accounts/logout")
    fun logout(): Observable<BaseResult<Any>>

    /**
     * //获取学豆列表
     */
    @GET("wallets/list")
    fun getSMoneyList(): Observable<BaseResult<MutableList<AccountQdBean>>>
    /**
     * 提交学豆订单
     */
    @POST("wallets/order/{id}")
    fun postOrder(@Path("id") id:String ): Observable<BaseResult<AccountOrder>>
    /**
     * 查看订单状态
     */
    @GET("wallets/order/{id}")
    fun getOrderStatus(@Path("id") id:String): Observable<BaseResult<AccountOrder>>
    /**
     * 获取学生科目列表
     */
    @GET("class/group/teacherInfo")
    fun getCourseItems(): Observable<BaseResult<List<String>>>
    /**
     * 加入班群
     */
    @POST("class/addClass")
    fun insertGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 班群列表
     */
    @GET("class/list")
    fun groupList(): Observable<BaseResult<List<ClassGroup>>>
    /**
     * 班群信息
     */
    @GET("class/group/infoV2")
    fun groupInfo(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ClassGroup>>
    /**
     * 退出班群
     */
    @POST("class/group/studentQuit")
    fun quitClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 通讯录
     */
    @GET("class/group/schoolList")
    fun getClassGroupUser(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ClassGroupUserList>>

    /**
     * 教材分类
     */
    @GET("book/types")
    fun getBookType(): Observable<BaseResult<BookStoreType>>
    /**
     * 教材列表
     */
    @GET("textbook/list")
    fun getTextBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TextbookStore>>
    /**
     * 书城列表
     */
    @GET("book/plus/list")
    fun getBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 题卷列表
     */
    @GET("book/list")
    fun getHomeworkBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TextbookStore>>
    /**
     * 购买书籍
     */
    @POST("buy/book/createOrder")
    fun buy(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 应用列表
     */
    @GET("application/list")
    fun getApks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AppList>>

    /**
     * 书画、壁纸
     */
    @GET("font/draw/list")
    fun getPaintings(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<PaintingList>>


    /**
     * 教学视频 课程列表
     */
    @GET("subject/video/list")
    fun getTeachCourseList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TeachingVideoList>>

    /**
     * 视频列表
     */
    @GET("talent/video/list")
    fun getTeachList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TeachingVideoList>>

    /**
     * 视频分类
     */
    @GET("talent/video/types")
    fun getTeachType(): Observable<BaseResult<TeachingVideoType>>

    /**
     * 获取考卷分类
     */
    @GET("common/type/school")
    fun getPaperType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<PaperTypeBean>>>
    /**
     * 学生获取考卷
     */
    @GET("student/task/studentExam")
    fun getExams(): Observable<BaseResult<ExamItem>>
    /**
     * 学生提交测试卷
     */
    @POST("student/task/pushExamWork")
    fun commitPaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 学生提交年级考卷
     */
    @POST("student/task/pushGradeExam")
    fun commitExam(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
//    /**
//     * 学生下载完老师发送的已批改试卷删除
//     */
//    @POST("student/task/deleteTag")
//    fun onDownloadCompletePaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取下发考卷
     */
    @GET("student/task/list")
    fun getPaperList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkPaperList>>
    /**
     * 获取下发考卷
     */
    @GET("student/task/listV2")
    fun getHomeworkPaperList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkPaperList>>

//    /**
//     * 获取作业本分类
//     */
//    @GET("common/type/list")
//    fun getHomeworkType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkType>>
    /**
     * 获取作业本分类
     */
    @GET("common/type/school")
    fun getHomeworkType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<HomeworkTypeBean>>>
    /**
     * 获取作业本所有信息
     */
    @POST("student/msg/list")
    fun getHomeworkMessage(@Body requestBody: RequestBody): Observable<BaseResult<Map<String, HomeworkMessageList>>>

    /**
     * 作业卷下载完成后 通知后台
     */
    @POST("task/group/studentDownload")
    fun onDownloadPaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取作业卷所有信息
     */
    @POST("task/group/studentListPlus")
    fun getHomeworkReel(@Body requestBody: RequestBody): Observable<BaseResult<Map<String, HomeworkPaperList>>>

    /**
     * 获取学生提交详情
     */
    @GET("submit/message/list")
    fun getHomeworkCommitDetails(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<HomeworkCommitMessageList>>
    /**
     * 消息列表
     */
    @GET("message/inform/list")
    fun getMessages(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<MessageList>>
    /**
     * 发送消息
     */
    @POST("message/inform/studentToParent")
    fun commitMessage(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取家长作业本
     */
    @GET("parent/homework/all")
    fun getParentsHomeworkType(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<MutableList<ParentTypeBean>>>
    /**
     * 消息列表
     */
    @POST("student/job/listByIds")
    fun getParentMessage(@Body requestBody: RequestBody): Observable<BaseResult<Map<String, ParentHomeworkMessageList>>>
    /**
     * 家长作业卷下发
     */
    @POST("student/job/downloadByIds")
    fun getParentReel(@Body requestBody: RequestBody): Observable<BaseResult<ParentHomeworkMessageList>>
    /**
     * 作业卷下载完成后 通知后台
     */
    @POST("student/job/downloadChange")
    fun commitParentLoad(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 学生提交考卷
     */
    @POST("student/job/childSubmit")
    fun commitParent(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 公共年级接口
     */
    @GET("userTypes")
    fun getCommonData(): Observable<BaseResult<CommonData>>
    /**
     * 获取学校接口
     */
    @GET("school/list")
    fun getCommonSchool(): Observable<BaseResult<MutableList<SchoolBean>>>

    /**
     * 获取云列表
     */
    @GET("cloud/data/list")
    fun getCloudList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CloudList>>
    /**
     * 获取分类
     */
    @GET("cloud/data/types")
    fun getCloudType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<MutableList<String>>>
    /**
     * 删除云列表
     */
    @POST("cloud/data/delete")
    fun deleteCloudList(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取消息通知
     */
    @GET("job/message/list")
    fun getHomeworkNotice(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkNoticeList>>
    /**
     * 删除作业通知
     */
    @POST("job/message/deleteAll")
    fun deleteHomeworkNotice(): Observable<BaseResult<Any>>
    /**
     * 获取学生批改通知
     */
    @GET("task/msg/list")
    fun getCorrectNotice(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<HomeworkNoticeList>>
    /**
     * 删除批改通知
     */
    @POST("task/msg/deleteAll")
    fun deleteCorrectNotice(): Observable<BaseResult<Any>>
    /**
     * 台历列表
     */
    @GET("calendar/list")
    fun getCalenderList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CalenderList>>

    /**
     * 获取老师课程表
     */
    @GET("class/group/courseInfo")
    fun getTeacherCourse(): Observable<BaseResult<String>>

    /**
     * 获取考试批改
     */
    @GET("school/exam/allStudentJob")
    fun getExamCorrectList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<MutableList<ExamCorrectBean>>>
    /**
     * 学校考试下载完成
     */
    @POST("school/exam/updateDownloadStatus")
    fun onDownloadCompleteExamCorrect(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取家长的权限控制
     */
    @GET("student/permission/info")
    fun getPermissionParentAllow(): Observable<BaseResult<PermissionParentBean>>
    /**
     * 获取学校的权限控制
     */
    @GET("student/data/rule")
    fun getPermissionSchoolAllow(): Observable<BaseResult<PermissionSchoolBean>>
}