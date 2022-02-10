package com.snaps.mobile.data.product

import com.snaps.mobile.data.util.handleHttpError
import com.snaps.mobile.data.util.SERVICE_TEMPOLARY_UNAVAILABLE
import com.snaps.mobile.data.util.applyRetryPolicy
import com.snaps.mobile.domain.product.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remote: RemoteProductDataSource,
    private val local: LocalProductDataSource,
) : ProductRepository {

    override fun getInfos(productCode: String, templateCode: String): Single<MergeInfos> {
        return remote.getProductInfos(productCode, templateCode)
            .compose(applyRetryPolicy(SERVICE_TEMPOLARY_UNAVAILABLE))
            .doOnSuccess(local::cacheProduct)
            .map {
                MergeInfos(
                    productInfo = ProductInfo(
                        coverType = it.productInfo.coverType ?: "",
                        productCode = productCode,
                        baseQuantity = it.productInfo.baseQuantity,
                        maxQuantity = it.productInfo.maxQuantity,
                        productType = it.productInfo.productType,
                        glossyType = it.productInfo.glossyType,
                        coverWidthPixel = it.productInfo.coverXmlWidth,
                        coverWidthMilimeter = it.productInfo.coverMillimeterWidth,
                        coverEdgeWidthMilimeter = it.productInfo.coverEdgeWidth,
                        coverSpineWidthPixel = it.productInfo.coverMidWidth,
                        coverXmlWidth = it.productInfo.coverXmlWidth,
                        sizeInfo = listOf(
                            ProductInfo.Size(
                                "cover",
                                it.productInfo.coverMillimeterWidth,
                                it.productInfo.coverMillimeterHeight,
                                it.productInfo.coverXmlWidth,
                                it.productInfo.coverXmlHeight
                            ),
                            ProductInfo.Size(
                                "title",
                                it.productInfo.titleMillimeterWidth,
                                it.productInfo.titleMillimeterHeight,
                                it.productInfo.pagePixelWidth,
                                it.productInfo.pagePixelHeight
                            ),
                            ProductInfo.Size(
                                "page",
                                it.productInfo.pageMillimeterWidth,
                                it.productInfo.pageMillimeterWidth,
                                it.productInfo.pagePixelWidth,
                                it.productInfo.pagePixelHeight
                            )
                        ),
                        pagePixelWidth = it.productInfo.pagePixelWidth,
                        pagePixelHeight = it.productInfo.pagePixelHeight
                    ),
                    templateInfo = TemplateInfo(),
                    templatePriceInfo = TemplatePriceInfo()
                )
            }
            .compose(handleHttpError())
    }

    override fun getProductPolicy(productCode: String, templateCode: String): Single<ProductPolicy> {
        return local.getProductPolicy(productCode, templateCode)
    }

    override fun getSpineInfo(): Single<SpineInfo> {
        return local.getRawSpineInfo()
            .map { spineInfoDto ->
                spineInfoDto.papers.map { paper ->
                    paper.spine.map { spine ->
                        SpineInfo.Paper.Spine(
                            minPages = spine.pageMin.toInt(),
                            maxPages = spine.pageMax.toInt(),
                            millimeter = spine.millimeter ?: "",
                            thickness = spine.thickness,
                            number = spine.number,
                        )
                    }.let {
                        SpineInfo.Paper(
                            code = paper.code,
                            millimeter = paper.millimeter,
                            mobileMaxpage = paper.mobileMaxpage,
                            spine = it,
                        )
                    }
                }.let {
                    SpineInfo(
                        version = spineInfoDto.version,
                        papers = it
                    )
                }
            }
    }
}