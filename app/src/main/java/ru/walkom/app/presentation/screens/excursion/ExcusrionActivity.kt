package ru.walkom.app.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ru.walkom.app.databinding.ActivityExcursionBinding
import ru.walkom.app.presentation.screens.map.MapActivity

class ExcursionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExcursionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExcursionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickCloseExcursion(view: View) {
        finish()
    }

    fun onClickRunExcursion(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}