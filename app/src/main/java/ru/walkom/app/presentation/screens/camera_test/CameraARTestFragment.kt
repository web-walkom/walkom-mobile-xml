package ru.walkom.app.presentation.screens.camera_test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.Config
import com.google.ar.core.Session
import ru.walkom.app.databinding.FragmentCameraArTestBinding


class CameraARTestFragment : Fragment() {

    private lateinit var binding: FragmentCameraArTestBinding
    private lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraArTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}