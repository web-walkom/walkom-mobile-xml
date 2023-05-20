package ru.walkom.app.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ExcursionItem(
    var id: String = "",
    val title: String = "",
    val photos: List<String> = emptyList()
): Parcelable

data class ExcursionOpen(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val photos: List<String> = emptyList()
)

data class ExcursionMap(
    var id: String = "",
    val placemarks: List<Placemark> = emptyList(),
    val waypoints: List<Waypoint> = emptyList()
)

data class ExcursionCamera(
    var id: String = "",
    val models: List<Model3D> = emptyList()
)

data class ExcursionNew(
    val title: String = "",
    val photos: List<String> = emptyList(),
    val description: String = "",
    val placemarks: List<Placemark> = emptyList(),
    val waypoints: List<Waypoint> = emptyList(),
    val models: List<Model3D> = emptyList()
)