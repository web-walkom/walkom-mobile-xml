package ru.walkom.app.presentation.screens.excursions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.domain.model.ExcursionAll
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import ru.walkom.app.domain.use_case.UploadExcursionUseCase
import javax.inject.Inject

@HiltViewModel
class ExcursionsViewModel @Inject constructor(
    private val getExcursionsUseCase : GetExcursionsUseCase,
    private val uploadExcursionUseCase: UploadExcursionUseCase
): ViewModel() {

    private val _stateExcursions = MutableLiveData<Response<List<ExcursionItem>>>()
    val stateExcursions: LiveData<Response<List<ExcursionItem>>> get() = _stateExcursions

    init {
        viewModelScope.launch {
            getExcursionsUseCase.invoke().collect { response ->
                _stateExcursions.postValue(response)
            }
        }

//        uploadExcursion()
    }

    private fun uploadExcursion() {
        val ID = "BVs8u01NWCTjpZDaTfeT"

        val excursion = ExcursionAll(
            title = "Keeper. Театрализованные экскурсии по Перми",
            description = "Знакомьтесь! Это Keeper - хранитель времени! Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            photos = listOf("https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/excursion/1.jpg"),
            placemarks = listOf(
                Placemark(
                    title = "Пермский медведь",
                    latitude = 58.037188,
                    longitude = 56.124989,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_bear/1.jpeg"
                    ),
                ),
                Placemark(
                    title = "Гимназия № 17",
                    latitude = 58.036989,
                    longitude = 56.124917,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/gymnasium_17/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Рождественско-Богородицкая церковь",
                    latitude = 58.036841,
                    longitude = 56.126014,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/church/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Пермский государственный институт культуры",
                    latitude = 58.036672,
                    longitude = 56.127291,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_state_institute_culture/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Дом пекарня наследника Демидовых",
                    latitude = 58.036879,
                    longitude = 56.127382,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/house_demidovs/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Триумф. Пермский кинотеатр",
                    latitude = 58.037025,
                    longitude = 56.126263,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/triumph/1.jpg"
                    ),
                ),
            ),
            waypoints = listOf(
                Waypoint(
                    audio = "1.mp3",
                    latitude = 58.037188,
                    longitude = 56.124989
                ),
                Waypoint(
                    audio = "2.mp3",
                    latitude = 58.036989,
                    longitude = 56.124917
                ),
                Waypoint(
                    audio = "3.mp3",
                    latitude = 58.036841,
                    longitude = 56.126014
                ),
                Waypoint(
                    audio = "4.mp3",
                    latitude = 58.036672,
                    longitude = 56.127291
                ),
                Waypoint(
                    audio = "5.mp3",
                    latitude = 58.036879,
                    longitude = 56.127382
                ),
                Waypoint(
                    audio = "6.mp3",
                    latitude = 58.037025,
                    longitude = 56.126263
                ),
            )
        )

        val excursion_ = ExcursionAll(
            title = "Keeper. Театрализованные экскурсии по Перми",
            description = "Знакомьтесь! Это Keeper - хранитель времени! Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            photos = listOf("https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/excursion/1.jpg"),
            placemarks = listOf(
                Placemark(
                    title = "Пермский медведь",
                    latitude = 58.010418,
                    longitude = 56.237335,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_bear/1.jpeg"
                    ),
                ),
                Placemark(
                    title = "Гимназия № 17",
                    latitude = 58.012611,
                    longitude = 56.242274,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/gymnasium_17/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Рождественско-Богородицкая церковь",
                    latitude = 58.012248,
                    longitude = 56.242676,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/church/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Пермский государственный институт культуры",
                    latitude = 58.013174,
                    longitude = 56.243045,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_state_institute_culture/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Дом пекарня наследника Демидовых",
                    latitude = 58.012553,
                    longitude = 56.243556,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/house_demidovs/1.jpg"
                    ),
                ),
                Placemark(
                    title = "Триумф. Пермский кинотеатр",
                    latitude = 58.012758,
                    longitude = 56.244125,
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/triumph/1.jpg"
                    ),
                ),
            ),
            waypoints = listOf(
                Waypoint(
                    audio = "1.mp3",
                    latitude = 58.010433,
                    longitude = 56.237325
                ),
                Waypoint(
                    audio = "2.mp3",
                    latitude = 58.010934,
                    longitude = 56.237625
                ),
                Waypoint(
                    audio = "3.mp3",
                    latitude = 58.011374,
                    longitude = 56.239166
                ),
                Waypoint(
                    audio = "4.mp3",
                    latitude = 58.012477,
                    longitude = 56.243069
                ),
                Waypoint(
                    audio = "5.mp3",
                    latitude = 58.012575,
                    longitude = 56.243399
                ),
                Waypoint(
                    audio = "6.mp3",
                    latitude = 58.012757,
                    longitude = 56.244027
                ),
            )
        )

        viewModelScope.launch {
            uploadExcursionUseCase.invoke(excursion).collect { response ->
                if (response is Response.Success) {
                    Log.i(TAG, "Success upload excursion")
                }
            }
        }
    }
}