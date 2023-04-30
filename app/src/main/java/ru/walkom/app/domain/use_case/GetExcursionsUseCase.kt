package ru.walkom.app.domain.use_case

import ru.walkom.app.domain.repository.ExcursionRepository

class GetExcursionsUseCase(
    private val repository: ExcursionRepository
) {

    operator fun invoke() = repository.getExcursions()
}