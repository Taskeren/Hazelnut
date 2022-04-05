package cn.taskeren.hazelnut.feature.bili

import city.warlock.d2api.GSON
import cn.taskeren.brigadierx.*
import cn.taskeren.hazelnut.core.command.HazelnutCommandManager
import cn.taskeren.hazelnut.core.config.HazelnutConfig
import cn.taskeren.hazelnut.core.util.fromJson
import cn.taskeren.hazelnut.feature.bili.model.BasicDataModel
import cn.taskeren.hazelnut.feature.bili.model.BiliLiveRoom
import com.mojang.brigadier.arguments.IntegerArgumentType
import kaikt.api.entity.enum.KMessageType
import kaikt.cardmsg.*
import kotlin.concurrent.thread

object BiliCommands {

	fun register() =
		HazelnutCommandManager.dispatcher.apply {
			register("bili") {

				literal("live") {
					argument("mid", IntegerArgumentType.integer()) {
						executesX { ctx ->
							val mid = IntegerArgumentType.getInteger(ctx, "mid")
							BiliApi.getLiveStatus(mid).onSuccess {
								val i = GSON.fromJson<BasicDataModel<BiliLiveRoom>>(it.string())
								val data = i.data
								ctx.source.sendMessage(buildCardMessage {
									header("$mid 的直播间")
									text(data.title)
									text("直播间${if(data.isLiving()) "正在直播" else if(data.isRoundPlaying()) "正在轮播" else "尚未开播"}")
									text("传送门：${data.url}")
									image(data.cover)
								}) {
									type = KMessageType.CardMessage
								}
							}.onFailure {
								ctx.source.sendMessage(it.message ?: it.javaClass.simpleName)
							}
						}
					}
				}

			}

			register("lives") {
				executesX { ctx ->
					thread {
						val total = HazelnutConfig.propBSpecUsers.stringList.size
						val infoList = HazelnutConfig.propBSpecUsers.stringList
							.filterNot { it == "0" }.map { BiliApi.getLiveStatus(it.toInt()) }
						val exists = infoList.filter { it.isSuccess }.mapNotNull { it.getOrNull() }
							.map { GSON.fromJson<BasicDataModel<BiliLiveRoom>>(it.string()) }
						val livingUsers = exists.filter { it.data.isLiving() }
						val livingCount = exists.count { it.data.isLiving() }
						if(livingCount == 0) {
							ctx.source.sendMessage("没有下饭主播。")
						} else {
							ctx.source.sendMessage(buildCardMessage {
								text("下饭主播 $livingCount of $total")
								livingUsers.forEach { model ->
									text("- ${model.data.title}")
								}
							}) {
								this.type = KMessageType.CardMessage
							}
						}
					}
				}
			}
		}

}