package ru.walkom.app.domain.repository

import ru.walkom.app.domain.model.Excursion

interface ExcursionRepository {
    fun getExcursions(): List<Excursion>
    fun getExcursionById(id: String): Excursion
}