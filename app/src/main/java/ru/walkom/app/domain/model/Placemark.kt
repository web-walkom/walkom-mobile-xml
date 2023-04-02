package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class Placemark(
    var point: Point,
    var title: String
)
