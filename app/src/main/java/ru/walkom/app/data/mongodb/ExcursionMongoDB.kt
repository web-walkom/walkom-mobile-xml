package ru.walkom.app.data.mongodb

import ru.walkom.app.domain.model.Excursion

interface ExcursionMongoDB {
    fun getExcursions(): List<Excursion>
    fun getExcursionById(id: String): Excursion
}