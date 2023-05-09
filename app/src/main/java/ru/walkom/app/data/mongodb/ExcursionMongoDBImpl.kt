package ru.walkom.app.data.mongodb

import android.util.Log
import com.yandex.mapkit.geometry.Point
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.walkom.app.R
import ru.walkom.app.common.Constants.APP_ID
import ru.walkom.app.common.Constants.TAG
import ru.walkom.app.domain.model.Excursion
import ru.walkom.app.domain.model.ExcursionRealm
import ru.walkom.app.domain.model.Placemark
import ru.walkom.app.domain.model.PlacemarkRealm
import ru.walkom.app.domain.model.Waypoint
import ru.walkom.app.domain.model.WaypointRealm

class ExcursionMongoDBImpl: ExcursionMongoDB {
    private val appService by lazy {
        val appConfig = AppConfiguration.Builder(appId = APP_ID).log(LogLevel.ALL).build()
        App.create(appConfig)
    }
    private val realm by lazy {
        val user = appService.currentUser!!

        val config = SyncConfiguration.Builder(
            user = user,
            schema = setOf(ExcursionRealm::class, PlacemarkRealm::class, WaypointRealm::class)
        )
            .name("Walkom")
            .schemaVersion(1)
            .build()
        Realm.open(config)
    }

//    private var mongoClient: CoroutineClient
//    private var mongoDatabase: CoroutineDatabase
//    private var mongoCollection: CoroutineCollection<ExcursionRealm>

    init {
//        mongoClient = KMongo.createClient("mongodb+srv://b0shka:mhV8yWn7NPvJJfxNZXhRQkyVNsUBVgUQCxaJIIYq8YW4KvyevjyA9cChpj1enhnaxGLSEv2dcpb9RcRoQPj5xVbOD4vLvCaww4xN1oZ6BwtWvHAzfjpv4lqTqPNLtsIP@cluster0.fmnivkg.mongodb.net/?retryWrites=true&w=majority").coroutine
//        mongoClient = KMongo.createClient().coroutine
//        mongoDatabase = mongoClient.getDatabase("Walkom")
//        mongoCollection = mongoDatabase.getCollection("excursions")
//        configureRealm()

//        runBlocking {
//            appService.login(Credentials.anonymous())
//        }
    }

    override fun configureRealm() {
//        val config = SyncConfiguration.Builder(app.currentUser(), "my-realm")
//            .allowQueriesOnUiThread(true)
//            .allowWritesOnUiThread(true)
//            .build()
//        realm = Realm.getInstance(config)

//        app.loginAsync(Credentials.anonymous()) {
//            if (it.isSuccess) {
//                Log.i(TAG, "Logged in anonymously")
//                user = app.currentUser()
//                if (user != null) {
//                    mongoClient = user!!.getMongoClient("mongodb-atlas")
//                    mongoDatabase = mongoClient.getDatabase("Walkom")
//                    mongoCollection = mongoDatabase.getCollection("excursions")
//                }
//            }
//            else {
//                Log.i(TAG, "Failed to login")
//                Log.i(TAG, it.error.message.toString())
//            }
//        }

//        realm = runBlocking {
//            val user = app.login(Credentials.anonymous())
//            Log.i(TAG, user.toString())
//            val config = SyncConfiguration.Builder(
//                user = user,
//                schema = setOf(ExcursionRealm::class, PlacemarkRealm::class, WaypointRealm:: class)
//            )
//                .build()
//            Realm.open(config)
//        }
    }

    override fun getExcursions(): Flow<List<ExcursionRealm>> {
//        val excursions = mongoCollection.find()
//        val excursions = realm.where<ExcursionRealm>().findAll()
//        Log.i(TAG, excursions.toString())
        return realm.query<ExcursionRealm>().asFlow().map { it.list }
    }

    override fun getExcursionById(id: String): Excursion {
        return Excursion(
            "63fb0fdc89ca647e7f10804b",
            "Keeper. Театрализованные экскурсии по Перми",
            "Знакомьтесь! Это Keeper - хранитель времени! Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            0,
            "/excursions/63fb0fdc89ca647e7f10804b/1.jpg"
        )
        //return realm.query<ExcursionRealm>().asFlow().map { it.list }
    }

    override fun getPlacemarksExcursion(id: String): List<Placemark> {
//        return listOf<Placemark>(
//            Placemark(
//                1,
//                Point(58.037188, 56.124989),
//                "Пермский медведь",
//                R.drawable.bear,
//                false
//            ),
//            Placemark(
//                2,
//                Point(58.036989, 56.124917),
//                "Гимназия № 17",
//                R.drawable.gymnasium_17,
//                false
//            ),
//            Placemark(
//                3,
//                Point(58.036841, 56.126014),
//                "Рождественско-Богородицкая церковь",
//                R.drawable.church,
//                false
//            ),
//            Placemark(
//                4,
//                Point(58.036672, 56.127291),
//                "Пермский государственный институт культуры",
//                R.drawable.perm_state_institute_culture,
//                false
//            ),
//            Placemark(
//                5,
//                Point(58.036879, 56.127382),
//                "Дом пекарня наследника Демидовых",
//                R.drawable.house_demidovs,
//                false
//            ),
//            Placemark(
//                6,
//                Point(58.037025, 56.126263),
//                "Триумф. Пермский кинотеатр",
//                R.drawable.triumph,
//                false
//            )
//        )

        return listOf<Placemark>(
            Placemark(
                1,
                Point(58.010418, 56.237335),
                "Пермский медведь",
                R.drawable.bear,
                false
            ),
            Placemark(
                2,
                Point(58.012611, 56.242274),
                "Гимназия № 17",
                R.drawable.gymnasium_17,
                false
            ),
            Placemark(
                3,
                Point(58.012248, 56.242676),
                "Рождественско-Богородицкая церковь",
                R.drawable.church,
                false
            ),
            Placemark(
                4,
                Point(58.013174, 56.243045),
                "Пермский государственный институт культуры",
                R.drawable.perm_state_institute_culture,
                false
            ),
            Placemark(
                5,
                Point(58.012553, 56.243556),
                "Дом пекарня наследника Демидовых",
                R.drawable.house_demidovs,
                false
            ),
            Placemark(
                6,
                Point(58.012758, 56.244125),
                "Триумф. Пермский кинотеатр",
                R.drawable.triumph,
                false
            )
        )
    }

    override fun getWaypointsExcursion(id: String): List<Waypoint> {
//        return listOf<Waypoint>(
//            Waypoint(
//                1,
//                Point(58.037188, 56.124989),
//                R.raw.guide_r2_2,
//                1,
//                false
//            ),
//            Waypoint(
//                2,
//                Point(58.036989, 56.124917),
//                R.raw.guide_r2_4,
//                null,
//                false
//            ),
//            Waypoint(
//                3,
//                Point(58.036841, 56.126014),
//                R.raw.guide_r2_5,
//                3,
//                false
//            ),
//            Waypoint(
//                4,
//                Point(58.036672, 56.127291),
//                R.raw.guide_r2_6,
//                4,
//                false
//            ),
//            Waypoint(
//                5,
//                Point(58.036879, 56.127382),
//                R.raw.guide_r2_7,
//                5,
//                false
//            ),
//            Waypoint(
//                6,
//                Point(58.037025, 56.126263),
//                R.raw.guide_r2_9,
//                6,
//                false
//            )
//        )

        return listOf<Waypoint>(
            Waypoint(
                1,
                Point(58.010433, 56.237325),
                R.raw.guide_r2_2,
                1,
                false
            ),
            Waypoint(
                2,
                Point(58.010934, 56.237625),
                R.raw.guide_r2_4,
                null,
                false
            ),
            Waypoint(
                3,
                Point(58.011374, 56.239166),
                R.raw.guide_r2_5,
                null,
                false
            ),
            Waypoint(
                4,
                Point(58.012153, 56.241898),
                null,
                3,
                false
            ),
            Waypoint(
                5,
                Point(58.012477, 56.243069),
                R.raw.guide_r2_6,
                4,
                false
            ),
            Waypoint(
                6,
                Point(58.012575, 56.243399),
                R.raw.guide_r2_7,
                5,
                false
            ),
            Waypoint(
                7,
                Point(58.012757, 56.244027),
                R.raw.guide_r2_9,
                6,
                false
            )
        )
    }
}