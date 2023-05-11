package ru.walkom.app.common

import android.media.MediaPlayer
import java.io.File

class AudioPlayer {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File

    fun play(audioKey: String, fileUrl: String, function: () -> Unit) {
//        18:25 https://www.youtube.com/watch?v=vMxKde2Adwo&list=PLY8G5DMG6TiOBq7OWFPWF2Um3FRB5s2ke&index=13&ab_channel=Мобильныйразработчик
//        if (file.exists()) {
//            val mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(file.absolutePath)
//            mediaPlayer.prepare()
//            mediaPlayer.start()
//        }
    }

    fun stop() {

    }

    fun init() {
        mediaPlayer = MediaPlayer()
    }
}