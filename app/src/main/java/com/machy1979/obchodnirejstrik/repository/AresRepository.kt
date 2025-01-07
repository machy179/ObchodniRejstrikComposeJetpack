package com.machy1979.obchodnirejstrik.repository

import android.util.Log
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

    suspend fun getAresDataEkonomickeSubjekty(ico: String): ResponseBody {
        return try {
            // Volání API přes Retrofit
            apiService.getAresDataEkonomickeSubjektyByIco(ico)
        } catch (e: Exception) {
            "".toResponseBody("text/plain".toMediaType())
        }
    }

    suspend fun getAresDataNazev(nazev: String, nazevMesto: String): ResponseBody {
        val requestBody = if (nazevMesto.isEmpty()) {
            """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev"}"""
        } else {
            """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev", "sidlo": { "textovaAdresa": "$nazevMesto"}}"""
        }
        Log.i("JSON odpověď: ", "1")
        return try {
            Log.i("JSON odpověď: ", "2")
            apiService.getAresDataEkonomickeSubjektyByNazev(requestBody)
        } catch (e: Exception) {
            Log.i("JSON odpověď: ", "Vytvoření prázdného ResponseBody v případě chyby")
            // Vytvoření prázdného ResponseBody v případě chyby
            "".toResponseBody("application/json".toMediaType())
        }
    }



}