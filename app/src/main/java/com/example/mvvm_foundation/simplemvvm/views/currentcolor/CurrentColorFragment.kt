package com.example.mvvm_foundation.simplemvvm.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mvvm_foundation.databinding.FragmentCurrentColorBinding
import com.example.mvvm_foundation.foundation.views.BaseFragment
import com.example.mvvm_foundation.foundation.views.BaseScreen
import com.example.mvvm_foundation.foundation.views.screenViewModel
import com.example.mvvm_foundation.simplemvvm.onTryAgain
import com.example.mvvm_foundation.simplemvvm.renderSimpleResult

class CurrentColorFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCurrentColorBinding.inflate(inflater, container, false)
        viewModel.currentColor.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    binding.colorView.setBackgroundColor(it.value)
                }
            )
        }
        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }

        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        return binding.root
    }

}