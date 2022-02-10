package com.snaps.mobile.data.project

data class UploadProjectResponseDto (
    val projectCode : String?,
    val countryCompanyCode : String?,
    val projectName : String?,
    val productCode : String?,
    val finishStatus : String?,
    val templateCode : String?,
    val imageYear : String?,
    val imageSequence : String?,
    val affxName : String?,
    val xmlPath : String?,
    val albumType : String?,
    val deleteYN : String?,
    val userNo : Int = 0,
    val registrationDate : String?,
    val lastUpdate : String?,
    val editUserNo : String?,
    val appType : String?,
    val osType : String?,
    val paperCode : String?,
    val usePhotoCount : String?,
    val frameCode : String?,
    val appVersion : String?,
    val backCode : String?,
    val colorCode : String?,
    val designCnt : String?,
    val quantity : String?,
) {
    fun toPrettyString(): String {
        val list = toString().split(", ")
        val sb = StringBuilder().append(this::class.java.simpleName).append("\n")
        list.forEachIndexed{ index, item ->
            sb.append(
                when(index) {
                    0 -> item.substring(this::class.java.simpleName.length + 1)
                    list.size - 1 -> item.substring(0, item.length - 1)
                    else -> item
                }
            ).append("\n")
        }
        return sb.toString()
    }
}