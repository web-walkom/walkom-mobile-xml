package ru.walkom.app.presentation.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
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
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.R
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_START_POINT
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_WAYPOINT
import ru.walkom.app.common.Constants.ERROR_DRAW_ROUTE
import ru.walkom.app.common.Constants.NOTIFICATION_CONDITIONS_START_TOUR
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentMapBinding
import ru.walkom.app.domain.model.PlacemarkInfoDialog
import ru.walkom.app.domain.model.Response


@AndroidEntryPoint
class MapFragment : Fragment(), UserLocationObjectListener, Session.RouteListener,
    CameraListener {

    private val args: MapFragmentArgs by navArgs()
    private val viewModel: MapViewModel by viewModels()
    private lateinit var binding: FragmentMapBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapKitFactory.initialize(APP_ACTIVITY)

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
        super.onDestroy()
        viewModel.audioPlayer.release()
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
            findNavController().popBackStack()
        }

        binding.listPlaces.setOnClickListener {
            findNavController().navigate(R.id.navigateToRouteTourFragment)
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
                        return@observe
                    }
                    is Response.Error -> {
                        Log.e(TAG, state.message)
                        return@observe
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

    private fun showInformationAboutPlacemark(placemark: PlacemarkInfoDialog) {
        val action = MapFragmentDirections.navigateToInfoPlacemarkFragment(placemark)
        findNavController().navigate(action)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onClickStartExcursion() {
        if (viewModel.statusBeingStartPoint) {
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
                    20.0f, 0.0f, 50.0f
                ),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )

            viewModel.audioPlayer.init()
            viewModel.audioPlayer.play(
                viewModel.waypointsLocations[0].audio,
                args.excursionId,
                { initialiseSeekBar() }
            ) {
                viewModel.statusStartExcursion = true
                binding.soundAction.visibility = View.INVISIBLE
            }

            if (viewModel.waypointsLocations.size > 2)
                viewModel.indexWaypointEnd = 2
        }
        else
            Toast.makeText(APP_ACTIVITY, NOTIFICATION_CONDITIONS_START_TOUR, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onClickPauseExcursion() {
        if (!viewModel.statusPauseExcursion) {
            binding.pauseExcursion.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_play))
            viewModel.statusPauseExcursion = true

            if (viewModel.audioPlayer.isPlaying())
                viewModel.audioPlayer.pause()
        }
        else {
            binding.pauseExcursion.setImageDrawable(APP_ACTIVITY.getDrawable(R.drawable.ic_pause))
            viewModel.statusPauseExcursion = false

            if (!viewModel.audioPlayer.isPlaying())
                viewModel.audioPlayer.start()
        }
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
                DISTANCE_CONTAINS_START_POINT
            )
            viewModel.statusBeingStartPoint = statusContains
        }
        else if (viewModel.statusStartExcursion && !viewModel.statusPauseExcursion)
            detectGPS(locationUser)
    }

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        viewModel.masstransitRoutes(routes)
    }

    override fun onMasstransitRoutesError(error: Error) {
        Toast.makeText(APP_ACTIVITY, ERROR_DRAW_ROUTE, Toast.LENGTH_SHORT).show()
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

        for (waypoint in viewModel.waypointsLocations.slice(viewModel.indexWaypointStart..viewModel.indexWaypointEnd)) {
            statusContains = viewModel.containsPointArea(
                Point(waypoint.latitude, waypoint.longitude),
                locationUser,
                DISTANCE_CONTAINS_WAYPOINT
            )

            if (statusContains && !waypoint.isPassed) {
                if (!viewModel.audioPlayer.isPlaying()) {
                    viewModel.audioPlayer.play(
                        waypoint.audio,
                        args.excursionId,
                        { initialiseSeekBar() }
                    ) {
                        binding.soundAction.visibility = View.INVISIBLE

                        if (viewModel.waypointsLocations.size - 1 != viewModel.indexWaypointEnd) {
                            viewModel.indexWaypointStart++
                            viewModel.indexWaypointEnd++
                        }
                        else {
                            // action to finish excursion
                        }
                    }
                }

                val placemark = PlacemarkInfoDialog("Название метки", emptyList())
                showInformationAboutPlacemark(placemark)
                waypoint.isPassed = true
                break
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

    private fun initialiseSeekBar() {
        binding.soundAction.visibility = View.VISIBLE
        binding.seekBar.max = viewModel.audioPlayer.getDuration()

        val handler = Handler()
        handler.postDelayed(object: Runnable {
            override fun run() {
                try {
                    binding.seekBar.progress = viewModel.audioPlayer.getCurrentPosition()
                    handler.postDelayed(this, 1000)
                } catch (e: java.lang.Exception) {
                    binding.seekBar.progress = 0
                }
            }
        }, 0)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser)
                    viewModel.audioPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }
}