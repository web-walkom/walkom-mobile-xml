package ru.walkom.app.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Placemark(
    val title: String = "",
    val photos: List<String> = emptyList(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)

@Parcelize
data class PlacemarkInfoDialog(
    val title: String = "",
    val photos: List<String> = emptyList(),
): Parcelable