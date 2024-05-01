package com.machy1979.obchodnirejstrik.viewmodel

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazOR
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRZP
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import com.machy1979.obchodnirejstrik.model.CompanyDataRZP
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
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class RZPViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    //pro výpis RZP - rejstřík živnostenského podnikání

/*    private val _companyDataFromRZP = MutableStateFlow(CompanyDataRZP())
    val companyDataFromRZP: StateFlow<CompanyDataRZP> = _companyDataFromRZP*/

    private val _companyDataFromRZP = MutableStateFlow(savedStateHandle.get<CompanyDataRZP>(COMPANY_DATA_FROM_RZP_KEY) ?: CompanyDataRZP())
    val companyDataFromRZP: StateFlow<CompanyDataRZP> = _companyDataFromRZP

    companion object {
        private const val COMPANY_DATA_FROM_RZP_KEY = "company_data_key"
        private const val BUTTON_CLICKED_RZP_KEY = "button_clicked_rzp_key"
    }

    fun updateCompanyDataFromRZP() { //v případě killnutí activity savedSatateHandle uloží níže uvedený objekt, aby se po znovuzobrazení aktivity tento načetl
        savedStateHandle.set(COMPANY_DATA_FROM_RZP_KEY, _companyDataFromRZP.value)
    }

    /*    private val _buttonClickedRZP = MutableStateFlow<Boolean>(false)
    val buttonClickedRZP: StateFlow<Boolean> =_buttonClickedRZP*/
    private val _buttonClickedRZP = MutableStateFlow(savedStateHandle.get<Boolean>(BUTTON_CLICKED_RZP_KEY) ?: false)
    val buttonClickedRZP: StateFlow<Boolean> = _buttonClickedRZP
    fun updateButtonClickedRZP() {
        savedStateHandle.set(BUTTON_CLICKED_RZP_KEY, _buttonClickedRZP.value)
    }

    private var _nacitaniRZP = MutableStateFlow(false)
    val nacitaniRZP: StateFlow<Boolean> = _nacitaniRZP
    private val _errorMessageRZP = MutableStateFlow<String>("")
    val errorMessageRZP: StateFlow<String> = _errorMessageRZP

    fun loadDataIcoRZP(ico: String, context: Context) {
        _buttonClickedRZP.value = false
        updateButtonClickedRZP()
        _nacitaniRZP.value = true
        viewModelScope.launch {
            try {
                val document = null
                val documentString = getAresDataIcoRZP(ico)
                val jsonObject = JSONObject(documentString)
                val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                if (kodValue=="") {
                    Log.i("RopzarzovaniOR: ","222")
                    if (documentString != null) {
                        val zaznamyArray = jsonObject.getJSONArray("zaznamy")
                        if (zaznamyArray.length() > 0) {
                            val firstZaznamObject = zaznamyArray.getJSONObject(0)
                            _companyDataFromRZP.value = RozparzovaniDatDotazRZP.vratCompanyData(firstZaznamObject, context)
                            _errorMessageRZP.value = " "
                            _buttonClickedRZP.value = true
                            updateCompanyDataFromRZP()
                            updateButtonClickedRZP()
                        } else {
                            _errorMessageRZP.value = "Žádný záznam k subjektu vARESu"
                            Log.i("RopzarzovaniOR: ","555")
                        }

                    } else {
                        _errorMessageRZP.value = "Nepodařilo se načíst data z ARESu"
                        Log.i("RopzarzovaniOR: ","666")
                    }
                } else {
                    _errorMessageRZP.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                    Log.i("RopzarzovaniOR: ","777")
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageRZP.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitaniRZP.value = false
        }
    }

    private suspend fun getAresDataIcoRZP(ico: String): String? {
        //val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_rzp.cgi?ico=$ico"
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-rzp/$ico"
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
        val pdfByteArray = StringToPdfConvector.createInMemoryPdf( context,null, companyDataFromRZP.value)
        // Uložení PDF obsahu do dočasného souboru v interním úložišti aplikace
        val tempPdfFileName = companyDataFromRZP.value.name+".pdf"
        val tempPdfFile = File(context.cacheDir, tempPdfFileName)

        FileOutputStream(tempPdfFile).use { outputStream ->
            outputStream.write(pdfByteArray)
            outputStream.flush()
        }

        val pdfUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", tempPdfFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            //  putExtra(Intent.EXTRA_STREAM, pdfUri)
            setClipData(ClipData.newRawUri("", pdfUri))
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, "Výpis z rejstříku živnostenského podnikání") //tohle je název
            putExtra(Intent.EXTRA_TEXT, "Výpis z RŽP: "+companyDataFromRZP.value.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

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
      //  Toast.makeText(context, "Ukládám", Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {//Tento dispatcher je určen pro asynchronní operace, které neblokují hlavní vlákno, jako jsou načítání nebo zápis do souborů, síťové operace atd.
                SharedState.setSaveToPdfClicked(true)
                val pdfFileName = companyDataFromRZP.value.name+"_RZP"
                val file = StringToPdfConvector.convertToPdf(pdfFileName,context,null, companyDataFromRZP.value)
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