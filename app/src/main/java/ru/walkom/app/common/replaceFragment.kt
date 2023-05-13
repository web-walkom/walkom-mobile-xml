package ru.walkom.app.common

import android.content.Intent
import androidx.fragment.app.Fragment
import ru.walkom.app.R
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.presentation.screens.MainActivity


fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack)
        APP_ACTIVITY.supportFragmentManager
            .beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .addToBackStack(null)
            .commit()
    else
        APP_ACTIVITY.supportFragmentManager
            .beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .commit()
}