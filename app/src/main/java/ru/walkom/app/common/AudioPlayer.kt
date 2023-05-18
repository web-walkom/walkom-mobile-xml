package ru.walkom.app.common

import android.media.MediaPlayer
import android.util.Log
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.FOLDER_AUDIO
import ru.walkom.app.common.Constants.TAG
import java.io.File

class AudioPlayer {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File

    fun init() {
        mediaPlayer = MediaPlayer()
    }

    fun play(audio: String, id: String, functionStart: () -> Unit, functionStop: () -> Unit) {
        file = File("${APP_ACTIVITY.filesDir}/$id/$FOLDER_AUDIO", audio)

        if (file.exists() && file.isFile) {
            try {
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                functionStart()
                mediaPlayer.setOnCompletionListener {
                    stop {
                        functionStop()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun stop(function: () -> Unit) {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            function()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            function()
        }
    }

    fun release() {
        if (this::mediaPlayer.isInitialized)
            mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun start() {
        mediaPlayer.start()
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }
}