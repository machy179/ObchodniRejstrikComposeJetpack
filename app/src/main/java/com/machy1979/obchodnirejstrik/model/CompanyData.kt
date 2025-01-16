package com.machy1979.obchodnirejstrik.model

//je to třída pro výpis z OR

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompanyData(
    var name: String = "",
    var ico: String = "",
    var dic: String = "",
    var address: String = "",
    var predmetPodnikani: MutableList<String> = mutableListOf(),
    var ostatniSkutecnosti: MutableList<String> = mutableListOf(),
    var statutarniOrganOsoby: MutableList<Osoba> = mutableListOf(),
    var statutarniOrganFirmy: MutableList<Firma> = mutableListOf(),
    var statutarniOrganSkutecnosti: MutableList<String> = mutableListOf(),
    var prokura: MutableList<Osoba> = mutableListOf(),
    var dozorciRada: MutableList<Osoba> = mutableListOf(),
    var spolecniciSVklademOsoby: MutableList<Osoba> = mutableListOf(),
    var spolecniciSVklademFirmy: MutableList<Firma> = mutableListOf(),
    var akcionariOsoby: MutableList<Osoba> = mutableListOf(),
    var akcionariFirmy: MutableList<Firma> = mutableListOf(),
    var likvidaceOsoby: MutableList<Osoba> = mutableListOf(),
    var likvidaceFirmy: MutableList<Firma> = mutableListOf(),
    var stavSubjektu: String = "",
    var pravniForma: String = "",
    var datumZapisu: String = "",
    var soud: String = "",
    var spisovaZnacka: String = "",
    var vklad: String = "",
    var splaceno: String = "",
    var akcie: MutableList<String> = mutableListOf(),
    var vedouciOrganizacniSlozky: MutableList<Osoba> = mutableListOf(),
) : Serializable {
    constructor(name: String, ico: String, dic: String, address: String) : this(
        name,
        ico,
        dic,
        address,
        mutableListOf<String>(),
        mutableListOf<String>(),
        mutableListOf<Osoba>(),
        mutableListOf<Firma>(),
        mutableListOf<String>(),
        mutableListOf<Osoba>(),
        mutableListOf<Osoba>()
    )
}


data class CompanyDataResponseVerejnyRejstrik(
    @SerializedName("obchodniJmeno") var name: String = "",
    @SerializedName("ico") var ico: String = "",
    @SerializedName("dic") var dic: String = "",
    @SerializedName("kod") var kod: String = "", //pokud odpověď bude kód, tak byla nějaká chyba v odpovědi
    @SerializedName("sidlo") var sidlo: Sidlo? = null // Vnořený objekt
) {
    val address: String
        get() = sidlo?.textovaAdresa ?: " " // Získáme textovaAdresa z vnořeného objektu
}

data class Sidlo(
    @SerializedName("textovaAdresa") var textovaAdresa: String = ""
)
