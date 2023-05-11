package ru.walkom.app.presentation.screens.excursion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.domain.model.ExcursionDB
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.DownloadDataExcursionUseCase
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase,
    private val downloadDataExcursionUseCase: DownloadDataExcursionUseCase
): ViewModel() {

    private val _stateExcursion = MutableLiveData<Response<ExcursionDB?>>()
    val stateExcursion: LiveData<Response<ExcursionDB?>> get() = _stateExcursion

    private val _stateAudio = MutableLiveData<Response<Boolean>>()
    val stateAudio: LiveData<Response<Boolean>> get() = _stateAudio

    private val ID = "QQ4oHDyYxtOme3Nu2VFq"

    init {
        getExcursionData()
    }

    private fun getExcursionData() {
        viewModelScope.launch {
            getExcursionByIdUseCase.invoke(ID).collect { response ->
                _stateExcursion.postValue(response)
            }
        }
    }

    fun downloadDataExcursion() {
        viewModelScope.launch {
            downloadDataExcursionUseCase.invoke(ID).collect { response ->
                _stateAudio.postValue(response)
            }
        }
    }
}