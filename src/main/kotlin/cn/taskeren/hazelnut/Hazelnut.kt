package cn.taskeren.hazelnut

import city.warlock.destinyDB.DestinyDatabase
import cn.taskeren.hazelnut.core.command.BotDefaultCommands
import cn.taskeren.hazelnut.core.config.*
import cn.taskeren.hazelnut.core.kai.KaiEventListeners
import cn.taskeren.hazelnut.feature.bili.BiliCommands
import cn.taskeren.hazelnut.feature.destiny.D2Commands
import kaikt.api.KToken
import kaikt.websocket.KaiClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Hazelnut")

val kApi by lazy { KToken(KToken.TokenType.Bot, HazelnutConfig.propBotToken.string).toApi() }
val kBot by lazy { KaiClient(kApi) }

val d2Db by lazy { DestinyDatabase(HazelnutConfig.propMongoDBUri.string) }

fun main() {
	println("榛子！")

	logger.info("正在加载榛子")

	logger.info("正在加载配置")

	logger.info("正在注册指令")
	BotDefaultCommands.register()
	D2Commands.register()
	BiliCommands.register()

	logger.info("开始加载开黑啦机器人")
	kBot.connect()

	logger.info("正在注册开黑啦机器人事件")
	kBot.eventBus.register(KaiEventListeners())

}