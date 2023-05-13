package ru.walkom.app.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionMap
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

interface ExcursionRepository {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>>
    fun downloadFilesExcursion(id: String): Flow<Response<Boolean>>
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
}