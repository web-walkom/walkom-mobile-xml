package ru.walkom.app.presentation.screens.camera

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.gorisse.thomas.lifecycle.getActivity
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityCameraArBinding

class CameraARActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraArBinding
    private var statusCreateAnchor = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val arModelNode = ArModelNode(
            placementMode = PlacementMode.BEST_AVAILABLE,
            hitPosition = Position(0.0f, 0.0f, -2.0f),
            followHitPosition = false,
            instantAnchor = true
        ).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/diligense.glb",
                autoAnimate = true,
                scaleToUnits = 1f,
                centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
            )
        }

        binding = ActivityCameraArBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sceneView.apply {
            geospatialEnabled = true
            onArSessionFailed = { e: Exception ->
                Log.e(TAG, e.message.toString())
            }
            onArFrame = {
                val earth = it.session.earth

                binding.latitude.text = "Latitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.latitude)}"
                binding.longitude.text = "Longitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.longitude)}"
                binding.altitude.text = "Altitude: ${earth?.cameraGeospatialPose?.altitude}"
                if (earth != null)
                    binding.earthState.text = "Status earth: ${earth.earthState}"
                else
                    binding.earthState.text = "Earth state: NULL"
                binding.trackingState.text = "Tracking state: ${earth?.trackingState}"
                binding.horizontalAccuracy.text = "Horizontal accuracy: ${earth?.cameraGeospatialPose?.horizontalAccuracy}"
                binding.verticalAccuracy.text = "Vertical accuracy: ${earth?.cameraGeospatialPose?.verticalAccuracy}"

                if (earth?.trackingState == TrackingState.TRACKING && !statusCreateAnchor) {
                    val cameraGeospatialPose = earth.cameraGeospatialPose

                    val earthAnchor = earth.createAnchor(
                        58.037069, 56.125718,
                        cameraGeospatialPose.altitude - 1,
                        0f, 0f, 0f, 1f
                    )

                    arModelNode.anchor = earthAnchor
                    binding.sceneView.addChild(arModelNode)
                    statusCreateAnchor = true
                }
            }
            onArSessionCreated = { arSession ->
            }
        }
    }

    fun onClickCloseCameraAR(view: View) {
        getActivity()?.onBackPressed()
        //finish()
    }
}