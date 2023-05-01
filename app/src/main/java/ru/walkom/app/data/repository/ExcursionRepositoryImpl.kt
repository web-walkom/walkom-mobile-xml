package ru.walkom.app.data.repository

import ru.walkom.app.data.mongodb.ExcursionMongoDB
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.repository.ExcursionRepository

class ExcursionRepositoryImpl(
    private val excursionMongoDB: ExcursionMongoDB
): ExcursionRepository {

    override fun getExcursions(): List<Excursion> = excursionMongoDB.getExcursions()

    override fun getExcursionById(id: String): Excursion = excursionMongoDB.getExcursionById(id)

    override fun getPlacemarksExcursion(id: String): List<Placemark> = excursionMongoDB.getPlacemarksExcursion(id)

    override fun getWaypointsExcursion(id: String): List<Waypoint> = excursionMongoDB.getWaypointsExcursion(id)
}