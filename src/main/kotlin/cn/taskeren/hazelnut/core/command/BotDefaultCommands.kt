package cn.taskeren.hazelnut.core.command

import cn.taskeren.brigadierx.*
import cn.taskeren.hazelnut.logger
import kaikt.websocket.acorn.*
import java.io.File
import kotlin.system.exitProcess

object BotDefaultCommands {

	fun register() = HazelnutCommandManager.dispatcher.apply {
		register("bot") {
			executesX {
				it.source.sendMessage("Hazelnut（榛果）v1.0")
			}

			literal("exit") {
				executesX {
					if(it.source is AcornUser) {
						val texts = listOf("呜呜呜，要离开了呢QAQ", "好吧，我们下次再见吧。", "再见惹qwq", "exitProcess(0w0)")
						it.source.sendMessage(texts.random())
						logger.warn("来自用户 ${(it.source as AcornUser).getName()}(${it.source.getId()}) 的关闭请求")
						exitProcess(0)
					} else {
						it.source.sendMessage("请机器人管理员私聊使用关闭指令")
					}
				}
			}
		}
	}

}