package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Waypoint(
    val id: Int,
    val point: Point,
    val audio: Int?,
    val affiliationPlacemarkId: Int?,
    var isPassed: Boolean
)

data class WaypointDB(
    val audio: String = "",
    val placemarkId: Int? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)