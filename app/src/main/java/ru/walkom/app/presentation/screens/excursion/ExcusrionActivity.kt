package ru.walkom.app.presentation.screens.excursion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ru.walkom.app.common.Constants
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityExcursionBinding
import ru.walkom.app.domain.model.Response

@AndroidEntryPoint
class ExcursionActivity : AppCompatActivity() {

    private val viewModel: ExcursionViewModel by viewModels()
    private lateinit var binding: ActivityExcursionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExcursionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.stateExcursion.observe(this) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.d(Constants.TAG, "Loading")
                    }
                    is Response.Success -> {
                        binding.excursionTitle.text = state.data?.title ?: ""
                        binding.excursionDescription.text = state.data?.description ?: ""
                        binding.excursionPhoto.load(state.data?.photos?.get(0))
                    }
                    is Response.Error -> {
                        Log.e(Constants.TAG, state.message)
                    }
                }
            }
        }
    }

//    fun onClickCloseExcursion(view: View) {
//        finish()
//    }

    fun onClickRunExcursion(view: View) {
//        val intent = Intent(this, MapActivity::class.java)
//        startActivity(intent)

        viewModel.downloadAudioExcursion()
        viewModel.stateAudio.observe(this) { response ->
            response?.let { state ->
                when (state) {
                    is Response.Loading -> {
                        Log.d(TAG, "Loading")
                    }
                    is Response.Success -> {
                        Log.i(TAG, this.filesDir.path)
//                        val mediaPlayer = MediaPlayer()
//                        mediaPlayer.setDataSource("${this.filesDir.path}/QQ4oHDyYxtOme3Nu2VFq_0.mp3")
//                        mediaPlayer.prepare()
//                        mediaPlayer.start()
                    }
                    is Response.Error -> {
                        Log.e(TAG, state.message)
                    }
                }
            }
        }
    }
}