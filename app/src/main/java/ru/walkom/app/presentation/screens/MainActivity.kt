package ru.walkom.app.presentation.screens

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowId
import android.view.WindowManager
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.common.replaceFragment
import ru.walkom.app.databinding.ActivityMainBinding
import ru.walkom.app.presentation.screens.excursions.ExcursionsFragment
import ru.walkom.app.presentation.screens.welcome.WelcomeFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowFlag()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY = this
        initFunc()
    }

    private fun setWindowFlag() {
//        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        this.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        this.supportActionBar?.elevation = 0f
        this.window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        this.window.statusBarColor = Color.TRANSPARENT
//        this.window.navigationBarColor = Color.TRANSPARENT
//        this.window.attributes.flags =
//                this.window.attributes.flags and
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv() and
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION.inv()

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT

//        this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun initFunc() {
        val userAuth = true

        if (userAuth)
            replaceFragment(ExcursionsFragment(), false)
        else
            replaceFragment(WelcomeFragment(), false)
    }
}