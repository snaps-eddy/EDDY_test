package com.snaps.mobile.domain

import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.save.SceneObject
import io.kotest.core.spec.style.BehaviorSpec
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import kotlin.math.cos
import kotlin.math.sin

class MeasurePaperFullPositionTest : BehaviorSpec({

    given("이미지틀 사이즈, 유저 이미지 사이즈") {
//        <object type="image" bgColor="16777215" angle="0" angleClip="0.00" uploadType="file" file="2021072218490377519.png" imgYear="2021" imgSeq="0099702347"
//        x="0.0" y="-399.0" width="539" height="1167" clipX="0.0" clipY="0.0" clipWidth="539" clipHeight="369" mask="" alpha="255" borderOption="inner" bordersinglecolortype=""/>

        val clipX = 0f
        val clipY = 40f
        val clipWidth = 213f
        val clipHeight = 325f

        val originWidth = 3648f
        val originHeight = 2736f

        val orientation = 6 // 90

        val sceneObject = SceneObject.Image().apply {
            drawIndex = "33"
            x = clipX
            y = clipY
            width = clipWidth
            height = clipHeight
        }

        /**
         * 오리엔테이션 먹인 Width, Height 로 계산할 것.
         */
        val thumbnailImage = ImageThumbnail(
            imgSeq = "",
            year = "",
            localId = "",
            type = AssetImageType.Device,
            thumbnailUri = "",
            originWidth = originWidth,
            originHeight = originHeight,
            thumbnailRemotePath = "",
            analysisInfo = AnalysisInfo(),
            orientation = orientation,
            date = "",
        )

        `when`("Measure !") {

            sceneObject.insertImage(thumbnailImage)


            then("Should be") {
                sceneObject.innerImage?.let {
                    println(it)
                    assertThat(it.width.toInt()).isEqualTo(325)
                    assertThat(it.height.toInt()).isEqualTo(244)

                    assertThat(it.x).isEqualTo(-68.594)
                    assertThat(it.y).isEqualTo(-40.667)

                }
            }
        }
    }
})

