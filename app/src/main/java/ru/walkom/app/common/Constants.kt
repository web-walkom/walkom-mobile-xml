package ru.walkom.app.common

object Constants {
    // Errors
    const val ERROR_INVALID_ERROR = "Неверная почта"
    const val ERROR_DRAW_ROUTE = "Ошибка построения маршрута"
    const val ERROR_DETECT_LOCATION = "Ошибка при определении местоположения"
    const val ERROR_UNKNOWN= "Неизвестная ошибка"

    // Notifications
    const val NOTIFICATION_CONDITIONS_START_TOUR = "Для начала экскурсии встаньте возле стартовой метки"
    const val NOTIFICATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, вернитесь или экскурсия будет завершена"
    const val NOTIFICATION_TERMINATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, экскурсия была завершена"

    // Distances
    const val DISTANCE_CONTAINS_START_POINT = 0.015
    const val DISTANCE_CONTAINS_WAYPOINT = 0.01
    const val DISTANCE_CONTAINS_ROUTE = 0.1
    const val DISTANCE_CONTAINS_ROUTE_EXTREME = 0.15

    // Texts
    const val TEXT_START = "Начало"
    const val TEXT_START_EXCURSION = "Начало экскурсии"
    const val DESCRIPTION_START_EXCURSION = "Встаньте у стартовой метки"
    const val TEXT_INTRODUCTION_EXCURSION = "Вступление"
    const val DESCRIPTION_INTRODUCTION_EXCURSION = "Внимательно послушайте"

    // Buttons
    const val BUTTON_LOAD_EXCURSION = "Скачать"
    const val BUTTON_RUN_EXCURSION = "Запустить"

    // FirestoreDB
    const val EXCURSIONS_COLLECTION = "excursions"
    const val PLACEMARKS_FIELD = "placemarks"
    const val WAYPOINTS_FIELD = "waypoints"

    // AWS S3
    const val ACCESS_KEY_S3 = "cp65795"
    const val SECRET_ACCESS_KEY_S3 = "08de18800fc1d95112e38acfd1a92c62"

    // Yandex MapKit
    val MAPKIT_API_KEY = "4e10e9f2-d783-499c-b77d-8fc64489b4ac"

    // Other
    const val TAG = "AppLog"
}