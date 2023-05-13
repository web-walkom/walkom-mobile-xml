package ru.walkom.app.presentation.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.replaceFragment
import ru.walkom.app.databinding.ActivityMainBinding
import ru.walkom.app.presentation.screens.excursions.ExcursionsFragment
import ru.walkom.app.presentation.screens.welcome.WelcomeFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY = this
        initFunc()
    }

    private fun initFunc() {
        val userAuth = true

        if (userAuth)
            replaceFragment(ExcursionsFragment(), false)
        else
            replaceFragment(WelcomeFragment(), false)
    }
}