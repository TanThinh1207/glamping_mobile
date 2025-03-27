package com.avocado.glamping.di

import android.content.Context
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.data.model.network.LoginApiService
import com.avocado.glamping.service.AuthApiService
import com.avocado.glamping.service.BookingApiService
import com.avocado.glamping.service.CampSiteService
import com.avocado.glamping.service.CampTypeApiService
import com.avocado.glamping.service.ChatApiService
import com.avocado.glamping.service.FacilityApiService
import com.avocado.glamping.service.FcmTokenApiService
import com.avocado.glamping.service.PaymentApiService
import com.avocado.glamping.service.RatingApiService
import com.avocado.glamping.service.RevenueApiService
import com.avocado.glamping.service.SelectionApiService
import com.avocado.glamping.service.UserApiService
import com.avocado.glamping.service.UtilityApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val API_URL = BuildConfig.API_URL
    private val DOMAIN = BuildConfig.DOMAIN

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit{

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(60, TimeUnit.SECONDS) // Increase read timeout
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)  // Add the interceptor here
            .addInterceptor(authInterceptor)
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
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .build()
    }


    @Provides
    @Singleton
    fun provideLoginApiService(retrofit : Retrofit): AuthApiService{
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService{
       return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingApiService(retrofit: Retrofit): BookingApiService{
        return retrofit.create(BookingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCampSiteApiService(retrofit: Retrofit): CampSiteService{
        return retrofit.create(CampSiteService::class.java)
    }

    @Provides
    @Singleton
    fun provideFcmTokenApiService(retrofit : Retrofit): FcmTokenApiService{
        return retrofit.create(FcmTokenApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService{
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFacilityApiService(retrofit: Retrofit): FacilityApiService{
        return retrofit.create(FacilityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUtilityApiService(retrofit: Retrofit): UtilityApiService{
        return retrofit.create(UtilityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCampTypeApiService(retrofit: Retrofit): CampTypeApiService{
        return retrofit.create(CampTypeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRevenueApiService(retrofit: Retrofit): RevenueApiService{
        return retrofit.create(RevenueApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService{
        return retrofit.create(ChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSelectionApiService(retrofit: Retrofit): SelectionApiService{
        return retrofit.create(SelectionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRatingApiService(retrofit: Retrofit): RatingApiService{
        return retrofit.create(RatingApiService::class.java)
    }
}