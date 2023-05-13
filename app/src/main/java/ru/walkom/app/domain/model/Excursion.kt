package ru.walkom.app.domain.model

data class ExcursionItem(
    var id: String = "",
    val title: String = "",
    val photos: List<String> = emptyList()
)

data class ExcursionOpen(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int? = null,
    val photos: List<String> = emptyList()
)

data class ExcursionMap(
    var id: String = "",
    val placemarks: List<PlacemarkDB> = emptyList(),
    val waypoints: List<WaypointDB> = emptyList()
)