package cn.taskeren.hazelnut.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import kaikt.websocket.acorn.AcornMessageSource

object HazelnutCommandManager {

	val dispatcher = CommandDispatcher<AcornMessageSource>()

	fun execute(sender: AcornMessageSource, command: String) {
		try {
			dispatcher.execute(command, sender)
		} catch(ex: CommandSyntaxException) {
			sender.sendMessage(ex.message ?: "未知错误！${ex.javaClass}")
		}
	}

}