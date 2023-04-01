package ru.walkom.app.presentation

import android.app.Application
import com.yandex.mapkit.MapKitFactory;

class MainApplication : Application() {
    private val MAPKIT_API_KEY: String = "4e10e9f2-d783-499c-b77d-8fc64489b4ac"

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}