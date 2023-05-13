package ru.walkom.app.domain.use_case

import ru.walkom.app.domain.repository.ExcursionRepository

class GetExcursionByIdUseCase(
    private val repository: ExcursionRepository
) {

    operator fun <T> invoke(id: String, type: Class<T>) = repository.getExcursionById(id, type)
}