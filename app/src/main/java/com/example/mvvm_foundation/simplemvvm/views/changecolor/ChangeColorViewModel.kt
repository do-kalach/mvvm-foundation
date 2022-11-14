package com.example.mvvm_foundation.simplemvvm.views.changecolor

import androidx.constraintlayout.motion.utils.ViewState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.example.mvvm_foundation.R
import com.example.mvvm_foundation.foundation.model.ErrorResult
import com.example.mvvm_foundation.foundation.model.FinalResult
import com.example.mvvm_foundation.foundation.model.PendingResult
import com.example.mvvm_foundation.foundation.model.SuccessResult
import com.example.mvvm_foundation.foundation.model.tasks.TasksFactory
import com.example.mvvm_foundation.foundation.navigator.Navigator
import com.example.mvvm_foundation.foundation.uiactions.UiActions
import com.example.mvvm_foundation.foundation.views.BaseViewModel
import com.example.mvvm_foundation.foundation.views.LiveResult
import com.example.mvvm_foundation.foundation.views.MediatorLiveResult
import com.example.mvvm_foundation.foundation.views.MutableLiveResult
import com.example.mvvm_foundation.simplemvvm.colors.ColorsRepository
import com.example.mvvm_foundation.simplemvvm.colors.NamedColor
import com.example.mvvm_foundation.simplemvvm.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    private val taskFactory: TasksFactory,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getLiveData("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableLiveData(false)

    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState

    val screenTitle: LiveData<String> = Transformations.map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_title_simple)
        }
    }

    init {
        load()
        // initializing MediatorLiveData
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() {
        _saveInProgress.postValue(true)

        taskFactory.async {
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId).await()
            colorsRepository.setCurrentColor(currentColor).await()
            return@async currentColor
        }
            .safeEnqueue(::onSaved)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        // map Result<List<NamedColor>> to Result<ViewState>
        _viewState.value = colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },

                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
    }

    private fun load() {
        colorsRepository.getAvailableColors().into(_availableColors)
    }

    private fun onSaved(result: FinalResult<NamedColor>) {
        _saveInProgress.value = false
        when (result) {
            is SuccessResult -> navigator.goBack(result.data)
            is ErrorResult -> uiActions.toast(uiActions.getString(R.string.error_happened))
        }
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean
    )

}