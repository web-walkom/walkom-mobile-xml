package ru.walkom.app.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.databinding.ActivityExcursionBinding
import ru.walkom.app.presentation.screens.excursions.ExcursionViewModel
import ru.walkom.app.presentation.screens.map.MapActivity

@AndroidEntryPoint
class ExcursionActivity : AppCompatActivity() {

    private val viewModel: ExcursionViewModel by viewModels()
    private lateinit var binding: ActivityExcursionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExcursionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getExcursionById()
        binding.titleExcursion.text = viewModel.excursion.title
        binding.descriptionExcursion.text = viewModel.excursion.description
    }

    fun onClickCloseExcursion(view: View) {
        finish()
    }

    fun onClickRunExcursion(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}