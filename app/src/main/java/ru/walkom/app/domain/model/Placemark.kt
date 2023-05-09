package ru.walkom.app.domain.model

import com.yandex.mapkit.geometry.Point
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

data class Placemark(
    val id: Int,
    val point: Point,
    val title: String,
    val image: Int,
    var isPassed: Boolean
)

open class PlacemarkRealm: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var title: String = ""
    var photo: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    @Ignore
    var isPassed: Boolean = false
}