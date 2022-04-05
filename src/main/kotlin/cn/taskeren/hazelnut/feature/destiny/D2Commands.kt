package cn.taskeren.hazelnut.feature.destiny

import city.warlock.d2api.compat.BungieLanguage
import city.warlock.d2api.entity.destiny.definitions.DestinyInventoryItemDefinition
import city.warlock.d2api.toBungieResourceUrl
import city.warlock.destinyDB.*
import city.warlock.destinyDB.updater.Language
import city.warlock.destinyDB.updater.tickets.UpdateTicketItemDefinition
import cn.taskeren.brigadierx.*
import cn.taskeren.hazelnut.*
import cn.taskeren.hazelnut.core.command.HazelnutCommandManager
import com.mojang.brigadier.arguments.StringArgumentType
import kaikt.api.entity.enum.KMessageType
import kaikt.cardmsg.*
import kaikt.websocket.acorn.AcornMessageSource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object D2Commands {

	private var lastDatabaseUpdated: Date? = null

	private val context = mutableMapOf<String, D2Context>()

	private data class D2Context(
		val guildId: String,
		var lastFindItem: DestinyInventoryItemDefinition? = null
	)

	/**
	 * 获取对应信息源的上下文，没有则新建
	 */
	private fun getContext(source: AcornMessageSource): D2Context {
		return context.computeIfAbsent(source.getId()) { D2Context(source.getId()) }
	}

	/**
	 * 更新信息源上下文
	 */
	private fun updateContext(source: AcornMessageSource, block: D2Context.() -> Unit) {
		getContext(source).apply(block)
	}

	fun register() =
		HazelnutCommandManager.dispatcher.apply {
			register("d2") {

				executesX { ctx ->
					ctx.source.sendMessage(
						"""
						===> 命运2 指令帮助 [${(0..9).random()}] <===
						/d2 find <物品名称> - 根据物品名称模糊搜索
						/d2 update - 更新机器人数据库
						/d2 name <物品名称> - 获取精确搜索物品信息（弃用）
						=====================
						${if(lastDatabaseUpdated != null) "上次数据库更新时间：${SimpleDateFormat().format(lastDatabaseUpdated)}" else "启动至今尚未更新过数据库"}
					""".trimIndent()
					)
				}

				literal("name") {
					argument("itemName", StringArgumentType.greedyString()) {
						executesX { ctx ->
							ctx.source.sendMessage("本指令已弃用，请使用 '/d2 find <物品名称>' 替换之。")

							val itemName = StringArgumentType.getString(ctx, "itemName")
							val items = d2Db.getItemDefinition(BungieLanguage.Chinese).findByName(itemName)

							if(items.count() == 1) {
								val i: DestinyInventoryItemDefinition = items.first()!!
								ctx.source.sendMessage(
									buildCardMessage {
										header("${i.displayProperties.name} (${i.hash})")
										text("${i.flavorText}")
										(i.displayProperties.highResIcon
											?: i.displayProperties.icon)?.toBungieResourceUrl()?.let {
											image(it)
										}
										textAndButton("light.gg", "${i.hash}")
									}
								) {
									type = KMessageType.CardMessage
								}
							} else if(items.count() > 1) {
								ctx.source.sendMessage(
									buildCardMessage {
										header("找到 ${items.count()} 个同名物品")
										items.forEach {
											text("〈${it.displayProperties.name}〉(${it.hash})")
											text("「${it.flavorText}」")
										}
									}
								) {
									type = KMessageType.CardMessage
								}
							} else {
								ctx.source.sendMessage("未找到 $itemName，请再次确认其使用繁体中文名称或使用ID查询。")
							}
						}
					}
				}

				literal("find") {
					argument("itemName", StringArgumentType.greedyString()) {
						executesX { ctx ->
							val itemName = StringArgumentType.getString(ctx, "itemName")

							// 根据 itemName 在各种翻译的数据库里找带有这个的物品实例
							val foundDefinitions = BungieLanguage.values().flatMap { lang -> // 遍历语言
								d2Db.getItemDefinition(lang).findByNameFuzzy(itemName) // 使用正则模糊搜索
							}.filter { it.isWeapon || it.isArmor } // 把除了武器和护甲的其他物品过滤

							// 查看是否有完美匹配名称的物品
							val perfectMatch = foundDefinitions.firstOrNull { it.displayName == itemName }

							if(foundDefinitions.isEmpty()) { // 啥都没有的输出
								ctx.source.sendMessage("未找到 $itemName")
							} else if(foundDefinitions.size == 1 || perfectMatch != null) { // 单个输出的情况：完美匹配或者只匹配到一个物品
								// 获取单个物品的 hash 获得其繁体中文和英文翻译的物品定义，并发送输出
								val hash = if(perfectMatch != null) {
									perfectMatch.hash!!
								} else {
									foundDefinitions[0].hash!!
								}
								val defCn = d2Db.getItemDefinition(BungieLanguage.Chinese).findByHash(hash)!!
								val defEn = d2Db.getItemDefinition(BungieLanguage.English).findByHash(hash)!!
								ctx.source.sendMessage("找到了：${defCn.displayName} [${defEn.displayName}] (${hash})")
								// 更新上下文
								updateContext(ctx.source) {
									lastFindItem = defCn
								}
							} else if(foundDefinitions.size < 10) { // 匹配到多个内容的情况，列表输出
								val text = foundDefinitions
									.map { it.hash!! }
									.map {
										d2Db.getItemDefinition(BungieLanguage.Chinese)
											.findByHash(it)!! to d2Db.getItemDefinition(BungieLanguage.English)
											.findByHash(it)!!
									}.joinToString(separator = "\n") {
										" - ${it.first.displayName} [${it.second.displayName}] (${it.first.hash})"
									}
								ctx.source.sendMessage("你找的是不是：\n$text")
							} else {
								val weirdoTexts = listOf("太多啦OAO！", "要溢出来啦QAQ！", "不可以啦QWQ！", "会坏掉的啦OWQ！")
								ctx.source.sendMessage("${weirdoTexts.random()}找到了 ${foundDefinitions.size} 条数据，请再详细一点啦w")
							}
						}
					}
				}

				literal("update") {
					executesX { ctx ->
						ctx.source.sendMessage("正在更新物品定义数据，这需要一定时间！")
						thread(name = "ItemDefinitionUpdater") {
							runCatching {
								d2Db.createUpdater().executeTicket(
									UpdateTicketItemDefinition(
										Language.ChineseSimplified,
										Language.ChineseTraditional,
										Language.English
									)
								)
							}.onSuccess {
								ctx.source.sendMessage("成功更新物品定义数据")
								lastDatabaseUpdated = Date()
							}.onFailure {
								ctx.source.sendMessage("更新物品定义失败！${it.message}")
								ctx.source.sendMessage(it.stackTraceToString())
							}
						}
					}
				}

				literal("details") {
					executesX { ctx ->
						val lastItem = getContext(ctx.source).lastFindItem
						if(lastItem == null) {
							ctx.source.sendMessage("联系不到上下文呢！先用 '/d2 find <物品名称>' 确定物品试试？")
						} else {
							val lastItemEn = d2Db.findByHash(lastItem.hash!!, BungieLanguage.English)!!
							val text = """
								**${lastItem.displayName}** [${lastItemEn.displayName}] (${lastItem.hash})
								${lastItem.displayLore}
								[light.gg](${lastItem.getLightGGUrl()})
								[物品图标](${lastItem.displayProperties.icon?.toBungieResourceUrl()})
							""".trimIndent()
							ctx.source.sendMessage(text) { type = KMessageType.KMarkdown }
						}
					}
				}

			}
		}

}