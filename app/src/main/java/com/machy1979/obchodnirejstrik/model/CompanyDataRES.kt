package com.machy1979.obchodnirejstrik.model

import java.io.Serializable

data class CompanyDataRES (
    var name: String = "",
    var ico: String = "",
    var dic: String = "",
    var address: String = "",
    var pravniForma: String ="",
    var datumVzniku: String = "",
    var zakladniUzemniJednotka: String = "",
    var kodZUJ: String = "",
    var okres: String = "",
    var kodOkresu: String = "",

    //statistické údaje:
    var institucSektor: String = "",
    var pocetZamestnancu: String = "",

    //NACE - list polí - na prvním místě číslo nace a na druhém je název nace
    var nace: MutableList<Nace> = mutableListOf<Nace>(),

    ) : Serializable