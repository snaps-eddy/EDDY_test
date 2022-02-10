package com.snaps.mobile.presentation.editor.ai_progress

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.snaps.common.utils.constant.Config
import com.snaps.mobile.presentation.editor.BuildConfig
import com.snaps.mobile.presentation.editor.dialog.EditorDialogState
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.FragmentAiProgressBinding
import com.snaps.mobile.presentation.editor.dialog.EditorDialogService
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class AiProgressFragment : Fragment() {
    private var _binding: FragmentAiProgressBinding? = null
    private val binding get() = _binding!!

    private var isPlayAnimation: Boolean = true
    private lateinit var imageViewA: ImageView
    private lateinit var imageViewB: ImageView
    private lateinit var imageViewTarget: ImageView

    private lateinit var textViewDescA: TextView
    private lateinit var textViewDescB: TextView
    private lateinit var textViewDescTarget: TextView

    private val vm by viewModels<AiProgressViewModel>()
    private val activityVm by activityViewModels<EditorViewModel>()

    @Inject
    lateinit var editorDialogService: EditorDialogService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiProgressBinding.inflate(inflater, container, false).apply {
            btnBack.setOnClickListener {
                showUploadCancelDialog()
            }

            btnCancel.setOnClickListener {
                showUploadCancelDialog()
            }

            btnRetryUpload.setOnClickListener {
                btnCancel.visibility = View.VISIBLE
                btnRetryUpload.visibility = View.GONE
                vm.resumeUpload()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.btnRetryUpload.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        vm.currentProjectName.observe(viewLifecycleOwner, {
            binding.tvTitle.text = if (it.isNullOrEmpty()) {
                getString(R.string.auto_recommand_making_photobook)
            } else {
                val title = if (it.length >= 16) it.subSequence(0, 16).toString() + "..." else it
                String.format(getString(R.string.auto_recommand_making_photobook_with_format), title)
            }
        })

        vm.networkError.observe(viewLifecycleOwner, {
            binding.btnCancel.visibility = View.GONE
            binding.btnRetryUpload.visibility = View.VISIBLE

            if (it.first) {
                if (BuildConfig.DEBUG) {
                    context?.let { context ->
                        Toast.makeText(context, it.second, Toast.LENGTH_LONG).show()
                    }
                }

                editorDialogService.showDialog(
                    EditorDialogState.Notice(
                        message = getString(R.string.smart_snaps_analysis_network_disconnect_alert)
                    )
                )
            }
        })

        vm.playAnimation.observe(viewLifecycleOwner, {
            isPlayAnimation = it
        })

        imageViewA = binding.ivSlideA
        imageViewB = binding.ivSlideB
        imageViewTarget = imageViewA
        vm.slideImage.observe(viewLifecycleOwner, {
            slideImageAnimation(imageUri = it.first, time = it.second)
        })

        textViewDescA = binding.tvDescA
        textViewDescB = binding.tvDescB
        textViewDescTarget = textViewDescA
        vm.productDesc.observe(viewLifecycleOwner, {
            slideProductDescAnimation(productDesc = it)
        })

        vm.userName.observe(viewLifecycleOwner, {
            showUserName(userName = it.first, time = it.second)
        })

        vm.taskProgress.observe(viewLifecycleOwner, {
            binding.pbTaskProgress.progress = it
            binding.tvProgressPercentage.text = ("$it%")
        })

        vm.finishProgress.observe(viewLifecycleOwner, {
            if (it) {
                activityVm.onCompleteAiProgressProcess()
                if (BuildConfig.DEBUG) {
                    if (binding.tvLapTimeMsg.text.isNotEmpty()) {
                        Toast.makeText(context, binding.tvLapTimeMsg.text, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        if (Config.isDevelopVersion()) {
            binding.tvLapTimeMsg.visibility = View.VISIBLE
            vm.lapTimeMsg.observe(viewLifecycleOwner, {
                binding.tvLapTimeMsg.text = it
            })
        }
    }

    private fun showUploadCancelDialog() {
        editorDialogService.showDialog(
            EditorDialogState.Choice(
            message = getString(R.string.smart_analysis_product_cancel_making_confirm_msg),
            rightAction = { activity?.onBackPressed() }
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editorDialogService.hideDialog()
        _binding = null
    }

    private enum class AnimationType {
        LEFT_TO_RIGHT,
        LEFT_TO_RIGHT_WEAK,
        LEFT_TO_RIGHT_ZOOM_IN,
        LEFT_TO_RIGHT_ZOOM_OUT,
        ZOOM_IN,
        ZOOM_IN_WEAK,
        ZOOM_OUT,
        ZOOM_OUT_WEAK
    }

    private fun slideImageAnimation(imageUri: String, time: Long) {
        if (isPlayAnimation) {
            val imageViewPrevious = imageViewTarget
            imageViewTarget = when (imageViewTarget) {
                imageViewA -> imageViewB
                imageViewB -> imageViewA
                else -> imageViewA
            }

            val animatorSetPrevious = createFadeOutAnimator(imageViewPrevious, 0)

            val isEnableEffectFadeIn = imageViewPrevious.drawable != null
            val randomIndex = Random().nextInt(AnimationType.values().size - 1)
            val animatorSetTarget = when (AnimationType.values()[randomIndex]) {
                AnimationType.LEFT_TO_RIGHT -> createAnimatorSetLeftToRight(imageViewTarget, 4f, time, isEnableEffectFadeIn)
                AnimationType.LEFT_TO_RIGHT_WEAK -> createAnimatorSetLeftToRight(imageViewTarget, 2f, time, isEnableEffectFadeIn)
                AnimationType.LEFT_TO_RIGHT_ZOOM_IN -> createAnimatorSetLeftToRightZoomIn(imageViewTarget, 3f, time, isEnableEffectFadeIn)
                AnimationType.LEFT_TO_RIGHT_ZOOM_OUT -> createAnimatorSetLeftToRightZoomOut(imageViewTarget, 3f, time, isEnableEffectFadeIn)
                AnimationType.ZOOM_IN -> createAnimatorZoomIn(imageViewTarget, 5f, time, isEnableEffectFadeIn)
                AnimationType.ZOOM_IN_WEAK -> createAnimatorZoomIn(imageViewTarget, 3f, time, isEnableEffectFadeIn)
                AnimationType.ZOOM_OUT -> createAnimatorZoomOut(imageViewTarget, 5f, time, isEnableEffectFadeIn)
                AnimationType.ZOOM_OUT_WEAK -> createAnimatorZoomOut(imageViewTarget, 3f, time, isEnableEffectFadeIn)
            }

            context?.let {
                Glide.with(it)
                    .load(imageUri)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(imageViewTarget.width, imageViewTarget.width)
                    .into(imageViewTarget)
            }

            animatorSetPrevious.start()
            animatorSetTarget.start()
        }
    }


    private fun slideProductDescAnimation(productDesc: String) {
        if (isPlayAnimation) {
            val textViewDescPrevious = textViewDescTarget
            textViewDescTarget = when (textViewDescTarget) {
                textViewDescA -> textViewDescB
                textViewDescB -> textViewDescA
                else -> textViewDescA
            }

            val animatorSetPrevious = createFadeOutAnimator(textViewDescPrevious, 0, 500)

            textViewDescTarget.alpha = 0f
            textViewDescTarget.text = productDesc
            val animatorSetTarget = createFadeInAnimator(textViewDescTarget, 500)

            animatorSetPrevious.start()
            animatorSetTarget.start()
        }
    }

    private fun showUserName(userName: String, time: Long) {
        binding.ivMemories.apply {
            alpha = 1f
            visibility = View.VISIBLE
        }
        createAnimatorFadeInAndFadeOut(binding.ivMemories, time, 1000, 1500).start()

        binding.ivUserName.apply {
            alpha = 0f
            visibility = View.VISIBLE
            text = ("by.$userName")
        }

        val delay = 400L
        createAnimatorFadeInAndFadeOut(binding.ivUserName, time - (delay * 2), 1000, 1500).apply {
            startDelay = delay
        }.start()
    }


    private val defaultFadeInOutTime = 1200L

    private fun createAnimatorFadeInAndFadeOut(
        view: View,
        time: Long,
        fadeInTime: Long = defaultFadeInOutTime,
        fadeOutTime: Long = defaultFadeInOutTime
    ): AnimatorSet {
        return AnimatorSet().apply {
            playTogether(
                createFadeInAnimator(view, fadeInTime),
                createFadeOutAnimator(view, time, fadeOutTime)
            )
        }
    }

    private fun createAnimatorSetLeftToRight(
        view: View,
        value: Float,
        time: Long,
        isEffectFadeIn: Boolean = true
    ): AnimatorSet {
        val translation = value / 2f * -1f
        val scale = 1f + (abs(value) / 100f)
        view.translationX = translation
        view.translationY = translation
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = if (isEffectFadeIn) 0f else 1f

        return if (isEffectFadeIn) {
            AnimatorSet().apply {
                playTogether(
                    createFadeInAnimator(view),
                    createTranslateAnimator(view, value, time),
                )
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    createTranslateAnimator(view, value, time),
                )
            }
        }
    }

    private fun createAnimatorSetLeftToRightZoomIn(
        view: View,
        value: Float,
        time: Long,
        isEffectFadeIn: Boolean = true
    ): AnimatorSet {
        val translation = value / 2f * -1f
        val scale = 1f + (abs(value) / 100f)
        view.translationX = translation
        view.translationY = translation
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = if (isEffectFadeIn) 0f else 1f

        val scaleZoom = 0.05f
        val animatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, view.scaleX, view.scaleX + scaleZoom)
        animatorScaleX.duration = time + defaultFadeInOutTime

        val animatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, view.scaleY, view.scaleY + scaleZoom)
        animatorScaleY.duration = time + defaultFadeInOutTime

        return if (isEffectFadeIn) {
            AnimatorSet().apply {
                playTogether(
                    animatorScaleX,
                    animatorScaleY,
                    createFadeInAnimator(view),
                    createTranslateAnimator(view, value, time),
                )
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    animatorScaleX,
                    animatorScaleY,
                    createTranslateAnimator(view, value, time),
                )
            }
        }
    }

    private fun createAnimatorSetLeftToRightZoomOut(
        view: View,
        value: Float,
        time: Long,
        isEffectFadeIn: Boolean = true
    ): AnimatorSet {
        val scaleZoom = 0.05f
        val translation = value / 2f * -1f
        val scale = 1f + (abs(value) / 100f) + scaleZoom
        view.translationX = translation
        view.translationY = translation
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = if (isEffectFadeIn) 0f else 1f

        val animatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, view.scaleX, view.scaleX - scaleZoom)
        animatorScaleX.duration = time + defaultFadeInOutTime

        val animatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, view.scaleY, view.scaleY - scaleZoom)
        animatorScaleY.duration = time + defaultFadeInOutTime

        return if (isEffectFadeIn) {
            AnimatorSet().apply {
                playTogether(
                    animatorScaleX,
                    animatorScaleY,
                    createFadeInAnimator(view),
                    createTranslateAnimator(view, value, time),
                )
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    animatorScaleX,
                    animatorScaleY,
                    createTranslateAnimator(view, value, time),
                )
            }
        }
    }

    private fun createAnimatorZoomIn(
        view: View,
        value: Float,
        time: Long,
        isEffectFadeIn: Boolean = true
    ): AnimatorSet {
        view.translationX = 0f
        view.translationY = 0f
        view.scaleX = 1f
        view.scaleY = 1f
        view.alpha = if (isEffectFadeIn) 0f else 1f

        return if (isEffectFadeIn) {
            AnimatorSet().apply {
                playTogether(
                    createFadeInAnimator(view),
                    createScaleXAnimator(view, value, time),
                    createScaleYAnimator(view, value, time),
                )
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    createScaleXAnimator(view, value, time),
                    createScaleYAnimator(view, value, time),
                )
            }
        }
    }

    private fun createAnimatorZoomOut(
        view: View,
        value: Float,
        time: Long,
        isEffectFadeIn: Boolean = true
    ): AnimatorSet {
        view.translationX = 0f
        view.translationY = 0f
        view.scaleX = 1f + value
        view.scaleY = 1f + value
        view.alpha = if (isEffectFadeIn) 0f else 1f

        return if (isEffectFadeIn) {
            AnimatorSet().apply {
                playTogether(
                    createFadeInAnimator(view),
                    createScaleXAnimator(view, value * -1, time),
                    createScaleYAnimator(view, value * -1, time),
                )
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    createScaleXAnimator(view, value * -1, time),
                    createScaleYAnimator(view, value * -1, time),
                )
            }
        }
    }

    private fun createFadeInAnimator(
        view: View,
        time: Long = defaultFadeInOutTime
    ): ValueAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            duration = time
        }
    }

    private fun createFadeOutAnimator(
        view: View,
        startDelayTime: Long,
        time: Long = defaultFadeInOutTime
    ): ValueAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
            startDelay = startDelayTime
            duration = time
        }
    }

    private fun createTranslateAnimator(
        view: View,
        movePercentage: Float,
        time: Long,
    ): ValueAnimator {
        val movePosHalf = view.width * movePercentage / 100 / 2
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_X, movePosHalf * -1, movePosHalf).apply {
            duration = time
        }
    }

    private fun createScaleXAnimator(
        view: View,
        zoomPercentage: Float,
        time: Long
    ): ValueAnimator {
        val zoom = 1f + (abs(zoomPercentage / 100f))
        val start = if (zoomPercentage > 0) 1f else zoom
        val end = if (zoomPercentage > 0) zoom else 1f
        return ObjectAnimator.ofFloat(view, View.SCALE_X, start, end).apply {
            duration = time
        }
    }

    private fun createScaleYAnimator(
        view: View,
        zoomPercentage: Float,
        time: Long
    ): ValueAnimator {
        val zoom = 1f + (abs(zoomPercentage / 100f))
        val start = if (zoomPercentage > 0) 1f else zoom
        val end = if (zoomPercentage > 0) zoom else 1f
        return ObjectAnimator.ofFloat(view, View.SCALE_Y, start, end).apply {
            duration = time
        }
    }
}