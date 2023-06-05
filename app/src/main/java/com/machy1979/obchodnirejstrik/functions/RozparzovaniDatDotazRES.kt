package com.machy1979.obchodnirejstrik.functions

import android.util.Log
import com.machy1979.obchodnirejstrik.model.*
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class RozparzovaniDatDotazRES {
    companion object {
        fun vratCompanyData(document: Document): CompanyDataRES {
            val name = document.select("D|OF").first()?.text() ?: " "
            val ico = document.select("D|ICO").first()?.text() ?: " "
            var address =  vratAdresu(document.select("D|SI")) ?: " "
            val pravniForma= document.select("D|NPF").first()?.text() ?: " "
            val datumVzniku= document.select("D|DV").first()?.text() ?: " "
            val zakladniUzemniJednotka= document.select("D|ZUJ").select("D|NZUJ").first()?.text() ?: " "
            val kodZUJ= document.select("D|ZUJ").select("D|Zuj_kod_orig").first()?.text() ?: " "
            val okres= document.select("D|ZUJ").select("D|Nazev_NUTS4").first()?.text() ?: " "
            val kodOkresu= document.select("D|ZUJ").select("D|NUTS4").first()?.text() ?: " "

            //statistické údaje:
            val institucSektor= document.select("D|SU").select("D|Esa2010t").first()?.text() ?: " "
            val pocetZamestnancu= document.select("D|SU").select("D|KPP").first()?.text() ?: " "

            //NACE - tady je to složitější, protože tag nadřízený je Nace a číslo nace je NACE, v JSOUP to hází divočinu
            //proto ověřuji, zda it.select("D|Nace").count() > 1, aby to bralo jen ty položky, které mají dva tagy Nace (respektive Nace a NACE),
            // které mají  a proto nám v cisloNace [1] - lepší způsob se mi nepodařilo najít
            val listNace: MutableList<Nace> = mutableListOf<Nace>()
            val zaznamyNace = document.select("D|Vypis_RES").select("D|Nace")
            zaznamyNace.forEach() {
                if (it.select("D|Nace").count() > 1) {
                    Log.i("NACE velikost:",it.select("D|Nace").count().toString())
                    val cisloNace = it.select("D|Nace")[1].select("D|NACE").first()?.text() ?: " "
                    val nazevNace = it.select("D|Nace").select("D|Nazev_NACE").first()?.text() ?: " "
                    listNace.add(Nace(
                        cisloNace,
                        nazevNace
                    ))
                }
           }


            val companyDataRES = CompanyDataRES(name, ico,"", address, pravniForma,datumVzniku, zakladniUzemniJednotka,
                kodZUJ, okres, kodOkresu, institucSektor, pocetZamestnancu, listNace)


            return companyDataRES
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
    }
}


