package com.noveleta.sabongbetting.Api;

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.noveleta.sabongbetting.Network.api.*
import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*
import okhttp3.*
import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {

    private val BASE_URL: String
        get() {
            val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
            return "http://$ip/"
        }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AddCookieInterceptor())
            .addInterceptor(ReceivedCookieInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Logs headers & body
            })
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        try {
            provideOkHttpClient().also {
                Log.d("RetrofitClient", "OkHttpClient initialized.")
            }
        } catch (e: Exception) {
            Log.e("RetrofitClient", "Failed to initialize OkHttpClient: ${e.message}")
            throw e
        }
    }

    val checkUserApi: CheckUserApi by lazy {
        try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(CheckUserApi::class.java).also {
                    Log.d("RetrofitClient", "CheckUserApi created successfully.")
                }
        } catch (e: Exception) {
            Log.e("RetrofitClient", "Failed to create CheckUserApi: ${e.message}")
            throw e
        }
    }
}
