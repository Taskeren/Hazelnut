package cn.taskeren.hazelnut.feature.bili

import cn.taskeren.hazelnut.core.util.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody

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