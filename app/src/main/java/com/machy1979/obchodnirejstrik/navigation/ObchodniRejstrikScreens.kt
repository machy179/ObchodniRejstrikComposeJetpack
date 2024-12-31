package com.machy1979.obchodnirejstrik.navigation

enum class ObchodniRejstrikScreens {
    UvodniObrazovka,
    HistorieVyhladavaniObrazovka,
    VypisFiremSeznamObrazovka,
    VypisIcoObrazovka,
    VypisORObrazovka,
    VypisRESObrazovka,
    VypisRZPObrazovka;

    companion object {
        fun fromRoute(route: String?): ObchodniRejstrikScreens =
            when (route?.substringBefore("/")) {
                UvodniObrazovka.name -> UvodniObrazovka
                HistorieVyhladavaniObrazovka.name -> HistorieVyhladavaniObrazovka
                VypisFiremSeznamObrazovka.name -> VypisFiremSeznamObrazovka
                VypisIcoObrazovka.name -> VypisIcoObrazovka
                VypisORObrazovka.name -> VypisORObrazovka
                VypisRESObrazovka.name -> VypisRESObrazovka
                VypisRZPObrazovka.name -> VypisRZPObrazovka
                null -> UvodniObrazovka
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
    }


}