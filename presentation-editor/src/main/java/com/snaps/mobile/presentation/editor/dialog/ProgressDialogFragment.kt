package com.snaps.mobile.presentation.editor.dialog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.DialogFragmentEditorProgressBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgressDialogFragment : BaseEditorDialogFragment<DialogFragmentEditorProgressBinding>() {

    private val vm by activityViewModels<EditorDialogViewModel>()

    override fun bindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFragmentEditorProgressBinding {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.corner_radius_10)
        return DialogFragmentEditorProgressBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false
        vm.message.observe(viewLifecycleOwner) {
            binding.tvMessage.text = it
        }
        vm.progress.observe(viewLifecycleOwner) {
            ObjectAnimator.ofInt(binding.progress, "progress", it)
                .setDuration(300)
                .start()
        }
    }
}