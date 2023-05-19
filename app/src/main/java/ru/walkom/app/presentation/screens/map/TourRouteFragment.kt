package ru.walkom.app.presentation.screens.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.walkom.app.R
import ru.walkom.app.databinding.FragmentTourRouteBinding
import ru.walkom.app.presentation.components.fragment_dialog.FragmentDialog


class TourRouteFragment: FragmentDialog() {

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
            findNavController().navigate(R.id.navigateToCameraARFragment)
        }
    }
}