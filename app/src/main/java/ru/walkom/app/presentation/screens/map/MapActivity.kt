package ru.walkom.app.presentation.screens.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gorisse.thomas.lifecycle.getActivity
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.R
import ru.walkom.app.common.Constants.DESCRIPTION_INTRODUCTION_EXCURSION
import ru.walkom.app.common.Constants.DESCRIPTION_START_EXCURSION
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_ROUTE
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_ROUTE_EXTREME
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_START_POINT
import ru.walkom.app.common.Constants.DISTANCE_CONTAINS_WAYPOINT
import ru.walkom.app.common.Constants.ERROR_DRAW_ROUTE
import ru.walkom.app.common.Constants.NOTIFICATION_CONDITIONS_START_TOUR
import ru.walkom.app.common.Constants.NOTIFICATION_DEVIATION_ROUTE
import ru.walkom.app.common.Constants.NOTIFICATION_TERMINATION_DEVIATION_ROUTE
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.common.Constants.TEXT_INTRODUCTION_EXCURSION
import ru.walkom.app.common.Constants.TEXT_START
import ru.walkom.app.common.Constants.TEXT_START_EXCURSION
import ru.walkom.app.databinding.ActivityMapsBinding
import ru.walkom.app.presentation.screens.camera.CameraARActivity
import java.lang.Math.*

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), UserLocationObjectListener, Session.RouteListener, CameraListener {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var binding: ActivityMapsBinding
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
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
        window.attributes.flags = window.attributes.flags and
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv() and
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION.inv()
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

        binding.titleTable.text = TEXT_START_EXCURSION
        binding.descriptionTable.text = DESCRIPTION_START_EXCURSION

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
                viewModel.placemarksLocations[0].point,
                15.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 1f),
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
        val resourceWaypointIcon = ImageProvider.fromResource(this, R.drawable.waypoint)
        val resourcePlacemarkIcon = ImageProvider.fromResource(this, R.drawable.placemark)
        val requestPoints = viewModel.buildingRoute(resourceWaypointIcon)

        viewModel.drawingPlacemarkIcon(resourcePlacemarkIcon)
        drawingStartPlacemark()
        drawingPlacemarkCard()

        viewModel.transportRouter = TransportFactory.getInstance().createPedestrianRouter()
        viewModel.transportRouter.requestRoutes(requestPoints, TimeOptions(), this)
    }

    private fun drawingStartPlacemark() {
        val textView = TextView(this)
        val cardView = CardView(this)
        val constraintLayout = LinearLayout(this)

        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setTextColor(resources.getColor(R.color.dark_gray))
        textView.setPadding(20, 10, 20, 10)
        textView.text = TEXT_START

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
            viewModel.placemarksLocations[0].point,
            ViewProvider(constraintLayout)
        )
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

        for (placemark in viewModel.placemarksLocations) {
            textView.text = placemark.title
            imageView.setImageResource(placemark.image)

            linearLayout.removeAllViews()
            linearLayout.addView(imageView)
            linearLayout.addView(textView)

            cardView.removeAllViews()
            cardView.addView(linearLayout)

            constraintLayout.removeAllViews()
            constraintLayout.addView(cardView)

            viewPlacemark = viewModel.mapObjects.addPlacemark(placemark.point, ViewProvider(constraintLayout))
            viewModel.placemarkCards.add(viewPlacemark)
        }
    }

    private fun showInformationAboutPlacemark() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_info_placemark, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        showBottomSheet(dialogView, dialog)
    }

    fun onClickListPlaces(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_tour_route, null)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
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
    fun onClickStartExcursion(view: View) {
        if (viewModel.statusBeingStartPoint) {
            binding.startExcursion.visibility = View.INVISIBLE
            binding.soundAction.visibility = View.VISIBLE
            binding.closeExcursion.setImageDrawable(getDrawable(R.drawable.ic_stop))

            binding.titleTable.text = TEXT_INTRODUCTION_EXCURSION
            binding.descriptionTable.text = DESCRIPTION_INTRODUCTION_EXCURSION

            viewModel.placemarkStart.isVisible = false

            binding.mapview.map.move(
                CameraPosition(
                    viewModel.placemarksLocations[0].point,
                    20.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )

            if (!this::mediaPlayer.isInitialized) {
                startAudio(R.raw.guide_r2_1)
                mediaPlayer.setOnCompletionListener {
                    viewModel.statusStartExcursion = true
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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun onClickPauseExcursion(view: View) {
        if (!viewModel.statusPause) {
            binding.pauseExcursion.setImageDrawable(getDrawable(R.drawable.ic_play))
            viewModel.statusPause = true

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
        else {
            binding.pauseExcursion.setImageDrawable(getDrawable(R.drawable.ic_pause))
            viewModel.statusPause = false

            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    fun onClickStopExcursion(view: View) {
        getActivity()?.onBackPressed()
        //finish()
    }

    fun onClickLocationMe(view: View) {
        if (viewModel.permissionLocation) {
            cameraUserPosition()
            viewModel.followUserLocation = true
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

    fun onClickShowAR(view: View) {
        val intent = Intent(this, CameraARActivity::class.java)
        startActivity(intent)
    }
    
    override fun onObjectAdded(userLocationView: UserLocationView) {
        viewModel.setAnchor(binding.mapview.width(), binding.mapview.height())
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
            viewModel.userLocationLayer.cameraPosition()!!.target.latitude,
            viewModel.userLocationLayer.cameraPosition()!!.target.longitude
        )

        if (!viewModel.statusStartExcursion) {
            val statusContains = viewModel.containsPointArea(
                viewModel.placemarksLocations[0].point,
                locationUser,
                DISTANCE_CONTAINS_START_POINT
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
                waypoint.point,
                locationUser,
                DISTANCE_CONTAINS_WAYPOINT
            )

            if (statusContains && !waypoint.isPassed) {
                if (waypoint.audio != null && !mediaPlayer.isPlaying)
                    startAudio(waypoint.audio)

                if (waypoint.affiliationPlacemarkId != null)
                    showInformationAboutPlacemark()

                waypoint.isPassed = true
            }
        }

        if (!statusContains) {
            // Проверка на отдаление от маршрута
            val distance = viewModel.getDistanceNearestWaypoint(locationUser)

            if (distance > DISTANCE_CONTAINS_ROUTE && distance <= DISTANCE_CONTAINS_ROUTE_EXTREME) {
                Toast.makeText(this, NOTIFICATION_DEVIATION_ROUTE, Toast.LENGTH_SHORT).show()
            }
            if (distance > DISTANCE_CONTAINS_ROUTE_EXTREME) {
                Toast.makeText(this, NOTIFICATION_TERMINATION_DEVIATION_ROUTE, Toast.LENGTH_SHORT).show()
                getActivity()?.onBackPressed()
            }
        }
    }

    private fun startAudio(audio: Int) {
        mediaPlayer = MediaPlayer.create(this, audio)
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