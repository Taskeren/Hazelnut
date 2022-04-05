package cn.taskeren.hazelnut

import city.warlock.d2api.compat.BungieLanguage
import city.warlock.d2api.entity.destiny.definitions.DestinyInventoryItemDefinition
import city.warlock.destinyDB.*
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import org.litote.kmongo.find

/**
 * 使用 $regex 正则匹配来模糊查询
 */
fun MongoCollection<DestinyInventoryItemDefinition>.findByNameFuzzy(name: String): FindIterable<DestinyInventoryItemDefinition> {
	return this.find("""{"displayProperties.name": { ${"$"}regex: "$name", ${"$"}options: "${"$"}i" }}""")
}

fun DestinyDatabase.findByHash(hash: UInt, lang: BungieLanguage = BungieLanguage.Chinese) =
	getItemDefinition(lang).findByHash(hash)

fun DestinyDatabase.findByName(name: String, lang: BungieLanguage = BungieLanguage.Chinese, fuzzy: Boolean = false) =
	if(fuzzy) {
		getItemDefinition(lang).findByNameFuzzy(name)
	} else {
		getItemDefinition(lang).findByName(name)
	}

val DestinyInventoryItemDefinition.displayName: String get() =
	displayProperties.name

val DestinyInventoryItemDefinition.displayLore: String get() =
	flavorText ?: String()

val DestinyInventoryItemDefinition.isWeapon: Boolean get() =
	itemCategoryHashes?.contains(1U) ?: false

val DestinyInventoryItemDefinition.isArmor: Boolean get() =
	itemCategoryHashes?.contains(20U) ?: false