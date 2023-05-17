package ru.walkom.app.presentation.screens.excursion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.R
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.BUTTON_LOAD_EXCURSION
import ru.walkom.app.common.Constants.BUTTON_RUN_EXCURSION
import ru.walkom.app.common.Constants.FOLDER_AUDIO
import ru.walkom.app.common.Constants.FOLDER_MODELS
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentExcursionBinding
import ru.walkom.app.domain.model.Response
import java.io.File


@AndroidEntryPoint
class ExcursionFragment : Fragment() {

    private val args: ExcursionFragmentArgs by navArgs()
    private val viewModel: ExcursionViewModel by viewModels()
    private lateinit var binding: FragmentExcursionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExcursionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.excursionTitle.text = args.excursion.title
        binding.excursionPhoto.load(args.excursion.photos[0])

        clickHandler()
        getExcursionHandler()

        val folderAudio = File("${APP_ACTIVITY.filesDir}/${args.excursion.id}", FOLDER_AUDIO)
        val folderModels = File("${APP_ACTIVITY.filesDir}/${args.excursion.id}", FOLDER_MODELS)
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
            findNavController().popBackStack()
        }

        binding.optionsExcursion.setOnClickListener {

        }

        binding.startExcursion.setOnClickListener {
            runExcursion()
        }
    }

    private fun runExcursion() {
        val folderAudio = File("${APP_ACTIVITY.filesDir}/${args.excursion.id}", FOLDER_AUDIO)
        val folderModels = File("${APP_ACTIVITY.filesDir}/${args.excursion.id}", FOLDER_MODELS)

        if (!folderAudio.exists() || !folderModels.exists()) {
            val folderData = File(APP_ACTIVITY.filesDir, args.excursion.id)
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
            val action = ExcursionFragmentDirections.navigateToMapFragment(args.excursion.id)
            findNavController().navigate(action)
        }
    }
}