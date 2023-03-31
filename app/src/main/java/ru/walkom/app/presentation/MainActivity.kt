package ru.walkom.app.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import ru.walkom.app.common.Constants.ERROR_INVALID_ERROR
import ru.walkom.app.common.isEmailValid
import ru.walkom.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueBtn.setOnClickListener {
//            if (!binding.emailField.text.toString().isEmailValid()) {
//                binding.emailField.error = ERROR_INVALID_ERROR
//            }
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}