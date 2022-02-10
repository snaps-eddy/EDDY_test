package com.snaps.mobile.presentation.editor.gallery

import com.snaps.mobile.presentation.editor.dialog.EditorDialogState

sealed class GalleryNavigation(val tag: String) {

    object AlbumList : GalleryNavigation(AlbumList::class.java.canonicalName ?: "AlbumList")
    object AlbumDetailList : GalleryNavigation(AlbumDetailList::class.java.canonicalName ?: "AlbumDetailList")
    object AlbumDetailListAsHome : GalleryNavigation(AlbumDetailListAsHome::class.java.canonicalName ?: "AlbumDetailListAsHome")
    object Complete : GalleryNavigation(Complete::class.java.canonicalName ?: "Complete")
    class Close(val hasChange: Boolean) : GalleryNavigation(Close::class.java.canonicalName ?: "Close")
    class Dialog(val dialogState: EditorDialogState) : GalleryNavigation("Dialog")

}