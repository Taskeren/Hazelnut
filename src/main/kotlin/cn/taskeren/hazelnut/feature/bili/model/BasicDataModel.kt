package cn.taskeren.hazelnut.feature.bili.model

data class BasicDataModel<T>(val code: Int, val message: String, val ttl: Int, val data: T)
