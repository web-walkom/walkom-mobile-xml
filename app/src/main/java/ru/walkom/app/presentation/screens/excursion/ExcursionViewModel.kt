package ru.walkom.app.presentation.screens.excursion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.DownloadFilesExcursionUseCase
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase,
    private val downloadFilesExcursionUseCase: DownloadFilesExcursionUseCase
): ViewModel() {

    private val _stateExcursion = MutableLiveData<Response<ExcursionOpen?>>()
    val stateExcursion: LiveData<Response<ExcursionOpen?>> get() = _stateExcursion

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
            downloadFilesExcursionUseCase.invoke(ID).collect { response ->
                _stateAudio.postValue(response)
            }
        }
    }
}