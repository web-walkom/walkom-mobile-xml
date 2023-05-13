package ru.walkom.app.presentation.screens.camera

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.databinding.FragmentCameraArBinding


class CameraARFragment : Fragment() {

    private lateinit var binding: FragmentCameraArBinding
    private var statusCreateAnchor = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraArBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(APP_ACTIVITY.window, false)

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

        binding.closeCameraAR.setOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }

        binding.sceneView.apply {
            geospatialEnabled = true
            onArSessionFailed = { e: Exception ->
                Log.e(Constants.TAG, e.message.toString())
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

                if (earth?.trackingState == TrackingState.TRACKING && !statusCreateAnchor) {
                    val cameraGeospatialPose = earth.cameraGeospatialPose

                    val earthAnchor = earth.createAnchor(
                        58.037158, 56.124979,
                        cameraGeospatialPose.altitude - 1.5,
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
}