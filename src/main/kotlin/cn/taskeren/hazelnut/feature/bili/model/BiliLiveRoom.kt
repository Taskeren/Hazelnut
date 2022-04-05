package cn.taskeren.hazelnut.feature.bili.model

import com.google.gson.annotations.SerializedName

data class BiliLiveRoom(
	val roomStatus: Int,
	val roundStatus: Int,
	val liveStatus: Int,
	val url: String,
	val title: String,
	val cover: String,
	val online: Int,
	val roomId: Int,
	@SerializedName("broadcast_type")
	val broadcastType: Int,
	@SerializedName("online_hidden")
	val onlineHidden: Int,
	val link: String
) {

	/**
	 * 是否存在房间
	 */
	fun isValidRoom() = roomStatus == 1

	/**
	 * 是否正在轮播
	 */
	fun isRoundPlaying() = roundStatus == 1

	/**
	 * 是否正在直播
	 */
	fun isLiving() = liveStatus == 1

}
