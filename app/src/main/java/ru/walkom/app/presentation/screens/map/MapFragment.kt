package ru.walkom.app.presentation.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import ru.walkom.app.R
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentMapBinding
import ru.walkom.app.domain.model.Response


class MapFragment : Fragment(), UserLocationObjectListener, Session.RouteListener,
    CameraListener {

    private val viewModel: MapViewModel by activityViewModels()
    private lateinit var binding: FragmentMapBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MapKitFactory.initialize(APP_ACTIVITY)
        setWindowFlag()

        checkLocationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                getRouteHandler()
            }
        }

        checkPermission()
        clickHandler()
    }

    override fun onStart() {
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()

        viewModel.placemarkIcons.clear()
        viewModel.placemarkCards.clear()
        viewModel.waypointIcons.clear()
        viewModel.polylines = null

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
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        APP_ACTIVITY.supportActionBar?.elevation = 0f
        APP_ACTIVITY.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        APP_ACTIVITY.window.statusBarColor = Color.TRANSPARENT
        APP_ACTIVITY.window.navigationBarColor = Color.TRANSPARENT
        APP_ACTIVITY.window.attributes.flags = APP_ACTIVITY.window.attributes.flags and
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv() and
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION.inv()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                APP_ACTIVITY,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                APP_ACTIVITY,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            getRouteHandler()
        else
            checkLocationPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
    }

    private fun clickHandler() {
        binding.closeExcursion.setOnClickListener {
            onClickStopExcursion()
        }

        binding.listPlaces.setOnClickListener {
            onClickListPlaces()
        }

        binding.zoomIn.setOnClickListener {
            onClickZoomIn()
        }

        binding.zoomOut.setOnClickListener {
            onClickZoomOut()
        }

        binding.location.setOnClickListener {
            onClickLocationMe()
        }

        binding.startExcursion.setOnClickListener {
            onClickStartExcursion()
        }

        binding.pauseExcursion.setOnClickListener {
            onClickPauseExcursion()
        }
    }

    private fun getRouteHandler() {
        viewModel.stateRoute.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.i(TAG, "Loading")
                    }
                    is Response.Success -> {
                        viewModel.placemarksLocations = state.data?.placemarks ?: listOf()
                        viewModel.waypointsLocations = state.data?.waypoints ?: listOf()
                        onMapReady()
                        return@let
                    }
                    is Response.Error -> {
                        Log.e(TAG, state.message)
                    }
                }
            }
        }
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()

        binding.titleTable.text = Constants.TEXT_START_EXCURSION
        binding.descriptionTable.text = Constants.DESCRIPTION_START_EXCURSION

        viewModel.userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        viewModel.userLocationLayer.isVisible = true
        viewModel.userLocationLayer.isHeadingEnabled = true
        viewModel.userLocationLayer.isAutoZoomEnabled = true
        viewModel.userLocationLayer.setObjectListener(this)

        binding.mapview.map.addCameraListener(this)
        initialApproximation()
        cameraUserPosition()
        viewModel.permissionLocation = true

        viewModel.mapObjects = binding.mapview.map.mapObjects.addCollection()
        drawingRoute()
    }

    private fun initialApproximation() {
        binding.mapview.map.move(
            CameraPosition(
                Point(
                    viewModel.placemarksLocations[0].latitude,
                    viewModel.placemarksLocations[0].longitude
                ),
                16.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.3f),
            null
        )
    }

    private fun cameraUserPosition() {
        if (viewModel.userLocationLayer.cameraPosition() != null) {
            binding.mapview.map.move(
                CameraPosition(
                    viewModel.userLocationLayer.cameraPosition()!!.target,
                    18.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun drawingRoute() {
        val resourceWaypointIcon = ImageProvider.fromResource(APP_ACTIVITY, R.drawable.waypoint)
        val resourcePlacemarkIcon = ImageProvider.fromResource(APP_ACTIVITY, R.drawable.placemark)
        val requestPoints = viewModel.buildingRoute(resourceWaypointIcon)

        viewModel.drawingPlacemarkIcon(resourcePlacemarkIcon)
        drawingStartPlacemark()
        drawingPlacemarkCard()

        viewModel.transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        viewModel.transportRouter.requestRoutes(requestPoints, TimeOptions(), this)
    }

    private fun drawingStartPlacemark() {
        val textView = TextView(APP_ACTIVITY)
        val cardView = CardView(APP_ACTIVITY)
        val constraintLayout = LinearLayout(APP_ACTIVITY)

        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setTextColor(resources.getColor(R.color.dark_gray))
        textView.setPadding(20, 10, 20, 10)
        textView.text = Constants.TEXT_START

        cardView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardView.radius = 10f
        cardView.setCardBackgroundColor(Color.argb(255, 255, 255, 255))
        cardView.addView(textView)

        constraintLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.setPadding(0, 0, 0, 200)
        constraintLayout.addView(cardView)

        viewModel.placemarkStart = viewModel.mapObjects.addPlacemark(
            Point(
                viewModel.placemarksLocations[0].latitude,
                viewModel.placemarksLocations[0].longitude
            ),
            ViewProvider(constraintLayout)
        )
    }

    private fun drawingPlacemarkCard() {
        var viewPlacemark: PlacemarkMapObject?
        val textView = TextView(APP_ACTIVITY)
        val imageView = ImageView(APP_ACTIVITY)
        val linearLayout = LinearLayout(APP_ACTIVITY)
        val cardView = CardView(APP_ACTIVITY)
        val constraintLayout = LinearLayout(APP_ACTIVITY)

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

        for (placemark in viewModel.placemarksLocations) {
            textView.text = placemark.title
//            imageView.setImageResource(placemark.photos[0])
            imageView.load(placemark.photos[0])

            linearLayout.removeAllViews()
            linearLayout.addView(imageView)
            linearLayout.addView(textView)

            cardView.removeAllViews()
            cardView.addView(linearLayout)

            constraintLayout.removeAllViews()
            constraintLayout.addView(cardView)

            viewPlacemark = viewModel.mapObjects.addPlacemark(
                Point(placemark.latitude, placemark.longitude),
                ViewProvider(constraintLayout)
            )
            viewModel.placemarkCards.add(viewPlacemark)
        }
    }

    private fun showInformationAboutPlacemark() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_info_placemark, null)
        val dialog = BottomSheetDialog(APP_ACTIVITY, R.style.BottomSheetDialogTheme)

        showBottomSheet(dialogView, dialog)
    }

    private fun onClickListPlaces() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_tour_route, null)
        val dialog = BottomSheetDialog(APP_ACTIVITY, R.style.BottomSheetDialogTheme)

        showBottomSheet(dialogView, dialog)
    }

    private fun showBottomSheet(dialogView: View, dialog: BottomSheetDialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.show()

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onClickStartExcursion() {
        if (!viewModel.statusBeingStartPoint) {
            binding.startExcursion.visibility = View.INVISIBLE
            binding.soundAction.visibility = View.VISIBLE
            binding.closeExcursion.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_stop))

            binding.titleTable.text = Constants.TEXT_INTRODUCTION_EXCURSION
            binding.descriptionTable.text = Constants.DESCRIPTION_INTRODUCTION_EXCURSION

            viewModel.placemarkStart.isVisible = false

            binding.mapview.map.move(
                CameraPosition(
                    Point(
                        viewModel.placemarksLocations[0].latitude,
                        viewModel.placemarksLocations[0].longitude
                    ),
                    20.0f, 0.0f, 70.0f
                ),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )

            if (!this::mediaPlayer.isInitialized) {
//                startAudio(viewModel.startPointAudio)
//                mediaPlayer.setOnCompletionListener {
//                    viewModel.statusStartExcursion = true
//                    binding.soundAction.visibility = View.INVISIBLE
//                    mediaPlayer.stop()
//                    mediaPlayer.reset()
//                }

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
            Toast.makeText(APP_ACTIVITY, Constants.NOTIFICATION_CONDITIONS_START_TOUR, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onClickPauseExcursion() {
        if (!viewModel.statusPause) {
            binding.pauseExcursion.setImageDrawable(Constants.APP_ACTIVITY.getDrawable(R.drawable.ic_play))
            viewModel.statusPause = true

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
        else {
            binding.pauseExcursion.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_pause))
            viewModel.statusPause = false

            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    private fun onClickStopExcursion() {
        APP_ACTIVITY.supportFragmentManager.popBackStack()
    }

    private fun onClickLocationMe() {
        if (viewModel.permissionLocation) {
            cameraUserPosition()
            viewModel.followUserLocation = true
        }
        else
            checkPermission()
    }

    private fun onClickZoomIn() {
        binding.mapview.map.move(
            CameraPosition(
                binding.mapview.map.cameraPosition.target,
                binding.mapview.map.cameraPosition.zoom + 1, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
    }

    private fun onClickZoomOut() {
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
        viewModel.setAnchor(binding.mapview.width(), binding.mapview.height())
        val resourceUserIcon = ImageProvider.fromResource(APP_ACTIVITY, R.drawable.location_user)

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
            viewModel.userLocationLayer.cameraPosition()!!.target.latitude,
            viewModel.userLocationLayer.cameraPosition()!!.target.longitude
        )

        if (!viewModel.statusStartExcursion) {
            val statusContains = viewModel.containsPointArea(
                Point(
                    viewModel.placemarksLocations[0].latitude,
                    viewModel.placemarksLocations[0].longitude
                ),
                locationUser,
                Constants.DISTANCE_CONTAINS_START_POINT
            )
            viewModel.statusBeingStartPoint = statusContains
        }
        else if (viewModel.statusStartExcursion && !viewModel.statusPause)
            detectGPS(locationUser)
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        viewModel.masstransitRoutes(routes)
    }

    override fun onMasstransitRoutesError(error: Error) {
        Toast.makeText(APP_ACTIVITY, Constants.ERROR_DRAW_ROUTE, Toast.LENGTH_SHORT).show()
        Log.e(TAG, error.toString())
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            if (viewModel.followUserLocation)
                viewModel.setAnchor(binding.mapview.width(), binding.mapview.height())
        }
        else {
            if (!viewModel.followUserLocation)
                viewModel.userLocationLayer.resetAnchor()
        }

        viewModel.cameraPositionChanged(cameraPosition)
    }

    private fun detectGPS(locationUser: Point) {
        var statusContains = false

        for (waypoint in viewModel.waypointsLocations) {
            // Проверка на подход пользователя к точке маршрута экскурсии
            statusContains = viewModel.containsPointArea(
                Point(waypoint.latitude, waypoint.longitude),
                locationUser,
                Constants.DISTANCE_CONTAINS_WAYPOINT
            )

            if (statusContains && !waypoint.isPassed) {
//                if (waypoint.audio != null && !mediaPlayer.isPlaying)
//                    startAudio(waypoint.audio)

//                if (waypoint.affiliationPlacemarkId != null)
                showInformationAboutPlacemark()

                waypoint.isPassed = true
            }
        }

        if (!statusContains) {
            // Проверка на отдаление от маршрута
            val distance = viewModel.getDistanceNearestWaypoint(locationUser)

            if (distance > Constants.DISTANCE_CONTAINS_ROUTE && distance <= Constants.DISTANCE_CONTAINS_ROUTE_EXTREME)
                Toast.makeText(APP_ACTIVITY, Constants.NOTIFICATION_DEVIATION_ROUTE, Toast.LENGTH_SHORT).show()
            if (distance > Constants.DISTANCE_CONTAINS_ROUTE_EXTREME) {
                Toast.makeText(APP_ACTIVITY, Constants.NOTIFICATION_TERMINATION_DEVIATION_ROUTE, Toast.LENGTH_SHORT).show()
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }
        }
    }

    private fun startAudio(audio: Int) {
        mediaPlayer = MediaPlayer.create(APP_ACTIVITY, audio)
        initialiseSeekBar()
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            binding.soundAction.visibility = View.INVISIBLE
        }
    }

    private fun initialiseSeekBar() {
        binding.soundAction.visibility = View.VISIBLE
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