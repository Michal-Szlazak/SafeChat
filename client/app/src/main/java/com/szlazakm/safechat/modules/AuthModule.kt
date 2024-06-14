package com.szlazakm.safechat.modules

import com.szlazakm.safechat.client.data.repositories.EncryptionSessionRepository
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.utils.auth.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.EncryptedMessageReceiver
import com.szlazakm.safechat.utils.auth.EncryptedMessageSender
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class, ActivityComponent::class)
class AuthModule {

    @Provides
    fun providesPreKeyService(preKeyRepository: PreKeyRepository): PreKeyService {
        return PreKeyService(preKeyRepository)
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
}