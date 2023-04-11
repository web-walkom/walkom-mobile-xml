package ru.walkom.app.domain.model

import android.graphics.drawable.Drawable
import com.yandex.mapkit.geometry.Point

data class AudioLocation(
    val id: Int,
    val point: Point,
    val audio: List<Int>
)
