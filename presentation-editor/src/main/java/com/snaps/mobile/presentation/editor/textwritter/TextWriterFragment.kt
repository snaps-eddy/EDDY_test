package com.snaps.mobile.presentation.editor.textwritter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.jakewharton.rxbinding4.widget.textChanges
import com.snaps.mobile.domain.save.TextAlign
import com.snaps.mobile.presentation.editor.R
import com.snaps.mobile.presentation.editor.databinding.FragmentTextWriterBinding
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlin.math.max

@AndroidEntryPoint
class TextWriterFragment : DialogFragment() {

    private var _binding: FragmentTextWriterBinding? = null
    private val binding get() = _binding!!

    private val hintTextSize = 13f
    private var textSize = 17f

    private val vm by viewModels<TextWriterViewModel>()

    private var keyboardHeight = 230.dp() // 키보드가 한번이라도 올라와야 정확히 판단할 수 있다.

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.window?.apply {
            attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        }
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                vm.setEvent(TextWriterContract.Event.OnClickBack(binding.etText.text.toString()))
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // full Screen code
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            decorView.setBackgroundColor(Color.TRANSPARENT)
            // 특정 OS? 혹은 단말? 에서 다이얼로그에 패딩이 강제로 들어가는 현상이 있는데 이를 제거하기 위한 코드
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTextWriterBinding.inflate(inflater, container, false)

        binding.btnTextColor.setOnClickListener {
            vm.setEvent(TextWriterContract.Event.OnClickTextColor(isColorPickerOpen()))
        }
        binding.etText.setOnClickListener {
            vm.setEvent(TextWriterContract.Event.OnClickEdtiText)
        }

        binding.textAlignGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                binding.btnAlignLeft.id -> binding.btnAlignLeft as MaterialButton
                binding.btnAlignCenter.id -> binding.btnAlignCenter as MaterialButton
                binding.btnAlignRight.id -> binding.btnAlignRight as MaterialButton
                else -> null
            }?.apply {
                if (isChecked) {
                    setIconTintResource(R.color.bittersweet)
                } else {
                    setIconTintResource(R.color.cod_gray)
                }
            }

            when (checkedId) {
                binding.btnAlignLeft.id -> TextAlign.Left
                binding.btnAlignCenter.id -> TextAlign.Center
                binding.btnAlignRight.id -> TextAlign.Right
                else -> null
            }?.run {
                vm.setEvent(TextWriterContract.Event.OnCheckAlign(this))
            }
        }

        binding.btnTopMenuLeft.setOnClickListener {
            hideKeyboard()
            vm.setEvent(TextWriterContract.Event.OnClickBack(binding.etText.text.toString()))
        }

        binding.btnTopMenuRight.setOnClickListener {
            hideKeyboard()
            vm.setEvent(TextWriterContract.Event.OnClickConfirm(binding.etText.text.toString()))
        }

        binding.layoutColorPicker.colorpicker.onSelectHexColor = { hexColor ->
            vm.setEvent(TextWriterContract.Event.OnChangeTextColor(hexColor))
        }

        binding.layoutColorPicker.btnCurrentColor.setOnClickListener {
            vm.setEvent(TextWriterContract.Event.OnClickDefaultTextColor)
        }

        binding.etText.hint = getString(R.string.initial_text)

        binding.etText
            .textChanges()
            .subscribe {
                binding.etText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (it.isEmpty()) hintTextSize else textSize)
            }
            .addTo(compositeDisposable)

        /**
         * Dialog setting
         */
        binding.fakedialogCancel.setOnClickListener {
            binding.fakedialog.isVisible = false
        }
        binding.fakedialogConfirm.setOnClickListener {
            binding.fakedialog.isVisible = false
            vm.setEvent(TextWriterContract.Event.OnClickDialogConfirm)
        }

        measureKeyboardHeight()
        return binding.root
    }

    /**
     * 키보드의 높이는 한번이라도 키보드가 보여야 구할 수 있다.
     */
    private fun measureKeyboardHeight() {
        val rootView = activity?.window?.decorView
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val calcHeight = rootView?.run {
                val r = Rect()
                getWindowVisibleDisplayFrame(r)
                val screenHeight = this.height
                screenHeight - r.bottom
            } ?: 0
            this.keyboardHeight = max(this.keyboardHeight, calcHeight)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard()
        lifecycleScope.launchWhenStarted {
            vm.uiState.collect {
                when (it.uiState) {
                    TextWriterContract.TextWriterState.Idle -> {
                        // Something Ready view..
                    }
                    TextWriterContract.TextWriterState.ShowColorPicker -> {
                        hideKeyboard()
                        delay(100)
                        binding.groupTextTools.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomMargin = keyboardHeight - 48.dp() //48dp 는 text tool의 높이.
                        }
                    }
                    TextWriterContract.TextWriterState.HideColorPicker -> {
                        binding.groupTextTools.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomMargin = 0
                        }
                    }
                    is TextWriterContract.TextWriterState.InitText -> {
                        val params = it.uiState
                        val color = Color.parseColor(params.color)
                        binding.etText.setTextColor(color)
                        binding.etText.setText(params.text)
                        binding.etText.setSelection(params.text.length)
                        if (params.text.isNotEmpty()) {
                            binding.etText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, params.fontSize)
                        }
                        textSize = params.fontSize
                        binding.etText.gravity = when (params.align) {
                            TextAlign.Left -> {
                                (binding.btnAlignLeft as MaterialButton).isChecked = true
                                Gravity.START
                            }
                            TextAlign.Center -> {
                                (binding.btnAlignCenter as MaterialButton).isChecked = true
                                Gravity.CENTER
                            }
                            TextAlign.Right -> {
                                (binding.btnAlignRight as MaterialButton).isChecked = true
                                Gravity.END
                            }
                        }
                        binding.fakeCurrentTextColor.setBackgroundColor(color)
                        binding.layoutColorPicker.btnCurrentColor.setBackgroundColor(color)
                        binding.layoutColorPicker.viewCurrentColorSelector.isVisible = true
                    }
                    is TextWriterContract.TextWriterState.UpdateTextColor -> {
                        val intColor = Color.parseColor(it.uiState.color)
                        updateTextColor(intColor)
                        binding.layoutColorPicker.viewCurrentColorSelector.isVisible = false
                    }
                    is TextWriterContract.TextWriterState.UpdateDefaultTextColor -> {
                        val intColor = Color.parseColor(it.uiState.color)
                        updateTextColor(intColor)
                        binding.layoutColorPicker.colorpicker.clearSelection()
                        binding.layoutColorPicker.btnCurrentColor.setBackgroundColor(intColor)
                        binding.layoutColorPicker.viewCurrentColorSelector.isVisible = true
                    }
                    is TextWriterContract.TextWriterState.UpdateTextAlign -> {
                        when (it.uiState.align) {
                            TextAlign.Left -> binding.etText.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                            TextAlign.Center -> binding.etText.gravity = Gravity.CENTER
                            TextAlign.Right -> binding.etText.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.effect.collect {
                when (it) {
                    TextWriterContract.Effect.Close -> {
                        setFragmentResult("TextWriterFragment", bundleOf(KEY_SCENE_USER_TEXT_CHANGED to false))
                        dismiss()
                    }
                    TextWriterContract.Effect.SaveAndClose -> {
                        setFragmentResult("TextWriterFragment", bundleOf(KEY_SCENE_USER_TEXT_CHANGED to true))
                        dismiss()
                    }
                    TextWriterContract.Effect.ConfirmHasChanges -> {
                        context?.run {
                            binding.fakedialog.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun updateTextColor(intColor: Int) {
        binding.etText.setTextColor(intColor)
        binding.fakeCurrentTextColor.setBackgroundColor(intColor)
    }

    private fun hideKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(binding.etText.windowToken, 0)
    }

    private fun showKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun isColorPickerOpen(): Boolean {
        return binding.groupTextTools.marginBottom > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        _binding = null
        compositeDisposable.clear()
    }

    companion object {
        const val KEY_SCENE_USER_TEXT_CHANGED = "key scene user text changed"
    }
}