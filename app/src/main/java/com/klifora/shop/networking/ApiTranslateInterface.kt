package com.klifora.shop.networking

import com.google.gson.JsonElement
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiTranslateInterface {
//    @FormUrlEncoded
//    @Headers("Content-Type: application/x-www-form-urlencoded")
//    @POST(TRANSLATE)
//    fun translate(
//        @Field("client") client: String = "gtx",
//        @Field("sl") sl: String = "en",
//        @Field("dt") dt: String = "t",
//        @Field("tl") tl: String,
//        @Field("q") q: String
//    ): Call<JsonElement>

    @Headers("Accept: application/json")
    @GET(TRANSLATE)
    fun translate(
        @Query("tl") lang: String,
        @Query("q") q: String
    ): Call<JsonElement>




    @POST("{id}"+UPDATE_ORDER_PAYMENT)
    suspend fun createOrder(
        @Header("Authorization") authHeader : String,
        @Path("id") id: String,
        @Body requestBody: RequestBody
    ): Response<JsonElement>


    @POST(INVOICE+"/{id}/invoice")
    suspend fun invoice(
        @Header("Authorization") authHeader : String,
        @Path("id") id: String,
        @Body requestBody: RequestBody
    ): Response<JsonElement>


    @POST(CANCEL+"/{id}/cancel")
    suspend fun cancel(
        @Header("Authorization") authHeader : String,
        @Path("id") id: String,
        @Body requestBody: RequestBody
    ): Response<JsonElement>
}

