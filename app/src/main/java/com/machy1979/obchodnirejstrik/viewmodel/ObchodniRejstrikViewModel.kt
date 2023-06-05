package com.machy1979.obchodnirejstrik.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.functions.*
import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import com.machy1979.obchodnirejstrik.model.CompanyDataRZP
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

    //pro výpis OR - obchodní rejstřík
    private val _companyDataFromOR = MutableStateFlow(CompanyData())
    val companyDataFromOR: StateFlow<CompanyData> = _companyDataFromOR
    private var _nacitaniOR = MutableStateFlow(false)
    val nacitaniOR: StateFlow<Boolean> = _nacitaniOR
    private val _errorMessageOR = MutableStateFlow<String>("")
    val errorMessageOR: StateFlow<String> = _errorMessageOR
    private val _buttonClickedOR = MutableStateFlow<Boolean>(false)
    val buttonClickedOR: StateFlow<Boolean> =_buttonClickedOR

    //pro výpis RZP - rejstřík živnostenského podnikání
    private val _companyDataFromRZP = MutableStateFlow(CompanyDataRZP())
    val companyDataFromRZP: StateFlow<CompanyDataRZP> = _companyDataFromRZP
    private var _nacitaniRZP = MutableStateFlow(false)
    val nacitaniRZP: StateFlow<Boolean> = _nacitaniRZP
    private val _errorMessageRZP = MutableStateFlow<String>("")
    val errorMessageRZP: StateFlow<String> = _errorMessageRZP
    private val _buttonClickedRZP = MutableStateFlow<Boolean>(false)
    val buttonClickedRZP: StateFlow<Boolean> =_buttonClickedRZP

    //pro výpis RES - rejstřík ekonomických subjektů
    private val _companyDataFromRES = MutableStateFlow(CompanyDataRES()) //tady nastavit na CompanyDataRES - tuhle třídu vytvořit
    val companyDataFromRES: StateFlow<CompanyDataRES> = _companyDataFromRES //to samé tady
    private var _nacitaniRES = MutableStateFlow(false)
    val nacitaniRES: StateFlow<Boolean> = _nacitaniRES
    private val _errorMessageRES = MutableStateFlow<String>("")
    val errorMessageRES: StateFlow<String> = _errorMessageRES
    private val _buttonClickedRES = MutableStateFlow<Boolean>(false)
    val buttonClickedRES: StateFlow<Boolean> =_buttonClickedRES






    fun vynulujCompanysData() {
        _companysData.clear()
    }

    fun loadDataIco(ico: String) {
        _nacitani.value = true
        loadDataIcoOR(ico) //paralelně ještě bude načítat už data přímo z OR
        loadDataIcoRZP(ico) //paralelně ještě bude načítat už data přímo z RZP
    //    loadDataIcoRES(ico) //paralelně ještě bude načítat už data přímo z Res
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
                Log.i("aaaa", "nazev: " + nazev)
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
        Log.i("aaaa", url)
        return try {
            withContext(Dispatchers.IO) {
                Log.i("aaaa", "10")
                Jsoup.connect(url).get()

            }
        } catch (e: Exception) {
            null
        }
    }
//****************************************************************************OR:
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

//****************************************************************************RZP:
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

    //****************************************************************************RES:
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
