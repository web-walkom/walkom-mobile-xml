package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

data class Waypoint(
    val id: Int,
    val point: Point,
    val audio: Int?,
    val affiliationPlacemarkId: Int?,
    var isPassed: Boolean
)

open class WaypointRealm: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var audio: String = ""
    var placemarkId: ObjectId? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    @Ignore
    var isPassed: Boolean = false
}