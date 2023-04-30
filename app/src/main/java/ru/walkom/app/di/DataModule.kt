package ru.walkom.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.walkom.app.data.mongodb.ExcursionMongoDB
import ru.walkom.app.data.mongodb.ExcursionMongoDBImpl
import ru.walkom.app.data.repository.ExcursionRepositoryImpl
import ru.walkom.app.domain.repository.ExcursionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideExcursionMongoDB(): ExcursionMongoDB = ExcursionMongoDBImpl()

    @Provides
    @Singleton
    fun provideExcursionRepository(excursionMongoDB: ExcursionMongoDB): ExcursionRepository {
        return ExcursionRepositoryImpl(excursionMongoDB = excursionMongoDB)
    }
}