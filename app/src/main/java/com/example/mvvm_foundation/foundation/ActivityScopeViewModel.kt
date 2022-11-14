package com.example.mvvm_foundation.foundation

import androidx.lifecycle.ViewModel
import com.example.mvvm_foundation.foundation.navigator.IntermediateNavigator
import com.example.mvvm_foundation.foundation.navigator.Navigator
import com.example.mvvm_foundation.foundation.uiactions.UiActions

class ActivityScopeViewModel(
    val uiActions: UiActions,
    val navigator: IntermediateNavigator
) : ViewModel(), Navigator by navigator, UiActions by uiActions {

    override fun onCleared() {
        super.onCleared()
        navigator.clear()
    }
}