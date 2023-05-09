package ru.walkom.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDBImpl
import ru.walkom.app.data.repository.ExcursionRepositoryImpl
import ru.walkom.app.domain.repository.ExcursionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideExcursionMongoDB(
        db: FirebaseFirestore
    ): ExcursionFirestoreDB {
        return ExcursionFirestoreDBImpl(db = db)
    }

    @Provides
    @Singleton
    fun provideExcursionRepository(excursionFirestoreDB: ExcursionFirestoreDB): ExcursionRepository {
        return ExcursionRepositoryImpl(excursionFirestoreDB = excursionFirestoreDB)
    }
}