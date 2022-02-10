package com.snaps.mobile.domain.product

import com.snaps.mobile.domain.error.SnapsThrowable

data class ProductInfo(
    val productCode: String,
    val coverType: String,
    val baseQuantity: Int,
    val maxQuantity: Int,
    val productType: String,
    val glossyType: String,
    val coverWidthPixel: Int,
    val coverWidthMilimeter: Int,
    val coverEdgeWidthMilimeter: Int,
    val coverSpineWidthPixel: Int,
    val coverXmlWidth: Int,
    val sizeInfo: List<Size>,
    val pagePixelWidth: Int,
    val pagePixelHeight: Int,
    private val productCodeInfo: ProductCodeInfo = ProductCodeInfo()
) {
    data class Size(
        val type: String,
        val mmWidth: Int,
        val mmHeight: Int,
        val pxWidth: Int,
        val pxHeight: Int
    )

    enum class PhotoBookSize {
        None,
        Size6X6,
        Size8X8,
        Size10X10,
        Size5X7,
        Size8X10,
        SizeA4
    }

    fun isPhotoBook(): Boolean {
        return productCodeInfo.isPhotoBook(productCode)
    }

    fun getPhotoBookSize(): PhotoBookSize {
        return when (productCodeInfo.getPhotoBookSize(productCode)) {
            ProductCodeInfo.PhotoBookSize.None -> PhotoBookSize.None
            ProductCodeInfo.PhotoBookSize.Size6X6 -> PhotoBookSize.Size6X6
            ProductCodeInfo.PhotoBookSize.Size8X8 -> PhotoBookSize.Size8X8
            ProductCodeInfo.PhotoBookSize.Size10X10 -> PhotoBookSize.Size10X10
            ProductCodeInfo.PhotoBookSize.Size5X7 -> PhotoBookSize.Size5X7
            ProductCodeInfo.PhotoBookSize.Size8X10 -> PhotoBookSize.Size8X10
            ProductCodeInfo.PhotoBookSize.SizeA4 -> PhotoBookSize.SizeA4
        }
    }

    fun isLeatherCover(): Boolean {
        return productCodeInfo.isLeatherCover(productCode)
    }

    fun getCoverSizeInfo(): Size? {
        return sizeInfo.find { it.type == "cover" }
    }

    fun getPageSizeInfo(): Size? {
        return sizeInfo.find { it.type == "page" }
    }

    fun isHardCover(): Boolean {
        return coverType == "hard" || coverType == "padding" || coverType == "leather"
    }

    fun isHardLeatherCover(): Boolean {
        return coverType == "leather"
    }

    fun isSoftCover(): Boolean {
        return coverType == "soft"
    }

    fun getPhotoBookHardCoverPixelPerMilimeter(): Float {
        return if (coverWidthPixel == 0 || coverEdgeWidthMilimeter == 0) {
            0f
        } else {
            coverWidthPixel.toFloat() / coverEdgeWidthMilimeter.toFloat()
        }
    }

    fun getPhotoBookSoftCoverPixelPerMilimeter(): Float {
        return if (coverWidthPixel == 0 || coverWidthMilimeter == 0) {
            0f
        } else {
            coverWidthPixel.toFloat() / coverWidthMilimeter.toFloat()
        }
    }

    fun getProductAspect(): ProductAspect {
        return when (pagePixelWidth / pagePixelHeight.toFloat()) {
            2.0f -> ProductAspect.Square
            0.5f -> ProductAspect.Vertical
            4.0f -> ProductAspect.Horizontal
            else -> throw SnapsThrowable("Not defined aspect. Width : $pagePixelWidth, Height : $pagePixelHeight")
        }
    }

//    fun getPhotoBookBaseSceneCount(): Int {
//        val coverSceneCount = 1
//        val titleSceneCount = 2
//        return baseQuantity * 2 + titleSceneCount + coverSceneCount
//    }
//
//    fun getPhotoBookMaxSceneCount(): Int {
//        val coverSceneCount = 1
//        val titleSceneCount = 2
//        return maxQuantity * 2 + titleSceneCount + coverSceneCount
//    }
}

