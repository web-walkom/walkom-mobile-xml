package ru.walkom.app.data.firestoreDB

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.ExcursionDB
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

interface ExcursionFirestoreDB {
    fun getExcursions(): Flow<Response<List<ExcursionDB>>>
    fun getExcursionById(id: String): Excursion
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
}