package com.snaps.mobile.presentation.editor.gallery.albumdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.airbnb.epoxy.*
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.snaps.mobile.presentation.editor.BuildConfig
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.FragmentAlbumDetailListBinding
import com.snaps.mobile.presentation.editor.gallery.GalleryViewModel
import com.snaps.mobile.presentation.editor.gallery.albumlist.AlbumListController
import com.snaps.mobile.presentation.editor.gallery.albumlist.AlbumListItem
import com.snaps.mobile.presentation.editor.utils.GridSpacingItemDecoration
import com.snaps.mobile.presentation.editor.utils.dp
import com.snaps.mobile.presentation.editor.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

@AndroidEntryPoint
class AlbumDetailListFragment : Fragment(), AlbumDetailListAdapter.CallBacks, AlbumListController.AdapterCallbacksListener,
    SelectImageBucketController.AdapterCallbacksListener {

    private var _binding: FragmentAlbumDetailListBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<GalleryViewModel>(ownerProducer = { requireParentFragment() })
    private val albumImagesAdapter = AlbumDetailListAdapter(this)
    private val epoxyVisibilityTracker = EpoxyVisibilityTracker()
    private val albumController = AlbumListController(this)
    private val selectBucketController = SelectImageBucketController(this)

    private val openAlbumCategory = ConstraintSet()
    private val closeAlbumCategory = ConstraintSet()

    private val trans = ChangeBounds().apply {
        this.interpolator = DecelerateInterpolator()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (binding.tvCurrentAlbum.isChecked) {
                binding.tvCurrentAlbum.isChecked = false
            } else {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlbumDetailListBinding.inflate(inflater, container, false).apply {
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

            closeAlbumCategory.clone(root)
            openAlbumCategory.apply {
                clone(root)
                connect(rvAlbums.id, ConstraintSet.TOP, appbar.id, ConstraintSet.BOTTOM)
                connect(rvAlbums.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                setElevation(rvAlbums.id, 1.dp().toFloat())
            }

            tvCurrentAlbum.setOnCheckedChangeListener { _, isOpen ->
                TransitionManager.beginDelayedTransition(root, trans)
                if (isOpen) {
                    openAlbumCategory.applyTo(root)
                } else {
                    closeAlbumCategory.applyTo(root)
                }
            }

            btnAddImageDone.setOnClickListener {
                vm.onClickAddBucket()
            }

            if (BuildConfig.DEBUG) {
                btnAddImageDone.setOnLongClickListener {
                    vm.onLongClickAddBucket()
                    true
                }
            }


            rvPhotos.apply {
                adapter = albumImagesAdapter
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 3).apply {
                    spanSizeLookup = albumImagesAdapter.spanSizeLookup
                }
                addItemDecoration(EpoxyItemSpacingDecorator(3.dp()))
                addGlidePreloader(
                    requestManager = Glide.with(this@AlbumDetailListFragment),
                    preloader = glidePreloader { requestManager: RequestManager, epoxyModel: AlbumDetailItemView_, _: ViewData<ViewMetadata?> ->
                        requestManager.loadImage(epoxyModel.uri, isPreloading = true)
                    },
                    maxPreloadDistance = 6
                )
            }

            rvAlbums.apply {
                setHasFixedSize(true)
                setController(albumController)
                layoutManager = GridLayoutManager(context, 2)
                addItemDecoration(GridSpacingItemDecoration(16.dp(), true))
            }

            rvSelections.apply {
                setController(selectBucketController)
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
            }

            // For visibility tracking
            epoxyVisibilityTracker.also {
                it.attach(rvPhotos)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.currentAlbumName.observe(viewLifecycleOwner, {
            binding.tvCurrentAlbum.text = if (it.length > 20) it.substring(0, 20) + "..." else it
        })

        vm.albumImages.observe(viewLifecycleOwner, {
            albumImagesAdapter.setData(it)
        })

        vm.albumList.observe(viewLifecycleOwner, {
            albumController.setData(it)
        })

        vm.imageSelectionCount.observe(viewLifecycleOwner, {
            val spanner = Spanner()
            if (it > 0) {
                spanner
                    .append("($it)", Spans.foreground(ContextCompat.getColor(requireContext(), R.color.sunglo)))
                    .append(" ")
            }
            binding.btnAddImageDone.text = spanner.append(getString(R.string.add_bucket))
        })

        vm.imageSelection.observe(viewLifecycleOwner, { list ->
            val preBucketDataCount = selectBucketController.currentData?.size ?: 0
            selectBucketController.setData(list)
            lifecycleScope.launch {
                if (list.isEmpty()) {
                    if (binding.rvSelections.isVisible) binding.rvSelections.isVisible = false
                } else {
                    if (!binding.rvSelections.isVisible) binding.rvSelections.apply {
                        isVisible = true
                        AlphaAnimation(0f, 1f).apply { duration = 200 }
                    }

                    if (preBucketDataCount < list.size) {
                        delay(200)
                        binding.rvSelections.scrollToPosition(list.size - 1)
                    }
                }
            }
        })
    }

    override fun onClickImage(image: AlbumDetailItem) {
        vm.onClickImage(image)
    }

    override fun onSelectImage(willSelect: Boolean, image: AlbumDetailItem, callback: (AlbumDetailItem) -> Unit) {
        vm.onSelectImage(willSelect, image, callback)
    }

    override fun onSelectGroup(willSelect: Boolean, groupItems: List<AlbumDetailItem>, callback: (List<AlbumDetailItem>) -> Unit) {
        vm.onSelectGroup(willSelect, groupItems, callback)
    }

    override fun onClickAlbum(album: AlbumListItem) {
        vm.onChangeAlbum(album)
        binding.tvCurrentAlbum.isChecked = false
    }

    override fun onClickDeleteTrayImage(image: AlbumDetailItem) {
        vm.onClickDeleteTrayItem(image) {
            albumImagesAdapter.updateSingleItem(image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with(binding) {
            rvAlbums.adapter = null
            rvPhotos.adapter = null
            rvSelections.adapter = null
        }
        _binding = null
    }
}