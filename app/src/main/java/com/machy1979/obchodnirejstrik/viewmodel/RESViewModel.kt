package com.machy1979.obchodnirejstrik.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRES
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.net.URL

class RESViewModel : ViewModel() {

    //pro výpis RES - rejstřík ekonomických subjektů
    private val _companyDataFromRES = MutableStateFlow(CompanyDataRES()) //tady nastavit na CompanyDataRES - tuhle třídu vytvořit
    val companyDataFromRES: StateFlow<CompanyDataRES> = _companyDataFromRES //to samé tady
    private var _nacitaniRES = MutableStateFlow(false)
    val nacitaniRES: StateFlow<Boolean> = _nacitaniRES
    private val _errorMessageRES = MutableStateFlow<String>("")
    val errorMessageRES: StateFlow<String> = _errorMessageRES
    private val _buttonClickedRES = MutableStateFlow<Boolean>(false)
    val buttonClickedRES: StateFlow<Boolean> =_buttonClickedRES

    fun loadDataIcoRES(ico: String) {
        _buttonClickedRES.value = false
        _nacitaniRES.value = true
        viewModelScope.launch {
            try {
                Log.i("aaaa", "ICO: " + ico)
                val document = getAresDataIcoRES(ico)
                if (document != null) {
                    _companyDataFromRES.value = RozparzovaniDatDotazRES.vratCompanyData(document)
                    if (_companyDataFromRES.value.ico == " ") {
                        _errorMessageRES.value = RozparzovaniDatDotazRES.vratErrorHlasku(document)
                    } else  {
                        _errorMessageRES.value = " "
                        _buttonClickedRES.value = true
                    }
                } else {
                    _errorMessageRES.value = "Nepodařilo se načíst data z ARESu"
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessageRES.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitaniRES.value = false
        }
    }

    private suspend fun getAresDataIcoRES(ico: String): Document? {
        val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_res.cgi?ico=$ico"
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
        //  fun share(context: Context) {


        // Uložení PDF obsahu do souboru v interním úložišti Download složky
        val pdfFileName = companyDataFromRES.value.name+"_RES"

        val file = StringToPdfConvector.convertToPdf(pdfFileName,context,null, null, companyDataFromRES.value)
        if (file != null) {
            Toast.makeText(context, "Soubor "+pdfFileName+" uložen v Downloads", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context,"Soubor se nepodařilo uložit", Toast.LENGTH_SHORT).show()
        }
    }

}