package cn.taskeren.hazelnut.feature.bili

import okhttp3.*

object BiliApi {

	private val cli = OkHttpClient()

	fun getLiveStatus(roomId: Int): Result<ResponseBody> {
		return runCatching {
			val url = HttpUrl.Builder()
				.scheme("https")
				.host("api.live.bilibili.com")
				.addPathSegments("/room/v1/Room/getRoomInfoOld")
				.addQueryParameter("mid", "$roomId")
				.build()
			cli.newCall(Request.Builder().url(url).get().build()).execute().body!!
		}
	}
}