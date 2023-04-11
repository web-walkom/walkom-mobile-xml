package ru.walkom.app.common

object Constants {

    // Errors
    const val ERROR_INVALID_ERROR = "Неверная почта"
    const val ERROR_DRAW_ROUTE = "Ошибка построения маршрута"
    const val ERROR_DETECT_LOCATION = "Ошибка при определении местоположения"
    const val UNKNOWN_ERROR = "Неизвестная ошибка"

    // Notifications
    const val NOTIFICATION_FAILED_DETECT_LOCATION = "Не удалось определить местоположение"
    const val NOTIFICATION_CONDITIONS_START_TOUR = "Для начала экскурсии встаньте возле стартовой метки"
    const val NOTIFICATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, вернитесь или экскурсия будет завершена"
    const val NOTIFICATION_TERMINATION_DEVIATION_ROUTE = "Вы отклонились от маршрута, экскурсия была завершена"

    // Other
    const val TAG = "AppLog"
}