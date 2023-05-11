package ru.walkom.app.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.ExcursionDB
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

interface ExcursionRepository {
    fun getExcursions(): Flow<Response<List<ExcursionDB>>>
    fun getExcursionById(id: String): Flow<Response<ExcursionDB?>>
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
    fun downloadDataExcursionUseCase(id: String): Flow<Response<Boolean>>
}