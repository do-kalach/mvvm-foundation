package com.example.mvvm_foundation.simplemvvm.views.currentcolor

import com.example.mvvm_foundation.R
import com.example.mvvm_foundation.foundation.model.PendingResult
import com.example.mvvm_foundation.foundation.model.SuccessResult
import com.example.mvvm_foundation.foundation.model.takeSuccess
import com.example.mvvm_foundation.foundation.navigator.Navigator
import com.example.mvvm_foundation.foundation.uiactions.UiActions
import com.example.mvvm_foundation.foundation.views.BaseViewModel
import com.example.mvvm_foundation.foundation.views.LiveResult
import com.example.mvvm_foundation.foundation.views.MutableLiveResult
import com.example.mvvm_foundation.simplemvvm.colors.ColorListener
import com.example.mvvm_foundation.simplemvvm.colors.ColorsRepository
import com.example.mvvm_foundation.simplemvvm.colors.NamedColor
import com.example.mvvm_foundation.simplemvvm.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    // --- example of listening results via model layer

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        load()
    }

    private fun load() {
        colorsRepository.getCurrentColor().into(_currentColor)
    }

}