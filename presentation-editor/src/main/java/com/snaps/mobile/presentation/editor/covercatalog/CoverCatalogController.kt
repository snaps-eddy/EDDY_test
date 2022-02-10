package com.snaps.mobile.presentation.editor.covercatalog

import com.airbnb.epoxy.TypedEpoxyController

/**
 * 만약 예상되는 커버리스트의 갯수가 600개가 넘어간다면
 * TypedEpoxyController 대신에 Adapter를 직접 구현해서 사용하면 성능 향상의 효과가 있음.
 */
class CoverCatalogController(
    private val callback: Callback
) : TypedEpoxyController<List<CoverCatalogItem>>() {

    override fun buildModels(data: List<CoverCatalogItem>?) {
        data?.forEach { coverData ->
            coverCatalogItemView {
                id(modelCountBuiltSoFar)
                coverThumbnailUri(coverData.coverThumbnailUri)
                selected(coverData.isSelected)
                onCheckCover {
                    callback.onSelectCover(coverData, it)
                }
            }
        }
    }


    interface Callback {

        fun onSelectCover(item: CoverCatalogItem, isChecked: Boolean)

    }

}