package com.example.messenger.data.local.db.converter

import androidx.room.TypeConverter
import com.example.messenger.common.exstantions.fromJson
import com.google.gson.Gson

class ArrayListConverter {

    @TypeConverter
    fun fromStringArrayList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringArrayList(value: String): List<String> {
        return try {
            Gson().fromJson<List<String>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}