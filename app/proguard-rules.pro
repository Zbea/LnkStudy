# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共库的成员
-dontskipnonpubliclibraryclassmembers
# 混淆时不做预校验
-dontpreverify
# 混淆时不记录日志
-verbose
# 代码优化
-dontshrink
# 不优化输入的类文件
-dontoptimize
# 保留注解不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 保留代码行号，方便异常信息的追踪
-keepattributes SourceFile,LineNumberTable
# 混淆采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# dump.txt文件列出apk包内所有class的内部结构
-dump proguard/class_files.txt
# seeds.txt文件列出未混淆的类和成员
-printseeds proguard/seeds.txt
# usage.txt文件列出从apk中删除的代码
-printusage proguard/unused.txt
# mapping.txt文件列出混淆前后的映射
-printmapping proguard/mapping.txt

# Android类
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.bll.lnkstudy.utils.SPUtil{
*;}
-keep public class com.bll.lnkstudy.mvp.model.* {
*;}

# support
-keep class android.support.** {*;}
-keep public class * extends android.support.**
-dontwarn android.support.**
-keep interface android.support.** { *; }

# androidx
-keep class androidx.** {*;}
-keep interface androidx.** {*;}
-keep public class * extends androidx.**
-dontwarn androidx.**

# 自定义控件
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# R文件
-keep class **.R$* {
 *;
}

# webview
-keepclassmembers class android.webkit.WebView {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, *);
}

# 按键等事件
-keepclassmembers class * {
    void *(**On*Event);
}
# onClick
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# native方法
-keepclasseswithmembernames class * {
    native <methods>;
}
# View构造方法
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#greendao
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
   public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keepclassmembers class * {
    public static ** $build;
    public static ** $properties;
}
-keepclassmembers class * implements org.greenrobot.greendao.Property {
   public static ** value();
}
-keep class * extends org.greenrobot.greendao.AbstractDaoMaster { *; }
-keep class * extends org.greenrobot.greendao.AbstractDaoSession { *; }
-keep class * extends org.greenrobot.greendao.AbstractDao { *; }

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    public <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# EventBus 3.0 and later:
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
# If you use AsyncExecutor (not recommended for Android), keep the following:
-keepclassmembers class * extends org.greenrobot.eventbus.util.AsyncExecutor {
    public <methods>;
}

#jzvd视频
-keep public class cn.jzvd.JZMediaSystem {*; }
-keep class tv.danmaku.ijk.media.player.** {*; }
-dontwarn tv.danmaku.ijk.media.player.*
-keep interface tv.danmaku.ijk.media.player.** { *; }

#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-keep class okhttp3.** { *;}
-keep class okio.** { *;}
-dontwarn sun.security.**
-keep class sun.security.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.**

#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn org.robovm.**
-keep class org.robovm.** { *; }

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-dontnote rx.internal.util.PlatformDependent

-keep class android.security*{
*;}

-keep public class * extends android.print.PrintService

# ============忽略警告，否则打包可能会不成功=============
-ignorewarnings