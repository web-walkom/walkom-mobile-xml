package ru.walkom.app.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.storage.ExcursionStorage
import ru.walkom.app.domain.model.ExcursionDB
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.repository.ExcursionRepository

class ExcursionRepositoryImpl(
    private val excursionFirestoreDB: ExcursionFirestoreDB,
    private val excursionStorage: ExcursionStorage
): ExcursionRepository {

    override fun getExcursions(): Flow<Response<List<ExcursionDB>>> = excursionFirestoreDB.getExcursions()

    override fun getExcursionById(id: String): Flow<Response<ExcursionDB?>> = excursionFirestoreDB.getExcursionById(id)

    override fun getPlacemarksExcursion(id: String): List<Placemark> = excursionFirestoreDB.getPlacemarksExcursion(id)

    override fun getWaypointsExcursion(id: String): List<Waypoint> = excursionFirestoreDB.getWaypointsExcursion(id)

    override fun downloadDataExcursionUseCase(id: String) = flow<Response<Boolean>> {
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