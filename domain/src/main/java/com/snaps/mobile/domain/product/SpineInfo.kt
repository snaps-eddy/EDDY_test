package com.snaps.mobile.domain.product

data class SpineInfo(
    val version: String,
    val papers: List<Paper>,
) {
    data class Paper(
        val code: String,
        val millimeter: String,
        val mobileMaxpage: String,
        val spine: List<Spine>,
    ) {
        data class Spine(
            val minPages: Int,
            val maxPages: Int,
            val millimeter: String,
            val thickness: String,
            val number: String,
        )
    }


    //스펙 정의 될때 까지 홀드
//    fun getPhotoBookSoftCoverCoverAdjustmentInfo(
//        baseSceeneCount: Int,
//        paperCode: String,
//        pixelPerMilimeter: Float,
//        sceneCount: Int
//    ): CoverAdjustmentInfo {
//        val spineWidthPixel = getPhotoBookSoftCoverSpineWidthPixel(
//            paperCode = paperCode,
//            pixelPerMilimeter = pixelPerMilimeter,
//            sceneCount = sceneCount
//        )
//        val baseSpineWidthPixel = PHOTO_BOOK_HARD_COVER_BASE_SPINE_WIDTH_MM * pixelPerMilimeter
//
//        return CoverAdjustmentInfo(
//            coverWidthPixel = baseCoverWidthPixel - baseSpineWidthPixel + spineWidthPixel,
//            spineWidthPixel = spineWidthPixel,
//            isShowSpine = true,  //TODO
//            leftToCenterMoveX = 0f,
//            rightToCenterMoveX = spineWidthPixel
//        )
//    }
//
//    private fun getPhotoBookSoftCoverSpineWidthPixel(
//        paperCode: String,
//        pixelPerMilimeter: Float,
//        sceneCount: Int
//    ): Float {
//        val spineWidthMM = getPhotoBookSoftCoverSpineWidthMilimeter(paperCode = paperCode, sceneCount = sceneCount)
//        return spineWidthMM * pixelPerMilimeter
//    }
//
//    private fun getPhotoBookSoftCoverSpineWidthMilimeter(paperCode: String, sceneCount: Int): Float {
//        val coverSceneCount = 1
//        val paperThickness = findPhotoBookSoftCoverPaperThickness(paperCode)
//        val paperCountExcludingCover = (sceneCount - coverSceneCount) / 2
//        return paperThickness * paperCountExcludingCover
//    }
//
//    private fun findPhotoBookSoftCoverPaperThickness(paperCode: String): Float {
//        return try {
//            papers.first { it.code == paperCode }.millimeter.toFloat()
//        } catch (e: NoSuchElementException) {
//            throw RuntimeException("unknown paper code : $String")
//        }
//    }

    ////////////////////////////////////////////////////////////////////

    fun getPhotoBookHardCoverSpineNo(paperCode: String, spredSceneCount: Int): String {
        val spine = findPhotoBookHardCoverSpine(paperCode = paperCode, spredSceneCount = spredSceneCount)
        return spine?.number ?: "1"
    }

    fun getPhotoBookHardCoverSpineWidthMilimeter(paperCode: String, spredSceneCount: Int): Float {
        val spine = findPhotoBookHardCoverSpine(paperCode = paperCode, spredSceneCount = spredSceneCount)
        return spine?.millimeter?.toFloat() ?: 0f
    }


    private fun findPhotoBookHardCoverSpine(paperCode: String, spredSceneCount: Int): Paper.Spine? {
        val paper = try {
            papers.first { it.code == paperCode }
        } catch (e: NoSuchElementException) {
            throw RuntimeException("unknown paper code : $String")
        }
        val coverSpredSceneCount = 1
        val titleSpredSceneCount = 1
        val titlePageSceneCount = 1
        val printPageSceneCount = (spredSceneCount - coverSpredSceneCount - titleSpredSceneCount) * 2 + titlePageSceneCount

        return try {
            paper.spine.first { it.minPages <= printPageSceneCount && printPageSceneCount <= it.maxPages }
        } catch (e: NoSuchElementException) {
            val minSpine = paper.spine.minByOrNull { it.minPages }
            val minPage = minSpine?.minPages ?: 0
            if (printPageSceneCount < minPage) {
                minSpine
            } else {
                paper.spine.maxByOrNull { it.maxPages }
            }
        }
    }
}