package com.snaps.mobile.presentation.editor.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 갤러리 시작 Fragment에 대한 정보
 */
sealed class GalleryHome : Parcelable {

    @Parcelize
    object AlbumList : GalleryHome()

    @Parcelize
    class AlbumDetails(val maxAddMoreCount: Int) : GalleryHome()

}