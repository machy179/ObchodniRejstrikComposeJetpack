package com.machy1979.obchodnirejstrik.functions

import android.content.Context

import android.util.Log

import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.Firma
import com.machy1979.obchodnirejstrik.model.Osoba
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class RozparzovaniDatDotazOR {
    companion object {
        lateinit var context: Context
        var pomocnyCounter = 0
        fun vratCompanyData(document: Document, context: Context): CompanyData {

            Log.i("chybaaa2: ",document.toString())
            this.context = context
            val name = document.select("D|OF").first()?.text() ?: " "
            val ico = document.select("D|ICO").first()?.text() ?: " "
            var address =  vratAdresu(document.select("D|SI"))

            val stavSubjektu= document.select("D|SSU").first()?.text() ?: " "
            val pravniForma= document.select("D|NPF").first()?.text() ?: " "
            val datumZapisu= document.select("D|DZOR").first()?.text() ?: " "
            val soud: String= document.select("D|REG").select("D|T")?.text() ?: " "
            val spisovaZnacka= document.select("D|REG").select("D|OV")?.text() ?: " "

            val vklad: String = document.select("D|KC").first()?.text() ?: " "
            val splaceno: String = document.select("D|PRC").first()?.text() ?: " "
            var akcie: String = document.select("D|DA").first()?.text() ?: " "

            document.select("D|H").first()?.let {akcie =akcie + ", hodnota: "+it.text()}
            document.select("D|Pocet").first()?.let {akcie =akcie + ", počet akcií: "+it.text()}
            document.select("D|PD").first()?.let {akcie =akcie + ", "+it.text()}


            //předmět podnikání
            val listPredmetPodnikani: MutableList<String> = mutableListOf<String>()
            val zaznamyPP = document.select("D|CIN").select("D|T")
            zaznamyPP.forEach() {
                listPredmetPodnikani.add(it.text())
            }

            //Ostatní skutečnosti
            val listOstatniSkutecnosti: MutableList<String> = mutableListOf<String>()
            val zaznamyOstSkusenosti = document.select("D|OSK").select("D|T")
            zaznamyOstSkusenosti.forEach() {
                listOstatniSkutecnosti.add(it.text())
            }

            //statutární orgán osoby
            val listStatutarniOrganOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyStatOrganOsoby = document.select("D|SO").select("D|CSO")
            zaznamyStatOrganOsoby.forEach() {
                var address = vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listStatutarniOrganOsoby.add(vlozOsobu(it, address))
                }
            }
            //statutární orgán firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listStatutarniOrganFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamyStatOrganFirmy = document.select("D|SO").select("D|CSO")
            zaznamyStatOrganFirmy.forEach() {
                var address = vratAdresu(it.select("D|PO"))
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listStatutarniOrganFirmy.add(vlozFirmu(it, address))
                }
            }

            //statutární orgán ostatní skutečnosti
            val listStatutarniOrganSkutecnosti: MutableList<String> = mutableListOf<String>()
            val zaznamyStatutarniOrganSkutecnosti = document.select("D|SO").select("D|T")
            zaznamyStatutarniOrganSkutecnosti.forEach() {
                listStatutarniOrganSkutecnosti.add(it.text())

            }

            //prokura
            val listProkura: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyProkura = document.select("D|PRO").select("D|PRA")
            zaznamyProkura.forEach() {
                var address =  vratAdresu(it.allElements)

                Log.i("aaaa", "PROKURA: " + it.select("D|P").text())

                val listZaznamy: MutableList<String> = mutableListOf<String>()
                var zaznamyPoznamky = it.select("D|T")
                zaznamyPoznamky.forEach(){
                    listZaznamy.add(it.text())
                }


                listProkura.add(vlozOsobu(it,address,listZaznamy))
            }

            //dozorčí rada
            val listDozorciRada: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyDozorciRada = document.select("D|DR").select("D|CDR")
            zaznamyDozorciRada.forEach() {
                var address =  vratAdresu(it.allElements)
                listDozorciRada.add(vlozOsobu(it,address))

            }

            //společníci s vkladem osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listspolecniciSVklademOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamySpolecniciSVklademOsoby = document.select("D|SSV").select("D|SS")
            zaznamySpolecniciSVklademOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listspolecniciSVklademOsoby.add(vlozOsobu(it,address))
                }
            }

            //společníci s vkladem firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listspolecniciSVklademFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamySpolecniciSVklademFirmy = document.select("D|SSV").select("D|SS")
            zaznamySpolecniciSVklademFirmy.forEach() {
                var address =  vratAdresu(it.select("D|PO"))
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listspolecniciSVklademFirmy.add(vlozFirmu(it,address))
                }
            }

            //akcioáři osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listAkcionariOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyAkcionariOsoby = document.select("D|AKI").select("D|AKR")
            zaznamyAkcionariOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listAkcionariOsoby.add(vlozOsobu(it,address))
                }
            }

            //akcionari firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listAkcionariFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamyAkcionariFirmy = document.select("D|AKI").select("D|AKR")
            Log.i("chybaaa: ",document.toString())
            zaznamyAkcionariFirmy.forEach() {
                var address =  vratAdresu(it.select("D|PO"))
                Log.i("chybaaa2: ",it.select("D|PO").select("D|OF").text())
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listAkcionariFirmy.add(vlozFirmu(it,address))
                }
            }

            //likvidace osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listLikvidaceOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyLikvidaceOsoby = document.select("D|LI").select("D|LIR")
            zaznamyLikvidaceOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listLikvidaceOsoby.add(vlozOsobu(it,address))
                }
            }

            //likvidace firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listLikvidaceFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamyLikvidaceFirmy = document.select("D|LI").select("D|LIR")
            Log.i("chybaaa: ",document.toString())
            zaznamyLikvidaceFirmy.forEach() {
                var address =  vratAdresu(it.select("D|PO"))
                Log.i("chybaaa2: ",it.select("D|PO").select("D|OF").text())
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listLikvidaceFirmy.add(vlozFirmu(it,address))
                }
            }


            //konečné vložení do companyData
            val companyData = CompanyData(name, ico,"", address,listPredmetPodnikani,
                listOstatniSkutecnosti,listStatutarniOrganOsoby, listStatutarniOrganFirmy,listStatutarniOrganSkutecnosti,listProkura,
                listDozorciRada,listspolecniciSVklademOsoby, listspolecniciSVklademFirmy, listAkcionariOsoby,
                listAkcionariFirmy, listLikvidaceOsoby, listLikvidaceFirmy,stavSubjektu, pravniForma,
                datumZapisu,soud,spisovaZnacka,vklad,splaceno, akcie)
            return companyData
        }

        private fun vlozOsobu(it: Element?, address: String, listZaznamy: MutableList<String> = mutableListOf<String>()): Osoba {
            return Osoba(
                it?.select("D|FO")?.select("D|TP")?.text() ?: "",
                it?.select("D|FO")?.select("D|J")?.text() ?: "",
                it?.select("D|FO")?.select("D|P")?.text()?: "",
                it?.select("D|F")?.text() ?: "", //tady byla změna z it?.select("D|FO")?.select("D|F")?.text() ?: "",

                it?.select("D|FO")?.select("D|DN")?.text()?: "",
                address,
                listZaznamy,
                it?.select("D|CLE")?.text() ?: "",
                it?.select("D|VF")?.text() ?: "",
                it?.select("D|VK")?.select("D|KC")?.text() ?: "",
                it?.select("D|SPL")?.select("D|PRC")?.text() ?: "",
                it?.select("D|OP")?.select("D|T")?.text() ?: ""

            )
        }



        fun vratAdresu(document: Elements): String {
            var address = document.select("D|NU").first()?.text() ?: ""
            document.select("D|CD").first()?.text()?.let {
                if(address=="") {
                    address =address +it
                } else address =address +" "+it
            }

            document.select("D|CA").first()?.text()?.let {
                if(address=="") {
                    address =address +it
                } else address =address +" "+it
            }

            document.select("D|NCO").first()?.text()?.let {
                address =address +", "+it
            }
            document.select("D|N").first()?.text()?.let {
                address =address +", "+it
            }
            document.select("D|PSC").first()?.text()?.let {
                address =address +", "+it
            }
            document.select("D|NS").first()?.text()?.let {
                address =address +", "+it
            }




            if (pomocnyCounter== 0) {
                println("GPS-mapa .....1")
               // StringToGpsToMap.presmerujZAdresyNaMapy(address, context)
                println("GPS-mapa .....1konec")
                pomocnyCounter++
            }

            return address
        }

        private fun vlozFirmu(it: Element?, address: String): Firma {
            return Firma(
                it?.select("D|PO")?.select("D|ICO")?.text()?: "",
                it?.select("D|PO")?.select("D|OF")?.text() ?: "",
                address,
                it?.select("D|VK")?.select("D|KC")?.text() ?: "",
                it?.select("D|SPL")?.select("D|PRC")?.text() ?: "",
                it?.select("D|OP")?.select("D|T")?.text() ?: ""
            )
        }

        fun vratErrorHlasku(document: Document): String {
            val errorHlaska = document.select("D|ET").first()?.text() ?: " "
            return errorHlaska
        }


    }
}