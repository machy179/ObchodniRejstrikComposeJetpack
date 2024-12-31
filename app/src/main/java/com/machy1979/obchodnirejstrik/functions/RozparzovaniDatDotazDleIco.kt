package com.machy1979.obchodnirejstrik.functions

import com.machy1979.obchodnirejstrik.model.CompanyData
import org.json.JSONObject


class RozparzovaniDatDotazDleIco {
    companion object {
        fun vratCompanyData(jsonObject: JSONObject): CompanyData {


            val name = jsonObject.optString("obchodniJmeno", " ") ?: " "
            val ico = jsonObject.optString("ico", " ") ?: " "
            val dic = jsonObject.optString("dic", " ") ?: " "
            var address = jsonObject.getJSONObject("sidlo").optString("textovaAdresa", " ") ?: " "
            val companyData = CompanyData(name, ico, dic, address)
            return companyData
        }

    }
}