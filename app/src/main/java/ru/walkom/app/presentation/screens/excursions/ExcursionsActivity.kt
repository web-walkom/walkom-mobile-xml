package ru.walkom.app.presentation.screens.excursions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.databinding.ActivityExcursionsBinding
import ru.walkom.app.presentation.ExcursionActivity

@AndroidEntryPoint
class ExcursionsActivity: AppCompatActivity() {

    private val viewModel: ExcursionsViewModel by viewModels()
    private lateinit var binding: ActivityExcursionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val state = viewModel.excursions

        binding = ActivityExcursionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.titleExcursion.text = viewModel.excursions[0].title
    }

    fun onClickExcursion(view: View) {
        val intent = Intent(this, ExcursionActivity::class.java)
        startActivity(intent)
    }
}