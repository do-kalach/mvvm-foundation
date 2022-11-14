package com.example.mvvm_foundation.foundation.model.tasks

import com.example.mvvm_foundation.foundation.model.FinalResult

typealias TaskListener<T> = (FinalResult<T>) -> Unit

interface Task<T> {

    fun await(): T

    fun enqueue(listener: TaskListener<T>)

    fun cancel()

}