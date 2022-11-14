package com.example.mvvm_foundation.simplemvvm.colors

import com.example.mvvm_foundation.foundation.model.Repository
import com.example.mvvm_foundation.foundation.model.tasks.Task

typealias ColorListener = (NamedColor) -> Unit


interface ColorsRepository : Repository {
    /**
     * Get the list of all available colors that may be chosen by the user.
     */
    fun getAvailableColors(): Task<List<NamedColor>>

    /**
     * Get the color content by its ID
     */
    fun getById(id: Long): Task<NamedColor>

    fun getCurrentColor(): Task<NamedColor>

    fun setCurrentColor(color: NamedColor): Task<Unit>

    /**
     * Listen for the current color changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addListener(listener: ColorListener)

    /**
     * Stop listening for the current color changes
     */
    fun removeListener(listener: ColorListener)
}