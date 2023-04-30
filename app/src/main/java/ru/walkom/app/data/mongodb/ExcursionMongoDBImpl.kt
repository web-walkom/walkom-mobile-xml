package ru.walkom.app.data.mongodb

import ru.walkom.app.domain.model.Excursion

class ExcursionMongoDBImpl: ExcursionMongoDB {

    override fun getExcursions(): List<Excursion> {
        return listOf<Excursion>(
            Excursion(
                "63fb0fdc89ca647e7f10804b",
                "Keeper. Театрализованные экскурсии по Перми",
                "Знакомьтесь! Это Keeper - хранитель времени!\n" +
                        "Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
                0,
                "/excursions/63fb0fdc89ca647e7f10804b/1.jpg"
            )
        )
    }

    override fun getExcursionById(id: String): Excursion {
        return Excursion(
            "63fb0fdc89ca647e7f10804b",
            "Keeper. Театрализованные экскурсии по Перми",
            "Знакомьтесь! Это Keeper - хранитель времени!\n" +
                    "Keeper - собирает легенды города Перми и расскажет вам тайны, секреты и истории, свидетелем которых был 100-200 лет назад.",
            0,
            "/excursions/63fb0fdc89ca647e7f10804b/1.jpg"
        )
    }
}