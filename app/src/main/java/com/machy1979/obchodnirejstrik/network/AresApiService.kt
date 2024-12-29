package com.machy1979.obchodnirejstrik.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import javax.inject.Singleton

@Singleton
interface AresApiService {
    @Headers(
        "User-Agent: Mozilla",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @GET("ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-vr/{ico}")
    suspend fun getAresDataFromORByIco(
        @Path("ico") ico: String
    ): ResponseBody

    @Headers(
        "User-Agent: Mozilla",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @GET("ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-rzp/{ico}")
    suspend fun getAresDataFromRZPByIco(
        @Path("ico") ico: String
    ): ResponseBody

    @Headers(
        "User-Agent: Mozilla",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @GET("ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-res/{ico}")
    suspend fun getAresDataFromRESByIco(
        @Path("ico") ico: String
    ): ResponseBody
}

