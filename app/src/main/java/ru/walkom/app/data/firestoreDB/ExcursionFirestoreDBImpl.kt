package ru.walkom.app.data.firestoreDB

import com.google.firebase.firestore.FirebaseFirestore
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.walkom.app.R
import ru.walkom.app.common.Constants.EXCURSIONS_COLLECTION
import ru.walkom.app.domain.model.ExcursionItem
import ru.walkom.app.domain.model.ExcursionOpen
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.Response
import ru.walkom.app.domain.model.Waypoint

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

    override fun getExcursionById(id: String) = flow<Response<ExcursionOpen?>> {
        try {
            emit(Response.Loading)

            val excursion = db.collection(EXCURSIONS_COLLECTION)
                .document(id)
                .get().await()
                .toObject(ExcursionOpen::class.java)

            excursion?.let {
                it.id = id
            }

            emit(Response.Success(excursion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun getPlacemarksExcursion(id: String): List<Placemark> {
        return listOf<Placemark>(
            Placemark(
                1,
                Point(58.037188, 56.124989),
                "Пермский медведь",
                R.drawable.bear,
                false
            ),
            Placemark(
                2,
                Point(58.036989, 56.124917),
                "Гимназия № 17",
                R.drawable.gymnasium_17,
                false
            ),
            Placemark(
                3,
                Point(58.036841, 56.126014),
                "Рождественско-Богородицкая церковь",
                R.drawable.church,
                false
            ),
            Placemark(
                4,
                Point(58.036672, 56.127291),
                "Пермский государственный институт культуры",
                R.drawable.perm_state_institute_culture,
                false
            ),
            Placemark(
                5,
                Point(58.036879, 56.127382),
                "Дом пекарня наследника Демидовых",
                R.drawable.house_demidovs,
                false
            ),
            Placemark(
                6,
                Point(58.037025, 56.126263),
                "Триумф. Пермский кинотеатр",
                R.drawable.triumph,
                false
            )
        )

//        return listOf<Placemark>(
//            Placemark(
//                1,
//                Point(58.010418, 56.237335),
//                "Пермский медведь",
//                R.drawable.bear,
//                false
//            ),
//            Placemark(
//                2,
//                Point(58.012611, 56.242274),
//                "Гимназия № 17",
//                R.drawable.gymnasium_17,
//                false
//            ),
//            Placemark(
//                3,
//                Point(58.012248, 56.242676),
//                "Рождественско-Богородицкая церковь",
//                R.drawable.church,
//                false
//            ),
//            Placemark(
//                4,
//                Point(58.013174, 56.243045),
//                "Пермский государственный институт культуры",
//                R.drawable.perm_state_institute_culture,
//                false
//            ),
//            Placemark(
//                5,
//                Point(58.012553, 56.243556),
//                "Дом пекарня наследника Демидовых",
//                R.drawable.house_demidovs,
//                false
//            ),
//            Placemark(
//                6,
//                Point(58.012758, 56.244125),
//                "Триумф. Пермский кинотеатр",
//                R.drawable.triumph,
//                false
//            )
//        )
    }

    override fun getWaypointsExcursion(id: String): List<Waypoint> {
        return listOf<Waypoint>(
            Waypoint(
                1,
                Point(58.037188, 56.124989),
                "R.raw.guide_r2_2",
                1,
                false
            ),
            Waypoint(
                2,
                Point(58.036989, 56.124917),
                "R.raw.guide_r2_4",
                null,
                false
            ),
            Waypoint(
                3,
                Point(58.036841, 56.126014),
                "R.raw.guide_r2_5",
                3,
                false
            ),
            Waypoint(
                4,
                Point(58.036672, 56.127291),
                "R.raw.guide_r2_6",
                4,
                false
            ),
            Waypoint(
                5,
                Point(58.036879, 56.127382),
                "R.raw.guide_r2_7",
                5,
                false
            ),
            Waypoint(
                6,
                Point(58.037025, 56.126263),
                "R.raw.guide_r2_9",
                6,
                false
            )
        )

//        return listOf<Waypoint>(
//            Waypoint(
//                1,
//                Point(58.010433, 56.237325),
//                R.raw.guide_r2_2,
//                1,
//                false
//            ),
//            Waypoint(
//                2,
//                Point(58.010934, 56.237625),
//                R.raw.guide_r2_4,
//                null,
//                false
//            ),
//            Waypoint(
//                3,
//                Point(58.011374, 56.239166),
//                R.raw.guide_r2_5,
//                null,
//                false
//            ),
//            Waypoint(
//                4,
//                Point(58.012153, 56.241898),
//                null,
//                3,
//                false
//            ),
//            Waypoint(
//                5,
//                Point(58.012477, 56.243069),
//                R.raw.guide_r2_6,
//                4,
//                false
//            ),
//            Waypoint(
//                6,
//                Point(58.012575, 56.243399),
//                R.raw.guide_r2_7,
//                5,
//                false
//            ),
//            Waypoint(
//                7,
//                Point(58.012757, 56.244027),
//                R.raw.guide_r2_9,
//                6,
//                false
//            )
//        )
    }
}