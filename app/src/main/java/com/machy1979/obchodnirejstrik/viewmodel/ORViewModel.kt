package com.machy1979.obchodnirejstrik.viewmodel

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazOR
import com.machy1979.obchodnirejstrik.functions.StringToPdfConvector
import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.screens.components.AlertDialogWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*


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


    fun saveToPdf(context: Context) {
  //  fun share(context: Context) {

        // Aplikační stav pro sledování stavu oprávnění
     //   var permissionGranted = MutableStateFlow(false)
     //   val permissionGranted_2: StateFlow<Boolean> = permissionGranted

   //     println("Oprávnění .....111111")
        // Pokud jsou oprávnění udělena, provede se tato část kódu
   //     if (permissionGranted_2.value) {
     //       println("Oprávnění .....333333")
            // Zde vložte váš kód, který se má provést po udělení oprávnění
            val pdfFileName = companyDataFromOR.value.name + ".pdf"

            val file = StringToPdfConvector.convertToPdf(pdfFileName,context,companyDataFromOR.value)
/*        } else {
            println("Oprávnění .....222222")
            // Zde se zobrazí dialog pro žádost o oprávnění
            AlertDialog.Builder(context)
                .setTitle("Potřebujeme povolení")
                .setMessage("Pro ukládání souborů potřebujeme vaše povolení. Klikněte na tlačítko Povolit pro pokračování.")
                .setPositiveButton("Povolit") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    ContextCompat.startActivity(context, intent, null)
                    println("Alert dialog PDF: až teď se toto provede")
                }
                .setNegativeButton("Zrušit") { _, _ -> }
                .show()
        }*/
        // Uložení PDF obsahu do souboru v interním úložišti Download složky
      //  val pdfFileName = companyDataFromOR.value.name + ".pdf"

        //val file = StringToPdfConvector.convertToPdf(pdfFileName,context,companyDataFromOR.value)



    }

    @Composable
    fun saveToPdf2(context: Context) {

    }

}