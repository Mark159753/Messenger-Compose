package com.example.messenger.common.exstantions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun <T>Gson.toModelOrNull(msg:String?, clazz:Class<T>):T?{
    if (msg.isNullOrBlank()) return null
    return try {
        fromJson(msg, clazz)
    }catch (e:Exception){
        null
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)