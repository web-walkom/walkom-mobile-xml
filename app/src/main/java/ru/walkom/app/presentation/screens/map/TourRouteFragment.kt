package ru.walkom.app.presentation.screens.map

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.FOLDER_MODELS
import ru.walkom.app.databinding.FragmentTourRouteBinding
import ru.walkom.app.presentation.components.fragment_alert_dialog.FragmentDialog
import java.io.File


class TourRouteFragment: FragmentDialog() {

    private val args: TourRouteFragmentArgs by navArgs()
    private lateinit var binding: FragmentTourRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTourRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showAR.setOnClickListener {
            val action = TourRouteFragmentDirections.navigateToCameraARFragment(args.excursionId)
            findNavController().navigate(action)
        }

        val model = ModelNode()
        val file = File("${APP_ACTIVITY.filesDir}/${args.excursionId}/${FOLDER_MODELS}/treasure_chest.glb")
        model.loadModelGlbAsync(
            glbFileLocation = Uri.fromFile(file).toString(),
            scaleToUnits = 1f,
            autoAnimate = true,
            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f)
        )

        binding.sceneView.addChild(model)
    }
}