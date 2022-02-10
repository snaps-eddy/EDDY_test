package com.snaps.mobile.data.project

import android.content.Context
import com.google.gson.Gson
import com.snaps.common.utils.constant.Config
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.data.BuildConfig
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class NetworkErrorLog @Inject constructor (
    private val context: Context,
    private val gson : Gson
) {
    private val prefName = "network_error_log"
    private val prefKey = "log"
    private val maxLineCount = 128
    private val isDevelopVersion = Config.isDevelopVersion()

    fun write(
        tag: String,
        throwable: Throwable,
        additionalInfoMap: HashMap<String, String> = hashMapOf()
    ) {
        if (!isDevelopVersion) return

        val additionalInfo = if (additionalInfoMap.size > 0) ", $additionalInfoMap" else ""
        val message = if (throwable is HttpException) {
            throwable.message + " -> " + getMessage(throwable) + additionalInfo
        } else {
            throwable.message + additionalInfo
        }

        val logcatMsg = StringBuilder()
            .append(" \n")
            .append(getErrorText())
            .append(" \n \n")
            .append(message)
            .append(" \n \n")
            .toString()
        Dlog.e("NetworkErrorLog", logcatMsg)

        context.getSharedPreferences(prefName, Context.MODE_PRIVATE).apply {
            val logLines = getString(prefKey, "")?.let {
                it.split("\n").let { list ->
                    val sb = StringBuffer()
                    list.subList(0, list.size.coerceAtMost(maxLineCount)).forEach{ logLine ->
                        if (sb.isNotEmpty()) sb.append("\n")
                        sb.append(logLine)
                    }
                    sb.toString()
                }
            } ?: ""

            val timeStamp = SimpleDateFormat("MM-dd HH:mm:ss", Locale.KOREA).format(Date())
            edit().putString(prefKey, "$timeStamp $tag: $message\n$logLines").apply()
        }
    }

    private fun getErrorText(): String {
        return StringBuilder()
            .append("███████╗██████╗ ██████╗  ██████╗ ██████╗").append("\n")
            .append("██╔════╝██╔══██╗██╔══██╗██╔═══██╗██╔══██╗").append("\n")
            .append("█████╗  ██████╔╝██████╔╝██║   ██║██████╔╝").append("\n")
            .append("██╔══╝  ██╔══██╗██╔══██╗██║   ██║██╔══██╗").append("\n")
            .append("███████╗██║  ██║██║  ██║╚██████╔╝██║  ██║").append("\n")
            .append("╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝")
            .toString()
    }

    private fun getMessage(httpException: HttpException): String? {
        val errorBody = httpException.response()?.errorBody()
        val errorBodyText = errorBody?.string()
        val msg = gson.fromJson(errorBodyText, HttpResponseErrorBody::class.java).toString()
        return msg.replace("HttpResponseErrorBody", "")
    }

    data class HttpResponseErrorBody(
        val status: String?,
        val message: String?,
        val errorMessage: String?,
        val errorCode: String?,
    )
}