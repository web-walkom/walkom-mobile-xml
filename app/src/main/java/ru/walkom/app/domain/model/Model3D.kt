package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Model3D(
    val model: String = "",
    var point: Point = Point()
)