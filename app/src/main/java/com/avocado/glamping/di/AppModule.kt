package com.avocado.glamping.di

import com.avocado.glamping.BuildConfig
import com.avocado.glamping.data.model.network.LoginApiService
import com.avocado.glamping.data.model.network.RegisterApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val API_URL = BuildConfig.API_URL
    private val DOMAIN = BuildConfig.DOMAIN

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // Add the interceptor here
            .addInterceptor{ chain ->
                var originalRequest = chain.request()

                val domain = DOMAIN
                val newUrl = originalRequest.url.newBuilder()
                    .host(domain)
                    .scheme("http")
                    .build()

                originalRequest = originalRequest.newBuilder().url(newUrl).build()
                chain.proceed(originalRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService{
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterApiService(retrofit: Retrofit): RegisterApiService{
        return retrofit.create(RegisterApiService::class.java)
    }
}