package ru.walkom.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.storage.ExcursionStorage
import ru.walkom.app.domain.model.ExcursionNew
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.repository.ExcursionRepository

class ExcursionRepositoryImpl(
    private val excursionFirestoreDB: ExcursionFirestoreDB,
    private val excursionStorage: ExcursionStorage
): ExcursionRepository {

    override fun getExcursions(): Flow<Response<List<ExcursionItem>>> = excursionFirestoreDB.getExcursions()

    override fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>> =
        excursionFirestoreDB.getExcursionById(id, type)

    override fun getSizeFilesExcursion(id: String): Flow<Response<Int>> = excursionStorage.getSizeFilesExcursion(id)

    override fun downloadFilesExcursion(id: String) = flow<Response<Boolean>> {
        excursionStorage.downloadAudioExcursion(id).collect { responseAudio ->
            when (responseAudio) {
                is Response.Loading -> emit(responseAudio)
                is Response.Error -> emit(responseAudio)
                is Response.Success -> {
                    excursionStorage.downloadModelsExcursion(id).collect { responseModels ->
                        emit(responseModels)
                    }
                }
            }
        }
    }

    override fun uploadExcursion(excursion: ExcursionNew): Flow<Response<Boolean>> = excursionFirestoreDB.uploadExcursion(excursion)
}