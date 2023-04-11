package ru.walkom.app.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Audio.Media
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
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
import com.yandex.runtime.ui_view.ViewProvider
import ru.walkom.app.R
import ru.walkom.app.common.Constants.ERROR_DRAW_ROUTE
import ru.walkom.app.common.Constants.NOTIFICATION_CONDITIONS_START_TOUR
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityMapsBinding
import ru.walkom.app.domain.model.AudioLocation
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Waypoint
import java.lang.Math.*
import kotlin.math.pow

class MapActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener, CameraListener {

    private val PLACEMARKS_LOCATIONS = listOf<Placemark>(
        Placemark(
            1,
            Point(58.010418, 56.237335),
            "Пермский медведь",
            R.drawable.bear
        ),
        Placemark(
            2,
            Point(58.011385, 56.239513),
            "Губернский детский приют",
            R.drawable.provincial_children_shelter
        ),
        Placemark(
            3,
            Point(58.012248, 56.242676),
            "Рождественско-Богородицкая церковь",
            R.drawable.church
        ),
        Placemark(
            4,
            Point(58.013174, 56.243045),
            "Пермский государственный институт культуры",
            R.drawable.perm_state_institute_culture
        ),
        Placemark(
            5,
            Point(58.012553, 56.243556),
            "Дом пекарня наследника Демидовых",
            R.drawable.house_demidovs
        ),
        Placemark(
            6,
            Point(58.012758, 56.244125),
            "Триумф. Пермский кинотеатр",
            R.drawable.triumph
        )
    )

    private val WAYPOINTS_LOCATIONS = listOf<Waypoint>(
        Waypoint(
            1,
            Point(58.010433, 56.237325),
            R.raw.guide_r2_2,
            1
        ),
        Waypoint(
            2,
            Point(58.010934, 56.237625),
            R.raw.guide_r2_4,
            null
        ),
        Waypoint(
            3,
            Point(58.011374, 56.239166),
            R.raw.guide_r2_5,
            2
        ),
        Waypoint(
            4,
            Point(58.012153, 56.241898),
            null,
            3
        ),
        Waypoint(
            5,
            Point(58.012477, 56.243069),
            R.raw.guide_r2_6,
            4
        ),
        Waypoint(
            6,
            Point(58.012575, 56.243399),
            R.raw.guide_r2_7,
            5
        ),
        Waypoint(
            7,
            Point(58.012757, 56.244027),
            R.raw.guide_r2_9,
            6
        )
    )

    private val PREVIEW_LOCATION = Point(58.011757, 56.240897)

    private var placemarkIcons = ArrayList<PlacemarkMapObject>()
    private var placemarkCards = ArrayList<PlacemarkMapObject>()
    private var polylines = ArrayList<PolylineMapObject>()

    private lateinit var binding: ActivityMapsBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var transportRouter: PedestrianRouter
    private lateinit var mediaPlayer: MediaPlayer

    private var permissionLocation = false
    private var followUserLocation = false
    private var statusStart = false
    private var statusPause = false
    private var excursionStartEnabled = false

    private var lastVisitPlacemark: Placemark? = null

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

    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        super.onDestroy()
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
        initialApproximation()
        cameraUserPosition()
        permissionLocation = true

        mapObjects = binding.mapview.map.mapObjects.addCollection()
        drawingRoute()
    }

    private fun initialApproximation() {
        binding.mapview.map.move(
            CameraPosition(PREVIEW_LOCATION, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            binding.mapview.map.move(
                CameraPosition(userLocationLayer.cameraPosition()!!.target, 18.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun drawingRoute() {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        for (waypoint in WAYPOINTS_LOCATIONS)
            requestPoints.add(RequestPoint(waypoint.point, RequestPointType.WAYPOINT, null))

        drawingPlacemarkIcon()
        drawingPlacemarkCard()

        transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        transportRouter.requestRoutes(requestPoints, TimeOptions(), this)
    }

    private fun drawingPlacemarkIcon() {
        val resourceMarkIconRed = ImageProvider.fromResource(this, R.drawable.location_place_red)

        for (placemark in PLACEMARKS_LOCATIONS) {
            drawLocationMark(placemark, resourceMarkIconRed)
        }
    }

    private fun drawLocationMark(placemark: Placemark, resourceMarkIcon: ImageProvider) {
        val viewPlacemark = mapObjects.addPlacemark(placemark.point)

        viewPlacemark.setIcon(
            resourceMarkIcon,
            IconStyle()
                .setAnchor(PointF(0.55f, 1f))
                .setRotationType(RotationType.NO_ROTATION)
                .setZIndex(0f)
                .setScale(1f)
        )
        placemarkIcons.add(viewPlacemark)
    }

    private fun drawingPlacemarkCard() {
        var viewPlacemark: PlacemarkMapObject?
        val textView = TextView(this)
        val imageView = ImageView(this)
        val linearLayout = LinearLayout(this)
        val cardView = CardView(this)
        val constraintLayout = LinearLayout(this)

        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.maxWidth = 300
        textView.maxLines = 3
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.setTextColor(resources.getColor(R.color.dark_gray))
        textView.setPadding(20, 10, 20, 10)

        imageView.layoutParams = ViewGroup.LayoutParams(
            170,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        linearLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL

        cardView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardView.elevation = 10f
        cardView.useCompatPadding = true
        cardView.preventCornerOverlap = false
        cardView.radius = 20f
        cardView.setCardBackgroundColor(Color.argb(255, 255, 255, 255))

        constraintLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.setPadding(530, 0, 0, 60)

        for (placemark in PLACEMARKS_LOCATIONS) {
            textView.text = placemark.title
            imageView.setImageResource(placemark.image)

            linearLayout.removeAllViews()
            linearLayout.addView(imageView)
            linearLayout.addView(textView)

            cardView.removeAllViews()
            cardView.addView(linearLayout)

            constraintLayout.removeAllViews()
            constraintLayout.addView(cardView)

            viewPlacemark = mapObjects.addPlacemark(placemark.point, ViewProvider(constraintLayout))
            placemarkCards.add(viewPlacemark)
        }
    }

    @SuppressLint("InflateParams")
    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_fragment, null)
        val dialog = Dialog(this)
        // val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogtheme)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.show()

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    fun onClickListPlaces(view: View) {
        showDialog()
    }

    fun onClickStartExcursion(view: View) {
        if (!excursionStartEnabled) {
            binding.startExcursion.visibility = View.INVISIBLE
            binding.soundAction.visibility = View.VISIBLE
            binding.closeExcursion.setImageDrawable(getDrawable(R.drawable.stop))

            binding.mapview.map.move(
                CameraPosition(PLACEMARKS_LOCATIONS[0].point, 20.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )

            statusStart = true

            if (!this::mediaPlayer.isInitialized) {
                startAudio(R.raw.guide_r2_1)
                mediaPlayer.setOnCompletionListener {
                    binding.soundAction.visibility = View.INVISIBLE
                }

                binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser)
                            mediaPlayer.seekTo(progress)
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }

                })
            }
        }
        else
            Toast.makeText(this, NOTIFICATION_CONDITIONS_START_TOUR, Toast.LENGTH_SHORT).show()
    }

    fun onCLickPauseExcursion(view: View) {
        if (!statusPause) {
            binding.pauseExcursion.setImageDrawable(getDrawable(R.drawable.play))
            statusPause = true

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
        else {
            binding.pauseExcursion.setImageDrawable(getDrawable(R.drawable.pause))
            statusPause = false

            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    fun onCLickStopExcursion(view: View) {
        getActivity()?.onBackPressed()
        //finish()
    }

    fun onClickLocationMe(view: View) {
        if (permissionLocation) {
            cameraUserPosition()
            followUserLocation = true
        }
        else
            checkPermission()
    }

    fun onClickZoomIn(view: View) {
        binding.mapview.map.move(
            CameraPosition(
                binding.mapview.map.cameraPosition.target,
                binding.mapview.map.cameraPosition.zoom + 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
    }

    fun onClickZoomOut(view: View) {
        binding.mapview.map.move(
            CameraPosition(
                binding.mapview.map.cameraPosition.target,
                binding.mapview.map.cameraPosition.zoom - 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()
        val resourceUserIcon = ImageProvider.fromResource(this, R.drawable.location_user)

        userLocationView.pin.setIcon(
            resourceUserIcon,
            IconStyle()
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        userLocationView.arrow.setIcon(
            resourceUserIcon,
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
        val locationUser = Point(
            userLocationLayer.cameraPosition()!!.target.latitude,
            userLocationLayer.cameraPosition()!!.target.longitude
        )

        if (!statusStart)
            checkStartPosition(locationUser)

        else if (statusStart && !statusPause)
            detectGPS(locationUser)
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        val strokeColor = Color.argb(255, 92, 163, 255)

        for (route in routes) {
            val polyline = mapObjects.addPolyline(route.geometry)
            polyline.setStrokeColor(strokeColor)
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

        if (cameraPosition.zoom < 18) {
            for (placemark in placemarkCards)
                placemark.isVisible = false
        }
        else {
            for (placemark in placemarkCards)
                placemark.isVisible = true
        }

        if (cameraPosition.zoom < 14.5) {
            //mapObjects?.isVisible = false
            for (placemark in placemarkIcons)
                placemark.isVisible = false
        }
        else {
            //mapObjects?.isVisible = true
            for (placemark in placemarkIcons)
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

    private fun checkStartPosition(locationUser: Point) {
        val statusContains = containsPointArea(PLACEMARKS_LOCATIONS[0].point, locationUser)
        excursionStartEnabled = statusContains
    }

    private fun detectGPS(locationUser: Point) {
        var statusContains = false

        for (placemark in PLACEMARKS_LOCATIONS) {
            statusContains = containsPointArea(placemark.point, locationUser)
            if (statusContains) {
                Log.d(TAG, "${placemark.id}: $statusContains")
                // show bottom sheet dialog
            }
        }

        for (waypoint in WAYPOINTS_LOCATIONS) {
            statusContains = containsPointArea(waypoint.point, locationUser)
            if (statusContains) {
                if (waypoint.audio != null) {
                    mediaPlayer.setOnCompletionListener {
                        startAudio(waypoint.audio)
                    }
                }
                else {
                    // Очистка пройденного марщрута
                }
            }
        }

        if (!mediaPlayer.isPlaying)
            binding.soundAction.visibility = View.INVISIBLE

        // Проверка на отдаление от маршрута
//        if (!statusContains) {
//            val nearestPlacemark = getNearestPlacemark(locationUser)
//        }
    }

    private fun containsPointArea(area: Point, point: Point): Boolean {
        val maxDistance = 7
        val distance = getDistanceBetweenPoints(area, point)

        if (distance <= maxDistance)
            return true
        return false
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

        val distance = 2 * r * kotlin.math.asin(
            kotlin.math.sqrt(
                kotlin.math.sin((pointRadian.latitude - areaRadian.latitude) / 2).pow(2.0)
                    + kotlin.math.cos(areaRadian.latitude) * kotlin.math.cos(pointRadian.latitude)
                    * kotlin.math.sin((pointRadian.longitude - areaRadian.longitude) / 2).pow(2.0)
            )
        )

        return distance * 1000
    }

    private fun getNearestPlacemark(locationUser: Point): Placemark? {
        var distanceMin: Double? = null
        var distanceNow: Double
        var nearestPlacemark: Placemark? = null

        for (placemark in PLACEMARKS_LOCATIONS) {
            distanceNow = getDistanceBetweenPoints(placemark.point, locationUser)

            if (placemark == PLACEMARKS_LOCATIONS[0])
                distanceMin = distanceNow
            else {
                if (distanceNow < distanceMin!!) {
                    distanceMin = distanceNow
                    nearestPlacemark = placemark
                }
            }
        }

        return nearestPlacemark
    }

    private fun startAudio(audio: Int) {
        mediaPlayer = MediaPlayer.create(this, audio)
        initialiseSeekBar()
        mediaPlayer.start()
    }

    private fun initialiseSeekBar() {
        binding.seekBar.max = mediaPlayer.duration

        val handler = Handler()
        handler.postDelayed(object: Runnable {
            override fun run() {
                try {
                    binding.seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (e: java.lang.Exception) {
                    binding.seekBar.progress = 0
                }
            }
        }, 0)
    }
}