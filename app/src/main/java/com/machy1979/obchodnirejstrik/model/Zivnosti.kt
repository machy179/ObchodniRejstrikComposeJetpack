package com.machy1979.obchodnirejstrik.model

import java.io.Serializable

data class Zivnosti(
    var nazevZivnosti: String = "",
    var druhZivnosti: String = "",
    var vznikOpravneni: String = "",
    var obory: MutableList<String> = mutableListOf<String>(),


    ) : Serializable