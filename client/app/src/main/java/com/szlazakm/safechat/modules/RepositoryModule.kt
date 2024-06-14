package com.szlazakm.safechat.modules

import android.app.Application
import android.content.Context
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.EncryptionSessionRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class, ActivityComponent::class)
class RepositoryModule {

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
    fun providesPreKeyRepository(context: Context): PreKeyRepository {
        return PreKeyRepository(context)
    }

    @Provides
    fun provideEncryptionSessionRepository(context: Context): EncryptionSessionRepository {
        return EncryptionSessionRepository(context)
    }
}