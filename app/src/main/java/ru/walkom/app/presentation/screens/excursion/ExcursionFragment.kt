package ru.walkom.app.presentation.screens.excursion

import android.annotation.SuppressLint
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
        checkFilesExcursion()
    }

    private fun clickHandler() {
        binding.closeExcursion.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.actionsExcursion.setOnClickListener {
            val action = ExcursionFragmentDirections.navigateToActionsExcursionFragment(args.excursion.id)
            findNavController().navigate(action)
        }

        binding.startExcursion.setOnClickListener {
            runExcursion()
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

    private fun checkFilesExcursion() {
        if (!viewModel.checkFilesExcursion()) {
            binding.actionsExcursion.visibility = View.GONE
            binding.startExcursion.text = BUTTON_LOAD_EXCURSION
            getSizeFilesHandler()
        }
    }

    private fun getSizeFilesHandler() {
        viewModel.stateSizeFiles.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun runExcursion() {
        if (!viewModel.checkFilesExcursion()) {
            val folderFiles = File(APP_ACTIVITY.filesDir, args.excursion.id)
            if (!folderFiles.exists())
                folderFiles.mkdir()

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
                            binding.actionsExcursion.visibility = View.VISIBLE

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
