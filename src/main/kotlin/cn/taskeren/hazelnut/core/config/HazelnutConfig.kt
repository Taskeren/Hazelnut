package cn.taskeren.hazelnut.core.config

import TConfig.Configuration
import TConfig.Property
import java.io.File
import kotlin.system.exitProcess

object HazelnutConfig {

	private val config = Configuration(File(".hazelnut/hazelnut.cfg"))

	val propBotToken: Property = config["general", "bot-token", "?", "机器人凭据（在 https://developer.kaiheila.cn 获取）"]
	val propMongoDBUri: Property = config["destiny", "database-uri", "mongodb://localhost:27017", "数据库链接"]

	val propBSpecUsers: Property = config["bilibili", "spec-users", arrayOf("0"), "特别关注的B站用户"]

	init {
		config.save()

		// 首次启动检查
		if(propBotToken.string == "?") {
			println("配置文档已生成，位于 \".hazelnut/hazelnut.cfg\"，请先修改配置文档")
			exitProcess(0)
		}
	}

	fun save() = config.save()

}