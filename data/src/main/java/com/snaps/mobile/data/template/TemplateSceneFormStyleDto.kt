package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

class TemplateSceneFormStyleDto (
    @SerializedName("@type")
    val type: String = "",
    @SerializedName("@subType")
    val subType: String = ""
)