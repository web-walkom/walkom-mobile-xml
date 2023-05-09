package ru.walkom.app.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

data class Excursion(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val photo: String,
//    val placemarks: List<Placemark>,
//    val waypoints: List<Waypoint>
)

//class ExcursionRealm : RealmObject() {
//    @PrimaryKey
//    var _id: ObjectId = ObjectId.invoke()
//    var title: String = ""
//    var description: String = ""
//    var price: Int? = null
//    var photos: String = ""
//    @Ignore
//    var placemarks: List<PlacemarkRealm> = realmListOf()
//    @Ignore
//    var waypoints: List<WaypointRealm> = realmListOf()
//}

open class ExcursionRealm : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var title: String = ""
    var description: String = ""
    var price: Int? = null
    var photos: String = ""
    @Ignore
    var placemarks: List<PlacemarkRealm>? = null
    @Ignore
    var waypoints: List<WaypointRealm>? = null
}