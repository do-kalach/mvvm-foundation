package com.example.mvvm_foundation.foundation.model.tasks

import com.example.mvvm_foundation.foundation.model.Repository

typealias TaskBody<T> = () -> T

interface TasksFactory : Repository {

    fun <T> async(body: TaskBody<T>): Task<T>

}