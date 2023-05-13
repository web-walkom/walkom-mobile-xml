package ru.walkom.app.presentation.screens.excursions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionsViewModel @Inject constructor(
    private val getExcursionsUseCase : GetExcursionsUseCase
): ViewModel() {

    private val _stateExcursions = MutableLiveData<Response<List<ExcursionItem>>>()
    val stateExcursions: LiveData<Response<List<ExcursionItem>>> get() = _stateExcursions

    init {
        getExcursions()
    }

    private fun getExcursions() {
        viewModelScope.launch {
            getExcursionsUseCase.invoke().collect { response ->
                _stateExcursions.postValue(response)
            }
        }
    }
}