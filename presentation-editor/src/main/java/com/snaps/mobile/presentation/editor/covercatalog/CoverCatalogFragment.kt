package com.snaps.mobile.presentation.editor.covercatalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.snaps.common.utils.log.Dlog
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.databinding.FragmentCoverCatalogBinding
import com.snaps.mobile.presentation.editor.utils.GridSpacingItemDecoration
import com.snaps.mobile.presentation.editor.utils.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CoverCatalogFragment : Fragment() {

    private var _binding: FragmentCoverCatalogBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<CoverCatalogViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCoverCatalogBinding.inflate(inflater, container, false).apply {
            rvCoverCatalog.apply {
                setHasFixedSize(true)
                setController(catalogController)
                layoutManager = GridLayoutManager(context, 2)
                addItemDecoration(GridSpacingItemDecoration(8.dp(), false))
            }
            btnConfirm.setOnClickListener {
                vm.setEvent(CoverCatalogContract.Event.OnClickConfirm)
            }
            btnClose.setOnClickListener {
                vm.setEvent(CoverCatalogContract.Event.OnClickClose)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            vm.uiState.collect {
                when (it.uiState) {
                    CoverCatalogContract.CoverCatalogListState.Idle -> {
                        binding.clpLoading.hide()
                    }
                    CoverCatalogContract.CoverCatalogListState.Loading -> {
                        binding.clpLoading.show()
                    }
                    is CoverCatalogContract.CoverCatalogListState.Refresh -> {
                        binding.clpLoading.hide()
                        catalogController.setData(it.uiState.coverCatalogItems)
                    }
                    is CoverCatalogContract.CoverCatalogListState.Success -> {
                        binding.clpLoading.hide()
                        catalogController.setData(it.uiState.coverCatalogItems)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.effect.collect {
                when (it) {
                    CoverCatalogContract.Effect.Close -> {
                        setFragmentResult("CoverCatalogFragment", bundleOf(KEY_COVER_TEMPLATE_CHANGED to false))
                        activity?.onBackPressed()
                    }
                    CoverCatalogContract.Effect.SaveAndClose -> {
                        setFragmentResult("CoverCatalogFragment", bundleOf(KEY_COVER_TEMPLATE_CHANGED to true))
                        activity?.onBackPressed()
                    }
                }
            }
        }
    }

    private val catalogController = CoverCatalogController(object : CoverCatalogController.Callback {
        override fun onSelectCover(item: CoverCatalogItem, isChecked: Boolean) {
            vm.setEvent(CoverCatalogContract.Event.OnSelectCover(item, isChecked))
        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_COVER_TEMPLATE_CHANGED = "key cover template changed"
    }
}