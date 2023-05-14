package ru.walkom.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.walkom.app.domain.repository.ExcursionRepository
import ru.walkom.app.domain.use_case.DownloadFilesExcursionUseCase
import ru.walkom.app.domain.use_case.GetExcursionByIdUseCase
import ru.walkom.app.domain.use_case.GetExcursionsUseCase
import ru.walkom.app.domain.use_case.UploadExcursionUseCase

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
    fun provideDownloadFilesExcursionUseCase(repository: ExcursionRepository): DownloadFilesExcursionUseCase {
        return DownloadFilesExcursionUseCase(repository = repository)
    }

    @Provides
    fun provideUploadExcursionUseCase(repository: ExcursionRepository): UploadExcursionUseCase {
        return UploadExcursionUseCase(repository = repository)
    }
}