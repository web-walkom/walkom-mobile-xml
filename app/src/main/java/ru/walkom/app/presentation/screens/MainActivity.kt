package ru.walkom.app.presentation.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.replaceActivity
import ru.walkom.app.common.replaceFragment
import ru.walkom.app.databinding.ActivityMainBinding
import ru.walkom.app.presentation.screens.auth.AuthActivity
import ru.walkom.app.presentation.screens.excursions.ExcursionsFragment


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
        initFunc()
    }

    private fun initFunc() {
        val userAuth = false

        if (userAuth)
            replaceFragment(ExcursionsFragment())
        else
            replaceActivity(AuthActivity())
    }
}