package com.snaps.mobile.presentation.editor.gallery

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.snaps.mobile.presentation.editor.EditorViewModel
import com.snaps.mobile.presentation.editor.databinding.FragmentGalleryBinding
import com.snaps.mobile.presentation.editor.databinding.ToastFillWidthCustomBinding
import com.snaps.mobile.presentation.editor.dialog.EditorDialogService
import com.snaps.mobile.presentation.editor.gallery.albumdetail.AlbumDetailListFragment
import com.snaps.mobile.presentation.editor.gallery.albumlist.AlbumListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<GalleryViewModel>()
    private val activityVm by activityViewModels<EditorViewModel>()

    @Inject
    lateinit var editorDialogService: EditorDialogService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            } else {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
        activity?.contentResolver?.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.navigation.observe(viewLifecycleOwner, { destination ->
            when (destination) {
                GalleryNavigation.AlbumList -> {
                    with(childFragmentManager) {
                        (this.findFragmentByTag(destination.tag) ?: AlbumListFragment())
                            .also {
                                this.beginTransaction()
                                    .replace(binding.galleryFragmentContainer.id, it, destination.tag)
                                    .commit()
                            }
                    }
                }
                GalleryNavigation.AlbumDetailList -> {
                    with(childFragmentManager) {
                        (this.findFragmentByTag(destination.tag) ?: AlbumDetailListFragment())
                            .also {
                                this.beginTransaction()
                                    .addToBackStack(destination.tag)
                                    .replace(binding.galleryFragmentContainer.id, it, destination.tag)
                                    .commit()
                            }
                    }
                }
                GalleryNavigation.AlbumDetailListAsHome -> {
                    with(childFragmentManager) {
                        (this.findFragmentByTag(destination.tag) ?: AlbumDetailListFragment())
                            .also {
                                this.beginTransaction()
                                    .replace(binding.galleryFragmentContainer.id, it, destination.tag)
                                    .commit()
                            }
                    }
                }
                GalleryNavigation.Complete -> {
                    activityVm.onCompleteGalleryProcess()
                }

                is GalleryNavigation.Dialog -> {
//                    val dialogState = when (destination.dialogState) {
//                        is EditorDialogState.Loading -> {
//                            destination.dialogState.copy(
//                                resouce = R.raw.lottie_loading_photobook,
//                                width = 200.dp(),
//                                height = 200.dp(),
//                                isDimBackground = true
//                            )
//                        }
//                        else -> destination.dialogState
//                    }
                    editorDialogService.showDialog(destination.dialogState)
                }
                is GalleryNavigation.Close -> {
                    setFragmentResult(KEY_REQUEST_FRGMENT, bundleOf(KEY_TRAY_ITEM_CHANGED to destination.hasChange))
                    editorDialogService.hideDialog()
                    activity?.onBackPressed()
                }
            }
        })
        vm.viewEffect.observe(viewLifecycleOwner, { viewEffect ->
            when (viewEffect) {
                is GalleryViewEffect.ShowToast -> {
                    context?.let {
                        Toast(it).apply {
                            this.view = ToastFillWidthCustomBinding.inflate(LayoutInflater.from(it), null, false).apply {
                                tvMsg.text = viewEffect.message
                            }.root
                            setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
                            duration = Toast.LENGTH_SHORT
                        }.show()
                    }
                }
            }
        })
    }

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            vm.onChangeMediaStore(selfChange, uri)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.contentResolver?.unregisterContentObserver(contentObserver)
    }

    companion object {
        const val KEY_REQUEST_FRGMENT = "key GalleryFragment request"
        const val KEY_TRAY_ITEM_CHANGED = "key tray item changed"
    }
}