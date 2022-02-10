package com.snaps.mobile.presentation.editor.tutorial

import android.webkit.JavascriptInterface
import javax.inject.Inject

class TutorialJavascriptInterface @Inject constructor() {

    private var callback: Callback? = null

    @JavascriptInterface
    fun CLOSE() {
        callback?.onCallClose()
    }

    fun registCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onCallClose()
    }

}