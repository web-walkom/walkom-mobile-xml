package ru.walkom.app.presentation.screens.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.replaceFragment
import ru.walkom.app.databinding.ActivityAuthBinding
import ru.walkom.app.presentation.screens.welcome.WelcomeFragment


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        replaceFragment(WelcomeFragment(), false)
    }
}