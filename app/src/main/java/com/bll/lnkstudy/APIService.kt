package com.bll.lnkstudy

import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.mvp.model.book.BookStore
import com.bll.lnkstudy.mvp.model.book.BookStoreType
import com.bll.lnkstudy.mvp.model.cloud.CloudList
import com.bll.lnkstudy.mvp.model.homework.*
import com.bll.lnkstudy.mvp.model.painting.PaintingList
import com.bll.lnkstudy.mvp.model.paper.PaperList
import com.bll.lnkstudy.mvp.model.paper.PaperType
import com.bll.lnkstudy.mvp.model.textbook.TextbookStore
import com.bll.lnkstudy.net.BaseResult
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface APIService{

    /**
     * 文件上传
     */
    @Multipart
    @POST("file/manyUpload")
    fun upload(@Part parts: List<MultipartBody.Part>): Observable<BaseResult<List<String>>>
    @POST("cloud/data/insert")
    fun cloudUpload(@Body requestBody: RequestBody): Observable<BaseResult<MutableList<Int>>>
    /**
     * 获取下载token
     */
    @POST("file/token")
    fun getQiniuToken(): Observable<BaseResult<String>>

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
    fun onClearDataUpdate(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
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
     * 修改姓名 "/accounts/changeGrade"
     */
    @POST("accounts/changeGrade")
    fun editGrade(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改学校信息
     */
    @POST("accounts/changeAddress")
    fun editSchool(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 退出登录 "/accounts/logout"
     */
    @POST("accounts/logout")
    fun logout(): Observable<BaseResult<Any>>

    /**
     * //获取学豆列表
     */
    @GET("wallets/list")
    fun getSMoneyList(@QueryMap map: HashMap<String,String>): Observable<BaseResult<AccountXDList>>
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
     * 退出班群
     */
    @POST("class/quit")
    fun quitClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 通讯录
     */
    @GET("class/schoolmateList")
    fun getClassGroupUser(): Observable<BaseResult<List<ClassGroupUser>>>

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
    fun buyBooks(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 应用列表
     */
    @GET("application/list")
    fun getApks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AppList>>

    /**
     * 购买apk
     */
    @POST("buy/book/createOrder")
    fun buyApk(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 书画、壁纸
     */
    @GET("font/draw/list")
    fun getPaintings(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<PaintingList>>

    /**
     * 购买apk
     */
    @POST("buy/book/createOrder")
    fun buyPainting(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 教学视频 课程列表
     */
    @GET("subject/video/list")
    fun getTeachCourseList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TeachingVideoList>>

    /**
     * 教学视频 课程分类
     */
    @GET("subject/video/types")
    fun getTeachCourseType(): Observable<BaseResult<TeachingVideoType>>

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
    @GET("common/type/list")
    fun getPaperType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<PaperType>>
    /**
     * 学生获取考卷
     */
    @GET("student/task/list")
    fun getPapersList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<PaperList>>
    /**
     * 学生提交考卷
     */
    @POST("student/task/pushExamWork")
    fun commitPaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 学生下载完老师发送的已批改试卷删除
     */
    @POST("student/task/deleteTag")
    fun deletePaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取作业本分类
     */
    @GET("common/type/list")
    fun getHomeworkType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkType>>
    /**
     * 获取作业本所有信息
     */
    @POST("student/msg/list")
    fun getHomeworkMessage(@Body requestBody: RequestBody): Observable<BaseResult<Map<String, HomeworkMessage>>>

    /**
     * 获取作业卷所有信息
     */
    @GET("task/group/studentList")
    fun getHomeworkReel(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkPaperList>>
    /**
     * 作业卷下载完成后 通知后台
     */
    @POST("task/group/studentDownload")
    fun commitHomeworkLoad(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取作业卷所有信息
     */
    @POST("task/group/studentListPlus")
    fun getHomeworkReel(@Body requestBody: RequestBody): Observable<BaseResult<Map<String, HomeworkPaperList>>>

//    /**
//     * 获取学生批改详情
//     */
//    @GET("task/group/sendList")
//    fun getHomeworkCorrectDetails(): Observable<BaseResult<MutableList<HomeworkDetailBean>>>
//    /**
//     * 获取学生提交详情
//     */
//    @GET("submit/message/list")
//    fun getHomeworkCommitDetails(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<HomeworkDetails>>
    /**
     * 消息列表
     */
    @GET("message/inform/list")
    fun getMessages(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<MessageList>>
    /**
     * 发送消息
     */
    @POST("message/inform/insertStudent")
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
    fun getParentMessage(@Body requestBody: RequestBody): Observable<BaseResult<Map<String,ParentHomeworkMessage>>>
    /**
     * 消息列表
     */
    @POST("student/job/downloadByIds")
    fun getParentReel(@Body requestBody: RequestBody): Observable<BaseResult<Map<String,MutableList<ParentHomeworkBean>>>>
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

}