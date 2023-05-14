package ru.walkom.app.data.storage

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.Response

interface ExcursionStorage {
    fun downloadAudioExcursion(id: String): Flow<Response<Boolean>>

    fun downloadModelsExcursion(id: String): Flow<Response<Boolean>>

    fun getSizeFilesExcursion(id: String): Flow<Response<Int>>
}