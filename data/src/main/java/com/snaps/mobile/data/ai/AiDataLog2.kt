package com.snaps.mobile.data.ai

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.template.TemplateSceneObject
import com.snaps.mobile.domain.template.ai.LayoutRecommendTemplate
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Gson 유니코드 파싱 문제 때문에 Gson을 주입 받아서 사용하도록.
 */
class AiDataLog2 @Inject constructor(
    private val context: Context,
    private val gson: Gson
) {
    companion object {
        private const val isWriteFile: Boolean = true
    }

    fun logPasringMultiform(
        tag: String,
        sceneIndex: Int,
        multiform: LayoutRecommendResponseDto2.Scene.Pages.Multiform,
        e: Exception
    ) {
        buildString {
            append(" \n")
            append("sceneIndex:${sceneIndex}\n")
            append("${e.message} \n")
            append("code: ${multiform.code} \n")
            append("data: ${multiform.data} \n")
        }.let { Dlog.e(tag, it) }
    }

    fun logLayoutRecommendTemplate(
        tag: String,
        thumbnailList: List<ImageThumbnail>,
        layoutRecommendTemplate: LayoutRecommendTemplate
    ) {
        val sceneToImageKeyList = mutableListOf<List<String>>()
        var imageKeyOffset = 0
        val cutLine = "-".repeat(16)
        val cutLine2 = "=".repeat(32)

        val totalObjectImageCount = layoutRecommendTemplate.template.scenes.fold(0) { total, scenes ->
            total + scenes.sceneObjects.count { it.type == AiDataProcess2.SCENE_OBJECT_TYPE_IMAGE }
        }
        val isValidImageCount = layoutRecommendTemplate.imageKeyList.size == totalObjectImageCount
        if (!isValidImageCount) {
            for (i in 0..20) Dlog.e(tag, "템플릿의 이미지 오브젝트와 추천 이미지 숫자 불일치 --> "
                    + "totalObjectImageCount: $totalObjectImageCount"
                    + "  imageKeyList.size: ${layoutRecommendTemplate.imageKeyList.size}")
        }

        layoutRecommendTemplate.template.scenes.mapIndexed { sceneIndex, scene ->
            val logScene = buildString {
                append(" \n")
                append("$cutLine2 [scene] ${sceneIndex + 1} $cutLine2 \n")
                val type = if (scene.type is Scene.Type.Cover) scene.type else scene.type.raw
                append("type: ${type}\n")
                append("subType: ${scene.subType.raw}\n")
                append("size: ${scene.width} x ${scene.height}\n")
            }
            val logSceneObjects = scene.sceneObjects.mapIndexed { sceneObjectIndex, sceneObject ->
                buildString {
                    append("$cutLine [sceneObject] ${sceneObjectIndex + 1} $cutLine\n")
                    append("type: ${sceneObject.type}\n")
                    append("subType: ${sceneObject.subType}\n")
                    append("position: ${sceneObject.x}, ${sceneObject.y}\n")
                    append("size: ${sceneObject.width} x ${sceneObject.height}\n")
                    when (sceneObject) {
                        is TemplateSceneObject.Background.Color -> append("bgColor: ${sceneObject.bgColor}\n")
                        is TemplateSceneObject.Background.Image -> {
                            append("resourceId: ${sceneObject.resourceId}\n")
                            append("middleImagePath: ${sceneObject.middleImagePath}\n")
                        }
                        is TemplateSceneObject.Sticker -> {
                            append("resourceId: ${sceneObject.resourceId}\n")
                            append("middleImagePath: ${sceneObject.middleImagePath}\n")
                        }
                        is TemplateSceneObject.Text.Spine -> {
                            append("name: ${sceneObject.name}\n")
                            append("text: ${sceneObject.text}\n")
                            append("placeholder: ${sceneObject.placeholder}\n")
                            append("readOnly: ${sceneObject.readOnly}\n")
                        }
                        is TemplateSceneObject.Text.User -> {
                            append("name: ${sceneObject.name}\n")
                            append("text: ${sceneObject.text}\n")
                            append("placeholder: ${sceneObject.placeholder}\n")
                            append("readOnly: ${sceneObject.readOnly}\n")
                        }
                        else -> ""
                    }
                }
            }.joinToString(separator = "")


            val imageKeys = if (isValidImageCount) {
                scene.sceneObjects.filter { it.type == AiDataProcess2.SCENE_OBJECT_TYPE_IMAGE }.let { objImage ->
                    layoutRecommendTemplate.imageKeyList.subList(imageKeyOffset, imageKeyOffset + objImage.size).let {
                        sceneToImageKeyList.add(it)
                        it.joinToString().also { imageKeyOffset += objImage.size }
                    }
                }.let {
                    "$cutLine imageKeyList $cutLine\n$it\n"
                }
            } else {
                ""
            }

//            val imageKeys = scene.sceneObjects.filter { it.type == AiDataProcess2.SCENE_OBJECT_TYPE_IMAGE }.let { objImage ->
//                layoutRecommendTemplate.imageKeyList.subList(imageKeyOffset, imageKeyOffset + objImage.size).let {
//                    sceneToImageKeyList.add(it)
//                    it.joinToString().also { imageKeyOffset += objImage.size }
//                }
//            }.let {
//                "$cutLine imageKeyList $cutLine\n$it\n"
//            }

            buildString {
                append(logScene)
                append(logSceneObjects)
                append(imageKeys)
                append("\n")
            }.also {
                Dlog.d(tag, it)
            }
        }.joinToString(separator = "\n").let { report ->
            if (isWriteFile) {
                File(context.externalCacheDir, "ai_template_report.txt").apply {
                    if (isFile) delete()

                    buildString {
                        val summaryTitle = buildString {
                            append("=".repeat(16)).append(" Summary ").append("=".repeat(16)).append("\n")
                        }.also {
                            append(it)
                        }
                        append("> creation time: ${SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.KOREA).format(Date())}\n")
                        append("> total scene: ${layoutRecommendTemplate.template.scenes.size}\n")
                        append("> total transfer image: ${thumbnailList.size}\n")
                        append("> total used image: ${layoutRecommendTemplate.imageKeyList.size}\n")

                        sceneToImageKeyList.filterIndexed { index, list ->
                            index > 0 && list.contains(layoutRecommendTemplate.imageKeyList.first())
                        }.let {
                            if (it.isNotEmpty()) {
                                append("> cover image scene no: ${sceneToImageKeyList.indexOf(it.first()) + 1}\n")
                            }
                        }

                        append("=".repeat(summaryTitle.length - 1)).append("\n\n\n")
                        append(report)
                    }.let {
                        writeText(it)
                    }
                }
            }
        }
    }

    fun writeAiResponse(
        fileName: String,
        response: LayoutRecommendResponseDto2
    ) {
        try {
            if (isWriteFile) {
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
            Dlog.e(e)
        }
    }

    //이거 너무 복잡하니까 수정하지 말아야지...
    private fun getPrettyJsonString(response: LayoutRecommendResponseDto2): String {
        var index = 0;
        val keyPrefix = "_#_@_#_"
        val keySuffix = "_#_@_#_" + System.currentTimeMillis()
        val multiformDataMap = HashMap<String, String>()
        val layoutDataMap = HashMap<String, String>()
        val sceneList: MutableList<LayoutRecommendResponseDto2.Scene> = mutableListOf()
        response.scene?.forEach { scene ->
            val pageList: MutableList<LayoutRecommendResponseDto2.Scene.Pages> = mutableListOf()
            scene.pages?.forEach { page ->
                val multiformList: MutableList<LayoutRecommendResponseDto2.Scene.Pages.Multiform> = mutableListOf()
                page.multiform?.forEach { multiform ->
                    val key = "_multiform_" + keyPrefix + (index++) + keySuffix
                    multiformDataMap[key] = gson.toJson(
                        JsonParser.parseString(multiform.data).asJsonObject
                    )
                    multiformList.add(multiform.copy(data = key))
                }
                val layoutList: MutableList<LayoutRecommendResponseDto2.Scene.Pages.Layouts> = mutableListOf()
                page.layouts?.forEach { layout ->
                    val key = "_layouts_" + keyPrefix + (index++) + keySuffix
                    if (!layout.data.isNullOrEmpty()) {
                        layoutDataMap[key] = gson.toJson(
                            JsonParser.parseString(layout.data).asJsonObject
                        )
                        layoutList.add(layout.copy(data = key))
                    }
                }
                pageList.add(page.copy(multiform = multiformList, layouts = layoutList))
            }
            sceneList.add(scene.copy(pages = pageList))
        }

        var jsonText = GsonBuilder().setPrettyPrinting().create().toJson(response.copy(scene = sceneList))
        for ((key, value) in multiformDataMap) {
            jsonText = jsonText.replace("\"" + key + "\"", value)
        }
        for ((key, value) in layoutDataMap) {
            jsonText = jsonText.replace("\"" + key + "\"", value)
        }
        return gson.newBuilder().setPrettyPrinting().create().toJson(
            JsonParser.parseString(jsonText).asJsonObject
        )
    }
}