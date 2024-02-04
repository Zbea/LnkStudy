package com.bll.lnkstudy.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.ArrayMap
import com.bll.lnkstudy.mvp.model.CourseItem
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.schedulers.Schedulers
import java.io.*


/**
 * 数据存储类　
 * 优先从map 中读取，如果map 中没有，再从文件或者　sharedPreferences 中读取。
 * 写入的时候，用handler 写入
 */
object SPUtil {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var map: ArrayMap<String, Any>
    private val gson = Gson()
    private lateinit var rootFile: File

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        map = ArrayMap()
        rootFile = context.cacheDir
    }

    fun getUserId():String{
        return getObj("user", User::class.java)?.accountId!!.toString()
    }

    fun putList(key: String,list: MutableList<ItemList>){
        val listStr= gson.toJson(list)
        putString(getUserId()+key,listStr)
    }

    fun putCourseItems(key: String,list: MutableList<CourseItem>){
        val listStr= gson.toJson(list)
        putString(getUserId()+key,listStr)
    }

    fun putListInt(key: String,list: MutableList<Int>){
        val listStr= gson.toJson(list)
        putString(getUserId()+key,listStr)
    }

    fun putListLong(key: String,list: MutableList<Long>){
        val listStr= gson.toJson(list)
        putString(getUserId()+key,listStr)
    }

    fun putString(key: String, value: String) {
        map[key] = value
        Schedulers.io().run {
            editor.putString(key, value).apply()
        }
    }

    fun getString(key: String): String {
        var s = map[key]
        if (s == null) {
            s = sharedPreferences.getString(key, "")
            if (s != null) {
                map[key] = s
            }
        }
        return s as String
    }

    fun getList(key: String): MutableList<ItemList> {
        return gson.fromJson(getString(getUserId()+key), object : TypeToken<List<ItemList>>() {}.type)
            ?: return mutableListOf()
    }

    fun getCourseItems(key: String): MutableList<CourseItem> {
        return gson.fromJson(getString(getUserId()+key), object : TypeToken<List<CourseItem>>() {}.type)
            ?: return mutableListOf()
    }

    fun getListInt(key: String): MutableList<Int> {
        return gson.fromJson(getString(getUserId()+key), object : TypeToken<List<Int>>() {}.type)
            ?: return mutableListOf()
    }

    fun getListLong(key: String): MutableList<Long> {
        return gson.fromJson(getString(getUserId()+key), object : TypeToken<List<Long>>() {}.type)
            ?: return mutableListOf()
    }

    fun putInt(key: String, value: Int) {
        map[key] = value
        Schedulers.io().run {
            editor.putInt(key, value).apply()
        }
    }

    fun getInt(key: String): Int {
        var result = map[key]
        if (result == null) {
            result = sharedPreferences.getInt(key, 0)
            map[key] = result
        }
        return result as Int
    }

    fun putBoolean(key: String, value: Boolean) {
        map[key] = value
        Schedulers.io().run {
            editor.putBoolean(key, value).apply()
        }
    }

    fun getBoolean(key: String): Boolean {
        var result = map[key]
        if (result == null) {
            result = sharedPreferences.getBoolean(key, false)
            map[key] = result
        }
        return result as Boolean
    }

    fun putObj(key: String, any: Any) {
        map[key] = any
        Schedulers.io().run {
            val file = File(rootFile, key)
            if (file.exists()) {
                file.delete()
            }
            file.writeText(gson.toJson(any))
        }
    }


    fun <T> getObj(key: String, cls: Class<T>): T? {
        var result = map[key]
        if (result == null) {
            val file = File(rootFile, key)
            if (file.exists()) {
                val text = file.readText()
                result = gson.fromJson(text, cls)
                if (result != null) {
                    map[key] = result
                }
            }
            else{
                return null
            }
        }
        return result as T
    }

    fun removeObj(key: String): Any? {
        val file = File(rootFile, key)
        if (file.exists()) {
            file.delete()
        }
        return map.remove(key)
    }



    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
                byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
                redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
                byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

}