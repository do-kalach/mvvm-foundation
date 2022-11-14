package com.example.mvvm_foundation.foundation.views

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.mvvm_foundation.foundation.BaseApplication
import com.example.mvvm_foundation.foundation.views.BaseScreen.Companion.ARG_SCREEN
import java.lang.reflect.Constructor

inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as BaseApplication
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    val activityScopeViewModel = (requireActivity() as FragmentsHolder).getActivityScopeViewModel()

    val dependencies = listOf(screen, activityScopeViewModel) + application.repositories

    ViewModelFactory(dependencies, this)
}


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructors = modelClass.constructors
        val constructor = constructors.maxByOrNull { it.typeParameters.size }!!

        val dependenciesWithSavedState = dependencies + handle

        // generating the list of arguments to be passed into the view-model's constructor
        val arguments = findDependencies(constructor, dependenciesWithSavedState)

        // creating view-model
        return constructor.newInstance(*arguments.toTypedArray()) as T
    }

    private fun findDependencies(constructor: Constructor<*>, dependencies: List<Any>): List<Any> {
        val args = mutableListOf<Any>()
        // here we iterate through view-model's constructor arguments and for each
        // argument we search dependency that can be assigned to the argument
        constructor.parameterTypes.forEach { parameterClass ->
            val dependency = dependencies.first { parameterClass.isAssignableFrom(it.javaClass) }
            args.add(dependency)
        }
        return args
    }
}