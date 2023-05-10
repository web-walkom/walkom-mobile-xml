package ru.walkom.app.data.storage_s3

import kotlinx.coroutines.flow.Flow
import ru.walkom.app.domain.model.Response

interface ExcursionStorageS3 {
    fun downloadAudioExcursion(id: String): Flow<Response<Boolean>>
}