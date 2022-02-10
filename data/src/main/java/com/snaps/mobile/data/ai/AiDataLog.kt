package com.snaps.mobile.data.ai

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.File
import javax.inject.Inject

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class AiDataLog @Inject constructor(
    private val context: Context,
    private val gson : Gson
) {
    companion object {
        private const val isActive: Boolean = true
    }

    fun writeAiResponse(
        fileName: String,
        response: LayoutRecommendResponseDto
    ) {
        try {
            if (isActive) {
                File(context.externalCacheDir, "aiResponse_${fileName}.json").apply {
                    if (isFile) delete()
                    writeText(gson.newBuilder().setPrettyPrinting().create().toJson(response))
                }

                File(context.externalCacheDir, "aiResponse_${fileName}_pretty.json").apply {
                    if (isFile) delete()
                    writeText(getPrettyJsonString(response))
                }
            }
        } catch (e: Exception) {
        }
    }

    fun writeAiResponseKeyList(
        fileName: String,
        scenesImageKeyList: List<List<String>>
    ) {
        if (isActive) {
            scenesImageKeyList.mapIndexed { index, list ->
                list.fold(StringBuilder("[${index}]\n")) { sb, seq ->
                    sb.append("${seq}\n")
                }.append("\n").toString()
            }.toList().fold(StringBuilder()) { sb, item ->
                sb.append(item)
            }.toString().let {
                File(context.externalCacheDir, "keyList_$fileName.txt").writeText(it)
            }
        }
    }

    //이거 너무 복잡하니까 수정하지 말아야지...
    private fun getPrettyJsonString(response: LayoutRecommendResponseDto): String {
        var index = 0;
        val keyPrefix = "_#_@_#_"
        val keySuffix = "_#_@_#_" + System.currentTimeMillis()
        val layoutDataMap = HashMap<String, String>()
        val sceneList: MutableList<LayoutRecommendResponseDto.Scene> = mutableListOf()
        response.scene?.forEach { scene ->
            var multiformPtr: LayoutRecommendResponseDto.Scene.Multiform? = null
            scene.multiform?.let { multiform ->
                val layoutList: MutableList<LayoutRecommendResponseDto.Scene.Multiform.Layouts> = mutableListOf()
                multiform.layouts?.forEach { layout ->
                    val key = keyPrefix + (index++) + keySuffix
                    layoutDataMap[key] = gson.toJson(
                        JsonParser.parseString(layout.data).asJsonObject
                    )
                    layoutList.add(layout.copy(data = key))
                }
                multiformPtr = multiform.copy(layouts = layoutList)
            }
            val pageList: MutableList<LayoutRecommendResponseDto.Scene.Pages> = mutableListOf()
            scene.pages?.forEach { page ->
                val layoutList: MutableList<LayoutRecommendResponseDto.Scene.Pages.Layouts> = mutableListOf()
                page.layouts?.forEach { layout ->
                    val key = keyPrefix + (index++) + keySuffix
                    layoutDataMap[key] = gson.toJson(
                        JsonParser.parseString(layout.data).asJsonObject
                    )
                    layoutList.add(layout.copy(data = key))
                }
                pageList.add(page.copy(layouts = layoutList))
            }
            sceneList.add(scene.copy(multiform = multiformPtr, pages = pageList))
        }

        var jsonText = GsonBuilder().setPrettyPrinting().create().toJson(response.copy(scene = sceneList))
        for ((key, value) in layoutDataMap) {
            jsonText = jsonText.replace("\"" + key + "\"", value)
        }
        return gson.newBuilder().setPrettyPrinting().create().toJson(
            JsonParser.parseString(jsonText).asJsonObject
        )
    }
}