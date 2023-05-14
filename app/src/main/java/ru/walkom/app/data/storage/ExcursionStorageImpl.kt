package ru.walkom.app.data.storage

import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.common.Constants.EXCURSIONS_BUCKET
import ru.walkom.app.common.Constants.FOLDER_AUDIO
import ru.walkom.app.common.Constants.FOLDER_MODELS
import ru.walkom.app.domain.model.Response
import java.io.File


class ExcursionStorageImpl(
    private val storage: StorageReference
): ExcursionStorage {

    override fun downloadAudioExcursion(id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            val folder = File("${APP_ACTIVITY.filesDir}/${id}", FOLDER_AUDIO)
            if (!folder.exists())
                folder.mkdir()

            val audios = storage.child("${EXCURSIONS_BUCKET}/${id}/${FOLDER_AUDIO}").listAll().await()
            audios.items.forEach { audio ->
                val fileRef = storage.child(audio.path)
                val file = File(folder, audio.name)
                fileRef.getFile(file).await()
            }

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun downloadModelsExcursion(id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            val folder = File("${APP_ACTIVITY.filesDir}/${id}", FOLDER_MODELS)
            if (!folder.exists())
                folder.mkdir()

            val models = storage.child("${EXCURSIONS_BUCKET}/${id}/${FOLDER_MODELS}").listAll().await()
            models.items.forEach { model ->
                val fileRef = storage.child(model.path)
                val file = File(folder, model.name)
                fileRef.getFile(file).await()
            }

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun getSizeFilesExcursion(id: String) = flow<Response<Int>> {
        try {
            emit(Response.Loading)
            var size = 0

            val audios = storage.child("${EXCURSIONS_BUCKET}/${id}/${FOLDER_AUDIO}").listAll().await()
            audios.items.forEach { audio ->
                val fileRef = storage.child(audio.path)
                val result = fileRef.metadata.await()
                size += (result.sizeBytes / 1024).toInt()
            }

            val models = storage.child("${EXCURSIONS_BUCKET}/${id}/${FOLDER_MODELS}").listAll().await()
            models.items.forEach { model ->
                val fileRef = storage.child(model.path)
                val result = fileRef.metadata.await()
                size += (result.sizeBytes / 1024).toInt()
            }

            emit(Response.Success(size / 1024))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}