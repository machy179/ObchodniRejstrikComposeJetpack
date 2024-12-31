package com.machy1979.obchodnirejstrik.model

import java.io.Serializable

data class CompanyDataRZP(
    var name: String = "",
    var ico: String = "",
    var dic: String = "",
    var address: String = "",
    var pravniForma: String = "",
    var typSubjektu: String = "",
    var evidujiciUrad: String = "",
    var vznikPrvniZivnosti: String = "",

    var osoby: MutableList<Osoba> = mutableListOf<Osoba>(),
    var zivnosti: MutableList<Zivnosti> = mutableListOf<Zivnosti>(),

    ) : Serializable