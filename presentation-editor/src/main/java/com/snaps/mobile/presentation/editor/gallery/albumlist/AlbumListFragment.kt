package com.snaps.mobile.presentation.editor.gallery.albumlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.snaps.mobile.presentation.editor.utils.GridSpacingItemDecoration
import com.snaps.mobile.presentation.editor.databinding.FragmentAlbumListBinding
import com.snaps.mobile.presentation.editor.utils.dp
import com.snaps.mobile.presentation.editor.gallery.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumListFragment : Fragment(), AlbumListController.AdapterCallbacksListener {

    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<GalleryViewModel>(ownerProducer = { requireParentFragment() })
    private val albumListController = AlbumListController(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlbumListBinding.inflate(inflater, container, false).apply {
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            rvAlbums.setHasFixedSize(true)
            rvAlbums.setController(albumListController)
            rvAlbums.layoutManager = GridLayoutManager(context, 2)
            rvAlbums.addItemDecoration(GridSpacingItemDecoration(16.dp(), true))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.albumList.observe(viewLifecycleOwner, {
            albumListController.setData(it)
        })
    }

    override fun onClickAlbum(album: AlbumListItem) {
        vm.onClickAlbum(album)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvAlbums.adapter = null
        _binding = null
    }

}