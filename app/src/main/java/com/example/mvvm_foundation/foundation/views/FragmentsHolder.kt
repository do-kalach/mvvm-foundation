package com.example.mvvm_foundation.foundation.views

import com.example.mvvm_foundation.foundation.ActivityScopeViewModel

interface FragmentsHolder {
    fun notifyScreenUpdates()
    fun getActivityScopeViewModel(): ActivityScopeViewModel
}