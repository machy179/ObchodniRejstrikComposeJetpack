package com.machy1979.obchodnirejstrik.functions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.Firma
import com.machy1979.obchodnirejstrik.model.Osoba
import org.json.JSONObject


class RozparzovaniDatDotazOR {
    companion object {
        lateinit var context: Context
        var pomocnyCounter = 0

        @SuppressLint("SuspiciousIndentation")
        fun vratCompanyData(jsonObject: JSONObject, context: Context): CompanyData {

            Log.i("RopzarzovaniOR: uvnitř", jsonObject.toString())
            this.context = context

            var name = " "
            jsonObject.optJSONArray("obchodniJmeno")?.let { obchodniJmenoArray ->
                for (i in 0 until obchodniJmenoArray.length()) {
                    val obchodniJmeno = obchodniJmenoArray.getJSONObject(i)
                    // Zpracování obchodního jména, provedete s ním, co je potřeba
                    if (!obchodniJmeno.has("datumVymazu")) {
                        name = obchodniJmeno?.optString("hodnota", " ") ?: " "
                    }
                }
            }

            val icoArray = jsonObject.optJSONArray("ico")
            val ico = icoArray?.optJSONObject(0)?.optString("hodnota", " ") ?: " "

            //adresa
            var address: String = " "
            jsonObject.optJSONArray("adresy")?.let { addressesArray ->
                for (i in 0 until addressesArray.length()) {
                    val adresa = addressesArray.getJSONObject(i)
                    // Zpracování obchodního jména, provedete s ním, co je potřeba
                    if (!adresa.has("datumVymazu")) {
                        address =
                            adresa?.getJSONObject("adresa")?.optString("textovaAdresa", " ") ?: " "
                    }
                }


            } ?: run {
                address = " "
            }


            var stavSubjektu = jsonObject.optString("stavSubjektu", " ") ?: " "
            if (stavSubjektu.equals("AKTIVNI")) stavSubjektu = "Aktivní"

            //právní forma
            val pravniFormaArray = jsonObject.optJSONArray("pravniForma")
            val pravniFormaZnacka =
                pravniFormaArray?.optJSONObject(0)?.optString("hodnota", " ") ?: " "
            val pravniFormyCiselnikArray = context.resources.getStringArray(R.array.pravni_forma)
            val pravniFormyCiselnikMap =
                pravniFormyCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val pravniForma = pravniFormyCiselnikMap[pravniFormaZnacka] ?: pravniFormaZnacka
            Log.i("RopzarzovaniOR: pravniForma", pravniForma)

            val datumZapisu = jsonObject.optString("datumZapisu", " ") ?: " "

            val spisovaZnackaArray = jsonObject.optJSONArray("spisovaZnacka")
            var spisovaZnacka = spisovaZnackaArray?.optJSONObject(0)?.optString("oddil", " ") ?: " "
            spisovaZnacka =
                spisovaZnacka + " " + spisovaZnackaArray?.optJSONObject(0)?.optString("vlozka", " ")
                    ?: " "

            val soudZnacka: String =
                spisovaZnackaArray.optJSONObject(0)?.optString("soud", " ") ?: " "
            val soudyCislenikArray = context.resources.getStringArray(R.array.soudy)
            val soudyCiselnikMap =
                soudyCislenikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val soud = soudyCiselnikMap[soudZnacka] ?: soudZnacka

            //vklad a splaceno musím řešit tak, že načte celý list a z něho vybere jen ten platný - u kterého není datum výmazu
            var vklad: String = ""
            var splaceno: String = ""
            jsonObject.optJSONArray("zakladniKapital")?.let { zakladniKapitalArray ->
                for (i in 0 until zakladniKapitalArray.length()) {
                    val kapitalObject = zakladniKapitalArray.optJSONObject(i)
                    Log.i("RopzarzovaniOR: kapitalObject:", kapitalObject.toString())
                    if (!kapitalObject.has("datumVymazu")) {
                        vklad =
                            kapitalObject.optJSONObject("vklad")?.optString("hodnota", " ") ?: " "
                        if (!vklad.equals(" ")) vklad = upravFinancniCastku(vklad)
                        Log.i("RopzarzovaniOR: vklad:", vklad)
                        splaceno =
                            kapitalObject.optJSONObject("splaceni")?.optString("hodnota") ?: " "
                        if (!splaceno.equals(" ")) {
                            if (kapitalObject.optJSONObject("splaceni")?.optString("typObnos")
                                    .equals("PROCENTA")
                            ) {
                                splaceno = splaceno + " " + "%"
                            } else {
                                splaceno = upravFinancniCastku(splaceno)
                            }

                        }
                        Log.i("RopzarzovaniOR: splaceno:", splaceno)
                    }
                }
            }

            //akcie:
            val listAkcie: MutableList<String> = mutableListOf<String>()
            Log.i("RopzarzovaniOR: akcie", "1")
            jsonObject.optJSONArray("akcie")?.let { akcieArray ->
                Log.i("RopzarzovaniOR: akcie", akcieArray.toString())
                for (i in 0 until akcieArray.length()) {
                    val akcielObject = akcieArray.optJSONObject(i)
                    if (!akcielObject.has("datumVymazu")) {
                        Log.i("RopzarzovaniOR: akcie", akcielObject.toString())
                        var akcie: String = akcielObject.optString("typAkcie", " ") ?: " "
                        Log.i("RopzarzovaniOR: akcie", "2")
                        when (akcie) {
                            "NA_JMENO" -> {
                                akcie = "Na jméno"
                            }

                            "KMENOVE_NA_JMENO" -> {
                                akcie = "Kmenové na jméno"
                            }

                            "NA_MAJITELE" -> {
                                akcie = "Na majitele"
                            }

                            "KMENOVE_NA_MAJITELE" -> {
                                akcie = "Kmenové na majitele"
                            }

                            else -> {
                            }
                        }
                        Log.i("RopzarzovaniOR: akcie", "3")
                        if (akcielObject.has("pocet")) {
                            Log.i("RopzarzovaniOR: akcie", "4")
                            akcie += "\npočet akcií: ${akcielObject.optString("pocet", " ")}"
                        }
                        Log.i("RopzarzovaniOR: akcie", "5")
                        if (akcielObject.optJSONObject("hodnota").has("hodnota")) {
                            akcie += "\nhodnota: ${
                                akcielObject.optJSONObject("hodnota")?.optString("hodnota", " ")
                                    ?.let { upravFinancniCastku(it) }
                            }"
                        }
                        listAkcie.add(akcie)
                    }
                }
            }

            //předmět podnikání
            val listPredmetPodnikani: MutableList<String> = mutableListOf<String>()
            val cinnosti = jsonObject.optJSONObject("cinnosti")
            if (cinnosti != null) {
                cinnosti.optJSONArray("predmetPodnikani")?.let { predmetPodnikaniArray ->
                    for (i in 0 until predmetPodnikaniArray.length()) {
                        val predmetPodnikanilObject = predmetPodnikaniArray.optJSONObject(i)
                        listPredmetPodnikani.add(predmetPodnikanilObject.optString("hodnota", " "))
                    }

                }
                cinnosti.optJSONArray("predmetCinnosti")?.let { predmetCinnostiArray ->
                    for (i in 0 until predmetCinnostiArray.length()) {
                        val predmetCinnostiObject = predmetCinnostiArray.optJSONObject(i)
                        listPredmetPodnikani.add(predmetCinnostiObject.optString("hodnota", " "))
                    }

                }


            }

            //ostatní skutečnosti
            val listOstatniSkutecnosti: MutableList<String> = mutableListOf<String>()
            jsonObject.optJSONArray("ostatniSkutecnosti")?.let { ostatniSkutecnosti ->
                for (i in 0 until ostatniSkutecnosti.length()) {
                    val ostatniSkutecnostiObject = ostatniSkutecnosti.optJSONObject(i)
                    listOstatniSkutecnosti.add(ostatniSkutecnostiObject.optString("hodnota", " "))
                }

            }


            //statutarni organy, "ostatniOrgany" a "spolecnici" - je tam níže uvedené
            var listProkura: MutableList<Osoba> = mutableListOf<Osoba>()
            var listStatutarniOrganOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            var listDozorciRada: MutableList<Osoba> = mutableListOf<Osoba>()
            var listspolecniciSVklademOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            var listAkcionariOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            var listAkcionariFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val listspolecniciSVklademFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val listStatutarniOrganFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val listLikvidaceFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val listLikvidaceOsoby: MutableList<Osoba> = mutableListOf<Osoba>()

            val listStatutarniOrganSkutecnosti: MutableList<String> = mutableListOf<String>()


            val objektyOrgany: Array<String> =
                arrayOf("statutarniOrgany", "ostatniOrgany", "spolecnici")

            for (str in objektyOrgany) {
                jsonObject.optJSONArray(str)?.let { statutarniOrganyArray ->
                    for (i in 0 until statutarniOrganyArray.length()) {
                        val statutarniOrganyObject = statutarniOrganyArray.optJSONObject(i)
                        //když už bude projíždět tento cyklus, tak se tady pokusím naplnit listStatutarniOrganSkutecnosti
                        if (str.equals("statutarniOrgany")) {
                            statutarniOrganyObject.optJSONArray("zpusobJednani")
                                ?.let { zpusobJednaniArray ->
                                    for (i in 0 until zpusobJednaniArray.length()) {
                                        val zpusobJednaniObject =
                                            zpusobJednaniArray.optJSONObject(i)
                                        if (!zpusobJednaniObject.has("datumVymazu")) {
                                            listStatutarniOrganSkutecnosti.add(
                                                zpusobJednaniObject.optString(
                                                    "hodnota",
                                                    " "
                                                )
                                            )
                                        }
                                    }
                                }
                        }
                        val listClenoveOrganu: MutableList<Osoba> = mutableListOf<Osoba>()
                        val listClenoveOrganuFirmy: MutableList<Firma> = mutableListOf<Firma>()
                        val objektyPopisListuOsob: Array<String> =
                            arrayOf("clenoveOrganu", "spolecnik")
                        for (popisListu in objektyPopisListuOsob) {
                            statutarniOrganyObject.optJSONArray(popisListu)
                                ?.let { clenoveOrganuArray ->
                                    for (i in 0 until clenoveOrganuArray.length()) {
                                        val clenOrganuObject = clenoveOrganuArray.optJSONObject(i)
                                        if (!clenOrganuObject.has("datumVymazu")) {
                                            if (clenOrganuObject.has("fyzickaOsoba")) {
                                                listClenoveOrganu.add(vlozOsobu(clenOrganuObject))
                                            } else if (clenOrganuObject.has("pravnickaOsoba")) {
                                                listClenoveOrganuFirmy.add(
                                                    vlozFirmu(
                                                        clenOrganuObject
                                                    )
                                                )
                                            } else if (clenOrganuObject.has("osoba")) { //takto to ma strukturováno spolecnik
                                                if (clenOrganuObject.optJSONObject("osoba")
                                                        .has("fyzickaOsoba")
                                                ) {
                                                    listClenoveOrganu.add(
                                                        vlozOsobuSpolecnik(
                                                            clenOrganuObject
                                                        )
                                                    )
                                                } else if (clenOrganuObject.optJSONObject("osoba")
                                                        .has("pravnickaOsoba")
                                                ) {
                                                    listClenoveOrganuFirmy.add(
                                                        vlozFirmuSpolecnik(
                                                            clenOrganuObject
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    when (statutarniOrganyObject.optString("typOrganu")) {
                                        "PROKURA" -> {
                                            listProkura.addAll(listClenoveOrganu)
                                        }

                                        "STATUTARNI_ORGAN" -> {
                                            listStatutarniOrganOsoby.addAll(listClenoveOrganu)
                                            listStatutarniOrganFirmy.addAll(listClenoveOrganuFirmy)
                                        }

                                        "DOZORCI_RADA" -> {
                                            listDozorciRada.addAll(listClenoveOrganu)
                                        }

                                        "SPOLECNIK" -> {
                                            listspolecniciSVklademOsoby.addAll(listClenoveOrganu)
                                            listspolecniciSVklademFirmy.addAll(
                                                listClenoveOrganuFirmy
                                            )
                                        }

                                        "AKCIONAR_SEKCE" -> {
                                            listAkcionariOsoby.addAll(listClenoveOrganu)
                                            listAkcionariFirmy.addAll(listClenoveOrganuFirmy)
                                        }

                                        "LIKVIDATOR" -> {
                                            listLikvidaceOsoby.addAll(listClenoveOrganu)
                                            listLikvidaceFirmy.addAll(listClenoveOrganuFirmy)
                                        }

                                        else -> {
                                        }
                                    }
                                }
                        }
                    }
                }
            }

            //odštěpný závod - v minulosti byl ale název organizační složka

            val listVedouciOrganizacniSlozkyOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            jsonObject.optJSONArray("odstepneZavody")?.let { odstepneZavodyArray ->
                for (i in 0 until odstepneZavodyArray.length()) {
                    val odstepnyZavodObject = odstepneZavodyArray.optJSONObject(i)

                    if (!odstepnyZavodObject.has("datumVymazu")) {
                        var obchodniJmenoOdstepnehoZavodu: String = ""
                        var vedouciOdstepnehoZavodu: Osoba = Osoba()
                        odstepnyZavodObject.optJSONArray("obchodniJmeno")
                            ?.let { obchodniJmenoOodstepnehoZavoduArray ->
                                for (i in 0 until obchodniJmenoOodstepnehoZavoduArray.length()) {
                                    val obchodniJmenoOdstepnehoZavodObject =
                                        obchodniJmenoOodstepnehoZavoduArray.optJSONObject(i)
                                    if (!obchodniJmenoOdstepnehoZavodObject.has("datumVymazu")) {
                                        obchodniJmenoOdstepnehoZavodu =
                                            obchodniJmenoOdstepnehoZavodObject?.optString("hodnota")
                                                ?: ""

                                    }

                                }
                            }
                        odstepnyZavodObject.optJSONArray("vedouci")
                            ?.let { vedouciOodstepnehoZavoduArray ->
                                for (i in 0 until vedouciOodstepnehoZavoduArray.length()) {
                                    val vedouciOdstepnehoZavodObject =
                                        vedouciOodstepnehoZavoduArray.optJSONObject(i)
                                    if (!vedouciOdstepnehoZavodObject.has("datumVymazu")) {
                                        vedouciOdstepnehoZavodu =
                                            vlozOsobu(vedouciOdstepnehoZavodObject)
                                    }
                                }
                            }
                        vedouciOdstepnehoZavodu.organizacniSlozka = obchodniJmenoOdstepnehoZavodu
                        listVedouciOrganizacniSlozkyOsoby.add(vedouciOdstepnehoZavodu)
                    }
                }
            }


            /*


                        //statutární orgán osoby
                        val listStatutarniOrganOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
                        val zaznamyStatOrganOsoby = document.select("D|SO").select("D|CSO")
                        var listZaznamyStaturatniOrganOsoby: MutableList<String> = mutableListOf<String>()
                        zaznamyStatOrganOsoby.forEach() {
                            var address = vratAdresu(it.select("D|FO"))

                            var zaznamyPoznamky = it.select("D|T")
                            listZaznamyStaturatniOrganOsoby = mutableListOf<String>()
                            zaznamyPoznamky.forEach(){
                                listZaznamyStaturatniOrganOsoby.add(it.text())
                            }

                            if(!(it.select("D|FO").select("D|P").text()=="")) {
                                listStatutarniOrganOsoby.add(vlozOsobu(it, address,listZaznamyStaturatniOrganOsoby))
                            }
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


                        //vedoucí organizační složky
                        Log.i("orgSlozka: ","1")
                        val listVedouciOrganizacniSlozkyOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
                        Log.i("orgSlozka: ","2")
                        val zaznamyVedouciOrganizacniSlozkyOsoby = document.select("D|OZY").select("D|OZ")
                        Log.i("orgSlozka: ","3")
                        zaznamyVedouciOrganizacniSlozkyOsoby.forEach() {
                            Log.i("orgSlozka: ","444")
                            var address =  vratAdresu(it.select("D|FO"))
                            if(!(it.select("D|FO").select("D|P").text()=="")) {
                                listVedouciOrganizacniSlozkyOsoby.add(vlozOsobu(it,address))
                            }
                        }


                        //konečné vložení do companyData
                        val companyData = CompanyData(name, ico,"", address,listPredmetPodnikani,
                            listOstatniSkutecnosti,listStatutarniOrganOsoby, listStatutarniOrganFirmy,listStatutarniOrganSkutecnosti,listProkura,
                            listDozorciRada,listspolecniciSVklademOsoby, listspolecniciSVklademFirmy, listAkcionariOsoby,
                            listAkcionariFirmy, listLikvidaceOsoby, listLikvidaceFirmy,stavSubjektu, pravniForma,
                            datumZapisu,soud,spisovaZnacka,vklad,splaceno, listAkcie,listVedouciOrganizacniSlozkyOsoby)*/
            val companyData = CompanyData(
                name,
                ico,
                "",
                address,
                listPredmetPodnikani,
                listOstatniSkutecnosti,
                listStatutarniOrganOsoby,
                listStatutarniOrganFirmy,
                listStatutarniOrganSkutecnosti,
                listProkura,
                listDozorciRada,
                listspolecniciSVklademOsoby,
                listspolecniciSVklademFirmy,
                listAkcionariOsoby,
                listAkcionariFirmy,
                listLikvidaceOsoby,
                listLikvidaceFirmy,
                stavSubjektu,
                pravniForma,
                datumZapisu,
                soud,
                spisovaZnacka,
                vklad,
                splaceno,
                listAkcie,
                listVedouciOrganizacniSlozkyOsoby
            )
            return companyData
        }

        private fun upravIco(input: String): String {
            val nulyKZacatku = 8 - input.length
            val upravenyString = "0".repeat(nulyKZacatku) + input
            return upravenyString
        }

        private fun upravFinancniCastku(input: String): String {
            // Odstranění všeho za ";"
            val cleanedString = input.substringBefore(";")
            // Odstranění všech nečíselných znaků
            val digitsOnly = cleanedString.replace(Regex("[^\\d]"), "")
            // Rozdělení na skupiny po třech znacích a spojení s mezerami
            val reversedParts = digitsOnly.reversed().chunked(3)
            var formattedString = reversedParts.joinToString(" ").reversed()
            formattedString += ",- Kč"


            return formattedString
        }

        private fun upravSplaceni(
            input: String,
            typObnosu: String,
        ): String { //nakonec použito i pro velikost podílu
            Log.i("upravSplaceni: ", input + "---" + typObnosu)
            var formattedString: String = ""
            // Odstranění všeho za ";"
            val cleanedString = input.substringBefore(";")
            formattedString = when (typObnosu) {
                "PROCENTA" -> "$cleanedString %"
                "TEXT" -> input
                "ZLOMEK" -> input.replace(";", "/")
                else -> {
                    // Odstranění všech nečíselných znaků
                    val digitsOnly = cleanedString.replace(Regex("[^\\d]"), "")
                    // Rozdělení na skupiny po třech znacích a spojení s mezerami
                    val reversedParts = digitsOnly.reversed().chunked(3)
                    reversedParts.joinToString(" ").reversed()
                }
            }



            return formattedString
        }



        private fun vlozOsobu(it: JSONObject): Osoba {
            val funkce =
                it?.optJSONObject("clenstvi")?.optJSONObject("funkce")?.optString("nazev", " ")
                    ?: ""
            return Osoba(
                it?.optJSONObject("fyzickaOsoba")?.optString("titulPredJmenem", "") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optString("jmeno", " ") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optString("prijmeni", " ") ?: "",
                funkce,

                it?.optJSONObject("fyzickaOsoba")?.optString("datumNarozeni", " ") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optJSONObject("adresa")
                    ?.optString("textovaAdresa", " ") ?: "",
                mutableListOf<String>(),
                it?.optJSONObject("clenstvi")?.optJSONObject("clenstvi")
                    ?.optString("vznikClenstvi", " ") ?: "",
                it?.optJSONObject("clenstvi")?.optJSONObject("funkce")
                    ?.optString("vznikFunkce", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ")
                    ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                ""

            )
        }

        private fun vlozOsobuSpolecnik(it: JSONObject): Osoba {
            val funkce =
                it?.optJSONObject("osoba")?.optJSONObject("clenstvi")?.optJSONObject("funkce")
                    ?.optString("nazev", " ") ?: ""

            var splaceno = ""

            var podil = ""

            var vklad = ""

            it.optJSONArray("podil")?.let { podilArray ->
                for (i in 0 until podilArray.length()) {
                    val podilObject = podilArray.optJSONObject(i)
                    if (!podilObject.has("datumVymazu")) {
                        splaceno =
                            podilObject.optJSONObject("splaceni")?.optString("hodnota", " ")
                                ?: ""
                        splaceno = upravSplaceni(
                            splaceno,
                            podilObject.optJSONObject("splaceni")?.optString("typObnos") ?: ""
                        )
                        podil = podilObject.optJSONObject("velikostPodilu")
                            ?.optString("hodnota", " ") ?: ""
                        Log.d("ObchodniRejstirkPodil:", "podil_velikostPodilu_hodnota1: " + podilArray.optJSONObject(0).optJSONObject("velikostPodilu")
                            ?.optString("hodnota", " "))
                        podil = upravSplaceni(
                            podil,
                            podilObject.optJSONObject("velikostPodilu")
                                ?.optString("typObnos", " ") ?: ""
                        )

                        vklad = upravFinancniCastku(
                            podilObject.optJSONObject("vklad")?.optString("hodnota", " ")
                                ?: ""
                        )
                    }
                }
            }

            return Osoba(
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")
                    ?.optString("titulPredJmenem", "") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optString("jmeno", " ")
                    ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")
                    ?.optString("prijmeni", " ") ?: "",
                funkce,

                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")
                    ?.optString("datumNarozeni", " ") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optJSONObject("adresa")
                    ?.optString("textovaAdresa", " ") ?: "",
                mutableListOf<String>(),
                it?.optJSONObject("osoba")?.optJSONObject("clenstvi")?.optJSONObject("clenstvi")
                    ?.optString("vznikClenstvi", " ") ?: "",
                it?.optJSONObject("clenstvi")?.optJSONObject("funkce")
                    ?.optString("vznikFunkce", " ") ?: "",
                vklad,
                splaceno,
                podil,
                ""


            )
        }

        private fun vlozFirmu(it: JSONObject): Firma {
            var ico = it?.optJSONObject("pravnickaOsoba")?.optString("ico", "") ?: ""
            if (!ico.equals("")) ico = upravIco(ico)
            return Firma(
                ico,
                it?.optJSONObject("pravnickaOsoba")?.optString("obchodniJmeno", "") ?: "",
                it?.optJSONObject("pravnickaOsoba")?.optJSONObject("adresa")
                    ?.optString("textovaAdresa", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ")
                    ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: ""
            )
        }

        private fun vlozFirmuSpolecnik(it: JSONObject): Firma {
            var ico =
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")?.optString("ico", "")
                    ?: ""
            if (!ico.equals("")) ico = upravIco(ico)


            var splaceno = ""

            var podil = ""

            var vklad = ""

            it.optJSONArray("podil")?.let { podilArray ->
                for (i in 0 until podilArray.length()) {
                    val podilObject = podilArray.optJSONObject(i)
                    if (!podilObject.has("datumVymazu")) {
                    splaceno =
                        podilObject.optJSONObject("splaceni")?.optString("hodnota", " ")
                            ?: ""
                    splaceno = upravSplaceni(
                        splaceno,
                        podilObject.optJSONObject("splaceni")?.optString("typObnos") ?: ""
                    )
                    podil = podilObject.optJSONObject("velikostPodilu")
                        ?.optString("hodnota", " ") ?: ""
                    Log.d("ObchodniRejstirkPodil:", "podil_velikostPodilu_hodnota1: " + podilArray.optJSONObject(0).optJSONObject("velikostPodilu")
                        ?.optString("hodnota", " "))
                    podil = upravSplaceni(
                        podil,
                        podilObject.optJSONObject("velikostPodilu")
                            ?.optString("typObnos", " ") ?: ""
                    )

                    vklad = upravFinancniCastku(
                        podilObject.optJSONObject("vklad")?.optString("hodnota", " ")
                            ?: ""
                    )
                }
                }






            }
            //ARRAY STOP



            return Firma(
                ico,
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")
                    ?.optString("obchodniJmeno", "") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")?.optJSONObject("adresa")
                    ?.optString("textovaAdresa", " ") ?: "",
                vklad,
                splaceno,
                podil
            )
        }


    }
}