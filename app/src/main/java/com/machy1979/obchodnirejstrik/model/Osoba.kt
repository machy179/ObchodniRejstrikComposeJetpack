package com.machy1979.obchodnirejstrik.model

data class Osoba(
    var titulyPredJmenem: String = "",
    var jmeno: String = "",
    var prijmeni: String = "",
    var funkce: String = "",
    var datNar: String = "",
    var adresa: String = "",
    var poznamky: MutableList<String> = mutableListOf<String>(),
    var clenstviOd: String = "",
    var veFunkciOd: String = "",

//pro společník s vkladem
    var vklad: String= "",
    var splaceno: String = "",
    var obchodniPodil: String = "",

)