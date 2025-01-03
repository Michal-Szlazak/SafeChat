package com.szlazakm.safechat.modules

import android.app.Application
import android.content.Context
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.EphemeralRatchetKeyPairRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.MessageKeysRepository
import com.szlazakm.safechat.client.data.repositories.MessageRepository
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.ReceiverChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.RootKeyRepository
import com.szlazakm.safechat.client.data.repositories.SenderChainKeyRepository
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
    fun provideRootKeyRepository(context: Context): RootKeyRepository {
        return RootKeyRepository(context)
    }

    @Provides
    fun provideSenderChainKeyRepository(context: Context): SenderChainKeyRepository {
        return SenderChainKeyRepository(context)
    }

    @Provides
    fun provideReceiverChainKeyRepository(context: Context): ReceiverChainKeyRepository {
        return ReceiverChainKeyRepository(context)
    }

    @Provides
    fun provideEphemeralRatchetKeyPairRepository(context: Context): EphemeralRatchetKeyPairRepository {
        return EphemeralRatchetKeyPairRepository(context)
    }

    @Provides
    fun provideMessageKeysRepository(context: Context): MessageKeysRepository {
        return MessageKeysRepository(context)
    }

    @Provides
    fun providesIdentityKeyRepository(context: Context): IdentityKeyRepository {
        return IdentityKeyRepository(context)
    }
}