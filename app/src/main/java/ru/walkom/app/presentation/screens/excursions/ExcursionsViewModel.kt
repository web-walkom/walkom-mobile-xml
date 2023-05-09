package ru.walkom.app.presentation.screens.excursions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.domain.model.ExcursionRealm
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionsViewModel @Inject constructor(
    private val getExcursionsUseCase : GetExcursionsUseCase
): ViewModel() {

    var excursions = emptyList<ExcursionRealm>()

    init {
        viewModelScope.launch {
//            getExcursionsUseCase.invoke().collect {
//                excursions = it
//                Log.i(TAG, "Success get excursions")
//            }
//            getExcursionsUseCase.invoke()
        }
    }
}