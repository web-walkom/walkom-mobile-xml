package ru.walkom.app.domain.model

import android.os.Parcelable
import com.yandex.mapkit.geometry.Point
import kotlinx.android.parcel.Parcelize

data class Placemark(
    val title: String = "",
    val photos: List<String> = emptyList(),
    val point: Point = Point()
)

@Parcelize
data class PlacemarkInfoDialog(
    val title: String = "",
    val photos: List<String> = emptyList()
): Parcelable