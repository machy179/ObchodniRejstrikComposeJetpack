package com.machy1979.obchodnirejstrik.viewmodel

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.canShare
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazDleIco
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazOR
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyData
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
import java.io.*
import java.net.URL


class ORViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    //pro výpis OR - obchodní rejstřík
/*    private val _companyDataFromOR = MutableStateFlow(CompanyData())
    val companyDataFromOR: StateFlow<CompanyData> = _companyDataFromOR*/

    private val _companyDataFromOR = MutableStateFlow(savedStateHandle.get<CompanyData>(COMPANY_DATA_FROM_OR_KEY) ?: CompanyData())
    val companyDataFromOR: StateFlow<CompanyData> = _companyDataFromOR

    companion object {
        private const val COMPANY_DATA_FROM_OR_KEY = "company_data_key"
        private const val BUTTON_CLICKED_OR_KEY = "button_clicked_or_key"

    }

    fun updateCompanyDataFromOR() { //v případě killnutí activity savedSatateHandle uloží níže uvedený objekt, aby se po znovuzobrazení aktivity tento načetl
            savedStateHandle.set(COMPANY_DATA_FROM_OR_KEY, _companyDataFromOR.value)
    }

/*    private val _buttonClickedOR = MutableStateFlow<Boolean>(false)
    val buttonClickedOR: StateFlow<Boolean> =_buttonClickedOR*/
    private val _buttonClickedOR = MutableStateFlow(savedStateHandle.get<Boolean>(ORViewModel.BUTTON_CLICKED_OR_KEY) ?: false)
    val buttonClickedOR: StateFlow<Boolean> = _buttonClickedOR
    fun updateButtonClickedOR() {
        savedStateHandle.set(ORViewModel.BUTTON_CLICKED_OR_KEY, _buttonClickedOR.value)
    }



    private var _nacitaniOR = MutableStateFlow(false)
    val nacitaniOR: StateFlow<Boolean> = _nacitaniOR
    private val _errorMessageOR = MutableStateFlow<String>("")
    val errorMessageOR: StateFlow<String> = _errorMessageOR

    fun loadDataIcoOR(ico: String, context: Context) {
        _buttonClickedOR.value = false
        updateButtonClickedOR()
        _nacitaniOR.value = true
        viewModelScope.launch {
            try {
                Log.i("RopzarzovaniOR: ico:",ico)
                val documentString = getAresDataIcoOR(ico)
                Log.i("RopzarzovaniOR: documentString:",documentString.toString())
                val jsonObject = JSONObject(documentString)
                val kodValue = jsonObject.optString("kod") //zjistí, zda ve výstupu je "kod", v tom případě ARES poslal zprávu z chybou
                Log.i("RopzarzovaniOR: ","111")
                if (kodValue=="") {
                    Log.i("RopzarzovaniOR: ","222")
                    if (documentString != null) {
                        val zaznamyArray = jsonObject.getJSONArray("zaznamy")
                        if (zaznamyArray.length() > 0) {
                            val firstZaznamObject = zaznamyArray.getJSONObject(0)
                            Log.i("RopzarzovaniOR: ","333")
                            _companyDataFromOR.value = RozparzovaniDatDotazOR.vratCompanyData(firstZaznamObject, context)
                            Log.i("RopzarzovaniOR: ","444")
                            _errorMessageOR.value = " "
                            _buttonClickedOR.value = true
                            updateCompanyDataFromOR()
                            updateButtonClickedOR()
                        } else {
                            _errorMessageOR.value = "Žádný záznam k subjektu vARESu"
                            Log.i("RopzarzovaniOR: ","555")
                        }

                    } else {
                        _errorMessageOR.value = "Nepodařilo se načíst data z ARESu"
                        Log.i("RopzarzovaniOR: ","666")
                    }
                } else {
                    _errorMessageOR.value = jsonObject.optString("popis").replace("&nbsp;", "") //ARES MI JEŠTĚ HÁZEL TOHLE &nbsp; - TAK TO MUSÍM MAZAT
                    Log.i("RopzarzovaniOR: ","777")
                }


                /*if (document != null) {
                    _companyDataFromOR.value = RozparzovaniDatDotazOR.vratCompanyData(document, context)
                    if (_companyDataFromOR.value.ico == " ") {
                        _errorMessageOR.value = RozparzovaniDatDotazOR.vratErrorHlasku(document)
                    } else  {
                        _errorMessageOR.value = " "
                        _buttonClickedOR.value = true
                    }
                } else {
                    _errorMessageOR.value = "Nepodařilo se načíst data z ARESu"
                }*/

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageOR.value = "Nepodařilo se načíst data z ARESu"
                Log.i("RopzarzovaniOR: ","888")
            }
            _nacitaniOR.value = false
            Log.i("RopzarzovaniOR: ","999")
        }
    }

    private suspend fun getAresDataIcoOR(ico: String): String? {
       // val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_or.cgi?ico=$ico"
        val url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty-vr/$ico"
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
         val pdfByteArray = StringToPdfConvector.createInMemoryPdf( context,companyDataFromOR.value)
        // Uložení PDF obsahu do dočasného souboru v interním úložišti aplikace
        val tempPdfFileName = companyDataFromOR.value.name+".pdf"
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
            putExtra(Intent.EXTRA_SUBJECT, "Výpis z obchodního rejstříku") //tohle je název
            putExtra(Intent.EXTRA_TEXT, "Výpis z obchodního rejstříku: "+companyDataFromOR.value.name)
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
                val pdfFileName = companyDataFromOR.value.name+"_OR"
                val file = StringToPdfConvector.convertToPdf(pdfFileName,context,companyDataFromOR.value)
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



    @Composable
    fun saveToPdf2(context: Context) {

    }

}