package com.szlazakm.safechat

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.MessageRepository
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.webclient.services.MessageSaverService
import com.szlazakm.safechat.webclient.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
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
    fun provideMessageSaverService(
        userService: UserService,
        contactRepository: ContactRepository,
        messageRepository: MessageRepository,
        userRepository: UserRepository
    ): MessageSaverService {
        return MessageSaverService(
            userService, contactRepository,
            messageRepository, userRepository)
    }
}