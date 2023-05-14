package ru.walkom.app.data.firestoreDB

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionAll
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Response

interface ExcursionFirestoreDB {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>>
    fun uploadExcursion(excursion: ExcursionAll): Flow<Response<Boolean>>
}