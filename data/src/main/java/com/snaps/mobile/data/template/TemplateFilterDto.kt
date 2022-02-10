package com.snaps.mobile.data.template

import com.google.gson.annotations.SerializedName

data class TemplateFilterDto(

    @SerializedName("@code")
    val code: String? = null,
    @SerializedName("@name")
    val name: String? = null,
)