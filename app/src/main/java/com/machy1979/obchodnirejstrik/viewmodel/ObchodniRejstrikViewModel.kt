package com.machy1979.obchodnirejstrik.viewmodel

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.machy1979.obchodnirejstrik.functions.*
import com.machy1979.obchodnirejstrik.model.CompanyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException



class ObchodniRejstrikViewModel  : ViewModel() {

    private val _companyData = MutableStateFlow(CompanyData())
    val companyData: StateFlow<CompanyData> = _companyData

    private var _companysData = mutableStateListOf<CompanyData>()
    val companysData: SnapshotStateList<CompanyData> = _companysData

    private var _nacitani = MutableStateFlow(false)
    val nacitani: StateFlow<Boolean> = _nacitani

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> = _errorMessage



    fun vynulujCompanysData() {
        _companysData.clear()
    }

    fun loadDataIco(ico: String) {
        _nacitani.value = true
        viewModelScope.launch {
            try {
                Log.i("aaaabbb", "ICO: " + ico)
                val documentString = getAresDataIco(ico)
                val jsonObject = JSONObject(documentString)
                val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                if (kodValue=="") {
                    if (documentString != null) {
                        _companyData.value = RozparzovaniDatDotazDleIco.vratCompanyData(jsonObject)
                        _errorMessage.value = ""
                    } else {
                        _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                    }
                } else {
                    _errorMessage.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                }
            } catch (e: Exception) {
                _errorMessage.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitani.value = false
        }
    }

    private suspend fun getAresDataIco(ico: String): String? {
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/$ico"
        val client = OkHttpClient()
        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                response.body?.string()

            }
        } catch (e: Exception) {
            Log.i("aaaabbb error =", e.toString())
            null
        }
    }

    fun loadDataNazev(nazev: String) {
        _nacitani.value = true
        viewModelScope.launch {
            try {
                val documentStringJson = getAresDataNazev(nazev)
                val jsonContent =
                    documentStringJson?.replace(Regex(".*?<body>(.*?)</body>.*", RegexOption.DOT_MATCHES_ALL), "$1")
                Log.i("JSON odpověď: ",jsonContent.toString())
                try {
                    val jsonObject = JSONObject(jsonContent)
                    val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                    if (kodValue=="") {
                        val ekonomickeSubjektyArray = jsonObject.getJSONArray("ekonomickeSubjekty")
                        if (ekonomickeSubjektyArray.length()==0) {
                            _errorMessage.value = "SUBJEKT NENALEZEN"
                        } else {
                            _errorMessage.value = ""
                            // Cyklus procházející všechny položky v poli ekonomických subjektů
                            for (i in 0 until ekonomickeSubjektyArray.length()) {
                                val ekonomickySubjekt = ekonomickeSubjektyArray.getJSONObject(i)
                                companysData.add(RozparzovaniDatProCompanysDataNovy.vratCompanyData(ekonomickySubjekt))
                            }
                        }

                    } else {
                        _errorMessage.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                    }

                } catch (e: JSONException) {
                    println("Chyba při převodu na JSONObject: ${e.message}")
                    _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessage.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitani.value = false
        }
    }

    private suspend fun getAresDataNazev(nazev: String): String? {
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/vyhledat"
        Log.d("Pokus JSON :", "111")
        return try {
            Log.d("Pokus JSON :", "222")
            withContext(Dispatchers.IO) {
                Log.d("Pokus JSON :", "333")
                Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .header("content-type", "application/json")
                    .header("accept", "application/json")
                    .requestBody("""{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev"}""")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post()
                    .body()
                    .toString()

            }

        } catch (e: Exception) {
            null
        }
    }

}
