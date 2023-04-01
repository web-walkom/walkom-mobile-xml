package ru.walkom.app.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.gorisse.thomas.lifecycle.getActivity
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import ru.walkom.app.R
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.common.Constants.UNKNOWN_ERROR
import ru.walkom.app.databinding.ActivityMapsBinding

class MapActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener {

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

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var binding: ActivityMapsBinding
    lateinit var userLocationLayer: UserLocationLayer
    private var mapObjects: MapObjectCollection? = null
    private var transportRouter: PedestrianRouter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowFlag()

        MapKitFactory.initialize(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        //binding.mapview.map.isNightModeEnabled = true
        setContentView(binding.root)

        mapObjects = binding.mapview.map.mapObjects.addCollection()
        requestLocationPermission()

        val mapKit: MapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.setObjectListener(this)

        buildRoute()
        initialApproximation()
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

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0)
            return
        }
    }

    private fun drawLocationMark(point: Point) {
        val view = View(this).apply {
            background = getDrawable(R.drawable.location_place)
        }
        mapObjects?.addPlacemark(point, ViewProvider(view))
    }

    private fun buildRoute() {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        for (place in PLACE_LOCATIONS)
            drawLocationMark(place)

        for (waypoint in WAYPOINTS_LOCATIONS)
            requestPoints.add(RequestPoint(waypoint, RequestPointType.WAYPOINT, null))

        transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        transportRouter!!.requestRoutes(requestPoints, TimeOptions(), this)
    }

    private fun initialApproximation() {
        binding.mapview.map.move(
            CameraPosition(Point(58.010455, 56.229435), 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    fun startExcursion(view: View) {
        binding.mapview.map.move(
            CameraPosition(PLACE_LOCATIONS[0], 20.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    fun locationMe(view: View) {
        userLocationLayer.cameraPosition()?.let { cameraPosition ->
            binding.mapview.map.move(
                CameraPosition(cameraPosition.target, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
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

        userLocationView.pin.setIcon(ImageProvider.fromResource(this, R.drawable.location_user))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.location_user))
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(view: UserLocationView) {
    }

    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        for (route in routes) {
            mapObjects!!.addPolyline(route.geometry)
        }
    }

    override fun onMasstransitRoutesError(error: Error) {
        Toast.makeText(this, UNKNOWN_ERROR, Toast.LENGTH_SHORT).show()
        Log.e(TAG, error.toString())
    }
}