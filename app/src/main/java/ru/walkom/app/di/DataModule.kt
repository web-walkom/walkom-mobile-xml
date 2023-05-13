package ru.walkom.app.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDBImpl
import ru.walkom.app.data.repository.ExcursionRepositoryImpl
import ru.walkom.app.data.storage.ExcursionStorage
import ru.walkom.app.data.storage.ExcursionStorageImpl
import ru.walkom.app.domain.repository.ExcursionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideStorageReference() = FirebaseStorage.getInstance().reference

    @Provides
    @Singleton
    fun provideExcursionFirestoreDB(
        db: FirebaseFirestore
    ): ExcursionFirestoreDB {
        return ExcursionFirestoreDBImpl(db = db)
    }

    @Provides
    @Singleton
    fun provideExcursionStorage(
        storage: StorageReference
    ): ExcursionStorage {
        return ExcursionStorageImpl(storage = storage)
    }

    @Provides
    @Singleton
    fun provideExcursionRepository(
        excursionFirestoreDB: ExcursionFirestoreDB,
        excursionStorage: ExcursionStorage
    ): ExcursionRepository {
        return ExcursionRepositoryImpl(
            excursionFirestoreDB = excursionFirestoreDB,
            excursionStorage = excursionStorage
        )
    }
}