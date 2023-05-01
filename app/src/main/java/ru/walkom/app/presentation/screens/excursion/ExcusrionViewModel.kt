package ru.walkom.app.presentation.screens.excursions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase
): ViewModel() {

    lateinit var excursion: Excursion

    fun getExcursionById() {
        excursion = getExcursionByIdUseCase.invoke("1")
    }
}