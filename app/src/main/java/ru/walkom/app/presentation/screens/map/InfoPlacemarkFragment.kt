package ru.walkom.app.presentation.screens.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import ru.walkom.app.databinding.FragmentInfoPlacemarkBinding
import ru.walkom.app.presentation.components.fragment_alert_dialog.FragmentDialog


class InfoPlacemarkFragment : FragmentDialog() {

    private val args: InfoPlacemarkFragmentArgs by navArgs()
    private lateinit var binding: FragmentInfoPlacemarkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoPlacemarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titlePlacemark.text = args.placemark.title

        binding.showAR.setOnClickListener {
            val action = InfoPlacemarkFragmentDirections.navigateToCameraARFragment(args.excursionId)
            findNavController().navigate(action)
        }

        val model = ModelNode()
        model.loadModelGlbAsync(
            glbFileLocation = "models/diligense.glb",
            scaleToUnits = 1f,
            autoAnimate = true,
            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f)
        )

        binding.sceneView.addChild(model)
    }
}