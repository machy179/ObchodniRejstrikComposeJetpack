package com.machy1979.obchodnirejstrik.repository

import com.machy1979.obchodnirejstrik.network.AresApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class AresRepository @Inject constructor(
    private val apiService: AresApiService,
) {

    suspend fun getAresDataOR(ico: String): ResponseBody {
        return try {
            // Volání API přes Retrofit
            apiService.getAresDataFromORByIco(ico)
        } catch (e: Exception) {
            // Vytvoření prázdného ResponseBody
            "".toResponseBody("text/plain".toMediaType())
        }
    }

    suspend fun getAresDataRZP(ico: String): ResponseBody {
        return try {
            // Volání API přes Retrofit
            apiService.getAresDataFromRZPByIco(ico)
        } catch (e: Exception) {
            "".toResponseBody("text/plain".toMediaType())
        }
    }

    suspend fun getAresDataRES(ico: String): ResponseBody {
        return try {
            // Volání API přes Retrofit
            apiService.getAresDataFromRESByIco(ico)
        } catch (e: Exception) {
            "".toResponseBody("text/plain".toMediaType())
        }
    }
}