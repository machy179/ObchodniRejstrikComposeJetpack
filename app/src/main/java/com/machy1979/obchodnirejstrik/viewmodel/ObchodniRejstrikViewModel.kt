package com.machy1979.obchodnirejstrik.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.functions.*
import com.machy1979.obchodnirejstrik.model.CompanyData

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder


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
                Log.i("aaaa", "ICO: " + ico)
                val document = getAresDataIco(ico)
                if (document != null) {

                     _companyData.value = RozparzovaniDatDotazDleIco.vratCompanyData(document)
                    if (_companyData.value.ico == " ") {
                        _errorMessage.value = RozparzovaniDatDotazDleIco.vratErrorHlasku(document)
                    } else  {
                        _errorMessage.value = ""

                    }
                } else {
                    _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                }

            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessage.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitani.value = false
        }
    }

    private suspend fun getAresDataIco(ico: String): Document? {
        val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_bas.cgi?ico=$ico"
        return try {
            withContext(Dispatchers.IO) {
                Log.i("aaaa", "10")
                Jsoup.connect(url).get()

            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadDataNazev(nazev: String) {
        _nacitani.value = true

        viewModelScope.launch {
            try {
                val document = getAresDataNazev(nazev)
                if (document != null) {
                    val zaznamy = document.select("are|Zaznam")
                    zaznamy.forEach() {
                        companysData.add(RozparzovaniDatProCompanysData.vratCompanyData(it))
                    }
                    if (zaznamy.size==0) {
                        _errorMessage.value = RozparzovaniDatProCompanysData.vratErrorHlasku(document)
                        if (_errorMessage.value ==" ") {
                            _errorMessage.value = "Subjekt nenalezen"
                        }
                    } else  _errorMessage.value = ""
                } else {
                    _errorMessage.value = "Nepodařilo se načíst data z ARESu"
                }
            } catch (e: Exception) {
                Log.i("aaaa", e.toString())
                _errorMessage.value = "Nepodařilo se načíst data z ARESu"
            }
            _nacitani.value = false
        }
    }

    private suspend fun getAresDataNazev(nazev: String): Document? {
        val url = "https://wwwinfo.mfcr.cz/cgi-bin/ares/darv_std.cgi?obchodni_firma=${URLEncoder.encode(nazev, "iso-8859-2")}&max_pocet=200"
        return try {
            withContext(Dispatchers.IO) {
                Jsoup.connect(url).get()
            }
        } catch (e: Exception) {
            null
        }
    }




}
