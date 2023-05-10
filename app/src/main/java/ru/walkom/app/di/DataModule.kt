package ru.walkom.app.di

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.walkom.app.common.Constants.ACCESS_KEY_S3
import ru.walkom.app.common.Constants.SECRET_ACCESS_KEY_S3
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDB
import ru.walkom.app.data.firestoreDB.ExcursionFirestoreDBImpl
import ru.walkom.app.data.repository.ExcursionRepositoryImpl
import ru.walkom.app.data.storage_s3.ExcursionStorageS3
import ru.walkom.app.data.storage_s3.ExcursionStorageS3Impl
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
    fun provideAWSS3(
        @ApplicationContext context: Context
    ): TransferUtility {
        val credentials = BasicAWSCredentials(ACCESS_KEY_S3, SECRET_ACCESS_KEY_S3)
        val s3Client = AmazonS3Client(credentials)

        return TransferUtility.builder()
            .defaultBucket("37399227-3d832f77-13e3-4864-9f21-46a4f4e85dce")
            .context(context)
//            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3Client)
            .build()
    }

    @Provides
    @Singleton
    fun provideExcursionFirestoreDB(
        db: FirebaseFirestore
    ): ExcursionFirestoreDB {
        return ExcursionFirestoreDBImpl(db = db)
    }

    @Provides
    @Singleton
    fun provideExcursionStorageS3(
        transferUtility: TransferUtility,
        @ApplicationContext context: Context
    ): ExcursionStorageS3 {
        return ExcursionStorageS3Impl(s3 = transferUtility, context = context)
    }

    @Provides
    @Singleton
    fun provideExcursionRepository(
        excursionFirestoreDB: ExcursionFirestoreDB,
        excursionStorageS3: ExcursionStorageS3
    ): ExcursionRepository {
        return ExcursionRepositoryImpl(
            excursionFirestoreDB = excursionFirestoreDB,
            excursionStorageS3 = excursionStorageS3
        )
    }
}