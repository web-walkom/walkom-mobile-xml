package ru.walkom.app.presentation.screens.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.walkom.app.R
import ru.walkom.app.databinding.FragmentTourRouteBinding


class TourRouteFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTourRouteBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTourRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.attributes.windowAnimations = R.style.BottomSheetAnimation

        binding.showAR.setOnClickListener {
            findNavController().navigate(R.id.navigateToCameraARFragment)
        }
    }
}