package com.szlazakm.safechat

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

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