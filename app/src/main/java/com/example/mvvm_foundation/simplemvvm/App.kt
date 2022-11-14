package com.example.mvvm_foundation.simplemvvm

import android.app.Application
import com.example.mvvm_foundation.foundation.BaseApplication
import com.example.mvvm_foundation.foundation.model.Repository
import com.example.mvvm_foundation.foundation.model.tasks.SimpleTaskFactory
import com.example.mvvm_foundation.simplemvvm.colors.InMemoryColorsRepository

class App : Application(), BaseApplication {

    private val taskFactory = SimpleTaskFactory()

    override val repositories: List<Repository> = listOf<Repository>(
        taskFactory,
        InMemoryColorsRepository(taskFactory)
    )
}