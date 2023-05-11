package ru.walkom.app.data.storage

import android.content.Context
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.walkom.app.domain.model.Response
import java.io.File


class ExcursionStorageImpl(
    private val storage: StorageReference,
    private val context: Context
): ExcursionStorage {

    override fun downloadAudioExcursion(id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            for (i in 0..6) {
                val fileRef = storage.child("excursions/${id}/audios/${i}.mp3")
                val file = File(context.filesDir, "QQ4oHDyYxtOme3Nu2VFq_${i}")
                fileRef.getFile(file).await()

//                fileRef.metadata.addOnCompleteListener {
//                    Log.i(TAG, it.result.sizeBytes.toString())
//                }
            }

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun downloadModelsExcursion(id: String) = flow<Response<Boolean>> {
        try {
            emit(Response.Loading)

            val fileRef = storage.child("excursions/${id}/models/diligense.glb")
            val file = File(context.filesDir, "QQ4oHDyYxtOme3Nu2VFq_diligense")
            fileRef.getFile(file).await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun getSizeDataExcursion(id: String) = flow<Response<Int>> {
        try {
            emit(Response.Loading)

            val fileRef = storage.child("excursions/${id}/models/diligense.glb")
            val result = fileRef.metadata.await()

            emit(Response.Success((result.sizeBytes / 1024).toInt()))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}