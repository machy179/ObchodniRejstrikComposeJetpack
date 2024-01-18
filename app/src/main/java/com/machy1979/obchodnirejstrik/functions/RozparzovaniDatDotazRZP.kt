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

            //evdidujici úřad
            val evidujiciUradZnacka= jsonObject.optString("zivnostenskyUrad", " ") ?: " " //musím ještě udělat číselník a namapovat to obdobně, jako právní forma
            val evidujiciUradyCiselnikArray = context.resources.getStringArray(R.array.zivnostensky_urad)
            val evidujiciUradyCiselnikMap = evidujiciUradyCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val evidujiciUrad = evidujiciUradyCiselnikMap[evidujiciUradZnacka] ?: evidujiciUradZnacka




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


            val companyDataRZP = CompanyDataRZP(name, ico,"", address, pravniForma,typSubjektu,evidujiciUrad,vznikPrvniZivnosti,
                listOsoby,listZivnosti)

            return companyDataRZP
        }

        private fun vlozOsobu(it: JSONObject): Osoba {
            var typAngazma = it?.optString("typAngazma", " ") ?: ""
            when (typAngazma) {
                "PODNIKATEL_RZP" -> typAngazma ="Podnikatel"
                "STATUTARNI_ZASTUPCE_RZP" -> typAngazma ="Statutární zástupce"
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