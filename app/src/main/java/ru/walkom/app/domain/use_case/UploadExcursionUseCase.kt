package ru.walkom.app.domain.use_case

import ru.walkom.app.domain.model.ExcursionNew
import ru.walkom.app.domain.repository.ExcursionRepository

class UploadExcursionUseCase(
    private val repository: ExcursionRepository
) {

    operator fun invoke(excursion: ExcursionNew, id: String) = repository.uploadExcursion(excursion, id)
}