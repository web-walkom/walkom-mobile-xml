package ru.walkom.app.presentation.screens.map

import android.graphics.Color
import android.graphics.PointF
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.use_case.GetPlacemarksExcursionUseCase
import ru.walkom.app.domain.use_case.GetWaypointsExcursionUseCase
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getPlacemarksExcursionUseCase: GetPlacemarksExcursionUseCase,
    private val getWaypointsExcursionUseCase: GetWaypointsExcursionUseCase
): ViewModel() {

    val placemarksLocations = getPlacemarksExcursionUseCase.invoke("1")
    val waypointsLocations = getWaypointsExcursionUseCase.invoke("1")

    lateinit var placemarkStart: PlacemarkMapObject
    var placemarkIcons = ArrayList<PlacemarkMapObject>()
    var placemarkCards = ArrayList<PlacemarkMapObject>()
    var waypointIcons = ArrayList<PlacemarkMapObject>()
    var polylines = ArrayList<PolylineMapObject>()

    lateinit var userLocationLayer: UserLocationLayer
    lateinit var mapObjects: MapObjectCollection
    lateinit var transportRouter: PedestrianRouter

    var permissionLocation = false
    var followUserLocation = false
    var statusStartExcursion = false
    var statusPause = false
    var statusBeingStartPoint = false

    fun buildingRoute(resourceIcon: ImageProvider): ArrayList<RequestPoint> {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        for (waypoint in waypointsLocations) {
            requestPoints.add(RequestPoint(waypoint.point, RequestPointType.WAYPOINT, null))
            drawingWaypointIcon(waypoint, resourceIcon)
        }

        return requestPoints
    }

    private fun drawingWaypointIcon(waypoint: Waypoint, resourceIcon: ImageProvider) {
        val viewWaypoint = mapObjects.addPlacemark(waypoint.point)

        viewWaypoint.setIcon(
            resourceIcon,
            IconStyle()
                .setRotationType(RotationType.NO_ROTATION)
                .setZIndex(0f)
                .setScale(0.5f)
        )
        waypointIcons.add(viewWaypoint)
    }

    fun drawingPlacemarkIcon(resourceIcon: ImageProvider) {
        var viewPlacemark: PlacemarkMapObject

        for (placemark in placemarksLocations) {
            viewPlacemark = mapObjects.addPlacemark(placemark.point)
            viewPlacemark.setIcon(
                resourceIcon,
                IconStyle()
                    .setAnchor(PointF(0.55f, 1f))
                    .setRotationType(RotationType.NO_ROTATION)
                    .setZIndex(0f)
                    .setScale(1f)
            )
            placemarkIcons.add(viewPlacemark)
        }
    }

    fun masstransitRoutes(routes: MutableList<Route>) {
        val strokeColor = Color.argb(255, 92, 163, 255)

        for (route in routes) {
            val polyline = mapObjects.addPolyline(route.geometry)
            polyline.setStrokeColor(strokeColor)
            polyline.strokeWidth = 5f
            polylines.add(polyline)
        }
    }

    fun cameraPositionChanged(cameraPosition: CameraPosition) {
        if (cameraPosition.zoom < 18) {
            for (placemark in placemarkCards)
                placemark.isVisible = false
        }
        else {
            for (placemark in placemarkCards)
                placemark.isVisible = true
        }

        if (cameraPosition.zoom < 14.5) {
            for (placemark in placemarkIcons)
                placemark.isVisible = false

            for (waypoint in waypointIcons)
                waypoint.isVisible = false

            placemarkStart.isVisible = false
        }
        else {
            for (placemark in placemarkIcons)
                placemark.isVisible = true

            for (waypoint in waypointIcons)
                waypoint.isVisible = true

            if (!statusStartExcursion)
                placemarkStart.isVisible = true
        }

        if (cameraPosition.zoom < 12.5) {
            for (polyline in polylines)
                polyline.isVisible = false
        }
        else {
            for (polyline in polylines)
                polyline.isVisible = true
        }
    }

    fun setAnchor(width: Int, height: Int) {
        userLocationLayer.setAnchor(
            PointF(
                (width * 0.5).toFloat(),
                (height * 0.5).toFloat()
            ),
            PointF(
                (width * 0.5).toFloat(),
                (height * 0.83).toFloat()
            )
        )

        followUserLocation = false
    }

    fun containsPointArea(area: Point, point: Point, maxDistance: Double): Boolean {
        val distance = getDistanceBetweenPoints(area, point)

        if (distance <= maxDistance)
            return true
        return false
    }

    fun getDistanceNearestWaypoint(locationUser: Point): Double {
        var distanceMin = 0.0
        var distanceNow: Double

        for (waypoint in waypointsLocations) {
            distanceNow = getDistanceBetweenPoints(waypoint.point, locationUser)

            if (waypoint == waypointsLocations[0])
                distanceMin = distanceNow
            else {
                if (distanceNow < distanceMin)
                    distanceMin = distanceNow
            }
        }

        return distanceMin
    }

    private fun getDistanceBetweenPoints(point1: Point, point2: Point): Double {
        val r = 6371

        val areaRadian = Point(
            point1.latitude * kotlin.math.PI / 180,
            point1.longitude * kotlin.math.PI / 180
        )
        val pointRadian = Point(
            point2.latitude * kotlin.math.PI / 180,
            point2.longitude * kotlin.math.PI / 180
        )

        return 2 * r * kotlin.math.asin(
            kotlin.math.sqrt(
                kotlin.math.sin((pointRadian.latitude - areaRadian.latitude) / 2).pow(2.0)
                        + kotlin.math.cos(areaRadian.latitude) * kotlin.math.cos(pointRadian.latitude)
                        * kotlin.math.sin((pointRadian.longitude - areaRadian.longitude) / 2).pow(2.0)
            )
        )
    }
}