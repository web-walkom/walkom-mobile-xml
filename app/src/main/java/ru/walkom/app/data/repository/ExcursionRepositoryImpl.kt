package ru.walkom.app.data.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.storage_s3.ExcursionStorageS3
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.ExcursionDB
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.repository.ExcursionRepository

class ExcursionRepositoryImpl(
    private val excursionFirestoreDB: ExcursionFirestoreDB,
    private val excursionStorageS3: ExcursionStorageS3
): ExcursionRepository {

    override fun getExcursions(): Flow<Response<List<ExcursionDB>>> = excursionFirestoreDB.getExcursions()

    override fun getExcursionById(id: String): Flow<Response<ExcursionDB?>> = excursionFirestoreDB.getExcursionById(id)

    override fun getPlacemarksExcursion(id: String): List<Placemark> = excursionFirestoreDB.getPlacemarksExcursion(id)

    override fun getWaypointsExcursion(id: String): List<Waypoint> = excursionFirestoreDB.getWaypointsExcursion(id)

    override fun downloadAudioExcursion(id: String): Flow<Response<Boolean>> = excursionStorageS3.downloadAudioExcursion(id)
}