package com.snaps.mobile.presentation.editor.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseEditorDialogFragment<T : ViewBinding> : DialogFragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = bindingView(inflater, container, savedInstanceState)
        return binding.root
    }

    abstract fun bindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): T

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}