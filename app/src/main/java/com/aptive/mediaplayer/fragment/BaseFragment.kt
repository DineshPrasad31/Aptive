package com.aptive.mediaplayer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aptive.mediaplayer.viewmodel.BaseViewModel

abstract class BaseFragment<V : BaseViewModel> : Fragment() {

    protected lateinit var viewModel: V

    abstract fun getViewModel(): Class<V>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    protected fun finish() {
        requireActivity().finish()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(getViewModel())
    }

}