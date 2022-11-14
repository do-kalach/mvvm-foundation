package com.example.mvvm_foundation.foundation.navigator

import com.example.mvvm_foundation.foundation.views.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)

    fun goBack(result: Any? = null)
}