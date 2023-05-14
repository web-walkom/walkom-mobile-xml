package ru.walkom.app.presentation.screens.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ru.walkom.app.common.Constants.ERROR_INVALID_ERROR
import ru.walkom.app.common.isEmailValid
import ru.walkom.app.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private val viewModel: WelcomeViewModel by activityViewModels()
    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.continueEmail.setOnClickListener {
            if (!binding.emailField.text.toString().isEmailValid())
                binding.emailField.error = ERROR_INVALID_ERROR
            else {
                viewModel.sendCode()
            }
        }
    }
}