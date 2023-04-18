package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Placemark(
    val id: Int,
    val point: Point,
    val title: String,
    val image: Int,
    var isPassed: Boolean
)
