package ru.walkom.app.common

object Constants {

    // Errors
    const val ERROR_INVALID_ERROR = "Неверная почта"
    const val ERROR_DRAW_ROUTE = "Ошибка построения маршрута"
    const val ERROR_DETECT_LOCATION = "Ошибка при определении местоположения"
    const val UNKNOWN_ERROR = "Неизвестная ошибка"

    // Notifications
    const val NOTIFICATION_CONDITIONS_START_TOUR = "Для начала экскурсии встаньте возле стартовой метки"
    const val NOTIFICATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, вернитесь или экскурсия будет завершена"
    const val NOTIFICATION_TERMINATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, экскурсия была завершена"

    // Distances
    const val DISTANCE_CONTAINS_START_POINT = 0.02
    const val DISTANCE_CONTAINS_PLACEMARK = 0.015
    const val DISTANCE_CONTAINS_WAYPOINT = 0.015
    const val DISTANCE_CONTAINS_ROUTE = 0.1
    const val DISTANCE_CONTAINS_ROUTE_EXTREME = 0.15

    // Texts
    const val TEXT_START = "Начало"
    const val TEXT_START_EXCURSION = "Начало экскурсии"
    const val DESCRIPTION_START_EXCURSION = "Встаньте у стартовой метки"
    const val TEXT_INTRODUCTION_EXCURSION = "Вступление"
    const val DESCRIPTION_INTRODUCTION_EXCURSION = "Внимательно послушайте"

    // Other
    const val TAG = "AppLog"
}