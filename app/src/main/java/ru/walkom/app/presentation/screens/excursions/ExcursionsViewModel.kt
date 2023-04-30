package ru.walkom.app.presentation.screens.excursions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionsViewModel @Inject constructor(
    private val getExcursionsUseCase : GetExcursionsUseCase
): ViewModel() {

    var excursions: List<Excursion> = listOf()

    fun getExcursions() {
        excursions = getExcursionsUseCase.invoke()
    }
}