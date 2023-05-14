package ru.walkom.app.presentation.screens.excursion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import coil.load
import ru.walkom.app.R
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.BUNDLE_KEY_ID
import ru.walkom.app.common.Constants.BUNDLE_KEY_PHOTOS
import ru.walkom.app.common.Constants.BUNDLE_KEY_TITLE
import ru.walkom.app.common.Constants.BUTTON_LOAD_EXCURSION
import ru.walkom.app.common.Constants.BUTTON_RUN_EXCURSION
import ru.walkom.app.common.Constants.FOLDER_AUDIO
import ru.walkom.app.common.Constants.FOLDER_MODELS
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentExcursionBinding
import ru.walkom.app.domain.model.Response
import java.io.File


class ExcursionFragment() : Fragment() {

    private val viewModel: ExcursionViewModel by activityViewModels()
    private lateinit var binding: FragmentExcursionBinding
//    private val args: ExcursionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentExcursionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments?.getString(BUNDLE_KEY_ID) != null) {
            viewModel.excursionId = arguments?.getString(BUNDLE_KEY_ID)
            viewModel.excursionTitle = arguments?.getString(BUNDLE_KEY_TITLE)
            viewModel.excursionPhoto = arguments?.getStringArray(BUNDLE_KEY_PHOTOS)?.get(0)
        }

        binding.excursionTitle.text = viewModel.excursionTitle
        binding.excursionPhoto.load(viewModel.excursionPhoto)

        clickHandler()
        getExcursionHandler()

        val folderAudio = File("${APP_ACTIVITY.filesDir}/${viewModel.excursionId}", FOLDER_AUDIO)
        val folderModels = File("${APP_ACTIVITY.filesDir}/${viewModel.excursionId}", FOLDER_MODELS)
        if (!folderAudio.exists() || !folderModels.exists()) {
            binding.startExcursion.text = BUTTON_LOAD_EXCURSION
            getSizeFilesHandler()
        }
    }

    private fun getExcursionHandler() {
        viewModel.stateExcursion.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        binding.progressLoad.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        binding.progressLoad.visibility = View.GONE
                        binding.excursionDescription.text = state.data?.description ?: ""
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

    private fun getSizeFilesHandler() {
        viewModel.stateSizeFiles.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.i(TAG, "Loading")
                    }
                    is Response.Success -> {
                        binding.startExcursion.text = "$BUTTON_LOAD_EXCURSION (${state.data} MB)"
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

    private fun clickHandler() {
        binding.closeExcursion.setOnClickListener {
            Navigation
                .findNavController(binding.root)
                .navigate(R.id.navigateToBackExcursionsFragment)
        }

        binding.optionsExcursion.setOnClickListener {

        }

        binding.startExcursion.setOnClickListener {
            runExcursion()
        }
    }

    private fun runExcursion() {
        val folderAudio = File("${APP_ACTIVITY.filesDir}/${viewModel.excursionId}", FOLDER_AUDIO)
        val folderModels = File("${APP_ACTIVITY.filesDir}/${viewModel.excursionId}", FOLDER_MODELS)

        if (!folderAudio.exists() || !folderModels.exists()) {
            val folderData = File(APP_ACTIVITY.filesDir, viewModel.excursionId)
            if (!folderData.exists())
                folderData.mkdir()

            viewModel.downloadFilesExcursion()
            viewModel.stateDownload.observe(viewLifecycleOwner) { response ->
                response?.let { state ->
                    when (state) {
                        is Response.Loading -> {
                            binding.progressDownload.visibility = View.VISIBLE
                            binding.startExcursion.isEnabled = false
                            binding.startExcursion.background = resources.getDrawable(R.drawable.green_button_opacity)
                        }

                        is Response.Success -> {
                            binding.progressDownload.visibility = View.GONE
                            binding.startExcursion.isEnabled = true
                            binding.startExcursion.background = resources.getDrawable(R.drawable.green_button)
                            binding.startExcursion.text = BUTTON_RUN_EXCURSION

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
        else {
            Navigation
                .findNavController(binding.root)
                .navigate(R.id.navigateToMapFragment)
        }
    }
}