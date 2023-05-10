package ru.walkom.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.walkom.app.domain.repository.ExcursionRepository
import ru.walkom.app.domain.use_case.DownloadAudioExcursionUseCase
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import ru.walkom.app.domain.use_case.GetPlacemarksExcursionUseCase
import ru.walkom.app.domain.use_case.GetWaypointsExcursionUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideGetExcursionsUseCase(repository: ExcursionRepository): GetExcursionsUseCase {
        return GetExcursionsUseCase(repository = repository)
    }

    @Provides
    fun provideGetExcursionByIdUseCase(repository: ExcursionRepository): GetExcursionByIdUseCase {
        return GetExcursionByIdUseCase(repository = repository)
    }

    @Provides
    fun provideGetPlacemarksExcursionUseCase(repository: ExcursionRepository): GetPlacemarksExcursionUseCase {
        return GetPlacemarksExcursionUseCase(repository = repository)
    }

    @Provides
    fun provideGetWaypointsExcursionUseCase(repository: ExcursionRepository): GetWaypointsExcursionUseCase {
        return GetWaypointsExcursionUseCase(repository = repository)
    }

    @Provides
    fun provideDownloadAudioExcursionUseCase(repository: ExcursionRepository): DownloadAudioExcursionUseCase {
        return DownloadAudioExcursionUseCase(repository = repository)
    }
}