package com.snaps.mobile.presentation.editor.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.DialogFragmentEditorChoiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChoiceDialogFragment : BaseEditorDialogFragment<DialogFragmentEditorChoiceBinding>() {

    private val vm by activityViewModels<EditorDialogViewModel>()

    override fun bindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFragmentEditorChoiceBinding {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.corner_radius_10)
        return DialogFragmentEditorChoiceBinding.inflate(inflater, container, false).apply {
            btnLeft.setOnClickListener {
                dismiss()
                vm.onClickLeftButton()
            }
            btnRight.setOnClickListener {
                dismiss()
                vm.onClickRightButton()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // 이거 없어야 하나?
        this.isCancelable = false
        vm.message.observe(viewLifecycleOwner) {
            binding.tvMessage.text = it
        }
        vm.leftButtonLabel.observe(viewLifecycleOwner) {
            binding.btnLeft.text = it
        }
        vm.rightButtonLabel.observe(viewLifecycleOwner) {
            binding.btnRight.text = it
        }
    }
}