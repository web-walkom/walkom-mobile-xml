package ru.walkom.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.storage.ExcursionStorage
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.repository.ExcursionRepository

class ExcursionRepositoryImpl(
    private val excursionFirestoreDB: ExcursionFirestoreDB,
    private val excursionStorage: ExcursionStorage
): ExcursionRepository {

    override fun getExcursions(): Flow<Response<List<ExcursionItem>>> = excursionFirestoreDB.getExcursions()

    override fun getExcursionById(id: String): Flow<Response<ExcursionOpen?>> = excursionFirestoreDB.getExcursionById(id)

    override fun getPlacemarksExcursion(id: String): List<Placemark> = excursionFirestoreDB.getPlacemarksExcursion(id)

    override fun getWaypointsExcursion(id: String): List<Waypoint> = excursionFirestoreDB.getWaypointsExcursion(id)

    override fun downloadFilesExcursion(id: String) = flow<Response<Boolean>> {
//        excursionStorage.getSizeDataExcursion(id).collect {
//            when (it) {
//                is Response.Loading -> emit(it)
//                is Response.Error -> emit(it)
//                is Response.Success -> {
//                    Log.i(TAG, it.data.toString())
//                }
//            }
//        }

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
}