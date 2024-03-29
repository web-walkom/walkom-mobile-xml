package ru.walkom.app.presentation.screens.excursions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.domain.model.ExcursionNew
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.Model3D
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
        val ID = "6TuCZDcCRx2hFf7Y4WAz"

        val excursion_ = ExcursionNew(
            title = "Keeper. Театрализованные экскурсии по Перми",
            description = "Знакомьтесь! Это Keeper - хранитель времени! Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            photos = listOf("https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/excursion/1.jpg"),
            placemarks = listOf(
                Placemark(
                    title = "Пермский медведь",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_bear/1.jpeg"
                    ),
                    Point(58.037188, 56.124989)
                ),
                Placemark(
                    title = "Гимназия № 17",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/gymnasium_17/1.jpg"
                    ),
                    Point(58.036989, 56.124917)
                ),
                Placemark(
                    title = "Рождественско-Богородицкая церковь",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/church/1.jpg"
                    ),
                    Point(58.036841, 56.126014)
                ),
                Placemark(
                    title = "Пермский государственный институт культуры",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_state_institute_culture/1.jpg"
                    ),
                    Point(58.036672, 56.127291)
                ),
                Placemark(
                    title = "Дом пекарня наследника Демидовых",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/house_demidovs/1.jpg"
                    ),
                    Point(58.036879, 56.127382)
                ),
                Placemark(
                    title = "Триумф. Пермский кинотеатр",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/triumph/1.jpg"
                    ),
                    Point(58.037025, 56.126263)
                ),
            ),
            waypoints = listOf(
                Waypoint(
                    audio = "0.mp3",
                    Point(58.037171, 56.125121)
                ),
                Waypoint(
                    audio = "1.mp3",
                    Point(58.037188, 56.124989)
                ),
                Waypoint(
                    audio = "2.mp3",
                    Point(58.036989, 56.124917)
                ),
                Waypoint(
                    audio = "3.mp3",
                    Point(58.036841, 56.126014)
                ),
                Waypoint(
                    audio = "4.mp3",
                    Point(58.036672, 56.127291)
                ),
                Waypoint(
                    audio = "5.mp3",
                    Point(58.036879, 56.127382)
                ),
                Waypoint(
                    audio = "6.mp3",
                    Point(58.037025, 56.126263)
                ),
            ),
            models = listOf(
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.037157, 56.124980)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.036989, 56.124917)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.036841, 56.126014)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.036672, 56.127291)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.036879, 56.127382)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.037025, 56.126263)
//                ),
            )
        )

        val excursion = ExcursionNew(
            title = "Keeper. Театрализованные экскурсии по Перми",
            description = "Знакомьтесь! Это Keeper - хранитель времени! Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            photos = listOf("https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/excursion/1.jpg"),
            placemarks = listOf(
                Placemark(
                    title = "Пермский медведь",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_bear/1.jpeg"
                    ),
                    Point(58.010418, 56.237335)
                ),
                Placemark(
                    title = "Гимназия № 17",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/gymnasium_17/1.jpg"
                    ),
                    Point(58.012611, 56.242274)
                ),
                Placemark(
                    title = "Рождественско-Богородицкая церковь",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/church/1.jpg"
                    ),
                    Point(58.012248, 56.242676)
                ),
                Placemark(
                    title = "Пермский государственный институт культуры",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/perm_state_institute_culture/1.jpg"
                    ),
                    Point(58.013174, 56.243045)
                ),
                Placemark(
                    title = "Дом пекарня наследника Демидовых",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/house_demidovs/1.jpg"
                    ),
                    Point(58.012553, 56.243556)
                ),
                Placemark(
                    title = "Триумф. Пермский кинотеатр",
                    photos = listOf(
                        "https://s3.timeweb.com/37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce/excursions/${ID}/photos/placemarks/triumph/1.jpg"
                    ),
                    Point(58.012758, 56.244125)
                ),
            ),
            waypoints = listOf(
                Waypoint(
                    audio = "0.mp3",
                    Point(58.010412, 56.237251)
                ),
                Waypoint(
                    audio = "1.mp3",
                    Point(58.010433, 56.237325)
                ),
                Waypoint(
                    audio = "2.mp3",
                    Point(58.010934, 56.237625)
                ),
                Waypoint(
                    audio = "3.mp3",
                    Point(58.011374, 56.239166)
                ),
                Waypoint(
                    audio = "4.mp3",
                    Point(58.012477, 56.243069)
                ),
                Waypoint(
                    audio = "5.mp3",
                    Point(58.012575, 56.243399)
                ),
                Waypoint(
                    audio = "6.mp3",
                    Point(58.012757, 56.244027)
                ),
            ),
            models = listOf(
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.010433, 56.237325)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.010934, 56.237625)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.011374, 56.239166)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.012477, 56.243069)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.012575, 56.243399)
//                ),
//                Model3D(
//                    model = "diligense.glb",
//                    Point(58.012757, 56.244027)
//                ),
            )
        )

        viewModelScope.launch {
            uploadExcursionUseCase.invoke(excursion, ID).collect { response ->
                if (response is Response.Success) {
                    Log.i(TAG, "Success upload excursion")
                }
            }
        }
    }
}