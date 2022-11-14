package com.example.mvvm_foundation.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.mvvm_foundation.foundation.model.ErrorResult
import com.example.mvvm_foundation.foundation.model.PendingResult
import com.example.mvvm_foundation.foundation.model.Result
import com.example.mvvm_foundation.foundation.model.SuccessResult

abstract class BaseFragment : Fragment() {
    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as FragmentsHolder).notifyScreenUpdates()
    }

    fun <T> renderResult(
        root: ViewGroup, result: Result<T>,
        onPending: () -> Unit,
        onError: (Exception) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        root.children.forEach { it.visibility = View.GONE }
        when (result) {
            is SuccessResult -> onSuccess(result.data)
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
        }
    }
}