package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Waypoint(
    val id: Int,
    val point: Point,
    val audio: Int?,
    val affiliationPlacemarkId: Int?
)
