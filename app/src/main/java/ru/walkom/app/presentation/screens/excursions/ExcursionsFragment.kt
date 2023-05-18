package ru.walkom.app.presentation.screens.excursions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentExcursionsBinding
import ru.walkom.app.domain.model.Response


@AndroidEntryPoint
class ExcursionsFragment : Fragment() {

    private val viewModel: ExcursionsViewModel by viewModels()
    private lateinit var binding: FragmentExcursionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExcursionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateExcursions.observe(viewLifecycleOwner) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        binding.progressLoad.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        binding.progressLoad.visibility = View.GONE
                        binding.excursionsList.visibility = View.VISIBLE
                        binding.excursionTitle.text = state.data[0].title
                        binding.excursionPhoto.load(state.data[0].photos[0])

                        binding.excursionCard.setOnClickListener {
                            val excursion = state.data[1]
                            val action = ExcursionsFragmentDirections.navigateToExcursionFragment(excursion)
                            findNavController().navigate(action)
                        }
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
}