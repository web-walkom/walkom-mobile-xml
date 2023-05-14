package ru.walkom.app.data.firestoreDB

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.walkom.app.common.Constants.EXCURSIONS_COLLECTION
import ru.walkom.app.domain.model.ExcursionAll
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
                if (it is ExcursionMap)
                    (it as ExcursionMap).id = id
                else if (it is ExcursionOpen)
                    (it as ExcursionOpen).id = id
            }

            emit(Response.Success(excursion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun uploadExcursion(excursion: ExcursionAll) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            val mapExcursion = hashMapOf<String, Any>()
            mapExcursion["title"] = excursion.title
            mapExcursion["photos"] = excursion.photos
            mapExcursion["description"] = excursion.description
            mapExcursion["price"] = excursion.price ?: 0
            mapExcursion["placemarks"] = excursion.placemarks
            mapExcursion["waypoints"] = excursion.waypoints
            db.collection(EXCURSIONS_COLLECTION).add(mapExcursion).await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}
