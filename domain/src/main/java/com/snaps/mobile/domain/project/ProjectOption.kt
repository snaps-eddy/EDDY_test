package com.snaps.mobile.domain.project

import com.snaps.mobile.domain.product.SpineInfo

data class ProjectOption(
    val projectCode: String,
    var productCode: String = "",
    var templateCode: String = "",
    var projectName: String = "",
    var pageAddCount: Int = 0,
    var productType: String = "",
    var finishStatus: String = "Y", //주문 불가 여부
    var quantity: Int = 1, //청첩장 같은 카드 상품은 수량이 1이 아닐 수 있다.
    var glossyType: String = "",  //현재 모든 상품에 glossType 있다.
    var paperCode: String = "",  //현재 모든 상품에 paperCode 있다.
    var affxName: String = "",  //액자 상품에서 특이한 값을 생성한다.
    var frameCode: String = "",
    var frameType: String = "",
    var coatingYN: String = "",
    var backType: String = "",
    var spineNo: String = "",
    var spineVersion: String = "",
    var calendarStartDate: String = "",
    var calendarEndDate: String = "",
    var orderFile: String = "",
    var convertJsonYN: String = "Y",
) {
    fun toPrettyString(): String {
        val list = toString().split(", ")
        val sb = StringBuilder().append(this::class.java.simpleName).append("\n")
        list.forEachIndexed { index, item ->
            sb.append(
                when (index) {
                    0 -> item.substring(this::class.java.simpleName.length + 1)
                    list.size - 1 -> item.substring(0, item.length - 1)
                    else -> item
                }
            ).append("\n")
        }
        return sb.toString()
    }

    fun setTemplateCode(templateCode: String): Boolean {
        this.templateCode = templateCode
        return true
    }

    /**
     * @return 원래 저장된 타이틀과 같은지 틀린지 비교값
     */
    fun setProjectName(willTitle: String): Boolean {
        val isChanged = this.projectName == willTitle
        this.projectName = willTitle
        return isChanged
    }

    fun setSpineInfo(spineInfo: SpineInfo, spineNo: String) {
        this.spineVersion = spineInfo.version
        this.spineNo = spineNo
    }

    fun setPageAddCount(pageAddCount: Int): Boolean {
        this.pageAddCount = pageAddCount
        return true
    }

    fun setAffxName(affxName: String): Boolean {
        this.affxName = affxName
        return true
    }

    fun setRecommendProjectName(recommendProjectName: String): String {
        if (projectName.isBlank()) {
            setProjectName(recommendProjectName)
        }
        return projectName
    }

//    val isNotSetTitle: Boolean
//        get() {
//            return projectName.isBlank()
//        }
//
//    val isSetTitle: Boolean
//        get() {
//            return projectName.isNotBlank()
//        }
}