package ru.walkom.app.data.storage_s3

import android.content.Context
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import kotlinx.coroutines.flow.flow
import ru.walkom.app.domain.model.Response
import java.io.File


class ExcursionStorageS3Impl(
    private val s3: TransferUtility,
    private val context: Context
): ExcursionStorageS3 {

    override fun downloadAudioExcursion(id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            s3.download("/excursions/${id}/audios/0.mp3", File(context.filesDir, "${id}_0.mp3"))
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}