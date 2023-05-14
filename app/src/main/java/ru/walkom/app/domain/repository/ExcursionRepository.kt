package ru.walkom.app.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionAll
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Response

interface ExcursionRepository {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>>
    fun downloadFilesExcursion(id: String): Flow<Response<Boolean>>
    fun uploadExcursion(excursion: ExcursionAll): Flow<Response<Boolean>>
}