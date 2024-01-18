package com.machy1979.obchodnirejstrik.functions

import android.content.Context
import android.util.Log
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.model.*
import org.json.JSONObject
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class RozparzovaniDatDotazRZP {
    companion object {
        lateinit var context: Context
        fun vratCompanyData(jsonObject: JSONObject, context: Context): CompanyDataRZP {
            var name = jsonObject.optString("obchodniJmeno", " ") ?: " "
            val ico = jsonObject.optString("ico", " ") ?: " "

            //adresa
            var address: String = " "
            jsonObject.optJSONArray("adresySubjektu")?.let { addressesArray ->
                for (i in 0 until addressesArray.length()) {
                    val adresa = addressesArray.getJSONObject(i)
                    // Zpracování obchodního jména, provedete s ním, co je potřeba
                    if (!adresa.has("datumVymazu")) {
                        address = adresa?.optString("textovaAdresa", " ") ?: " "
                    }
                }


            }?: run {
                address = " "
            }

            //právní forma
            val pravniFormaZnacka= jsonObject.optString("pravniForma", " ") ?: " "
            val pravniFormyCiselnikArray = context.resources.getStringArray(R.array.pravni_forma)
            val pravniFormyCiselnikMap = pravniFormyCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val pravniForma = pravniFormyCiselnikMap[pravniFormaZnacka] ?: pravniFormaZnacka


            var typSubjektu= jsonObject.optString("typSubjektu", " ") ?: " "
            when (typSubjektu) {
                "F" -> typSubjektu ="fyzická osoba"
                "P" -> typSubjektu ="právnická osoba"
            }
            val evidujiciUrad= jsonObject.optString("zivnostenskyUrad", " ") ?: " " //musím ještě udělat číselník a namapovat to obdobně, jako právní forma
            val vznikPrvniZivnosti= jsonObject.optString("datumVzniku", " ") ?: " "

            //osoba podnikatel
            val listOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            jsonObject.optJSONObject("osobaPodnikatel")?.let {
                Log.i("vlozOobuPodnikatel","111")
                listOsoby.add(vlozOsobu(it))
            }

            //angažované osoby
            jsonObject.optJSONArray("angazovaneOsoby")?.let { angazovaneOsobyArray ->
                for (i in 0 until angazovaneOsobyArray.length()) {
                    val angazovanaOsobaObject = angazovaneOsobyArray.optJSONObject(i)

                    if (!angazovanaOsobaObject.has("platnostDo")) {
                        listOsoby.add(vlozOsobu(angazovanaOsobaObject))
                    }
                }
            }

            //živnosti
            val listZivnosti: MutableList<Zivnosti> = mutableListOf<Zivnosti>()
            jsonObject.optJSONArray("zivnosti")?.let { zivnostiArray ->
                for (i in 0 until zivnostiArray.length()) {
                    val zivnostiObject =zivnostiArray.optJSONObject(i)
                    if (!zivnostiObject.has("datumZaniku")) {
                        val nazevZivnosti = zivnostiObject?.optString("predmetPodnikani") ?: " "
                        var druhZivnosti = zivnostiObject?.optString("druhZivnosti") ?: " "
                        when (druhZivnosti) {
                            "R" -> druhZivnosti ="Ohlašovací řemeslná"
                            "L" -> druhZivnosti="Ohlašovací volná"
                            "K" -> druhZivnosti="Koncesovaná"
                            "V" -> druhZivnosti="Ohlašovací vázaná"
                        }
                        val vznikOpravneni = zivnostiObject?.optString("datumVzniku") ?: " "
                        val obory: MutableList<String> = mutableListOf<String>()
                        zivnostiObject.optJSONArray("oboryCinnosti")?.let { oboryCinnostiArray ->
                            for (i in 0 until oboryCinnostiArray.length()) {
                                val oborObject =oboryCinnostiArray.optJSONObject(i)
                                if (!oborObject.has("datumZaniku")) {
                                    obory.add(oborObject?.optString("oborNazev") ?: " ")
                                }
                            }
                        }
                        listZivnosti.add(Zivnosti(nazevZivnosti, druhZivnosti,vznikOpravneni,obory))
                    }
                }
            }


/*            //osoby
            val listOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyOsoby = document.select("D|Osoby").select("D|Osoba")
            zaznamyOsoby.forEach() {
                var address =  ""
                listOsoby.add(vlozOsobu(it,address))
                when (listOsoby.last().funkce) {
                    "P" -> listOsoby.last().funkce="podnikatel"
                    "S" -> listOsoby.last().funkce="člen statutárního orgánu"
                }

            }

            //živnosti
            val listZivnosti: MutableList<Zivnosti> = mutableListOf<Zivnosti>()
            val zaznamyZivnosti = document.select("D|ZI").select("D|Z")
            zaznamyZivnosti.forEach() {
                val nazevZivnosti = it.select("D|PP").first()?.text() ?: " "
                var druhZivnosti = it.select("D|Druh").first()?.text() ?: " "
                when (druhZivnosti) {
                    "R" -> druhZivnosti ="Ohlašovací řemeslná"
                    "L" -> druhZivnosti="Ohlašovací volná"
                    "K" -> druhZivnosti="Koncesovaná"
                    "V" -> druhZivnosti="Ohlašovací vázaná"
                }
                val vznikOpravneni = it.select("D|Vznik").first()?.text() ?: " "
                val obory: MutableList<String> = mutableListOf<String>()
                val zaznamyObory = it.select("D|T")
                zaznamyObory.forEach() {
                    obory.add(it.text())
                }

                listZivnosti.add(Zivnosti(nazevZivnosti, druhZivnosti,vznikOpravneni,obory))
            }


            val companyDataRZP = CompanyDataRZP(name, ico,"", address, pravniForma,typSubjektu,evidujiciUrad,vznikPrvniZivnosti,
            listOsoby,listZivnosti)*/

            val companyDataRZP = CompanyDataRZP(name, ico,"", address, pravniForma,typSubjektu,evidujiciUrad,vznikPrvniZivnosti,
                listOsoby,listZivnosti)

            return companyDataRZP
        }

        private fun vlozOsobu(it: Element?, address: String, listZaznamy: MutableList<String> = mutableListOf<String>()): Osoba {
            return Osoba(
                it?.select("D|TP")?.text() ?: "",
                it?.select("D|J")?.text() ?: "",
                it?.select("D|P")?.text()?: "",
                it?.select("D|Role")?.text() ?: "",

                it?.select("D|DN")?.text()?: "",
                address,
                listZaznamy,
                it?.select("D|CLE")?.text() ?: "",
                it?.select("D|VF")?.text() ?: "",
                it?.select("D|VK")?.select("D|KC")?.text() ?: "",
                it?.select("D|SPL")?.select("D|PRC")?.text() ?: "",
                it?.select("D|OP")?.select("D|T")?.text() ?: ""

            )
        }

        fun vratErrorHlasku(document: Document): String {
            val errorHlaska = document.select("D|ET").first()?.text() ?: " "
            return errorHlaska
        }

        fun vratAdresu(document: Elements): String {
            var address = document.select("D|NU").first()?.text() ?: ""
            document.select("D|CD").first()?.text()?.let {
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


/*            address =address +" "+ (document.select("D|CD").first()?.text() ?: "")//
            address =address +", "+ (document.select("D|NCO").first()?.text() ?: " ")
            address =address +", "+ (document.select("D|N").first()?.text() ?: " ")
            address =address +", "+ (document.select("D|NS").first()?.text() ?: " ")*/
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

        private fun vlozOsobu(it: JSONObject): Osoba {
            var typAngazma = it?.optString("typAngazma", " ") ?: ""
            when (typAngazma) {
                "PODNIKATEL_RZP" -> typAngazma ="podnikatel"
                "STATUTARNI_ZASTUPCE_RZP" -> typAngazma ="statutární zástupce"
            }

            return Osoba(
                it?.optString("titulPredJmenem", "") ?: "",
                it?.optString("jmeno", " ") ?: "",
                it?.optString("prijmeni", " ") ?: "",
                typAngazma,

                it?.optString("datumNarozeni", " ") ?: "",
                "",
                mutableListOf<String>(),
                it?.optString("platnostOd", " ") ?: "",
                "",
                "",
                "",
                "",
                ""

            )
        }
    }
}