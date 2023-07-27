package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point

data class PointModel(
    val altitude: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)