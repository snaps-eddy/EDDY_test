package com.snaps.mobile.presentation.editor.imageEdit

import com.airbnb.epoxy.TypedEpoxyController
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.presentation.editor.imageEdit.itemview.filterImageItemView
import com.snaps.mobile.presentation.editor.imageEdit.model.FilterImageItemUiModel

class FilterImageController(
    private val callbacks: CallbacksListener
) : TypedEpoxyController<List<FilterImageItemUiModel>>() {

    override fun buildModels(data: List<FilterImageItemUiModel>?) {
        data?.forEach { itemData ->
            filterImageItemView {
                id(modelCountBuiltSoFar)
                translatedFilterName(itemData.filterName)
                filterUri(itemData.filteredImageUri)
                applied(itemData.isApplied)
                onSelect { callbacks.onSelectFilter(itemData.filter) }
            }
        }
    }

    interface CallbacksListener {

        fun onSelectFilter(filter: Filter)

    }

}