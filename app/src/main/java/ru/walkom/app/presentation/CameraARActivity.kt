package ru.walkom.app.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.databinding.ActivityCameraArBinding

class CameraARActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraArBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityCameraArBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arModelNode = ArModelNode(
            placementMode = PlacementMode.BEST_AVAILABLE,
            hitPosition = Position(0.0f, 0.0f, -2.0f),
            followHitPosition = false,
            instantAnchor = true
        )

        arModelNode.loadModelGlbAsync(
            context = this,
            glbFileLocation = "models/robot.glb",
            autoAnimate = true,
            scaleToUnits = 1f,
            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
            onError = { exception ->
                Log.e(TAG, exception.message.toString())
            },
            onLoaded = { modelInstance ->

            }
        )

    }
}