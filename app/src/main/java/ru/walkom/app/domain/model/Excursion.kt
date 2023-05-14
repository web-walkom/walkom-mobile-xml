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
    val placemarks: List<Placemark> = emptyList(),
    val waypoints: List<Waypoint> = emptyList(),
    val models: List<Model3D> = emptyList()
)

data class ExcursionAll(
    val title: String = "",
    val photos: List<String> = emptyList(),
    val description: String = "",
    val price: Int? = null,
    val placemarks: List<Placemark> = emptyList(),
    val waypoints: List<Waypoint> = emptyList()
)