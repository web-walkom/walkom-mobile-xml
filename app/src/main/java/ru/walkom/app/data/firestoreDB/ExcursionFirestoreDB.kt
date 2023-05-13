package ru.walkom.app.data.firestoreDB

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionMap
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

interface ExcursionFirestoreDB {
    fun getExcursions(): Flow<Response<List<ExcursionItem>>>
    fun <T> getExcursionById(id: String, type: Class<T>): Flow<Response<T?>>
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
}