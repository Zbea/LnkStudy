package com.bll.lnkstudy.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.ArrayMap
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.schedulers.Schedulers


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
    private val strs= mutableListOf("token","password","account","user")

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        map = ArrayMap()
    }

    /**
     * 获取账号相关保存key
     */
    private fun getKeyStr(key: String):String{
        if (!strs.contains(key)){
            return getUserId()+ key
        }
        return key
    }

    fun getUserId():String{
        return getObj("user", User::class.java)?.accountId?.toString()?:""
    }


    fun putCurrentCourses(key: String, list: MutableList<String>){
        val listStr= gson.toJson(list)
        putString(key,listStr)
    }
    fun putClassGroupItems(key: String,list: MutableList<ClassGroup>){
        val listStr= gson.toJson(list)
        putString(key,listStr)
    }

    fun putListItems(key: String,list: MutableList<ItemList>){
        val listStr= gson.toJson(list)
        putString(key,listStr)
    }

    fun putListInt(key: String,list: MutableList<Int>){
        val listStr= gson.toJson(list)
        putString(key,listStr)
    }

    fun putListLong(key: String,list: MutableList<Long>){
        val listStr= gson.toJson(list)
        putString(key,listStr)
    }

    fun getList(key: String): MutableList<ItemList> {
        return gson.fromJson(getString(key), object : TypeToken<List<ItemList>>() {}.type)
            ?: return mutableListOf()
    }

    fun getCurrentCourses(key: String): MutableList<String> {
        return gson.fromJson(getString(key), object : TypeToken<List<String>>() {}.type)
            ?: return mutableListOf()
    }

    fun getClassGroupItems(key: String): MutableList<ClassGroup> {
        return gson.fromJson(getString(key), object : TypeToken<List<ClassGroup>>() {}.type)
            ?: return mutableListOf()
    }

    fun getListItems(key: String): MutableList<ItemList> {
        return gson.fromJson(getString(key), object : TypeToken<List<ItemList>>() {}.type)
            ?: return mutableListOf()
    }

    fun getListInt(key: String): MutableList<Int> {
        return gson.fromJson(getString(key), object : TypeToken<List<Int>>() {}.type)
            ?: return mutableListOf()
    }

    fun getListLong(key: String): MutableList<Long> {
        return gson.fromJson(getString(key), object : TypeToken<List<Long>>() {}.type)
            ?: return mutableListOf()
    }

    fun putString(key: String, value: String) {
        val keyStr= getKeyStr(key)
        synchronized(map) {
            map[keyStr] = value
        }
        Schedulers.io().run {
            editor.putString(keyStr, value).apply()
        }
    }

    fun getString(key: String): String {
        val keyStr= getKeyStr(key)
        var s = map[keyStr]
        if (s == null) {
            s = sharedPreferences.getString(keyStr, "")
            if (s != null) {
                map[keyStr] = s
            }
        }
        return s as String
    }

    fun putInt(key: String, value: Int) {
        val keyStr= getKeyStr(key)
        synchronized(map) {
            map[keyStr] = value
        }
        Schedulers.io().run {
            editor.putInt(keyStr, value).apply()
        }
    }

    fun getInt(key: String): Int {
        val keyStr= getKeyStr(key)
        var result = map[keyStr]
        if (result == null) {
            result = sharedPreferences.getInt(keyStr, 0)
            map[keyStr] = result
        }
        return result as Int
    }

    fun putBoolean(key: String, value: Boolean) {
        val keyStr= getKeyStr(key)
        synchronized(map) {
            map[keyStr] = value
        }
        Schedulers.io().run {
            editor.putBoolean(keyStr, value).apply()
        }
    }

    fun getBoolean(key: String): Boolean {
        val keyStr= getKeyStr(key)
        var result = map[keyStr]
        if (result == null) {
            result = sharedPreferences.getBoolean(keyStr, false)
            map[keyStr] = result
        }
        return result as Boolean
    }

    fun putObj(key: String, any: Any) {
        val keyStr= getKeyStr(key)
        putString(keyStr,gson.toJson(any))
    }

    fun <T> getObj(key: String, cls: Class<T>): T? {
        val keyStr= getKeyStr(key)
        return gson.fromJson(getString(keyStr), cls)
    }

    fun removeObj(key: String): Any? {
        val keyStr= getKeyStr(key)
        synchronized(map) {
            map.remove(keyStr)
        }
        Schedulers.io().run {
            editor.remove(keyStr).apply()
        }
        return map.remove(keyStr)
    }
}