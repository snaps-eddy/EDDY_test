package com.snaps.mobile.presentation.editor.imageEdit.model

import com.snaps.mobile.domain.save.Filter

data class FilterImageItemUiModel(
    val filter: Filter,
    val filteredImageUri: String,
    val filterName: String,
) {
    var isApplied: Boolean = false
}