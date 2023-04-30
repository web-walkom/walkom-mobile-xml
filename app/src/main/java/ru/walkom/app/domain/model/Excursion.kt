package ru.walkom.app.domain.model

data class Excursion(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val photo: String,
//    val placemarks: List<Placemark>,
//    val waypoints: List<Waypoint>
)
