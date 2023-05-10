package ru.walkom.app.presentation.screens.excursion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import ru.walkom.app.common.Constants
import ru.walkom.app.databinding.FragmentExcursionBinding
import ru.walkom.app.domain.model.Response


class ExcursionFragment : Fragment() {

    private val viewModel: ExcursionViewModel by activityViewModels()
    private lateinit var binding: FragmentExcursionBinding

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
                        Log.d(Constants.TAG, "Loading")
                    }
                    is Response.Success -> {
                        binding.excursionTitle.text = state.data?.title ?: ""
                        binding.excursionDescription.text = state.data?.description ?: ""
                        binding.excursionPhoto.load(state.data?.photos?.get(0))
                    }
                    is Response.Error -> {
                        Log.e(Constants.TAG, state.message)
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExcursionFragment()
    }
}