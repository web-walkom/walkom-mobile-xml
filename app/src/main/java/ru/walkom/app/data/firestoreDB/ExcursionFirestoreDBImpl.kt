package ru.walkom.app.data.firestoreDB

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.walkom.app.common.Constants.DESCRIPTION_FIELD
import ru.walkom.app.common.Constants.EXCURSIONS_COLLECTION
import ru.walkom.app.common.Constants.MODELS_FIELD
import ru.walkom.app.common.Constants.PHOTOS_FIELD
import ru.walkom.app.common.Constants.PLACEMARKS_FIELD
import ru.walkom.app.common.Constants.PRICE_FIELD
import ru.walkom.app.common.Constants.TITLE_FIELD
import ru.walkom.app.common.Constants.WAYPOINTS_FIELD
import ru.walkom.app.domain.model.ExcursionCamera
import ru.walkom.app.domain.model.ExcursionNew
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionMap
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Response

class ExcursionFirestoreDBImpl(
    private val db: FirebaseFirestore
): ExcursionFirestoreDB {
    override fun getExcursions() = flow<Response<List<ExcursionItem>>> {
        try {
            emit(Response.Loading)

            val excursions = db.collection(EXCURSIONS_COLLECTION)
                .get().await().map { document ->
                    val excursion = document.toObject(ExcursionItem::class.java)
                    excursion.id = document.id
                    excursion
                }

            emit(Response.Success(excursions))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun <T> getExcursionById(id: String, type: Class<T>) = flow<Response<T?>> {
        try {
            emit(Response.Loading)

            val excursion = db.collection(EXCURSIONS_COLLECTION)
                .document(id)
                .get().await()
                .toObject(type)

            excursion?.let {
                when (it) {
                    is ExcursionOpen -> (it as ExcursionOpen).id = id
                    is ExcursionMap -> (it as ExcursionMap).id = id
                    is ExcursionCamera -> (it as ExcursionCamera).id = id
                }
            }

            emit(Response.Success(excursion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun uploadExcursion(excursion: ExcursionNew, id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            val mapExcursion = hashMapOf<String, Any>()
            mapExcursion[TITLE_FIELD] = excursion.title
            mapExcursion[PHOTOS_FIELD] = excursion.photos
            mapExcursion[DESCRIPTION_FIELD] = excursion.description
            mapExcursion[PLACEMARKS_FIELD] = excursion.placemarks
            mapExcursion[WAYPOINTS_FIELD] = excursion.waypoints
            mapExcursion[MODELS_FIELD] = excursion.models

            db.collection(EXCURSIONS_COLLECTION).document(id).set(mapExcursion).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}
