package com.machy1979.obchodnirejstrik.functions

import android.content.Context

import android.util.Log
import com.machy1979.obchodnirejstrik.R

import com.machy1979.obchodnirejstrik.model.CompanyData
import com.machy1979.obchodnirejstrik.model.Firma
import com.machy1979.obchodnirejstrik.model.Osoba
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class RozparzovaniDatDotazOR {
    companion object {
        lateinit var context: Context
        var pomocnyCounter = 0
        fun vratCompanyData(jsonObject: JSONObject, context: Context): CompanyData {

            Log.i("RopzarzovaniOR: uvnitř",jsonObject.toString())
            this.context = context

            var name = " "
            val obchodniJmenoArray = jsonObject.optJSONArray("obchodniJmeno")
            jsonObject.optJSONArray("obchodniJmeno")?.let { obchodniJmenoArray ->
                for (i in 0 until obchodniJmenoArray.length()) {
                    val obchodniJmeno = obchodniJmenoArray.getJSONObject(i)
                    // Zpracování obchodního jména, provedete s ním, co je potřeba
                    if (!obchodniJmeno.has("datumVymazu")) {
                        name = obchodniJmeno?.optString("hodnota", " ") ?: " "
                    }
                }
            }

            Log.i("RopzarzovaniOR: jmeno",name)
            val icoArray = jsonObject.optJSONArray("ico")
            val ico = icoArray?.optJSONObject(0)?.optString("hodnota", " ") ?: " "
            Log.i("RopzarzovaniOR: ico",ico)

            var address: String
            val addressesArray = jsonObject.optJSONArray("adresy")
            Log.i("RopzarzovaniOR: addressesArray",addressesArray.toString())
            jsonObject.optJSONArray("adresy")?.let { addressesArray ->
                val firstAddressObject = addressesArray.optJSONObject(0)?.getJSONObject("adresa")
                address = firstAddressObject?.optString("textovaAdresa", " ") ?: " "

            }?: run {
                address = " " // Adresa není k dispozici, nastavte výchozí hodnotu
            }

            if (addressesArray.length() > 0) {
                val firstAddressObject = addressesArray.optJSONObject(0)?.getJSONObject("adresa")
                address = firstAddressObject?.optString("textovaAdresa", " ") ?: " "

            } else {
                address = " " // Adresa není k dispozici, nastavte výchozí hodnotu
            }
            Log.i("RopzarzovaniOR: address",address)

            var stavSubjektu= jsonObject.optString("stavSubjektu", " ") ?: " "
            if (stavSubjektu.equals("AKTIVNI")) stavSubjektu= "Aktivní"

            //právní forma
            val pravniFormaArray = jsonObject.optJSONArray("pravniForma")
            val pravniFormaZnacka= pravniFormaArray?.optJSONObject(0)?.optString("hodnota", " ") ?: " "
            val pravniFormyCiselnikArray = context.resources.getStringArray(R.array.pravni_forma)
            val pravniFormyCiselnikMap = pravniFormyCiselnikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val pravniForma = pravniFormyCiselnikMap[pravniFormaZnacka] ?: pravniFormaZnacka
            Log.i("RopzarzovaniOR: pravniForma",pravniForma)

            val datumZapisu= jsonObject.optString("datumZapisu", " ") ?: " "

            val spisovaZnackaArray = jsonObject.optJSONArray("spisovaZnacka")
            var spisovaZnacka= spisovaZnackaArray?.optJSONObject(0)?.optString("oddil", " ") ?: " "
            spisovaZnacka= spisovaZnacka+" " + spisovaZnackaArray?.optJSONObject(0)?.optString("vlozka", " ") ?: " "

            val soudZnacka: String= spisovaZnackaArray.optJSONObject(0)?.optString("soud", " ") ?: " "
            val soudyCislenikArray = context.resources.getStringArray(R.array.soudy)
            val soudyCiselnikMap = soudyCislenikArray.map { it.split(",") }.associate { it[0] to it[1] }
            val soud = soudyCiselnikMap[soudZnacka] ?: soudZnacka

            //vklad a splaceno musím řešit tak, že načte celý list a z něho vybere jen ten platný - u kterého není datum výmazu
            var vklad: String = ""
            var splaceno: String = ""
            jsonObject.optJSONArray("zakladniKapital")?.let { zakladniKapitalArray ->
                for (i in 0 until zakladniKapitalArray.length()) {
                    val kapitalObject = zakladniKapitalArray.optJSONObject(i)
                    Log.i("RopzarzovaniOR: kapitalObject:",kapitalObject.toString())
                    if (!kapitalObject.has("datumVymazu")) {
                        vklad = kapitalObject.optJSONObject("vklad")?.optString("hodnota", " ") ?: " "
                        if (!vklad.equals(" ")) vklad=upravFinancniCastku(vklad)
                        Log.i("RopzarzovaniOR: vklad:",vklad)
                        splaceno = kapitalObject.optJSONObject("splaceni")?.optString("hodnota") ?: " "
                        if (!splaceno.equals(" ")) splaceno=splaceno + " "+"%"
                        Log.i("RopzarzovaniOR: splaceno:",splaceno)
                    }
                }
            }

            //akcie:
            val listAkcie: MutableList<String> = mutableListOf<String>()
            Log.i("RopzarzovaniOR: akcie","1")
            jsonObject.optJSONArray("akcie")?.let { akcieArray ->
                Log.i("RopzarzovaniOR: akcie",akcieArray.toString())
                for (i in 0 until akcieArray.length()) {
                    val akcielObject = akcieArray.optJSONObject(i)
                    Log.i("RopzarzovaniOR: akcie",akcielObject.toString())
                    var akcie: String = akcielObject.optString("typAkcie", " ") ?: " "
                    Log.i("RopzarzovaniOR: akcie","2")
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
                    Log.i("RopzarzovaniOR: akcie","3")
                    if (akcielObject.has("pocet")) {
                        Log.i("RopzarzovaniOR: akcie","4")
                        akcie += "\npočet akcií: ${akcielObject.optString("pocet", " ")}"
                    }
                    Log.i("RopzarzovaniOR: akcie","5")
                    if (akcielObject.optJSONObject("hodnota").has("hodnota")) {
                        akcie += "\nhodnota: ${akcielObject.optJSONObject("hodnota")?.optString("hodnota", " ")
                            ?.let { upravFinancniCastku(it) }}"
                    }
                    listAkcie.add(akcie)
                }
            }

            //předmět podnikání
            val listPredmetPodnikani: MutableList<String> = mutableListOf<String>()
            val cinnosti = jsonObject.optJSONObject("cinnosti")
            if (cinnosti != null) {
                cinnosti.optJSONArray("predmetPodnikani")?.let {predmetPodnikaniArray ->
                    for (i in 0 until predmetPodnikaniArray.length()) {
                        val predmetPodnikanilObject = predmetPodnikaniArray.optJSONObject(i)
                        listPredmetPodnikani.add(predmetPodnikanilObject.optString("hodnota", " "))
                    }

                }
                cinnosti.optJSONArray("predmetCinnosti")?.let {predmetCinnostiArray ->
                    for (i in 0 until predmetCinnostiArray.length()) {
                        val predmetCinnostiObject = predmetCinnostiArray.optJSONObject(i)
                        listPredmetPodnikani.add(predmetCinnostiObject.optString("hodnota", " "))
                    }

                }


            }

            //ostatní skutečnosti
            val listOstatniSkutecnosti: MutableList<String> = mutableListOf<String>()
            jsonObject.optJSONArray("ostatniSkutecnosti")?.let {ostatniSkutecnosti ->
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
            val listStatutarniOrganSkutecnosti: MutableList<String> = mutableListOf<String>()


            val objektyOrgany: Array<String> = arrayOf("statutarniOrgany", "ostatniOrgany", "spolecnici")

            for (str in objektyOrgany) {
                jsonObject.optJSONArray(str)?.let { statutarniOrganyArray ->
                    for (i in 0 until statutarniOrganyArray.length()) {
                        val statutarniOrganyObject = statutarniOrganyArray.optJSONObject(i)
                        //když už bude projíždět tento cyklus, tak se tady pokusím naplnit listStatutarniOrganSkutecnosti
                        if (str.equals("statutarniOrgany")) {
                            statutarniOrganyObject.optJSONArray("zpusobJednani")?.let { zpusobJednaniArray ->
                                for (i in 0 until zpusobJednaniArray.length()) {
                                    val zpusobJednaniObject = zpusobJednaniArray.optJSONObject(i)
                                    if (!zpusobJednaniObject.has("datumVymazu")) {
                                        listStatutarniOrganSkutecnosti.add(zpusobJednaniObject.optString("hodnota", " "))
                                    }
                                }
                            }
                        }
                        val listClenoveOrganu: MutableList<Osoba> = mutableListOf<Osoba>()
                        val listClenoveOrganuFirmy: MutableList<Firma> = mutableListOf<Firma>()
                        val objektyPopisListuOsob: Array<String> = arrayOf("clenoveOrganu", "spolecnik")
                        for (popisListu in objektyPopisListuOsob) {
                            statutarniOrganyObject.optJSONArray(popisListu)?.let { clenoveOrganuArray ->
                                for (i in 0 until clenoveOrganuArray.length()) {
                                    val clenOrganuObject = clenoveOrganuArray.optJSONObject(i)
                                    if (!clenOrganuObject.has("datumVymazu")) {
                                        if(clenOrganuObject.has("fyzickaOsoba")) {
                                            listClenoveOrganu.add(vlozOsobu(clenOrganuObject))
                                        } else if(clenOrganuObject.has("pravnickaOsoba")) {
                                            listClenoveOrganuFirmy.add(vlozFirmu(clenOrganuObject))
                                        } else if(clenOrganuObject.has("osoba")) { //takto to ma strukturováno spolecnik
                                            if (clenOrganuObject.optJSONObject("osoba").has("fyzickaOsoba")) {
                                                listClenoveOrganu.add(vlozOsobuSpolecnik(clenOrganuObject))
                                            } else if (clenOrganuObject.optJSONObject("osoba").has("pravnickaOsoba")) {
                                                listClenoveOrganuFirmy.add(vlozFirmuSpolecnik(clenOrganuObject))
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
                                        listspolecniciSVklademFirmy.addAll(listClenoveOrganuFirmy)
                                    }
                                    "AKCIONAR_SEKCE" -> {
                                        listAkcionariOsoby.addAll(listClenoveOrganu)
                                        listAkcionariFirmy.addAll(listClenoveOrganuFirmy)
                                    }
                                    else -> {
                                    }
                                }
                            }
                        }
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
            val companyData = CompanyData(name, ico,"", address,listPredmetPodnikani,
                listOstatniSkutecnosti,listStatutarniOrganOsoby,listStatutarniOrganFirmy,listStatutarniOrganSkutecnosti,listProkura,
                listDozorciRada,listspolecniciSVklademOsoby, listspolecniciSVklademFirmy, listAkcionariOsoby,
                listAkcionariFirmy, mutableListOf<Osoba>(), mutableListOf<Firma>(),stavSubjektu, pravniForma,
                datumZapisu,soud,spisovaZnacka,vklad,splaceno, listAkcie,mutableListOf<Osoba>())
            return companyData
        }

        private fun vlozOsobu1(it: Element?, address: String, listZaznamy: MutableList<String> = mutableListOf<String>()): Osoba {
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
                it?.select("D|OP")?.select("D|T")?.text() ?: "",
                it?.select("D|OF")?.text() ?: ""

            )
        }

        fun vratCompanyDataOld(document: Document, context: Context): CompanyData {

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

            //akcie
            val vklad: String = document.select("D|KC").first()?.text() ?: ""
            val splaceno: String = document.select("D|PRC").first()?.text() ?: ""



            val listAkcie: MutableList<String> = mutableListOf<String>()
            val zaznamyAkcie = document.select("D|Akcie").select("D|EM")
            zaznamyAkcie.forEach() {
                var akcie: String = it.select("D|DA").first()?.text() ?: ""
                it.select("D|H").first()?.let {akcie =akcie + ",\nhodnota: "+it.text()}
                it.select("D|Pocet").first()?.let {akcie =akcie + ",\npočet akcií: "+it.text()}
                it.select("D|PD").first()?.let {akcie =akcie + ",\n"+it.text()}
                it.select("D|T").first()?.let {akcie =akcie + ",\n"+it.text()}

                listAkcie.add(akcie)
            }


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
            var listZaznamyStaturatniOrganOsoby: MutableList<String> = mutableListOf<String>()
            zaznamyStatOrganOsoby.forEach() {
                var address = vratAdresu(it.select("D|FO"))

                var zaznamyPoznamky = it.select("D|T")
                listZaznamyStaturatniOrganOsoby = mutableListOf<String>()
                zaznamyPoznamky.forEach(){
                    listZaznamyStaturatniOrganOsoby.add(it.text())
                }

                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listStatutarniOrganOsoby.add(vlozOsobu1(it, address,listZaznamyStaturatniOrganOsoby))
                }
            }
            //statutární orgán firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listStatutarniOrganFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamyStatOrganFirmy = document.select("D|SO").select("D|CSO")
            zaznamyStatOrganFirmy.forEach() {
                var address = vratAdresu(it.select("D|PO"))
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listStatutarniOrganFirmy.add(vlozFirmu1(it, address))
                }
            }

            //statutární orgán ostatní skutečnosti
            val listStatutarniOrganSkutecnosti: MutableList<String> = mutableListOf<String>()
            val zaznamyStatutarniOrganSkutecnosti = document.select("D|SO").select("D|T")
            zaznamyStatutarniOrganSkutecnosti.forEach() {
                if(!listZaznamyStaturatniOrganOsoby.contains(it.text())) {
                    listStatutarniOrganSkutecnosti.add(it.text())
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


                listProkura.add(vlozOsobu1(it,address,listZaznamy))
            }

            //dozorčí rada
            val listDozorciRada: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyDozorciRada = document.select("D|DR").select("D|CDR")
            zaznamyDozorciRada.forEach() {
                var address =  vratAdresu(it.allElements)
                listDozorciRada.add(vlozOsobu1(it,address))

            }

            //společníci s vkladem osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listspolecniciSVklademOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamySpolecniciSVklademOsoby = document.select("D|SSV").select("D|SS")
            zaznamySpolecniciSVklademOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listspolecniciSVklademOsoby.add(vlozOsobu1(it,address))
                }
            }

            //společníci s vkladem firmy - zapodmínkovat, že kdy to nenajde napřiklad D/PO nějaký text, tak se to vůbec nebude vkládat
            val listspolecniciSVklademFirmy: MutableList<Firma> = mutableListOf<Firma>()
            val zaznamySpolecniciSVklademFirmy = document.select("D|SSV").select("D|SS")
            zaznamySpolecniciSVklademFirmy.forEach() {
                var address =  vratAdresu(it.select("D|PO"))
                if(!(it.select("D|PO").select("D|OF").text()=="")) {
                    listspolecniciSVklademFirmy.add(vlozFirmu1(it,address))
                }
            }

            //akcioáři osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listAkcionariOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyAkcionariOsoby = document.select("D|AKI").select("D|AKR")
            zaznamyAkcionariOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listAkcionariOsoby.add(vlozOsobu1(it,address))
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
                    listAkcionariFirmy.add(vlozFirmu1(it,address))
                }
            }

            //likvidace osoby - zapodmínkovat, že kdy to nenajde napřiklad D/FO nějaký text, tak se to vůbec nebude vkládat
            val listLikvidaceOsoby: MutableList<Osoba> = mutableListOf<Osoba>()
            val zaznamyLikvidaceOsoby = document.select("D|LI").select("D|LIR")
            zaznamyLikvidaceOsoby.forEach() {
                var address =  vratAdresu(it.select("D|FO"))
                if(!(it.select("D|FO").select("D|P").text()=="")) {
                    listLikvidaceOsoby.add(vlozOsobu1(it,address))
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
                    listLikvidaceFirmy.add(vlozFirmu1(it,address))
                }
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
                    listVedouciOrganizacniSlozkyOsoby.add(vlozOsobu1(it,address))
                }
            }


            //konečné vložení do companyData
            val companyData = CompanyData(name, ico,"", address,listPredmetPodnikani,
                listOstatniSkutecnosti,listStatutarniOrganOsoby, listStatutarniOrganFirmy,listStatutarniOrganSkutecnosti,listProkura,
                listDozorciRada,listspolecniciSVklademOsoby, listspolecniciSVklademFirmy, listAkcionariOsoby,
                listAkcionariFirmy, listLikvidaceOsoby, listLikvidaceFirmy,stavSubjektu, pravniForma,
                datumZapisu,soud,spisovaZnacka,vklad,splaceno, listAkcie,listVedouciOrganizacniSlozkyOsoby)
            return companyData
        }

        private fun vlozOsobuOld(it: Element?, address: String, listZaznamy: MutableList<String> = mutableListOf<String>()): Osoba {
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
                it?.select("D|OP")?.select("D|T")?.text() ?: "",
                it?.select("D|OF")?.text() ?: ""

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

        private fun vlozFirmu1(it: Element?, address: String): Firma {
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

        fun upravFinancniCastku(input: String): String {
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

        private fun vlozOsobu(it: JSONObject): Osoba {
            val funkce = it?.optJSONObject("clenstvi")?.optJSONObject("funkce")?.optString("nazev", " ") ?: ""
            return Osoba(
                it?.optJSONObject("fyzickaOsoba")?.optString("titulPredJmenem", "") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optString("jmeno", " ") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optString("prijmeni", " ") ?: "",
                funkce,

                it?.optJSONObject("fyzickaOsoba")?.optString("datumNarozeni", " ") ?: "",
                it?.optJSONObject("fyzickaOsoba")?.optJSONObject("adresa")?.optString("textovaAdresa", " ") ?: "",
                mutableListOf<String>(),
                it?.optJSONObject("clenstvi")?.optJSONObject("clenstvi")?.optString("vznikClenstvi", " ") ?: "",
                it?.optJSONObject("clenstvi")?.optJSONObject("funkce")?.optString("vznikFunkce", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("velikostPodilu")?.optString("hodnota", " ") ?: ""

            )
        }

        private fun vlozOsobuSpolecnik(it: JSONObject): Osoba {
            val funkce = it?.optJSONObject("osoba")?.optJSONObject("clenstvi")?.optJSONObject("funkce")?.optString("nazev", " ") ?: ""
            return Osoba(
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optString("titulPredJmenem", "") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optString("jmeno", " ") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optString("prijmeni", " ") ?: "",
                funkce,

                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optString("datumNarozeni", " ") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("fyzickaOsoba")?.optJSONObject("adresa")?.optString("textovaAdresa", " ") ?: "",
                mutableListOf<String>(),
                it?.optJSONObject("osoba")?.optJSONObject("clenstvi")?.optJSONObject("clenstvi")?.optString("vznikClenstvi", " ") ?: "",
                it?.optJSONObject("clenstvi")?.optJSONObject("funkce")?.optString("vznikFunkce", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("velikostPodilu")?.optString("hodnota", " ") ?: ""

            )
        }

        private fun vlozFirmu(it: JSONObject): Firma {
            return Firma(
                it?.optJSONObject("pravnickaOsoba")?.optString("ico", "") ?: "",
                it?.optJSONObject("pravnickaOsoba")?.optString("obchodniJmeno", "") ?: "",
                it?.optJSONObject("pravnickaOsoba")?.optJSONObject("adresa")?.optString("textovaAdresa", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: ""
            )
        }

        private fun vlozFirmuSpolecnik(it: JSONObject): Firma {
            return Firma(
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")?.optString("ico", "") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")?.optString("obchodniJmeno", "") ?: "",
                it?.optJSONObject("osoba")?.optJSONObject("pravnickaOsoba")?.optJSONObject("adresa")?.optString("textovaAdresa", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("vklad")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("splaceni")?.optString("hodnota", " ") ?: "",
                it?.optJSONObject("podil")?.optJSONObject("velikostPodilu")?.optString("hodnota", " ") ?: ""
            )
        }


    }
}