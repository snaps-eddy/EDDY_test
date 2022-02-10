package com.snaps.mobile.domain.save

import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.domain.product.ProductInfo
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.template.Template
import com.snaps.mobile.domain.template.TemplateScene
import java.util.*

class Save(
    var scenes: MutableList<Scene> = mutableListOf(),
    val info: SaveInfo
) {

    fun updateSaveInfo() {
        info.increasingNumberOfEdits()
        info.updateSaveDate()
    }

    fun applyTeamplte(template: Template) {
        template.scenes.mapIndexed { index, templateScene ->
            Scene().apply {
                this.applySceneTemplate(index, templateScene)
            }.also {
                scenes.add(it)
            }
        }
    }

    fun insertImageContent(imageList: Queue<ImageThumbnail?>) {
        scenes.flatMap { it.sceneObjects }
            .filterIsInstance(SceneObject.Image::class.java)
            .map { sceneObjectImage ->
                imageList.poll()?.let {
                    sceneObjectImage.insertImage(it)
                }
            }
    }

    /**
     * Save 밖에 있는 이미지를 SceneObject.Image에 넣는 기능.
     */
    fun insertImageToSceneObject(targetImage: ImageThumbnail, sceneId: String, sceneObjectId: String) {
        findSceneObject<SceneObject.Image>(sceneId, sceneObjectId)?.insertImage(targetImage)
    }

    /**
     * 사진틀에 들어간 사진 두개를 스왑하는 기능.
     */
    fun swapUserImages(fromSceneId: String, fromSceneObjectId: String, toSceneId: String, toSceneObjectId: String) {
        val fromContent = findSceneObject<SceneObject.Image>(fromSceneId, fromSceneObjectId)?.content
        val toContent = findSceneObject<SceneObject.Image>(toSceneId, toSceneObjectId)?.content

        if (fromContent == null || toContent == null) {
            return
        }

        findSceneObject<SceneObject.Image>(fromSceneId, fromSceneObjectId)?.changeImageContent(toContent)
        findSceneObject<SceneObject.Image>(toSceneId, toSceneObjectId)?.changeImageContent(fromContent)
    }

    /**
     * 사진 해상도 체크.
     * 해상도 계산시 필요한 size info는 Product Info에 들어있다.
     * 만약 최종 스펙에 Scene 안에 mmSize와 pxSize가 들어가게 되면 편할 듯?
     */
    fun checkWarningResolution(product: ProductInfo): Save {
        val coverSizeInfo = product.getCoverSizeInfo()
        val pageSizeInfo = product.getPageSizeInfo()

        if (coverSizeInfo == null || pageSizeInfo == null) {
            // throw exception or pass default ??
            Dlog.e("Missing product Size Info")
            return this
        }

        scenes.map { scene ->
            // 해당 씬에 맞는 mm, px 을 가져온다.
            val sceneSizeInfo = when (scene.type) {
                is Scene.Type.Cover -> coverSizeInfo
                Scene.Type.Page -> pageSizeInfo
            }
            // 가로 세로 같은 비율로 하나만 구한다.
            val sceneRatio = sceneSizeInfo.pxWidth / sceneSizeInfo.mmWidth.toFloat()
            // 현재 씬의 비율에 맞는 PPC를 구한다.
            val mmPPCRatio = mmPPC / sceneRatio
            scene.sceneObjects.filterIsInstance<SceneObject.Image>().map {
                it.content?.run {
                    val availableMaxWidth = this.width.div(mmPPCRatio)
                    val availableMaxHeight = this.height.div(mmPPCRatio)
                    this.warningResolution = availableMaxWidth < it.innerImage?.width ?: 0f || availableMaxHeight < it.innerImage?.height ?: 0f
                }
            }
        }
        return this
    }

    fun changeScene(currentSceneDrawId: String, newTemplateScene: TemplateScene, imageList: Queue<ImageThumbnail?>) {
        findScene(currentSceneDrawId)?.changeTemplate(newTemplateScene, imageList)
    }

    fun addPairScene(recommendScene: TemplateScene, imageList: Queue<ImageThumbnail?>, prevSceneDrawIndex: String) {
        findScene(prevSceneDrawIndex)?.let { findScene ->
            val prevIndex = scenes.indexOf(findScene)
            Scene().apply {
                this.makeEmptyScene(prevIndex + 1, findScene)
                this.applyAiRecommendLayout(recommendScene, imageList)
            }.also {
                scenes.add(prevIndex + 1, it)
            }
            Scene().apply {
                this.makeEmptyScene(prevIndex + 2, findScene)
            }.also {
                scenes.add(prevIndex + 2, it)
            }
        }
    }

    fun swapScenes(fromDrawIndex: String, toDrawIndex: String) {
        val fromSceneIndex = findScene(fromDrawIndex).run { scenes.indexOf(this) }
        val toSceneIndex = findScene(toDrawIndex).run { scenes.indexOf(this) }

        Collections.swap(scenes, fromSceneIndex, toSceneIndex)
    }

    fun moveScene(targetDrawIndex: String, dstDrawIndex: String, after: Boolean) {
        findScene(targetDrawIndex)?.let { targetScene ->
            val currentIndex = targetScene.run { scenes.indexOf(this) }
            scenes.removeAt(currentIndex)

            val dstIndex = findScene(dstDrawIndex).run {
                val index = scenes.indexOf(this)
                if (after) index + 1 else index
            }
            scenes.add(dstIndex, targetScene)
        }
    }

    companion object {
        private const val PPC = 40
        const val mmPPC = PPC / 10
    }

    inline fun <reified T : SceneObject> findSceneObject(sceneDrawIndex: String, sceneObjectDrawIndex: String): T? {
        return findScene(sceneDrawIndex)
            ?.sceneObjects?.filterIsInstance<T>()
            ?.find { it.drawIndex == sceneObjectDrawIndex }
    }

    inline fun <reified T : SceneObject> findSceneObject(sceneObjectDrawIndex: String): T? {
        return scenes.map { it.sceneObjects }.flatten()
            .filterIsInstance<T>()
            .find { it.drawIndex == sceneObjectDrawIndex }
    }

    fun findScene(currentSceneDrawId: String): Scene? {
        return scenes.find { it.drawIndex == currentSceneDrawId }
    }

    fun getAllImageContents(): List<ImageContent> {
        return scenes
            .flatMap { it.sceneObjects }
            .filterIsInstance<SceneObject.Image>()
            .mapNotNull { it.content }
    }

    fun findImages(targetImgSeq: String): List<ImageContent> {
        return scenes
            .flatMap { it.sceneObjects }
            .filterIsInstance<SceneObject.Image>()
            .mapNotNull { it.content }
            .filter { it.imgSeq == targetImgSeq }
    }

    fun deleteScenes(rightSceneDrawIndex: String): Boolean {
        val rightIndex = scenes.indexOfFirst { it.drawIndex == rightSceneDrawIndex }
        val leftIndex = rightIndex - 1
        if (scenes[leftIndex].subType != Scene.SubType.Page) {
            return false
        }

        scenes.removeAt(leftIndex)
        scenes.removeAt(leftIndex)
        return true
    }

    fun getSpreadSceneCount(): Int {
        val spreadSceneCount = scenes.filter { it.subType == Scene.SubType.Spread }.size
        val pageSceneCount = scenes.filter { it.subType != Scene.SubType.Spread }.size
        return spreadSceneCount + pageSceneCount / 2
    }

    fun getCoverScene(): Scene? {
        return scenes.firstOrNull {
            // 첫번째가 커버지만 혹시나 해서 ...
            it.type is Scene.Type.Cover
        }
    }

    fun updateProjectName(willTitle: String) {
        getCoverScene()?.apply {
            sceneObjects.filterIsInstance<SceneObject.Text.Spine>().forEach { it.text = willTitle }
            sceneObjects.filterIsInstance<SceneObject.Text.User>().filter { it.name == "title" }.forEach { it.text = willTitle }
        }
    }

    fun applyAiRecommendLayout(currentSceneDrawId: String, recommendScene: TemplateScene, imageList: LinkedList<ImageThumbnail?>) {
        findScene(currentSceneDrawId)?.applyAiRecommendLayout(recommendScene, imageList)
    }

    fun applyLayout(currentSceneDrawId: String, recommendScene: TemplateScene) {
        findScene(currentSceneDrawId)?.applyLayout(recommendScene)
    }
}