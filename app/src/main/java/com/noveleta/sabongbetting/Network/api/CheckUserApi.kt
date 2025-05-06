package com.noveleta.sabongbetting.Network.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import okhttp3.ResponseBody

import com.noveleta.sabongbetting.Model.*

interface CheckUserApi {
    @FormUrlEncoded
    @POST("checkLogin.php")
    fun checkUser(
        @Field("androidLogin") androidLogin: String = "true", //Boolean
        @Field("mobileUser") mobileUser: String,
        @Field("password") password: String
    ): Call<ResponseBody>
}
