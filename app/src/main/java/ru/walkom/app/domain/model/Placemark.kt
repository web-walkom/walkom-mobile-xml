package ru.walkom.app.domain.model

data class Placemark(
    val title: String = "",
    val photos: List<String> = emptyList(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)