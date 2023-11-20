package com.machy1979.obchodnirejstrik.functions

import com.machy1979.obchodnirejstrik.model.CompanyData
import org.jsoup.nodes.Document


class RozparzovaniDatDotazDleIco {
    companion object {
        fun vratCompanyData(document: Document): CompanyData {
            val name = document.select("D|OF").first()?.text() ?: " "
            val ico = document.select("D|ICO").first()?.text() ?: " "
            val dic = document.select("D|DIC").first()?.text() ?: " "
            var address = document.select("D|UC").first()?.text() ?: " "
            address =address +", "+ (document.select("D|PB").first()?.text() ?: " ")
            val companyData = CompanyData(name, ico,dic, address)
            return companyData
        }

        fun vratErrorHlasku(document: Document): String {
            val errorHlaska = document.select("D|ET").first()?.text() ?: " "
            return errorHlaska
        }
    }
}