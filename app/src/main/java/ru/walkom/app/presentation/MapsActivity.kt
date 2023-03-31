package ru.walkom.app.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.*
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
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

class MapsActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener {

    private lateinit var binding: ActivityMapsBinding

    private val MAPKIT_API_KEY: String = "4e10e9f2-d783-499c-b77d-8fc64489b4ac"

    // Points
    private val INTERMEDIATE_PERM_BEAR_LOCATION: Point = Point(58.010433, 56.237325)
    private val PERM_BEAR_LOCATION: Point = Point(58.010418, 56.237335)

    private val INTERMEDIATE_PROVINCIAL_CHILDRENS_SHELTER_LOCATION: Point = Point(58.011458, 56.239391)
    private val PROVINCIAL_CHILDRENS_SHELTER_LOCATION: Point = Point(58.011385, 56.239513)

    private val INTERMEDIATE_CHURCH_VIRGIN_LOCATION = Point(58.012153, 56.241898)
    private val CHURCH_VIRGIN_LOCATION: Point = Point(58.012248, 56.242676)

    private val THEOLOGICAL_COLLEGE_LOCATION: Point = Point(58.013174, 56.243045)

    private val INTERMEDIATE_HOUSE_DEMIDOV_LOCATION = Point(58.012626, 56.243570)
    private val HOUSE_DEMIDOV_LOCATION = Point(58.012553, 56.243556)

    private val MERCHANTS_ESTATE_LOCATION = Point(58.013015, 56.243879)

    private val INTERMEDIATE_STAROKIRPICHNY_LANE_LOCATION = Point(58.012757, 56.244027)
    private val STAROKIRPICHNY_LANE_LOCATION = Point(58.012607, 56.244302)

    private val INTERMEDIATE_PHARMACIES_BONET_BARTMINSKY_LOCATION = Point(58.012951, 56.244698)
    private val PHARMACIES_BONET_BARTMINSKY_LOCATION = Point(58.012810, 56.244517)

    private val INTERMEDIATE_HOUSE_PADALKA_LOCATION = Point(58.012951, 56.244698)
    private val HOUSE_PADALKA_LOCATION = Point(58.012867, 56.244769)

    private val HOUSE_KAMENSKY_LOCATION = Point(58.013235, 56.244661)

    private val INTERMEDIATE_HOUSE_STEPANOV_LOCATION = Point(58.013070, 56.245109)
    private val HOUSE_STEPANOV_LOCATION = Point(58.013006, 56.245146)

    private val INTERMEDIATE_HOUSE_BAZANOV_LOCATION = Point(58.013412, 56.246297)
    private val HOUSE_BAZANOV_LOCATION = Point(58.013230, 56.245909)

    private val INTERMEDIATE_PIOTROVSKY_BOOKSTORE_LOCATION = Point(58.013511, 56.246665)
    private val PIOTROVSKY_BOOKSTORE_LOCATION = Point(58.013506, 56.246934)

    private val INTERMEDIATE_PERM_OPERA_HOUSE_LOCATION_1 = Point(58.014427, 56.247151)
    private val INTERMEDIATE_PERM_OPERA_HOUSE_LOCATION_2 = Point(58.015203, 56.246618)
    private val PERM_OPERA_HOUSE_LOCATION = Point(58.015961, 56.245820)

    private val INTERMEDIATE_HOUSE_TUPITSYNS_LOCATION = Point(58.015515, 56.247746)
    private val HOUSE_TUPITSYNS_LOCATION = Point(58.015318, 56.248883)

    private val INTERMEDIATE_PSATU_LOCATION = Point(58.015761, 56.247541)
    private val PSATU_LOCATION = Point(58.016081, 56.247895)

    private val INTERMEDIATE_EUROPEAN_NUMBERS_LOCATION = Point(58.017509, 56.246013)
    private val EUROPEAN_NUMBERS_LOCATION = Point(58.017358, 56.245424)

    private val INTERMEDIATE_HOUSE_POPOV_LOCATION = Point(58.018438, 56.245127)
    private val HOUSE_POPOV_LOCATION = Point(58.018312, 56.244831)

    private val INTERMEDIATE_KAMA_RIVER_SHIPPING_COMPANY_LOCATION = Point(58.018574, 56.245447)
    private val KAMA_RIVER_SHIPPING_COMPANY_LOCATION = Point(58.018464, 56.245577)

    private val INTERMEDIATE_HOUSE_MESHKOV_LOCATION_1 = Point(58.018801, 56.246172)
    private val INTERMEDIATE_HOUSE_MESHKOV_LOCATION_2 = Point(58.018581, 56.246479)
    private val INTERMEDIATE_HOUSE_MESHKOV_LOCATION_3 = Point(58.018750, 56.246888)
    private val HOUSE_MESHKOV_LOCATION = Point(58.018745, 56.246547)

    private val INTERMEDIATE_HOUSE_WITH_LIONS_LOCATION = Point(58.018164, 56.243980)
    private val HOUSE_WITH_LIONS_LOCATION = Point(58.017954, 56.244041)

    private val INTERMEDIATE_HOUSE_VERDEREVSKY_LOCATION = Point(58.017930, 56.243147)
    private val HOUSE_VERDEREVSKY_LOCATION = Point(58.017773, 56.243241)

    private val INTERMEDIATE_BATHHOUSE_KASHINA_LOCATION = Point(58.017638, 56.241350)
    private val BATHHOUSE_KASHINA_LOCATION = Point(58.017330, 56.241714)

    private val HOUSE_CHERDYNTSEV_LOCATION = Point(58.017911, 56.240654)

    private val INTERMEDIATE_SPIRITUAL_CONSISTORY_LOCATION = Point(58.017082, 56.239363)
    private val SPIRITUAL_CONSISTORY_LOCATION = Point(58.017292, 56.239172)

    private val INTERMEDIATE_CATHEDRAL_LOCATION = Point(58.016256, 56.234080)
    private val CATHEDRAL_LOCATION = Point(58.016414, 56.234689)

    private val OBSERVATION_DECK_LOCATION = Point(58.016927, 56.233439)

    private val SALT_PERMIAN_LAND_LOCATION = Point(58.016668, 56.231868)


    lateinit var userLocation: UserLocationLayer
    private var mapObjects: MapObjectCollection? = null
    private var transportRouter: PedestrianRouter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mapKit: MapKit = MapKitFactory.getInstance()
        requestLocationPermission()

        userLocation = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocation.isVisible = false
        userLocation.setObjectListener(this)

        transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        mapObjects = binding.mapview.map.mapObjects.addCollection()
        submitRequest()

        binding.mapview.map.move(
            CameraPosition(PERM_BEAR_LOCATION, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )

        binding.back.setOnClickListener {
            //getActivity()?.onBackPressed()
        }

        binding.location.setOnClickListener {
            userLocation.cameraPosition()?.let { cameraPosition ->
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
        userLocation.setAnchor(
            PointF((binding.mapview.width() * 0.5).toFloat(), (binding.mapview.height() * 0.5).toFloat()),
            PointF((binding.mapview.width() * 0.5).toFloat(), (binding.mapview.height() * 0.83).toFloat())
        )

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawLocationMark(point: Point) {
        val view = View(this).apply {
            background = getDrawable(R.drawable.location_place)
        }
        mapObjects?.addPlacemark(point, ViewProvider(view))
    }

    private fun submitRequest() {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        // Places of the tour
        drawLocationMark(PERM_BEAR_LOCATION)
        drawLocationMark(PROVINCIAL_CHILDRENS_SHELTER_LOCATION)
        drawLocationMark(CHURCH_VIRGIN_LOCATION)
        drawLocationMark(THEOLOGICAL_COLLEGE_LOCATION)
        drawLocationMark(HOUSE_DEMIDOV_LOCATION)
        drawLocationMark(MERCHANTS_ESTATE_LOCATION)
        drawLocationMark(STAROKIRPICHNY_LANE_LOCATION)
        drawLocationMark(PHARMACIES_BONET_BARTMINSKY_LOCATION)
        drawLocationMark(HOUSE_PADALKA_LOCATION)
        drawLocationMark(HOUSE_KAMENSKY_LOCATION)
        drawLocationMark(HOUSE_STEPANOV_LOCATION)
        drawLocationMark(HOUSE_BAZANOV_LOCATION)
        drawLocationMark(PIOTROVSKY_BOOKSTORE_LOCATION)
        drawLocationMark(PERM_OPERA_HOUSE_LOCATION)
        drawLocationMark(HOUSE_TUPITSYNS_LOCATION)
        drawLocationMark(PSATU_LOCATION)
        drawLocationMark(EUROPEAN_NUMBERS_LOCATION)
        drawLocationMark(HOUSE_POPOV_LOCATION)
        drawLocationMark(KAMA_RIVER_SHIPPING_COMPANY_LOCATION)
        drawLocationMark(HOUSE_MESHKOV_LOCATION)
        drawLocationMark(HOUSE_WITH_LIONS_LOCATION)
        drawLocationMark(HOUSE_VERDEREVSKY_LOCATION)
        drawLocationMark(BATHHOUSE_KASHINA_LOCATION)
        drawLocationMark(HOUSE_CHERDYNTSEV_LOCATION)
        drawLocationMark(SPIRITUAL_CONSISTORY_LOCATION)
        drawLocationMark(CATHEDRAL_LOCATION)
        drawLocationMark(OBSERVATION_DECK_LOCATION)
        drawLocationMark(SALT_PERMIAN_LAND_LOCATION)

        // Route points
        requestPoints.add(RequestPoint(INTERMEDIATE_PERM_BEAR_LOCATION, RequestPointType.WAYPOINT, null))
        //requestPoints.add(RequestPoint(INTERMEDIATE_PROVINCIAL_CHILDRENS_SHELTER_LOCATION, RequestPointType.WAYPOINT, null))
        //requestPoints.add(RequestPoint(INTERMEDIATE_CHURCH_VIRGIN_LOCATION, RequestPointType.WAYPOINT, null))
        //requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_DEMIDOV_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_STAROKIRPICHNY_LANE_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_PHARMACIES_BONET_BARTMINSKY_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_PADALKA_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_STEPANOV_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_BAZANOV_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_PIOTROVSKY_BOOKSTORE_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_PERM_OPERA_HOUSE_LOCATION_1, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_PERM_OPERA_HOUSE_LOCATION_2, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_TUPITSYNS_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_PSATU_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_EUROPEAN_NUMBERS_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_POPOV_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_KAMA_RIVER_SHIPPING_COMPANY_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_MESHKOV_LOCATION_1, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_MESHKOV_LOCATION_2, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_MESHKOV_LOCATION_3, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_MESHKOV_LOCATION_2, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_WITH_LIONS_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_HOUSE_VERDEREVSKY_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_BATHHOUSE_KASHINA_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_SPIRITUAL_CONSISTORY_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(INTERMEDIATE_CATHEDRAL_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(OBSERVATION_DECK_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(SALT_PERMIAN_LAND_LOCATION, RequestPointType.WAYPOINT, null))

        transportRouter!!.requestRoutes(requestPoints, TimeOptions(), this)
    }
}