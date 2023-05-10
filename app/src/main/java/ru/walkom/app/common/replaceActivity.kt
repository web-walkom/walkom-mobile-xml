package ru.walkom.app.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
}