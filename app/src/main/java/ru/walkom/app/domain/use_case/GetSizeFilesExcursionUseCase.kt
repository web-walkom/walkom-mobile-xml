package ru.walkom.app.domain.use_case

import ru.walkom.app.domain.repository.ExcursionRepository

class GetSizeFilesExcursionUseCase(
    private val repository: ExcursionRepository
) {

    operator fun invoke(id: String) = repository.getSizeFilesExcursion(id)
}