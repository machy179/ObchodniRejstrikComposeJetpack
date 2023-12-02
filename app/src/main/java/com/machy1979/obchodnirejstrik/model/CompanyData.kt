package com.machy1979.obchodnirejstrik.model

//je to třída pro výpis z OR
data class CompanyData(
    var name: String = "",
    var ico: String = "",
    var dic: String = "",
    var address: String = "",
    var predmetPodnikani: MutableList<String> = mutableListOf<String>(),
    var ostatniSkutecnosti: MutableList<String> = mutableListOf<String>(),
    var statutarniOrganOsoby: MutableList<Osoba> = mutableListOf<Osoba>(),
    var statutarniOrganFirmy: MutableList<Firma> = mutableListOf<Firma>(),
    var statutarniOrganSkutecnosti: MutableList<String> = mutableListOf<String>(),
    var prokura: MutableList<Osoba> = mutableListOf<Osoba>(),
    var dozorciRada: MutableList<Osoba> = mutableListOf<Osoba>(),
 //   var akcionari: MutableList<Osoba> = mutableListOf<Osoba>(), //muze to byt i firma, tak to je třeba ošetřit
    var spolecniciSVklademOsoby: MutableList<Osoba> = mutableListOf<Osoba>(),
    var spolecniciSVklademFirmy: MutableList<Firma> = mutableListOf<Firma>(),

    var akcionariOsoby: MutableList<Osoba> = mutableListOf<Osoba>(),
    var akcionariFirmy: MutableList<Firma> = mutableListOf<Firma>(),

    var likvidaceOsoby: MutableList<Osoba> = mutableListOf<Osoba>(),
    var likvidaceFirmy: MutableList<Firma> = mutableListOf<Firma>(),



//OR
    var stavSubjektu: String = "",
    var pravniForma: String = "",
    var datumZapisu: String = "",
    var soud: String = "",
    var spisovaZnacka: String = "",

    var vklad: String = "",
    var splaceno: String = "",
    var akcie: MutableList<String> = mutableListOf<String>(),
    var vedouciOrganizacniSlozky: MutableList<Osoba> = mutableListOf<Osoba>(),




) {
    constructor(name: String,ico: String,dic: String,address: String): this(name, ico,dic, address, mutableListOf<String>(),
        mutableListOf<String>(),mutableListOf<Osoba>(),mutableListOf<Firma>(),mutableListOf<String>(),mutableListOf<Osoba>(),mutableListOf<Osoba>())
}