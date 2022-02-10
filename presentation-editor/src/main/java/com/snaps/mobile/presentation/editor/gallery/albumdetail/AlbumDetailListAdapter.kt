package com.snaps.mobile.presentation.editor.gallery.albumdetail

import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.IdUtils
import com.snaps.mobile.presentation.editor.gallery.AlbumImageSet

class AlbumDetailListAdapter(
    private val callback: CallBacks
) : EpoxyAdapter() {

    init {
        spanCount = 3
    }

    fun setData(data: AlbumImageSet?) {
        if (models.isNotEmpty()) {
            removeAllModels()
        }

        data?.forEach { groupedImages ->
            val key = groupedImages.first().date
            addModel(AlbumDetailItemHeaderView_().apply {
                id(key.toDays)
                children(groupedImages)
                groupChecked(groupedImages.find { !it.selected } == null)
                spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
                checkClickListener { _, willSelect ->
                    callback.onSelectGroup(willSelect, groupedImages) { results ->
                        if (groupedImages.size != results.size) {
                            throw IllegalStateException("Original children size and results size diffrent !!.")
                        }

                        //Update Header
                        children(results)
                        groupChecked(willSelect && results.find { !it.selected } == null)
                        notifyModelChanged(this, PAYLOAD_UPDATE_SELECTION)

                        //Update children
                        models
                            .filterIsInstance<AlbumDetailItemView_>()
                            .filter { it.parentKey == key.toDays }
                            .mapIndexed { index, model ->
                                model.apply {
                                    itemChecked(results[index].selected)
                                }
                            }
                            .run {
                                notifyItemRangeChanged(this.first().position(), results.size.coerceAtMost(20), PAYLOAD_UPDATE_SELECTION)
                            }
                    }
                }
                /**
                 * 차일드 중에 선택되거나 해제되면 호출된다.
                 */
                onChildSelect {
                    groupChecked(children().find { !it.selected } == null)
                    notifyModelChanged(this, PAYLOAD_UPDATE_SELECTION)
                }
            })

            groupedImages.map { detailItem ->
                AlbumDetailItemView_().apply {
                    id(detailItem.id)
                    parentKey(key.toDays)
                    detailItem(detailItem)
                    uri(detailItem.thumbnailUri)
                    itemChecked(detailItem.selected)
                    position(models.size)
                    clickListener { _, _, _, _ -> callback.onClickImage(detailItem) }
                    selectListener { willSelect ->
                        callback.onSelectImage(willSelect, detailItem) { updateSelectState(it) }
                    }
                    spanSizeOverride { _, _, _ -> 1 }
                }
            }.run {
                addModels(this)
            }
        }
    }

    fun updateSingleItem(newData: AlbumDetailItem) {
        models.filterIsInstance<AlbumDetailItemView_>()
            .find { it.id() == IdUtils.hashString64Bit(newData.id) }
            ?.run {
                updateSelectState(newData)
            }
    }

    private fun AlbumDetailItemView_.updateSelectState(newData: AlbumDetailItem) {
        detailItem(newData)
        itemChecked(newData.selected)
        notifyModelChanged(this, PAYLOAD_UPDATE_SELECTION)
        models
            .filterIsInstance<AlbumDetailItemHeaderView_>()
            .find { it.id() == parentKey() }
            ?.let { header ->
                header.onChildSelect(newData.selected)
            }
    }

    interface CallBacks {

        fun onClickImage(image: AlbumDetailItem)

        fun onSelectImage(willSelect: Boolean, image: AlbumDetailItem, callback: (AlbumDetailItem) -> Unit)

        fun onSelectGroup(willSelect: Boolean, groupItems: List<AlbumDetailItem>, callback: (List<AlbumDetailItem>) -> Unit)

    }

    companion object {
        const val PAYLOAD_UPDATE_SELECTION = "payload key update selection"
    }

}