package cn.taskeren.hazelnut.core.kai

import cn.taskeren.hazelnut.core.command.HazelnutCommandManager
import kaikt.websocket.event.direct.PrivateTextMessageEvent
import kaikt.websocket.event.guild.GuildTextMessageEvent
import org.greenrobot.eventbus.Subscribe

class KaiEventListeners {

	private val prefix = '/'

	@Subscribe
	fun onGuildMessage(e: GuildTextMessageEvent) {
		if(e.content.startsWith(prefix)) {
			val cmd = e.content.substring(1)
			val sender = e.channel
			HazelnutCommandManager.execute(sender, cmd)
		}
	}

	@Subscribe
	fun onPrivateMessage(e: PrivateTextMessageEvent) {
		if(e.content.startsWith(prefix)) {
			val cmd = e.content.substring(1)
			val sender = e.authorUser
			HazelnutCommandManager.execute(sender, cmd)
		}
	}

}