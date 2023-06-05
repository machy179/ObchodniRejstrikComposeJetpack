package com.machy1979.obchodnirejstrik.model

data class Firma (
    var ico: String = "",
    var name: String = "",
    var address: String = "",

    //pro společník s vkladem
    var vklad: String= "",
    var splaceno: String = "",
    var obchodniPodil: String = ""
)
