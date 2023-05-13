package ru.walkom.app.presentation.screens.verify_code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ru.walkom.app.common.restartActivity
import ru.walkom.app.databinding.FragmentVerifyCodeBinding


class VerifyCodeFragment : Fragment() {

    private val viewModel: VerifyCodeViewModel by activityViewModels()
    private lateinit var binding: FragmentVerifyCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.continueCode.setOnClickListener {
            val code = binding.codeField.text.toString()
            if (viewModel.checkCode(code)) {
                restartActivity()
            }
        }
    }
}