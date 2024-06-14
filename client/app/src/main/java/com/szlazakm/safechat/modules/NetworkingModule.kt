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
            .baseUrl("http://192.168.0.230:8080")   //For PC
//            .baseUrl("http://10.67.43.127:8080")
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