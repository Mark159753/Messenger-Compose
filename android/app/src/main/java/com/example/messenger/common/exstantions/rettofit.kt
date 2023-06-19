package com.example.messenger.common.exstantions

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun File.toMultiPartBody(name:String, type:String): MultipartBody.Part {
    val requestBody = asRequestBody(type.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, getName(), requestBody)
}

fun Any.toPartMap(): Map<String, RequestBody> {
    return toMapOnlyConstructor(this)
        .mapValues { m ->
            m.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }
}


fun <T : Any> toMapOnlyConstructor(obj: T): Map<String, Any?> {
    val kClass = obj::class as KClass<T>
    val primaryConstructorPropertyNames = kClass.primaryConstructor?.parameters?.map { it.name } ?: run {
        return toMap(obj)
    }
    return kClass.memberProperties.mapNotNull { prop ->
        prop.name.takeIf { it in primaryConstructorPropertyNames }?.let {
            it to prop.get(obj)?.let { value ->
                if (value::class.isData) {
                    toMap(value)
                } else {
                    value
                }
            }
        }
    }.toMap()
}

fun <T : Any> toMap(obj: T): Map<String, Any?> {
    return (obj::class as KClass<T>).memberProperties.associate { prop ->
        prop.name to prop.get(obj)?.let { value ->
            if (value::class.isData) {
                toMap(value)
            } else {
                value
            }
        }
    }
}