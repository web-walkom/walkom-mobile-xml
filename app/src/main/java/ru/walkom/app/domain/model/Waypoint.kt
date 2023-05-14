package ru.walkom.app.domain.model

data class Waypoint(
    val audio: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isPassed: Boolean = false
)