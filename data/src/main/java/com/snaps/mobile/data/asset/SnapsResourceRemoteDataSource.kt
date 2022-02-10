package com.snaps.mobile.data.asset

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.product.ProductAspect
import com.snaps.mobile.domain.save.Scene
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SnapsResourceRemoteDataSource @Inject constructor(
    private val snapsResourceApi: SnapsResourceApi
) {

    /**
     * Snaps 배경 리스트.
     * 배경 resourceType : 039040
     * etc.
     * pageType을 빈값으로 보내면 정사각 이미지가 내려온다고 함.
     */
    fun getBackgroundImages(productCode: String, aspect: ProductAspect): Single<GetSnapsResourceResponse> {
        Dlog.d("Aspect : $aspect, code : ${aspect.code}")
        return snapsResourceApi.getResources(
            resourceType = backgroundClassCode,
            productCode = productCode,
            pageType = pagePageCode,
            subType = spreadSubType,
            directionType = aspect.code
        )

    }

    /**
     * resourceType : 045020
     * pageType : 159001 (cover)
     * pageType : 159002 (inner)
     */
    fun getCoverCatalog(productCode: String, aspect: ProductAspect): Single<GetSnapsResourceResponse> {
        return snapsResourceApi.getResources(
            resourceType = multiformClassCode,
            productCode = productCode,
            pageType = coverPageCode,
            directionType = aspect.code
        )
    }

    /**
     * resourceType : 045030 - 커버, 045031 - 타이틀, 045032 - 내지
     * pageTytpe : 400001 - page , 400002 - spread
     */
    fun getLayouts(productCode: String, type: Scene.Type, aspect: ProductAspect): Single<GetSnapsResourceResponse> {
        return snapsResourceApi.getResources(
            resourceType = layoutClassCode,
            productCode = productCode,
            pageType = when (type) {
                is Scene.Type.Cover -> coverPageCode
                Scene.Type.Page -> pagePageCode
            },
            subType = when (type) {
                is Scene.Type.Cover -> spreadSubType
                Scene.Type.Page -> pageSubType
            },
            directionType = aspect.code
        )
    }

    companion object {
        // 21 개 - 700 개
        // 사진 추가 100개

        //resource class code
        private const val multiformTemplateClassCode: String = "045021"
        private const val multiformClassCode: String = "045020"
        private const val layoutClassCode: String = "045032"
        private const val backgroundClassCode: String = "039040"

        //pageCode cover 인가 내지인
        private const val coverPageCode: String = "364001"
        private const val pagePageCode: String = "364003"

        //subtype가 쪽단위 인가 장단위인가
        private const val pageSubType: String = "400001"
        private const val spreadSubType: String = "400002"

        //itemCode
        private const val frontItemCode: String = "159003"
        private const val backItemCode: String = "159004"

        //directionCode 정/가/세
//        private const val widthDirectionCode: String = "194001"
//        private const val heightDirectionCode: String = "194002"
//        private const val squareDirectionCode: String = "194003"
        /**
         * 포토북은 itemcode 빼고 다 넣는걸로..
         */
    }

}