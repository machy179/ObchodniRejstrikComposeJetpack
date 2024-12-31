package com.machy1979.obchodnirejstrik.functions

import android.content.Context
import android.util.Log
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.model.CompanyDataRES
import com.machy1979.obchodnirejstrik.model.Nace
import org.json.JSONObject

class RozparzovaniDatDotazRES {
    companion object {
        lateinit var context: Context
        fun vratCompanyData(jsonObject: JSONObject, context: Context): CompanyDataRES {
            Log.i("RREESS address", jsonObject.toString())
            var name = jsonObject.optString("obchodniJmeno", " ") ?: " "
            val ico = jsonObject.optString("ico", " ") ?: " "
            var address = jsonObject.optJSONObject("sidlo")?.optString("textovaAdresa", " ") ?: " "
            Log.i("RREESS address", address)

            //právní forma
            val pravniFormaZnacka = jsonObject.optString("pravniForma", " ") ?: " "
            val pravniFormyCiselnikArray = context.resources.getStringArray(R.array.pravni_forma)
            val pravniFormyCiselnikMap =
                pravniFormyCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val pravniForma = pravniFormyCiselnikMap[pravniFormaZnacka] ?: pravniFormaZnacka

            val datumVzniku = jsonObject.optString("datumVzniku", " ") ?: " "

            val kodZUJ = jsonObject.optString("zakladniUzemniJednotka", " ") ?: " "
            val zakladniUzemniJednotkaCiselnikArray =
                context.resources.getStringArray(R.array.zakladni_uzemni_jednotka)
            val zakladniUzemniJednotkaCiselnikMap =
                zakladniUzemniJednotkaCiselnikArray.map { it.split(",") }
                    .associate { it[0] to it[1] }
            val zakladniUzemniJednotka = zakladniUzemniJednotkaCiselnikMap[kodZUJ] ?: kodZUJ

            var okres = jsonObject.optJSONObject("sidlo")?.optString("nazevOkresu", " ") ?: " "
            if (okres.equals(" ")) okres =
                jsonObject.optJSONObject("sidlo")?.optString("nazevSpravnihoObvodu", " ") ?: " "
            var kodOkresu = jsonObject.optJSONObject("sidlo")?.optString("kodOkresu", " ") ?: " "
            if (kodOkresu.equals(" ")) kodOkresu =
                jsonObject.optJSONObject("sidlo")?.optString("kodMestskehoObvodu", " ") ?: " "
            Log.i("RREESS okres", okres)

            //statistické údaje:
            val institucSektorZnacka = jsonObject.optJSONObject("statistickeUdaje")
                ?.optString("institucionalniSektor2010", " ") ?: " "
            val institucSektorCiselnikArray =
                context.resources.getStringArray(R.array.institucionalni_sektor)
            val institucSektorCiselnikMap =
                institucSektorCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val institucSektor =
                institucSektorCiselnikMap[institucSektorZnacka] ?: institucSektorZnacka


            val pocetZamestnancuZnacka = jsonObject.optJSONObject("statistickeUdaje")
                ?.optString("kategoriePoctuPracovniku", " ") ?: " "
            val pocetZamestnancuCiselnikArray =
                context.resources.getStringArray(R.array.kategorie_poctu_zamestnancu)
            val pocetZamestnancuCiselnikMap =
                pocetZamestnancuCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val pocetZamestnancu =
                pocetZamestnancuCiselnikMap[pocetZamestnancuZnacka] ?: pocetZamestnancuZnacka

            //NACE - tady je to složitější, protože tag nadřízený je Nace a číslo nace je NACE, v JSOUP to hází divočinu
            //proto ověřuji, zda it.select("D|Nace").count() > 1, aby to bralo jen ty položky, které mají dva tagy Nace (respektive Nace a NACE),
            // které mají  a proto nám v cisloNace [1] - lepší způsob se mi nepodařilo najít
            val listNace: MutableList<Nace> = mutableListOf<Nace>()
            val czNaceArray = context.resources.getStringArray(R.array.cz_nace)
            val czNaceyCiselnikMap = czNaceArray.map { it.split(",") }.associate { it[0] to it[1] }
            jsonObject.optJSONArray("czNace")?.let { czNaceArray ->
                for (i in 0 until czNaceArray.length()) {
                    val czNaceObject = czNaceArray.optString(i)
                    Log.i("RREESS czNace", czNaceObject)
                    listNace.add(
                        Nace(
                            czNaceObject,
                            czNaceyCiselnikMap[czNaceObject] ?: czNaceObject
                        )
                    )

                }
            }

            val companyDataRES = CompanyDataRES(
                name, ico, "", address, pravniForma, datumVzniku, zakladniUzemniJednotka,
                kodZUJ, okres, kodOkresu, institucSektor, pocetZamestnancu, listNace
            )


            return companyDataRES
        }


    }
}


