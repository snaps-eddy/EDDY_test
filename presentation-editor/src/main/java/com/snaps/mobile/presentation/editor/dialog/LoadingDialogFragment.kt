package com.snaps.mobile.presentation.editor.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import com.snaps.mobile.presentation.editor.databinding.DialogFragmentEditorLoadingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingDialogFragment : BaseEditorDialogFragment<DialogFragmentEditorLoadingBinding>() {

    private val vm by activityViewModels<EditorDialogViewModel>()

    override fun bindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFragmentEditorLoadingBinding {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        return DialogFragmentEditorLoadingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.loadingAnimStyle.observe(viewLifecycleOwner) { style ->
            if (!style.isDimBackground) {
                dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            with(binding) {
                laLoading.setAnimation(style.resouce)
                laLoading.updateLayoutParams {
                    width = style.width
                    height = style.height
                }
                laLoading.playAnimation()
            }
        }
    }

    override fun onDestroyView() {
        binding.laLoading.pauseAnimation()
        super.onDestroyView()
    }
}