package ru.walkom.app.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ru.walkom.app.R
import ru.walkom.app.databinding.ActivityExcursionBinding
import ru.walkom.app.databinding.ActivityExcursionsBinding

class ExcursionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExcursionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExcursionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickExcursion(view: View) {
        val intent = Intent(this, ExcursionActivity::class.java)
        startActivity(intent)
    }
}