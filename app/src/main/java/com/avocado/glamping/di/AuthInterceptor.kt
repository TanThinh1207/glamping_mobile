package com.avocado.glamping.di

import android.content.Context
import android.content.Intent
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.activity.LoginRegisterActivity
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = UserPreferences.getUser(context)?.accessToken

        val request = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(request.build())

        if (response.code == 401) {
            // 🛑 Ensure the response is closed before handling the token expiration
            response.close()

            handleTokenExpiration(context)
        }

        return response
    }

    private fun handleTokenExpiration(context: Context) {
        UserPreferences.clearUser(context)

        val intent = Intent(context, LoginRegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
