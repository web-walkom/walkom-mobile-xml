package ru.walkom.app.domain.model

data class Excursion(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val photos: List<String>,
//    val placemarks: List<Placemark>,
//    val waypoints: List<Waypoint>
)
data class ExcursionDB(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int? = null,
    val photos: List<String> = emptyList(),
    val placemarks: List<PlacemarkDB> = emptyList(),
    val waypoints: List<WaypointDB> = emptyList()
)