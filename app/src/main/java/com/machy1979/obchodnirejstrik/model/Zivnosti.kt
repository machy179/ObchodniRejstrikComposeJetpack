package com.machy1979.obchodnirejstrik.model

data class Zivnosti(
    var nazevZivnosti: String = "",
    var druhZivnosti: String = "",
    var vznikOpravneni: String = "",
    var obory: MutableList<String> = mutableListOf<String>(),


    )