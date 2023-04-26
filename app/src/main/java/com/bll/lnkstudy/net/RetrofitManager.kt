package com.bll.lnkstudy.net

import android.util.Log
import com.bll.lnkstudy.APIService
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


object RetrofitManager{

    val service: APIService by lazy (LazyThreadSafetyMode.SYNCHRONIZED){
        getRetrofit().create(APIService::class.java)
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url.newBuilder()
                    // Provide your custom parameter here
//                    .addQueryParameter("udid", "d2807c895f0348a180148c9dfa6f2feeac0781b5")
//                    .addQueryParameter("deviceModel", "")
                    .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", SPUtil.getString("token"))
                    .method(originalRequest.method, originalRequest.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }



    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.isNetworkAvailable(MyApplication.mContext)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtil.isNetworkAvailable(MyApplication.mContext)) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("nyn")
                        .build()
            }
            response
        }
    }

    private fun getRetrofit(): Retrofit {
        // 获取retrofit的实例
        return Retrofit.Builder()
                .baseUrl(Constants.URL_BASE)  //自己配置
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }

    private fun getNameVerifier():HostnameVerifier{
        return HostnameVerifier { p0, p1 -> true }
    }


    private fun getOkHttpClient(): OkHttpClient {
        //添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.i("response", message + "\n\n") }
        //可以设置请求过滤的水平,body,basic,headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val sc=SSLContext.getInstance("SSL")
        val trustManager: X509TrustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }
        var rustManagers = arrayOf(trustManager)
        sc.init(null,rustManagers, SecureRandom())


        //设置 请求的缓存的大小跟位置
        val cacheFile = File(MyApplication.mContext.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

        return OkHttpClient.Builder()
                .addInterceptor(addQueryParameterInterceptor())  //参数添加
                .addInterceptor(addHeaderInterceptor()) // token过滤
                .sslSocketFactory(sc.socketFactory,trustManager)//ssh信任管理器
                .hostnameVerifier(getNameVerifier())//域名管理
//              .addInterceptor(addCacheInterceptor())
                .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
                .cache(cache)  //添加缓存
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .build()
    }

}
