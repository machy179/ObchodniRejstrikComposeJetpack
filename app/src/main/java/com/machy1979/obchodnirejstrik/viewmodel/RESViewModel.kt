package com.machy1979.obchodnirejstrik.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRES
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRZP
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import com.machy1979.obchodnirejstrik.model.SharedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.net.URL

class RESViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    //pro výpis RES - rejstřík ekonomických subjektů
/*    private val _companyDataFromRES = MutableStateFlow(CompanyDataRES())
    val companyDataFromRES: StateFlow<CompanyDataRES> = _companyDataFromRES */

    private val _companyDataFromRES = MutableStateFlow(savedStateHandle.get<CompanyDataRES>(COMPANY_DATA_FROM_RES_KEY) ?: CompanyDataRES())
    val companyDataFromRES: StateFlow<CompanyDataRES> = _companyDataFromRES

    companion object {
        private const val COMPANY_DATA_FROM_RES_KEY = "company_data_key"
        private const val BUTTON_CLICKED_RES_KEY = "button_clicked_res_key"
    }

    fun updateCompanyDataFroRES() { //v případě killnutí activity savedSatateHandle uloží níže uvedený objekt, aby se po znovuzobrazení aktivity tento načetl
        savedStateHandle.set(COMPANY_DATA_FROM_RES_KEY, _companyDataFromRES.value)
    }

/*    private val _buttonClickedRES = MutableStateFlow<Boolean>(false)
    val buttonClickedRES: StateFlow<Boolean> =_buttonClickedRES*/
    private val _buttonClickedRES = MutableStateFlow(savedStateHandle.get<Boolean>(RESViewModel.BUTTON_CLICKED_RES_KEY) ?: false)
    val buttonClickedRES: StateFlow<Boolean> = _buttonClickedRES
    fun updateButtonClickedRES() {
        savedStateHandle.set(RESViewModel.BUTTON_CLICKED_RES_KEY, _buttonClickedRES.value)
    }


    private var _nacitaniRES = MutableStateFlow(false)
    val nacitaniRES: StateFlow<Boolean> = _nacitaniRES
    private val _errorMessageRES = MutableStateFlow<String>("")
    val errorMessageRES: StateFlow<String> = _errorMessageRES


    fun loadDataIcoRES(ico: String, context: Context) {
        _buttonClickedRES.value = false
        updateButtonClickedRES()
        _nacitaniRES.value = true
        viewModelScope.launch {
            try {
                Log.i("aaaa", "ICO: " + ico)
                val document = null
                val documentString = getAresDataIcoRES(ico)

                val jsonObject = JSONObject(documentString)
                val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                if (kodValue=="") {
                    if (documentString != null) {
                        val zaznamyArray = jsonObject.getJSONArray("zaznamy")
                        if (zaznamyArray.length() > 0) {
                            val firstZaznamObject = zaznamyArray.getJSONObject(0)
                            _companyDataFromRES.value = RozparzovaniDatDotazRES.vratCompanyData(firstZaznamObject, context)
                            _errorMessageRES.value = " "
                            _buttonClickedRES.value = true
                            updateCompanyDataFroRES()
                            updateButtonClickedRES()
                        } else {
                            _errorMessageRES.value = "Žádný záznam k subjektu vARESu"
                            Log.i("RopzarzovaniOR: ","555")
                        }

                    } else {
                        _errorMessageRES.value = "Nepodařilo se načíst data z ARESu"
                        Log.i("RopzarzovaniOR: ","666")
                    }
                } else {
                    _errorMessageRES.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                    Log.i("RopzarzovaniOR: ","777")
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageRES.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitaniRES.value = false
        }
    }

    private suspend fun getAresDataIcoRES(ico: String): String? {
      //  val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_res.cgi?ico=$ico"
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-res/$ico"
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
            null
        }
    }

    fun share(context: Context) {
        // Create an ACTION_SEND implicit intent with order details in the intent extras
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Výpis z RES") //tohle je název
            putExtra(Intent.EXTRA_TEXT, "RES bla bla bla") //tohle je context textu
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.app_name)
            )
        )
    }

    fun saveToPdf(context: Context) {
        //je třeba to spustit ve vláknu, při větších firmách se to dělalo dlouho a hlavní vlákno zamrzalo
       // Toast.makeText(context, "Ukládám", Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {//Tento dispatcher je určen pro asynchronní operace, které neblokují hlavní vlákno, jako jsou načítání nebo zápis do souborů, síťové operace atd.
                SharedState.setSaveToPdfClicked(true)
                val pdfFileName = companyDataFromRES.value.name+"_RES"
                val file = StringToPdfConvector.convertToPdf(pdfFileName,context,null, null, companyDataFromRES.value)
                SharedState.setSaveToPdfClicked(false)

                withContext(Dispatchers.Main) {//se používá pro provádění operací, které mění UI nebo nějakým způsobem interagují s UI prvkem. Tento dispatcher by měl být použit, když potřebujete aktualizovat UI nebo spustit nějakou akci v hlavním vlákně (UI vláknu).
                    if (file != null) {
                        Toast.makeText(context, "V Downloads uložen soubor " + pdfFileName, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Soubor se nepodařilo uložit", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}