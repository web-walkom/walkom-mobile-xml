package ru.walkom.app.presentation.screens.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.ARGUMENT_EXCURSION_ID
import ru.walkom.app.domain.model.ExcursionCamera
import ru.walkom.app.domain.model.Model3D
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import javax.inject.Inject


@HiltViewModel
class CameraARViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase,
    private val state: SavedStateHandle
): ViewModel() {

    private val excursionId = state.get<String>(ARGUMENT_EXCURSION_ID)

    private val _stateModels = MutableLiveData<Response<ExcursionCamera?>>()
    val stateModels: LiveData<Response<ExcursionCamera?>> get() = _stateModels

    lateinit var models: List<Model3D>

    init {
        viewModelScope.launch {
            if (excursionId != null) {
                getExcursionByIdUseCase.invoke(excursionId, ExcursionCamera::class.java).collect { response ->
                    _stateModels.postValue(response)
                }
            }
        }
    }
}