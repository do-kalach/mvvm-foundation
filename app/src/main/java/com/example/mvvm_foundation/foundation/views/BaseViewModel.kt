package com.example.mvvm_foundation.foundation.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvm_foundation.foundation.model.PendingResult
import com.example.mvvm_foundation.foundation.model.Result
import com.example.mvvm_foundation.foundation.model.tasks.Task
import com.example.mvvm_foundation.foundation.model.tasks.TaskListener
import com.example.mvvm_foundation.foundation.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

open class BaseViewModel : ViewModel() {

    private val tasks = mutableSetOf<Task<*>>()

    override fun onCleared() {
        super.onCleared()
        tasks.forEach { it.cancel() }
        tasks.clear()
    }

    open fun onResult(result: Any) {

    }

    fun <T> Task<T>.safeEnqueue(listener: TaskListener<T>? = null) {
        tasks.add(this)
        this.enqueue {
            tasks.remove(this)
            listener?.invoke(it)
        }
    }

    fun <T> Task<T>.into(liveResult: MutableLiveResult<T>) {
        liveResult.value = PendingResult()
        this.safeEnqueue {
            liveResult.value = it
        }
    }
}
