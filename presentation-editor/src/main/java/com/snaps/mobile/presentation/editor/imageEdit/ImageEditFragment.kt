package com.snaps.mobile.presentation.editor.imageEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.google.android.material.button.MaterialButton
import com.snaps.mobile.domain.save.Filter
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.FragmentImageEditBinding
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageEditFragment : Fragment() {
    private var _binding: FragmentImageEditBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<ImageEditViewModel>()

    private val _filteredPreviewImagesVisible = MutableLiveData(false)

    private val hideFilteredPreviewImages = ConstraintSet()
    private val showFilteredPReviewImages = ConstraintSet()

    private val showTrans = ChangeBounds().apply {
        this.duration = 100
        this.interpolator = AccelerateDecelerateInterpolator()
    }

    private val hideTrans = ChangeBounds().apply {
        this.duration = 100
        this.interpolator = AccelerateDecelerateInterpolator()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImageEditBinding.inflate(inflater, container, false).apply {
            tvTopMenuLeft.setOnClickListener {
                activity?.onBackPressed()
            }

            tvTopMenuRight.setOnClickListener {
                updateImageEditParams()
                vm.applyPhotoEdits()
            }

            btnPreviousPhoto.setOnClickListener {
                updateImageEditParams()
                vm.showPreviousPhoto()
            }

            btnNextPhoto.setOnClickListener {
                updateImageEditParams()
                vm.showNextPhoto()
            }

            btnRotatePhoto.setOnClickListener {
                if (binding.ivPhoto.isSetBitmap()) {
                    ivPhoto.rotate()
                }
            }

            btnResetEdit.setOnClickListener {
                if (binding.ivPhoto.isSetBitmap()) {
                    ivPhoto.reset()
                    rvFilters.scrollToPosition(0)
                    vm.updatePreviewListApplied(Filter.None())
                }
            }

            btnPhotoFilter.setOnClickListener {
                if (binding.ivPhoto.isSetBitmap()) {
                    _filteredPreviewImagesVisible.value = _filteredPreviewImagesVisible.value?.not()
                }
            }

            rvFilters.apply {
                rvFilters.setController(filterImageController)
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                addItemDecoration(EpoxyItemSpacingDecorator(12.dp()))
            }

            hideFilteredPreviewImages.clone(root)
            showFilteredPReviewImages.apply {
                clone(root)
                val rvFilterId = rvFilters.id
                clear(rvFilterId, ConstraintSet.TOP)
                connect(rvFilterId, ConstraintSet.BOTTOM, btnNextPhoto.id, ConstraintSet.TOP)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.filteredImageUri.observe(viewLifecycleOwner, {
            binding.ivPhoto.applyFilteredImage(it)
        })

        vm.titleText.observe(viewLifecycleOwner, {
            binding.tvTitle.text = it
        })

        vm.imageEditParams.observe(viewLifecycleOwner, {
            binding.ivPhoto.setImageEditParams(it)

            context?.let { ctx ->
                binding.btnPreviousPhoto.apply {
                    val color = if (vm.isFirstPhoto()) R.color.light_grey else R.color.white
                    (this as MaterialButton).setIconTintResource(color)
                    setTextColor(ContextCompat.getColor(ctx, color))
                    isClickable = !vm.isFirstPhoto()
                }

                binding.btnNextPhoto.apply {
                    val color = if (vm.isLastPhoto()) R.color.light_grey else R.color.white
                    (this as MaterialButton).setIconTintResource(color)
                    setTextColor(ContextCompat.getColor(ctx, color))
                    isClickable = !vm.isLastPhoto()
                }
            }
        })

        vm.isApplied.observe(viewLifecycleOwner, {
            setFragmentResult("ImageEditFragment", bundleOf(KEY_SCENE_USER_IMAGE_CHANGED to true))
            activity?.onBackPressed()
        })

        vm.filterPreviews.observe(viewLifecycleOwner, { list ->
            if (list.firstOrNull { it.filteredImageUri.isNullOrEmpty() } == null) {
                filterImageController.setData(list)
            } else {
                _filteredPreviewImagesVisible.value = false
            }
        })

        _filteredPreviewImagesVisible.observe(viewLifecycleOwner, {
            toggleFilterImages(it)
        })
    }

    private fun updateImageEditParams() {
        vm.updateImageEditParams(binding.ivPhoto.getImageEditResult())
    }

    private fun toggleFilterImages(show: Boolean) {
        if (show) {
            TransitionManager.beginDelayedTransition(binding.root, showTrans)
            showFilteredPReviewImages.applyTo(binding.root)
            (binding.btnPhotoFilter as? MaterialButton)?.setIconTintResource(R.color.bittersweet)
        } else {
            TransitionManager.beginDelayedTransition(binding.root, hideTrans)
            hideFilteredPreviewImages.applyTo(binding.root)
            (binding.btnPhotoFilter as? MaterialButton)?.setIconTintResource(R.color.white)
        }
    }

    private val filterImageController = FilterImageController(object : FilterImageController.CallbacksListener {
        /**
         * RecyclerView Item Callbacks
         */
        override fun onSelectFilter(filter: Filter) {
            vm.onSelectFilter(filter)
        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_SCENE_USER_IMAGE_CHANGED = "key scene user image changed"
    }
}