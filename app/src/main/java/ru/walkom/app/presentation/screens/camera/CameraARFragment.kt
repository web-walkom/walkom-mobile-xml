package ru.walkom.app.presentation.screens.camera

import android.annotation.SuppressLint
import android.net.Uri
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
import com.yandex.mapkit.geometry.Point
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
        getModelsHandler()

        binding.closeCameraAR.setOnClickListener {
            for (node in viewModel.nodes) {
                node.anchor?.detach()
                node.destroy()
            }

            findNavController().popBackStack()
        }

        binding.test.setOnClickListener {
            manualOverlay()
        }

        binding.testPoint.setOnClickListener {
            overlayModels()
        }
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
//                        binding.actions.visibility = View.VISIBLE
                        overlayModels()
//                        overlayModelTest()
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

    private fun manualOverlay() {
        val arModelNode = ArModelNode(
            placementMode = PlacementMode.BEST_AVAILABLE,
            hitPosition = Position(0.0f, 0.0f, -2.0f),
            followHitPosition = false,
            instantAnchor = true
        ).apply {
            val file = File("${APP_ACTIVITY.filesDir}/${args.excursionId}/$FOLDER_MODELS/${viewModel.models[viewModel.indexModelEnd].model}")
            loadModelGlbAsync(
                glbFileLocation = Uri.fromFile(file).toString(),
                autoAnimate = true,
                scaleToUnits = 1.5f,
                centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
            )
        }

        binding.sceneView.addChild(arModelNode)
        viewModel.nodes.add(arModelNode)
    }

    @SuppressLint("SetTextI18n")
    private fun overlayModels() {
        binding.sceneView.apply {
            geospatialEnabled = true
            onArSessionFailed = { e: Exception ->
                Log.e(TAG, e.message.toString())
            }
            onArFrame = { arFrame ->
                arFrame.session.earth.let { earth ->
                    binding.latitude.text =
                        "Latitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.latitude)}"
                    binding.longitude.text =
                        "Longitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.longitude)}"
                    binding.altitude.text =
                        "Altitude: ${String.format("%.6f", earth?.cameraGeospatialPose?.altitude)}"

                    if (earth != null)
                        binding.earthState.text = "Status earth: ${earth.earthState}"
                    else
                        binding.earthState.text = "Earth state: NULL"
                    binding.trackingState.text = "Tracking state: ${earth?.trackingState}"

                    if (earth?.trackingState == TrackingState.TRACKING && !viewModel.statusCreateAnchors) {
                        val cameraGeospatialPose = earth.cameraGeospatialPose
                        val model = viewModel.models[viewModel.indexModelEnd]
                        val distance = viewModel.getDistanceBetweenPoints(
                            Point(cameraGeospatialPose.latitude, cameraGeospatialPose.longitude),
                            Point(model.point.latitude, model.point.longitude)
                        )

                        binding.distance.text = "Distance: ${String.format("%.4f", distance)}"
                        binding.indexModel.text = "Index model: ${viewModel.indexModelEnd}"

                        if (distance < 0.01) {
                            val earthAnchor = earth.createAnchor(
                                model.point.latitude, model.point.longitude,
                                cameraGeospatialPose.altitude - 1.5,
                                0f, 0f, 0f, 1f
                            )

                            val arModelNode = ArModelNode(
                                placementMode = PlacementMode.BEST_AVAILABLE,
                                hitPosition = Position(0.0f, 0.0f, -2.0f),
                                followHitPosition = false,
                                instantAnchor = true
                            ).apply {
                                val file = File("${APP_ACTIVITY.filesDir}/${args.excursionId}/$FOLDER_MODELS/${viewModel.models[viewModel.indexModelEnd].model}")
                                loadModelGlbAsync(
                                    glbFileLocation = Uri.fromFile(file).toString(),
                                    autoAnimate = true,
                                    scaleToUnits = 3f,
                                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                                )
                            }

                            arModelNode.anchor = earthAnchor
                            binding.sceneView.addChild(arModelNode)
                            viewModel.nodes.add(arModelNode)

                            if (viewModel.indexModelEnd != viewModel.models.size)
                                viewModel.indexModelEnd++
                            else
                                viewModel.statusCreateAnchors = true

                            if (viewModel.nodes.size > 3) {
                                viewModel.nodes[0].anchor?.detach()
                                viewModel.nodes[0].destroy()
                                viewModel.nodes.removeAt(0)
                            }
                        }
                    }
                }
            }
            onArSessionCreated = { arSession ->
            }
        }
    }
}