package ru.walkom.app.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import ru.walkom.app.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueBtn.setOnClickListener {
//            if (!binding.emailField.text.toString().isEmailValid()) {
//                binding.emailField.error = ERROR_INVALID_ERROR
//            }
            val intent = Intent(this, ExcursionActivity::class.java)
            startActivity(intent)
        }
    }
}