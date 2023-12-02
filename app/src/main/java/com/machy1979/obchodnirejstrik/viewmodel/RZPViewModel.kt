package com.machy1979.obchodnirejstrik.viewmodel

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRZP
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyDataRZP
import com.machy1979.obchodnirejstrik.model.SharedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class RZPViewModel : ViewModel() {

    //pro výpis RZP - rejstřík živnostenského podnikání
    private val _companyDataFromRZP = MutableStateFlow(CompanyDataRZP())
    val companyDataFromRZP: StateFlow<CompanyDataRZP> = _companyDataFromRZP
    private var _nacitaniRZP = MutableStateFlow(false)
    val nacitaniRZP: StateFlow<Boolean> = _nacitaniRZP
    private val _errorMessageRZP = MutableStateFlow<String>("")
    val errorMessageRZP: StateFlow<String> = _errorMessageRZP
    private val _buttonClickedRZP = MutableStateFlow<Boolean>(false)
    val buttonClickedRZP: StateFlow<Boolean> =_buttonClickedRZP

    fun loadDataIcoRZP(ico: String) {
        _buttonClickedRZP.value = false
        _nacitaniRZP.value = true
        viewModelScope.launch {
            try {
                Log.i("aaaa", "ICO: " + ico)
                val document = getAresDataIcoRZP(ico)
                if (document != null) {
                    _companyDataFromRZP.value = RozparzovaniDatDotazRZP.vratCompanyData(document)
                    if (_companyDataFromRZP.value.ico == " ") {
                        _errorMessageRZP.value = RozparzovaniDatDotazRZP.vratErrorHlasku(document)
                    } else  {
                        _errorMessageRZP.value = " "
                        _buttonClickedRZP.value = true
                    }
                } else {
                    _errorMessageRZP.value = "Nepodařilo se načíst data z ARESu"
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageRZP.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitaniRZP.value = false
        }
    }

    private suspend fun getAresDataIcoRZP(ico: String): Document? {
        val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_rzp.cgi?ico=$ico"
        return try {
            withContext(Dispatchers.IO) {
                Log.i("aaaa", "10")
/*                Jsoup.connect(url)
                    .get()*/
                //výše uvedené mi například při výpisu ZEPO Bohuslavice u dozorčí rady házelo v tagu &lt a dozorčí radu to nevypsalo
                //tady jsem našel níže uvedené řešení: https://stackoverflow.com/questions/43773855/jsoup-parser-not-working-as-expected-for-particular-url-only
                Jsoup.parse(URL(url).openStream(), "UTF-8", "", Parser.xmlParser());

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