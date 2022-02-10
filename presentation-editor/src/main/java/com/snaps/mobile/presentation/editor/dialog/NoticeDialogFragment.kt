package com.snaps.mobile.presentation.editor.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.DialogFragmentEditorNoticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeDialogFragment : BaseEditorDialogFragment<DialogFragmentEditorNoticeBinding>() {

    private val vm by activityViewModels<EditorDialogViewModel>()

    override fun bindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFragmentEditorNoticeBinding {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.corner_radius_10)
        return DialogFragmentEditorNoticeBinding.inflate(inflater, container, false).apply {
            btnSingle.setOnClickListener {
                dismiss()
                vm.onclickSingleButton()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false
        vm.message.observe(viewLifecycleOwner) {
            binding.tvMessage.text = it
        }
        vm.singleButtonLabel.observe(viewLifecycleOwner) {
            binding.btnSingle.text = it
        }
    }

}