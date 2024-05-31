package com.machy1979.obchodnirejstrik.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.machy1979.obchodnirejstrik.functions.*
import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.Query
import com.machy1979.obchodnirejstrik.repository.ORRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class ObchodniRejstrikViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ORRepository
) : ViewModel() {
//class ObchodniRejstrikViewModel(private val savedStateHandle: SavedStateHandle)   : ViewModel() {

/*    private val _companyData = MutableStateFlow(CompanyData())
    val companyData: StateFlow<CompanyData> = _companyData*/

    //aby po restartu aktivity přežila CompanyData, je nutné do MV dát výše uvedený (private val savedStateHandle: SavedStateHandle), poté udělat níže uvedené změny, funkc companyUpdate spustit na vhodném místě, v tomto případě po načtení dat z Aresu a hlavně firmu CompanyData Seriablizovat
    private val _companyData = MutableStateFlow(savedStateHandle.get<CompanyData>(COMPANY_DATA_KEY) ?: CompanyData())
    val companyData: StateFlow<CompanyData> = _companyData

    companion object {
        private const val COMPANY_DATA_KEY = "company_data_key"
        private const val COMPANYS_DATA_KEY = "companys_data_keyy"
    }

    // Metoda pro aktualizaci dat o firmě
    fun updateCompanyData() { //v případě killnutí activity savedSatateHandle uloží níže uvedený objekt, aby se po znovuzobrazení aktivity tento načetl
        savedStateHandle.set(COMPANY_DATA_KEY, _companyData.value)
    }
    //konec změny aby po restartu....

/*    private val _companysData = mutableStateListOf<CompanyData>()
    val companysData: SnapshotStateList<CompanyData> = _companysData*/

    //aby po restartu aktivity přežila níže CompanysData - protože nelze do savedStateHandle vkládat SnapshotStateList, musí se to převést na ArrayList, ten vložit jde:
    private val _companysData: SnapshotStateList<CompanyData> = savedStateHandle.get<ArrayList<CompanyData>>(COMPANYS_DATA_KEY)?.let {
        mutableStateListOf(*it.toTypedArray())
    } ?: mutableStateListOf<CompanyData>()

    val companysData: SnapshotStateList<CompanyData> = _companysData

    fun updateCompanysData() { //v případě killnutí activity savedSatateHandle uloží níže uvedený objekt, aby se po znovuzobrazení aktivity tento načetl
        val companyDataList = _companysData.toList()
        val arrayList = ArrayList(companyDataList)
        savedStateHandle.set(COMPANYS_DATA_KEY, arrayList)

    }

    //konec změny aby po restartu....

    private val _nacitani = MutableStateFlow(false)
 //   val nacitani: StateFlow<Boolean> = _nacitani
    val nacitani = _nacitani.asStateFlow()

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: StateFlow<String> = _errorMessage

    //načtení historie
    private val _queryList = MutableStateFlow<List<Query>>(emptyList()) //pro room je třeba tady dát Fow a ne jen mutalbeStateListOf
    val queryList = _queryList.asStateFlow()
    private val _nactenoQueryList = MutableStateFlow(false)
    //   val nacitani: StateFlow<Boolean> = _nacitani
    val nactenoQueryList = _nactenoQueryList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllNotes().distinctUntilChanged()
                .collect { listOfNotes ->
                    if (listOfNotes.isNullOrEmpty()) {
                        Log.d("Empty", ": Empty list")
                    }else {
                        val distinctNotes = listOfNotes.distinctBy { it.ico } // aby se vymazaly duplicity
                        _queryList.value = distinctNotes
                        _nactenoQueryList.value = true
                    }

                }

        }
        // noteList.addAll(NotesDataSource().loadNotes())
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotes()



        }
        _queryList.value = emptyList()
        _nactenoQueryList.value = false
        // noteList.addAll(NotesDataSource().loadNotes())
    }


    fun vynulujCompanysData() {
        _companysData.clear()
        updateCompanysData()
    }

    fun loadDataIco(ico: String) {
        _nacitani.value = true
        saveQueryToFirebse(ico)
        viewModelScope.launch {
            try {
                Log.i("aaaabbb", "ICO: " + ico)

                val documentString = getAresDataIco(ico)
                val jsonObject = JSONObject(documentString)
                val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                if (kodValue=="") {
                    if (documentString != null) {
                        _companyData.value = RozparzovaniDatDotazDleIco.vratCompanyData(jsonObject)
                        updateCompanyData()
                        repository.addQuery(Query(ico = companyData.value.ico, name = companyData.value.name, address = companyData.value.address)) //uložení do databáze
                        _errorMessage.value = ""
                        saveSearchCompany()
                    } else {
                        _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                    }
                } else {
                    _errorMessage.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                }
            } catch (e: Exception) {
                _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                Log.e("ChybaUlozeniMV",e.toString())
            }
            _nacitani.value = false
        }
    }

    private fun saveSearchCompany() {
        var searchCompanyToSave = mutableMapOf<String, Any>()
        searchCompanyToSave["address"] = companyData.value.address
        searchCompanyToSave["ico"] = companyData.value.ico
        searchCompanyToSave["name"] = companyData.value.name
        searchCompanyToSave["date"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        FirebaseFirestore.getInstance().collection("search_company")
            .add(searchCompanyToSave)
    }

    private fun saveQueryToFirebse(dotaz: String) {
        var queryToSave = mutableMapOf<String, Any>()
        queryToSave["query"] = dotaz
        queryToSave["date"] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        FirebaseFirestore.getInstance().collection("queries")
            .add(queryToSave)
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

    fun loadDataNazev(nazev: String, nazevMesto: String) {
        _nacitani.value = true
        saveQueryToFirebse(nazev + ""+nazevMesto)
        viewModelScope.launch {
            try {

                val documentStringJson = getAresDataNazev(nazev, nazevMesto)
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
                            updateCompanysData()


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

    private suspend fun getAresDataNazev(nazev: String,nazevMesto: String): String? {
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/vyhledat"
        val requestBody = if (nazevMesto.isEmpty()) { """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev"}"""
        } else {
            """{"start": 0, "pocet": 1000, "razeni": ["obchodniJmeno"], "obchodniJmeno": "$nazev", "sidlo": { "textovaAdresa": "$nazevMesto"}}"""
        }
        Log.d("nazevMesto :", nazevMesto)
        return try {
            withContext(Dispatchers.IO) {
                Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .header("content-type", "application/json")
                    .header("accept", "application/json")
                    .requestBody(requestBody)
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
