package ru.walkom.app.domain.model

data class SliderModel(
    val id: Int,
    val image: String
) {
    constructor():this(
        0, ""
    )
}
