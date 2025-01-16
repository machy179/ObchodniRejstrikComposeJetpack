package com.machy1979.obchodnirejstrik.repository

import android.util.Log
import com.google.gson.Gson
import com.machy1979.obchodnirejstrik.model.AresResponse
import com.machy1979.obchodnirejstrik.network.AresApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun getAresDataEkonomickeSubjekty(ico: String): ResponseBody {
        return try {
            // Volání API přes Retrofit
            apiService.getAresDataEkonomickeSubjektyByIco(ico)
        } catch (e: Exception) {
            "".toResponseBody("text/plain".toMediaType())
        }
    }

    suspend fun getAresDataNazev(nazev: String, nazevMesto: String): AresResponse {
        val json = if (nazevMesto.isEmpty()) {
            """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev"}"""
        } else {
            """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev", "sidlo": { "textovaAdresa": "$nazevMesto"}}"""
        }

        val requestBody = json.toRequestBody("application/json".toMediaType())

        lateinit var aresResponse: AresResponse //odpověď už v objektu rozpárzované z json odpovědi včetně stavu, zda nedošlo k chybě v odpovědi např. při velkém počtu výsledků atp
        try {
            val rawResponse = apiService.getAresDataEkonomickeSubjektyByNazev(requestBody)

            if (rawResponse.isSuccessful) {
                aresResponse = Gson().fromJson(rawResponse.body()?.string(), AresResponse::class.java)

            } else if (rawResponse.code() == 400) {
                aresResponse = Gson().fromJson(rawResponse.errorBody()?.string(), AresResponse::class.java)
            }

        } catch (e: Exception) {
            Log.e("HTTP_OR", "error apriService: "+e.toString())
        }
        return aresResponse
    }



}