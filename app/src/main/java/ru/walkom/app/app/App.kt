package ru.walkom.app.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory;
import dagger.hilt.android.HiltAndroidApp
import ru.walkom.app.common.Constants.MAPKIT_API_KEY

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}