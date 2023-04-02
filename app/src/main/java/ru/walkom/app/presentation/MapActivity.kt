package ru.walkom.app.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.gorisse.thomas.lifecycle.getActivity
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import ru.walkom.app.R
import ru.walkom.app.common.Constants.ERROR_DRAW_ROUTE
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityMapsBinding

class MapActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener, CameraListener {

    private val PLACE_LOCATIONS = listOf<Point>(
        Point(58.010418, 56.237335),
        Point(58.011385, 56.239513),
        Point(58.012248, 56.242676),
        Point(58.013174, 56.243045),
        Point(58.012553, 56.243556),
        Point(58.013015, 56.243879),
        Point(58.012607, 56.244302),
        Point(58.012810, 56.244517),
        Point(58.012867, 56.244769),
        Point(58.013235, 56.244661),
        Point(58.013006, 56.245146),
        Point(58.013230, 56.245909),
        Point(58.013506, 56.246934),
        Point(58.015961, 56.245820),
        Point(58.015318, 56.248883),
        Point(58.016081, 56.247895),
        Point(58.017358, 56.245424),
        Point(58.018312, 56.244831),
        Point(58.018464, 56.245577),
        Point(58.018745, 56.246547),
        Point(58.017954, 56.244041),
        Point(58.017773, 56.243241),
        Point(58.017330, 56.241714),
        Point(58.017911, 56.240654),
        Point(58.017292, 56.239172),
        Point(58.016414, 56.234689),
        Point(58.016927, 56.233439),
        Point(58.016668, 56.231868)
    )

    private val WAYPOINTS_LOCATIONS = listOf<Point>(
        Point(58.010433, 56.237325),
        Point(58.011458, 56.239391),
        Point(58.012153, 56.241898),
        Point(58.012626, 56.243570),
        Point(58.012757, 56.244027),
        Point(58.012951, 56.244698),
        Point(58.012951, 56.244698),
        Point(58.013070, 56.245109),
        Point(58.013412, 56.246297),
        Point(58.013511, 56.246665),
        Point(58.014427, 56.247151),
        Point(58.015203, 56.246618),
        Point(58.015515, 56.247746),
        Point(58.017509, 56.246013),
        Point(58.018438, 56.245127),
        Point(58.018574, 56.245447),
        Point(58.018801, 56.246172),
        Point(58.018713, 56.246883),
        Point(58.018164, 56.243980),
        Point(58.017930, 56.243147),
        Point(58.017638, 56.241350),
        Point(58.017082, 56.239363),
        Point(58.016256, 56.234080),
        Point(58.016927, 56.233439),
        Point(58.016668, 56.231868)
    )

    private val PERM_LOCATION = Point(58.010455, 56.229435)

    private var placemarks = ArrayList<PlacemarkMapObject>()
    private var polylines = ArrayList<PolylineMapObject>()

    private lateinit var binding: ActivityMapsBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    lateinit var userLocationLayer: UserLocationLayer
    private var mapObjects: MapObjectCollection? = null
    private var transportRouter: PedestrianRouter? = null

    private var permissionLocation = false
    private var followUserLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setWindowFlag()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[ACCESS_FINE_LOCATION] == true ||
                permissions[ACCESS_COARSE_LOCATION] == true) {
                onMapReady()
            }
        }

        checkPermission()
    }

    private fun setWindowFlag() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        supportActionBar?.elevation = 0f
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.attributes.flags = window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv() and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION.inv()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        )
            onMapReady()
        else
            checkLocationPermission.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()

        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        binding.mapview.map.addCameraListener(this)
        cameraUserPosition()
        permissionLocation = true

        mapObjects = binding.mapview.map.mapObjects.addCollection()
        drawingRoute()
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            binding.mapview.map.move(
                CameraPosition(userLocationLayer.cameraPosition()!!.target, 18.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            binding.mapview.map.move(
                CameraPosition(PERM_LOCATION, 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun drawLocationMark(point: Point) {
        val placemark = mapObjects?.addPlacemark(point)

        placemark?.setIcon(
            ImageProvider.fromResource(this, R.drawable.location_place),
            IconStyle()
                .setAnchor(PointF(0.55f, 1f))
                .setRotationType(RotationType.NO_ROTATION)
                .setZIndex(0f)
                .setScale(1f)
        )
        placemark?.setText("Hello")

        if (placemark != null)
            placemarks.add(placemark)
    }

    private fun drawingRoute() {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        for (place in PLACE_LOCATIONS)
            drawLocationMark(place)

        for (waypoint in WAYPOINTS_LOCATIONS)
            requestPoints.add(RequestPoint(waypoint, RequestPointType.WAYPOINT, null))

        transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        transportRouter!!.requestRoutes(requestPoints, TimeOptions(), this)
    }

    fun startExcursion(view: View) {
        binding.mapview.map.move(
            CameraPosition(PLACE_LOCATIONS[0], 20.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    fun locationMe(view: View) {
        if (permissionLocation) {
            cameraUserPosition()
            followUserLocation = true
        }
        else
            checkPermission()
    }

    fun closeExcursion(view: View) {
        getActivity()?.onBackPressed()
        //finish()
    }

    fun zoomIn(view: View) {
        binding.mapview.map.move(
            CameraPosition(
                binding.mapview.map.cameraPosition.target,
                binding.mapview.map.cameraPosition.zoom + 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
    }

    fun zoomOut(view: View) {
        binding.mapview.map.move(
            CameraPosition(
                binding.mapview.map.cameraPosition.target,
                binding.mapview.map.cameraPosition.zoom - 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
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
        setAnchor()

        userLocationView.pin.setIcon(
            ImageProvider.fromResource(this, R.drawable.location_user),
            IconStyle()
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(this, R.drawable.location_user),
            IconStyle()
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(view: UserLocationView) {
    }

    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        for (route in routes) {
            val polyline = mapObjects!!.addPolyline(route.geometry)
            polyline.setStrokeColor(Color.argb(255, 92, 163, 255))
            polyline.strokeWidth = 5f
            polylines.add(polyline)
        }
    }

    override fun onMasstransitRoutesError(error: Error) {
        Toast.makeText(this, ERROR_DRAW_ROUTE, Toast.LENGTH_SHORT).show()
        Log.e(TAG, error.toString())
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            if (followUserLocation)
                setAnchor()
        }
        else {
            if (!followUserLocation)
                noAnchor()
        }

        if (cameraPosition.zoom < 14.5) {
            //binding.mapview.map.mapObjects.isVisible = false
            for (placemark in placemarks)
                placemark.isVisible = false
        }
        else {
            //binding.mapview.map.mapObjects.isVisible = true
            for (placemark in placemarks)
                placemark.isVisible = true
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

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF(
                (binding.mapview.width() * 0.5).toFloat(),
                (binding.mapview.height() * 0.5).toFloat()
            ),
            PointF(
                (binding.mapview.width() * 0.5).toFloat(),
                (binding.mapview.height() * 0.83).toFloat()
            )
        )

        //binding.location.setImageResource(R.drawable.navigation)
        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer.resetAnchor()
        //binding.location.setImageResource(R.drawable.navigation_disabled)
    }
}