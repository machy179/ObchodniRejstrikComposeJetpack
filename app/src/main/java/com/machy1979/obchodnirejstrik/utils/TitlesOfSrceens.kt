package com.machy1979.obchodnirejstrik.utils


import androidx.annotation.StringRes
import com.machy1979.obchodnirejstrik.R


enum class TitlesOfSrceens (@StringRes val title: Int) {

  //  UvodniObrazovka(title = R.string.app_name),
    UvodniObrazovka(title = R.string.prazdny_retezec),
    VypisFiremSeznam(title = R.string.vypis_firem_seznam),
    VypisIco(title = R.string.vypis_ico),
    VypisOR(title = R.string.vypis_or),
    VypisRZP(title = R.string.vypis_RZP),
    VypisRES(title = R.string.vypis_RES),
    HistorieVyhledavani(title = R.string.history_queries)


}



