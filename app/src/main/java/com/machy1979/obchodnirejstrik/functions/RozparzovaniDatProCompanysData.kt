package com.machy1979.obchodnirejstrik.functions


import android.util.Log
import com.machy1979.obchodnirejstrik.model.CompanyData
import org.json.JSONObject
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class RozparzovaniDatProCompanysData {
    companion object {
        fun vratCompanyData(element: Element): CompanyData {
            val companyData = CompanyData()
            companyData.name = element.select("are|Obchodni_firma")?.text() ?: " " //pokud element nenajde (tím pádem bude null), tak se propíše " " - aneb Elvis...
            companyData.ico = element.select("are|ICO").first()?.text() ?: " "
            var address = element.select("dtt|Nazev_obce").first()?.text() ?: " "
            element.select("dtt|Nazev_ulice").first()?.text()?.let {
                address =address +", "+it
            }
            address =address +" "+ (element.select("dtt|Cislo_domovni").first()?.text() ?: " ")
            companyData.address = address
            return companyData
        }

        fun vratErrorHlasku(document: Document): String {
            val errorHlaska = document.select("dtt|Error_text").first()?.text() ?: " "
            return errorHlaska
        }
    }
}

class RozparzovaniDatProCompanysDataNovy {
    companion object {
        fun vratCompanyData(jsonObject: JSONObject): CompanyData {
            val companyData = CompanyData()
            companyData.name = jsonObject.optString("obchodniJmeno", " ") ?: " " //optString je lepší, než getString, protože pokud obchodniJmeno neexistuje, nahradí to fallbackem
            companyData.ico = jsonObject.optString("ico", " ") ?: " "
            var address = jsonObject.getJSONObject("sidlo").optString("textovaAdresa", " ") ?: " "

            companyData.address = address
            return companyData
        }

    }
}