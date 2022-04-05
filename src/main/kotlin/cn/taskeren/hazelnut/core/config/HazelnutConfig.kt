package cn.taskeren.hazelnut.core.config

import city.warlock.d2api.GSON
import cn.taskeren.hazelnut.logger
import com.google.gson.GsonBuilder
import java.io.File
import kotlin.system.exitProcess

class HazelnutConfig internal constructor(
	val botToken: String,
	val biliLiveUsers: List<String>
)

lateinit var hazelnutConfig: HazelnutConfig

private val prettyPrintGson = GsonBuilder().setPrettyPrinting().create()

fun reloadHazelnutConfig() {
	val file = File("./hazelnut.config")
	runCatching {
		if(!file.exists()) {
			file.createNewFile()
			hazelnutConfig = HazelnutConfig("TOKEN_HERE!", emptyList())
			saveHazelnutConfig()
		}

		hazelnutConfig = GSON.fromJson(file.bufferedReader(), HazelnutConfig::class.java)
	}.onFailure {
		System.err.println("Cannot load configuration!")
		it.printStackTrace()
		exitProcess(1)
	}
}

fun saveHazelnutConfig() {
	val file = File("./hazelnut.config")
	runCatching {
		file.writeText(prettyPrintGson.toJson(hazelnutConfig))
	}.onFailure {
		System.err.println("Cannot save configuration!")
		it.printStackTrace()
	}
}

fun checkHazelnutConfig() {
	if(hazelnutConfig.botToken == "TOKEN_HERE!") {
		logger.warn("机器人 Token 尚未配置，请配置后再启动！")
		exitProcess(-1)
	}
	if(hazelnutConfig.biliLiveUsers.isEmpty()) {
		logger.warn("空下饭主播列表！")
	}
}