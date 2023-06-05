package com.machy1979.obchodnirejstrik.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazRZP
import com.machy1979.obchodnirejstrik.model.CompanyDataRZP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
                Jsoup.connect(url).get()

            }
        } catch (e: Exception) {
            null
        }
    }
}