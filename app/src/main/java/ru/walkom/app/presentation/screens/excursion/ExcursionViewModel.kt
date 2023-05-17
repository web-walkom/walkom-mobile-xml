package ru.walkom.app.presentation.screens.excursion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.ARGUMENT_EXCURSION
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.DownloadFilesExcursionUseCase
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import ru.walkom.app.domain.use_case.GetSizeFilesExcursionUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase,
    private val getSizeFilesExcursionUseCase: GetSizeFilesExcursionUseCase,
    private val downloadFilesExcursionUseCase: DownloadFilesExcursionUseCase,
    private val state: SavedStateHandle
): ViewModel() {

    private val _stateExcursion = MutableLiveData<Response<ExcursionOpen?>>()
    val stateExcursion: LiveData<Response<ExcursionOpen?>> get() = _stateExcursion

    private val _stateSizeFiles = MutableLiveData<Response<Int>>()
    val stateSizeFiles: LiveData<Response<Int>> get() = _stateSizeFiles

    private val _stateDownload = MutableLiveData<Response<Boolean>>()
    val stateDownload: LiveData<Response<Boolean>> get() = _stateDownload

    private val excursionId = state.get<ExcursionItem>(ARGUMENT_EXCURSION)?.id

    init {
        getExcursionById()
        getSizeFilesExcursion()
    }

    private fun getExcursionById() {
        viewModelScope.launch {
            excursionId?.let {
                getExcursionByIdUseCase.invoke(it, ExcursionOpen::class.java).collect { response ->
                    _stateExcursion.postValue(response)
                }
            }
        }
    }

    private fun getSizeFilesExcursion() {
        viewModelScope.launch {
            excursionId?.let {
                getSizeFilesExcursionUseCase.invoke(it).collect { response ->
                    _stateSizeFiles.postValue(response)
                }
            }
        }
    }

    fun downloadFilesExcursion() {
        viewModelScope.launch {
            excursionId?.let {
                downloadFilesExcursionUseCase.invoke(it).collect { response ->
                    _stateDownload.postValue(response)
                }
            }
        }
    }
}