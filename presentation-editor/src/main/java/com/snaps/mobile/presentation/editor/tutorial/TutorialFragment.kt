package com.snaps.mobile.presentation.editor.tutorial

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.snaps.mobile.presentation.editor.databinding.FragmentTutorialBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    private val vm by viewModels<TutorialViewModel>()

    @Inject
    lateinit var jsInterface: TutorialJavascriptInterface

    @Inject
    lateinit var tutorialWebViewClient: TutorialWebViewClient

    @Inject
    lateinit var tutorialWebChromeClient: TutorialWebChromeClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        initWebView()
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(binding.webview) {
            //단말 해상도 및 시스템 설정에서 폰트 사이즈의 영향을 받지 않도록 TextZoom Level 고정
            settings.textZoom = 100
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            // 줌컨트롤추가
            settings.builtInZoomControls = false

            // 몇몇 단말기에서 주소 클릭이 안된다고 해서 넣은 코드..해결이 될지는 모르겠음.
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)

            // // 웹페이지를 화면사이즈에 맞춤.
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true


            // 캐쉬를 하지 않는다..
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            // 하드웨어 가속?
            // 평균적으로 킷캣 이상에서는 하드웨어 가속이 성능이 좋음.
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            jsInterface.registCallback(vm)

            addJavascriptInterface(jsInterface, "HybridApp")
            webViewClient = tutorialWebViewClient
            webChromeClient = tutorialWebChromeClient
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            vm.uiState.collect {
                when (it.uiState) {
                    is TutorialContract.TutorialState.Idle -> {
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.effect.collect {
                when (it) {
                    TutorialContract.Effect.Close -> {
                        activity?.onBackPressed()
                    }
                    is TutorialContract.Effect.LoadUrl -> {
                        loadUrl(it.url)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webview.removeAllViews()
        binding.webview.destroy()
        _binding = null
    }


    private fun loadUrl(url: String) {
        binding.webview.loadUrl(url)
    }

}