package ru.walkom.app.common

import ru.walkom.app.presentation.screens.MainActivity

object Constants {

    // System
    lateinit var APP_ACTIVITY: MainActivity
    const val TAG = "AppLog"

    // Errors
    const val ERROR_INVALID_ERROR = "Неверная почта"
    const val ERROR_DRAW_ROUTE = "Ошибка построения маршрута"

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

    // Folders data
    const val FOLDER_AUDIO = "audios"
    const val FOLDER_MODELS = "models"

    // Fragment arguments
    const val ARGUMENT_EXCURSION = "excursion"
    const val ARGUMENT_EXCURSION_ID = "excursionId"

    // Firebase Firestore Database
    const val EXCURSIONS_COLLECTION = "excursions"
    const val TITLE_FIELD = "title"
    const val PHOTOS_FIELD = "photos"
    const val DESCRIPTION_FIELD = "description"
    const val PRICE_FIELD = "price"
    const val PLACEMARKS_FIELD = "placemarks"
    const val WAYPOINTS_FIELD = "waypoints"
    const val MODELS_FIELD = "models"

    // Firebase storage
    const val EXCURSIONS_BUCKET = "excursions"

    // Yandex MapKit
    const val MAPKIT_API_KEY = "4e10e9f2-d783-499c-b77d-8fc64489b4ac"
}