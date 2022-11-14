package com.example.mvvm_foundation.foundation

import com.example.mvvm_foundation.foundation.model.Repository

interface BaseApplication {
    val repositories: List<Repository>
}