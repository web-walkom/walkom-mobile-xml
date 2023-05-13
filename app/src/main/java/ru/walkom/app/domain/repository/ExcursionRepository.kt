package ru.walkom.app.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

interface ExcursionRepository {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun getExcursionById(id: String): Flow<Response<ExcursionOpen?>>
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
    fun downloadFilesExcursion(id: String): Flow<Response<Boolean>>
}