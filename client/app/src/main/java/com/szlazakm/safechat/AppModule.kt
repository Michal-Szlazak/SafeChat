package com.szlazakm.safechat

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.EncryptionSessionRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.utils.auth.EncryptedMessageSender
import com.szlazakm.safechat.utils.auth.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.EncryptedMessageReceiver
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class, ActivityComponent::class)
class AppModule {

    @Provides
    fun provideContactRepository(context: Context): ContactRepository {
        return ContactRepository(context)
    }

    @Provides
    fun provideMessageRepository(context: Context): MessageRepository {
        return MessageRepository(context)
    }

    @Provides
    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository(context)
    }

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.67.39.166:8080")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesPreKeyRepository(context: Context): PreKeyRepository {
        return PreKeyRepository(context)
    }

    @Provides
    fun providesPreKeyService(preKeyRepository: PreKeyRepository): PreKeyService {
        return PreKeyService(preKeyRepository)
    }

    @Provides
    fun providesPreKeyWebService(retrofit: Retrofit): PreKeyWebService {
        return retrofit.create(PreKeyWebService::class.java)
    }

    @Provides
    fun providesPreKeyManager(
        userRepository: UserRepository,
        preKeyService: PreKeyService,
        preKeyWebService: PreKeyWebService
    ): PreKeyManager {
        return PreKeyManager(userRepository, preKeyService, preKeyWebService)
    }

    @Provides
    fun provideUserWebService(retrofit: Retrofit) : UserWebService{
        return retrofit.create(UserWebService::class.java)
    }

    @Provides
    fun providesEncryptionSessionInitializer(
        userWebService: UserWebService,
        userRepository: UserRepository
    ) : AliceEncryptionSessionInitializer {
        return AliceEncryptionSessionInitializer(userWebService, userRepository)
    }

    @Provides
    fun provideEncryptedMessageSender(
        aliceEncryptionSessionInitializer: AliceEncryptionSessionInitializer,
        encryptionSessionRepository: EncryptionSessionRepository
    ): EncryptedMessageSender {
        return EncryptedMessageSender(aliceEncryptionSessionInitializer, encryptionSessionRepository)
    }

    @Provides
    fun provideEncryptionSessionRepository(context: Context): EncryptionSessionRepository {
        return EncryptionSessionRepository(context)
    }

    @Provides
    fun provideEncryptedMessageReceiver(
        encryptionSessionRepository: EncryptionSessionRepository,
        bobDecryptionSessionInitializer: BobDecryptionSessionInitializer
    ): EncryptedMessageReceiver {
        return EncryptedMessageReceiver(encryptionSessionRepository, bobDecryptionSessionInitializer)
    }

    @Provides
    fun provideBobDecryptionSessionInitializer(
        userRepository: UserRepository,
        preKeyRepository: PreKeyRepository
    ): BobDecryptionSessionInitializer {
        return BobDecryptionSessionInitializer(userRepository, preKeyRepository)
    }

    @Provides
    fun provideMessageSaverManager(context: Context): MessageSaverManager {
        return MessageSaverManager(context)
    }

}