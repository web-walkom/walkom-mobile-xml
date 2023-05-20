package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Waypoint(
    val audio: String = "",
    val point: Point = Point(),
    var isPassed: Boolean = false
)