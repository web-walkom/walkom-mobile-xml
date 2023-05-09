package ru.walkom.app.presentation.screens.excursions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityExcursionsBinding
import ru.walkom.app.domain.model.Response
import ru.walkom.app.presentation.ExcursionActivity

@AndroidEntryPoint
class ExcursionsActivity: AppCompatActivity() {

    private val viewModel: ExcursionsViewModel by viewModels()
    private lateinit var binding: ActivityExcursionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateExcursions.observe(this) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.d(TAG, "Loading")
                    }
                    is Response.Success -> {
                        binding.excursions.visibility = View.VISIBLE
                        binding.excursionTitle.text = state.data[0].title
                        binding.excursionPhoto.load(state.data[0].photos[0])
                    }
                    is Response.Error -> {
                        Log.d(TAG, state.message)
                    }
                }
            }
        }

        binding = ActivityExcursionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickExcursion(view: View) {
        val intent = Intent(this, ExcursionActivity::class.java)
        startActivity(intent)
    }
}