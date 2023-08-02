package com.machy1979.obchodnirejstrik.viewmodel

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazOR
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*
import java.nio.charset.StandardCharsets

class ORViewModel : ViewModel() {

    //pro výpis OR - obchodní rejstřík
    private val _companyDataFromOR = MutableStateFlow(CompanyData())
    val companyDataFromOR: StateFlow<CompanyData> = _companyDataFromOR
    private var _nacitaniOR = MutableStateFlow(false)
    val nacitaniOR: StateFlow<Boolean> = _nacitaniOR
    private val _errorMessageOR = MutableStateFlow<String>("")
    val errorMessageOR: StateFlow<String> = _errorMessageOR
    private val _buttonClickedOR = MutableStateFlow<Boolean>(false)
    val buttonClickedOR: StateFlow<Boolean> =_buttonClickedOR

    fun loadDataIcoOR(ico: String) {
        _buttonClickedOR.value = false
        _nacitaniOR.value = true
        viewModelScope.launch {
            try {
                Log.i("aaaa", "ICO: " + ico)
                val document = getAresDataIcoOR(ico)
                if (document != null) {
                    _companyDataFromOR.value = RozparzovaniDatDotazOR.vratCompanyData(document)
                    if (_companyDataFromOR.value.ico == " ") {
                        _errorMessageOR.value = RozparzovaniDatDotazOR.vratErrorHlasku(document)
                    } else  {
                        _errorMessageOR.value = " "
                        _buttonClickedOR.value = true
                    }
                } else {
                    _errorMessageOR.value = "Nepodařilo se načíst data z ARESu"
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageOR.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitaniOR.value = false
        }
    }

    private suspend fun getAresDataIcoOR(ico: String): Document? {
        val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_or.cgi?ico=$ico"
        return try {
            withContext(Dispatchers.IO) {
                Log.i("aaaa", "10")
                Jsoup.connect(url).get()

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

}