package ru.walkom.app.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionNew
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Response

interface ExcursionRepository {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>>
    fun getSizeFilesExcursion(id: String): Flow<Response<Int>>
    fun downloadFilesExcursion(id: String): Flow<Response<Boolean>>
    fun uploadExcursion(excursion: ExcursionNew, id: String): Flow<Response<Boolean>>
}