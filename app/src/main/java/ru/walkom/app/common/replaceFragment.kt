package ru.walkom.app.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.walkom.app.R


fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit()
}

fun Fragment.replaceFragment(fragment: Fragment) {
    fragmentManager
        ?.beginTransaction()
        ?.replace(R.id.dataContainer, fragment)
        ?.addToBackStack(null)
        ?.commit()
}