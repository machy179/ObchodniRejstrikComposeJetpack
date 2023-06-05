package com.machy1979.obchodnirejstrik.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRES
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
                Jsoup.connect(url).get()

            }
        } catch (e: Exception) {
            null
        }
    }

}