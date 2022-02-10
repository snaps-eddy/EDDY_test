package com.snaps.mobile.domain.project

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.asset.RecipeImage
import com.snaps.mobile.domain.product.*
import com.snaps.mobile.domain.save.*
import com.snaps.mobile.domain.template.Template
import com.snaps.mobile.domain.template.TemplateScene
import java.util.*
import kotlin.math.abs


/**
 * @Makro
 * 프로젝트는 백엔드에 존재하는 개념이지만 프로젝트 자체를 넘겨주는 API 는 없다.
 * 프로젝트는 application 에서 만들고, 그에 필요한 정보들을 각각의 API를 통해 채워서 사용한다.
 */
data class Project(
    val code: String,
) {

    private val originalImages: MutableList<ImageOriginal> = mutableListOf()
    private val thumbnailImages: MutableList<ImageThumbnail> = mutableListOf()
    private val recipeImages: MutableList<RecipeImage> = mutableListOf()

    lateinit var save: Save
        private set
    lateinit var projectOption: ProjectOption
        private set
    lateinit var productInfo: ProductInfo
        private set
    lateinit var templateInfo: TemplateInfo
        private set
    lateinit var templatePriceInfo: TemplatePriceInfo
        private set
    lateinit var spineInfo: SpineInfo
        private set

    fun putRecipeImage(images: List<RecipeImage>): Int {
        recipeImages.clear()
        recipeImages.addAll(images)
        return recipeImages.size
    }

    fun addMoreRecipeImage(images: List<RecipeImage>): Project {
        val recipeLocalIds = recipeImages.map { it.localId }
        recipeImages.addAll(images.filterNot { it.localId in recipeLocalIds })
        return this
    }

    fun getRecipeImage(): List<RecipeImage> {
        return recipeImages
    }

    fun getRecipeImageNotUpload(): List<RecipeImage> {
        return recipeImages.filter { it.year.isNullOrBlank() && it.imgSeq.isNullOrBlank() }
    }

    fun loadAiTemplate(template: Template, imageKeyList: List<String>, recommendProjectName: String) {
        val projectName = projectOption.setRecommendProjectName(recommendProjectName)

        save.applyTeamplte(template)
        val usingImages = imageKeyList.map { imageKey ->
            thumbnailImages.find { it.outputImageSequence == imageKey }
        }
        save.insertImageContent(LinkedList(usingImages))
        save.checkWarningResolution(productInfo)

        save.scenes.find { it.type is Scene.Type.Cover }?.sceneObjects?.apply {
            filterIsInstance<SceneObject.Text.Spine>().forEach { it.text = projectName }
            filterIsInstance<SceneObject.Text.User>().filter { it.name == "title" }.forEach { it.text = projectName }
//            filterIsInstance<SceneObject.Text.User>().filter { it.name != "title" }.forEach {
//                for (i in 0..20) Dlog.e("땜방", "포토북 커버의 템플릿 책등 정보가 없어서 그냥 텍스트 오프젝트에 텍스트 넣고 있다!!!")
//                it.text = projectName
//            }
        }
    }

    fun updateRecipeImage(receipt: ImageLoadReceipt) {
        val updatedImage = recipeImages.indices.find {
            recipeImages[it].localId == receipt.localId
        }?.run {
            val original = recipeImages[this]
            recipeImages[this] = original.copy(imgSeq = receipt.imgSeq, year = receipt.year, remoteUri = receipt.thumbnailRemotePath)
            recipeImages[this]
        } ?: throw RuntimeException("Failed update image.")

//        Dlog.d(recipeImages.map { it.remoteUri })

        val thumbnail = ImageThumbnail(
            imgSeq = receipt.imgSeq,
            year = receipt.year,
            localId = receipt.localId,
            thumbnailRemotePath = receipt.thumbnailRemotePath,
            originWidth = receipt.width,
            originHeight = receipt.height,
            thumbnailUri = updatedImage.localUri,
            type = updatedImage.type,
            analysisInfo = receipt.analysisInfo,
            orientation = receipt.orientation,
            date = receipt.exifData,
        )

        if (thumbnail in thumbnailImages) {
            val index = thumbnailImages.indexOf(thumbnail)
            thumbnailImages[index] = thumbnail
        } else {
            thumbnailImages.add(thumbnail)
        }
    }

    fun getImageThumbnail(): List<ImageThumbnail> {
        return thumbnailImages
    }

    fun swapImage(fromSceneIndex: String, fromSceneObjectIndex: String, toSceneIndex: String, toSceneObjectIndex: String): Save {
        save.swapUserImages(fromSceneIndex, fromSceneObjectIndex, toSceneIndex, toSceneObjectIndex)
        save.checkWarningResolution(productInfo)
        return save
    }

    fun getWillUploadImageList(): List<ImageThumbnail> {
        return thumbnailImages
            .filter { thumb -> originalImages.none { thumb.outputImageSequence == it.outputImageSequence } }
            .filter { thumb -> getImageContentInSave().map { it.outputImageSequence }.contains(thumb.outputImageSequence) }
    }

    fun updateUploadedImage(thumbnail: ImageThumbnail): Boolean {
        val originalImage = ImageOriginal(
            imgSeq = thumbnail.imgSeq,
            year = thumbnail.year
        )
        if (originalImage in originalImages) {
            val index = originalImages.indexOf(originalImage)
            originalImages[index] = originalImage
        } else {
            originalImages.add(originalImage)
        }
        return true
    }

    fun setProjectOption(projectOption: ProjectOption): Project {
        this.projectOption = projectOption
        return this
    }

    fun setInfos(mergedInfos: MergeInfos): Project {
        this.productInfo = mergedInfos.productInfo
        this.templateInfo = mergedInfos.templateInfo
        this.templatePriceInfo = mergedInfos.templatePriceInfo
        return this
    }

    fun setSpineInfo(spineInfo: SpineInfo): Project {
        this.spineInfo = spineInfo
        return this
    }

    fun getSceneLayoutCode(currentSceneDrawId: String): String {
        return save.findScene(currentSceneDrawId)?.layoutCode ?: throw Exception("Not found Scene")
    }

    fun getThumbnailsInScene(withAddImgSeq: String? = null, withDeleteImgSeq: String? = null, sceneId: String): List<ImageThumbnail> {
        return mutableListOf<ImageThumbnail>().apply {
            this.addAll(
                save.findScene(sceneId)
                    ?.sceneObjects
                    ?.filterIsInstance<SceneObject.Image>()
                    ?.mapNotNull { it.content }
                    ?.map {
                        thumbnailImages.first { thumbnailImage -> thumbnailImage.imgSeq == it.imgSeq }
                    }
                    ?: throw Exception("Not found Scene")
            )

            if (!withAddImgSeq.isNullOrBlank()) {
                this.add(thumbnailImages.first { it.imgSeq == withAddImgSeq })
            }

            if (!withDeleteImgSeq.isNullOrBlank()) {
                this.remove(thumbnailImages.first { it.imgSeq == withDeleteImgSeq })
            }
        }
    }

    fun updateAnalysisInfo(analysisInfoMap: Map<String, AnalysisInfo>) {
        analysisInfoMap.forEach { (imageKey, analysisInfo) ->
            thumbnailImages.find { it.outputImageSequence == imageKey }?.let { imageThumbnail ->
                if (imageThumbnail.analysisInfo.isAnalysis) return@forEach
                val index = thumbnailImages.indexOf(imageThumbnail)
                thumbnailImages[index] = imageThumbnail.copy(analysisInfo = analysisInfo)
            }
        }
    }

    fun updateSingleScene(currentSceneDrawId: String, recommendScene: TemplateScene, imageKeyList: List<String>): Save {
        val usingImages = imageKeyList.map { imageKey ->
            thumbnailImages.find { it.outputImageSequence == imageKey }
        }
        save.changeScene(currentSceneDrawId, recommendScene, LinkedList(usingImages))
        return save.checkWarningResolution(productInfo)
    }

    fun addPairScene(scene: TemplateScene, imageKeyList: List<String>, prevSceneDrawIndex: String) {
        val usingImages = imageKeyList.map { imageKey ->
            thumbnailImages.find { it.outputImageSequence == imageKey }
        }
        save.addPairScene(scene, LinkedList(usingImages), prevSceneDrawIndex)
        save.checkWarningResolution(productInfo)
    }

    fun setlocationSearch(locationSearch: String): Project {
        save.info.locationSearch = locationSearch
        return this
    }

    fun setUserAgent(userAgent: String): Project {
        save.info.userAgent = userAgent
        return this
    }

    fun setSave(save: Save): Project {
        this.save = save
        this.thumbnailImages.clear()
        this.recipeImages.clear()
        this.save.scenes
            .flatMap { it.sceneObjects }
            .filterIsInstance<SceneObject.Image>()
            .mapNotNull { it.content }
            .forEach { imageContent ->
                imageContent.run {
                    thumbnailImages.add(
                        ImageThumbnail(
                            year = imageContent.year,
                            imgSeq = imageContent.imgSeq,
                            localId = "",
                            type = AssetImageType.Device,
                            thumbnailUri = "",  // TODO 이거 맞나?
                            originWidth = imageContent.width,
                            originHeight = imageContent.height,
                            thumbnailRemotePath = imageContent.middleImagePath,
                            analysisInfo = imageContent.analysisInfo,
                            orientation = imageContent.orientation,
                            date = imageContent.date,
                        )
                    )

                    recipeImages.add(
                        RecipeImage(
                            year = imageContent.year,
                            imgSeq = imageContent.imgSeq,
                            localId = "",
                            type = AssetImageType.Remote,
                            localUri = "",
                            remoteUri = imageContent.middleImagePath,  // TODO 이거 맞나?
                            width = imageContent.width,
                            height = imageContent.height,
                            orientation = imageContent.orientation
                        )
                    )

                    originalImages.add(
                        ImageOriginal(
                            imgSeq = imageContent.imgSeq,
                            year = imageContent.year
                        )
                    )
                }
            }
        return this
    }

    fun setTemplateCode(templateCode: String): Boolean {
        return projectOption.setTemplateCode(templateCode)
    }

    fun setProjectName(willTitle: String): Boolean {
        val result = projectOption.setProjectName(willTitle)
        save.updateProjectName(willTitle)
        return result
    }

    fun getProjectName(): String {
        return projectOption.projectName
    }

    fun swapScenes(fromDrawIndex: String, toDrawIndex: String): Save {
        save.swapScenes(fromDrawIndex, toDrawIndex)
        return save
    }

    /**
     * @param before -> dstScene 앞에 넣을 것인지에 대한 flag
     */
    fun moveScene(targetDrawIndex: String, dstDrawIndex: String, after: Boolean): Save {
        save.moveScene(targetDrawIndex, dstDrawIndex, after)
        return save
    }

    fun extractUserImage(sceneDrawIndex: String) {
        save.findScene(sceneDrawIndex)
            ?.sceneObjects?.run {
                this.filterIsInstance<SceneObject.Image>()
                    .forEach {
                        this.remove(it)
                    }
            }
    }

    fun checkWarningResolutions(): Project {
        save.checkWarningResolution(productInfo)
        return this
    }

    private fun findThumbnailImage(imgSeq: String): ImageThumbnail? {
        return thumbnailImages.find { it.imgSeq == imgSeq }
    }

    fun replaceUserImage(targetImgSeq: String, dstSceneDrawIndex: String, dstSceneObjectDrawIndex: String) {
        findThumbnailImage(targetImgSeq)
            ?.let {
                save.insertImageToSceneObject(it, dstSceneDrawIndex, dstSceneObjectDrawIndex)
            }
    }

    fun getUserImagesIn(sceneDrawIndex: String): List<ImageThumbnail> {
        return save.findScene(sceneDrawIndex)
            ?.sceneObjects
            ?.filterIsInstance<SceneObject.Image>()
            ?.mapNotNull { it.content }
            ?.mapNotNull { content ->
                thumbnailImages.find { it.imgSeq == content.imgSeq }
            } ?: listOf()
    }

    fun findUserImage(targetImgSeq: String): ImageThumbnail? {
        return thumbnailImages.find { it.imgSeq == targetImgSeq }
    }

    fun updateSaveInfo(): Project {
        save.updateSaveInfo()
        return this
    }

    fun isMaxScene(): Boolean {
        return if (productInfo.isPhotoBook()) {
            val coverSpreadSceneCount = 1
            val titleSpreadSceneCount = 1
            val sceneCount = save.getSpreadSceneCount() - coverSpreadSceneCount - titleSpreadSceneCount
            sceneCount >= productInfo.maxQuantity
//            sceneCount >= 400  //TODO::테스트 (오바마 커버 테스트)
        } else {
            //TODO
            true
        }
    }

    fun isMinScene(): Boolean {
        return if (productInfo.isPhotoBook()) {
            val coverSpreadSceneCount = 1
            val titleSpreadSceneCount = 1
            val sceneCount = save.getSpreadSceneCount() - coverSpreadSceneCount - titleSpreadSceneCount
            sceneCount <= productInfo.baseQuantity
        } else {
            //TODO
            true
        }
    }

    fun setPhotoBookCoverType(): Project {
        if (!productInfo.isPhotoBook()) return this

        save.scenes.first { it.type is Scene.Type.Cover }.apply {
            type = Scene.Type.Cover(
                isLeather = productInfo.isHardLeatherCover(),
                isSoft = productInfo.isSoftCover()
            )
        }
        return this
    }

    fun adjustPhotoBookHardCoverSpineThickness(): Project {
        if (!productInfo.isHardCover()) return this

        val coverAdjustmentInfo = getPhotoBookHardCoverAdjustmentInfo()
        save.scenes.first { it.type is Scene.Type.Cover }.apply {
            midWidth = coverAdjustmentInfo.midWidth
        }

        val adjustOffsetX = coverAdjustmentInfo.coverWidthPixel - save.scenes.first { it.type is Scene.Type.Cover }.width
        if (abs(adjustOffsetX) < 1f) return this

        save.scenes.first { it.type is Scene.Type.Cover }.apply {
            //앞 커버에 있는 것들 오른쪽으로 밀기
            sceneObjects.filter { it.x > width / 2f }.forEach {
                it.x += adjustOffsetX
            }

            //가로 크기 늘리기
            width = productInfo.coverXmlWidth.toFloat().coerceAtLeast(width + adjustOffsetX)

            //첵등 텍스트 가운데 정렬 시키기
            sceneObjects.filterIsInstance<SceneObject.Text.Spine>().forEach {
                it.x = (width - it.width) / 2  //TODO:: 여러개??
            }

            //배경 가운데 정렬
            sceneObjects.filterIsInstance<SceneObject.Background>().forEach {
                it.x = (width - it.width) / 2
            }
        }
        return this
    }

    //소프트/하드 포토북 공통으로 사용하려고 만들긴했는데...
    private data class CoverAdjustmentInfo(
        val coverWidthPixel: Float,
        val spineWidthPixel: Float,
        val midWidth: Float,
        val isShowSpine: Boolean,
        val leftToCenterMoveX: Float,
        val rightToCenterMoveX: Float,
    )

    //메모 : spineInfo, projectOption, save, productInfo 사용
    private fun getPhotoBookHardCoverAdjustmentInfo(): CoverAdjustmentInfo {
        val hardCoverBasicSpineWidthmm = 8f  // 매직!! (컨디에서 커버를 디자인 할때 책등을 8mm라고 가정하고 디자인 했다.)
        val spineWidthmm = spineInfo.getPhotoBookHardCoverSpineWidthMilimeter(
            paperCode = projectOption.paperCode,
            spredSceneCount = save.getSpreadSceneCount()
        )
        val pixelPerMilimeter = productInfo.getPhotoBookHardCoverPixelPerMilimeter()
        val addSpineWidthPixel = run {
            val addSpineWidthmm = spineWidthmm - hardCoverBasicSpineWidthmm
            addSpineWidthmm * pixelPerMilimeter
        }

        return CoverAdjustmentInfo(
            coverWidthPixel = productInfo.coverXmlWidth + addSpineWidthPixel,
            spineWidthPixel = addSpineWidthPixel,
            isShowSpine = true,
            midWidth = spineWidthmm * pixelPerMilimeter,
            leftToCenterMoveX = 0f,
            rightToCenterMoveX = addSpineWidthPixel
        )
    }

    /**
     * 현재 Save의 설정된 페이지 수에 맞는 스파인 정보를 projectOption에 업데이트 한다.
     */
    fun measureSpineNo(): Project {
        val spineNo = run {
            if (productInfo.isHardCover()) {
                val spreadSceneCount = save.getSpreadSceneCount()
                spineInfo.getPhotoBookHardCoverSpineNo(projectOption.paperCode, spreadSceneCount)
            } else {
                ""
            }
        }
        projectOption.setSpineInfo(spineInfo, spineNo)
        return this
    }

    fun measurePagesAddCount(): Project {
        if (productInfo.isPhotoBook()) {
            val baseSpreadSceneCount = run {
                val coverSpreadSceneCount = 1
                val titleSpreadSceneCount = 1
                productInfo.baseQuantity + coverSpreadSceneCount + titleSpreadSceneCount
            }
            projectOption.setPageAddCount(save.getSpreadSceneCount() - baseSpreadSceneCount)
        }
        return this
    }

    fun setAffxName(appVersion: String): Project {
        val androidOsType = "190002"
        projectOption.setAffxName(androidOsType + "_" + appVersion)
        return this
    }

    /**
     * Save 에 사용된 모든 이미지
     */
    private fun getImageContentInSave(): List<ImageContent> {
        return save.getAllImageContents()
    }

    inline fun <reified T : SceneObject> findSceneObject(sceneDrawIndex: String, sceneObjectDrawIndex: String): T? {
        return save.findSceneObject(sceneDrawIndex, sceneObjectDrawIndex)
    }

    inline fun <reified T : SceneObject> findSceneObject(sceneObjectDrawIndex: String): T? {
        return save.findSceneObject(sceneObjectDrawIndex)
    }

    fun findThumbnail(imgSeq: String): ImageThumbnail? {
        return thumbnailImages.find { it.imgSeq == imgSeq }
    }

    fun deleteRecipeImage(targetImgSeq: String): Boolean {
        // if exist save ?
        val imagesInSave = save.findImages(targetImgSeq)
        if (imagesInSave.isNotEmpty()) {
            // Save에 있는 이미지는 지울 수 없다.
            return false
        }
        return recipeImages
            .find { it.imgSeq == targetImgSeq }
            ?.run {
                recipeImages.remove(this)
            } ?: false
    }

    fun addScenes(emptyLeft: Scene, emptyRight: Scene): Boolean {
        return save.scenes.run {
            val resultLeft = add(emptyLeft)
            val resultRight = add(emptyRight)
            resultLeft && resultRight
        }
    }

    fun deleteScenes(rightSceneDrawIndex: String): Boolean {
        return save.deleteScenes(rightSceneDrawIndex)
    }

    /**
     * 커버 변경.
     * 1. backup image content
     * 2. change scene property
     * 3. change sceneobjects property
     * 4. fill image content
     * 5. Spine 텍스트와 name 값이 "title" 인 TextObject 에 프로젝트 이름 넣어준다.
     */
    fun changeCoverSceneTemplate(templateScene: TemplateScene) {
        save.getCoverScene()?.apply {
            val imageContents = LinkedList(this.getImageContents())

            this.copyPropertyFrom(templateScene)
            this.copySceneObjectsFrom(templateScene.sceneObjects)
            this.fillImageContents(imageContents)
            this.fillTitleText(getProjectName())
        }
    }

    /**
     * 백그라운드만 변경.
     */
    fun changeSceneBackground(dstSceneDrawIndex: String, resourceId: String, resourceUri: String) {
        save.findScene(dstSceneDrawIndex)?.apply {
            this.changeBackground(resourceId, resourceUri)
        }
    }

    fun getPageSceneCount(): Int {
        val coverSpreadSceneCount = 1
        val titleSpreadSceneCount = 1
        return save.getSpreadSceneCount() - coverSpreadSceneCount - titleSpreadSceneCount
    }

    fun updateSceneObjectText(sceneObjectDrawIndex: String, text: String, textAlign: TextAlign, hexColor: String) {
        save.findSceneObject<SceneObject.Text>(sceneObjectDrawIndex)?.apply {
            this.text = text
            this.defaultStyle = this.defaultStyle.copy(
                textAlign = textAlign,
                color = hexColor
            )
        }
        Dlog.d("is Update ? ${save.findSceneObject<SceneObject.Text>(sceneObjectDrawIndex)}")
    }

    /**
     * 하단 트레이 "레이아웃" 으로 변경하는 기능
     */
    fun changeSceneLayout(dstSceneDrawIndex: String, layoutTemplate: TemplateScene) {
        save.applyLayout(dstSceneDrawIndex, layoutTemplate)
    }

    /**
     * Ai 추천 "레이아웃" 으로 변경하는 기능
     */
    fun applyAiRecommendLayout(currentSceneDrawId: String, recommendScene: TemplateScene, imageKeyList: List<String>): Save {
        val usingImages = imageKeyList.map { imageKey ->
            thumbnailImages.find { it.outputImageSequence == imageKey }
        }
        save.applyAiRecommendLayout(currentSceneDrawId, recommendScene, LinkedList(usingImages))
        return save.checkWarningResolution(productInfo)
    }
}