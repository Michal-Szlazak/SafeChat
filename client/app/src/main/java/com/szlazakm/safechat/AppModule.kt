package com.szlazakm.safechat

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.MessageRepository
import com.szlazakm.safechat.client.data.Repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
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
            .baseUrl("http://192.168.0.230:8080")
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
}