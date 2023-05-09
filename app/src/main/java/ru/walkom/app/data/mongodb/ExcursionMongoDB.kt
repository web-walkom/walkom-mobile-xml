package ru.walkom.app.data.mongodb

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.ExcursionRealm
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Waypoint

interface ExcursionMongoDB {
    fun configureRealm()
    fun getExcursions(): Flow<List<ExcursionRealm>>
    fun getExcursionById(id: String): Excursion
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
}