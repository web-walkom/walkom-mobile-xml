package ru.walkom.app.data.mongodb

import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Waypoint

interface ExcursionMongoDB {
    fun getExcursions(): List<Excursion>
    fun getExcursionById(id: String): Excursion
    fun getPlacemarksExcursion(id: String): List<Placemark>
    fun getWaypointsExcursion(id: String): List<Waypoint>
}