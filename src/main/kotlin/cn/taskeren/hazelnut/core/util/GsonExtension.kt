package cn.taskeren.hazelnut.core.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJson(json: String): T {
	val type = (object : TypeToken<T>() {}).type
	return this.fromJson(json, type)
}