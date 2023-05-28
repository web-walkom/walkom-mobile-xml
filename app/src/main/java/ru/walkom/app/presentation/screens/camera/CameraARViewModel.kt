package ru.walkom.app.presentation.screens.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.ar.node.ArModelNode
import kotlinx.coroutines.launch
import ru.walkom.app.common.Constants.ARGUMENT_EXCURSION_ID
import ru.walkom.app.domain.model.ExcursionCamera
import ru.walkom.app.domain.model.Model3D
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.pow


@HiltViewModel
class CameraARViewModel @Inject constructor(
    private val getExcursionByIdUseCase : GetExcursionByIdUseCase,
    private val state: SavedStateHandle
): ViewModel() {

    private val excursionId = state.get<String>(ARGUMENT_EXCURSION_ID)

    private val _stateModels = MutableLiveData<Response<ExcursionCamera?>>()
    val stateModels: LiveData<Response<ExcursionCamera?>> get() = _stateModels

    lateinit var models: List<Model3D>
    val nodes: ArrayList<ArModelNode> = arrayListOf()
    var statusCreateAnchors = false

    var indexModelEnd = 0

    init {
        viewModelScope.launch {
            if (excursionId != null) {
                getExcursionByIdUseCase.invoke(excursionId, ExcursionCamera::class.java).collect { response ->
                    _stateModels.postValue(response)
                }
            }
        }
    }

    fun getDistanceBetweenPoints(point1: Point, point2: Point): Double {
        val r = 6371

        val areaRadian = Point(
            point1.latitude * PI / 180,
            point1.longitude * PI / 180
        )
        val pointRadian = Point(
            point2.latitude * PI / 180,
            point2.longitude * PI / 180
        )

        return 2 * r * kotlin.math.asin(
            kotlin.math.sqrt(
                kotlin.math.sin((pointRadian.latitude - areaRadian.latitude) / 2).pow(2.0)
                        + kotlin.math.cos(areaRadian.latitude) * kotlin.math.cos(pointRadian.latitude)
                        * kotlin.math.sin((pointRadian.longitude - areaRadian.longitude) / 2).pow(2.0)
            )
        )
    }
}