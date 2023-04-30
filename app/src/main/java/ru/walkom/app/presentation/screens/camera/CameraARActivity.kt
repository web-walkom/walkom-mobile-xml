package ru.walkom.app.presentation.screens.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.utils.TAG
import ru.walkom.app.databinding.ActivityCameraArBinding

class CameraARActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraArBinding
    private lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityCameraArBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sceneView.apply {
            geospatialEnabled = true
            onArSessionFailed = { e: Exception ->
                Log.e(TAG, e.message.toString())
            }
            onArSessionCreated = { arSession ->
                val earth = arSession.earth

                binding.latitude.text = "Latitude: ${earth?.cameraGeospatialPose?.latitude}"
                binding.longitude.text = "Longitude: ${earth?.cameraGeospatialPose?.longitude}"
                binding.altitude.text = "Altitude: ${earth?.cameraGeospatialPose?.altitude}"
                if (earth != null)
                    binding.status.text = "Status earth: true"
                else
                    binding.status.text = "Earth state: false"
                binding.earthState.text = "Tracking state: ${earth?.trackingState}"

                if (earth != null && earth.trackingState == TrackingState.TRACKING) {
                    val cameraGeospatialPose = earth.cameraGeospatialPose
                    val rotation = Rotation(0f, 0f, 0f)
                    val latitude = 58.037092
                    val longitude = 56.125722
                    val earthAnchor = earth.createAnchor(latitude, longitude, cameraGeospatialPose.altitude - 1, rotation.toFloatArray())

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
                        ) {
                            binding.sceneView.planeRenderer.isVisible = true
                        }
                    }

                    arModelNode.anchor = earthAnchor
                    binding.sceneView.addChild(arModelNode)
                }
            }
        }
    }

    fun onClickCloseCameraAR(view: View) {
        finish()
    }
}