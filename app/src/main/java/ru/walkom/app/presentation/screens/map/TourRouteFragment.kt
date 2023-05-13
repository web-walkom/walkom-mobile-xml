package ru.walkom.app.presentation.screens.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.walkom.app.common.replaceFragment
import ru.walkom.app.databinding.FragmentTourRouteBinding
import ru.walkom.app.presentation.screens.camera.CameraARFragment

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
        replaceFragment(CameraARFragment())
    }
}