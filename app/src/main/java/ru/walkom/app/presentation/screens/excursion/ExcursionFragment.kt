package ru.walkom.app.presentation.screens.excursion

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import ru.walkom.app.R
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.BUTTON_LOAD_EXCURSION
import ru.walkom.app.common.Constants.BUTTON_RUN_EXCURSION
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentExcursionBinding
import ru.walkom.app.domain.model.Response
import java.io.File


class ExcursionFragment : Fragment() {

    private val viewModel: ExcursionViewModel by activityViewModels()
    private lateinit var binding: FragmentExcursionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcursionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.stateExcursion.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.i(TAG, "Loading")
                    }
                    is Response.Success -> {
                        binding.excursionTitle.text = state.data?.title ?: ""
                        binding.excursionDescription.text = state.data?.description ?: ""
                        binding.excursionPhoto.load(state.data?.photos?.get(0))
                    }
                    is Response.Error -> {
                        Log.e(TAG, state.message)
                    }
                }
            }
        }

        val file = File(activity?.filesDir, "QQ4oHDyYxtOme3Nu2VFq_0")
        if (!file.exists())
            binding.startExcursion.text = BUTTON_LOAD_EXCURSION

        binding.closeExcursion.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.startExcursion.setOnClickListener {
            val file = File(activity?.filesDir, "QQ4oHDyYxtOme3Nu2VFq_0")

//            binding.progressLoad.visibility = View.VISIBLE
//            binding.startExcursion.isEnabled = false
//            binding.startExcursion.background = resources.getDrawable(R.drawable.green_button_opacity)

            if (!file.exists()) {
                binding.progressLoad.visibility = View.VISIBLE
                binding.startExcursion.isEnabled = false
                binding.startExcursion.background = resources.getDrawable(R.drawable.green_button_opacity)

                viewModel.downloadDataExcursion()
                viewModel.stateAudio.observe(viewLifecycleOwner) { response ->
                    response?.let { state ->
                        when (state) {
                            is Response.Loading -> {
                                Log.i(TAG, "Loading")
                            }

                            is Response.Success -> {
                                binding.progressLoad.visibility = View.GONE
                                binding.startExcursion.isEnabled = true
                                binding.startExcursion.background = resources.getDrawable(R.drawable.green_button)
                                binding.startExcursion.text = BUTTON_RUN_EXCURSION
                                return@let
                            }

                            is Response.Error -> {
                                Log.e(TAG, state.message)
                            }
                        }
                    }
                }
            }
            else {
                Log.i(TAG, "Run excursion")
            }
        }
    }
}