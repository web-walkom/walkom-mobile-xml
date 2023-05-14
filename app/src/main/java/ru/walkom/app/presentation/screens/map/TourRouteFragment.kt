package ru.walkom.app.presentation.screens.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ru.walkom.app.R
import ru.walkom.app.databinding.FragmentTourRouteBinding

class TourRouteFragment: Fragment() {

    private lateinit var binding: FragmentTourRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTourRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun showAR(view: View) {
        Navigation
            .findNavController(binding.root)
            .navigate(R.id.navigateToCameraARFragment)
    }
}