package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName
import com.snaps.mobile.domain.save.Scene
import com.snaps.mobile.domain.template.Template

data class TemplateDto(
    @SerializedName("scene")
    val scenes: List<TemplateSceneDto> = listOf()
)
