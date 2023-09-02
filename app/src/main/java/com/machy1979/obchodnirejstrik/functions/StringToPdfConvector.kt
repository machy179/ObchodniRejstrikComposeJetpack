package com.machy1979.obchodnirejstrik.functions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import java.io.FileOutputStream
import com.itextpdf.text.pdf.PdfWriter
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.model.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class StringToPdfConvector {

    companion object {

      //  val font = FontFactory.getFont("Times-Roman", "Cp1250", true)
        val font = FontFactory.getFont(FontFactory.COURIER, BaseFont.CP1250, true) //české znaky umí: Courier

        val boldFont = Font(font)
        val nadpisFontSize = 16f
        val nadpisFont = Font(font.baseFont, nadpisFontSize, Font.BOLD)
        lateinit var table: PdfPTable
        lateinit var context: Context


        fun convertToPdf(outputPath: String, context: Context, companyDataOR: CompanyData? = null, companyDataRZP: CompanyDataRZP? = null, companyDataRES: CompanyDataRES? = null): File? {
            val document = Document()
            this.context = context
            println("Oprávnění .....444444")

           if (PermissionsChecker.checkStoragePermissions()) {
                println("Oprávnění pro zápis uděleno")
               try {
                   // Zkontrolujte, zda existuje složka pro ukládání PDF souboru
                   println("Oprávnění pro zápis uděleno")
                   val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                   if (!downloadsDir.exists()) {
                       downloadsDir.mkdirs()
                   }

                   // Nastaví cestu k výstupnímu PDF souboru
                   val pdfFile = File(downloadsDir, outputPath)

                   // Pokud již soubor existuje, smažeme ho, abychom zajistili, že nedojde k přepsání
                   if (pdfFile.exists()) {
                       pdfFile.delete()
                   }

                   PdfWriter.getInstance(document, FileOutputStream(pdfFile))
                   document.open()


                   nastavVlastnostiTabulky()

                   if (!(companyDataOR==null)) companyDataOR?.let { vkladejUdajeDoTabulkyOR(it) }
                   if (!(companyDataRZP==null)) companyDataRZP?.let { vkladejUdajeDoTabulkyRZP(it) }
                   if (!(companyDataRES==null)) companyDataRES?.let { vkladejUdajeDoTabulkyRES(it) }

                   document.add(table)
                   document.close()

                   println("Text byl úspěšně převeden do PDF souboru.")

                   return pdfFile

               } catch (e: Exception) {
                   println("Chyba při převodu textu do PDF: ${e.message}")
                   return null
               }
            } else {
               println("Oprávnění pro zápis NEuděleno")
               return null
            }


        }

        fun createInMemoryPdf(context: Context, companyDataOR: CompanyData? = null, companyDataRZP: CompanyDataRZP? = null, companyDataRES: CompanyDataRES? = null): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val document = Document()
            this.context = context

            try {

                val pdfWriter2 = PdfWriter.getInstance(document, byteArrayOutputStream)
                pdfWriter2.setPdfVersion(PdfWriter.PDF_VERSION_1_7) //stále to nefunguje a ukládá to do pdf verze 1.4
                document.open()


                nastavVlastnostiTabulky()

                if (!(companyDataOR==null)) companyDataOR?.let { vkladejUdajeDoTabulkyOR(it) }
                if (!(companyDataRZP==null)) companyDataRZP?.let { vkladejUdajeDoTabulkyRZP(it) }
                if (!(companyDataRES==null)) companyDataRES?.let { vkladejUdajeDoTabulkyRES(it) }
               
                document.add(table)
                document.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return byteArrayOutputStream.toByteArray()
        }

        private fun vkladejUdajeDoTabulkyRZP(companyDataRZP: CompanyDataRZP) {
            vlozNadpisDoTabulky("REJSTŘÍK ŽIVNOSTENSKÉHO PODNIKÁNÍ")
            vlozRadekDoTabulkyJedenText("Základní údaje", true, true, false)
            vlozRadekDoTabulkyDvaTexty("Název: ",companyDataRZP.name)
            vlozRadekDoTabulkyDvaTexty("ICO: ",companyDataRZP.ico)
            vlozRadekDoTabulkyDvaTexty("DIC: ",companyDataRZP.dic)
            vlozRadekDoTabulkyDvaTexty("Adresa: ",companyDataRZP.address)
            vlozRadekDoTabulkyDvaTexty("Právní forma:",companyDataRZP.pravniForma)
            vlozRadekDoTabulkyDvaTexty("Typ subjektu:",companyDataRZP.typSubjektu)
            vlozRadekDoTabulkyDvaTexty("Právní forma: ",companyDataRZP.pravniForma)
            vlozRadekDoTabulkyDvaTexty("Evidující úřad:",companyDataRZP.evidujiciUrad)
            vlozRadekDoTabulkyDvaTexty("Vznik první živnosti:",companyDataRZP.vznikPrvniZivnosti)

            if(companyDataRZP.osoby.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Společníci s vkladem", true, true, false)
                vypisOsoby(companyDataRZP.osoby)
            }

           if(companyDataRZP.zivnosti.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Ostatní skutečnosti", true, true, false)
               companyDataRZP.zivnosti.forEach { polozka ->
                   vlozRadekDoTabulkyDvaTexty("Název: ",polozka.nazevZivnosti)
                   vlozRadekDoTabulkyDvaTexty("Druh: ",polozka.druhZivnosti)
                   vlozRadekDoTabulkyDvaTexty("Vznik oprávnění: ",polozka.vznikOpravneni)
                   vlozRadekDoTabulkyDvaTexty("Obory: ","")
                   polozka.obory.forEach { polozka2 ->
                       vlozRadekDoTabulkyJedenText(polozka2, false, false)
                   }

                }
            }


        }

        private fun vkladejUdajeDoTabulkyRES(companyDataRES: CompanyDataRES) {

        }

        private fun vkladejUdajeDoTabulkyOR(companyDataOR: CompanyData) {
            vlozNadpisDoTabulky("OBCHODNÍ REJSTŘÍK")
            vlozRadekDoTabulkyJedenText("Základní údaje", true, true, false)
            vlozRadekDoTabulkyDvaTexty("Název: ",companyDataOR.name)
            vlozRadekDoTabulkyDvaTexty("ICO: ",companyDataOR.ico)
            vlozRadekDoTabulkyDvaTexty("DIC: ",companyDataOR.dic)
            vlozRadekDoTabulkyDvaTexty("Sídlo: ",companyDataOR.address)
            vlozRadekDoTabulkyDvaTexty("Datum zápisu: ",companyDataOR.datumZapisu)
            vlozRadekDoTabulkyDvaTexty("Stav sbujektu: ",companyDataOR.stavSubjektu)
            vlozRadekDoTabulkyDvaTexty("Právní forma: ",companyDataOR.pravniForma)
            vlozRadekDoTabulkyDvaTexty("Datum zápisu: ",companyDataOR.datumZapisu)
            vlozRadekDoTabulkyDvaTexty("Soud: ",companyDataOR.soud)
            vlozRadekDoTabulkyDvaTexty("Spisová značka: ",companyDataOR.spisovaZnacka)

            if(companyDataOR.predmetPodnikani.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Předmět podnikání", true, true, false)
                companyDataOR.predmetPodnikani.forEach { polozka ->
                    vlozRadekDoTabulkyJedenText(polozka, false, false)
                }
            }

            if(companyDataOR.ostatniSkutecnosti.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Ostatní skutečnosti", true, true, false)
                companyDataOR.ostatniSkutecnosti.forEach { polozka ->
                    vlozRadekDoTabulkyJedenText(polozka, false, false)
                }
            }


            vlozRadekDoTabulkyJedenText(" ", true, false, false)
            vlozRadekDoTabulkyJedenText(" ", true, false, false)
            vlozRadekDoTabulkyJedenText("Kapitál", true, true, false)
            vlozRadekDoTabulkyDvaTexty("Vklad: ",companyDataOR.vklad)
            vlozRadekDoTabulkyDvaTexty("Splaceno: ",companyDataOR.splaceno)
            vlozRadekDoTabulkyDvaTexty("Akcie: ",companyDataOR.akcie)

            if(companyDataOR.statutarniOrganOsoby.size>0 || companyDataOR.statutarniOrganFirmy.size>0 || companyDataOR.statutarniOrganSkutecnosti.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Statutární orgán", true, true, false)
                vypisOsoby(companyDataOR.statutarniOrganOsoby)
                vypisFirmy(companyDataOR.statutarniOrganFirmy)
                companyDataOR.statutarniOrganSkutecnosti.forEachIndexed { index, polozka ->
                    if (index==0) vlozRadekDoTabulkyJedenText(" ", true, false, false)
                    vlozRadekDoTabulkyJedenText(polozka, false, false)
                }
            }

            if(companyDataOR.prokura.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Prokura", true, true, false)
                vypisOsoby(companyDataOR.prokura)
            }

            if(companyDataOR.dozorciRada.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Dozorčí rada", true, true, false)
                vypisOsoby(companyDataOR.dozorciRada)
            }

            if(companyDataOR.spolecniciSVklademOsoby.size>0 || companyDataOR.spolecniciSVklademFirmy.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Společníci s vkladem", true, true, false)
                vypisOsoby(companyDataOR.spolecniciSVklademOsoby)
                vypisFirmy(companyDataOR.spolecniciSVklademFirmy)
            }

            if(companyDataOR.akcionariOsoby.size>0 || companyDataOR.akcionariFirmy.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Akcionáři", true, true, false)
                vypisOsoby(companyDataOR.akcionariOsoby)
                vypisFirmy(companyDataOR.akcionariFirmy)
            }

            if(companyDataOR.likvidaceOsoby.size>0 || companyDataOR.likvidaceFirmy.size>0) {
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText(" ", true, false, false)
                vlozRadekDoTabulkyJedenText("Likvidace", true, true, false)
                vypisOsoby(companyDataOR.likvidaceOsoby)
                vypisFirmy(companyDataOR.likvidaceFirmy)
            }


        }

        private fun vlozNadpisDoTabulky(nadpis: String) {

                val cell = PdfPCell(Paragraph(nadpis, nadpisFont ))
                cell.borderColor = BaseColor.WHITE
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
                cell.colspan = 2 //spojí 2 buňky horizontálně
                table.addCell(cell)
            vlozRadekDoTabulkyJedenText("(výpis ke dni: "+getCurrentDate()+")", false, true, false, false)
            vlozRadekDoTabulkyJedenText(" ", true, false, false)



        }

        private fun vypisFirmy(firmy: MutableList<Firma>) {
            firmy.forEachIndexed { index, polozka ->
                vlozRadekDoTabulkyDvaTexty("Firma: ",polozka.name)
                vlozRadekDoTabulkyDvaTexty("Ico: ",polozka.ico)
                vlozRadekDoTabulkyDvaTexty("Sídlo: ",polozka.address)
                if(!(polozka.vklad=="")) vlozRadekDoTabulkyDvaTexty("Vklad: ",polozka.vklad+" Kč")
                if(!(polozka.splaceno=="")) vlozRadekDoTabulkyDvaTexty("Splaceno: ",polozka.splaceno +" %")
                if(!(polozka.obchodniPodil=="")) vlozRadekDoTabulkyDvaTexty("Obchodní podíl: ",polozka.obchodniPodil)

                if (!(index == firmy.size - 1)) { //když to není poslední průchod, tak udělá mezeru
                    vlozRadekDoTabulkyMezeru()

                }

            }
        }

        private fun vypisOsoby(osoby: MutableList<Osoba>) {
            osoby.forEachIndexed { index, polozka ->
                vlozRadekDoTabulkyDvaTexty("Funkce: ",polozka.funkce)
                vlozRadekDoTabulkyDvaTexty("Jméno: ",polozka.jmeno + " "+ polozka.prijmeni + " " +polozka.titulyPredJmenem)
                vlozRadekDoTabulkyDvaTexty("Datum narození: ",polozka.datNar)
                vlozRadekDoTabulkyDvaTexty("Bydliště: ",polozka.adresa)
                if(!(polozka.clenstviOd=="")) vlozRadekDoTabulkyDvaTexty("Členství od: ",polozka.clenstviOd)
                if(!(polozka.veFunkciOd=="")) vlozRadekDoTabulkyDvaTexty("Ve funkci od: ",polozka.veFunkciOd)
                if(!(polozka.vklad=="")) vlozRadekDoTabulkyDvaTexty("Vklad: ",polozka.vklad+" Kč")
                if(!(polozka.splaceno=="")) vlozRadekDoTabulkyDvaTexty("Splaceno: ",polozka.splaceno +" %")
                if(!(polozka.obchodniPodil=="")) vlozRadekDoTabulkyDvaTexty("Obchodní podíl: ",polozka.obchodniPodil)

                polozka.poznamky.forEach { polozka2->
                    vlozRadekDoTabulkyJedenText(polozka2, false, false)
                }

                if (!(index == osoby.size - 1)) { //když to není poslední průchod, tak udělá mezeru
                    vlozRadekDoTabulkyMezeru()
                }

            }
        }

        private fun nastavVlastnostiTabulky() {
            table = PdfPTable(2)
            boldFont.style = Font.BOLD
            val columnWidths = floatArrayOf(2f, 8f)
            table.setWidths(columnWidths)
            table.widthPercentage = 95f
        }

        fun vlozRadekDoTabulkyDvaTexty(levyText: String, pravyText: String, ) {
            val cellLeft = PdfPCell(Paragraph(levyText, boldFont ))
            cellLeft.borderColor = BaseColor.WHITE
            cellLeft.backgroundColor = BaseColor(ContextCompat.getColor(context , R.color.pozadi_pdf_vypisu_radek))
            cellLeft.setBorder(Rectangle.TOP or Rectangle.BOTTOM or Rectangle.LEFT)
            table.addCell(cellLeft)

            val cellRight = PdfPCell(Paragraph(pravyText, font ))
            cellRight.borderColor = BaseColor.WHITE
            cellRight.backgroundColor = BaseColor(ContextCompat.getColor(context , R.color.pozadi_pdf_vypisu_radek))
            cellRight.setBorder(Rectangle.TOP or Rectangle.BOTTOM or Rectangle.RIGHT)
            table.addCell(cellRight)


        }

        fun vlozRadekDoTabulkyJedenText(text: String, bold: Boolean, stred: Boolean, pozadi: Boolean = true, pomlcka: Boolean = true) {
            val cell = if (bold) PdfPCell(Paragraph(text, boldFont )) else PdfPCell(Paragraph(if (pomlcka) "- " +text else "" +text, font )) //když to bude bold, tak je to nadpis, v opačném případě je to normální položka, tak se tam přidá i pomlčka, ale když to bude pod nadpisem, tedy údaj o datumu pořízení, tak pomlcka bude false a nepřidá se tam
            cell.borderColor = BaseColor.WHITE
            if (stred) cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            if (pozadi) cell.backgroundColor = BaseColor(ContextCompat.getColor(context , R.color.pozadi_pdf_vypisu_radek))
            cell.colspan = 2 //spojí 2 buňky horizontálně
            table.addCell(cell)

        }

        private fun vlozRadekDoTabulkyMezeru() {
            val cell = PdfPCell(Paragraph(" ", font ))
            cell.borderColor = BaseColor.WHITE
            cell.colspan = 2 //spojí 2 buňky horizontálně
            table.addCell(cell)
        }

        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Měsíce jsou indexované od nuly, proto +1
            val year = calendar.get(Calendar.YEAR)

            return "$day.$month.$year"
        }

    }


}