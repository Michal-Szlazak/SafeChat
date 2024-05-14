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
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides
    fun provideMessageSaverManager(context: Context): MessageSaverManager {
        return MessageSaverManager(context)
    }

}