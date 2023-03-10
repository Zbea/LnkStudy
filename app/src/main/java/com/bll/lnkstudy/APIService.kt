package com.bll.lnkstudy

import com.bll.lnkstudy.mvp.model.*
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
    fun getTextBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 教材参考列表
     */
    @GET("book/list")
    fun getTextBookCKs(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 书城列表
     */
    @GET("book/plus/list")
    fun getBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
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
     * 公共年级接口
     */
    @GET("userTypes")
    fun getCommonGrade(): Observable<BaseResult<CommonBean>>

    /**
     * 学生获取考卷
     */
    @GET("student/task/list")
    fun getPapersList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ReceivePaper>>
    /**
     * 学生提交考卷
     */
    @POST("student/task/pushExamWork")
    fun commitPaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

}