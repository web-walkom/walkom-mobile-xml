package ru.walkom.app.presentation.screens.camera

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.ar.core.TrackingState
import dagger.hilt.android.AndroidEntryPoint
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.FOLDER_MODELS
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentCameraArBinding
import ru.walkom.app.domain.model.Response
import java.io.File


@AndroidEntryPoint
class CameraARFragment : Fragment() {

    private val args: CameraARFragmentArgs by navArgs()
    private val viewModel: CameraARViewModel by viewModels()
    private lateinit var binding: FragmentCameraArBinding
    private var statusCreateAnchors = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraArBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(APP_ACTIVITY.window, false)

        binding.closeCameraAR.setOnClickListener {
            findNavController().popBackStack()
        }

        overlayModels()
//        getModelsHandler()
    }

    private fun getModelsHandler() {
        viewModel.stateModels.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.i(TAG, "Loading")
                    }
                    is Response.Success -> {
                        viewModel.models = state.data?.models ?: listOf()
                        overlayModels()
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

    private fun overlayModels() {


        binding.sceneView.apply {
            geospatialEnabled = true
            onArSessionFailed = { e: Exception ->
                Log.e(TAG, e.message.toString())
            }
            onArFrame = {
                val earth = it.session.earth

                binding.latitude.text =
                    "Latitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.latitude)}"
                binding.longitude.text =
                    "Longitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.longitude)}"
                binding.altitude.text = "Altitude: ${earth?.cameraGeospatialPose?.altitude}"
                if (earth != null)
                    binding.earthState.text = "Status earth: ${earth.earthState}"
                else
                    binding.earthState.text = "Earth state: NULL"
                binding.trackingState.text = "Tracking state: ${earth?.trackingState}"

                if (earth?.trackingState == TrackingState.TRACKING && !statusCreateAnchors) {
                    val cameraGeospatialPose = earth.cameraGeospatialPose

                    val arModelNode = ArModelNode(
                        placementMode = PlacementMode.BEST_AVAILABLE,
                        hitPosition = Position(0.0f, 0.0f, -2.0f),
                        followHitPosition = false,
                        instantAnchor = true
                    ).apply {
                        loadModelGlbAsync(
//                glbFileLocation = "${APP_ACTIVITY.filesDir}/${args.excursionId}/$FOLDER_MODELS/diligense.glb",
                            glbFileLocation = "models/diligense.glb",
                            autoAnimate = true,
                            scaleToUnits = 1f,
                            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                        )
                    }

                    val earthAnchor = earth.createAnchor(
                        58.037186, 56.124991,
                        cameraGeospatialPose.altitude - 1.5,
                        0f, 0f, 0f, 1f
                    )

                    arModelNode.anchor = earthAnchor
                    binding.sceneView.addChild(arModelNode)

//                    for (model in viewModel.models.slice(0..5)) {
//                        val earthAnchor = earth.createAnchor(
//                            model.point.latitude, model.point.longitude,
//                            cameraGeospatialPose.altitude - 1.5,
//                            0f, 0f, 0f, 1f
//                        )
//
//                        arModelNode.anchor = earthAnchor
//                        binding.sceneView.addChild(arModelNode)
//                    }

//                    for (model in viewModel.models) {
//                        val arModelNode = ArModelNode(
//                            placementMode = PlacementMode.BEST_AVAILABLE,
//                            hitPosition = Position(0.0f, 0.0f, -2.0f),
//                            followHitPosition = false,
//                            instantAnchor = true
//                        ).apply {
//                            loadModelGlbAsync(
//                                glbFileLocation = "${APP_ACTIVITY.filesDir}/${args.excursionId}/$FOLDER_MODELS/${model.model}",
//                                autoAnimate = true,
//                                scaleToUnits = 1f,
//                                centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
//                            )
//                        }
//
//                        val earthAnchor = earth.createAnchor(
//                            model.point.latitude, model.point.longitude,
//                            cameraGeospatialPose.altitude - 1.5,
//                            0f, 0f, 0f, 1f
//                        )
//
//                        arModelNode.anchor = earthAnchor
//                        binding.sceneView.addChild(arModelNode)
//                    }

                    statusCreateAnchors = true
                }
            }
            onArSessionCreated = { arSession ->
            }
        }
    }
}