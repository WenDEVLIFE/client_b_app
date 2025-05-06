package com.noveleta.sabongbetting.Api

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

import okhttp3.Interceptor
import okhttp3.Response

class ReceivedCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.headers("Set-Cookie").forEach { header ->
            if (header.startsWith("PHPSESSID")) {
                val sessionId = header.substringAfter("PHPSESSID=").substringBefore(';')
                SessionManager.sessionId = sessionId
            }
        }
        return response
    }
}

