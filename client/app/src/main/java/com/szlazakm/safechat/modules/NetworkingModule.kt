package com.szlazakm.safechat.modules

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
class NetworkingModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // for emulator
//            .baseUrl("https://safechat-986401487521.us-central1.run.app") // server on GCP
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesPreKeyWebService(retrofit: Retrofit): PreKeyWebService {
        return retrofit.create(PreKeyWebService::class.java)
    }

    @Provides
    fun provideUserWebService(retrofit: Retrofit) : UserWebService {
        return retrofit.create(UserWebService::class.java)
    }
}