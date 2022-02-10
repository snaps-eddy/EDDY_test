package com.snaps.mobile.presentation.editor.title

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.databinding.FragmentEnterTitleBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EnterTitleFragment : Fragment() {

    private var _binding: FragmentEnterTitleBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<EnterTitleViewModel>()
    private val activityVm by activityViewModels<EditorViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEnterTitleBinding.inflate(inflater, container, false).apply {
            btnEnterDone.setOnClickListener {
                vm.onClickNext(titleEdit.text.toString())
            }
            btnEnterDoneBottom.setOnClickListener {
                vm.onClickNext(titleEdit.text.toString())
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            titleEdit.filters = arrayOf(
                TitleInputFilter(),
                InputFilter.LengthFilter(25)
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.next.observe(viewLifecycleOwner, {
            activityVm.onCompleteTitleProcess()
        })

        vm.close.observe(viewLifecycleOwner, {
            setFragmentResult("EnterTitleFragment", bundleOf(KEY_TITLE_CHANGED to true))
            activity?.onBackPressed()
        })

        vm.currentProjectName.observe(viewLifecycleOwner, {
            binding.titleEdit.setText(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_TITLE_CHANGED = "key title changed"
    }

}