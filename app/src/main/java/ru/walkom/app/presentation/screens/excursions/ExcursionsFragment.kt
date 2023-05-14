package ru.walkom.app.presentation.screens.excursions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import coil.load
import ru.walkom.app.R
import ru.walkom.app.common.Constants.BUNDLE_KEY_ID
import ru.walkom.app.common.Constants.BUNDLE_KEY_PHOTOS
import ru.walkom.app.common.Constants.BUNDLE_KEY_TITLE
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.FragmentExcursionsBinding
import ru.walkom.app.domain.model.Response


class ExcursionsFragment : Fragment() {

    private val viewModel: ExcursionsViewModel by activityViewModels()
    private lateinit var binding: FragmentExcursionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExcursionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
//                            replaceFragment(ExcursionFragment(state.data[0]))

                            val excursion = state.data[0]
//                            val action = ExcursionsFragmentDirections.navigateToExcursionFragment(
//                                excursion.id,
//                                excursion.title,
//                                excursion.photos.toTypedArray()
//                            )

                            val bundle = Bundle()
                            bundle.putString(BUNDLE_KEY_ID, excursion.id)
                            bundle.putString(BUNDLE_KEY_TITLE, excursion.title)
                            bundle.putStringArray(BUNDLE_KEY_PHOTOS, excursion.photos.toTypedArray())

                            Navigation
                                .findNavController(binding.root)
                                .navigate(R.id.navigateToExcursionFragment, bundle)
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