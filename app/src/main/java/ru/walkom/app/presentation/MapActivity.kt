package ru.walkom.app.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Dialog
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityMapsBinding
import ru.walkom.app.domain.model.Placemark

class MapActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener, CameraListener {

    private val PLACEMARKS_LOCATIONS = listOf<Placemark>(
        Placemark(
            Point(58.010418, 56.237335),
            "Пермский медведь",
            R.drawable.bear
        ),
        Placemark(
            Point(58.011385, 56.239513),
            "Губернский детский приют",
            R.drawable.provincial_children_shelter
        ),
        Placemark(
            Point(58.012248, 56.242676),
            "Рождественско-Богородицкая церковь",
            R.drawable.church
        ),
        Placemark(
            Point(58.013174, 56.243045),
            "Пермский государственный институт культуры",
            R.drawable.perm_state_institute_culture
        ),
        Placemark(
            Point(58.012553, 56.243556),
            "Дом пекарня наследника Демидовых",
            R.drawable.house_demidovs
        ),
        Placemark(
            Point(58.012758, 56.244125),
            "Триумф. Пермский кинотеатр",
            R.drawable.triumph
        ),
        /*
        Placemark(
            Point(58.012607, 56.244302),
            "Старокирпичный переулок"
        ),
        Placemark(
            Point(58.013015, 56.243879),
            "Усадьба купчихи М.Т. Киселёвой"
        ),
        Placemark(
            Point(58.012810, 56.244517),
            "Аптеки Боне и Бартминского"
        ),
        Placemark(
            Point(58.012867, 56.244769),
            "Дом присяжного поверенного Падалка"
        ),
        Placemark(
            Point(58.013235, 56.244661),
            "Доходно-деловой дом купца А.Г. Каменского"
        ),
        Placemark(
            Point(58.013006, 56.245146),
            "Дом купцов Степановых"
        ),
        Placemark(
            Point(58.013230, 56.245909),
            "Дом купцов Базановых"
        ),
        Placemark(
            Point(58.013506, 56.246934),
            "Книжный магазин Пиотровских"
        ),
        Placemark(
            Point(58.015961, 56.245820),
            "Пермский оперный театр"
        ),
        Placemark(
            Point(58.015318, 56.248883),
            "Дом Тупицыных"
        ),
        Placemark(
            Point(58.016081, 56.247895),
            "Пермский государственный аграрно-технологический университет"
        ),
        Placemark(
            Point(58.017358, 56.245424),
            "Европейские номера"
        ),
        Placemark(
            Point(58.018312, 56.244831),
            "Дом купца П.А. Попова"
        ),
        Placemark(
            Point(58.018464, 56.245577),
            "Узел связи Камского речного пароходства"
        ),
        Placemark(
            Point(58.018745, 56.246547),
            "Дом Мешкова"
        ),
        Placemark(
            Point(58.017954, 56.244041),
            "Дом со львами"
        ),
        Placemark(
            Point(58.017773, 56.243241),
            "Дом В.Е. Вердеревского, председателя казенной палаты"
        ),
        Placemark(
            Point(58.017330, 56.241714),
            "Торговая Баня мещанки Кашиной"
        ),
        Placemark(
            Point(58.017911, 56.240654),
            "Дома купцов Чердынцевых"
        ),
        Placemark(
            Point(58.017292, 56.239172),
            "Духовная консистория"
        ),
        Placemark(
            Point(58.016414, 56.234689),
            "Спасопреображенский кафедральный собор и архиерейский дом"
        ),
        Placemark(
            Point(58.016927, 56.233439), "Смотровая площадка"),
        Placemark(
            Point(58.016668, 56.231868), "Соль земли Пермской"),
        */
    )

    private val WAYPOINTS_LOCATIONS = listOf<Point>(
        Point(58.010433, 56.237325),
        Point(58.011458, 56.239391),
        Point(58.012153, 56.241898),
        Point(58.012626, 56.243570),
        Point(58.012757, 56.244027),
        /*
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
         */
    )

    private val PREVIEW_LOCATION = Point(58.011757, 56.240897)

    private var placemarkIcons = ArrayList<PlacemarkMapObject>()
    private var placemarkTexts = ArrayList<PlacemarkMapObject>()
    private var polylines = ArrayList<PolylineMapObject>()

    private lateinit var binding: ActivityMapsBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    lateinit var userLocationLayer: UserLocationLayer
    private var mapObjects: MapObjectCollection? = null
    private var transportRouter: PedestrianRouter? = null

    private var permissionLocation = false
    private var followUserLocation = false

    private var statusStart = false
    private var statusPause = false

    private lateinit var mediaPlayer: MediaPlayer

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

    private fun drawLocationMark(placemark: Placemark, resourceMarkIcon: ImageProvider) {
        val viewPlacemark = mapObjects?.addPlacemark(placemark.point)

        viewPlacemark?.setIcon(
            resourceMarkIcon,
            IconStyle()
                .setAnchor(PointF(0.55f, 1f))
                .setRotationType(RotationType.NO_ROTATION)
                .setZIndex(0f)
                .setScale(1f)
        )

        if (viewPlacemark != null)
            placemarkIcons.add(viewPlacemark)
    }

    private fun drawingRoute() {
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        for (waypoint in WAYPOINTS_LOCATIONS)
            requestPoints.add(RequestPoint(waypoint, RequestPointType.WAYPOINT, null))

        drawingPlacemarkIcon()
        drawingPlacemarkText()

        transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        transportRouter!!.requestRoutes(requestPoints, TimeOptions(), this)
    }

    private fun drawingPlacemarkIcon() {
        val resourceMarkIcon = ImageProvider.fromResource(this, R.drawable.location_place)

        for (placemark in PLACEMARKS_LOCATIONS)
            drawLocationMark(placemark, resourceMarkIcon)
    }

    private fun drawingPlacemarkText() {
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
        constraintLayout
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

            viewPlacemark = mapObjects?.addPlacemark(placemark.point, ViewProvider(constraintLayout))
            if (viewPlacemark != null)
                placemarkTexts.add(viewPlacemark)
        }
    }

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
        binding.startExcursion.visibility = View.INVISIBLE
        binding.pauseExcursion.visibility = View.VISIBLE
        binding.closeExcursion.setImageDrawable(getDrawable(R.drawable.stop))

        binding.mapview.map.move(
            CameraPosition(PLACEMARKS_LOCATIONS[0].point, 20.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )

        statusStart = true

        if (!this::mediaPlayer.isInitialized) {
            mediaPlayer = MediaPlayer.create(this, R.raw.music_1)
        }

        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
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
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        val strokeColor = Color.argb(255, 92, 163, 255)

        for (route in routes) {
            val polyline = mapObjects!!.addPolyline(route.geometry)
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
            for (placemark in placemarkTexts)
                placemark.isVisible = false
        }
        else {
            for (placemark in placemarkTexts)
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
}