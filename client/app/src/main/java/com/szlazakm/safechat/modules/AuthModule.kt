package com.szlazakm.safechat.modules

import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.EphemeralRatchetKeyPairRepository
import com.szlazakm.safechat.client.data.repositories.IdentityKeyRepository
import com.szlazakm.safechat.client.data.repositories.MessageKeysRepository
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.repositories.ReceiverChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.RootKeyRepository
import com.szlazakm.safechat.client.data.repositories.SenderChainKeyRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.utils.auth.alice.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.bob.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.MessageDecryptor
import com.szlazakm.safechat.utils.auth.MessageEncryptor
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
        userRepository: UserRepository,
        aliceEncryptionSessionInitializer: AliceEncryptionSessionInitializer,
        rootKeyRepository: RootKeyRepository,
        senderChainKeyRepository: SenderChainKeyRepository,
        ephemeralRatchetKeyPairRepository: EphemeralRatchetKeyPairRepository,
        identityKeyRepository: IdentityKeyRepository,
        contactRepository: ContactRepository
    ): MessageEncryptor {
        return MessageEncryptor(
            userRepository,
            aliceEncryptionSessionInitializer,
            rootKeyRepository,
            senderChainKeyRepository,
            ephemeralRatchetKeyPairRepository,
            identityKeyRepository,
            contactRepository
        )
    }

    @Provides
    fun provideMessageDecryptor(
        userRepository: UserRepository,
        bobDecryptionSessionInitializer: BobDecryptionSessionInitializer,
        rootKeyRepository: RootKeyRepository,
        receiverChainKeyRepository: ReceiverChainKeyRepository,
        senderChainKeyRepository: SenderChainKeyRepository,
        messageKeysRepository: MessageKeysRepository,
        identityKeyRepository: IdentityKeyRepository,
        ephemeralRatchetKeyPairRepository: EphemeralRatchetKeyPairRepository
    ): MessageDecryptor {
        return MessageDecryptor(
            userRepository,
            bobDecryptionSessionInitializer,
            rootKeyRepository,
            receiverChainKeyRepository,
            senderChainKeyRepository,
            messageKeysRepository,
            identityKeyRepository,
            ephemeralRatchetKeyPairRepository
        )
    }

    @Provides
    fun provideBobDecryptionSessionInitializer(
        userRepository: UserRepository,
        preKeyRepository: PreKeyRepository
    ): BobDecryptionSessionInitializer {
        return BobDecryptionSessionInitializer(userRepository, preKeyRepository)
    }
}