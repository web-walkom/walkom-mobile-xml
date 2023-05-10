package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Placemark(
    val id: Int,
    val point: Point,
    val title: String,
    val image: Int,
    var isPassed: Boolean
)

data class PlacemarkDB(
    val title: String = "",
    val photos: List<String> = emptyList(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)