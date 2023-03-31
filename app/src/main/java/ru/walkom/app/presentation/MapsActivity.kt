package ru.walkom.app.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import ru.walkom.app.R
import ru.walkom.app.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), UserLocationObjectListener, DrivingSession.DrivingRouteListener {

    private lateinit var binding: ActivityMapsBinding

    private val MAPKIT_API_KEY: String = "4e10e9f2-d783-499c-b77d-8fc64489b4ac"
    private val ROUTE_START_LOCATION: Point = Point(58.010420, 56.237355)
    private val ROUTE_END_LOCATION: Point = Point(58.015564, 56.246246)
    private val SCREEN_CENTER: Point = Point(
        (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
        (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
    )

    lateinit var locationMapKit: UserLocationLayer
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mapKit: MapKit = MapKitFactory.getInstance()
        requestLocationPermission()

        locationMapKit = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        locationMapKit.isVisible = true
        locationMapKit.setObjectListener(this)

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = binding.mapview.map.mapObjects.addCollection()

        submitRequest()

        binding.back.setOnClickListener {
            //getActivity()?.onBackPressed()
        }

        binding.location.setOnClickListener {
            locationMapKit.cameraPosition()?.let { cameraPosition ->
                binding.mapview.map.move(
                    CameraPosition(cameraPosition.target, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }

    override fun onStart() {
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()

        super.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()

        super.onStop()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        locationMapKit.setAnchor(
            PointF((binding.mapview.width() * 0.5).toFloat(), (binding.mapview.height() * 0.5).toFloat()),
            PointF((binding.mapview.width() * 0.5).toFloat(), (binding.mapview.height() * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.location_user))

//        val picIcon = userLocationView.pin.useCompositeIcon()
//        picIcon.setIcon("icon", ImageProvider.fromResource(this, R.drawable.location_place), IconStyle()
//            .setAnchor(PointF(0f, 0f))
//            .setRotationType(RotationType.NO_ROTATION)
//            .setZIndex(0f)
//            .setScale(1f)
//        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
//        for (route in p0) {
//            mapObjects!!.addPolygon(route.geometry)
//        }
    }

    override fun onDrivingRoutesError(p0: Error) {

    }

    private fun submitRequest() {

    }
}