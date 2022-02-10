package com.snaps.mobile.presentation.editor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.snaps.common.HomeActivity
import com.snaps.mobile.presentation.editor.EditorViewModel.Companion.RECIPE_PARAMS
import com.snaps.mobile.presentation.editor.ai_progress.AiProgressFragment
import com.snaps.mobile.presentation.editor.covercatalog.CoverCatalogFragment
import com.snaps.mobile.presentation.editor.covercatalog.CoverCatalogViewModel
import com.snaps.mobile.presentation.editor.databinding.ActivityEditorBinding
import com.snaps.mobile.presentation.editor.dialog.EditorDialogService
import com.snaps.mobile.presentation.editor.gallery.GalleryFragment
import com.snaps.mobile.presentation.editor.gallery.GalleryHome
import com.snaps.mobile.presentation.editor.gallery.GalleryViewModel
import com.snaps.mobile.presentation.editor.imageEdit.ImageEditFragment
import com.snaps.mobile.presentation.editor.imageEdit.ImageEditViewModel
import com.snaps.mobile.presentation.editor.sketch.SketchFragment
import com.snaps.mobile.presentation.editor.title.EnterTitleFragment
import com.snaps.mobile.presentation.editor.title.EnterTitleViewModel
import com.snaps.mobile.presentation.editor.tutorial.TutorialFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditorActivity : AppCompatActivity() {

    private var _binding: ActivityEditorBinding? = null
    private val binding get() = _binding!!
    private val vm: EditorViewModel by viewModels()

    @Inject
    lateinit var homeActivity: HomeActivity

    @Inject
    lateinit var editorDialogService: EditorDialogService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()

        vm.navigation.observe(this, { destination ->
            replaceFragment(destination)
        })
    }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }

    private fun replaceFragment(destination: EditorNavigation) {
        when (destination) {
            is EditorNavigation.Gallery -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: GalleryFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                                GalleryViewModel.KEY_HOME to GalleryHome.AlbumList
                            )
                        }
                        .also {
                            this.beginTransaction()
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.ModalGallery -> {
                // 사용자 이미지 추가
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: GalleryFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                                GalleryViewModel.KEY_HOME to GalleryHome.AlbumDetails(destination.maxAddMoreCount),
                            )
                        }
                        .also {
                            this.beginTransaction()
                                .addToBackStack(destination.tag)
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.Title -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: EnterTitleFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                                EnterTitleViewModel.KEY_IS_CREATE_PROCESS to destination.isCreateProcess
                            )

                        }
                        .also {
                            this.beginTransaction()
                                .addToBackStack(destination.tag)
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.AiProgress -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: AiProgressFragment())
                        .apply {
                            arguments = bundleOf(RECIPE_PARAMS to destination.recipeParams)
                        }
                        .also {
                            this.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            this.beginTransaction()
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.Sketch -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: SketchFragment())
                        .apply {
                            arguments = bundleOf(RECIPE_PARAMS to destination.recipeParams)
                        }
                        .also {
                            this.beginTransaction()
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.ImageEdit -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: ImageEditFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                                ImageEditViewModel.KEY_SCENE_DRAW_INDEX to destination.sceneDrawIndex,
                                ImageEditViewModel.KEY_SCENE_OBJECT_DRAW_INDEX to destination.sceneObjectDrawIndex,
                                ImageEditViewModel.KEY_IMG_SEQ to destination.imgSeq,
                            )
                        }
                        .also {
                            this.beginTransaction()
                                .addToBackStack(destination.tag)
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.Finish -> {
                Intent(this, homeActivity.getTargetClass())
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("goToCart", destination.goToCart)
                    .putExtra("isFromCart", destination.isFromCart)
                    .also {
                        this.startActivity(it)
                        this.finish()
                    }
            }
            is EditorNavigation.CoverCatalog -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: CoverCatalogFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                                CoverCatalogViewModel.KEY_CURRENT_COVER_TEMPLATE_CODE to destination.coverTemplateCode
                            )
                        }
                        .also {
                            this.beginTransaction()
                                .addToBackStack(destination.tag)
                                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.nothing, R.anim.nothing, R.anim.slide_out_top)
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.Tutorial -> {
                with(supportFragmentManager) {
                    (this.findFragmentByTag(destination.tag) ?: TutorialFragment())
                        .apply {
                            arguments = bundleOf(
                                RECIPE_PARAMS to destination.recipeParams,
                            )
                        }
                        .also {
                            this.beginTransaction()
                                .addToBackStack(destination.tag)
                                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.nothing, R.anim.nothing, R.anim.slide_out_top)
                                .replace(R.id.editor_fragment_container, it, destination.tag)
                                .commit()
                        }
                }
            }
            is EditorNavigation.Dialog -> {
                editorDialogService.showDialog(destination.dialogState)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}