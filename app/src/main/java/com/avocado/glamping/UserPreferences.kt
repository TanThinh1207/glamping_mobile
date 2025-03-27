package com.avocado.glamping

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.avocado.glamping.data.model.resp.AuthResponse
import com.avocado.glamping.data.model.resp.UserResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserPreferences {
    private const val PREF_NAME = "secure_prefs"
    private const val USER_KEY = "user_data"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            android.util.Log.e("UserPreferences", "EncryptedSharedPreferences failed, clearing storage", e)
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply() // Reset corrupted data
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) // Return a default SharedPreferences
        }
    }



    fun saveUser(context: Context, user: AuthResponse){
        val sharedPreferences = getEncryptedPrefs(context)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(user)

        editor.putString(USER_KEY, json)
        editor.apply()

        android.util.Log.d("UserPreferences", "User data saved: $json")
    }

    fun getUser(context: Context): AuthResponse? {
        val sharedPreferences = getEncryptedPrefs(context)
        return try {
            val json = sharedPreferences.getString(USER_KEY, null)
            if (json != null) {
                val type = object : TypeToken<AuthResponse>() {}.type
                Gson().fromJson(json, type)
            } else {
                null
            }
        } catch (e: Exception) { // Catch decryption errors
            if (e is javax.crypto.AEADBadTagException) {
                // Log the error and clear corrupted data
                android.util.Log.e("UserPreferences", "Decryption failed, clearing user data", e)
                clearUser(context) // Reset storage
            }
            null
        }
    }


    fun clearUser(context: Context) {
        val sharedPreferences = getEncryptedPrefs(context)
        sharedPreferences.edit().remove(USER_KEY).apply()
    }
}